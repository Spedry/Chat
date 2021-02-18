package client;

import org.json.JSONObject;

public class MessageSender {
    private final ClientSide clientSide;

    public MessageSender(ClientSide clientSide) {
        this.clientSide = clientSide;
    }

    public void printWriter(JSONObject data) {
        String stringData =  String.valueOf(data);
        String encodedString = org.apache.commons.codec.binary.Base64.encodeBase64String(clientSide.getRsa().encrypt(stringData));
        clientSide.getPrintWriter().println(encodedString);
    }

}
