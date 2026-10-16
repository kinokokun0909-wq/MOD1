package com.example.examplemod.client;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.GunNetwork;
import com.example.examplemod.Mp5Item;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Read the use key even when a block or entity would consume vanilla right click. */
@Mod.EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT)
public final class GunInput {
    private static int ticksUntilNextShot;

    private GunInput() {}

    private static boolean canFire(Minecraft minecraft) {
        return minecraft.player != null && minecraft.level != null
                && minecraft.screen == null && minecraft.isWindowActive()
                && !minecraft.isPaused() && minecraft.player.isAlive()
                && !minecraft.player.isSpectator()
                && minecraft.player.getMainHandItem().getItem() instanceof Mp5Item;
    }

    @SubscribeEvent
    public static void onInteraction(InputEvent.InteractionKeyMappingTriggered event) {
        if (event.isUseItem() && canFire(Minecraft.getInstance())) {
            // The tick handler owns firing. Do not open chests, mount entities,
            // use the offhand, or also start vanilla item use with this click.
            event.setCanceled(true);
            event.setSwingHand(false);
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft minecraft = Minecraft.getInstance();
        if (!canFire(minecraft) || !minecraft.options.keyUse.isDown()) {
            ticksUntilNextShot = 0;
            return;
        }
        if (ticksUntilNextShot > 0) ticksUntilNextShot--;
        if (ticksUntilNextShot == 0) {
            GunNetwork.requestShot();
            ticksUntilNextShot = 2;
        }
    }
}
