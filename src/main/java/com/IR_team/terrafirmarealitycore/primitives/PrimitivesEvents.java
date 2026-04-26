package com.IR_team.terrafirmarealitycore.primitives;

import com.IR_team.terrafirmarealitycore.TFR_Core;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.entity.EntitySubLevelUtil;
import net.dries007.tfc.util.events.LoggingEvent;
import net.dries007.tfc.util.AxeLoggingHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = TFR_Core.MODID)
public class PrimitivesEvents {

    /**
     * Intercepts TFC tree chopping to trigger the falling tree animation/conversion.
     */
    @SubscribeEvent
    public static void onLogging(LoggingEvent event) {
        if (!event.isCanceled()) {
            // Cancel TFC's instant block removal
            event.setCanceled(true);
            
            Level level = event.getLevel() instanceof Level ? (Level) event.getLevel() : null;
            if (level != null) {
                // Trigger our custom Sable conversion
                dynamicTrees.summonFallingTree(
                    level, 
                    event.getPos(), 
                    event.getState()
                );
            }
        }
    }

    /**
     * Prevents players from "punching" contraptions (applying force) while holding a TFC axe.
     * This uses HIGHEST priority to ensure we cancel the event before other mods process it.
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onAttackContraption(AttackEntityEvent event) {
        Player player = event.getEntity();
        if (player == null || player.level().isClientSide) return;

        // Check if the player is holding a valid TFC axe
        if (AxeLoggingHelper.isLoggingAxe(player.getMainHandItem())) {
            Entity target = event.getTarget();
            
            // Check for Sable KinematicContraption or any entity tracked by Sable physics
			TFR_Core.LOGGER.info(target.getClass().getName());
            if (target instanceof dev.ryanhcode.sable.api.sublevel.KinematicContraption || 
                EntitySubLevelUtil.getTrackingSubLevel(target) != null ||
                target instanceof com.simibubi.create.content.contraptions.AbstractContraptionEntity) {
                
                event.setCanceled(true);
            }
        }
    }

    /**
     * Prevents players from punching blocks within a contraption using an axe.
     * In some cases, clicking a contraption might be treated as a block click.
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLeftClickContraption(PlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getEntity();
        if (player == null || player.level().isClientSide) return;

        if (AxeLoggingHelper.isLoggingAxe(player.getMainHandItem())) {
            // Check if the block being clicked is inside a Sable SubLevel
            if (Sable.HELPER.getContaining(player.level(), event.getPos()) != null) {
                TFR_Core.LOGGER.info("TFR-Core: Left-click on contraption block cancelled by player holding a TFC axe");
                //event.setCanceled(true);
            }
        }
    }
}
