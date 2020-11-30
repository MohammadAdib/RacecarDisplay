package mohammad.adib.racecar.model;

import com.github.jsonj.JsonArray;
import com.github.jsonj.JsonElement;
import com.github.jsonj.JsonObject;
import com.github.jsonj.tools.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class Calibration {

    public List<GearInfo> gears;
    public int margin;

    public Calibration(int margin) {
        this.margin = margin;
        gears = new ArrayList<>();
    }

    public Calibration(String data) {
        gears = new ArrayList<>();
        JsonParser parser = new JsonParser();
        JsonObject object = parser.parse(data).asObject();
        margin = object.getInt("margin");
        JsonArray gearInfoArray = object.getArray("gears");
        for (JsonElement element : gearInfoArray) {
            JsonObject gearInfoObject = element.asObject();
            gears.add(new GearInfo(
                    gearInfoObject.getString("name"),
                    gearInfoObject.getInt("x"),
                    gearInfoObject.getInt("y")
            ));
        }
    }

    @Override
    public String toString() {
        return asJsonObject().toString();
    }

    public JsonObject asJsonObject() {
        JsonObject object = new JsonObject();
        object.put("margin", margin);
        JsonArray gearInfoArray = new JsonArray();
        for (GearInfo gearInfo : gears) {
            gearInfoArray.add(gearInfo.asJsonObject());
        }
        object.put("gears", gearInfoArray);
        return object;
    }
}
