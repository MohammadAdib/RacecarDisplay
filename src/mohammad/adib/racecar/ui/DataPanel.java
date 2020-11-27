package mohammad.adib.racecar.ui;

import mohammad.adib.racecar.model.Calibration;
import mohammad.adib.racecar.model.GearInfo;
import mohammad.adib.racecar.monitor.DataMonitor;
import mohammad.adib.racecar.util.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class DataPanel extends JLayeredPane {

    private static final String NEUTRAL = "N";
    private CalibrationPanel calibrationPanel;
    private JLabel gearLabel, metricsLabel, debugLabel;
    private final DataMonitor dataMonitor = DataMonitor.getInstance();
    private Calibration calibration;
    private String currentGear = NEUTRAL;
    private long shiftStart = 0;

    public DataPanel() {
        if (!Utils.isCalibrated()) {
            startCalibration();
        } else {
            init();
        }
    }

    private void init() {
        loadCalibration();
        setupGearDisplay();
        setupMetricsDisplay();
        setupDebugValues();
        listenForData();
    }

    private void loadCalibration() {
        try {
            calibration = Utils.getCalibration();
        } catch (IOException e) {
            e.printStackTrace();
            startCalibration();
        }
    }

    private void startCalibration() {
        removeAll();
        calibrationPanel = new CalibrationPanel(() -> {
            remove(calibrationPanel);
            init();
        });
        add(calibrationPanel, 2);
    }

    private void listenForData() {
        dataMonitor.addListener((x, y) -> {
            debugLabel.setText(x + ", " + y);
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
            if (gear.equals(NEUTRAL)) {
                shiftStart = System.currentTimeMillis();
                metricsLabel.setVisible(false);
            } else {
                calculateDelta();
            }
            currentGear = gear;
        }
    }

    private void calculateDelta() {
        long delta = System.currentTimeMillis() - shiftStart;
        if (delta < 1000) {
            metricsLabel.setText("Δ " + delta + "ms");
            metricsLabel.setVisible(true);
        }
    }

    private void setupGearDisplay() {
        gearLabel = new JLabel();
        gearLabel.setForeground(Color.RED);
        gearLabel.setBounds(0, 6, Utils.WIDTH, Utils.HEIGHT);
        gearLabel.setText(NEUTRAL);
        gearLabel.setVerticalAlignment(JLabel.CENTER);
        gearLabel.setHorizontalAlignment(JLabel.CENTER);
        gearLabel.setFont(new Font("Dialog", Font.BOLD, 300));
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
        add(gearLabel, 0);
    }

    private void setupMetricsDisplay() {
        metricsLabel = new JLabel();
        metricsLabel.setBounds(20, 150, 100, 50);
        metricsLabel.setForeground(Color.WHITE);
        metricsLabel.setVerticalAlignment(JLabel.CENTER);
        metricsLabel.setHorizontalAlignment(JLabel.CENTER);
        metricsLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        add(metricsLabel, 4);
    }

    private void setupDebugValues() {
        debugLabel = new JLabel();
        if (Utils.isInDevMode()) {
            debugLabel.setBounds(12, 8, 100, 20);
            debugLabel.setForeground(Color.DARK_GRAY);
            add(debugLabel, 1);
        }
    }
}
