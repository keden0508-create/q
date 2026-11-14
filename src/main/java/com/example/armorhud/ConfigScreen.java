package com.example.armorhud;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class ConfigScreen extends Screen {
    private final Screen parent;

    public ConfigScreen(Screen parent) {
        super(Component.translatable("armorhud.config.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        ArmorHudConfig c = ArmorHudConfig.get();
        int w = 220, x = this.width / 2 - w / 2;
        int y = this.height / 2 - 70;

        addToggle(x, y, w, "armorhud.config.show_armor", () -> c.showArmor, v -> c.showArmor = v);
        addToggle(x, y + 24, w, "armorhud.config.show_hands", () -> c.showHands, v -> c.showHands = v);
        addToggle(x, y + 48, w, "armorhud.config.show_fireworks", () -> c.showFireworks, v -> c.showFireworks = v);
        addToggle(x, y + 72, w, "armorhud.config.show_totems", () -> c.showTotems, v -> c.showTotems = v);

        // 위치: 왼쪽 / 오른쪽
        addRenderableWidget(Button.builder(sideLabel(c.rightSide), b -> {
            c.rightSide = !c.rightSide;
            b.setMessage(sideLabel(c.rightSide));
        }).bounds(x, y + 96, w, 20).build());

        addRenderableWidget(Button.builder(Component.translatable("gui.done"), b -> onClose())
                .bounds(x, y + 126, w, 20).build());
    }

    private void addToggle(int x, int y, int w, String key, BooleanSupplier get, Consumer<Boolean> set) {
        addRenderableWidget(Button.builder(label(key, get.getAsBoolean()), b -> {
            set.accept(!get.getAsBoolean());
            b.setMessage(label(key, get.getAsBoolean()));
        }).bounds(x, y, w, 20).build());
    }

    private static Component label(String key, boolean on) {
        return Component.translatable(key).append(": ")
                .append(Component.translatable(on ? "options.on" : "options.off"));
    }

    private static Component sideLabel(boolean right) {
        return Component.translatable("armorhud.config.side").append(": ")
                .append(Component.translatable(right ? "armorhud.config.right" : "armorhud.config.left"));
    }

    @Override
    public void onClose() {
        ArmorHudConfig.save();
        this.minecraft.gui.setScreen(parent);
    }
}
