package net.harry146wd.voices_in_my_head;

import com.mojang.logging.LogUtils;
import net.harry146wd.voices_in_my_head.client.TwitchAuthScreen;
import net.harry146wd.voices_in_my_head.enchantment.ModEnchantments;
import net.harry146wd.voices_in_my_head.item.ModItems;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(VoicesInMyHead.MOD_ID)
public class VoicesInMyHead {


    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "voices_in_my_head";

    public VoicesInMyHead() {

        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(this::setup);

        ModItems.register(eventBus);
        ModEnchantments.register(eventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
    }

    @SubscribeEvent
    public void onOpenOptionsScreen(ScreenEvent.InitScreenEvent.Post event){
        if(event.getScreen() instanceof OptionsScreen){
            event.addListener(new Button(event.getScreen().width/2 - 100, event.getScreen().height/6 + 16, 200, 20,
                    new TextComponent("TwitchSettings"), button -> {
                        event.getScreen().getMinecraft().setScreen(new TwitchAuthScreen(event.getScreen()));
                    }));
        }
    }


}

