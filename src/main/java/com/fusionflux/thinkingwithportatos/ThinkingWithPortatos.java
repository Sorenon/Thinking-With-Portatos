package com.fusionflux.thinkingwithportatos;

import com.fusionflux.thinkingwithportatos.blocks.ThinkingWithPortatosBlocks;
import com.fusionflux.thinkingwithportatos.client.ThinkingWithPortatosClient;
import com.fusionflux.thinkingwithportatos.config.ThinkingWithPortatosConfig;
import com.fusionflux.thinkingwithportatos.entity.CubeEntity;
import com.fusionflux.thinkingwithportatos.entity.ThinkingWithPortatosEntities;
import com.fusionflux.thinkingwithportatos.items.PortalGun;
import com.fusionflux.thinkingwithportatos.items.ThinkingWithPortatosItems;
import com.fusionflux.thinkingwithportatos.physics.BodyGrabbingManager;
import com.fusionflux.thinkingwithportatos.sound.ThinkingWithPortatosSounds;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.api.event.ElementCollisionEvents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

public class ThinkingWithPortatos implements ModInitializer {

    public static final ThinkingWithPortatosConfig CONFIG = new ThinkingWithPortatosConfig();

    public static final String MODID = "thinkingwithportatos";

    public static final ItemGroup ThinkingWithPortatosGroup = FabricItemGroupBuilder.build(
            id("general"),
            () -> new ItemStack(ThinkingWithPortatosItems.PORTAL_GUN_MODEL2));

    int t = 0;

    public static Identifier id(String path) {
        return new Identifier(MODID, path);
    }

    public static final BodyGrabbingManager bodyGrabbingManager = new BodyGrabbingManager(true);

    @Override
    public void onInitialize() {
        ThinkingWithPortatosConfig.register();
        ThinkingWithPortatosBlocks.registerBlocks();
        ThinkingWithPortatosItems.registerItems();
        ThinkingWithPortatosEntities.registerEntities();
        ThinkingWithPortatosSounds.registerSounds();
        registerPacketListener();
        bodyGrabbingManager.init();

        /*ElementCollisionEvents.BLOCK_COLLISION.register((element, blockPos, blockState) -> {
            if (!((CubeEntity) element).world.isClient) {
                if (t == 0) {
                    ((CubeEntity) element).world.playSound(null, ((CubeEntity) element).getPos().getX(), ((CubeEntity) element).getPos().getY(), ((CubeEntity) element).getPos().getZ(), ThinkingWithPortatosSounds.CUBE_HIT_EVENT, SoundCategory.NEUTRAL, .3F, 1F);
                    t = 3;
                }
                t--;
            }
        });*/

    }

    public static BodyGrabbingManager getBodyGrabbingManager(boolean client) {
        if (client) {
            return ThinkingWithPortatosClient.bodyGrabbingManager;
        } else {
            return bodyGrabbingManager;
        }
    }

    private void registerPacketListener() {
        ServerPlayNetworking.registerGlobalReceiver(id("ptl_lft_click"), (server, player, handler, buf, responseSender) -> {
            ServerWorld serverWorld = player.getServerWorld();
            Hand hand = buf.readEnumConstant(Hand.class);
            ItemStack itemStack = player.getStackInHand(hand);
            player.updateLastActionTime();
            if (!itemStack.isEmpty() && itemStack.getItem() == ThinkingWithPortatosItems.PORTAL_GUN || itemStack.getItem() == ThinkingWithPortatosItems.PORTAL_GUN_MODEL2) {
                server.execute(() -> ((PortalGun) itemStack.getItem()).useLeft(serverWorld, player, hand));

            }
        });

    }

}
