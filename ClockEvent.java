import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.JPanel;

public class ClockEvent extends JPanel {
    private static final int DIAMETER = 30;
    protected int label;
    protected int process;
    protected int numProcesses;
    protected ClockEvent nextPtr = null; // next event on process
    protected ClockEvent prevPtr = null; // previous event on process
    protected ClockEvent toPtr =
        null; // event receiving message from this event
    protected ClockEvent fromPtr =
        null; // event that sent a message to this event
    protected int lamportTime = 0;
    protected ArrayList<Integer> vectorTime;

    public ClockEvent(int label, int processNum, int totalProcesses) {
        this.label = label;
        this.process = processNum;
        numProcesses = totalProcesses;
        this.vectorTime =
            new ArrayList<Integer>(Collections.nCopies(totalProcesses, 0));
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.fillOval((getWidth() - DIAMETER) / 2, (getHeight() - DIAMETER) / 2,
                   DIAMETER, DIAMETER);
    }

    public String toString() {
        String retString = "(" + String.valueOf(label);
        retString += ":" + String.valueOf(lamportTime);
        return retString + ")";
    }
}
