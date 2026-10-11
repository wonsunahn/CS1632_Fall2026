- [Description](#description)
- [Setting up Build Environment](#setting-up-build-environment)
  * [Building](#building)
  * [Editing Source Code on VSCode Desktop](#editing-source-code-on-vscode-desktop)
- [Testing and Debugging Memory Errors](#testing-and-debugging-memory-errors)
  * [Turning off ASLR (Address Space Layout Randomization)](#turning-off-aslr--address-space-layout-randomization-)
  * [Using Google ASAN (Address Sanitizer)](#using-google-asan--address-sanitizer-)
  * [Debugging](#debugging)
  * [Comparing Google ASAN with Valgrind](#comparing-google-asan-with-valgrind)
- [Testing and Debugging Datarace Errors](#testing-and-debugging-datarace-errors)
  * [Using Google TSAN (Thread Sanitizer)](#using-google-tsan--thread-sanitizer-)
  * [Debugging](#debugging-1)
- [Downloading Your Work](#downloading-your-work)
- [Resources](#resources)

# Description

This set of code demonstrates concepts we learned in the Software QA and
Nondeterminism lecture.  By trying out these programs, you will learn the following:

1. Observe how values of pointers in C are randomized through ASLR leading to nondeterministic program behavior.

1. Observe how pointer values can leak out to program output through memory errors.

1. Learn how to turn ASLR off to make C pointers deterministic.

1. Learn how to use ASAN (Google Address Sanitizer) to debug stack overflow memory errors.

1. Learn how to use ASAN (Google Address Sanitizer) to debug dangling pointer memory errors.

1. Observe how dataraces in C leads to nondeterministic program behavior.

1. Learn how to use TSAN (Google Thread Sanitizer) to debug datarace errors.

1. Compare ASAN with Valgrind, another memory error detection tool.

# Setting up Build Environment

In order to use ASAN or TSAN, you need to clang version >= 3.1 or gcc version >= 4.8.
Since you are unlikely to have either installed on your local
computer, I am going to ask you to use Codespaces, a containerized development environment provided by GitHub.

## Building

In order to launch the codespace, you only need to click on the big green "Code" button on your GitHub.com page for your repository, and then select the "Codespaces" tab, after which you can click on "Create codespace on main" button.  Refer to the picture in the below link:

https://docs.github.com/en/codespaces/developing-in-a-codespace/creating-a-codespace-for-a-repository#creating-a-codespace-for-a-repository

That will launch a new tab on your web browser showing a VSCode-like development environment.  I will initially launch a script to install gdb and valgrind.  You can leave that running on the background while working on a bash shell on the Terminal.

On the bash shell, invoke 'make':

   ```
   $ make
   mrabbit2@WONSUN-DESKTOP:~/CS1632_Sanitizer$ make
   gcc -g -w -std=gnu99 -pthread heap.c -lm -o heap.bin
   gcc -g -w -std=gnu99 -pthread stack.c -lm -o stack.bin
   gcc -g -w -std=gnu99 -pthread stack_overflow.c -lm -o stack_overflow.bin
   gcc -g -w -std=gnu99 -pthread stack_pointer_return.c -lm -o stack_pointer_return.bin
   gcc -g -w -std=gnu99 -pthread binary_tree.c -lm -o binary_tree.bin
   gcc -g -w -std=gnu99 -pthread datarace.c -lm -o datarace.bin
   gcc -g -w -std=gnu99 -pthread -fsanitize=address stack_overflow.c -lm -o stack_overflow.asan
   gcc -g -w -std=gnu99 -pthread -fsanitize=address stack_pointer_return.c -lm -o stack_pointer_return.asan
   gcc -g -w -std=gnu99 -pthread -fsanitize=address binary_tree.c -lm -o binary_tree.asan
   gcc -g -w -std=gnu99 -pthread -fPIE -pie -fsanitize=thread datarace.c -lm -o datarace.tsan
   ```

Note how when I create ASAN instrumented binaries (e.g. stack_overflow.asan,
stack_pointer_return.asan, ...), I pass the **-fsanitize=address** compiler option
to gcc.  You need to pass it to both the compilation stage and the linking
stage.

Also note how when I create TSAN instrumented binaries (e.g. datarace.tsan) I
pass the **-fsanitize=thread** compiler option to gcc.  I also pass the
**-fPIE** and **-pie** options to the compilation and linking stages
respectively.  This makes your code position independent, and is needed for
TSAN to work flawlessly (I'm assuming you learned what PIE is in CS 449).

## Editing Source Code on VSCode Desktop

You can keep working on the Codespaces browser tab, or if you prefer a desktop
environment, you can click on the hamburger icon on the top left (the icon with
three horizontal bars), and then click on "Open in VS Code Desktop", which will
launch your desktop VSCode and connect it to codespaces.

# Testing and Debugging Memory Errors


## Turning off ASLR (Address Space Layout Randomization)

heap.c is a simple program that mallocs some bytes on the heap and prints out
the pointer to that heap location.  You can use the VSCode IDE to open it.

As we learned, even this simple program can display nondeterministic behavior
due to ASLR.  Try it out yourself!

```
$ ./heap.bin
p = 0x55de862c92a0
$ ./heap.bin
p = 0x56317e9b52a0
$ ./heap.bin
p = 0x5654dc8022a0
```

Your actual values will vary but you can see how the output is randomized.

Likewise, stack.c is a simple program that prints out the pointer to a stack
location.  And it also displays nondeterministic behavior due to ASLR:

```
$ ./stack.bin
p = 0x7fff5e1060d0
$ ./stack.bin
p = 0x7fff4526e850
$ ./stack.bin
p = 0x7ffd5c507230
```

Now, let's try running both with ASLR turned off.  I've written a simple script
named 'run_aslr_off.sh' that does exactly that:

```
$ bash run_aslr_off.sh ./heap.bin
setarch x86_64 -R ./heap.bin
p = 0x5555555592a0
$ bash run_aslr_off.sh ./heap.bin
setarch x86_64 -R ./heap.bin
p = 0x5555555592a0
$ bash run_aslr_off.sh ./heap.bin
setarch x86_64 -R ./heap.bin
p = 0x5555555592a0
$ bash run_aslr_off.sh ./stack.bin
setarch x86_64 -R ./stack.bin
p = 0x7fffffffe3e0
$ bash run_aslr_off.sh ./stack.bin
setarch x86_64 -R ./stack.bin
p = 0x7fffffffe3e0
$ bash run_aslr_off.sh ./stack.bin
setarch x86_64 -R ./stack.bin
p = 0x7fffffffe3e0
```

Note that now the output is no longer random!  This is what it says if you 'man
setarch':

```
$ man setarch
...
 -R, --addr-no-randomize
              Disables randomization of the virtual address space (turns on ADDR_NO_RANDOMIZE).
...
```

Did you ever get the feeling that your C program that used to behave randomly
suddenly becomes deterministic when you run it on top of GDB (GNU Debugger)?
That is because GDB by default turns off ASLR for debugging purposes so that
behavior is reproducible.  Turning off ASLR can be very useful in a debug
setting.  Let's confirm with our own eyes that GDB does in fact turn off
ASLR by running it three times:

```
$ gdb stack.bin
...
Reading symbols from stack.bin...
(gdb) run
Starting program: /afs/pitt.edu/home/w/a/wahn/teaching/cs1632/CS1632_Sanitizer/stack.bin
p = 0x7fffffffe580
[Inferior 1 (process 3981010) exited normally]
(gdb) run
Starting program: /afs/pitt.edu/home/w/a/wahn/teaching/cs1632/CS1632_Sanitizer/stack.bin
p = 0x7fffffffe580
[Inferior 1 (process 3981014) exited normally]
(gdb) run
Starting program: /afs/pitt.edu/home/w/a/wahn/teaching/cs1632/CS1632_Sanitizer/stack.bin
p = 0x7fffffffe580
[Inferior 1 (process 3981202) exited normally]
(gdb)
```

If you want to probe deeper, "personality" is the system call used in both
"setarch" and "gdb" that turns off ASLR.  A brief look at 'man personality'
shows:

```
$ man personality
...
SYNOPSIS
       #include <sys/personality.h>

       int personality(unsigned long persona);

DESCRIPTION
...
       The flag values are as follows:
...
       ADDR_NO_RANDOMIZE (since Linux 2.6.12)
              With this flag set, disable address-space-layout randomization.
...
```

You can see through the "strace" Linux system call trace utility that
"personality" is called with the "ADDR_NO_RANDOMIZE" argument both when
stack.bin is invoked on top of "setarch -R" or on top of "gdb":

```
$ strace setarch -R ./stack.bin 2>&1 |grep personality
personality(PER_LINUX|ADDR_NO_RANDOMIZE) = 0 (PER_LINUX)
```

```
$ strace gdb -ex=r -ex=q stack.bin 2>&1 |grep personality
personality(0xffffffff)                 = 0 (PER_LINUX)
personality(PER_LINUX|ADDR_NO_RANDOMIZE) = 0 (PER_LINUX)
personality(0xffffffff)                 = 0x40000 (PER_LINUX|ADDR_NO_RANDOMIZE)
personality(PER_LINUX)                  = 0x40000 (PER_LINUX|ADDR_NO_RANDOMIZE)
```

## Using Google ASAN (Address Sanitizer)

1. stack_overflow.c is a buggy program that demonstrates the stack buffer overflow
issue that we discussed in the lecture.  In the main function, it starts by
creating a linked list of 3 nodes on the stack.  Then, it sends 32 bytes of
first.data to the screen:

   ```
   send_data(first.data, 32);
   ```

   First, let's try executing the program a few times as before:

   ```
   $ ./stack_overflow.bin
   [Sent data]
   48 65 6c 6c 6f 2e 2e  0 c0 60 fb 80 fe 7f  0  0 57 6f 72 6c 64 2e 2e  0 d0 60 fb 80 fe 7f  0  0
   $ ./stack_overflow.bin
   [Sent data]
   48 65 6c 6c 6f 2e 2e  0 f0 4f 4c bf ff 7f  0  0 57 6f 72 6c 64 2e 2e  0  0 50 4c bf ff 7f  0  0
   $ ./stack_overflow.bin
   [Sent data]
   48 65 6c 6c 6f 2e 2e  0 e0 87 7e  f fc 7f  0  0 57 6f 72 6c 64 2e 2e  0 f0 87 7e  f fc 7f  0  0
   ```

   You can see that this is also a nondeterministic program.  But we only
sent data to the output and no addresses this time, so where did the
nondeterminism come from?  If you look more closely, you will notice that
the first 8 bytes do not change from run to run.  If you know ASCII code,
you will be able to decypher it to he the "Hello.." string (with nul
terminator) inside first.data.  Then, what are the second 8 bytes that keep
on changing?  For that, let's try running stack_overflow in verbose mode
using the "-v" option:

   ```
   $ ./stack_overflow.bin -v
   [Stack Frame]
   return address = 0x7f2cf873d0b3
   old base pointer = (nil) <--- base pointer
   padding (8 bytes)
   padding (8 bytes)
   third.next = (nil)
   third.data = .......
   second.next = 0x7fffeca47e60 <--- Sent!
   second.data = World.. <--- Sent!
   first.next = 0x7fffeca47e50 <--- Sent!
   first.data = Hello.. <--- Sent!
   [Sent data]
   48 65 6c 6c 6f 2e 2e  0 50 7e a4 ec ff 7f  0  0 57 6f 72 6c 64 2e 2e  0 60 7e a4 ec ff 7f  0  0
   ```

   You will notice that the second 8 bytes is the pointer address inside
first.next (in reverse, since x86 architecture uses little endian ordering)!
So why is first.next being sent along with first.data?  That is because the
first.data buffer is only 8 bytes long, so when send_data attempts to send
32 bytes, it also sends the 8 bytes that come after first.data, which in the
stack layout happens to be first.next.

   In short, the address inside first.next **leaks out** to program output
even though the programmer never intended it in the source code.  And this
address randomized by ASLR is what is causing the nondeterminism.  Of
course, you could again turn off ASLR to make the buggy program run
deterministically, at least while debugging:

   ```
   $ bash run_aslr_off.sh ./stack_overflow.bin
   setarch x86_64 -R ./stack_overflow.bin
   [Sent data]
   48 65 6c 6c 6f 2e 2e  0 e0 e5 ff ff ff 7f  0  0 57 6f 72 6c 64 2e 2e  0 f0 e5 ff ff ff 7f  0  0
   $ bash run_aslr_off.sh ./stack_overflow.bin
   setarch x86_64 -R ./stack_overflow.bin
   [Sent data]
   48 65 6c 6c 6f 2e 2e  0 e0 e5 ff ff ff 7f  0  0 57 6f 72 6c 64 2e 2e  0 f0 e5 ff ff ff 7f  0  0
   $ bash run_aslr_off.sh ./stack_overflow.bin
   setarch x86_64 -R ./stack_overflow.bin
   [Sent data]
   48 65 6c 6c 6f 2e 2e  0 e0 e5 ff ff ff 7f  0  0 57 6f 72 6c 64 2e 2e  0 f0 e5 ff ff ff 7f  0  0
   ```

   Note that the memory error still exists, but the program is going to be
easier to debug since at least you can reproduce the same behavior every
time you run the program.

   But when the time comes to deploy your program, your end users will most
likely have ASLR turned on in their machines for security.  What then?  Your
programs will again be nondeterministic, and even if the program ran
correctly when debugging with ASLR turned off, there is no guarantee that
the correct behavior will be reproduced with ASLR turned back on.  So we may
still get surprise defects.

   How can we have a deterministic program when all addresses are randomized?
Easy: just don't let addresses leak out to program output!  As we discussed,
unless for debugging purposes, programs will almost never intentionally
output addresses where data is stored --- they will typically output the
data.  It is just that addresses leak out to output due to memory errors
(like in this case).  So if we can catch all memory errors, then problem
solved!  ASAN is exactly the kind of tool that can help you do that.

   Now let's see if ASAN can find the bug for us by running the instrumented binary:

   ```
   $ ./stack_overflow.asan
   [Sent data]
   =================================================================
   ==3982981==ERROR: AddressSanitizer: stack-buffer-overflow on address 0x7ffc5e8d3330 at pc 0x56480b55c353 bp 0x7ffc5e8d32b0 sp 0x7ffc5e8d32a0
   READ of size 1 at 0x7ffc5e8d3330 thread T0
       #0 0x56480b55c352 in send_data /afs/pitt.edu/home/w/a/wahn/teaching/cs1632/CS1632_Sanitizer/stack_overflow.c:17
       #1 0x56480b55c8a3 in main /afs/pitt.edu/home/w/a/wahn/teaching/cs1632/CS1632_Sanitizer/stack_overflow.c:49
       #2 0x7f429c3fb0b2 in __libc_start_main (/lib/x86_64-linux-gnu/libc.so.6+0x240b2)
       #3 0x56480b55c22d in _start (/afs/pitt.edu/home/w/a/wahn/teaching/cs1632/CS1632_Sanitizer/stack_overflow.asan+0x222d)

   Address 0x7ffc5e8d3330 is located in stack of thread T0 at offset 48 in frame
       #0 0x56480b55c394 in main /afs/pitt.edu/home/w/a/wahn/teaching/cs1632/CS1632_Sanitizer/stack_overflow.c:22

     This frame has 3 object(s):
       [32, 48) 'first' (line 23) <== Memory access at offset 48 overflows this variable
       [64, 80) 'second' (line 23)
       [96, 112) 'third' (line 23)
   ...
   ```

   ASAN is able to pinpoint exactly where the illegal "READ of size 1"
happened at stack_overflow.c:17!  That is where the out of bounds array
access happens.  Below that line is the stack trace so we know the calling
context.

1. stack_pointer_return.c is another buggy program with a common memory error
where a function returns a pointer to a local array.  When the function
returns, the local array is deallocated with the rest of the function frame
as it is now out of scope, thereby leaving the pointer dangling.  Similar to
the stack overflow memory error, accessing dangling pointers results in
**undefined behavior** according to the C language specifications.  The
particular version of GCC installed on the machine (GCC 9.3.0) chose to set
this dangling pointer to a null pointer before returning from the function
bar() rather than leaving it dangling (which is a good choice).  So, you get
deterministic behavior in this case --- it's just that accessing a null
pointer results in a segmentation fault:


   ```
   $ ./stack_pointer_return.bin
   Segmentation fault (core dumped)
   ```

   Old versions of GCC and some other compilers would just leave the pointer
dangling which would cause an access of a dangling pointer.  So what happens
then?  Well, the pointer is dangling because the memory that it used to
point to is deallocated.  That piece of memory eventually gets reallocated
to hold other values (in this case, when a new stack frame is allocated).
If that value is an address, you would get nondeterministic behavior.

   In any case, it is a memory error that needs to be fixed!  So let's see if
ASAN can help us this time as well:

   ```
   $ ./stack_pointer_return.asan
   [Sent data]
   AddressSanitizer:DEADLYSIGNAL
   =================================================================
   ==3983404==ERROR: AddressSanitizer: SEGV on unknown address 0x000000000000 (pc 0x55be4fe0f2f3 bp 0x7ffff2292920 sp 0x7ffff2292900 T0)
   ==3983404==The signal is caused by a READ memory access.
   ==3983404==Hint: address points to the zero page.
       #0 0x55be4fe0f2f2 in send_data /afs/pitt.edu/home/w/a/wahn/teaching/cs1632/CS1632_Sanitizer/stack_pointer_return.c:8
       #1 0x55be4fe0f454 in main /afs/pitt.edu/home/w/a/wahn/teaching/cs1632/CS1632_Sanitizer/stack_pointer_return.c:23
       #2 0x7f6edbe040b2 in __libc_start_main (/lib/x86_64-linux-gnu/libc.so.6+0x240b2)
       #3 0x55be4fe0f1cd in _start (/afs/pitt.edu/home/w/a/wahn/teaching/cs1632/CS1632_Sanitizer/stack_pointer_return.asan+0x11cd)

   AddressSanitizer can not provide additional info.
   SUMMARY: AddressSanitizer: SEGV /afs/pitt.edu/home/w/a/wahn/teaching/cs1632/CS1632_Sanitizer/stack_pointer_return.c:8 in send_data
   ==3983404==ABORTING
   ```

   Again, stack_pointer_return.c:8 is flagged as an illegal read because it
is attempting to read a location that has already been deallocated.  

## Debugging

Modify stack_overflow.c and stack_pointer_return.c so that they no longer
contain memory errors.  

Once you are done, invoke 'make' again to recompile the binaries:

```
$ make
gcc -c -g -w stack_overflow.c -o stack_overflow.o
gcc stack_overflow.o -lm -o stack_overflow.bin
gcc -c -g -w -fsanitize=address stack_overflow.c -o stack_overflow.asan.o
gcc stack_overflow.asan.o -lm -fsanitize=address -o stack_overflow.asan
...
```

Now, you should now see the data properly sent to the output in both cases:


```
$ ./stack_overflow.asan 
[Sent data]
48 65 6c 6c 6f 2e 2e  0
```

```
$ ./stack_pointer_return.asan
[Sent data]
 1  2  3  4  5  6  7  8 
```

Since there are no memory errors, the ASAN instrumentation should not output
any errors.

If you are stuck debugging the programs, here are some hints:

1. stack_overflow.c contains a stack overflow, so the solution is to reduce the
   number of bytes to fit within the provided buffer.
1. stack_pointer_return.c is attempting to return a pointer to a stack
   location.  One way to fix it is to declare the array to be a static local
variable so that it gets moved from the stack to static memory which has
persistent duration.  

## Comparing Google ASAN with Valgrind

You may have used a runtime memory error checking tool called Valgrind in CS
449: Introduction to System Software or somewhere else.  In terms of purpose,
ASAN and Valgrind share common goals.  However, ASAN is superior to Valgrind in
some ways.  That is because ASAN performs instrumentation at the source code
level whereas Valgrind performs instrumentation at the binary level.  A lot of
the semantic information that was present at the source code level is removed
at the binary level, meaning Valgrind instrumentation cannot be as detailed and
as efficient as ASAN instrumentation.  

For one thing, ASAN is much faster than Valgrind.  Since the source code
provides much more semantic information, ASAN can make a much better decision
on where instrumentation is needed.  Also, the instrumentation gets optimized
along with other code using compiler optimizations.  'binary_tree.c' is a
benchmark in the [Language Shootout Benchmark
Suite](https://benchmarksgame-team.pages.debian.net/benchmarksgame/index.html).
Let's time 'binary_tree.c' a) running without instrumentation, b) running with
ASAN instrumentation, and c) running with Valgrind instrumentation:

   ```
   $ time ./binary_tree.bin 10
   ...
   real    0m0.012s
   user    0m0.012s
   sys     0m0.000s

   $ time ./binary_tree.asan 10
   ...
   real    0m0.174s
   user    0m0.145s
   sys     0m0.029s

   $ time valgrind ./binary_tree.bin 10
   ...
   real    0m1.831s
   user    0m1.723s
   sys     0m0.108s
   ```

   'time' is a Linux utility used to time an application.  The last three rows
starting with 'real', 'user', and 'sys' is output from the 'time' utility and
not from the application.  We are going to learn more about it when we talk
about Performance Testing, but for now, all you need to know is that 'real'
measures real time (as in actual wall clock time to run an application).  As
you can see, ASAN results in an approximately 14.5X slowdown whereas Valgrind
results in an approximately 152.6X slowdown!

So, is Valgrind obsolete?  No, Valgrind does have one strong point: that it can
instrument binaries without the need of source code and without the need of
recompilation.  But if you do have the source code (which is typically the case
for tested software), most people would prefer ASAN over Valgrind.

# Testing and Debugging Datarace Errors

## Using Google TSAN (Thread Sanitizer)

datarace.c is a buggy program with a datarace on the variable 'shared'.  Hence,
everytime you run the program you will get nondeterministic output:

```
$ ./datarace.bin
shared=1024461
$ ./datarace.bin
shared=1041862
$ ./datarace.bin
shared=1021775
```

Now let's try using TSAN to discover this bug by running the instrumented
datarace.tsan binary.  Note that we need to run TSAN with ASLR off, which is in
theory not necessary, but is necessary in this case because of compatibility
with how Ubuntu 24.04 (the linux version for our current devcontainer) does
ASLR with an entropy of 32 bits (vm.mmap_rnd_bits=32).  Don't worry if that
does not make sense to you --- just make sure you run TSAN with ALSR off.

```
$ bash run_aslr_off.sh ./datarace.tsan
==================
WARNING: ThreadSanitizer: data race (pid=2438881)
  Write of size 4 at 0x55eb33b6f014 by thread T1:
    #0 add /home/PITT/wahn/nondeterminism/C/datarace.c:8 (datarace.tsan+0x12af)

  Previous read of size 4 at 0x55eb33b6f014 by main thread:
    #0 add /home/PITT/wahn/nondeterminism/C/datarace.c:8 (datarace.tsan+0x129a)
    #1 main /home/PITT/wahn/nondeterminism/C/datarace.c:18 (datarace.tsan+0x1325)

  Location is global 'shared' of size 4 at 0x55eb33b6f014 (datarace.tsan+0x000000004014)

  Thread T1 (tid=2438883, running) created by main thread at:
    #0 pthread_create ../../../../src/libsanitizer/tsan/tsan_interceptors_posix.cpp:962 (libtsan.so.0+0x5ea79)
    #1 main /home/PITT/wahn/nondeterminism/C/datarace.c:16 (datarace.tsan+0x131b)

SUMMARY: ThreadSanitizer: data race /home/PITT/wahn/nondeterminism/C/datarace.c:8 in add
==================
shared=1000000
ThreadSanitizer: reported 1 warnings
```

It tells you exactly what each thread was doing to cause the datarace.  The
"main thread" was executing add in line datarace.c:8 and "thread T1" (the
child thread) was likewise executing add at the same source line.  That is
exactly where the unprotected 'shared++' is happening.

## Debugging

Modify datarace.c so that it no longer contains datarace errors.  Edit and
compile using 'make' as before.  After debugging, you should now see the
shared variable incremented 2000000 times every time you run it:

```
$ ./datarace.bin
shared=2000000
```

Hint: you will have to use the pthreads mutex API to acquire and relase the
lock while the update to the shared variable is happening.  Use the
pthread_mutex_init, pthread_mutex_lock, and pthread_mutex_unlock APIs you
learned in CS 449.  If you need a reminder, here is the pthread_mutex_init
manpage: https://linux.die.net/man/3/pthread_mutex_init

There is some example code at the bottom of the manpage.  For your uses, using
the PTHREAD_MUTEX_INITIALIZER initializer should suffice.

# Downloading Your Work

Codespaces live in GitHub cloud storage and is garbage collected after 30 days
of inactivity (if you don't do anything about it after a 24-hour warning
email).  That means any changes to the files you applied will be destroyed too.
Note that the file changes are not pushed to the central GitHub repository
unless you do a git commit and push, as the codespace is just another machine
with a cloned copy.  Now you don't own the course repository so you cannot push
your changes there (and you wouldn't want to either!).  So your other option is
to right click on the files you want to backup on the IDE and then click
`Download` to download them to your machine.

# Resources

Google Address Sanitizer (ASAN) Wiki:
https://github.com/google/sanitizers/wiki/addresssanitizer

Google Thread Sanitizer (TSAN) Report Format Wiki:
https://github.com/google/sanitizers/wiki/ThreadSanitizerReportFormat

Google Thread Sanitizer (TSAN) About Races Wiki:
https://github.com/google/sanitizers/wiki/ThreadSanitizerAboutRaces

Stack overflow, stack pointer return, and data race are also well know security
vunerabilities documented by MITRE in the Common Weakness Enumeration (CWE).

* CWE-121: Stack-based Buffer Overflow: https://cwe.mitre.org/data/definitions/121.html
* CWE-562: Return of Stack Variable Address: https://cwe.mitre.org/data/definitions/562.html
* CWE-362: Concurrent Execution using Shared Resource with Improper Synchronization ('Race Condition'): https://cwe.mitre.org/data/definitions/362.html
