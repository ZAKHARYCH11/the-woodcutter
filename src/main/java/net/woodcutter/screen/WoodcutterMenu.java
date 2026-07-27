package net.woodcutter.screen;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.woodcutter.block.ModBlocks;
import net.woodcutter.sound.ModSounds;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class WoodcutterMenu extends StonecutterMenu {

    private final ContainerLevelAccess access;
    private final boolean[] isClosing;

    public WoodcutterMenu(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, ContainerLevelAccess.NULL);
    }

    public WoodcutterMenu(int syncId, Inventory playerInventory, ContainerLevelAccess access) {
        this(syncId, playerInventory, access, new boolean[]{false});
    }

    private WoodcutterMenu(int syncId, Inventory playerInventory, ContainerLevelAccess access, boolean[] isClosing) {
        super(syncId, playerInventory, wrapAccess(access, isClosing));
        this.access = access;
        this.isClosing = isClosing;
    }

    private static ContainerLevelAccess wrapAccess(ContainerLevelAccess original, boolean[] isClosing) {
        return new ContainerLevelAccess() {
            private long lastSoundTime = -1L;

            @Override
            public <T> Optional<T> evaluate(BiFunction<net.minecraft.world.level.Level, BlockPos, T> getter) {
                return original.evaluate(getter);
            }

            @Override
            public void execute(BiConsumer<net.minecraft.world.level.Level, BlockPos> action) {
                if (isClosing[0]) {
                    original.execute(action);
                } else {
                    original.execute((level, pos) -> {
                        long gameTime = level.getGameTime();
                        if (this.lastSoundTime != gameTime) {
                            level.playSound(null, pos, ModSounds.WOODCUTTER_TAKE_RESULT, SoundSource.BLOCKS, 1.0F, 1.0F);
                            this.lastSoundTime = gameTime;
                        }
                    });
                }
            }
        };
    }

    @Override
    public MenuType<?> getType() {
        return ModMenuTypes.WOODCUTTER_MENU;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, ModBlocks.WOODCUTTER);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            boolean isWood = stack.is(ItemTags.PLANKS) ||
                    stack.is(ItemTags.LOGS) ||
                    stack.is(Items.BAMBOO_BLOCK) ||
                    stack.is(Items.STRIPPED_BAMBOO_BLOCK) ||
                    stack.is(Items.BAMBOO_MOSAIC);

            if (slotIndex >= 2 && !isWood) {
                return ItemStack.EMPTY;
            }
        }
        return super.quickMoveStack(player, slotIndex);
    }

    @Override
    public void removed(Player player) {
        this.isClosing[0] = true;
        super.removed(player);
    }
}