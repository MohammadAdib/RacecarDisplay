package mohammad.adib.racecar.monitor;

import mohammad.adib.racecar.util.Utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static mohammad.adib.racecar.Main.printToConsole;

public class OBDPythonMonitor {

    private static OBDPythonMonitor instance;
    private final List<OBDListener> listeners;
    private boolean active;

    private OBDPythonMonitor() {
        listeners = new ArrayList<>();
    }

    public static OBDPythonMonitor getInstance() {
        if (instance == null) {
            instance = new OBDPythonMonitor();
        }
        return instance;
    }

    public void start() {
        try {
            Runtime rt = Runtime.getRuntime();
            Process p = rt.exec("python /home/pi/obd_monitor.py");
            printToConsole("Starting python obd monitor");

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(p.getErrorStream()));

            new Thread(() -> {
                try {
                    String s;
                    while ((s = stdInput.readLine()) != null) {
                        printToConsole(s);
                        try {
                            String[] parts = s.split(",");
                            int rpm = (int) Double.parseDouble(parts[0]);
                            int load = (int) Double.parseDouble(parts[1]);
                            int throttle = (int) Double.parseDouble(parts[2]);
                            int intakeTemp = (int) Double.parseDouble(parts[3]);
                            int coolantTemp = (int) Double.parseDouble(parts[4]);
                            for (OBDListener listener : listeners) {
                                if (!active) listener.onActive();
                                listener.onUpdate(rpm, load, throttle, intakeTemp, coolantTemp);
                            }
                            active = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

            new Thread(() -> {
                try {
                    if (stdError.readLine() != null) {
                        printToConsole("Error, restarting");
                        Utils.sleep(1000);
                        start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addListener(OBDListener listener) {
        listeners.add(listener);
    }

    public void removeListener(OBDListener listener) {
        listeners.remove(listener);
    }

    public interface OBDListener {

        void onUpdate(int rpm, int load, int throttle, int intakeTemp, int coolantTemp);

        void onActive();
    }
}
