package com.example.examplemod;

import com.example.examplemod.client.TestEntityShipModel;
import net.minecraft.client.renderer.culling.Frustum;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** The large battleship visual model for mod1:test_entity. */
@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT)
public class TestEntityRenderer extends EntityRenderer<TestEntity> {
    public TestEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        shadowRadius = 12.0F;
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ExampleMod.TEST_ENTITY.get(), TestEntityRenderer::new);
    }

    @Override
    public void render(TestEntity ship, float yaw, float partialTick,
                       PoseStack poses, MultiBufferSource buffers, int light) {
        poses.pushPose();
        poses.mulPose(Axis.YP.rotationDegrees(-yaw));
        var blockRenderer = Minecraft.getInstance().getBlockRenderer();
        // The immutable geometry is generated once, rather than every render frame.
        for (TestEntityShipModel.Part part : TestEntityShipModel.PARTS) {
            poses.pushPose();
            poses.translate(part.x(), part.y(), part.z());
            if (part.yaw() != 0) poses.mulPose(Axis.YP.rotationDegrees(part.yaw()));
            if (part.pitch() != 0) poses.mulPose(Axis.XP.rotationDegrees(part.pitch()));
            if (part.roll() != 0) poses.mulPose(Axis.ZP.rotationDegrees(part.roll()));
            poses.scale(part.width(), part.height(), part.length());
            poses.translate(-0.5, -0.5, -0.5);
            int partLight = part.material() == TestEntityShipModel.Material.LIGHT ? 15728880 : light;
            blockRenderer.renderSingleBlock(material(part.material()).defaultBlockState(),
                    poses, buffers, partLight, OverlayTexture.NO_OVERLAY);
            poses.popPose();
        }
        poses.popPose();
        super.render(ship, yaw, partialTick, poses, buffers, light);
    }

    private static Block material(TestEntityShipModel.Material material) {
        return switch (material) {
            case HULL -> Blocks.GRAY_CONCRETE;
            case DECK -> Blocks.LIGHT_GRAY_CONCRETE;
            case DARK -> Blocks.BLACK_CONCRETE;
            case RED -> Blocks.RED_CONCRETE;
            case WOOD -> Blocks.BROWN_CONCRETE;
            case WOOD_LIGHT -> Blocks.OAK_PLANKS;
            case WOOD_DARK -> Blocks.SPRUCE_PLANKS;
            case WATERLINE -> Blocks.BLACK_TERRACOTTA;
            case RUST -> Blocks.BROWN_TERRACOTTA;
            case STEEL -> Blocks.CYAN_TERRACOTTA;
            case BLACK -> Blocks.BLACK_CONCRETE;
            case ROPE -> Blocks.GRAY_TERRACOTTA;
            case GUN -> Blocks.GRAY_CONCRETE;
            case WINDOW -> Blocks.CYAN_CONCRETE;
            case WHITE -> Blocks.WHITE_CONCRETE;
            case LIGHT -> Blocks.SEA_LANTERN;
            case GREEN -> Blocks.LIME_CONCRETE;
            case BRONZE -> Blocks.YELLOW_TERRACOTTA;
        };
    }

    @Override
    public boolean shouldRender(TestEntity entity, Frustum frustum,
                                double cameraX, double cameraY, double cameraZ) {
        // The visual ship is 180 blocks long; the Mob's interaction box is tiny.
        return entity.distanceToSqr(cameraX, cameraY, cameraZ) < 512.0D * 512.0D
                && frustum.isVisible(entity.getBoundingBox().inflate(100.0D, 50.0D, 100.0D));
    }

    @Override
    public ResourceLocation getTextureLocation(TestEntity ship) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
