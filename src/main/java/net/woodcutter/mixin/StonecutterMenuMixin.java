package net.woodcutter.mixin;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.woodcutter.screen.WoodcutterMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StonecutterMenu.class)
public class StonecutterMenuMixin {

    @ModifyVariable(method = "setupRecipeList", at = @At("HEAD"), argsOnly = true)
    private ItemStack filterMenuRecipes(ItemStack item) {
        boolean isWood = item.is(ItemTags.PLANKS) ||
                item.is(ItemTags.LOGS) ||
                item.is(Items.BAMBOO_BLOCK) ||
                item.is(Items.STRIPPED_BAMBOO_BLOCK) ||
                item.is(Items.BAMBOO_MOSAIC);

        if (((Object) this).getClass() == StonecutterMenu.class && isWood) {
            return ItemStack.EMPTY;
        }

        if (((Object) this).getClass() == WoodcutterMenu.class && !isWood) {
            return ItemStack.EMPTY;
        }

        return item;
    }

    @Inject(method = "quickMoveStack", at = @At("HEAD"), cancellable = true)
    private void preventWoodShiftClickInStonecutter(Player player, int slotIndex, CallbackInfoReturnable<ItemStack> cir) {
        if (((Object) this).getClass() == StonecutterMenu.class) {
            StonecutterMenu menu = (StonecutterMenu) (Object) this;
            Slot slot = menu.slots.get(slotIndex);
            if (slot != null && slot.hasItem()) {
                ItemStack stack = slot.getItem();
                boolean isWood = stack.is(ItemTags.PLANKS) ||
                        stack.is(ItemTags.LOGS) ||
                        stack.is(Items.BAMBOO_BLOCK) ||
                        stack.is(Items.STRIPPED_BAMBOO_BLOCK) ||
                        stack.is(Items.BAMBOO_MOSAIC);

                if (slotIndex >= 2 && isWood) {
                    cir.setReturnValue(ItemStack.EMPTY);
                }
            }
        }
    }
}