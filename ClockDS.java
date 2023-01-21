import java.util.ArrayList;

public class ClockDS {
    ArrayList<ArrayList<Event>> processes = new ArrayList<ArrayList<Event>>();
    int curEventVal = 1;

    public ClockDS(int numProcesses) {
        for (int i = 0; i < numProcesses; i++) {
            processes.add(new ArrayList<Event>());
        }
    }

    public void addEvent(int process) {
        ArrayList<Event> curProc = processes.get(process);
        Event newEvent = new Event(curEventVal);
        if (!curProc.isEmpty()) {
            Event prevEvent = curProc.get(curProc.size() - 1);
            newEvent.prevPtr = prevEvent;
            prevEvent.nextPtr = newEvent;
        }
        curProc.add(newEvent);
        propagateLamportTime(newEvent);
        curEventVal++;
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
        propagateLamportTime(to);
    }

    public void addProcess() { processes.add(new ArrayList<Event>()); }

    // clear and recalculate Lamport time on all events
    public void recalculateLamportTimes() {
        for (ArrayList<Event> process : processes) {
            for (Event event : process) {
                event.lamportTime = -1;
            }
            if (!process.isEmpty()) {
                propagateLamportTime(process.get(0));
            }
        }
    }

    // recursive method to set correct Lamport time on event and then update all
    // events happening after it
    private void propagateLamportTime(Event event) {
        if (event != null) {
            int prev = 0;
            int from = 0;
            if (event.prevPtr != null) {
                prev = event.prevPtr.lamportTime;
            }
            if (event.fromPtr != null) {
                from = event.fromPtr.lamportTime;
            }
            int newTime = Math.max(prev, from) + 1;
            if (newTime > event.lamportTime) {
                event.lamportTime = newTime;
                propagateLamportTime(event.nextPtr);
                propagateLamportTime(event.toPtr);
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
        int label;
        Event nextPtr = null;
        Event prevPtr = null; // previous event on process
        Event toPtr = null;   // event receiving message from this event
        Event fromPtr = null; // event who sent a message to this event
        int lamportTime = -1;
        // TODO: vector time

        public String toString() {
            String retString = "(" + String.valueOf(label);
            retString += ":" + String.valueOf(lamportTime);
            return retString + ")";
        }

        public Event(int val) { label = val; }
    }

    public static void main(String[] args) {
        ClockDS c = new ClockDS(3);
        c.addEvents(0, 2);
        c.addEvents(1, 2);
        c.addEvents(2, 2);
        c.addMessage(c.getEvent(0, 1), c.getEvent(1, 0));
        c.addMessage(c.getEvent(1, 1), c.getEvent(2, 1));
        System.out.println(c);
    }
}
