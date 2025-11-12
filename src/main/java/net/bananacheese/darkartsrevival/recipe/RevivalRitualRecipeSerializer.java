package net.bananacheese.darkartsrevival.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;

import java.util.List;

public class RevivalRitualRecipeSerializer implements RecipeSerializer<RevivalRitualRecipe> {
    public static final RevivalRitualRecipeSerializer INSTANCE = new RevivalRitualRecipeSerializer();

    private static final MapCodec<RevivalRitualRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    com.mojang.serialization.Codec.INT.fieldOf("min_tier").forGetter(RevivalRitualRecipe::getMinTier),
                    Ingredient.CODEC.fieldOf("altar_item").forGetter(RevivalRitualRecipe::getAltarItem),
                    com.mojang.serialization.Codec.INT.optionalFieldOf("fluid_amount", 0).forGetter(RevivalRitualRecipe::getFluidAmount),
                    Ingredient.CODEC.listOf().optionalFieldOf("pedestal_items", List.of()).forGetter(RevivalRitualRecipe::getPedestalItems),
                    ItemStack.CODEC.optionalFieldOf("result", ItemStack.EMPTY).forGetter(RevivalRitualRecipe::getResultItem)
            ).apply(instance, RevivalRitualRecipe::new)
    );

    private static final PacketCodec<RegistryByteBuf, RevivalRitualRecipe> PACKET_CODEC = new PacketCodec<>() {
        @Override
        public RevivalRitualRecipe decode(RegistryByteBuf buf) {
            return read(buf);
        }

        @Override
        public void encode(RegistryByteBuf buf, RevivalRitualRecipe recipe) {
            write(buf, recipe);
        }
    };

    @Override
    public MapCodec<RevivalRitualRecipe> codec() {
        return CODEC;
    }

    @Override
    public PacketCodec<RegistryByteBuf, RevivalRitualRecipe> packetCodec() {
        return PACKET_CODEC;
    }

    private static void write(RegistryByteBuf buf, RevivalRitualRecipe recipe) {
        buf.writeVarInt(recipe.getMinTier());
        Ingredient.PACKET_CODEC.encode(buf, recipe.getAltarItem());
        buf.writeVarInt(recipe.getFluidAmount());

        buf.writeVarInt(recipe.getPedestalItems().size());
        for (Ingredient ingredient : recipe.getPedestalItems()) {
            Ingredient.PACKET_CODEC.encode(buf, ingredient);
        }

        ItemStack.PACKET_CODEC.encode(buf, recipe.getResultItem());
    }

    private static RevivalRitualRecipe read(RegistryByteBuf buf) {
        int minTier = buf.readVarInt();
        Ingredient altarItem = Ingredient.PACKET_CODEC.decode(buf);
        int fluidAmount = buf.readVarInt();

        int pedestalCount = buf.readVarInt();
        List<Ingredient> pedestalItems = new java.util.ArrayList<>();
        for (int i = 0; i < pedestalCount; i++) {
            pedestalItems.add(Ingredient.PACKET_CODEC.decode(buf));
        }

        ItemStack result = ItemStack.PACKET_CODEC.decode(buf);

        return new RevivalRitualRecipe(minTier, altarItem, fluidAmount, pedestalItems, result);
    }
}