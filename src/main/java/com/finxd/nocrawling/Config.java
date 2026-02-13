package com.finxd.nocrawling;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

import static com.finxd.nocrawling.NoCrawling.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static boolean oldEyeHeight;

    public static final ForgeConfigSpec.BooleanValue OLD_EYE_HEIGHT = BUILDER
            .comment(" Should player's crouching eye level be at it's old value?")
            .comment(" (this option clashes with Nostalgic Tweaks' \"oldSneaking\" tweak, the animation breaks a little, so both options shouldn't be used at the same time)")
            .comment(" true = 1.54 (old value), false = 1.27 (modern vanilla value)")
            .define("oldEyeHeight", false);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    @SubscribeEvent
    public static void onConstructModEvent(final FMLConstructModEvent event) {
        final ModLoadingContext context = ModLoadingContext.get();
        context.registerConfig(ModConfig.Type.SERVER, SPEC);
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        oldEyeHeight = OLD_EYE_HEIGHT.get();
    }
}


