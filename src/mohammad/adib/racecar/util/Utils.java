package mohammad.adib.racecar.util;

import mohammad.adib.racecar.Main;
import mohammad.adib.racecar.model.Calibration;
import mohammad.adib.racecar.model.GearInfo;
import mohammad.adib.racecar.model.LapTimeData;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Utils {

    public static final int WIDTH = 480, HEIGHT = 320;
    private static final String CALIBRATION_FILE_NAME = "calibration.json";
    private static final String LAP_TIMES_FILE_NAME = "laptimes.txt";

    public static boolean isInDevMode() {
        return !System.getProperty("user.home").contains("/pi");
    }

    private static String getPathToCalibration() {
        return System.getProperty("user.home") + File.separator + CALIBRATION_FILE_NAME;
    }

    private static String getPathToLapTimes() {
        return System.getProperty("user.home") + File.separator + LAP_TIMES_FILE_NAME;
    }

    public static boolean isCalibrated() {
        File file = new File(getPathToCalibration());
        return file.exists();
    }

    public static boolean hasLapTimes() {
        File file = new File(getPathToLapTimes());
        return file.exists();
    }

    public static void saveCalibration(Calibration calibration) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(getPathToCalibration()));
        out.write(calibration.toString());
        out.close();
        Main.printToConsole("Calibration saved successfully");
    }

    public static void saveLapTimes(LapTimeData lapTimeData) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(getPathToLapTimes()));
            out.write(lapTimeData.toString());
            out.close();
            Main.printToConsole("Lap times saved successfully");
        } catch (Exception e) {
            Main.printToConsole("Failed to save lap times");
            e.printStackTrace();
        }
    }

    public static Calibration getCalibration() throws IOException {
        String data = readFile(getPathToCalibration(), StandardCharsets.UTF_8);
        return new Calibration(data);
    }

    public static LapTimeData getLapTimes() throws IOException {
        String data = readFile(getPathToLapTimes(), StandardCharsets.UTF_8);
        return new LapTimeData(data);
    }

    private static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static boolean isGearSelected(GearInfo gearInfo, int x, int y, int margin) {
        return Math.abs(gearInfo.x - x) < margin && Math.abs(gearInfo.y - y) < margin;
    }

    public static String getFormattedLapTime(long time) {
        return String.format("%02d:%02d.%03d",
                TimeUnit.MILLISECONDS.toMinutes(time),
                TimeUnit.MILLISECONDS.toSeconds(time) % TimeUnit.MINUTES.toSeconds(1),
                time % 1000);
    }
}
