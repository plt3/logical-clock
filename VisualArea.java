import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.JButton;
import javax.swing.JPanel;

public class VisualArea extends JPanel {
    private ArrayList<ClockProcess> processes;
    private int curEventLabel = 1;

    public VisualArea(int numProcesses) {
        processes = new ArrayList<ClockProcess>();
        ButtonListener bListener = new ButtonListener();
        setLayout(new GridLayout(numProcesses, 1));

        for (int i = 0; i < numProcesses; i++) {
            ClockProcess proc = new ClockProcess(i);
            proc.processButton.addActionListener(bListener);
            processes.add(proc);
            add(proc);
        }
        addEvent(0);
    }

    class ButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JButton source = (JButton)e.getSource();
            // get ID of button that was clicked
            int buttonId = (int)source.getClientProperty("id");
            addEvent(buttonId);
        }
    }

    public void addEvent(int process) {
        ClockProcess curProc = processes.get(process);
        ClockEvent newEvent =
            new ClockEvent(curEventLabel, process, processes.size());
        if (!curProc.isEmpty()) {
            ClockEvent prevEvent = curProc.get(curProc.numEvents() - 1);
            newEvent.prevPtr = prevEvent;
            prevEvent.nextPtr = newEvent;
        }
        curProc.addEvent(newEvent);
        propagateTimestamps(newEvent);
        curEventLabel++;
    }

    public ClockEvent getEvent(int process, int eventNum) {
        return processes.get(process).get(eventNum);
    }

    public void addEvents(int process, int numEvents) {
        for (int i = 0; i < numEvents; i++) {
            addEvent(process);
        }
    }

    public void addMessage(ClockEvent from, ClockEvent to) {
        from.toPtr = to;
        to.fromPtr = from;
        propagateTimestamps(to);
    }

    public void addProcess() {
        processes.add(new ClockProcess(processes.size()));
        // TODO: need to add button listener here
        for (ClockProcess process : processes) {
            for (ClockEvent event : process.getEvents()) {
                event.vectorTime.add(0);
            }
        }
    }

    // clear and recalculate timestamps on all events
    public void recalculateTimes() {
        for (ClockProcess process : processes) {
            // reset timestamps for all Events
            ArrayList<ClockEvent> events = process.getEvents();
            for (ClockEvent event : events) {
                event.lamportTime = 0;
                for (int i = 0; i < event.vectorTime.size(); i++) {
                    event.vectorTime.set(i, 0);
                }
            }
            if (!process.isEmpty()) {
                propagateTimestamps(events.get(0));
            }
        }
    }

    // recursive method to set correct Lamport and vector time on event and then
    // update all events happening after it
    private void propagateTimestamps(ClockEvent event) {
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
            ClockProcess process = processes.get(i);
            retString += "P" + String.valueOf(i) + ": ";
            for (int j = 0; j < process.numEvents(); j++) {
                retString += process.get(j).toString();
                if (j + 1 != process.numEvents()) {
                    retString += ", ";
                }
            }
            retString += "\n";
        }

        return retString.trim();
    }
}
