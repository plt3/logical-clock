import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.ParseException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class OuterFrame extends JFrame implements MouseListener {
    protected VisualArea mainArea;
    protected ButtonArea buttonArea;
    protected ButtonListener bListener;
    protected ProcessNumListener pListener;
    protected static final int DEFAULT_NUM_PROCESSES = 5;

    public OuterFrame() {
        bListener = new ButtonListener();
        pListener = new ProcessNumListener();
        mainArea = new VisualArea(DEFAULT_NUM_PROCESSES, bListener);
        buttonArea = new ButtonArea(bListener, pListener);
        add(mainArea, BorderLayout.CENTER);
        add(buttonArea, BorderLayout.SOUTH);

        setFocusable(true);
        addKeyListener(new KeybindListener());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setTitle("Logical Clock Calculator");
        setVisible(true);
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
        OuterFrame.this.requestFocusInWindow();
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
                OuterFrame.this.mainArea.addEvent(buttonId, OuterFrame.this);
            }
            OuterFrame.this.requestFocusInWindow();
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
                        mainArea.addProcess(true);
                    } else if (value == prevValue - 1) {
                        mainArea.removeProcess(true);
                    } else {
                        // otherwise redraw all processes
                        mainArea.setNumProcesses(value);
                    }
                    prevValue = value;
                }
            }
            OuterFrame.this.requestFocusInWindow();
        }
    }

    class KeybindListener implements KeyListener {
        private ClockEvent prevEvent = null;

        @Override
        public void keyTyped(KeyEvent e) {
            char keyChar = e.getKeyChar();
            JSpinner spinner = OuterFrame.this.buttonArea.processNumSpinner;
            int spinnerVal = (int)spinner.getValue();

            if (Character.isDigit(keyChar)) {
                // press number keys to add events to corresponding process
                int processNum = Character.getNumericValue(keyChar) - 1;
                if (processNum >= 0 &&
                    processNum < mainArea.getNumProcesses()) {
                    OuterFrame.this.mainArea.addEvent(processNum,
                                                      OuterFrame.this);
                }
            } else if (keyChar == '+') {
                // add/remove processes with +/-
                spinner.setValue(spinnerVal + 1);
            } else if (keyChar == '-' && spinnerVal > 1) {
                spinner.setValue(spinnerVal - 1);
            } else if (Character.isLetter(keyChar) &&
                       OuterFrame.this.mainArea.curEventLabel <= 26) {
                ClockEvent event =
                    OuterFrame.this.mainArea.getEvent(String.valueOf(keyChar));
                if (event != null) {
                    if (prevEvent == null) {
                        event.setDrawOutline(true);
                        prevEvent = event;
                    } else {
                        if (prevEvent != event) {
                            OuterFrame.this.mainArea.addMessage(prevEvent,
                                                                event);
                        }
                        prevEvent.setDrawOutline(false);
                        prevEvent = null;
                    }
                    buttonArea.displayTimestamps(event);
                    repaint();
                }
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {}

        @Override
        public void keyReleased(KeyEvent e) {}
    }
}
