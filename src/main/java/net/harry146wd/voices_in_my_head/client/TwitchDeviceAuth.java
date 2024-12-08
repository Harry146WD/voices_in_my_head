package net.harry146wd.voices_in_my_head.client;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class TwitchDeviceAuth {
    private static final String CLIENT_ID = "qff49f99tc1crobhukhxze9lvlwjph";
    private static final String DEVICE_AUTH_URL = "https://id.twitch.tv/oauth2/device";
    private static final String TOKEN_URL = "https://id.twitch.tv/oauth2/token";
    private static final String SCOPES = "chat:read";

    public static JsonObject requestDeviceCode() throws Exception {
        URL url = new URL(DEVICE_AUTH_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setDoOutput(true);

        String params = "client_id=" + CLIENT_ID + "&scopes=" + SCOPES.replace(" ", "%20");
        try (OutputStream os = connection.getOutputStream()) {
            os.write(params.getBytes());
            os.flush();
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            return JsonParser.parseString(response.toString()).getAsJsonObject();
        }
    }

    public static JsonObject pollForToken(String deviceCode) throws Exception {
        while (true) {
            Thread.sleep(5000);  // Consulta cada 5 segundos

            URL url = new URL(TOKEN_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoOutput(true);

            String params = "client_id=" + CLIENT_ID
                    + "&scopes=" + SCOPES.replace(" ", "%20")
                    + "&device_code=" + deviceCode
                    + "&grant_type=urn:ietf:params:oauth:grant-type:device_code";
            try (OutputStream os = connection.getOutputStream()) {
                os.write(params.getBytes());
                os.flush();
            }

            int statusCode = connection.getResponseCode();
            if (statusCode == 200) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                    return JsonParser.parseString(response.toString()).getAsJsonObject();
                }
            } else if (statusCode == 400) {
                System.out.println("Authorization pending...");
            } else {
                throw new Exception("Error: " + statusCode);
            }
        }
    }

    public static JsonObject refreshToken(String refreshToken) throws Exception {

        String params = "grant_type=refresh_token"
                + "&client_id=" + CLIENT_ID
                + "&refresh_token=" + URLEncoder.encode(refreshToken, StandardCharsets.UTF_8);

        URL url = new URL(TOKEN_URL + "?" + params);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");

        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            JsonObject jsonResponse = JsonParser.parseReader(reader).getAsJsonObject();

            String newAccessToken = jsonResponse.get("access_token").getAsString();
            String newRefreshToken = jsonResponse.get("refresh_token").getAsString();
            int newExpiresIn = jsonResponse.get("expires_in").getAsInt();
            long newLastObtainedTime = System.currentTimeMillis();

            // Guardar los nuevos tokens
            DataManager.saveTwitchAuthData(newAccessToken, newRefreshToken, newExpiresIn, newLastObtainedTime);
            return jsonResponse;

        } else {
            InputStreamReader reader = new InputStreamReader(connection.getErrorStream());
            JsonObject errorResponse = JsonParser.parseReader(reader).getAsJsonObject();
            String error = errorResponse.get("message").getAsString();
            throw new Exception("Error al refrescar el token: " + error);
        }
    }


    public static boolean isTokenExpired(int expiresIn, long lastObtainedTokenTime){
       // return  expiresIn < (System.currentTimeMillis() / 1000) -(lastObtainedTokenTime / 1000) ;
        long currentTimeInSeconds = System.currentTimeMillis() / 1000; // Convertir tiempo actual a segundos
        long tokenObtainedTimeInSeconds = lastObtainedTokenTime / 1000; // Convertir tiempo de obtención a segundos

        return currentTimeInSeconds >= (tokenObtainedTimeInSeconds + expiresIn);
    }


    public static String getUserName(String accessToken) {
        try {
            URL url = new URL("https://api.twitch.tv/helix/users");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);
            connection.setRequestProperty("Client-Id", CLIENT_ID);

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                JsonObject jsonResponse = JsonParser.parseReader(reader).getAsJsonObject();
                JsonArray data = jsonResponse.getAsJsonArray("data");
                if (!data.isEmpty()) {
                    JsonObject user = data.get(0).getAsJsonObject();
                    return user.get("login").getAsString();
                }
            } else {
                System.err.println("Error al obtener el nombre de usuario: " + responseCode);
            }
        } catch (Exception e) {
            System.err.println("Excepción al obtener el nombre de usuario: " + e.getMessage());
        }
        return null;
    }


}