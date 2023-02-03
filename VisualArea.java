import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class VisualArea extends JPanel {
    private ArrayList<ClockProcess> processes;
    private int curEventLabel = 1;
    protected ClockEvent messageSource = null;
    ActionListener bListener;

    public VisualArea(int numProcesses, ActionListener bListener) {
        this.bListener = bListener;
        processes = new ArrayList<ClockProcess>();
        setLayout(new GridLayout(numProcesses, 1));

        for (int i = 0; i < numProcesses; i++) {
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

    public void deleteAllEvents() {
        for (ClockProcess process : processes) {
            process.clear();
        }
        curEventLabel = 1;
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
                    g.drawLine(p1.x, p1.y, p2.x, p2.y);

                    // TODO: draw arrowhead
                    // Polygon arrowHead = new Polygon();
                    // double p2Angle = Math.atan((p2.x - p1.x) / (p2.y -
                    // p1.y)); System.out.println(p2Angle);
                    // arrowHead.addPoint(p2.x, p2.y);
                    // arrowHead.addPoint(p2.x - 20, p2.y - 20);
                    // arrowHead.addPoint(p2.x, p2.y - 20);
                    // arrowHead.addPoint(fullWidth - 20, pHeight / 2 + 7);
                    // g.fillPolygon(arrowHead);
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
