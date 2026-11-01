FUN-USAGE - For the GUI and text UI, there shall be three or four commandline
arguments. The first argument (slot count) and second argument (bean count)
shall be a number greater than 0, and the third argument is either "luck" or
"skill". The optional fourth commandline argument shall be "debug". Otherwise,
the program shall terminate with a usage help message.

FUN-MACHINE-DEBUG - When either UI is invoked with the optional "debug"
argument, the program shall print a representation of the machine layout,
in-flight bean positions, and slot bean counts after each changed state while
the simulation is advancing.

FUN-MACHINE-INIT - When either UI is invoked with a slot count of N, the bean
machine is configured with N-1 rows of pegs in a triangular formation where the
top row has 1 peg and the bottom row has N-1 pegs. If N is 1, there shall be no
peg rows. Below the pegs, there shall be N empty slots numbered from 0 to N-1.
There shall be a bean above the top row peg, or above the single slot if N is 1.
The number of beans in the remaining pool shall be the bean count passed on the
command line minus one.

FUN-MACHINE-STEP - The bean machine simulation shall advance one step by having
each bean in the machine fall to the next row of pegs. They shall fall left of
the peg or to the right of the peg according to the requirements set forth in
either FUN-MACHINE-LUCK or FUN-MACHINE-SKILL. When a bean is below the bottom
row of pegs, it shall fall into one of the slots below it. When the number of
remaining beans is greater than 0, a new bean shall be inserted above the top
row peg.

FUN-MACHINE-LUCK - When a bean encounters a peg, it shall have equal chances of
falling left or right.

FUN-MACHINE-SKILL - At the start of the program, each bean shall be assigned a
skill level 0 to (slot count - 1) according to a normal distribution. The skill
level of a bean shall not change for the duration of the program. When falling
through the machine, a bean shall fall right for the first N pegs it encounters,
where N is its skill level. For the remaining pegs, it shall fall left.

FUN-MACHINE-AVERAGE - The machine shall maintain an average slot index of all
beans currently in slots, weighted by the number of beans in each slot. If no
beans are in slots, the average shall be 0. When displayed, the average shall be
rounded to the nearest hundredth, with trailing zeros and any trailing decimal
point omitted (for example, "2.1" and "1" rather than "2.10" and "1.00").

FUN-MACHINE-LOWER-HALF - On invocation of the "lower half" feature, the feature
shall affect only beans in slots. It shall keep half of those beans, discarding
the rest, and leave in-flight and remaining-pool beans unchanged. If there is an
odd number of slotted beans, it shall keep (N+1)/2 beans, where N is the number
of slotted beans. The kept beans shall be those in the lowest-indexed slots (the
leftmost slots).

FUN-MACHINE-UPPER-HALF - On invocation of the "upper half" feature, the feature
shall affect only beans in slots. It shall keep half of those beans, discarding
the rest, and leave in-flight and remaining-pool beans unchanged. If there is an
odd number of slotted beans, it shall keep (N+1)/2 beans, where N is the number
of slotted beans. The kept beans shall be those in the highest-indexed slots
(the rightmost slots).

FUN-MACHINE-COLLECT - On invocation of the "collect" feature, all beans in
flight and in the slots shall be added back to the pool of remaining beans. The
machine shall then prepare for the next run by moving one bean from that pool to
the top of the machine, if any beans remain.

FUN-MACHINE-RESET - On invocation of the "reset" feature, the machine shall be
reset to the initial state using the original beans.

FUN-TEXTUI-INIT - When the Text UI is invoked, the bean machine shall be
initialized as described in FUN-MACHINE-INIT. The console shall display a list
of commands consisting of "S - Start the bean simulation", "L - Lower half",
"U - Upper half", "C - Collect beans and prepare for the next run", "R - Reset
machine to initial state", "Q - Quit the program".

FUN-TEXTUI-PROMPT - When the Text UI is invoked and after the completion of each
command other than "Q" or "q", the user shall be prompted to enter a command
using a single letter "S", "L", "U", "C", "R", or "Q" which stands for
"(S)tart", "(L)ower half", "(U)pper half", "(C)ollect", "(R)eset", and "(Q)uit"
respectively.

FUN-TEXTUI-START - On command "S" or "s", the bean simulation shall advance
until all beans have reached slots, according to FUN-MACHINE-STEP.

FUN-TEXTUI-LOWER-HALF - On command "L" or "l", the "lower half" feature
described in FUN-MACHINE-LOWER-HALF shall be invoked.

FUN-TEXTUI-UPPER-HALF - On command "U" or "u", the "upper half" feature
described in FUN-MACHINE-UPPER-HALF shall be invoked.

FUN-TEXTUI-COLLECT - On command "C" or "c", the "collect" feature described in
FUN-MACHINE-COLLECT shall be invoked.

FUN-TEXTUI-RESET - On command "R" or "r", the "reset" feature described in
FUN-MACHINE-RESET shall be invoked.

FUN-TEXTUI-QUIT - On command "Q" or "q", the Text UI shall exit the program.

FUN-TEXTUI-SLOTS - After every valid command except the "quit" command, the Text
UI shall display the slot bean counts followed by the remaining bean count
followed by the average slot index. The output for slot bean counts shall be
"Slot bean counts:" followed by a row of numbers representing the bean counts
for each slot. The number of values in the row shall equal the slot count passed
into the command line. The output for the remaining bean count shall be
"Remaining: " followed by the number of remaining beans. The output for the
average slot index shall be "Average: " followed by the number specified in
FUN-MACHINE-AVERAGE.

FUN-TEXTUI-INVALID-COMMAND - On input of any other command than the ones
specified in FUN-TEXTUI-PROMPT, the list of commands described in
FUN-TEXTUI-INIT shall be displayed on the console and the user shall be prompted
again for input.

FUN-GUI-INIT - When the GUI is invoked, the bean machine shall be initialized as
described in FUN-MACHINE-INIT. The window shall display the triangular set of
pegs followed by the slots in the bean machine. Below the machine, there shall
be 8 buttons: "Step", "Slow", "Fast", "Stop", "Lower Half", "Upper Half",
"Collect", "Reset". The top right corner shall display two strings: "Average =
\<num\>", where \<num\> is the number defined in FUN-MACHINE-AVERAGE and
"Remaining = \<num\>", where \<num\> is the number of beans in the remaining
pool. The display shall update whenever the machine state changes.

FUN-GUI-SLOTS - The bottom slots shall display the number of beans in each slot
in the form of a bar graph where the height of the bar is proportional to the
number of beans.

FUN-GUI-STEP - When the "Step" button is pressed, the bean counter shall advance
one step as described in FUN-MACHINE-STEP.

FUN-GUI-CONTINUOUS - When the "Slow" or "Fast" button is pressed, the bean
machine shall advance steps continuously. Each step in Slow mode shall take
longer than each step in Fast mode.

FUN-GUI-STOP - When the "Stop" button is pressed, the beans shall stop advancing
after any step currently being animated is complete.

FUN-GUI-LOWER-HALF - When the "Lower Half" button is pressed, the "lower half"
feature described in FUN-MACHINE-LOWER-HALF shall be invoked.

FUN-GUI-UPPER-HALF - When the "Upper Half" button is pressed, the "upper half"
feature described in FUN-MACHINE-UPPER-HALF shall be invoked.

FUN-GUI-COLLECT - When the "Collect" button is pressed, the "collect" feature
described in FUN-MACHINE-COLLECT shall be invoked.

FUN-GUI-RESET - When the "Reset" button is pressed, the "reset" feature
described in FUN-MACHINE-RESET shall be invoked.
