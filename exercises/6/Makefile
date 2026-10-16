# Every program gets a plain build (.bin).  Programs listed in ASAN_PROGS or
# TSAN_PROGS also get an AddressSanitizer (.asan) or ThreadSanitizer (.tsan)
# build.  To add a program, add its name (without .c) to the right lists.
# All binaries are written to $(BINDIR).
PROGS      = binary_tree sorted_list
ASAN_PROGS = binary_tree
TSAN_PROGS = sorted_list

BINDIR  = bin
TARGETS = $(addprefix $(BINDIR)/, $(PROGS:=.bin) $(ASAN_PROGS:=.asan) $(TSAN_PROGS:=.tsan))

CC     = gcc
CFLAGS = -g -w -std=gnu99 -pthread
LDLIBS = -lm

ASAN_FLAGS = -fsanitize=address
TSAN_FLAGS = -fPIE -pie -fsanitize=thread

.PHONY: all clean

all: $(TARGETS)

# "| $(BINDIR)" creates bin/ first without rebuilding when its timestamp changes.
$(BINDIR)/%.bin: %.c | $(BINDIR)
	$(CC) $(CFLAGS) $< $(LDLIBS) -o $@

$(BINDIR)/%.asan: %.c | $(BINDIR)
	$(CC) $(CFLAGS) $(ASAN_FLAGS) $< $(LDLIBS) -o $@

$(BINDIR)/%.tsan: %.c | $(BINDIR)
	$(CC) $(CFLAGS) $(TSAN_FLAGS) $< $(LDLIBS) -o $@

$(BINDIR):
	mkdir -p $@

clean:
	rm -rf $(BINDIR)