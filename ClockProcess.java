import java.awt.Graphics;
import java.awt.Polygon;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class ClockProcess extends JPanel {
    private ArrayList<ClockEvent> events;
    private JButton processButton;
    private JPanel processLinePanel;

    public ClockProcess(int processIndex) {
        events = new ArrayList<ClockEvent>();
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        processButton = new JButton("P" + String.valueOf(processIndex + 1));
        processLinePanel = new JPanel();
        add(processButton);
        add(processLinePanel);
    }

    public ArrayList<ClockEvent> getEvents() { return events; }

    public boolean isEmpty() { return events.isEmpty(); }

    public int numEvents() { return events.size(); }

    public ClockEvent get(int index) { return events.get(index); }

    public void addEvent(ClockEvent event) { events.add(event); }

    // TODO: decide if this should be just for a ProcessLine class?
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        int bWidth = processButton.getWidth();
        int fullWidth = getWidth();
        int pHeight = processLinePanel.getHeight();
        g.drawLine(bWidth, pHeight / 2, fullWidth, pHeight / 2);
        Polygon arrowHead = new Polygon();
        arrowHead.addPoint(fullWidth, pHeight / 2);
        arrowHead.addPoint(fullWidth - 20, pHeight / 2 - 7);
        arrowHead.addPoint(fullWidth - 20, pHeight / 2 + 7);
        g.fillPolygon(arrowHead);
    }
}
