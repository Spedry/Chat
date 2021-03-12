package database;

import lombok.Getter;
import lombok.NonNull;
import org.json.JSONException;
import org.json.JSONObject;
import hash.Hashing;
import shortcuts_for_M_and_V.Variables;

public abstract class AbsUser extends LoginToMySQL {
    @Getter
    private final String userName, userHash;
    @Getter
    private final byte[] publicKey;
    @Getter
    private byte[] privateKey;


    public AbsUser(@NonNull JSONObject jsonObject, @NonNull byte[] publicKey) {
        this.userName = getDatafromJson(jsonObject, Variables.USERNAME);
        this.userHash = getDatafromJson(jsonObject, Variables.PASSWORD);
        this.publicKey = publicKey;
    }

    String getDatafromJson(JSONObject jsonObject, String type) throws JSONException {
        return jsonObject.getJSONObject(Variables.DATA).getString(type);
    }

    public void secondHash() {
        Hashing hashing = new Hashing();
        hashing.hashIt(userHash);
        privateKey = hashing.getSalt();
    }
}
