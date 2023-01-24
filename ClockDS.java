import java.util.ArrayList;
import java.util.Collections;

public class ClockDS {
    private ArrayList<ArrayList<Event>> processes =
        new ArrayList<ArrayList<Event>>();
    private int curEventLabel = 1;

    public ClockDS(int numProcesses) {
        for (int i = 0; i < numProcesses; i++) {
            processes.add(new ArrayList<Event>());
        }
    }

    public void addEvent(int process) {
        ArrayList<Event> curProc = processes.get(process);
        Event newEvent = new Event(curEventLabel, process);
        if (!curProc.isEmpty()) {
            Event prevEvent = curProc.get(curProc.size() - 1);
            newEvent.prevPtr = prevEvent;
            prevEvent.nextPtr = newEvent;
        }
        curProc.add(newEvent);
        propagateTimestamps(newEvent);
        curEventLabel++;
    }

    public Event getEvent(int process, int eventNum) {
        return processes.get(process).get(eventNum);
    }

    public void addEvents(int process, int numEvents) {
        for (int i = 0; i < numEvents; i++) {
            addEvent(process);
        }
    }

    public void addMessage(Event from, Event to) {
        from.toPtr = to;
        to.fromPtr = from;
        propagateTimestamps(to);
    }

    public void addProcess() {
        processes.add(new ArrayList<Event>());
        for (ArrayList<Event> process : processes) {
            for (Event event : process) {
                event.vectorTime.add(0);
            }
        }
    }

    // clear and recalculate timestamps on all events
    public void recalculateTimes() {
        for (ArrayList<Event> process : processes) {
            // reset timestamps for all Events
            for (Event event : process) {
                event.lamportTime = 0;
                for (int i = 0; i < event.vectorTime.size(); i++) {
                    event.vectorTime.set(i, 0);
                }
            }
            if (!process.isEmpty()) {
                propagateTimestamps(process.get(0));
            }
        }
    }

    // recursive method to set correct Lamport and vector time on event and then
    // update all events happening after it
    private void propagateTimestamps(Event event) {
        if (event != null) {
            boolean updated = false;
            int prevLamp = 0;
            int fromLamp = 0;
            ArrayList<Integer> prevVec = new ArrayList<Integer>(
                Collections.nCopies(processes.size(), 0));
            ArrayList<Integer> fromVec = new ArrayList<Integer>(
                Collections.nCopies(processes.size(), 0));

            if (event.prevPtr != null) {
                prevLamp = event.prevPtr.lamportTime;
                prevVec = event.prevPtr.vectorTime;
            }
            if (event.fromPtr != null) {
                fromLamp = event.fromPtr.lamportTime;
                fromVec = event.fromPtr.vectorTime;
            }

            // update Lamport timestamp
            int newLampTime = Math.max(prevLamp, fromLamp) + 1;
            if (newLampTime > event.lamportTime) {
                event.lamportTime = newLampTime;
                updated = true;
            }

            // update vector timestamp
            for (int i = 0; i < event.vectorTime.size(); i++) {
                int newEntry = Math.max(prevVec.get(i), fromVec.get(i));
                if (newEntry > event.vectorTime.get(i)) {
                    event.vectorTime.set(i, newEntry);
                    updated = true;
                }
            }
            int newVecTime = Math.max(prevVec.get(event.process),
                                      fromVec.get(event.process)) +
                             1;
            if (newVecTime > event.vectorTime.get(event.process)) {
                event.vectorTime.set(event.process, newVecTime);
                updated = true;
            }

            // if changes have been made, make sure to propagate
            if (updated) {
                propagateTimestamps(event.nextPtr);
                propagateTimestamps(event.toPtr);
            }
        }
    }

    public String toString() {
        String retString = "";

        for (int i = 0; i < processes.size(); i++) {
            ArrayList<Event> process = processes.get(i);
            retString += "P" + String.valueOf(i) + ": ";
            for (int j = 0; j < process.size(); j++) {
                retString += process.get(j).toString();
                if (j + 1 != process.size()) {
                    retString += ", ";
                }
            }
            retString += "\n";
        }

        return retString.trim();
    }

    private class Event {
        private int label;
        private int process;
        private Event nextPtr = null; // next event on process
        private Event prevPtr = null; // previous event on process
        private Event toPtr = null;   // event receiving message from this event
        private Event fromPtr = null; // event who sent a message to this event
        private int lamportTime = 0;
        private ArrayList<Integer> vectorTime = new ArrayList<Integer>(
            Collections.nCopies(ClockDS.this.processes.size(), 0));

        public Event(int label, int process) {
            this.label = label;
            this.process = process;
        }

        public String toString() {
            String retString = "(" + String.valueOf(label);
            retString += ":" + String.valueOf(lamportTime);
            return retString + ")";
        }
    }

    public static void main(String[] args) {
        ClockDS c = new ClockDS(3);
        c.addProcess();
        // c.addEvents(0, 2);
        // c.addEvents(1, 2);
        // c.addEvents(2, 2);
        // c.addMessage(c.getEvent(0, 1), c.getEvent(1, 0));
        // c.addMessage(c.getEvent(1, 1), c.getEvent(2, 1));

        c.addEvents(0, 3);
        c.addEvents(1, 5);
        c.addEvents(2, 4);
        c.addEvents(3, 4);
        c.addMessage(c.getEvent(0, 0), c.getEvent(1, 1));
        c.addMessage(c.getEvent(0, 2), c.getEvent(3, 3));
        c.addMessage(c.getEvent(1, 0), c.getEvent(2, 2));
        c.addMessage(c.getEvent(1, 3), c.getEvent(2, 3));
        c.addMessage(c.getEvent(2, 0), c.getEvent(0, 1));
        c.addMessage(c.getEvent(3, 0), c.getEvent(2, 1));
        c.addMessage(c.getEvent(3, 1), c.getEvent(1, 2));
        c.addMessage(c.getEvent(3, 2), c.getEvent(1, 4));
        System.out.println(c);
        System.out.println(c.getEvent(3, 3).vectorTime);
    }
}
