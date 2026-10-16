package com.example.examplemod;

import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

/** Clients request a shot; only the server chooses the target and damage. */
public final class GunNetwork {
    private static final String VERSION = "1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(ExampleMod.MODID, "gun"),
            () -> VERSION, VERSION::equals, VERSION::equals);

    private GunNetwork() {}

    public static void register() {
        CHANNEL.registerMessage(0, Fire.class, (message, buffer) -> {},
                buffer -> new Fire(), (message, supplier) -> {
                    var context = supplier.get();
                    context.enqueueWork(() -> {
                        var player = context.getSender();
                        if (player != null && player.isAlive() && !player.isSpectator()
                                && player.getMainHandItem().getItem() instanceof Mp5Item gun) {
                            gun.fire(player.serverLevel(), player);
                        }
                    });
                    context.setPacketHandled(true);
                }, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    public static void requestShot() {
        CHANNEL.sendToServer(new Fire());
    }

    private record Fire() {}
}
