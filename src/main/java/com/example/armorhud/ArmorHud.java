package com.example.armorhud;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public final class ArmorHud {
    private static final EquipmentSlot[] ARMOR = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };
    private static final EquipmentSlot[] HANDS = {
            EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND
    };
    private static final int ROW_H = 20;
    private static final int MARGIN = 6;
    private static final int TEXT_OFFSET = 22;

    private static final ItemStack FIREWORK_ICON = new ItemStack(Items.FIREWORK_ROCKET);
    private static final ItemStack TOTEM_ICON = new ItemStack(Items.TOTEM_OF_UNDYING);

    private record Row(ItemStack stack, String shortText, String detailText, int color) {}

    private ArmorHud() {}

    /** 인벤토리(핫바, 갑옷칸, 왼손 포함)에 있는 특정 아이템 총 개수 */
    private static int count(Inventory inv, Item item) {
        int total = 0;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack s = inv.getItem(i);
            if (s.is(item)) total += s.getCount();
        }
        return total;
    }

    private static void addEquip(List<Row> rows, ItemStack s) {
        if (s.isEmpty()) return;
        String name = s.getHoverName().getString();
        if (s.isDamageableItem()) {
            int max = s.getMaxDamage();
            int left = max - s.getDamageValue();
            int pct = max > 0 ? left * 100 / max : 0;
            rows.add(new Row(s, pct + "%",
                    name + "  " + left + "/" + max + " (" + pct + "%)",
                    0xFF000000 | s.getBarColor()));
        } else {
            rows.add(new Row(s, "", name, 0xFFFFFFFF));
        }
    }

    private static void addCount(List<Row> rows, ItemStack icon, int n) {
        rows.add(new Row(icon, "x" + n,
                icon.getHoverName().getString() + "  x" + n,
                n > 0 ? 0xFFFFFFFF : 0xFF888888));
    }

    public static void extract(GuiGraphicsExtractor g, DeltaTracker dt) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || ArmorHudClient.hudHidden) return;

        ArmorHudConfig cfg = ArmorHudConfig.get();
        boolean detail = ArmorHudClient.detailKey.isDown(); // 누르는 동안만

        List<Row> rows = new ArrayList<>(8);
        if (cfg.showArmor) {
            for (EquipmentSlot slot : ARMOR) addEquip(rows, mc.player.getItemBySlot(slot));
        }
        if (cfg.showHands) {
            for (EquipmentSlot slot : HANDS) addEquip(rows, mc.player.getItemBySlot(slot));
        }
        Inventory inv = mc.player.getInventory();
        if (cfg.showFireworks) addCount(rows, FIREWORK_ICON, count(inv, Items.FIREWORK_ROCKET));
        if (cfg.showTotems) addCount(rows, TOTEM_ICON, count(inv, Items.TOTEM_OF_UNDYING));
        if (rows.isEmpty()) return;

        int maxW = 0;
        for (Row r : rows) {
            String t = detail ? r.detailText() : r.shortText();
            maxW = Math.max(maxW, mc.font.width(t));
        }
        int blockW = TEXT_OFFSET + maxW;

        int screenW = mc.getWindow().getGuiScaledWidth();
        int screenH = mc.getWindow().getGuiScaledHeight();
        int x = cfg.rightSide ? screenW - MARGIN - blockW : MARGIN;
        int y0 = screenH / 2 - rows.size() * ROW_H / 2;

        if (detail) {
            g.fill(x - 3, y0 - 3, x + blockW + 4, y0 + rows.size() * ROW_H + 1, 0x90000000);
        }

        int y = y0;
        for (Row r : rows) {
            g.item(r.stack(), x, y);
            g.itemDecorations(mc.font, r.stack(), x, y);
            String text = detail ? r.detailText() : r.shortText();
            if (!text.isEmpty()) {
                g.text(mc.font, text, x + TEXT_OFFSET, y + 4, r.color(), true);
            }
            y += ROW_H;
        }
    }
}
