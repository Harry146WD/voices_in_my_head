package net.harry146wd.voices_in_my_head.client;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

public class TwitchClient {


    public static void connectToTwitchChat(com.github.twitch4j.TwitchClient twitchClient, String accessToken) {

        // Obtener el nombre de usuario autenticado
        String userName = TwitchDeviceAuth.getUserName(accessToken);

        if (userName != null) {
            // Unirse al canal del usuario
            System.out.println("Uniendose al Chat de " + userName + "...");
            twitchClient.getChat().joinChannel(userName);


            // Escuchar mensajes del chat
            twitchClient.getEventManager().onEvent(ChannelMessageEvent.class, event -> {
                System.out.println("Mensaje recibido: " + event.getMessage());
                // Aquí puedes procesar el mensaje como desees
            });

        } else {
            System.err.println("No se pudo obtener el nombre de usuario.");
        }

    }

    public static com.github.twitch4j.TwitchClient getTwitchClient(String accessToken){
        // Crear el cliente de Twitch
         com.github.twitch4j.TwitchClient twitchClient = TwitchClientBuilder.builder()
                .withEnableChat(true)
                .withChatAccount(new OAuth2Credential("twitch", accessToken))
                .build();

         return twitchClient;
    }

    // Metodo para desconectar del chat
    public static void disconnectFromChat(com.github.twitch4j.TwitchClient twitchClient) {
        if (twitchClient != null) {
            twitchClient.getChat().getChannels().forEach(twitchClient.getChat()::leaveChannel);
            twitchClient.close();
            System.out.println("Desconectado del chat de Twitch.");
        }
    }
}
