import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

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
    protected JLabel centerLabel;

    public ClockEvent(int label, int processNum, int totalProcesses) {
        this.label = label;
        this.process = processNum;
        numProcesses = totalProcesses;
        this.vectorTime =
            new ArrayList<Integer>(Collections.nCopies(totalProcesses, 0));

        centerLabel = new JLabel(String.valueOf(label), SwingConstants.CENTER);
        centerLabel.setFont(centerLabel.getFont().deriveFont(Font.BOLD, 20f));
        centerLabel.setForeground(Color.red);
        // int ebGap = 5;
        // centerLabel.setBorder(
        //     BorderFactory.createEmptyBorder(ebGap, ebGap, ebGap, ebGap));
        setLayout(new BorderLayout());
        add(centerLabel, BorderLayout.CENTER);
        setBackground(Color.red);
    }

    @Override
    public void paintComponent(Graphics g) {
        // super.paintComponent(g);
        int x = (getWidth() - DIAMETER) / 2;
        int y = (getHeight() - DIAMETER) / 2;
        g.setColor(Color.blue);
        g.fillOval(x, y, DIAMETER, DIAMETER);
    }

    public String toString() {
        String retString = "(" + String.valueOf(label);
        retString += ":" + String.valueOf(lamportTime);
        return retString + ")";
    }
}
