package mohammad.adib.racecar.ui;

import mohammad.adib.racecar.util.Utils;

import javax.swing.*;
import java.awt.*;

public class RPMPanel extends JLayeredPane {

    private static final int WIDTH = Utils.WIDTH - 48;

    private static final Color BLUE = Color.decode("#0048ff");
    private static final Color YELLOW = Color.decode("#ffc000");
    private static final Color RED = Color.RED;

    private static final Color[] COLORS = new Color[]{BLUE, BLUE, YELLOW, RED, RED, YELLOW, BLUE, BLUE};
    private static final int[] THRESHOLDS = new int[]{1000, 2500, 5000, 6500, 6500, 5000, 2500, 1000};
    private final JLabel rpmLabel;
    private int rpm;

    public RPMPanel() {
        rpmLabel = new JLabel();
        rpmLabel.setForeground(Color.RED);
        rpmLabel.setVerticalAlignment(JLabel.CENTER);
        rpmLabel.setHorizontalAlignment(JLabel.CENTER);
        rpmLabel.setFont(new Font("Dialog", Font.BOLD, 40));
        rpmLabel.setBounds(0, 28, WIDTH, 40);
        add(rpmLabel, 0);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        for (int i = 0; i < 8; i++) {
            int minimumRPM = THRESHOLDS[i];
            g2.setColor(rpm > minimumRPM ? COLORS[i] : Color.GRAY);
            int width = WIDTH / 8;
            g2.fillRect(width * i, 0, WIDTH / 8, 20);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(8));
            g2.drawRect(width * i + 4, 4, WIDTH / 8 - 8, 16);
        }
    }

    public void setRPM(int rpm) {
        this.rpm = rpm;
        rpmLabel.setText(rpm + "");
        repaint();
    }
}
