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

    private CalibrationPanel calibrationPanel;
    private JLabel gearLabel, debugLabel;
    private final DataMonitor dataMonitor = DataMonitor.getInstance();
    private Calibration calibration;

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
                    gearLabel.setText(gear.name);
                    inGear = true;
                    break;
                }
            }
            if (!inGear) gearLabel.setText("N");
        });
    }

    private void setupGearDisplay() {
        gearLabel = new JLabel();
        gearLabel.setForeground(Color.RED);
        gearLabel.setText("N");
        gearLabel.setBounds(0, -25, Utils.WIDTH, Utils.HEIGHT);
        gearLabel.setVerticalAlignment(JLabel.CENTER);
        gearLabel.setHorizontalAlignment(JLabel.CENTER);
        gearLabel.setFont(new Font(gearLabel.getFont().getName(), Font.BOLD, 300));
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

    private void setupDebugValues() {
        debugLabel = new JLabel();
        debugLabel.setBounds(8, 4, 100, 20);
        debugLabel.setForeground(Color.DARK_GRAY);
        add(debugLabel, 1);
    }
}
