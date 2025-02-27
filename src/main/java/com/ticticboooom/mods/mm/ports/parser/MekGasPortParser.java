package com.ticticboooom.mods.mm.ports.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.ticticboooom.mods.mm.ports.state.PortState;
import com.ticticboooom.mods.mm.ports.state.MekGasPortState;
import com.ticticboooom.mods.mm.ports.storage.PortStorage;
import com.ticticboooom.mods.mm.ports.storage.MekGasPortStorage;
import lombok.SneakyThrows;
import mekanism.api.chemical.gas.GasStack;
import mekanism.client.jei.MekanismJEI;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

import java.util.List;
import java.util.function.Supplier;

public class MekGasPortParser implements IPortFactory {

    @Override
    public void setIngredients(IIngredients ingredients, List<?> stacks, boolean input) {
        if (input) {
            ingredients.setInputs(MekanismJEI.TYPE_GAS, (List<GasStack>)stacks);
        } else {
            ingredients.setOutputs(MekanismJEI.TYPE_GAS, (List<GasStack>)stacks);
        }
    }

    @Override
    public PortState createState(JsonObject obj) {
        DataResult<Pair<MekGasPortState, JsonElement>> apply = JsonOps.INSTANCE.withDecoder(MekGasPortState.CODEC).apply(obj);
        return apply.result().get().getFirst();
    }

    @SneakyThrows
    @Override
    public PortState createState(PacketBuffer buf) {
        return buf.readWithCodec(MekGasPortState.CODEC);
    }

    @Override
    public Supplier<PortStorage> createStorage(JsonObject obj) {
        return () -> {
            DataResult<Pair<MekGasPortStorage, JsonElement>> apply = JsonOps.INSTANCE.withDecoder(MekGasPortStorage.CODEC).apply(obj);
            return apply.result().get().getFirst();
        };
    }

    @SneakyThrows
    @Override
    public void write(PacketBuffer buf, PortState state) {
        buf.writeWithCodec(MekGasPortState.CODEC, (MekGasPortState)state);
    }
}
