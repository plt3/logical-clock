# logical-clock

> Java Swing Lamport/vector clock calculator for events over multiple processes

## Demo:

https://user-images.githubusercontent.com/65266160/217148177-bfa497a3-2bde-46c1-a16f-a76a00c5186b.mov

## Installation and Running:

- tested with java 19.0.1
- clone repo with `git clone https://github.com/plt3/logical-clock`
- in project directory, compile with `javac LogicalClock.java`
- then run Swing application with `java LogicalClock`

## Usage:

- press buttons to the left of process lines to add events to the process
- mouse over an event to automatically see its Lamport and vector timestamps
- click on an event to set it as the source for a message (you should see it highlighted in red)
  - then, click on another event to set it as the destination for a message
- can clear all events and change the number of processes with the bottom buttons

### Keybindings:

- type the key of a process number to add events to that process
  - i.e. typing `1` will add an event to process **P1**
- type the letter of an event to set it as a message source, then another event to set it as the destination
  - i.e. typing `ab` will send a message from event **a** to event **b**
- type `+` and `-` to increment/decrement the number of processes
