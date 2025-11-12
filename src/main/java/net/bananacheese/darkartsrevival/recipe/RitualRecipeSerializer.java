package net.bananacheese.darkartsrevival.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;

import java.util.List;

public class RitualRecipeSerializer implements RecipeSerializer<RitualRecipe> {
    public static final RitualRecipeSerializer INSTANCE = new RitualRecipeSerializer();

    private static final MapCodec<RitualRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.INT.fieldOf("min_tier").forGetter(RitualRecipe::getMinTier),
                    Ingredient.CODEC.fieldOf("altar_item").forGetter(RitualRecipe::getAltarItem),
                    Codec.INT.optionalFieldOf("fluid_amount", 0).forGetter(RitualRecipe::getFluidAmount),
                    Ingredient.CODEC.listOf().optionalFieldOf("pedestal_items", List.of()).forGetter(RitualRecipe::getPedestalItems),
                    ItemStack.CODEC.fieldOf("result").forGetter(RitualRecipe::getResultItem)
            ).apply(instance, RitualRecipe::new)
    );

    private static final PacketCodec<RegistryByteBuf, RitualRecipe> PACKET_CODEC = new PacketCodec<>() {
        @Override
        public RitualRecipe decode(RegistryByteBuf buf) {
            return read(buf);
        }

        @Override
        public void encode(RegistryByteBuf buf, RitualRecipe recipe) {
            write(buf, recipe);
        }
    };

    @Override
    public MapCodec<RitualRecipe> codec() {
        return CODEC;
    }

    @Override
    public PacketCodec<RegistryByteBuf, RitualRecipe> packetCodec() {
        return PACKET_CODEC;
    }

    private static void write(RegistryByteBuf buf, RitualRecipe recipe) {
        buf.writeVarInt(recipe.getMinTier());
        Ingredient.PACKET_CODEC.encode(buf, recipe.getAltarItem());
        buf.writeVarInt(recipe.getFluidAmount());

        buf.writeVarInt(recipe.getPedestalItems().size());
        for (Ingredient ingredient : recipe.getPedestalItems()) {
            Ingredient.PACKET_CODEC.encode(buf, ingredient);
        }

        ItemStack.PACKET_CODEC.encode(buf, recipe.getResultItem());
    }

    private static RitualRecipe read(RegistryByteBuf buf) {
        int minTier = buf.readVarInt();
        Ingredient altarItem = Ingredient.PACKET_CODEC.decode(buf);
        int fluidAmount = buf.readVarInt();

        int pedestalCount = buf.readVarInt();
        List<Ingredient> pedestalItems = new java.util.ArrayList<>();
        for (int i = 0; i < pedestalCount; i++) {
            pedestalItems.add(Ingredient.PACKET_CODEC.decode(buf));
        }

        ItemStack result = ItemStack.PACKET_CODEC.decode(buf);

        return new RitualRecipe(minTier, altarItem, fluidAmount, pedestalItems, result);
    }
}