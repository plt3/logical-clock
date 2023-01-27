import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JButton;
import javax.swing.JFrame;

public class OuterFrame extends JFrame implements MouseListener {
    VisualArea mainArea;
    ButtonListener bListener;

    public OuterFrame() {
        bListener = new ButtonListener();
        mainArea = new VisualArea(5, bListener);
        add(mainArea, BorderLayout.CENTER);
        JButton b = new JButton("text here");
        add(b, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setTitle("Logical Clock Calculator");
        setVisible(true);
    }

    public void addEvent(int process) {
        ClockEvent newEvent = mainArea.addEvent(process);
        newEvent.addMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO: handle cases where messages shouldn't be added
        ClockEvent curEvent = (ClockEvent)e.getSource();
        if (mainArea.messageSource == null) {
            curEvent.setDrawOutline(true);
            mainArea.messageSource = curEvent;
        } else {
            mainArea.messageSource.setDrawOutline(false);
            mainArea.addMessage(mainArea.messageSource, curEvent);
            mainArea.messageSource = null;
        }
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {
        ClockEvent curEvent = (ClockEvent)e.getSource();
        // TODO: actually display this somewhere in the UI
        System.out.println(curEvent.vectorTime);
    }

    @Override
    public void mouseExited(MouseEvent e) {}

    class ButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JButton source = (JButton)e.getSource();
            // get ID of button that was clicked
            int buttonId = (int)source.getClientProperty("id");
            addEvent(buttonId);
        }
    }
}
