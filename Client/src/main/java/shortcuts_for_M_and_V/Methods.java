package shortcuts_for_M_and_V;

import lombok.NonNull;
import org.json.JSONException;
import org.json.JSONObject;

public class Methods {
    public static final JSONObject createJson(@NonNull String IDString,
                                  @NonNull String dataOne,
                                  @NonNull Object objectOne,
                                  String dataTwo,
                                  Object objectTwo) throws JSONException {
        JSONObject dataOfJsonObject = new JSONObject()
                .put(dataOne, objectOne);
        if (dataTwo != null && objectTwo != null)
            dataOfJsonObject.put(dataTwo, objectTwo);
        return new JSONObject()
                .put(Variables.ID, IDString)
                .put(Variables.DATA, dataOfJsonObject);
    }

    public static final JSONObject createJson(@NonNull String IDString,
                                              @NonNull String dataOne,
                                              @NonNull Object objectOne) throws JSONException {
        JSONObject dataOfJsonObject = new JSONObject()
                .put(dataOne, objectOne);
        return new JSONObject()
                .put(Variables.ID, IDString)
                .put(Variables.DATA, dataOfJsonObject);
    }
}
