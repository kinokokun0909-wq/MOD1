package com.example.examplemod.client;

import java.util.ArrayList;
import java.util.List;

/** Geometry in block units: bow +Z, length 60, beam 25, maximum visual draft 6. */
public final class WarshipGeometry {
    public enum Material { HULL, DECK, DARK, RED, WOOD, WINDOW, WHITE, LIGHT, GREEN, BRONZE }
    public record Part(Material material, float x, float y, float z,
                       float width, float height, float length,
                       float yaw, float pitch, float roll) {}

    public static final List<Part> PARTS = build();

    private WarshipGeometry() {}

    private static List<Part> build() {
        List<Part> p = new ArrayList<>();
        // Eight underwater sections form a deep rounded bilge with a narrower bottom.
        // The keel rises toward both ends rather than making a rectangular underwater box.
        for (int i = 0; i < 60; i++) {
            float z = -30 + i;
            float half = halfWidth(z + 0.5F);
            float draft = draft(z + 0.5F);
            float sliceHeight = (draft - 0.65F) / 8;
            for (int slice = 0; slice < 8; slice++) {
                float t = (slice + 0.5F) / 8;
                float sectionHalf = half * 0.94F * (1 - 0.72F * t * t);
                float bottom = -0.65F - (slice + 1) * sliceHeight;
                box(p, Material.RED, -sectionHalf, bottom, z, sectionHalf * 2, sliceHeight, 1);
            }
            box(p, Material.DARK, -half * 0.94F, -0.65F, z, half * 1.88F, 0.75F, 1);
            box(p, Material.HULL, -half, 0.1F, z, half * 2, 1.2F, 1);
            box(p, Material.DECK, -half + 0.12F, 1.3F, z, half * 2 - 0.24F, 0.12F, 1);
        }
        // A central keel rib and two bronze five-blade propellers under the stern.
        box(p, Material.RED, -0.6F, -6.0F, -20, 1.2F, 0.18F, 38);
        for (float x : new float[]{-3.4F, 3.4F}) {
            box(p, Material.DARK, x - 0.13F, -3.52F, -29.5F, 0.26F, 0.26F, 5.5F);
            box(p, Material.BRONZE, x - 0.3F, -3.69F, -29.8F, 0.6F, 0.6F, 0.4F);
            for (int blade = 0; blade < 5; blade++) {
                float angle = blade * 72;
                double radians = Math.toRadians(angle);
                centered(p, Material.BRONZE,
                        x - (float) Math.sin(radians) * 0.65F,
                        -3.39F + (float) Math.cos(radians) * 0.65F,
                        -29.6F, 0.34F, 1.25F, 0.18F, 0, 0, angle);
            }
        }
        // Rudder and its stock stay inside the original sixty-block length.
        box(p, Material.DARK, -0.12F, -4.0F, -28.7F, 0.24F, 2.2F, 0.24F);
        box(p, Material.RED, -0.18F, -5.2F, -29.6F, 0.36F, 2.1F, 1.5F);
        // Teak-colored walkways: narrow repeated boards, rather than a stretched texture.
        for (int i = 0; i < 13; i++) {
            box(p, Material.WOOD, -5.7F + i * 0.88F, 1.421F, -24, 0.82F, 0.035F, 47);
        }
        // Railings follow the curved perimeter, leaving the bow visibly tapered.
        for (int i = 0; i < 20; i++) {
            float z1 = -30 + i * 3 + 0.1F;
            float z2 = Math.min(29.9F, z1 + 3);
            for (int side : new int[]{-1, 1}) {
                float x1 = side * (halfWidth(z1) - 0.16F);
                float x2 = side * (halfWidth(z2) - 0.16F);
                box(p, Material.HULL, x1 - 0.055F, 1.42F, z1 - 0.055F, 0.11F, 0.86F, 0.11F);
                rail(p, x1, z1, x2, z2, 2.22F);
                rail(p, x1, z1, x2, z2, 1.86F);
            }
        }
        rail(p, -8.8F, -29.8F, 8.8F, -29.8F, 2.22F);
        rail(p, -8.8F, -29.8F, 8.8F, -29.8F, 1.86F);

        // Central aft superstructure: four stepped levels and bridge windows.
        box(p, Material.HULL, -4.2F, 1.46F, -12, 8.4F, 2.0F, 10);
        box(p, Material.DARK, -4.35F, 3.46F, -12.15F, 8.7F, 0.15F, 10.3F);
        box(p, Material.HULL, -3.25F, 3.61F, -9.5F, 6.5F, 1.9F, 7.0F);
        box(p, Material.DECK, -3.4F, 5.51F, -9.65F, 6.8F, 0.15F, 7.3F);
        box(p, Material.HULL, -2.7F, 5.66F, -8.2F, 5.4F, 1.85F, 5.2F);
        box(p, Material.DARK, -2.9F, 7.51F, -8.4F, 5.8F, 0.18F, 5.6F);
        box(p, Material.HULL, -1.7F, 7.69F, -7.6F, 3.4F, 0.8F, 3.2F);
        // Separate dark frames and blue panes give the bridge readable detail.
        for (int i = 0; i < 5; i++) {
            box(p, Material.DARK, -2.45F + i * 1.0F, 6.25F, -3.02F, 0.85F, 0.82F, 0.08F);
            box(p, Material.WINDOW, -2.38F + i * 1.0F, 6.34F, -2.97F, 0.71F, 0.64F, 0.055F);
        }
        for (int side : new int[]{-1, 1}) {
            for (int i = 0; i < 4; i++) {
                box(p, Material.WINDOW, side < 0 ? -2.73F : 2.7F, 6.35F, -7.7F + i * 1.05F,
                        0.04F, 0.6F, 0.75F);
            }
            for (int i = 0; i < 7; i++) {
                box(p, Material.DARK, side < 0 ? -4.24F : 4.2F, 2.1F, -11.2F + i * 1.3F,
                        0.05F, 0.38F, 0.5F);
            }
            // Walkway wings and their outer guard rails.
            box(p, Material.HULL, side < 0 ? -5.0F : 3.25F, 5.36F, -4.5F, 1.75F, 0.15F, 1.6F);
            box(p, Material.HULL, side < 0 ? -5.0F : 4.9F, 5.51F, -4.5F, 0.1F, 0.55F, 1.6F);
        }
        box(p, Material.DARK, -0.6F, 1.46F, -2.02F, 1.2F, 1.45F, 0.06F);
        box(p, Material.WHITE, 0.35F, 2.1F, -1.97F, 0.12F, 0.12F, 0.04F);
        // Pilot station is immediately forward of the bridge, at the entity origin.
        box(p, Material.HULL, -0.65F, 1.46F, -1.15F, 1.3F, 0.7F, 0.4F);
        box(p, Material.WINDOW, -0.48F, 2.16F, -1.07F, 0.96F, 0.045F, 0.25F);

        // Two capped funnels, with vent ribs and black mouths.
        for (float z : new float[]{-15.3F, -19.4F}) {
            box(p, Material.HULL, -1.65F, 1.46F, z - 1.0F, 3.3F, 3.1F, 2.0F);
            box(p, Material.DARK, -1.75F, 4.56F, z - 1.1F, 3.5F, 0.55F, 2.2F);
            box(p, Material.DARK, -1.35F, 5.11F, z - 0.7F, 2.7F, 0.1F, 1.4F);
            for (int i = 0; i < 5; i++) {
                box(p, Material.DARK, -1.68F, 2.5F + i * 0.28F, z - 0.75F, 0.04F, 0.1F, 1.5F);
                box(p, Material.DARK, 1.64F, 2.5F + i * 0.28F, z - 0.75F, 0.04F, 0.1F, 1.5F);
            }
        }

        // Tripod-style main mast with bracing and two radar arrays.
        box(p, Material.HULL, -0.16F, 8.49F, -6.1F, 0.32F, 4.71F, 0.32F);
        centered(p, Material.HULL, -0.85F, 7.2F, -6.05F, 0.16F, 5.0F, 0.16F, 0, 0, -18);
        centered(p, Material.HULL, 0.85F, 7.2F, -6.05F, 0.16F, 5.0F, 0.16F, 0, 0, 18);
        box(p, Material.HULL, -3.0F, 10.2F, -6.1F, 6.0F, 0.13F, 0.18F);
        box(p, Material.HULL, -1.8F, 11.9F, -6.1F, 3.6F, 0.12F, 0.18F);
        box(p, Material.DARK, -1.15F, 10.6F, -6.3F, 2.3F, 0.85F, 0.14F);
        for (int i = 0; i < 6; i++) {
            box(p, Material.HULL, -1.05F + i * 0.4F, 10.68F, -6.34F, 0.07F, 0.7F, 0.05F);
        }
        box(p, Material.HULL, -0.65F, 12.4F, -6.23F, 1.3F, 0.45F, 0.13F);
        box(p, Material.LIGHT, -0.2F, 12.9F, -6.14F, 0.4F, 0.15F, 0.4F);
        // Red/green navigation lamps at the bridge wings.
        box(p, Material.RED, -5.0F, 6.05F, -3.6F, 0.25F, 0.25F, 0.25F);
        box(p, Material.GREEN, 4.75F, 6.05F, -3.6F, 0.25F, 0.25F, 0.25F);

        // Three triple-gun main turrets. Rear turret points toward the stern.
        turret(p, 0, 19.0F, 1.46F, 0);
        turret(p, 0, 9.0F, 2.3F, 0);
        turret(p, 0, -22.0F, 1.46F, 180);
        disc(p, Material.HULL, 0, 1.46F, 9.0F, 3.2F, 0.84F);
        // Secondary twin guns down each side, clear of the central walkway.
        for (int side : new int[]{-1, 1}) {
            for (float z : new float[]{6.0F, -5.0F, -15.5F}) {
                smallTurret(p, side * 8.2F, z, side * 45);
            }
            // Raised anti-air mounts and paired small barrels.
            for (float z : new float[]{1.0F, -10.0F, -20.0F}) {
                float x = side * 10.4F;
                disc(p, Material.HULL, x, 1.46F, z, 0.85F, 0.3F);
                box(p, Material.DARK, x - 0.3F, 1.76F, z - 0.25F, 0.6F, 0.55F, 0.5F);
                box(p, Material.HULL, x - 0.27F, 2.08F, z, 0.12F, 0.12F, 1.1F);
                box(p, Material.HULL, x + 0.15F, 2.08F, z, 0.12F, 0.12F, 1.1F);
            }
            lifeboat(p, side * 6.35F, -10.4F);
            lifeboat(p, side * 6.35F, -17.0F);
            // Davit supports over the boats.
            for (float z : new float[]{-10.4F, -17.0F}) {
                box(p, Material.HULL, side * 5.0F - 0.08F, 1.46F, z, 0.16F, 2.2F, 0.16F);
                box(p, Material.HULL, side < 0 ? -6.9F : 5.0F, 3.66F, z, 1.9F, 0.16F, 0.16F);
            }
        }
        // Bow fittings, bollards and paired anchor housings.
        for (int side : new int[]{-1, 1}) {
            box(p, Material.DARK, side * 1.1F - 0.14F, 1.46F, 27.0F, 0.28F, 0.45F, 0.28F);
            box(p, Material.HULL, side * 1.1F - 0.27F, 1.83F, 26.96F, 0.54F, 0.12F, 0.36F);
            box(p, Material.DARK, side < 0 ? -5.0F : 4.5F, 0.6F, 24.0F, 0.5F, 0.6F, 0.5F);
            for (float z : new float[]{-27.0F, 23.0F}) {
                box(p, Material.DARK, side * 5.1F - 0.2F, 1.46F, z, 0.4F, 0.35F, 0.4F);
            }
        }
        return List.copyOf(p);
    }

