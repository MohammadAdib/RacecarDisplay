package mohammad.adib.racecar.ui;

import mohammad.adib.racecar.model.Calibration;
import mohammad.adib.racecar.model.GearInfo;
import mohammad.adib.racecar.monitor.DataMonitor;
import mohammad.adib.racecar.util.*;

import javax.swing.*;
import java.awt.*;

import static mohammad.adib.racecar.util.Utils.sleep;

public class CalibrationPanel extends JLayeredPane implements DataListener {

    private final String[] GEARS = new String[]{"R", "1", "2", "3", "4", "5"};
    private final CalibrationListener listener;
    private JLabel gearLabel, debugLabel;
    private final DataMonitor dataMonitor = DataMonitor.getInstance();
    private final Calibration calibration = new Calibration(30);

    public CalibrationPanel(CalibrationListener listener) {
        this.listener = listener;
        setBackground(Color.WHITE);
        setBounds(0, 0, Utils.WIDTH, Utils.HEIGHT);
        setOpaque(true);
        dataMonitor.addListener(this);
        setupGearDisplay();
        setupDebugValues();
        startCalibration();
    }

    private void setupGearDisplay() {
        JLabel selectLabel = new JLabel();
        selectLabel.setForeground(Color.RED);
        selectLabel.setText("SELECT AND HOLD");
        selectLabel.setVerticalAlignment(JLabel.CENTER);
        selectLabel.setHorizontalAlignment(JLabel.CENTER);
        selectLabel.setBounds(0, 0, Utils.WIDTH, 72);
        add(selectLabel, 1);


        gearLabel = new JLabel();
        gearLabel.setForeground(Color.RED);
        gearLabel.setBounds(0, 40, Utils.WIDTH, Utils.HEIGHT - 100);
        gearLabel.setVerticalAlignment(JLabel.CENTER);
        gearLabel.setHorizontalAlignment(JLabel.CENTER);
        gearLabel.setFont(new Font(gearLabel.getFont().getName(), Font.BOLD, 200));
        add(gearLabel, 0);
    }

    private void setupDebugValues() {
        // Debug values
        debugLabel = new JLabel();
        debugLabel.setBounds(8, 4, 100, 20);
        debugLabel.setForeground(Color.LIGHT_GRAY);
        add(debugLabel, 2);
    }

    private void startCalibration() {
        new Thread(() -> {
            sleep(1000);
            System.out.println("Starting calibration");
            for (String gear : GEARS) {
                gearLabel.setText(gear);
                sleep(3000);
                recordGearData(gear);
            }
            saveCalibration();
        }).start();
    }

    private void saveCalibration() {
        try {
            Utils.saveCalibration(calibration);
            listener.onComplete();
            dataMonitor.removeListener(this);
        } catch (Exception e) {
            // retry
            e.printStackTrace();
            startCalibration();
        }
    }

    private void recordGearData(String name) {
        calibration.gears.add(new GearInfo(name, dataMonitor.getX(), dataMonitor.getY()));
    }

    @Override
    public void onDataChanged(int x, int y) {
        debugLabel.setText(x + ", " + y);
    }
}
