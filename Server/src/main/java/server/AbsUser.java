package server;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class AbsUser {

    private final JSONObject jsonObject;
    private final String data;

    public AbsUser(JSONObject jsonObject, String data) {
        this.jsonObject = jsonObject;
        this.data = data;
    }

    String getStringfromJson(String string) throws JSONException {
        return jsonObject.getJSONObject(data).getString(string);
    }
}
