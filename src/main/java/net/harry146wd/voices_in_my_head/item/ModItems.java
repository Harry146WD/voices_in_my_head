package net.harry146wd.voices_in_my_head.item;

import net.harry146wd.voices_in_my_head.VoicesInMyHead;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, VoicesInMyHead.MOD_ID);

    public static final RegistryObject<Item> WANDOFVOICES = ITEMS.register("wand_of_voices",
            () -> new WandOfVoices(new  Item.Properties().tab(CreativeModeTab.TAB_MISC)));



    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
