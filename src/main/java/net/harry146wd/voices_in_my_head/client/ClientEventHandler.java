package net.harry146wd.voices_in_my_head.client;


import com.google.gson.JsonObject;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.client.Minecraft;

@Mod.EventBusSubscriber(modid = "voices_in_my_head", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEventHandler {

    private static boolean hasRun = false;
    //private static com.github.twitch4j.TwitchClient twitchClient = null;


    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        String accessToken = null;

        if (!hasRun && DataManager.existsDataFile()) {
            String refreshToken = null;
            int expiresIn;
            long lastObtainedTokenTime;

            boolean tokenValid = true;

            JsonObject authData = DataManager.loadTwitchAuthData();

            if (authData != null) {

                accessToken = authData.get("access_token").getAsString();
                refreshToken = authData.get("refresh_token").getAsString();
                expiresIn = authData.get("expires_in").getAsInt();
                lastObtainedTokenTime = authData.get("last_obtained_time").getAsLong();

                if (accessToken != null && refreshToken != null) {
                    if (TwitchDeviceAuth.isTokenExpired(expiresIn, lastObtainedTokenTime)) {

                        try {
                            JsonObject response = TwitchDeviceAuth.refreshToken(refreshToken);

                            accessToken = response.get("access_token").getAsString();

                        } catch (Exception e) {
                            System.err.println("No se pudo refrescar el token.");
                            tokenValid = false;
                        }
                    }

                    if(tokenValid){
                       // twitchClient = TwitchClient.getTwitchClient(accessToken);
                    }else {
                        System.err.println("No se pudo obtener el cliente ya que el token no es valido");
                    }

                }

            }

        }

        if (!hasRun && Minecraft.getInstance().player != null && Minecraft.getInstance().level != null) {
            hasRun = true;  // Nos aseguramos de que el código solo se ejecute una vez
            //TwitchClient.connectToTwitchChat(twitchClient, accessToken);  // Llama al metodo para cargar el chat de Twitch
        }


        // Verificar si el jugador ha salido del mundo
        if (hasRun && (Minecraft.getInstance().player == null || Minecraft.getInstance().level == null)) {
            hasRun = false;  // Reiniciar para cuando vuelva a entrar a otro mundo
            //TwitchClient.disconnectFromChat(twitchClient);  // Desconectar del chat de Twitch
        }
    }
}
