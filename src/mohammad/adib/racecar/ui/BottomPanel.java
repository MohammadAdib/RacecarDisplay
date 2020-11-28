package mohammad.adib.racecar.ui;

import mohammad.adib.racecar.util.Utils;

import javax.swing.*;
import java.awt.*;

public class BottomPanel extends JLayeredPane {

    private JLabel coolantLabel, intakeLabel;
    
    public BottomPanel() {
        setBackground(Color.decode("#222222"));
        setOpaque(true);
        setupImages();
        setupLabels();
    }

    private void setupImages() {
        ImageIcon coolantIcon = new ImageIcon("coolant_temp.png");
        JLabel coolantImageLabel = new JLabel(coolantIcon);
        coolantImageLabel.setBounds(78, 18, 38, 38);
        add(coolantImageLabel);
        ImageIcon intakeIcon = new ImageIcon("intake_temp.png");
        JLabel intakeImageLabel = new JLabel(intakeIcon);
        intakeImageLabel.setBounds(Utils.WIDTH - 116, 18, 38, 38);
        add(intakeImageLabel);
    }

    private void setupLabels() {
        coolantLabel = new JLabel();
        coolantLabel.setText("210º C");
        coolantLabel.setFont(new Font("Dialog", Font.BOLD, 18));
        coolantLabel.setVerticalAlignment(JLabel.CENTER);
        coolantLabel.setHorizontalAlignment(JLabel.CENTER);
        coolantLabel.setBounds(60, 68, 75, 20);
        coolantLabel.setForeground(Color.WHITE);
        add(coolantLabel);
        
        intakeLabel = new JLabel();
        intakeLabel.setText("210º C");
        intakeLabel.setFont(new Font("Dialog", Font.BOLD, 18));
        intakeLabel.setVerticalAlignment(JLabel.CENTER);
        intakeLabel.setHorizontalAlignment(JLabel.CENTER);
        intakeLabel.setBounds(Utils.WIDTH - 135, 68, 75, 20);
        intakeLabel.setForeground(Color.WHITE);
        add(intakeLabel);
    }
}
