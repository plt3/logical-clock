import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class ClockEvent extends JPanel {
    protected String label;
    protected int process;
    protected ClockEvent nextPtr = null; // next event on process
    protected ClockEvent prevPtr = null; // previous event on process
    protected ClockEvent toPtr =
        null; // event receiving message from this event
    protected ClockEvent fromPtr =
        null; // event that sent a message to this event
    protected int lamportTime = 0;
    protected ArrayList<Integer> vectorTime;
    protected boolean drawOutline = false;
    private JLabel centerLabel;

    public ClockEvent(int labelInt, int processNum, int totalProcesses) {
        this.label = Utils.convertNumberToLabel(labelInt);
        setLayout(new BorderLayout());

        this.process = processNum;
        this.vectorTime =
            new ArrayList<Integer>(Collections.nCopies(totalProcesses, 0));
        centerLabel = new JLabel(label, SwingConstants.CENTER);
        centerLabel.setFont(centerLabel.getFont().deriveFont(Font.BOLD, 20f));
        centerLabel.setForeground(Color.WHITE);
        add(centerLabel, BorderLayout.CENTER);
    }

    public void setDrawOutline(boolean drawOutline) {
        this.drawOutline = drawOutline;
    }

    public boolean getDrawOutline() { return drawOutline; }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int x = (getWidth() - VisualArea.DIAMETER) / 2;
        int y = (getHeight() - VisualArea.DIAMETER) / 2;
        g.fillOval(x, y, VisualArea.DIAMETER, VisualArea.DIAMETER);

        if (drawOutline) {
            int outlineDistance = VisualArea.DIAMETER / 20;
            g.setColor(Color.RED);
            Graphics2D g2 = (Graphics2D)g;
            g2.setStroke(new BasicStroke(VisualArea.DIAMETER / 8));
            g2.drawOval(x - outlineDistance, y - outlineDistance,
                        VisualArea.DIAMETER + 2 * outlineDistance,
                        VisualArea.DIAMETER + 2 * outlineDistance);
        }
    }

    public String toString() {
        String retString = "(" + label;
        retString += ":" + String.valueOf(lamportTime);
        return retString + ")";
    }
}
