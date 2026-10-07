package com.example.examplemod;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

/** Registers the standalone gun without changes to the main mod constructor. */
@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModGuns {
    private ModGuns() {}

    @SubscribeEvent
    public static void registerItems(RegisterEvent event) {
        event.register(ForgeRegistries.Keys.ITEMS, helper ->
                helper.register(new ResourceLocation(ExampleMod.MODID, "mp5"),
                        new Mp5Item()));
    }
}
