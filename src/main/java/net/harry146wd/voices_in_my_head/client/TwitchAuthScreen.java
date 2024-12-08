package net.harry146wd.voices_in_my_head.client;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class TwitchAuthScreen extends Screen {
    private final Screen parent;
    private JsonObject response;
    private String userCode;
    private String deviceCode;
    private String verificationUrl;
    public boolean deviceCodeRequested = false;
    private String codeText;
    public boolean deviceCodeInserted = false;
    public boolean tokenObtained = false;
    public String accessToken;
    public String refreshToken;
    public int expiresIn;
    public long lastObtainedTokenTime;

    public TwitchAuthScreen(Screen parentScreen) {
        super(new TextComponent("Twitch Integration Settings"));
        parent = parentScreen;
    }

    @Override
    protected void init(){
        this.addRenderableWidget(new Button(this.width/ 2 - 110, this.height/ 2 - 20, 100, 20,
            new TextComponent("Log in to Twitch"), button -> {
            System.out.println("Loguearse a Twitch");

            try {
                System.out.println(verificationUrl);
                openBrowser(verificationUrl);

                deviceCodeInserted = true;

            } catch (Exception e) {
                System.err.println("No se pudo abrir el navegador: " + e);
            }

            //this.minecraft.setScreen(this.parent);
            }
        ));


        // Botón para regresar
        this.addRenderableWidget(new Button(this.width / 2 - 110, this.height / 2 + 20, 220, 20,
            new TextComponent("Back"), button -> {

            DataManager.saveTwitchAuthData(accessToken, refreshToken, expiresIn, lastObtainedTokenTime);

            if(this.minecraft != null){
                this.minecraft.setScreen(this.parent);
            }

        }));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta){

        this.renderBackground(poseStack);  // Renderiza el fondo de la pantalla

        if(!deviceCodeRequested){
            try {
                response = TwitchDeviceAuth.requestDeviceCode();

                deviceCode = response.get("device_code").getAsString();

                userCode = response.get("user_code").getAsString();

                verificationUrl = response.get("verification_uri").getAsString();

                deviceCodeRequested = true;

                codeText = "Copy this code: " + userCode ;
            }catch (Exception e){
                String errorMsg = "No se pudo obtener el codigo de dispositivo ";
                System.out.println(errorMsg + e);
                codeText = errorMsg;
            }
        }

        // Renderizar el título
        drawCenteredString(poseStack, this.font, this.title.getString(), this.width / 2, 20, 0xFFFFFF);

        // Renderizar el texto debajo del título
        drawCenteredString(poseStack, this.font, codeText, this.width / 2, 40, 0xCCCCCC);

        if(deviceCodeRequested){

            this.addRenderableWidget(new Button(this.width / 2 - 50, 55, 100, 20,
                    new TextComponent("Copy Code"), button -> {
                    copyToClipboard(userCode);
            }));

            // Texto ajustado con salto de línea
            List<FormattedCharSequence> lines = this.font.split(
                    new TextComponent("Then click the 'Log in to Twitch' button and paste the code in the opening twitch auth tab, when you are done with the tab, click obtain token"),
                    this.width - 40 // Ajusta este valor para definir los márgenes
            );
            int y = 80; // Posición vertical inicial
            for (FormattedCharSequence line : lines) {
                drawCenteredString(poseStack, this.font, line, this.width / 2, y, 0xCCCCCC);
                y += this.font.lineHeight; // Avanza una línea hacia abajo
            }
        }

        if(deviceCodeInserted) {
            this.addRenderableWidget(new Button(this.width / 2 + 10, this.height / 2 - 20, 100, 20,
                    new TextComponent("Obtain Token"), button -> {
                if(!tokenObtained){
                    try {
                        response = TwitchDeviceAuth.pollForToken(deviceCode);

                        accessToken = response.get("access_token").getAsString();
                        refreshToken = response.get("refresh_token").getAsString();
                        expiresIn = response.get("expires_in").getAsInt();
                        lastObtainedTokenTime = System.currentTimeMillis();
                        tokenObtained = true;

                        System.out.println("Token obtenido: "+ accessToken +" Expira en: " + expiresIn);
                    }catch (Exception e){
                        String errorMsg = "No se pudo obtener el token ";
                        System.out.println(errorMsg + e);
                    }
                }
            }));
        }

        super.render(poseStack, mouseX, mouseY, delta);
    }

    private void openBrowser(String url) {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                // Windows
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
            } else if (os.contains("mac")) {
                // macOS
                Runtime.getRuntime().exec("open " + url);
            } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
                // Linux/Unix
                Runtime.getRuntime().exec("xdg-open " + url);
            } else {
                throw new UnsupportedOperationException("Sistema operativo no soportado");
            }
        } catch (Exception e) {
            System.err.println("Error al abrir el navegador: " + e.getMessage());
        }
    }

    private void copyToClipboard(String text) {
        try {
            Minecraft.getInstance().keyboardHandler.setClipboard(text);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }


    }
}
