package net.harry146wd.voices_in_my_head.client;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class DataManager {

    public static final String FILE_PATH = "auth_data.json";


    public static void saveTwitchAuthData(String accessToken, String refreshToken, int expiresIn, long lastObtainedTime){
        try (FileWriter file = new FileWriter(FILE_PATH)) {
            JsonObject json = new JsonObject();
            json.addProperty("access_token", accessToken);
            json.addProperty("refresh_token", refreshToken);
            json.addProperty("expires_in", expiresIn);
            json.addProperty("last_obtained_time", lastObtainedTime);

            file.write(json.toString());
            file.flush();
            System.out.println("Tokens guardados correctamente.");
        } catch (IOException e) {
            System.err.println("Error al guardar los tokens: " + e.getMessage());
        }
    }

    public static JsonObject loadTwitchAuthData() {
        try (FileReader reader = new FileReader(FILE_PATH)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException e) {
            System.err.println("Error al cargar los tokens: " + e.getMessage());
            return null;
        }
    }

    public static boolean existsDataFile(){
        File file = new File(FILE_PATH);
        boolean exists = false;

        if (file.exists()) {
            if (file.isFile()) {
                exists = true;
            }
        }

        return exists;
    }
}