    private static float halfWidth(float z) {
        if (z < -18) return 9 + 3.5F * (float) Math.sqrt(Math.max(0, (z + 30) / 12));
        if (z <= 8) return 12.5F;
        return 12.5F * (float) Math.pow(Math.max(0.001F, (30 - z) / 22), 0.65);
    }

    private static float draft(float z) {
        if (z < -20) return 3.2F + 2.8F * (z + 30) / 10;
        if (z > 18) return 1.5F + 4.5F * (float) Math.pow(Math.max(0, (30 - z) / 12), 0.65);
        return 6.0F;
    }

    private static void turret(List<Part> p, float x, float z, float y, float yaw) {
        disc(p, Material.DARK, x, y, z, 2.8F, 0.3F);
        local(p, Material.HULL, x, y, z, 0, 1.0F, 0, 5.1F, 1.4F, 4.3F, yaw, 0);
        // Sloped front plate and a dark roof rim.
        local(p, Material.HULL, x, y, z, 0, 1.0F, 2.05F, 4.7F, 1.5F, 0.22F, yaw, -18);
        local(p, Material.DECK, x, y, z, 0, 1.77F, -0.1F, 5.2F, 0.14F, 4.0F, yaw, 0);
        for (float offset : new float[]{-1.25F, 0, 1.25F}) {
            local(p, Material.DARK, x, y, z, offset, 1.15F, 2.24F, 0.6F, 0.62F, 0.5F, yaw, 0);
            local(p, Material.HULL, x, y, z, offset, 1.25F, 4.7F, 0.38F, 0.38F, 5.0F, yaw, 0);
            local(p, Material.DARK, x, y, z, offset, 1.25F, 7.24F, 0.46F, 0.46F, 0.18F, yaw, 0);
        }
        local(p, Material.DARK, x, y, z, -1.7F, 1.92F, -0.9F, 0.65F, 0.16F, 0.8F, yaw, 0);
    }

