package com.IR_team.terrafirmarealitycore.registry;

import com.IR_team.terrafirmarealitycore.TFR_Core;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TFR_Core.MODID);

    // Example item registration
    public static final Supplier<Item> EXAMPLE_ITEM = ITEMS.registerSimpleItem("example_item", new Item.Properties());
    public static final Supplier<BlockItem> EXAMPLE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("example_block", ModBlocks.EXAMPLE_BLOCK);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
