# Every program gets a plain build (.bin).  Programs listed in ASAN_PROGS or
# TSAN_PROGS also get an AddressSanitizer (.asan) or ThreadSanitizer (.tsan)
# build.  To add a program, add its name (without .c) to the right lists.
PROGS      = heap stack stack_overflow stack_pointer_return binary_tree datarace
ASAN_PROGS = stack_overflow stack_pointer_return binary_tree
TSAN_PROGS = datarace

TARGETS = $(PROGS:=.bin) $(ASAN_PROGS:=.asan) $(TSAN_PROGS:=.tsan)

CC     = gcc
CFLAGS = -g -w -std=gnu99 -pthread
LDLIBS = -lm

ASAN_FLAGS = -fsanitize=address
TSAN_FLAGS = -fPIE -pie -fsanitize=thread

.PHONY: all clean

all: $(TARGETS)

%.bin: %.c
	$(CC) $(CFLAGS) $< $(LDLIBS) -o $@

%.asan: %.c
	$(CC) $(CFLAGS) $(ASAN_FLAGS) $< $(LDLIBS) -o $@

%.tsan: %.c
	$(CC) $(CFLAGS) $(TSAN_FLAGS) $< $(LDLIBS) -o $@

clean:
	rm -f $(TARGETS) *.o
