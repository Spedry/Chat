package database;

import lombok.Getter;
import lombok.NonNull;
import org.json.JSONException;
import org.json.JSONObject;
import hash.Hashing;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public abstract class AbsUser {
    @Getter
    private final String userName, userHash;
    @Getter
    private final byte[] publicKey;
    @Getter
    private byte[] privateKey;
    private Hashing hashing;

    public AbsUser(@NonNull JSONObject jsonObject, @NonNull byte[] publicKey) {
        this.userName = getDatafromJson(jsonObject, "Username");
        this.userHash = getDatafromJson(jsonObject, "Password");
        this.publicKey = publicKey;
    }

    String getDatafromJson(JSONObject jsonObject, String type) throws JSONException {
        return jsonObject.getJSONObject("Data").getString(type);
    }

    public void secondHash() {
        hashing = new Hashing();
        hashing.hashIt(userHash);
        privateKey = hashing.getSalt();
    }
}
