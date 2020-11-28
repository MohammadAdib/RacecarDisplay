package mohammad.adib.racecar.monitor;

import jssc.SerialPort;
import jssc.SerialPortException;
import mohammad.adib.racecar.util.OBDUtils;
import mohammad.adib.racecar.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class OBDMonitor {

    private static final String RPM = "010C";
    private static final String LOAD = "0104";
    private static final String THROTTLE = "0111";
    private static final String INTAKE_TEMP = "010F";
    private static final String COOLANT_TEMP = "0105";
    protected ScheduledThreadPoolExecutor executor;
    private static OBDMonitor instance;
    private SerialPort serialPort;
    private final List<OBDListener> listeners;
    private boolean active;

    public static OBDMonitor getInstance() {
        if (instance == null) {
            instance = new OBDMonitor();
        }
        return instance;
    }

    private OBDMonitor() {
        listeners = new ArrayList<>();
    }

    public void start() {
        serialPort = new SerialPort("/dev/rfcomm0");
        try {
            serialPort.openPort();
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
        Utils.sleep(1000);
        sendCommand("ATZ", true, true);
        sendCommand("ATL1", true, true);
        sendCommand("ATH0", true, true);
        sendCommand("ATE0", true, true);
        sendCommand("ATS1", true, true);
        sendCommand("ATSP0", true, true);
        executor = new ScheduledThreadPoolExecutor(1);
        executor.execute(() -> {
            try {
                while (serialPort.isOpened()) {
                    int rpm = OBDUtils.getRPM(sendCommand(RPM));
                    int load = OBDUtils.getLoad(sendCommand(LOAD));
                    int throttle = OBDUtils.getThrottle(sendCommand(THROTTLE));
                    int intakeTemp = OBDUtils.getIntakeTemp(sendCommand(INTAKE_TEMP));
                    int coolantTemp = OBDUtils.getCoolantTemp(sendCommand(COOLANT_TEMP));
                    if (!active) {
                        for (OBDListener listener : listeners) listener.onActive();
                        active = true;
                    }
                    for (OBDListener listener : listeners) listener.onUpdate(rpm, load, throttle, intakeTemp, coolantTemp);
                }
            } catch (Exception e) {
                e.printStackTrace();
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

    private String sendCommand(String command) {
        return sendCommand(command, false, false);
    }

    private String sendCommand(String command, boolean sleep, boolean log) {
        if (log) System.out.println(command + " --------------------");
        command = command + "\r";
        try {
            serialPort.writeBytes(command.getBytes());
            if (sleep) Utils.sleep(250);
            byte b;
            StringBuilder builder = new StringBuilder();
            while ((b = serialPort.readBytes(1)[0]) > -1) {
                if (b == '>') {
                    break;
                }
                builder.append((char) b);
            }
            String s = builder.toString().trim();
            s = s.replaceAll("SEARCHING...", "").replaceAll(" ", "");
            if (log) System.out.println(s);
            return s;
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void addListener(OBDListener listener) {
        listeners.add(listener);
    }

    public void removeListener(OBDListener listener) {
        listeners.remove(listener);
    }

    public interface OBDListener {

        public void onUpdate(int rpm, int load, int throttle, int intakeTemp, int coolantTemp);

        public void onActive();
    }
}
