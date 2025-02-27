package com.ticticboooom.mods.mm.block.container;

import com.ticticboooom.mods.mm.block.tile.ControllerBlockEntity;
import com.ticticboooom.mods.mm.block.tile.MachinePortBlockEntity;
import lombok.Getter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public class PortBlockContainer extends Container {

    private final PlayerInventory inv;
    @Getter
    private MachinePortBlockEntity tile;

    public PortBlockContainer(@Nullable ContainerType<?> p_i50105_1_, int windowId, PlayerInventory inv, MachinePortBlockEntity tile) {
        super(p_i50105_1_, windowId);
        this.inv = inv;
        tile.getStorage().setupContainer(this, inv, tile);
        this.tile = tile;
    }

    public PortBlockContainer(ContainerType<?> container, int windowId, PlayerInventory player, PacketBuffer buf) {
        this(container, windowId, player, (MachinePortBlockEntity) player.player.level.getBlockEntity(buf.readBlockPos()));
    }

    @Override
    public boolean stillValid(PlayerEntity p_75145_1_) {
        return true;
    }

    @Override
    public Slot addSlot(Slot p_75146_1_) {
        return super.addSlot(p_75146_1_);
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity p_82846_1_, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Object o = tile.getStorage().getLO().orElse(null);
        if (o instanceof ItemStackHandler) {
            ItemStackHandler handler = ((ItemStackHandler) o);
            Slot slot = this.getSlot(index);
            if (slot.hasItem()) {
                ItemStack itemStack1 = slot.getItem();
                itemStack = itemStack1.copy();
                if (index < handler.getSlots()) {
                    if (!this.moveItemStackTo(itemStack1, handler.getSlots(), inv.getContainerSize(), true)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(itemStack1, 0, handler.getSlots(), false)) {
                    return ItemStack.EMPTY;
                }

                if (itemStack1.isEmpty()) {
                    slot.set(ItemStack.EMPTY);
                } else {
                    slot.setChanged();
                }
            }
        }
        return itemStack;
    }
}
