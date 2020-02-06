package sk.Spedry.GUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatGUI {
    private static JFrame chatFrame = new JFrame("Chat");
    private JPanel loginFrame;
    private JButton button;
    private JTextField chatField;

    private String ID;

    public static void main(String[] args) {
        chatFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ChatGUI chatGUI = new ChatGUI();
        //chatGUI.loginFrame();
    }

    /*public void loginFrame() {
        chatFrame.setVisible(false);
        loginFrame = new JFrame("Login");

    }*/

    public ChatGUI() {
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String messege;
                messege = chatField.getText();

            }
        });
    }
}
