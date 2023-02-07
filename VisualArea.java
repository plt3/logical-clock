import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class VisualArea extends JPanel {
    protected static final int DIAMETER = 40;
    private ArrayList<ClockProcess> processes;
    protected int curEventLabel = 0;
    protected ClockEvent messageSource = null;
    ActionListener bListener;

    public VisualArea(int numProcesses, ActionListener bListener) {
        this.bListener = bListener;
        processes = new ArrayList<ClockProcess>();
        setLayout(new GridLayout(0, 1));
        setNumProcesses(numProcesses);
    }

    public void addEvent(int process, MouseListener mListener) {
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
        curEventLabel++;
        repaint();
        newEvent.addMouseListener(mListener);
    }

    public ClockEvent getEvent(int process, int eventNum) {
        return processes.get(process).get(eventNum);
    }

    public ClockEvent getEvent(String labelStr) {
        for (ClockProcess process : processes) {
            for (ClockEvent event : process.getEvents()) {
                if (event.label.equals(labelStr)) {
                    return event;
                }
            }
        }
        return null;
    }

    public void addMessage(ClockEvent from, ClockEvent to) {
        from.toPtr = to;
        to.fromPtr = from;
        Utils.propagateTimestamps(to, processes.size());
    }

    // add a process to the end while keeping all other events intact
    public void addProcess(boolean refresh) {
        ClockProcess proc = new ClockProcess(processes.size());
        processes.add(proc);

        for (ClockProcess process : processes) {
            for (ClockEvent event : process.getEvents()) {
                event.vectorTime.add(0);
            }
        }

        proc.processButton.addActionListener(bListener);
        add(proc);

        if (refresh) {
            revalidate();
            repaint();
        }
    }

    // remove last process while keeping all other events intact
    public void removeProcess(boolean refresh) {
        int lastInd = processes.size() - 1;

        for (ClockProcess process : processes) {
            for (ClockEvent event : process.getEvents()) {
                event.vectorTime.remove(lastInd);
            }
        }
        // remove messages to and from events on process that is getting removed
        for (ClockEvent event : processes.get(lastInd).getEvents()) {
            if (event.fromPtr != null) {
                event.fromPtr.toPtr = null;
            }
            if (event.toPtr != null) {
                event.toPtr.fromPtr = null;
            }
        }

        remove(lastInd);
        processes.remove(lastInd);

        if (refresh) {
            revalidate();
            repaint();
        }
    }

    public int getNumProcesses() { return processes.size(); }

    public void setNumProcesses(int numProcesses) {
        removeAll();
        processes.clear();
        curEventLabel = 0;
        for (int i = 0; i < numProcesses; i++) {
            addProcess(false);
        }
        revalidate();
        repaint();
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

    public void deleteAllEvents() {
        for (ClockProcess process : processes) {
            process.clear();
        }
        curEventLabel = 0;
        repaint();
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
                    Point p1 = SwingUtilities.convertPoint(
                        event, event.getWidth() / 2, event.getHeight() / 2,
                        this);
                    Point p2 = SwingUtilities.convertPoint(
                        event.toPtr, event.toPtr.getWidth() / 2,
                        event.toPtr.getHeight() / 2, this);

                    Utils.drawLineWithArrow(g, p1.x, p1.y, p2.x, p2.y,
                                            DIAMETER / 2);
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
