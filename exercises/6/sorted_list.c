/**
 * @file sorted_list.c
 * @brief Concurrent sorted linked list with fine-grained (per-node) locking.
 *
 * compilation:
 *     gcc -g -O1 -fsanitize=thread sorted_list.c -o sorted_list -lpthread
 *     gcc -g -O2 sorted_list.c -o sorted_list -lpthread
 *
 * usage: ./sorted_list <ops per thread>
 *     e.g. ./sorted_list 100000
 *
 * NUM_THREADS workers each repeatedly pick a random key in [0, KEY_RANGE)
 * and either add it to the set (if absent) or remove it.  After all workers
 * finish, main checks the final list.
 */

#include <assert.h>
#include <limits.h>
#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>

/** Number of worker threads. */
#define NUM_THREADS 2
/** Keys are drawn from [0, KEY_RANGE). */
#define KEY_RANGE 100

/** A list node.  Each node carries its own lock. */
typedef struct node {
    int             key;    /**< key stored in this node */
    struct node*    next;   /**< next node in ascending key order */
    pthread_mutex_t lock;   /**< protects this node's next field */
} node;

/** A sorted set of ints, bracketed by two sentinel nodes. */
typedef struct {
    node*  head;       /**< sentinel with key INT_MIN */
    node*  tail;       /**< sentinel with key INT_MAX */
    int    size;       /**< number of real (non-sentinel) nodes */
} sortedList;

/** Per-worker data.  Each element is written only by its own worker, so no
    lock is needed.  main reads the results only after pthread_join returns,
    when the worker has finished. */
typedef struct {
    int id;        /**< worker number, also used to seed rand_r */
    int adds;      /**< successful adds */
    int removes;   /**< successful removes */
} perThreadData;

/** The list shared by all workers. */
sortedList list;

/** Number of operations each worker performs. */
int opsPerThread;


/**
 * @brief Allocate and initialize a new node.
 *
 * @param key  key to store in the node
 * @param next node that will follow the new node
 * @return pointer to the new node, with its lock initialized and unlocked
 */
node* NewNode(int key, node* next)
{
    node* n = (node*)malloc(sizeof(node));
    n->key = key;
    n->next = next;
    pthread_mutex_init(&n->lock, NULL);
    return n;
} /* NewNode() */


/**
 * @brief Destroy a node's lock and free the node.
 *
 * @param n node to free; it must be unlinked from the list and unlocked
 */
void FreeNode(node* n)
{
    pthread_mutex_destroy(&n->lock);
    free(n);
} /* FreeNode() */


/**
 * @brief Initialize an empty list consisting of the two sentinels.
 *
 * @param l list to initialize
 */
void ListInit(sortedList* l)
{
    l->tail = NewNode(INT_MAX, NULL);
    l->head = NewNode(INT_MIN, l->tail);
    l->size = 0;
} /* ListInit() */


/**
 * @brief Locate the window in which key belongs.
 *
 * Traverses the list from the head sentinel using hand-over-hand locking.
 *
 * @param l     list to search
 * @param key   key to look for
 * @param predp on return, the last node with a key less than key
 * @param currp on return, the first node with a key greater than or equal
 *              to key
 * @post *predp and *currp are both locked by the caller,
 *       (*predp)->key < key <= (*currp)->key, and (*predp)->next == *currp.
 */
void Find(sortedList* l, int key, node** predp, node** currp)
{
    node* pred = l->head;
    pthread_mutex_lock(&pred->lock);
    node* curr = pred->next;

    while (curr->key < key) {
        pthread_mutex_unlock(&pred->lock);
        pthread_mutex_lock(&curr->lock);
        pred = curr;
        curr = curr->next;
    }
    pthread_mutex_lock(&curr->lock);

    *predp = pred;
    *currp = curr;
} /* Find() */


/**
 * @brief Remove key from the set if it is present.
 *
 * @param l   list to remove from
 * @param key key to remove
 * @return 1 if a node was removed, 0 if key was not in the set
 */
