package mohammad.adib.racecar.ui;

import mohammad.adib.racecar.model.Calibration;
import mohammad.adib.racecar.model.GearInfo;
import mohammad.adib.racecar.model.LapTimeData;
import mohammad.adib.racecar.monitor.GearDataMonitor;
import mohammad.adib.racecar.util.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static mohammad.adib.racecar.util.Utils.getFormattedLapTime;

public class MainPanel extends JLayeredPane {

    private static final String NEUTRAL = "N";
    private static final int TOP_MARGIN = 16, BOTTOM_MARGIN = 16;
    private final GearDataMonitor dataMonitor = GearDataMonitor.getInstance();
    private GearCalibrationPanel calibrationPanel;
    private JLabel gearLabel, lapTime, pastLapTimes;
    private Calibration calibration;
    private LapTimeData lapTimeData;
    private long lapStart;
    private String currentGear = NEUTRAL;
    private boolean initialized = false, timing = false;

    private MouseAdapter lapTimeMouseAdapter = new MouseAdapter() {
        long pressedTime;

        @Override
        public void mousePressed(MouseEvent e) {
            pressedTime = System.currentTimeMillis();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (System.currentTimeMillis() - pressedTime < 1000) {
                if (!timing) {
                    startLapTimer();
                } else {
                    lapTimeData.addLapTime(getElapsedLapTime());
                    lapStart = System.currentTimeMillis();
                    Utils.saveLapTimes(lapTimeData);
                }
            } else {
                // Clear lap times
                lapTimeData = new LapTimeData();
                Utils.saveLapTimes(lapTimeData);
                timing = false;
            }
            updatePastLapTimes();
        }
    };

    private void init() {
        if (!Utils.isCalibrated()) {
            startCalibration();
        } else {
            loadData();
            setupGear();
            setupLapTimes();
            listenForData();
            initialized = true;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!initialized) init();
    }

    private void loadData() {
        try {
            if (!Utils.hasLapTimes()) {
                lapTimeData = new LapTimeData();
                Utils.saveLapTimes(lapTimeData);
            } else {
                lapTimeData = Utils.getLapTimes();
            }
            calibration = Utils.getCalibration();
        } catch (IOException e) {
            e.printStackTrace();
            startCalibration();
        }
    }

    private void startCalibration() {
        removeAll();
        calibrationPanel = new GearCalibrationPanel(() -> {
            remove(calibrationPanel);
            init();
        });
        add(calibrationPanel, 2);
    }

    private void listenForData() {
        GearDataMonitor.getInstance().start();
        dataMonitor.addListener((x, y) -> {
            boolean inGear = false;
            for (GearInfo gear : calibration.gears) {
                if (Utils.isGearSelected(gear, x, y, calibration.margin)) {
                    setGear(gear.name);
                    inGear = true;
                    break;
                }
            }
            if (!inGear) setGear(NEUTRAL);
        });
    }

    private void setGear(String gear) {
        if (!gear.equals(currentGear)) {
            gearLabel.setText(gear);
            currentGear = gear;
        }
    }

    private void setupGear() {
        gearLabel = new JLabel();
        gearLabel.setForeground(Color.RED);
        gearLabel.setBounds(10, TOP_MARGIN, getWidth() / 2, getHeight() - TOP_MARGIN - 80);
        gearLabel.setText(NEUTRAL);
        gearLabel.setVerticalAlignment(JLabel.CENTER);
        gearLabel.setHorizontalAlignment(JLabel.CENTER);
        gearLabel.setFont(new Font("Dialog", Font.BOLD, 240));
        gearLabel.addMouseListener(new MouseAdapter() {
            long pressedTime;

            @Override
            public void mousePressed(MouseEvent e) {
                pressedTime = System.currentTimeMillis();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (System.currentTimeMillis() - pressedTime >= 1000) {
                    startCalibration();
                }
            }
        });
        add(gearLabel, 1);
    }

    private void setupLapTimes() {
        lapTime = new JLabel();
        lapTime.setBounds(10, getHeight() - BOTTOM_MARGIN - 50, getWidth() / 2, 40);
        lapTime.setForeground(Color.WHITE);
        lapTime.setVerticalAlignment(JLabel.CENTER);
        lapTime.setHorizontalAlignment(JLabel.CENTER);
        lapTime.setFont(new Font("Dialog", Font.BOLD, 40));
        lapTime.setText("00:00.000");
        lapTime.addMouseListener(lapTimeMouseAdapter);
        add(lapTime, 1);

        pastLapTimes = new JLabel();
        pastLapTimes.setBounds(getWidth() / 2, TOP_MARGIN, getWidth() / 2, getHeight() - TOP_MARGIN);
        pastLapTimes.setForeground(Color.WHITE);
        pastLapTimes.setVerticalAlignment(JLabel.CENTER);
        pastLapTimes.setHorizontalAlignment(JLabel.CENTER);
        pastLapTimes.setFont(new Font("Dialog", Font.PLAIN, 30));
        updatePastLapTimes();
        pastLapTimes.addMouseListener(lapTimeMouseAdapter);
        add(pastLapTimes, 1);
    }

    private void startLapTimer() {
        timing = true;
        lapStart = System.currentTimeMillis();
        new Thread(() -> {
            while (timing) {
                lapTime.setText(getFormattedLapTime(getElapsedLapTime()));
                Utils.sleep(30);
            }
            lapTime.setText("00:00.000");
        }).start();
    }

    private void updatePastLapTimes() {
        pastLapTimes.setText(lapTimeData.isEmpty() ? "No lap times" : lapTimeData.toFormattedString(7));
    }

    private long getElapsedLapTime() {
        return System.currentTimeMillis() - lapStart;
    }
}
