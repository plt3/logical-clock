import java.awt.GridLayout;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultFormatter;

public class ButtonArea extends JPanel {
    private static final String DEFAULT_TEXT = "no event selected";
    private JLabel timestampLabel;
    protected JButton resetButton;
    protected JSpinner processNumSpinner;

    public ButtonArea(ActionListener bListener, ChangeListener pListener) {
        setLayout(new GridLayout(2, 1));

        timestampLabel = new JLabel(DEFAULT_TEXT, SwingConstants.CENTER);
        timestampLabel.setFont(timestampLabel.getFont().deriveFont(24.0f));
        timestampLabel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JPanel buttonPanel = new JPanel();
        resetButton = new JButton("Clear all events");
        processNumSpinner = new JSpinner();
        JComponent spinnerEditor = processNumSpinner.getEditor();
        JFormattedTextField jftf =
            ((JSpinner.DefaultEditor)spinnerEditor).getTextField();
        jftf.setColumns(2); // make spinner display 2 digits
        JFormattedTextField field =
            (JFormattedTextField)spinnerEditor.getComponent(0);
        DefaultFormatter formatter = (DefaultFormatter)field.getFormatter();
        // TODO: check if this is necessary
        formatter.setCommitsOnValidEdit(true); // send ChangeEvents immediately
        processNumSpinner.addChangeListener(pListener);

        resetButton.addActionListener(bListener);
        buttonPanel.add(resetButton);
        buttonPanel.add(processNumSpinner);

        add(timestampLabel);
        add(buttonPanel);
    }

    public void displayTimestamps(ClockEvent event) {
        if (event != null) {
            timestampLabel.setText("Event " + event.label +
                                   ": Lamport time: " + event.lamportTime +
                                   ", vector time: " + event.vectorTime);
        } else {
            timestampLabel.setText(DEFAULT_TEXT);
        }
    }
}
