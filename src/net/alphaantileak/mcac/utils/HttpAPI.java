package net.alphaantileak.mcac.utils;

import com.google.gson.Gson;
import net.alphaantileak.mcac.server.data.MinecraftAuthStartRequest;
import net.alphaantileak.mcac.server.data.MinecraftAuthStartResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpAPI {
    private static final String ENDPOINT = "https://mcac.alphaantileak.net/api/v1";
    private static final Gson GSON = new Gson();

    public static MinecraftAuthStartResponse startAuth(MinecraftAuthStartRequest request) throws IOException  {
        URL url = new URL(ENDPOINT + "/minecraft/auth/start");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setDoInput(true);
        con.setDoOutput(true);
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "MCAC-Server");

        try (OutputStream out = con.getOutputStream()) {
            out.write(GSON.toJson(request).getBytes());
            out.flush();
        }

        try (InputStream in = con.getInputStream()) {
            return GSON.fromJson(new InputStreamReader(in), MinecraftAuthStartResponse.class);
        }
    }
}
