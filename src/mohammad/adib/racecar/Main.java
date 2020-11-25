package mohammad.adib.racecar;

import mohammad.adib.racecar.monitor.DataMonitor;
import mohammad.adib.racecar.ui.GUI;
import mohammad.adib.racecar.util.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Main {

    public static GUI gui;

    public static void main(String[] args) {
        initUI();
        DataMonitor.getInstance().start();
    }

    private static void initUI() {
        gui = new GUI();
        JFrame frame = new JFrame("Racecar Display");
        if (!Utils.isInDevMode()) {
            frame.setCursor(frame.getToolkit().createCustomCursor(
                    new BufferedImage(5, 5, BufferedImage.TYPE_INT_ARGB),
                    new Point(0, 0), "invisible"));
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setUndecorated(true);
            frame.setAlwaysOnTop(true);
        } else {
            frame.setPreferredSize(new Dimension(480,320));
        }
        frame.setContentPane(gui.mainPanel);
        frame.pack();
        frame.dispose();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
