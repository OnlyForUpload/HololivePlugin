package tech.ice.plugins.HololivePlugin.bungee;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Cooldown {
    private static final Map<UUID, Instant> map = new HashMap<>();

    public static void setCooldown(UUID key) {
        map.put(key, Instant.now().plus(Duration.ofSeconds(10)));
    }

    public static boolean hasCooldown(UUID key) {
        Instant cooldown = map.get(key);
        return cooldown != null && Instant.now().isBefore(cooldown);
    }

    public static Instant removeCooldown(UUID key) {
        return map.remove(key);
    }

    public static Duration getRemainingCooldown(UUID key) {
        Instant cooldown = map.get(key);
        Instant now = Instant.now();
        if (cooldown != null && now.isBefore(cooldown)) {
            return Duration.between(now, cooldown);
        } else {
            return Duration.ZERO;
        }
    }
}
