package com.ticticboooom.mods.mm.client.jei.category;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.ticticboooom.mods.mm.MM;
import com.ticticboooom.mods.mm.block.ControllerBlock;
import com.ticticboooom.mods.mm.client.util.GuiBlockRenderBuilder;
import com.ticticboooom.mods.mm.data.MachineStructureRecipe;
import com.ticticboooom.mods.mm.data.model.structure.MachineStructureRecipeKeyModel;
import com.ticticboooom.mods.mm.registration.MMLoader;
import com.ticticboooom.mods.mm.registration.MMSetup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MachineStructureRecipeCategory implements IRecipeCategory<MachineStructureRecipe> {

    private static final ResourceLocation overlayRl = new ResourceLocation(MM.ID, "textures/gui/gui_large_jei.png");
    private static final ResourceLocation iconRl = new ResourceLocation(MM.ID, "textures/items/blueprint.png");
    private static final ResourceLocation slotRl = new ResourceLocation(MM.ID, "textures/gui/slot_parts.png");
    private IJeiHelpers helpers;
    private ControllerBlock controller;
    private MachineStructureRecipe recipe;
    private float xRotation = 0;
    private double xLastMousePosition = 0;
    private float yRotation = 0;
    private double yLastMousePosition = 0;
    private int sliceY = 0;
    private boolean slicingActive = false;

    public MachineStructureRecipeCategory(IJeiHelpers helpers, ControllerBlock controller) {
        this.helpers = helpers;
        this.controller = controller;
    }

    @Override
    public ResourceLocation getUid() {
        return new ResourceLocation(MM.ID, "machine_structure_" + controller.getControllerId());
    }

    @Override
    public Class<? extends MachineStructureRecipe> getRecipeClass() {
        return MachineStructureRecipe.class;
    }

    @Override
    public String getTitle() {
        return controller.getControllerName();
    }

    @Override
    public IDrawable getBackground() {
        return helpers.getGuiHelper().createDrawable(overlayRl, 0, 0, 162, 150);
    }

    @Override
    public IDrawable getIcon() {
        return helpers.getGuiHelper().createDrawableIngredient(new ItemStack(MMSetup.BLUEPRINT.get(), 1));
    }

    private IDrawable getButton() {return helpers.getGuiHelper().createDrawable(slotRl, 0, 44, 18, 18);}

    @Override
    public void setIngredients(MachineStructureRecipe machineStructureRecipe, IIngredients iIngredients) {
        Ingredient ingredient = Ingredient.of(new ItemStack(MMSetup.BLUEPRINT.get()));
        ArrayList<Ingredient> objects = new ArrayList<>();
        objects.add(ingredient);
        objects.add(Ingredient.of(new ItemStack(ForgeRegistries.ITEMS.getValue(controller.getRegistryName()))));
        iIngredients.setInputIngredients(objects);
    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, MachineStructureRecipe machineStructureRecipe, IIngredients iIngredients) {

    }

    @Override
    public void draw(MachineStructureRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        if (this.recipe != recipe) {
            this.recipe = null;
            xRotation = 0;
            yRotation = 0;
            sliceY = 0;
            slicingActive = false;
        }
        Minecraft mc = Minecraft.getInstance();
        if (xLastMousePosition == 0) {
            xLastMousePosition = mouseX;
        }
        if (yLastMousePosition == 0) {
            yLastMousePosition = mouseY;
        }

        if (GLFW.glfwGetMouseButton(mc.getWindow().getWindow(), GLFW.GLFW_MOUSE_BUTTON_1) != 0) {
            double relMoveX = mouseX - xLastMousePosition;
            double relMoveY = mouseY - yLastMousePosition;
            xRotation += relMoveX;
            yRotation += relMoveY;
        }
        int furthestX = Integer.MAX_VALUE;
        int nearestX = Integer.MIN_VALUE;
        int furthestZ = Integer.MAX_VALUE;
        int nearestZ = Integer.MIN_VALUE;

        int topY = Integer.MIN_VALUE;
        int bottomY = Integer.MAX_VALUE;

        for (MachineStructureRecipeKeyModel part : recipe.getModels().get(0)) {
            furthestX = Math.min(part.getPos().getX(), furthestX);
            nearestX = Math.max(part.getPos().getX(), nearestX);
            furthestZ = Math.min(part.getPos().getZ(), furthestZ);
            nearestZ = Math.max(part.getPos().getZ(), nearestZ);
            topY = Math.max(part.getPos().getY(), topY);
            bottomY = Math.min(part.getPos().getY(), bottomY);
        }
        int scaleFactor = Math.abs(furthestX) + Math.abs(nearestX) + Math.abs(furthestZ) + Math.abs(nearestZ);
        scaleFactor = scaleFactor / 4;
        nearestX++;
        nearestZ++;
        float centreX = ((float) nearestX - furthestX) / 2f;
        float centreZ = ((float) nearestZ - furthestZ) / 2f;

        List<MachineStructureRecipeKeyModel> parts = recipe.getModels().get(0);
        if (slicingActive) {
            parts = parts.stream().filter(x -> x.getPos().getY() == sliceY).collect(Collectors.toList());
        }

        for (MachineStructureRecipeKeyModel part : parts) {
            if (part.getBlock().isEmpty() && part.getTag().isEmpty()) {
                continue;
            }

            BlockPos bp = new BlockPos(-part.getPos().getX(), part.getPos().getY(), -part.getPos().getZ());

            if (!part.getBlock().equals("")) {
                ResourceLocation resourceLocation = new ResourceLocation(part.getBlock());
                Block block = ForgeRegistries.BLOCKS.getValue(resourceLocation);
                BlockState defaultState = block.defaultBlockState();
                new GuiBlockRenderBuilder(defaultState).at(bp)
                        .withPrePosition(new Vector3f(6.5f, -5, 10))
                .withRotation(new Quaternion(new Vector3f(1, 0, 0), 15 + yRotation, true))
                .withRotation(new Quaternion(new Vector3f(0, -1, 0), 225 - xRotation, true))
                .withScale(new Vector3f(12f, -12, 12))
                .finalize(matrixStack);
            }
        }
        if (sliceY == 0) {
            ControllerBlock block = null;
            for (RegistryObject<ControllerBlock> reg : MMLoader.BLOCKS) {
                if (recipe.getControllerId().contains(reg.get().getControllerId())) {
                    block = reg.get();
                }
            }
            if (block != null) {
                BlockState defaultState = block.defaultBlockState().setValue(DirectionalBlock.FACING, Direction.NORTH);
                new GuiBlockRenderBuilder(defaultState).at(new BlockPos(0, 0, 0))
                        .withPrePosition(new Vector3f(6.5f, -5, 10))
                        .withRotation(new Quaternion(new Vector3f(1, 0, 0), 15 + yRotation, true))
                        .withRotation(new Quaternion(new Vector3f(0, -1, 0), 225 - xRotation, true))
                        .withScale(new Vector3f(12f, -12f, 12f))
                        .finalize(matrixStack);

            }
        }

        this.recipe = recipe;
        xLastMousePosition = mouseX;
        yLastMousePosition = mouseY;
        getButton().draw(matrixStack, 144, 125);
    }

    @Override
    public boolean handleClick(MachineStructureRecipe recipe, double mouseX, double mouseY, int mouseButton) {
        if (mouseX > 144 && mouseY > 125 && mouseX < 162 && mouseY < 143) {
            startIncrementSlice();
        }
        return false;
    }

    private void startIncrementSlice() {

        int topY = Integer.MIN_VALUE;
        int bottomY = Integer.MAX_VALUE;

        for (MachineStructureRecipeKeyModel part : recipe.getModels().get(0)) {
            topY = Math.max(part.getPos().getY(), topY);
            bottomY = Math.min(part.getPos().getY(), bottomY);
        }

        if (!slicingActive){
            slicingActive = true;
            sliceY = bottomY;
        } else if (sliceY == topY){
            slicingActive = false;
            sliceY = 0;
        } else {
            sliceY++;
        }


    }
}