int Remove(sortedList* l, int key)
{
    node *pred, *curr;
    int removed = 0;
    Find(l, key, &pred, &curr);
    if (curr->key == key) {
        pred->next = curr->next;
        l->size--;
        removed = 1;
    }
    pthread_mutex_unlock(&curr->lock);
    pthread_mutex_unlock(&pred->lock);
    if (removed)
        FreeNode(curr);
    return removed;
} /* Remove() */


/**
 * @brief Add key only if it is not already in the set.
 *
 * @param l   list to insert into
 * @param key key to insert
 * @return 1 if key was added, 0 if it was already in the set
 */
int AddIfAbsent(sortedList* l, int key)
{
    node *pred, *curr;
    int found;

    /* Check whether key is already in the set. */
    Find(l, key, &pred, &curr);
    found = (curr->key == key);
    pthread_mutex_unlock(&curr->lock);
    pthread_mutex_unlock(&pred->lock);
    if (found)
        return 0;

    /* Insert key into its sorted position. */
    Find(l, key, &pred, &curr);
    assert(curr->key != key);
    pred->next = NewNode(key, curr);
    l->size++;
    pthread_mutex_unlock(&curr->lock);
    pthread_mutex_unlock(&pred->lock);
    return 1;
} /* AddIfAbsent() */


/**
 * @brief Thread body: perform random adds and removes on the shared list.
 *
 * Each operation picks a key uniformly from [0, KEY_RANGE) and, with equal
 * probability, calls AddIfAbsent() or Remove() on it.  The counts of
 * successful adds and removes are stored in the worker's perThreadData.
 *
 * @param arg pointer to this worker's perThreadData
 * @return NULL
 */
void* Worker(void* arg)
{
    perThreadData* td = (perThreadData*)arg;
    unsigned seed = td->id + 1;
    int i;

    for (i = 0; i < opsPerThread; i++) {
        int key = rand_r(&seed) % KEY_RANGE;
        if (rand_r(&seed) % 2)
            td->adds += AddIfAbsent(&list, key);
        else
            td->removes += Remove(&list, key);
    }
    return NULL;
} /* Worker() */


/**
 * @brief Run the workers, then check the final list.
 *
 * Prints the number of nodes found by traversal, the size field, and the
 * net number of successful adds, followed by three checks: the size field
 * matches the traversal, the traversal matches adds minus removes, and the
 * keys are in strictly ascending order.
 *
 * @param argc argument count; must be 2
 * @param argv program name and number of operations per worker
 * @return 0 after the checks are printed, 1 on a usage error
 */
int main(int argc, char* argv[])
{
    pthread_t     threads[NUM_THREADS];
    perThreadData threadData[NUM_THREADS] = { { 0 } };
    int           totalAdds = 0, totalRemoves = 0, count = 0, sorted = 1, i;
    node*         n;

    if (argc != 2 || (opsPerThread = atoi(argv[1])) < 1) {
        fprintf(stderr, "usage: %s <ops per thread>\n"
                        "    e.g. %s 100000\n", argv[0], argv[0]);
        return 1;
    }

    ListInit(&list);

    for (i = 0; i < NUM_THREADS; i++) {
        threadData[i].id = i;
        pthread_create(&threads[i], NULL, Worker, &threadData[i]);
    }

    for (i = 0; i < NUM_THREADS; i++) {
        pthread_join(threads[i], NULL);
        totalAdds += threadData[i].adds;
        totalRemoves += threadData[i].removes;
    }

    for (n = list.head->next; n != list.tail; n = n->next) {
        count++;
        if (n->key >= n->next->key)
            sorted = 0;
    }

    printf("Nodes in list = %d, size field = %d, adds - removes = %d\n",
           count, list.size, totalAdds - totalRemoves);
    printf("Size check result = %d\n", count == list.size);
    printf("Accounting check result = %d\n", count == totalAdds - totalRemoves);
    printf("Order check result = %d\n", sorted);

    return 0;
} /* main() */
