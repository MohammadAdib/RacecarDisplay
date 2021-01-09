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
        calibrationPanel = new GearCalibrationPanel(() -> {
            removeAll();
            init();
        });
        add(calibrationPanel, 1);
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
        gearLabel.setBounds(10, 0, getWidth() / 2, getHeight());
        gearLabel.setText(NEUTRAL);
        gearLabel.setVerticalAlignment(JLabel.CENTER);
        gearLabel.setHorizontalAlignment(JLabel.CENTER);
        gearLabel.setFont(new Font("Dialog", Font.BOLD, 270));
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
        lapTime.setBounds(getWidth() / 2 + 20, TOP_MARGIN + 10, getWidth() / 2, 40);
        lapTime.setForeground(Color.WHITE);
        lapTime.setVerticalAlignment(JLabel.CENTER);
        lapTime.setHorizontalAlignment(JLabel.LEFT);
        lapTime.setFont(new Font("Dialog", Font.BOLD, 36));
        lapTime.setText("00:00.000");
        lapTime.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                timing = false;
            }
        });
        add(lapTime, 1);

        pastLapTimes = new JLabel();
        pastLapTimes.setBounds(getWidth() / 2 + 20, TOP_MARGIN + 60, getWidth() / 2, getHeight() - TOP_MARGIN);
        pastLapTimes.setForeground(Color.WHITE);
        pastLapTimes.setVerticalAlignment(JLabel.TOP);
        pastLapTimes.setHorizontalAlignment(JLabel.LEFT);
        pastLapTimes.setFont(new Font("Dialog", Font.PLAIN, 30));
        updatePastLapTimes();
        pastLapTimes.addMouseListener(new MouseAdapter() {
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
        });
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
        pastLapTimes.setText(lapTimeData.toFormattedString(6));
    }

    private long getElapsedLapTime() {
        return System.currentTimeMillis() - lapStart;
    }
}
