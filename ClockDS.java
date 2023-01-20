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
        processes.get(process).add(new Event(curEventVal));
        curEventVal++;
    }

    public void addEvents(int process, int numEvents) {
        ArrayList<Event> procList = processes.get(process);
        for (int i = 0; i < numEvents; i++) {
            procList.add(new Event(curEventVal));
            curEventVal++;
        }
    }

    public void addProcess() { processes.add(new ArrayList<Event>()); }

    public String toString() { return processes.toString(); }

    private class Event {
        int label;
        Event toPtr = null;
        Event fromPtr = null;
        int lamportTime = -1;
        // TODO: vector time

        public String toString() { return String.valueOf(label); }

        public Event(int val) { label = val; }
    }

    public static void main(String[] args) {
        ClockDS c = new ClockDS(5);
        c.addEvents(0, 4);
        c.addEvents(2, 3);
        System.out.println(c);
    }
}
