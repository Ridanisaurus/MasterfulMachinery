package com.ticticboooom.mods.mm.registration;

import com.ticticboooom.mods.mm.MM;
import com.ticticboooom.mods.mm.ports.MasterfulPortType;
import com.ticticboooom.mods.mm.ports.parser.*;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class MMPorts {
    public static Map<ResourceLocation, MasterfulPortType> PORTS = new HashMap<>();

    public static void init() {
        PORTS.put(new ResourceLocation(MM.ID, "items"), new MasterfulPortType(new ResourceLocation(MM.ID, "items"), new ItemPortParser()));
        PORTS.put(new ResourceLocation(MM.ID, "fluids"), new MasterfulPortType(new ResourceLocation(MM.ID, "fluids"), new FluidPortParser()));
        PORTS.put(new ResourceLocation(MM.ID, "energy"),new MasterfulPortType(new ResourceLocation(MM.ID, "energy"), new EnergyPortParser()));
        PORTS.put(new ResourceLocation(MM.ID, "mekanism_gas"),new MasterfulPortType(new ResourceLocation(MM.ID, "mekanism_gas"), new MekGasPortParser()));
        PORTS.put(new ResourceLocation(MM.ID, "mekanism_slurry"),new MasterfulPortType(new ResourceLocation(MM.ID, "mekanism_slurry"), new MekSlurryPortParser()));
    }
}
