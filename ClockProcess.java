import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Polygon;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class ClockProcess extends JPanel {
    private ArrayList<ClockEvent> events;
    protected JButton processButton;
    // private JPanel processLinePanel;
    private ProcessLinePanel pPanel;
    // private GridLayout processLinePanelLayout;

    public ClockProcess(int processIndex) {
        events = new ArrayList<ClockEvent>();
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        processButton = new JButton("P" + String.valueOf(processIndex + 1));
        // give button ID so that ButtonListener knows which one was clicked
        processButton.putClientProperty("id", processIndex);

        // processLinePanel = new JPanel();
        // processLinePanelLayout = new GridLayout(1, 0);
        // processLinePanel.setLayout(processLinePanelLayout);
        add(processButton);
        // add(processLinePanel);
        pPanel = new ProcessLinePanel();
        add(pPanel);
        if (processIndex % 2 == 0) {
            pPanel.setBackground(Color.pink);
        }
    }

    public ArrayList<ClockEvent> getEvents() { return events; }

    public boolean isEmpty() { return events.isEmpty(); }

    public int numEvents() { return events.size(); }

    public ClockEvent get(int index) { return events.get(index); }

    public void addEvent(ClockEvent event) {
        events.add(event);
        // processLinePanelLayout.setColumns(processLinePanelLayout.getColumns()
        // +
        //                                   1);
        // processLinePanel.add(event);
        // System.out.println(pPanel.layout.getColumns());
        // pPanel.layout.setColumns(pPanel.layout.getColumns() + 1);
        pPanel.subpanel.add(event);
        pPanel.subpanel
            .revalidate(); // necessary for new ClockEvent to actually show up
        pPanel.repaint();  // necessary to redraw the process arrow
        System.out.println(pPanel.getHeight());
        System.out.println(this);
        // repaint();
        // event.repaint();
    }

    public class ProcessLinePanel extends JPanel {
        protected GridLayout layout;
        protected JPanel subpanel;

        public ProcessLinePanel() {
            // layout = new GridBagLayout();
            // setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            subpanel = new JPanel();
            layout = new GridLayout(1, 0);
            subpanel.setLayout(layout);
            add(subpanel);
            // setBackground(Color.pink);
        }

        // TODO: decide if this should be just for a ProcessLine class?
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            int bWidth = processButton.getWidth();
            int fullWidth = getWidth();
            // int pHeight = processLinePanel.getHeight();
            int pHeight = getHeight();
            System.out.println("here");
            g.drawLine(0, pHeight / 2, fullWidth, pHeight / 2);
            Polygon arrowHead = new Polygon();
            arrowHead.addPoint(fullWidth, pHeight / 2);
            arrowHead.addPoint(fullWidth - 20, pHeight / 2 - 7);
            arrowHead.addPoint(fullWidth - 20, pHeight / 2 + 7);
            g.fillPolygon(arrowHead);
        }
    }
}
