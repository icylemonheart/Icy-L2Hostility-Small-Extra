package com.icyl2extra.pandora;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.icyl2extra.IcyL2HostilitySmallExtra;
import com.icyl2extra.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FusionCharmItem extends Item implements ICurioItem {

    public static final String TAG_CHARMS = "FusionCharms";

    private static final TagKey<Attribute> PERCENTAGE_ATTR = TagKey.create(
            Registries.ATTRIBUTE, ResourceLocation.fromNamespaceAndPath("l2damagetracker", "percentage"));
    private static final TagKey<Attribute> NEGATIVE_ATTR = TagKey.create(
            Registries.ATTRIBUTE, ResourceLocation.fromNamespaceAndPath("l2damagetracker", "negative"));

    public FusionCharmItem(Properties properties) {
        super(properties);
    }

    public static ItemStack create(List<ItemStack> charms, HolderLookup.Provider registries) {
        ItemStack stack = new ItemStack(ModItems.FUSION_CHARM.get());
        ListTag list = new ListTag();
        for (ItemStack charm : charms) {
            ItemStack copy = charm.copy();
            copy.setCount(1);
            list.add(copy.save(registries));
        }
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.put(TAG_CHARMS, list));
        return stack;
    }

    public static List<ItemStack> getCharms(ItemStack stack, HolderLookup.Provider registries) {
        List<ItemStack> result = new ArrayList<>();
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) {
            return result;
        }
        ListTag list = data.copyTag().getList(TAG_CHARMS, Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            ItemStack.parse(registries, list.getCompound(i)).ifPresent(result::add);
        }
        return result;
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(
            SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> result = LinkedHashMultimap.create();
        if (slotContext.entity() == null) {
            return result;
        }
        List<ItemStack> charms = getCharms(stack, slotContext.entity().registryAccess());
        for (int i = 0; i < charms.size(); i++) {
            int index = i;
            ItemStack charm = charms.get(i);
            CuriosApi.getCurio(charm).ifPresent(curio -> {
                Multimap<Holder<Attribute>, AttributeModifier> sub =
                        curio.getAttributeModifiers(slotContext, id);
                for (Map.Entry<Holder<Attribute>, AttributeModifier> entry : sub.entries()) {
                    AttributeModifier modifier = entry.getValue();
                    String seed = id + "|fusion|" + index + "|"
                            + entry.getKey().getKey().location();
                    ResourceLocation newId = ResourceLocation.fromNamespaceAndPath(
                            IcyL2HostilitySmallExtra.MODID,
                            "fusion/" + UUID.nameUUIDFromBytes(seed.getBytes(StandardCharsets.UTF_8)));
                    result.put(entry.getKey(),
                            new AttributeModifier(newId, modifier.amount(), modifier.operation()));
                }
            });
        }
        return result;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        HolderLookup.Provider registries = context.registries() != null ? context.registries() : RegistryAccess.EMPTY;
        List<ItemStack> charms = getCharms(stack, registries);
        if (charms.isEmpty()) {
            tooltip.add(Component.translatable(
                    "tooltip." + IcyL2HostilitySmallExtra.MODID + ".fusion_charm.empty")
                    .withStyle(ChatFormatting.GRAY));
            return;
        }
        tooltip.add(Component.translatable(
                "tooltip." + IcyL2HostilitySmallExtra.MODID + ".fusion_charm.header")
                .withStyle(ChatFormatting.GOLD));
        for (int i = 0; i < charms.size(); i++) {
            ItemStack charm = charms.get(i);
            int lvl = CharmLevelData.getLevel(charm);
            MutableComponent name = charm.getHoverName().copy();
            String suffix = CharmLevelData.levelSuffix(lvl);
            if (!suffix.isEmpty()) {
                name = name.append(Component.literal(suffix).withStyle(ChatFormatting.GOLD));
            }
            tooltip.add(Component.literal("▪ ").withStyle(ChatFormatting.GRAY).append(name));

            int index = i;
            CuriosApi.getCurio(charm).ifPresent(curio -> {
                ResourceLocation previewId = ResourceLocation.fromNamespaceAndPath(
                        IcyL2HostilitySmallExtra.MODID, "fusion_preview/" + index);
                Multimap<Holder<Attribute>, AttributeModifier> sub = curio.getAttributeModifiers(
                        new SlotContext("curio", null, 0, false, false), previewId);
                for (Map.Entry<Holder<Attribute>, AttributeModifier> entry : sub.entries()) {
                    tooltip.add(Component.literal("   ").append(
                            formatModifier(entry.getKey(), entry.getValue())));
                }
            });
        }
    }

    private static Component formatModifier(Holder<Attribute> attr, AttributeModifier modifier) {
        double amount = modifier.amount();
        boolean percentage = attr.is(PERCENTAGE_ATTR);
        boolean negativeAttr = attr.is(NEGATIVE_ATTR);
        ChatFormatting color = (amount < 0) ^ negativeAttr ? ChatFormatting.RED : ChatFormatting.BLUE;
        String sign = amount < 0 ? "-" : "+";
        DecimalFormat fmt = ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT;
        String value;
        if (percentage && modifier.operation() == AttributeModifier.Operation.ADD_VALUE) {
            value = sign + fmt.format(Math.abs(amount) * 100.0D) + "%";
        } else if (percentage) {
            value = "x" + fmt.format(1.0D + amount);
        } else if (modifier.operation() == AttributeModifier.Operation.ADD_VALUE) {
            value = sign + fmt.format(Math.abs(amount));
        } else {
            value = sign + fmt.format(Math.abs(amount) * 100.0D) + "%";
        }
        return Component.empty()
                .append(Component.literal(value).withStyle(color))
                .append(" ")
                .append(Component.translatable(attr.value().getDescriptionId()))
                .withStyle(color);
    }
}
