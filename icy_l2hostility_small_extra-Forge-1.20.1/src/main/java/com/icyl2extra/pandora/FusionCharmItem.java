package com.icyl2extra.pandora;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.icyl2extra.IcyL2HostilitySmallExtra;
import com.icyl2extra.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FusionCharmItem extends Item implements ICurioItem {

    public static final String TAG_CHARMS = "FusionCharms";

    private static final TagKey<Attribute> PERCENTAGE_ATTR = TagKey.create(
            Registries.ATTRIBUTE, new ResourceLocation("l2damagetracker", "percentage"));
    private static final TagKey<Attribute> NEGATIVE_ATTR = TagKey.create(
            Registries.ATTRIBUTE, new ResourceLocation("l2damagetracker", "negative"));

    public FusionCharmItem(Properties properties) {
        super(properties);
    }

    public static ItemStack create(List<ItemStack> charms) {
        ItemStack stack = new ItemStack(ModItems.FUSION_CHARM.get());
        ListTag list = new ListTag();
        for (ItemStack charm : charms) {
            ItemStack copy = charm.copy();
            copy.setCount(1);
            list.add(copy.save(new CompoundTag()));
        }
        stack.getOrCreateTag().put(TAG_CHARMS, list);
        return stack;
    }

    public static List<ItemStack> getCharms(ItemStack stack) {
        List<ItemStack> result = new ArrayList<>();
        if (stack == null || !stack.hasTag()) {
            return result;
        }
        ListTag list = stack.getTag().getList(TAG_CHARMS, Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            result.add(ItemStack.of(list.getCompound(i)));
        }
        return result;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(
            SlotContext slotContext, UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> result = LinkedHashMultimap.create();
        List<ItemStack> charms = getCharms(stack);
        for (int i = 0; i < charms.size(); i++) {
            ItemStack charm = charms.get(i);
            int index = i;
            CuriosApi.getCurio(charm).resolve().ifPresent(curio -> {
                Multimap<Attribute, AttributeModifier> sub = curio.getAttributeModifiers(slotContext, uuid);
                for (Map.Entry<Attribute, AttributeModifier> entry : sub.entries()) {
                    AttributeModifier modifier = entry.getValue();
                    UUID newId = UUID.nameUUIDFromBytes((uuid.toString() + "|fusion|" + index + "|"
                            + entry.getKey().getDescriptionId()).getBytes(StandardCharsets.UTF_8));
                    result.put(entry.getKey(), new AttributeModifier(
                            newId, modifier.getName(), modifier.getAmount(), modifier.getOperation()));
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
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        List<ItemStack> charms = getCharms(stack);
        if (charms.isEmpty()) {
            tooltip.add(Component.translatable("tooltip." + IcyL2HostilitySmallExtra.MODID + ".fusion_charm.empty")
                    .withStyle(ChatFormatting.GRAY));
            return;
        }
        tooltip.add(Component.translatable("tooltip." + IcyL2HostilitySmallExtra.MODID + ".fusion_charm.header")
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

            CuriosApi.getCurio(charm).resolve().ifPresent(curio -> {
                Multimap<Attribute, AttributeModifier> sub = curio.getAttributeModifiers(
                        new SlotContext("curio", null, 0, false, false), UUID.randomUUID());
                for (Map.Entry<Attribute, AttributeModifier> entry : sub.entries()) {
                    tooltip.add(Component.literal("   ").append(formatModifier(entry.getKey(), entry.getValue())));
                }
            });
        }
    }

    private static boolean isInAttrTag(Attribute attr, TagKey<Attribute> tag) {
        return ForgeRegistries.ATTRIBUTES.getHolder(attr).map(holder -> holder.is(tag)).orElse(false);
    }

    private static Component formatModifier(Attribute attr, AttributeModifier modifier) {
        double amount = modifier.getAmount();
        boolean percentage = isInAttrTag(attr, PERCENTAGE_ATTR);
        boolean negativeAttr = isInAttrTag(attr, NEGATIVE_ATTR);
        ChatFormatting color = (amount < 0) ^ negativeAttr ? ChatFormatting.RED : ChatFormatting.BLUE;
        String sign = amount < 0 ? "-" : "+";
        DecimalFormat fmt = ItemStack.ATTRIBUTE_MODIFIER_FORMAT;
        String value;
        if (percentage && modifier.getOperation() == AttributeModifier.Operation.ADDITION) {
            value = sign + fmt.format(Math.abs(amount) * 100.0D) + "%";
        } else if (percentage) {
            value = "x" + fmt.format(1.0D + amount);
        } else if (modifier.getOperation() == AttributeModifier.Operation.ADDITION) {
            value = sign + fmt.format(Math.abs(amount));
        } else {
            value = sign + fmt.format(Math.abs(amount) * 100.0D) + "%";
        }
        return Component.empty()
                .append(Component.literal(value).withStyle(color))
                .append(" ")
                .append(Component.translatable(attr.getDescriptionId()))
                .withStyle(color);
    }
}
