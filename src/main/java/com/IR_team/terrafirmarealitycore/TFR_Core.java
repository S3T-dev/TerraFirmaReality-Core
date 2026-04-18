package com.IR_team.terrafirmarealitycore;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(TFR_Core.MODID)
public class TFR_Core {

    public static final String MODID = "tfrc";
    public static final Logger LOGGER = LogUtils.getLogger();

    public TFR_Core(IEventBus modEventBus, ModContainer modContainer) {
        // Register event listeners here when you add @SubscribeEvent methods
    }

}
