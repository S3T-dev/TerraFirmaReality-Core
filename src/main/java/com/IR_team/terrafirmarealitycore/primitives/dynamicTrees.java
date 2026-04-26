package com.IR_team.terrafirmarealitycore.primitives;

import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import com.IR_team.terrafirmarealitycore.TFR_Core;
import net.dries007.tfc.common.blocks.wood.ILeavesBlock;
import net.dries007.tfc.util.AxeLoggingHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3d;

import java.util.*;

/**
 * Manager class for TFC tree felling using Sable's simulated sublevels.
 */
public class dynamicTrees {

    private static boolean isInternalProcessing = false;

    /**
     * Converts a TFC tree into a Sable simulated sublevel (contraption).
     */
    public static void summonFallingTree(Level world, BlockPos cutPos, BlockState cutState) {
        // Guard against recursion
        if (isInternalProcessing) return;
        if (!(world instanceof ServerLevel serverLevel)) return;
        
        // Find the player who likely chopped the tree (nearest player within 10 blocks)
        Player player = serverLevel.getNearestPlayer(cutPos.getX() + 0.5, cutPos.getY() + 0.5, cutPos.getZ() + 0.5, 10.0, false);

        isInternalProcessing = true;
        try {
            processSableTree(serverLevel, cutPos, player);
        } catch (Throwable t) {
            TFR_Core.LOGGER.error("TFR-Core: Error during tree felling at " + cutPos, t);
        } finally {
            isInternalProcessing = false;
        }
    }

    private static void processSableTree(ServerLevel world, BlockPos cutPos, Player player) {
        // Find all logs in the TFC tree structure
        List<BlockPos> logs = AxeLoggingHelper.findLogs(world, cutPos);
        if (logs == null || logs.isEmpty()) return;

        Set<BlockPos> toMove = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();
        
        for (BlockPos log : logs) {
            if (!world.getBlockState(log).isAir()) {
                toMove.add(log);
                queue.add(log);
            }
        }
        
        if (toMove.isEmpty()) return;
        
        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();
            for (Direction dir : Direction.values()) {
                BlockPos neighbor = current.relative(dir);
                if (!toMove.contains(neighbor)) {
                    BlockState state = world.getBlockState(neighbor);
                    if (state.getBlock() instanceof ILeavesBlock) {
                        toMove.add(neighbor);
                        queue.add(neighbor);
                    }
                }
            }
        }

        TFR_Core.LOGGER.info("TFR-Core: Assembling tree ({} blocks) into SubLevel at {}", toMove.size(), cutPos);

        BoundingBox3i bounds = BoundingBox3i.from(toMove);
        ServerSubLevel subLevel = SubLevelAssemblyHelper.assembleBlocks(world, cutPos, toMove, bounds);

        if (subLevel != null) {
            RigidBodyHandle handle = RigidBodyHandle.of(subLevel);
            if (handle != null) {
                Vector3d lookDir;
                if (player != null) {
                    // Fall in the direction the player is looking
                    lookDir = new Vector3d(player.getLookAngle().x, 0, player.getLookAngle().z);
                } else {
                    // Random fallback if no player found
                    Random random = new Random();
                    lookDir = new Vector3d(random.nextDouble() - 0.5, 0, random.nextDouble() - 0.5);
                }
                
                if (lookDir.lengthSquared() < 0.001) lookDir.set(0, 0, 1);
                lookDir.normalize();

                // Rotate around the perpendicular axis
                Vector3d rotationAxis = new Vector3d(lookDir).cross(0, 1, 0).normalize();
                
                // Add angular velocity
                double forceMagnitude = 0.85;
                handle.addLinearAndAngularVelocity(new Vector3d(0, 0, 0), new Vector3d(rotationAxis).mul(forceMagnitude));
                
                // Slight forward kick
                handle.addLinearAndAngularVelocity(new Vector3d(lookDir).mul(0.1), new Vector3d(0, 0, 0));
            }
        }
    }
}