    private static void smallTurret(List<Part> p, float x, float z, float yaw) {
        disc(p, Material.DARK, x, 1.46F, z, 1.45F, 0.24F);
        local(p, Material.HULL, x, 1.46F, z, 0, 0.8F, 0, 2.2F, 1.1F, 2.0F, yaw, 0);
        for (float offset : new float[]{-0.4F, 0.4F}) {
            local(p, Material.HULL, x, 1.46F, z, offset, 0.95F, 1.85F, 0.2F, 0.2F, 2.0F, yaw, 0);
            local(p, Material.DARK, x, 1.46F, z, offset, 0.95F, 2.85F, 0.24F, 0.24F, 0.08F, yaw, 0);
        }
    }

    private static void lifeboat(List<Part> p, float x, float z) {
        box(p, Material.WHITE, x - 0.7F, 1.75F, z - 2.0F, 1.4F, 0.4F, 4.0F);
        box(p, Material.WHITE, x - 0.5F, 1.75F, z + 2.0F, 1.0F, 0.4F, 0.45F);
        box(p, Material.WHITE, x - 0.7F, 2.15F, z - 2.0F, 0.15F, 0.3F, 4.0F);
        box(p, Material.WHITE, x + 0.55F, 2.15F, z - 2.0F, 0.15F, 0.3F, 4.0F);
        box(p, Material.DARK, x - 0.55F, 2.15F, z - 1.85F, 1.1F, 0.02F, 3.7F);
        for (int i = 0; i < 3; i++) box(p, Material.WOOD, x - 0.55F, 2.25F, z - 1.2F + i, 1.1F, 0.12F, 0.22F);
    }

