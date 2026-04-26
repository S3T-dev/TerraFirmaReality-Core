package com.IR_team.terrafirmarealitycore.aeronautics;

import com.IR_team.terrafirmarealitycore.TFR_Core;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@EventBusSubscriber(modid = TFR_Core.MODID)
public class AeronauticsEvents {

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            riverFix.checkAllContraptionsInRiver(serverLevel);
        }
    }
}
