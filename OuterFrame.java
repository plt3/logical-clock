import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JFrame;

public class OuterFrame extends JFrame {
    public OuterFrame() {
        VisualArea v = new VisualArea();
        add(v, BorderLayout.CENTER);
        JButton b = new JButton("text here");
        add(b, BorderLayout.SOUTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setTitle("Logical Clock Calculator");
        setVisible(true);
    }
}
