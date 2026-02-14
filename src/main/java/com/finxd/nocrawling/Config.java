package com.finxd.nocrawling;

import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.ModConfigSpec;

import static com.finxd.nocrawling.NoCrawling.MODID;

@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static boolean oldEyeHeight;

    public static final ModConfigSpec.BooleanValue OLD_EYE_HEIGHT = BUILDER
            .comment(" Should player's crouching eye level be at it's old value?")
            .comment(" (this option clashes with Nostalgic Tweaks' \"oldSneaking\" tweak, the animation breaks a little, so both options shouldn't be used at the same time)")
            .comment(" true = 1.54 (old value), false = 1.27 (modern vanilla value)")
            .define("oldEyeHeight", false);

    static final ModConfigSpec SPEC = BUILDER.build();

    @net.neoforged.bus.api.SubscribeEvent
    static void onLoad(final net.neoforged.fml.event.config.ModConfigEvent event) {
        oldEyeHeight = OLD_EYE_HEIGHT.get();
    }
}


