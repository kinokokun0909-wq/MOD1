package com.example.examplemod;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
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
        event.register(ForgeRegistries.Keys.ITEMS, helper -> {
            helper.register(new ResourceLocation(ExampleMod.MODID, "mp5"), new Mp5Item());
            helper.register(new ResourceLocation(ExampleMod.MODID, "sniper"), new SniperItem());
        });
    }

    @SubscribeEvent
    public static void addCreativeItems(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(ForgeRegistries.ITEMS.getValue(new ResourceLocation(ExampleMod.MODID, "mp5")));
            event.accept(ForgeRegistries.ITEMS.getValue(new ResourceLocation(ExampleMod.MODID, "sniper")));
        }
    }
}
