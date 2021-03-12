package shortcuts_for_M_and_V;

import lombok.NonNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Methods {
    public static final JSONObject createJson(@NonNull String IDString,
                                              @NonNull String dataOne,
                                              @NonNull Object objectOne,
                                              @NonNull String dataTwo,
                                              @NonNull Object objectTwo,
                                              @NonNull String dataThree,
                                              @NonNull Object objectThree) throws JSONException {
        JSONObject dataOfJsonObject = new JSONObject()
                .put(dataOne, objectOne);
        if (dataTwo != null && objectTwo != null)
            dataOfJsonObject.put(dataTwo, objectTwo);
        if (dataThree != null && objectThree != null)
            dataOfJsonObject.put(dataThree, objectThree);
        return new JSONObject()
                .put(Variables.ID, IDString)
                .put(Variables.DATA, dataOfJsonObject);
    }

    public static final JSONObject createJson(@NonNull String IDString,
                                              @NonNull String dataOne,
                                              @NonNull Object objectOne,
                                              @NonNull String dataTwo,
                                              @NonNull Object objectTwo) throws JSONException {
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

    public static final JSONObject createJsonListofUsers(List<String> listofOnlineUsers) {
        JSONArray jsonArray = new JSONArray(listofOnlineUsers);
        JSONObject jsonObject = new JSONObject()
                .put(Variables.ID, Variables.SHOW_LOGIN_OF_ONLINE_USER)
                .put(Variables.DATA, jsonArray);
        return jsonObject;
    }

}
