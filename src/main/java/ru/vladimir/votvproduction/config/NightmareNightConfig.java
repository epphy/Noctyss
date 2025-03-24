package ru.vladimir.votvproduction.config;

import eu.endercentral.crazy_advancements.advancement.AdvancementDisplay;
import eu.endercentral.crazy_advancements.advancement.ToastNotification;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.Nullable;
import ru.vladimir.votvproduction.utility.LoggerUtility;

import java.io.File;
import java.util.List;
import java.util.Objects;

@Getter
@RequiredArgsConstructor
public final class NightmareNightConfig implements AbstractConfig {
    private static final String FILE_CONFIG_NAME = "NightmareNight.yml";

    // Sections
    private static final String SETTINGS = "settings.";
    private static final String EFFECT_SETTINGS = "settings.effect.";
    private static final String SOUND_SETTINGS = "settings.sound.";
    private static final String TIME_SETTINGS = "settings.time.";
    private static final String SPAWNRATE_SETTINGS = "settings.spawnrate.";
    private static final String NOTIFICATION_SETTINGS = "settings.notification.";

    // Configs
    private final JavaPlugin plugin;
    private FileConfiguration fileConfig;
    private File file;
    private long checkFrequency;
    private int eventChance;
    private long effectGiveFrequency;
    private List<PotionEffect> effects;
    private long soundPlayFrequency;
    private List<Sound> sounds;
    private long timeModifyFrequency;
    private long nightLength;
    private int monsterMultiplier;
    private Toast endToast;

    @Override
    public void load() {
        save();
        parse();
    }

    private void save() {
        if (file == null) {
            file = new File(plugin.getDataFolder(), FILE_CONFIG_NAME);
        }

        if (!file.exists()) {
            plugin.saveResource(FILE_CONFIG_NAME, false);
        }

        fileConfig = YamlConfiguration.loadConfiguration(file);
    }

    private void parse() {
        parseGeneralSettings();
        parseEffectSettings();
        parseSoundSettings();
        parseTimeSettings();
        parseSpawnrateSettings();
        parseNotificationSettings();
    }

    private void parseGeneralSettings() {
        checkFrequency = fileConfig.getInt(SETTINGS + "check-frequency", 100);
        eventChance = fileConfig.getInt(SETTINGS + "event-chance", 5);
    }

    private void parseEffectSettings() {
        effectGiveFrequency = fileConfig.getInt(EFFECT_SETTINGS + "give-effect-frequency", 200);
        effects = getEffects(
                fileConfig.getStringList(EFFECT_SETTINGS + "effects"));
    }

    private List<PotionEffect> getEffects(List<String> effectNames) {
        return effectNames.stream()
                .filter(Objects::nonNull)
                .map(this::getKey)
                .map(key -> RegistryAccess.registryAccess().getRegistry(RegistryKey.MOB_EFFECT).get(key))
                .filter(Objects::nonNull)
                .map(type -> new PotionEffect(type, (int) effectGiveFrequency + 100, 0))
                .toList();
    }

    private void parseSoundSettings() {
        soundPlayFrequency = fileConfig.getInt(SOUND_SETTINGS + "sound-play-frequency", 1200);
        sounds = getSounds(
                fileConfig.getStringList(SOUND_SETTINGS + "sounds"));
    }

    private List<Sound> getSounds(List<String> soundNames) {
        return soundNames.stream()
                .filter(Objects::nonNull)
                .map(this::getKey)
                .map(key -> RegistryAccess.registryAccess().getRegistry(RegistryKey.SOUND_EVENT).get(key))
                .filter(Objects::nonNull)
                .toList();
    }

    private NamespacedKey getKey(String name) {
        return new NamespacedKey("minecraft", name.toLowerCase().trim());
    }

    private void parseTimeSettings() {
        timeModifyFrequency = fileConfig.getInt(TIME_SETTINGS + "time-modification-frequency", 100);
        nightLength = fileConfig.getInt(TIME_SETTINGS + "night-length", 22000);
    }

    private void parseSpawnrateSettings() {
        monsterMultiplier = fileConfig.getInt(SPAWNRATE_SETTINGS + "monster-multiplier", 2);
    }

    private void parseNotificationSettings() {
        endToast = getEndToast();
    }

    @Nullable
    private Toast getEndToast() {
        final boolean enabled = fileConfig.getBoolean(NOTIFICATION_SETTINGS + "enabled", true);
        final boolean oneTime = fileConfig.getBoolean(NOTIFICATION_SETTINGS + "one-time", true);
        final AdvancementDisplay.AdvancementFrame frame = getFrame(
                fileConfig.getString(NOTIFICATION_SETTINGS + "end.frame", "TASK"));
        final Material icon = getIcon(
                fileConfig.getString(NOTIFICATION_SETTINGS + "end.icon", "COAL_BLOCK"));
        final String text = fileConfig.getString(NOTIFICATION_SETTINGS + "end.text", "What was that?");

        if (frame == null || icon == null) {
            LoggerUtility.warn(this, "Failed to load end toast because either frame or icon are null: %s, %s"
                    .formatted(frame, icon));
            return null;
        }

        return new Toast(enabled, oneTime, new ToastNotification(icon, text, frame));
    }

    @Nullable
    private AdvancementDisplay.AdvancementFrame getFrame(String frameName) {
        try {
            return AdvancementDisplay.AdvancementFrame.valueOf(frameName);
        } catch (IllegalArgumentException | NullPointerException e) {
            LoggerUtility.warn(this, "Failed to load frame for end toast because it is null");
            return null;
        }
    }

    @Nullable
    private Material getIcon(String materialName) {
        try {
            return Material.valueOf(materialName.toUpperCase().trim());
        } catch (IllegalArgumentException | NullPointerException e) {
            LoggerUtility.warn(this, "Failed to load icon for end toast because it is null");
            return null;
        }
    }

    @Override
    public void reload() {
        load();
    }
}
