package net.alphaantileak.mcac.server.data;

import com.google.gson.annotations.SerializedName;

import java.security.PublicKey;
import java.util.Base64;

public class MinecraftAuthStartRequest {
    public String username;
    @SerializedName("public-key")
    public String publicKey;

    public MinecraftAuthStartRequest(String username, PublicKey publicKey) {
        this.username = username;
        this.publicKey = Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }
}
