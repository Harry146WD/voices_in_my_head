package net.harry146wd.voices_in_my_head.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import javax.naming.Context;

public class WandOfVoices extends Item {
    public WandOfVoices(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context){
        Level world = context.getLevel();
        Player player = context.getPlayer();

        System.out.println("Usando el Item");

        if(player != null && world != null){

        }

        return InteractionResult.SUCCESS;
    }
}
