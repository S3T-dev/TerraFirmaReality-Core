package com.IR_team.terrafirmarealitycore;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

import com.IR_team.terrafirmarealitycore.registry.ModBlocks;
import com.IR_team.terrafirmarealitycore.registry.ModItems;
import com.IR_team.terrafirmarealitycore.registry.ModCreativeTabs;

@Mod(TFR_Core.MODID)
public class TFR_Core {

    public static final String MODID = "tfrc";
    public static final Logger LOGGER = LogUtils.getLogger();

    public TFR_Core(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
    }

}
