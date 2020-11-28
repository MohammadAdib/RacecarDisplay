package mohammad.adib.racecar.monitor;

import jssc.SerialPort;
import jssc.SerialPortException;
import mohammad.adib.racecar.util.DataListener;
import mohammad.adib.racecar.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class GearDataMonitor {

    protected ScheduledThreadPoolExecutor executor;
    private static GearDataMonitor instance;
    private SerialPort serialPort;
    private final List<DataListener> listeners;
    private int x, y;

    private GearDataMonitor() {
        executor = new ScheduledThreadPoolExecutor(1);
        listeners = new ArrayList<>();
    }

    public static GearDataMonitor getInstance() {
        if (instance == null) {
            instance = new GearDataMonitor();
        }
        return instance;
    }

    public void start() {
        executor.execute(() -> {
            try {
                serialPort = new SerialPort(Utils.isInDevMode() ? "COM6" : "/dev/ttyUSB0");
                serialPort.openPort();
                serialPort.setParams(9600, 8, 1, 0);
                while (serialPort.isOpened()) {
                    processMessage(serialPort.readString(128));
                }
                System.out.println("Started monitoring serial: " + serialPort.getPortName());
            } catch (SerialPortException e) {
                System.err.println("Failed to monitor serial data");
            }
        });
    }

    public void stop() {
        try {
            serialPort.closePort();
        } catch (SerialPortException ignored) {
        }
        executor.shutdown();
    }

    private void processMessage(String message) {
        message = message.substring(message.indexOf("\n") + 1, message.lastIndexOf("\n"));
        try {
            String[] lines = message.split("\n");
            // average the results
            int xSum = 0;
            int ySum = 0;
            for (String line : lines) {
                String[] parts = line.trim().split(",");
                xSum += Integer.parseInt(parts[0]);
                ySum += Integer.parseInt(parts[1]);
            }
            x = xSum / lines.length;
            y = ySum / lines.length;
            // notify listeners
            for (DataListener listener : listeners) listener.onDataChanged(x, y);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void addListener(DataListener listener) {
        listeners.add(listener);
    }

    public void removeListener(DataListener listener) {
        listeners.remove(listener);
    }

    public boolean isActive() {
        return serialPort.isOpened();
    }
}
