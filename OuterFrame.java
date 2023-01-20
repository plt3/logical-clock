import javax.swing.JButton;
import javax.swing.JFrame;

public class OuterFrame extends JFrame {
    public OuterFrame() {
        JFrame f = new JFrame();
        JButton b = new JButton("text here");
        add(b);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        setTitle("Logical Clock Calculator");
        setVisible(true);
    }
}
