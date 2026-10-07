package com.example.examplemod.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/** Iron Citadel: 180 x 28 blocks. Bow +Z, waterline Y=0. Geometry loads once. */
public final class TestEntityShipModel {
    public enum Material {
        HULL, DECK, DARK, RED, WOOD, WINDOW, WHITE, LIGHT, GREEN, BRONZE,
        WOOD_LIGHT, WOOD_DARK, WATERLINE, RUST, STEEL, BLACK, ROPE, GUN
    }

    public record Part(Material material, float x, float y, float z,
                       float width, float height, float length,
                       float yaw, float pitch, float roll) {}

    public static final List<Part> PARTS = load();

    private TestEntityShipModel() {}

    private static List<Part> load() {
        String path = "/assets/mod1/models/test_entity_warship.geometry.json";
        var stream = TestEntityShipModel.class.getResourceAsStream(path);
        if (stream == null) throw new IllegalStateException("Missing warship model: " + path);
        try (var reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            var model = JsonParser.parseReader(reader).getAsJsonObject();
            List<Part> parts = new ArrayList<>();
            for (var element : model.getAsJsonArray("parts")) {
                JsonObject p = element.getAsJsonObject();
                float w = number(p, "width");
                float h = number(p, "height");
                float d = number(p, "length");
                if (w <= 0 || h <= 0 || d <= 0) {
                    throw new IllegalStateException("Invalid warship part dimensions");
                }
                parts.add(new Part(Material.valueOf(p.get("material").getAsString()),
                        number(p, "x"), number(p, "y"), number(p, "z"), w, h, d,
                        number(p, "yaw"), number(p, "pitch"), number(p, "roll")));
            }
            if (parts.isEmpty()) throw new IllegalStateException("Empty warship model");
            return List.copyOf(parts);
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot read warship model: " + path, exception);
        }
    }

    private static float number(JsonObject part, String key) {
        float value = part.get(key).getAsFloat();
        if (!Float.isFinite(value)) throw new IllegalStateException("Invalid model coordinate: " + key);
        return value;
    }
}