    private static void disc(List<Part> p, Material m, float x, float y, float z, float radius, float height) {
        for (int i = 0; i < 10; i++) {
            float width = radius / 5;
            float offset = -radius + width * (i + 0.5F);
            float halfLength = (float) Math.sqrt(radius * radius - offset * offset);
            box(p, m, x + offset - width / 2, y, z - halfLength, width, height, halfLength * 2);
        }
    }

    private static void rail(List<Part> p, float x1, float z1, float x2, float z2, float y) {
        float length = (float) Math.hypot(x2 - x1, z2 - z1);
        float yaw = (float) Math.toDegrees(Math.atan2(x2 - x1, z2 - z1));
        centered(p, Material.HULL, (x1 + x2) / 2, y, (z1 + z2) / 2,
                0.07F, 0.07F, length, yaw, 0, 0);
    }

    private static void local(List<Part> p, Material m, float x, float y, float z,
                              float ox, float oy, float oz, float w, float h, float d,
                              float yaw, float pitch) {
        double a = Math.toRadians(yaw);
        centered(p, m, x + (float) (Math.cos(a) * ox + Math.sin(a) * oz), y + oy,
                z + (float) (-Math.sin(a) * ox + Math.cos(a) * oz), w, h, d, yaw, pitch, 0);
    }

    private static void box(List<Part> p, Material m, float x, float y, float z, float w, float h, float d) {
        centered(p, m, x + w / 2, y + h / 2, z + d / 2, w, h, d, 0, 0, 0);
    }

    private static void centered(List<Part> p, Material m, float x, float y, float z,
                                 float w, float h, float d, float yaw, float pitch, float roll) {
        p.add(new Part(m, x, y, z, w, h, d, yaw, pitch, roll));
    }
}
