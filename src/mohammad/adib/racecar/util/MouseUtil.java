package mohammad.adib.racecar.util;

import mohammad.adib.racecar.Main;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MouseUtil implements Runnable, MouseListener {

    private boolean holding;
    private int seconds;
    private Thread thread;

    public void mousePressed(MouseEvent e) {
        holding = true;
        thread = new Thread(this);
        thread.start();
    }

    public void mouseReleased(MouseEvent e) {
        holding = false;
        Main.printToConsole("Held for: " + seconds);
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    public void mouseClicked(MouseEvent e) {
    }

    public void run() {
        try {
            while (holding) {
                seconds++;
                // put some code here
                if (seconds == 3) {
                    holding = false;
                    Main.printToConsole("Held for maximum time!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}