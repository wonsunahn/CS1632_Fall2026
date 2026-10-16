/**
 * @file binary_tree.c
 * @brief Builds a binary tree in which every node stores a copy of the same
 *        name, then checks that every node's copy matches the name.
 *
 * Adapted from the binary-trees program in The Computer Language Benchmarks
 * Game (https://salsa.debian.org/benchmarksgame-team/benchmarksgame/),
 * contributed by Kevin Carson and modified by Wonsun Ahn.
 *
 * compilation:
 *     gcc -g -fsanitize=address binary_tree.c -o binary_tree
 *     gcc -g binary_tree.c -o binary_tree
 *
 * usage: ./binary_tree <name>
 *     e.g. ./binary_tree abc
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

/** Depth of the tree.  A tree of depth d has 2^(d+1) - 1 nodes. */
#define TREE_DEPTH 10

/** A tree node.  Every node stores its own copy of the name. */
typedef struct tn {
    char         name[4];   /**< copy of the name */
    struct tn*   left;      /**< left subtree, or NULL for a leaf */
    struct tn*   right;     /**< right subtree, or NULL for a leaf */
} treeNode;


/**
 * @brief Allocate and initialize a new tree node.
 *
 * @param name  name to copy into the node
 * @param left  left subtree, or NULL for a leaf
 * @param right right subtree, or NULL for a leaf
 * @return pointer to the new node
 */
treeNode* NewTreeNode(const char* name, treeNode* left, treeNode* right)
{
    treeNode*    new;

    new = (treeNode*)malloc(sizeof(treeNode));

    strcpy(new->name, name);
    new->left = left;
    new->right = right;

    return new;
} /* NewTreeNode() */


/**
 * @brief Check that every node in a tree stores a copy of name.
 *
 * Visits the nodes in preorder and stops at the first mismatch, printing
 * the name found in the mismatching node.
 *
 * @param name expected name
 * @param tree root of the tree to check
 * @return 1 if every node matches, 0 otherwise
 */
int NameCheck(const char* name, treeNode* tree)
{
    int match;
    if (strcmp(name, tree->name) == 0) {
        match = 1;
    } else {
        printf("Failure: name=%s, tree->name=%s\n", name, tree->name);
        match = 0;
    }
    if (tree->left == NULL) {
        return match;
    } else {
        return match && NameCheck(name, tree->left) && NameCheck(name, tree->right);
    }
} /* NameCheck() */


/**
 * @brief Build a complete binary tree in which every node stores name.
 *
 * @param name  name to store in every node
 * @param depth depth of the tree; 0 builds a single leaf
 * @return root of the new tree
 */
treeNode* BottomUpTree(const char* name, unsigned depth)
{
    if (depth > 0)
        return NewTreeNode
        (
            name,
            BottomUpTree(name, depth - 1),
            BottomUpTree(name, depth - 1)
        );
    else
        return NewTreeNode(name, NULL, NULL);
} /* BottomUpTree() */


/**
 * @brief Free every node in a tree.
 *
 * @param tree root of the tree to free
 */
void DeleteTree(treeNode* tree)
{
    if (tree->left != NULL)
    {
        DeleteTree(tree->left);
        DeleteTree(tree->right);
    }

    free(tree);
} /* DeleteTree() */


/**
 * @brief Build a tree of depth TREE_DEPTH storing the given name in every
 *        node, check it, and free it.
 *
 * @param argc argument count; must be 2
 * @param argv program name and the name to store
 * @return 0 after the check result is printed, 1 on a usage error
 */
int main(int argc, char* argv[])
{
    const char* name;
    treeNode*   tree;
    int         result;

    if (argc != 2) {
        fprintf(stderr, "usage: %s <name>\n"
                        "(e.g. %s abc)\n", argv[0], argv[0]);
        return 1;
    }
    name = argv[1];

    tree = BottomUpTree(name, TREE_DEPTH);
    result = NameCheck(name, tree);
    DeleteTree(tree);

    printf("Name check result = %d\n", result);

    return 0;
} /* main() */
