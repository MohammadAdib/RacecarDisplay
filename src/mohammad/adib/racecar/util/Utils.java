package mohammad.adib.racecar.util;

import mohammad.adib.racecar.model.Calibration;
import mohammad.adib.racecar.model.GearInfo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class Utils {

    public static final int WIDTH = 480, HEIGHT = 320;
    private static final String CALIBRATION_FILE_NAME = "calibration.json";

    private static String getComputerName() {
        Map<String, String> env = System.getenv();
        if (env.containsKey("COMPUTERNAME"))
            return env.get("COMPUTERNAME");
        else return env.getOrDefault("HOSTNAME", "Unknown Computer");
    }

    public static boolean isInDevMode() {
        return getComputerName().equals("DESKTOP-WA-WORK");
    }

    private static String getPathToCalibration() {
        if (isInDevMode()) {
            return System.getProperty("user.home") + File.separator + "Desktop" + File.separator + CALIBRATION_FILE_NAME;
        } else {
            return "/home/pi/" + CALIBRATION_FILE_NAME;
        }
    }

    public static boolean isCalibrated() {
        File file = new File(getPathToCalibration());
        return file.exists();
    }

    public static void saveCalibration(Calibration calibration) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(getPathToCalibration()));
        out.write(calibration.toString());
        out.close();
        System.out.println("Calibration saved successfully");
    }

    public static Calibration getCalibration() throws IOException {
        String data = readFile(getPathToCalibration(), StandardCharsets.UTF_8);
        return new Calibration(data);
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
}
