package com.IR_team.terrafirmarealitycore.registry;

import com.IR_team.terrafirmarealitycore.TFR_Core;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TFR_Core.MODID);

    public static final Supplier<CreativeModeTab> MAIN_TAB = CREATIVE_MODE_TABS.register("main_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + TFR_Core.MODID + ".main_tab"))
            .icon(() -> new ItemStack(Items.IRON_INGOT)) // Set a representative icon
            .displayItems((parameters, output) -> {
                // Add items here
                output.accept(ModItems.EXAMPLE_ITEM.get());
                output.accept(ModItems.EXAMPLE_BLOCK_ITEM.get());
            }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
