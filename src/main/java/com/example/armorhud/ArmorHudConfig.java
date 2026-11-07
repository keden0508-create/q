package com.example.armorhud;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** config/armorhud.json 에 저장되는 설정 (외부 라이브러리 없이 Gson만 사용) */
public final class ArmorHudConfig {
    public boolean showArmor = true;
    public boolean showHands = true;
    public boolean showFireworks = true;
    public boolean showTotems = true;
    public boolean rightSide = false;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static ArmorHudConfig instance = new ArmorHudConfig();

    public static ArmorHudConfig get() {
        return instance;
    }

    private static Path path() {
        return FabricLoader.getInstance().getConfigDir().resolve("armorhud.json");
    }

    public static void load() {
        Path p = path();
        if (!Files.exists(p)) {
            save();
            return;
        }
        try {
            ArmorHudConfig loaded = GSON.fromJson(Files.readString(p, StandardCharsets.UTF_8), ArmorHudConfig.class);
            if (loaded != null) instance = loaded;
        } catch (Exception e) {
            // 파일이 깨졌으면 기본값 사용
            instance = new ArmorHudConfig();
        }
    }

    public static void save() {
        try {
            Files.writeString(path(), GSON.toJson(instance), StandardCharsets.UTF_8);
        } catch (IOException ignored) {
        }
    }
}
