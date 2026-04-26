package com.IR_team.terrafirmarealitycore.aeronautics;

import dev.ryanhcode.sable.api.physics.force.ForceTotal;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.joml.Vector3d;

public class riverFix {

	public static final TagKey<Biome> TFC_IS_RIVER = TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath("tfc", "is_river"));

	public static void applyForceToContraption(ServerSubLevel subLevel, SubLevelPhysicsSystem physicsSystem, Vector3d linearImpulse, Vector3d torqueImpulse) {
		RigidBodyHandle handle = physicsSystem.getPhysicsHandle(subLevel);
		if (handle != null) {
			ForceTotal forceTotal = new ForceTotal();
			forceTotal.applyLinearAndAngularImpulse(linearImpulse, torqueImpulse);
			handle.applyForcesAndReset(forceTotal);
		}
	}
	
	public static boolean isContraptionInRiver(ServerSubLevel subLevel) {
		// Get the main world level
		Level mainLevel = subLevel.getLevel();
		
		// Find the center of mass of the contraption in world coordinates
		Vector3d worldPos = subLevel.logicalPose().transformPosition(new Vector3d(subLevel.getMassTracker().getCenterOfMass()));
		BlockPos pos = BlockPos.containing(worldPos.x, worldPos.y, worldPos.z);
		
		// Check if the center is within a TFC river biome
		//boolean inRiverBiome = mainLevel.getBiome(pos).is(TFC_IS_RIVER);
		
		// Check if it's submerged in TFC River Water specifically
		net.minecraft.world.level.block.state.BlockState blockState = mainLevel.getBlockState(pos);
		boolean inRiverWater = blockState.getBlock() instanceof net.dries007.tfc.common.blocks.RiverWaterBlock;
		
		return inRiverWater;
	}

	public static net.minecraft.world.phys.Vec3 getRiverFlow(ServerSubLevel subLevel) {
		Level mainLevel = subLevel.getLevel();
		Vector3d worldPos = subLevel.logicalPose().transformPosition(new Vector3d(subLevel.getMassTracker().getCenterOfMass()));
		BlockPos pos = BlockPos.containing(worldPos.x, worldPos.y, worldPos.z);
		
		net.minecraft.world.level.block.state.BlockState blockState = mainLevel.getBlockState(pos);
		if (blockState.getBlock() instanceof net.dries007.tfc.common.blocks.RiverWaterBlock) {
			net.dries007.tfc.world.river.Flow flow = blockState.getValue(net.dries007.tfc.common.blocks.RiverWaterBlock.FLOW);
			return flow.getVector();
		}
		
		return net.minecraft.world.phys.Vec3.ZERO;
	}

	public static void checkAllContraptionsInRiver(net.minecraft.server.level.ServerLevel level) {
		if (level instanceof dev.ryanhcode.sable.mixinterface.plot.SubLevelContainerHolder holder) {
			dev.ryanhcode.sable.api.sublevel.SubLevelContainer container = holder.sable$getPlotContainer();
			if (container != null) {
				for (dev.ryanhcode.sable.sublevel.SubLevel subLevel : container.getAllSubLevels()) {
					if (subLevel instanceof ServerSubLevel serverSubLevel) {
						boolean inRiver = isContraptionInRiver(serverSubLevel);
						
						Vector3d pos = serverSubLevel.logicalPose().position();
						System.out.println("Contraption at (" + pos.x + ", " + pos.y + ", " + pos.z + ") is in river: " + inRiver);
						
						if (inRiver) {
							net.minecraft.world.phys.Vec3 flow = getRiverFlow(serverSubLevel);
							
							Vector3d flowDir = new Vector3d(flow.x, flow.y, flow.z);
							if (flowDir.lengthSquared() > 0.001) {
								flowDir.normalize();
								
								Vector3d currentVelocity = serverSubLevel.latestLinearVelocity;
								double speedAlongFlow = currentVelocity.dot(flowDir);
								double maxSpeed = 8.0; // 2 blocks per second limit
								
								if (speedAlongFlow < maxSpeed) {
									double mass = serverSubLevel.getMassTracker().getMass();
									
									// Target acceleration in blocks/sec^2
									double targetAcceleration = 4.0;
									// Impulse is applied every tick (20 times a second)
									// Delta V per tick = acceleration / 20
									double deltaV = targetAcceleration / 20.0;
									
									// Smoothly reduce force as it approaches max speed
									double speedFactor = 1.0 - (Math.max(0, speedAlongFlow) / maxSpeed);
									
									// Transform the flow direction into the contraption's local space
									Vector3d localFlowDir = new Vector3d(flowDir);
									serverSubLevel.logicalPose().orientation().transformInverse(localFlowDir);
									
									// Impulse = Mass * Delta V
									Vector3d linearImpulse = localFlowDir.mul(mass * deltaV * speedFactor);
									Vector3d torqueImpulse = new Vector3d(0, 0, 0);
									
									SubLevelPhysicsSystem physicsSystem = SubLevelPhysicsSystem.get(serverSubLevel.getLevel());
									applyForceToContraption(serverSubLevel, physicsSystem, linearImpulse, torqueImpulse);
								}
							}
						}
					}
				}
			}
		}
	}
}
