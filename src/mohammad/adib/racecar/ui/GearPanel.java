package mohammad.adib.racecar.ui;

import mohammad.adib.racecar.model.Calibration;
import mohammad.adib.racecar.model.GearInfo;
import mohammad.adib.racecar.monitor.GearDataMonitor;
import mohammad.adib.racecar.monitor.OBDMonitor;
import mohammad.adib.racecar.util.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class GearPanel extends JLayeredPane {

    private static final String NEUTRAL = "N";
    private final GearDataMonitor dataMonitor = GearDataMonitor.getInstance();
    private final OBDMonitor obdMonitor = OBDMonitor.getInstance();
    private GearCalibrationPanel calibrationPanel;
    private BottomPanel bottomPanel;
    private RPMPanel rpmPanel;
    private JProgressBar loadBar, throttleBar;
    private JLabel gearLabel, metricsLabel;
    private Calibration calibration;
    private String currentGear = NEUTRAL;
    private long shiftStart = 0;

    public GearPanel() {
        if (!Utils.isCalibrated()) {
            startCalibration();
        } else {
            init();
        }
    }

    private void init() {
        loadCalibration();
        setupGear();
        setupDelta();
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
        calibrationPanel = new GearCalibrationPanel(() -> {
            remove(calibrationPanel);
            init();
        });
        add(calibrationPanel, 2);
    }

    private void listenForData() {
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
        obdMonitor.addListener(new OBDMonitor.OBDListener() {
            @Override
            public void onUpdate(int rpm, int load, int throttle, int intakeTemp, int coolantTemp) {
                rpmPanel.setRPM(rpm);
                loadBar.setValue(load);
                throttleBar.setValue(throttle);
                bottomPanel.setTemps(coolantTemp, intakeTemp);
            }

            @Override
            public void onActive() {
                displayOBDData();
            }
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

    private void setupGear() {
        gearLabel = new JLabel();
        gearLabel.setForeground(Color.RED);
        gearLabel.setBounds(0, 6, Utils.WIDTH, Utils.HEIGHT);
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

    private void setupDelta() {
        metricsLabel = new JLabel();
        metricsLabel.setBounds(0, 260, Utils.WIDTH, 50);
        metricsLabel.setForeground(Color.WHITE);
        metricsLabel.setVerticalAlignment(JLabel.CENTER);
        metricsLabel.setHorizontalAlignment(JLabel.CENTER);
        metricsLabel.setFont(new Font("Dialog", Font.BOLD, 15));
        add(metricsLabel, 0);
    }

    private void setupBottom() {
        bottomPanel = new BottomPanel();
        bottomPanel.setBounds(0, Utils.HEIGHT - 110, Utils.WIDTH, 110);
        add(bottomPanel, 2);
    }

    private void setupLoad() {
        loadBar = new JProgressBar(JProgressBar.VERTICAL, 0, 100);
        loadBar.setValue(40);
        loadBar.setBackground(Color.DARK_GRAY);
        loadBar.setForeground(Color.RED);
        loadBar.setBorderPainted(false);
        loadBar.setBounds(0, 0, 24, Utils.HEIGHT);
        add(loadBar, 0);

        VerticalLabel loadLabel = new VerticalLabel("LOAD");
        loadLabel.setDirection(VerticalLabel.Direction.VERTICAL_DOWN);
        loadLabel.setForeground(Color.RED);
        loadLabel.setFont(new Font("Dialog", Font.PLAIN, 18));
        loadLabel.setBounds(0, 30, 100, 100);
        add(loadLabel, 0);
    }

    private void setupThrottle() {
        Color color = Color.decode("#6ab04c");
        throttleBar = new JProgressBar(JProgressBar.VERTICAL, 0, 100);
        throttleBar.setValue(60);
        throttleBar.setBackground(Color.DARK_GRAY);
        throttleBar.setForeground(color);
        throttleBar.setBorderPainted(false);
        throttleBar.setBounds(Utils.WIDTH - 24, 0, 24, Utils.HEIGHT);
        add(throttleBar, 0);

        VerticalLabel throttleLabel = new VerticalLabel("THROTTLE");
        throttleLabel.setDirection(VerticalLabel.Direction.VERTICAL_DOWN);
        throttleLabel.setForeground(color);
        throttleLabel.setFont(new Font("Dialog", Font.PLAIN, 18));
        throttleLabel.setBounds(Utils.WIDTH - 96, 30, 100, 100);
        add(throttleLabel, 0);
    }

    private void setupRPM() {
        rpmPanel = new RPMPanel();
        rpmPanel.setBounds(24, 0, Utils.WIDTH - 48, 80);
        add(rpmPanel, 0);
    }

    private void displayOBDData() {
        setupBottom();
        setupLoad();
        setupThrottle();
        setupRPM();
    }
}
