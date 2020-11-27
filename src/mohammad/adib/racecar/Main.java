package mohammad.adib.racecar;

import jssc.SerialPort;
import jssc.SerialPortException;
import mohammad.adib.racecar.ui.GUI;
import mohammad.adib.racecar.util.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Main {

    public static GUI gui;
    static SerialPort serialPort;

    public static void main(String[] args) {
        //initUI();
        //DataMonitor.getInstance().start();
        startRfComm();
    }

    private static void printRPM(String s) {
        s = s.substring(s.length() - 5).replaceAll(" ", "");
        int x = Integer.decode("0x" + s.substring(0, 2));
        int y = Integer.decode("0x" + s.substring(2, 4));
        int rpm = (256 * x + y) / 4;
        System.out.println("RPM: " + rpm);
    }

    private static void startRfComm() {
        try {
            serialPort = new SerialPort("/dev/rfcomm0");
            serialPort.openPort();
            Utils.sleep(1000);
            sendCommand("ATZ");
            sendCommand("ATL1");
            sendCommand("ATH0");
            sendCommand("ATE0");
            sendCommand("ATS1");
            sendCommand("ATSP0");
            //sendCommand("0100");
            while (true) {
                String s = sendCommand("010C");
                //printRPM(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String sendCommand(String command) {
        System.out.println(command + " -------------");
        command = command + "\r";
        try {
            serialPort.writeBytes(command.getBytes());
            Utils.sleep(250);
            byte b;
            StringBuilder builder = new StringBuilder();
            while ((b = serialPort.readBytes(1)[0]) > -1) {
                if (b == '>') {
                    break;
                }
                builder.append((char) b);
            }
            String s = builder.toString().trim();
            s = s.replaceAll("SEARCHING...", "");
            System.out.println(s);
            return s;
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static void initUI() {
        gui = new GUI();
        JFrame frame = new JFrame("Racecar Display");
        if (!Utils.isInDevMode()) {
            frame.setCursor(frame.getToolkit().createCustomCursor(
                    new BufferedImage(5, 5, BufferedImage.TYPE_INT_ARGB),
                    new Point(0, 0), "invisible"));
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setAlwaysOnTop(true);
        } else {
            frame.setPreferredSize(new Dimension(480, 320));
        }
        frame.setUndecorated(true);
        frame.setContentPane(gui.mainPanel);
        frame.pack();
        frame.dispose();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
