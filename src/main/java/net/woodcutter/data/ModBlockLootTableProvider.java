package net.woodcutter.data;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.minecraft.core.HolderLookup;
import net.woodcutter.block.ModBlocks;

import java.util.concurrent.CompletableFuture;

public class ModBlockLootTableProvider extends FabricBlockLootSubProvider {

    public ModBlockLootTableProvider(FabricPackOutput packOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(packOutput, registryLookup);
    }

    @Override
    public void generate() {
        this.dropSelf(ModBlocks.WOODCUTTER);
    }
}