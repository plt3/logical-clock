import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.JPanel;

public class ClockEvent extends JPanel {
    private static final int DIAMETER = 40;
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
    protected boolean drawOutline = false;

    public ClockEvent(int label, int processNum, int totalProcesses) {
        this.label = label;
        this.process = processNum;
        numProcesses = totalProcesses;
        this.vectorTime =
            new ArrayList<Integer>(Collections.nCopies(totalProcesses, 0));
    }

    public void setDrawOutline(boolean drawOutline) {
        this.drawOutline = drawOutline;
    }

    public boolean getDrawOutline() { return drawOutline; }

    @Override
    protected void paintComponent(Graphics g) {
        int x = (getWidth() - DIAMETER) / 2;
        int y = (getHeight() - DIAMETER) / 2;
        g.fillOval(x, y, DIAMETER, DIAMETER);

        if (drawOutline) {
            int outlineDistance = 2;
            g.setColor(Color.RED);
            Graphics2D g2 = (Graphics2D)g;
            g2.setStroke(new BasicStroke(5));
            g2.drawOval(x - outlineDistance, y - outlineDistance,
                        DIAMETER + 2 * outlineDistance,
                        DIAMETER + 2 * outlineDistance);
        }
    }

    public String toString() {
        String retString = "(" + String.valueOf(label);
        retString += ":" + String.valueOf(lamportTime);
        return retString + ")";
    }
}
