package database;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.json.JSONException;
import org.json.JSONObject;
import shortcuts_for_M_and_V.Variables;

public abstract class AbsRoom extends LoginToMySQL {
    @Getter
    private final String roomName, roomPassword;
    @Getter @Setter
    private int RoomId;

    public AbsRoom(@NonNull JSONObject jsonObject) {
        this.roomName = getDatafromJson(jsonObject, Variables.ROOM);
        this.roomPassword = getDatafromJson(jsonObject, Variables.PASSWORD);
    }

    String getDatafromJson(JSONObject jsonObject, String type) throws JSONException {
        return jsonObject.getJSONObject(Variables.DATA).getString(type);
    }
}
