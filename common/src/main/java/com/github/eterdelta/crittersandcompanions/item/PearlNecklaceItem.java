package com.github.eterdelta.crittersandcompanions.item;

import com.github.eterdelta.crittersandcompanions.platform.Services;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

public class PearlNecklaceItem extends Item {

    private static Stream<ItemStack> getEquipment(Player player) {
        return Stream.of(
                player.getInventory().getNonEquipmentItems().stream(),
                Services.PLATFORM.getAdditionalEquipment(player)
        ).flatMap(Function.identity());
    }

    public static Optional<PearlNecklaceItem> getWearing(Entity entity) {
        if (!(entity instanceof Player player)) return Optional.empty();
        return getEquipment(player)
                .map(ItemStack::getItem)
                .filter(it -> it instanceof PearlNecklaceItem)
                .map(it -> (PearlNecklaceItem) it)
                .max(Comparator.comparing(PearlNecklaceItem::getLevel));
    }

    private final int level;

    public PearlNecklaceItem(Properties properties, int necklaceLevel) {
        super(properties);
        this.level = necklaceLevel;
    }

    private String percentage(int level, double base) {
        return String.format("%.0f", level * 100 * base);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> components, TooltipFlag tooltipFlag) {
        components.accept(Component.translatable("pearl_necklace.level", this.level).withStyle(ChatFormatting.DARK_GRAY));
        components.accept(Component.empty());

        components.accept(Component.translatable("pearl_necklace.swim_speed", percentage(level, Services.CONFIGS.common().necklaceSwimSpeed.get())).withStyle(ChatFormatting.GRAY));
        components.accept(Component.translatable("pearl_necklace.drowned_range", percentage(level, Services.CONFIGS.common().necklaceDrownedDebuff.get())).withStyle(ChatFormatting.GRAY));
        if (level > 1) {
            components.accept(Component.translatable("pearl_necklace.guardian_range", percentage(level, Services.CONFIGS.common().necklaceGuardianDebuff.get())).withStyle(ChatFormatting.GRAY));
        }
    }

    public int getLevel() {
        return this.level;
    }

}
