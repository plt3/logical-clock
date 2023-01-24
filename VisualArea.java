import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Polygon;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class VisualArea extends JPanel {
    private int numProcesses = 5; // TODO: allow this to change
    private ClockDS eventStructure;

    public VisualArea() {
        eventStructure = new ClockDS(numProcesses);
        setLayout(new GridLayout(numProcesses, 1));
        for (int i = 0; i < numProcesses; i++) {
            JPanel processPanel = new JPanel();
            processPanel.setLayout(
                new BoxLayout(processPanel, BoxLayout.X_AXIS));
            ProcessLine processLine = new ProcessLine();
            JButton processButton = new JButton("P" + String.valueOf(i + 1));
            processPanel.add(processButton);
            processPanel.add(processLine);
            add(processPanel);
        }
    }

    protected class ProcessLine extends JPanel {
        @Override
        public void paint(Graphics g) {
            super.paint(g);

            int width = getWidth();
            int height = getHeight();
            g.drawLine(0, height / 2, width, height / 2);
            Polygon arrowHead = new Polygon();
            arrowHead.addPoint(width, height / 2);
            arrowHead.addPoint(width - 20, height / 2 - 7);
            arrowHead.addPoint(width - 20, height / 2 + 7);
            g.fillPolygon(arrowHead);
        }
    }
}
