package com.example.examplemod;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

/** A first controllable ship prototype. Spawn with /summon mod1:warship. */
@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class WarshipEntity extends Boat {
    // EntityType creates a registry holder: construct it only inside RegisterEvent.
    public static EntityType<WarshipEntity> TYPE;

    public WarshipEntity(EntityType<? extends Boat> type, Level level) {
        super(type, level);
        // The visual hull is much larger than the prototype's interaction box.
        // Keep the visible bow/stern from disappearing at the edge of the screen.
        this.noCulling = true;
    }

    @SubscribeEvent
    public static void registerEntities(RegisterEvent event) {
        event.register(ForgeRegistries.Keys.ENTITY_TYPES, helper -> {
            TYPE = EntityType.Builder
                    .<WarshipEntity>of(WarshipEntity::new, MobCategory.MISC)
                    .sized(1.5F, 0.7F)
                    .clientTrackingRange(10)
                    .build(ExampleMod.MODID + ":warship");
            helper.register(new ResourceLocation(ExampleMod.MODID, "warship"), TYPE);
        });
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public Item getDropItem() {
        return Items.IRON_INGOT;
    }

    @Override
    public double getPassengersRidingOffset() {
        return 1.81D;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        // Keep the full-sized visual ship visible beyond the tiny interaction box's range.
        return distance < 256.0D * 256.0D;
    }
}
