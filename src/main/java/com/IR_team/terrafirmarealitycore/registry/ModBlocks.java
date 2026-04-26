package com.IR_team.terrafirmarealitycore.registry;

import com.IR_team.terrafirmarealitycore.TFR_Core;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(TFR_Core.MODID);

    // Example block registration
     public static final Supplier<Block> EXAMPLE_BLOCK = BLOCKS.registerSimpleBlock("example_block", BlockBehaviour.Properties.of());

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
