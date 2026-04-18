package com.IR_team.terrafirmarealitycore;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(TFR_Core.MODID)
public class TFR_Core
{
    public static final String MODID = "tfrc";
    private static final Logger LOGGER = LogUtils.getLogger();

    public TFR_Core()
    {
        FMLJavaModLoadingContext context = FMLJavaModLoadingContext.get();
        IEventBus modEventBus = context.getModEventBus();

        MinecraftForge.EVENT_BUS.register(this);
    }
}
