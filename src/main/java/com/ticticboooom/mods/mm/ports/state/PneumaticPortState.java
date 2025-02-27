package com.ticticboooom.mods.mm.ports.state;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ticticboooom.mods.mm.MM;
import com.ticticboooom.mods.mm.client.jei.category.MMJeiPlugin;
import com.ticticboooom.mods.mm.client.jei.ingredients.model.PressureStack;
import com.ticticboooom.mods.mm.ports.storage.PneumaticPortStorage;
import com.ticticboooom.mods.mm.ports.storage.PortStorage;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredientType;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.system.CallbackI;

import java.util.List;

public class PneumaticPortState extends PortState {

    public static final Codec<PneumaticPortState> CODEC = RecordCodecBuilder.create(x -> x.group(
            Codec.FLOAT.fieldOf("pressure").forGetter(z -> z.pressure)
    ).apply(x, PneumaticPortState::new));

    private float pressure;

    public PneumaticPortState(float pressure) {
        this.pressure = pressure;
    }

    @Override
    public void processRequirement(List<PortStorage> storage) {
        float current = pressure;
        for (PortStorage portStorage : storage) {
            if (portStorage instanceof PneumaticPortStorage){
                PneumaticPortStorage pnc = (PneumaticPortStorage) portStorage;
                float prePressure = pnc.getInv().getPressure();
                pnc.getInv().setPressure(pnc.getInv().getPressure() - current);
                current -= prePressure;
                if (current  <= 0){
                    return;
                }
            }
        }
    }

    @Override
    public boolean validateRequirement(List<PortStorage> storage) {
        float current = pressure;
        for (PortStorage portStorage : storage) {
            if (portStorage instanceof PneumaticPortStorage){
                PneumaticPortStorage pnc = (PneumaticPortStorage) portStorage;
                float prePressure = pnc.getInv().getPressure();
                current -= prePressure;
                if (current  <= 0){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void processResult(List<PortStorage> storage) {
        float current = pressure;
        for (PortStorage portStorage : storage) {
            if (portStorage instanceof PneumaticPortStorage){
                PneumaticPortStorage pnc = (PneumaticPortStorage) portStorage;
                float prePressure = pnc.getInv().getPressure();
                if (pnc.getInv().getPressure() - current > pnc.getInv().getDangerPressure()){
                    pnc.getInv().setPressure(pnc.getInv().getDangerPressure());
                    current -= pnc.getInv().getDangerPressure() - prePressure;
                } else {
                    pnc.getInv().setPressure(prePressure + current);
                    current -= prePressure;
                }
                if (current  <= 0){
                    return;
                }
            }
        }
    }

    @Override
    public boolean validateResult(List<PortStorage> storage) {
        float current = pressure;
        for (PortStorage portStorage : storage) {
            if (portStorage instanceof PneumaticPortStorage){
                PneumaticPortStorage pnc = (PneumaticPortStorage) portStorage;
                float prePressure = pnc.getInv().getPressure();
                if (pnc.getInv().getPressure() - current > pnc.getInv().getDangerPressure()){
                    pnc.getInv().setPressure(pnc.getInv().getDangerPressure());
                    current -= pnc.getInv().getDangerPressure() - prePressure;
                } else {
                    pnc.getInv().setPressure(prePressure + current);
                    current -= prePressure;
                }
                if (current  <= 0){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public ResourceLocation getName() {
        return new ResourceLocation(MM.ID, "pressure");
    }

    @Override
    public IIngredientType<?> getJeiIngredientType() {
        return MMJeiPlugin.PRESSURE_TYPE;
    }

    @Override
    public <T> List<T> getIngredient(boolean input) {
        return (List<T>) Lists.newArrayList(new PressureStack(pressure));
    }

    @Override
    public void setupRecipe(IRecipeLayout layout, Integer typeIndex, int x, int y, boolean input) {
        IGuiIngredientGroup<PressureStack> group = layout.getIngredientsGroup(MMJeiPlugin.PRESSURE_TYPE);
        group.init(typeIndex, input, x, y);
        group.set(typeIndex, new PressureStack(pressure));
    }

    @Override
    public void render(MatrixStack ms, int x, int y, int mouseX, int mouseY, IJeiHelpers helpers) {
        IDrawableStatic slot = helpers.getGuiHelper().getSlotDrawable();
        slot.draw(ms, x, y);
    }
}
