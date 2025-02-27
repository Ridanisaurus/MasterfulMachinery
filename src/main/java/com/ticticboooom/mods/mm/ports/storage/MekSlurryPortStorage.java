package com.ticticboooom.mods.mm.ports.storage;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ticticboooom.mods.mm.MM;
import com.ticticboooom.mods.mm.block.container.PortBlockContainer;
import com.ticticboooom.mods.mm.block.tile.MachinePortBlockEntity;
import com.ticticboooom.mods.mm.helper.RLUtils;
import com.ticticboooom.mods.mm.inventory.mek.PortMekSlurryInventory;
import lombok.Getter;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import java.util.Objects;

public class MekSlurryPortStorage extends PortStorage {
    public static final Codec<MekSlurryPortStorage> CODEC  = RecordCodecBuilder.create(x -> x.group(
            Codec.LONG.fieldOf("capacity").forGetter(z -> z.capacity)
    ).apply(x, MekSlurryPortStorage::new));

    @Getter
    private final PortMekSlurryInventory inv;
    private long capacity;
    private final LazyOptional<PortMekSlurryInventory> invLO;


    public MekSlurryPortStorage(long capacity) {
        inv = new PortMekSlurryInventory(capacity);
        this.capacity = capacity;
        invLO = LazyOptional.of(() -> inv);
    }

    @Override
    public <T> LazyOptional<T> getLO() {
        return invLO.cast();
    }

    @Override
    public <T> boolean validate(Capability<T> cap) {
        return cap == Capabilities.SLURRY_HANDLER_CAPABILITY;
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        nbt.putString("slurry", inv.getStack().getType().getRegistryName().toString());
        nbt.putLong("amount", inv.getStack().getAmount());
        return nbt;
    }

    @Override
    public void load(CompoundNBT nbt) {
        if (nbt.contains("slurry")){
            inv.setStack(new SlurryStack(Objects.requireNonNull(MekanismAPI.slurryRegistry().getValue(RLUtils.toRL(nbt.getString("slurry")))), nbt.getLong("amount")));
        }
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, int left, int top, Screen screen) {
        Minecraft.getInstance().textureManager.bind(new ResourceLocation(MM.ID, "textures/gui/port_gui.png"));
        screen.blit(stack, left, top, 0, 0, 175, 256);
        int barOffsetX = 175 - 30;
        int barOffsetY = 20;
        screen.blit(stack, left + barOffsetX, top + barOffsetY, 175, 18, 18, 108);
        float amount = 0;
        if (inv.getStack().getAmount() > 0) {
            amount = (float) inv.getStack().getAmount() / inv.getTankCapacity(0);
        }
        screen.blit(stack, left + barOffsetX, top + barOffsetY, 193, 18, 18, (int) (108 * amount));
        AbstractGui.drawString(stack, Minecraft.getInstance().font, inv.getStack().getType().getTextComponent().getString(), left + 30, top + 60, 0xfefefe);
        AbstractGui.drawString(stack, Minecraft.getInstance().font, inv.getStack().getAmount() + "mB", left + 30, top + 80, 0xfefefe);

    }
}
