package net.alphaantileak.mcac.server.data;

import com.google.gson.annotations.SerializedName;

public class MinecraftAuthStartResponse {
    @SerializedName("verify-token")
    public String verifyToken; // encrypted
}
