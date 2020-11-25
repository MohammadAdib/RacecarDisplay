package mohammad.adib.racecar.model;

import com.github.jsonj.JsonObject;

public class GearInfo {

    public String name;
    public int x, y;

    public GearInfo(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public JsonObject asJsonObject() {
        JsonObject object = new JsonObject();
        object.put("name", name);
        object.put("x", x);
        object.put("y", y);
        return object;
    }
}