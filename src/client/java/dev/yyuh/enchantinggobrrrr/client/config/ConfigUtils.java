package dev.yyuh.enchantinggobrrrr.client.config;

import net.fabricmc.loader.api.FabricLoader;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class ConfigUtils {
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("enchantinggobrr.json");

    public static boolean get(String path) {
        if (!CONFIG_PATH.toFile().exists()) {
            return false;
        }

        try {
            File config = new File(CONFIG_PATH.toUri());
            String c = new String(Files.readAllBytes(Path.of(config.getPath())));
            JSONObject object = new JSONObject(c);

            if (!object.has(path)) return false;

            return object.getBoolean(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void save(String path, boolean state) {
        CompletableFuture.runAsync(() -> {
            File config = new File(CONFIG_PATH.toUri());
            String c = null;

            try {
                JSONObject object = new JSONObject();
                if (config.exists()) {
                    c = new String(Files.readAllBytes(Path.of(config.getPath())));
                    object = new JSONObject(c);
                }

                object.put(path, state);

                if (!config.exists()) {
                    config.createNewFile();
                }
                FileWriter writer = new FileWriter(config.getPath());
                writer.write(object.toString());
                writer.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    public static int get(String path, int defaultValue) {
        if (!CONFIG_PATH.toFile().exists()) {
            return defaultValue;
        }

        try {
            File config = new File(CONFIG_PATH.toUri());
            String content = new String(Files.readAllBytes(config.toPath()));
            JSONObject object = new JSONObject(content);
            return object.optInt(path, defaultValue);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static void save(String path, int integer) {
        CompletableFuture.runAsync(() -> {
            File config = new File(CONFIG_PATH.toUri());
            String c = null;

            try {
                JSONObject object = new JSONObject();
                if (config.exists()) {
                    c = new String(Files.readAllBytes(Path.of(config.getPath())));
                    object = new JSONObject(c);
                }

                object.put(path, integer);

                if (!config.exists()) {
                    config.createNewFile();
                }
                FileWriter writer = new FileWriter(config.getPath());
                writer.write(object.toString());
                writer.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }
}