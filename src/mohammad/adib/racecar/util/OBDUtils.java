package mohammad.adib.racecar.util;

public class OBDUtils {

    public static int getRPM(String rpm) {
        rpm = rpm.substring(rpm.length() - 4);
        int x = Integer.decode("0x" + rpm.substring(0, 2));
        int y = Integer.decode("0x" + rpm.substring(2, 4));
        return (256 * x + y) / 4;
    }

    public static int getLoad(String load) {
        return (int) ((Integer.decode("0x" + load.substring(load.length() - 2)) / 2.55) + 0.5);
    }

    public static int getIntakeTemp(String intakeTemp) {
        return getTemp(intakeTemp);
    }

    public static int getCoolantTemp(String coolantTemp) {
        return getTemp(coolantTemp);
    }

    private static int getTemp(String temp) {
        return Integer.decode("0x" + temp.substring(temp.length() - 2)) - 40;
    }
}
