import java.awt.Graphics;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class ClockProcess extends JPanel {
    private ArrayList<ClockEvent> events;
    protected JButton processButton;
    private JPanel processLinePanel;
    private GridLayout processLinePanelLayout;

    public ClockProcess(int processIndex) {
        events = new ArrayList<ClockEvent>();
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        processButton = new JButton("P" + String.valueOf(processIndex + 1));
        // give button ID so that ButtonListener knows which one was clicked
        processButton.putClientProperty("id", processIndex);
        processLinePanel = new JPanel();
        processLinePanelLayout = new GridLayout(1, 0);
        processLinePanel.setLayout(processLinePanelLayout);
        add(processButton);
        add(processLinePanel);
    }

    public ArrayList<ClockEvent> getEvents() { return events; }

    public boolean isEmpty() { return events.isEmpty(); }

    public int numEvents() { return events.size(); }

    public ClockEvent get(int index) { return events.get(index); }

    public void addEvent(ClockEvent event) {
        events.add(event);
        processLinePanelLayout.setColumns(processLinePanelLayout.getColumns() +
                                          1);
        processLinePanel.add(event);
        revalidate(); // necessary for new ClockEvent to actually show up
        repaint();    // necessary to redraw the process arrow
    }

    public void clear() {
        events.clear();
        processLinePanel.removeAll();
    }

    // TODO: decide if this should be just for a ProcessLine class?
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        int bWidth = processButton.getWidth();
        int fullWidth = getWidth();
        int pHeight = processLinePanel.getHeight();
        Utils.drawLineWithArrow(g, bWidth, pHeight / 2, fullWidth, pHeight / 2,
                                0);
    }
}
