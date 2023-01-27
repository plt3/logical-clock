import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class VisualArea extends JPanel {
    private ArrayList<ClockProcess> processes;
    private int curEventLabel = 1;
    protected ClockEvent messageSource = null;

    public VisualArea(int numProcesses, ActionListener bListener) {
        processes = new ArrayList<ClockProcess>();
        setLayout(new GridLayout(numProcesses, 1));

        for (int i = 0; i < numProcesses; i++) {
            // ClockProcess proc = new ClockProcess(i);
            // proc.processButton.addActionListener(bListener);
            // processes.add(proc);
            // add(proc);
            addProcess(bListener);
        }
    }

    public ClockEvent addEvent(int process) {
        ClockProcess curProc = processes.get(process);
        ClockEvent newEvent =
            new ClockEvent(curEventLabel, process, processes.size());
        if (!curProc.isEmpty()) {
            ClockEvent prevEvent = curProc.get(curProc.numEvents() - 1);
            newEvent.prevPtr = prevEvent;
            prevEvent.nextPtr = newEvent;
        }
        curProc.addEvent(newEvent);
        Utils.propagateTimestamps(newEvent, processes.size());
        // newEvent.addMouseListener(this);
        curEventLabel++;
        repaint();
        return newEvent;
    }

    public ClockEvent getEvent(int process, int eventNum) {
        return processes.get(process).get(eventNum);
    }

    public void addMessage(ClockEvent from, ClockEvent to) {
        from.toPtr = to;
        to.fromPtr = from;
        Utils.propagateTimestamps(to, processes.size());
    }

    public void addProcess(ActionListener bListener) {
        ClockProcess proc = new ClockProcess(processes.size());
        processes.add(proc);
        for (ClockProcess process : processes) {
            for (ClockEvent event : process.getEvents()) {
                event.vectorTime.add(0);
            }
        }
        proc.processButton.addActionListener(bListener);
        add(proc);
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
                Utils.propagateTimestamps(events.get(0), processes.size());
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setStroke(new BasicStroke(3));

        // go through each event and draw message line if there is one
        for (ClockProcess process : processes) {
            for (ClockEvent event : process.getEvents()) {
                if (event.toPtr != null) {
                    // TODO: draw arrowhead
                    Point p1 = SwingUtilities.convertPoint(
                        event, event.getWidth() / 2, event.getHeight() / 2,
                        this);
                    Point p2 = SwingUtilities.convertPoint(
                        event.toPtr, event.toPtr.getWidth() / 2,
                        event.toPtr.getHeight() / 2, this);
                    g.drawLine(p1.x, p1.y, p2.x, p2.y);
                }
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
