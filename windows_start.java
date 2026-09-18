import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

class windows_start extends JFrame implements ActionListener {
    private JTextField textField;
    
    windows_start() {
        setSize(250, 250);
        
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setBackground(Color.BLACK);
        setResizable(false);
    
        JPanel panel = Background();
        JPanel panel_CENTER = panel_CENTER();

        panel.add(panel_CENTER);
        add(panel, BorderLayout.CENTER);
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private JPanel Background() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.black);
        return panel;
    }

    private JPanel panel_CENTER() {
        JPanel panel_CENTER = new JPanel(new GridLayout(3,1));
        panel_CENTER.setBackground(Color.black);
        JLabel label = new JLabel("อุกกาบาตกี่ลูก");
        label.setForeground(Color.white);
        label.setFont(new Font("Tahoma", Font.PLAIN, 16));

        Button button = new Button("OK");
        button.setForeground(Color.black);
        button.setFont(new Font("Tahoma", Font.PLAIN, 14));
        button.addActionListener(this);

        textField = new JTextField("5");
        textField.setForeground(Color.black);
        textField.setFont(new Font("Tahoma", Font.PLAIN, 14));

        panel_CENTER.add(label);
        panel_CENTER.add(textField);
        panel_CENTER.add(button);
        return panel_CENTER;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int count = Integer.parseInt(textField.getText());
        this.dispose();

        JFrame frame = new JFrame("Bouncing Meteorites");
        BouncingMeteorite panel = new BouncingMeteorite(count);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        panel.startThread();
    }
}