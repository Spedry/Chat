package ignore.GUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static ignore.GUI.TestPOST.POST;

public class Chat {
    private JButton Send;
    private JPanel Chat;

    public Chat() {
        Send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                POST();
            }
        });
    }
    public static void main(String[] args) {
        JFrame frame = new JFrame("Chat");
        frame.setContentPane(new Chat().Chat);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
