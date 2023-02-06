import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.ParseException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class OuterFrame extends JFrame implements MouseListener {
    VisualArea mainArea;
    ButtonArea buttonArea;
    ButtonListener bListener;
    ProcessNumListener pListener;
    protected static final int DEFAULT_NUM_PROCESSES = 5;

    public OuterFrame() {
        bListener = new ButtonListener();
        pListener = new ProcessNumListener();
        mainArea = new VisualArea(DEFAULT_NUM_PROCESSES, bListener);
        buttonArea = new ButtonArea(bListener, pListener);
        add(mainArea, BorderLayout.CENTER);
        add(buttonArea, BorderLayout.SOUTH);

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
            buttonArea.displayTimestamps(curEvent);
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
        buttonArea.displayTimestamps(curEvent);
    }

    @Override
    public void mouseExited(MouseEvent e) {}

    class ButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JButton source = (JButton)e.getSource();
            if (source == OuterFrame.this.buttonArea.resetButton) {
                mainArea.deleteAllEvents();
                OuterFrame.this.buttonArea.displayTimestamps(null);
            } else {
                // get ID of button that was clicked
                int buttonId = (int)source.getClientProperty("id");
                addEvent(buttonId);
            }
        }
    }

    class ProcessNumListener implements ChangeListener {
        private int prevValue = OuterFrame.DEFAULT_NUM_PROCESSES;

        @Override
        public void stateChanged(ChangeEvent e) {
            JSpinner source = (JSpinner)e.getSource();
            try {
                source.commitEdit();
            } catch (ParseException ex) {
            }
            int value = (Integer)source.getValue();
            if (value != prevValue) {
                if (value <= 0) {
                    // don't let value go below 1
                    source.setValue(1);
                } else {
                    OuterFrame.this.buttonArea.displayTimestamps(null);
                    if (value == prevValue + 1) {
                        // just call addProcess/removeProcess if
                        // incrementing/decrementing
                        // number of processes
                        mainArea.addProcess(bListener, true);
                    } else if (value == prevValue - 1) {
                        mainArea.removeProcess(true);
                    } else {
                        // otherwise redraw all processes
                        mainArea.setNumProcesses(value, bListener);
                    }
                    prevValue = value;
                }
            }
        }
    }
}
