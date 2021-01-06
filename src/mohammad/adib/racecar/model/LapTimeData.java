package mohammad.adib.racecar.model;

import java.util.ArrayList;

import static mohammad.adib.racecar.util.Utils.getFormattedLapTime;

public class LapTimeData {

    private ArrayList<Long> lapTimes;

    public LapTimeData() {
        lapTimes = new ArrayList<>();
    }

    public LapTimeData(String data) {
        lapTimes = new ArrayList<>();
        if (data.contains("\n")) {
            String[] items = data.split("\n");
            for (String item : items) {
                lapTimes.add(Long.parseLong(item));
            }
        }
    }

    public void addLapTime(long time) {
        if (lapTimes.size() > 100) {
            lapTimes.remove(0);
        }
        lapTimes.add(time);
    }

    public boolean isEmpty() {
        return lapTimes.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder data = new StringBuilder();
        for (long time : lapTimes) {
            data.append(time).append("\n");
        }
        return data.toString().trim();
    }

    public String toFormattedString(int lastN) {
        StringBuilder data = new StringBuilder();
        if (lastN > lapTimes.size()) {
            lastN = lapTimes.size();
        }
        data.append("<html>");
        for (int i = lapTimes.size() - lastN; i < lapTimes.size(); i++) {
            data.append(getFormattedLapTime(lapTimes.get(i)));
            if (i > 0) {
                long delta = lapTimes.get(i) - lapTimes.get(i - 1);
                if (delta > 0) {
                    data.append(" <font color=\"red\">▼");
                } else {
                    data.append(" <font color=\"green\">▲");
                }
                data.append("</font>");
            }
            data.append("<br>");
        }
        data.append("</html>");
        return data.toString().trim();
    }
}
