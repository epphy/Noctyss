package ru.vladimir.noctyss.config;

import eu.endercentral.crazy_advancements.advancement.AdvancementDisplay;
import eu.endercentral.crazy_advancements.advancement.ToastNotification;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import ru.vladimir.noctyss.utility.LoggerUtility;

import java.io.File;
import java.util.List;

@Getter
@RequiredArgsConstructor
public final class NightmareNightConfig implements IConfig {
    private static final String FILE_CONFIG_NAME = "NightmareNight.yml";

    // Sections
    private static final String SETTINGS = "settings.";
    private static final String LIGHT_SETTINGS = "settings.light.";
    private static final String TIME_SETTINGS = "settings.time.";
    private static final String NOTIFICATION_SETTINGS = "settings.notification.";

    // Dependency
    private final @NonNull JavaPlugin plugin;

    // Configs
    private FileConfiguration fileConfig;
    private File file;

    // General
    private boolean eventEnabled;
    private long checkFrequency;
    private int eventChance;

    // Light
    private boolean darkness;
    private final long darknessGiveFrequency = 300L;

    // Sound
    private final long soundPlayFrequency = 1200L;
    private final List<Sound> sounds = List.of(Sound.AMBIENT_CAVE);

    // Time
    private final long nightLength = 22000L;
    private long timeModifyFrequency;

    // Spawn rate
    private final int monsterMultiplier = 2;

    // Notification
    private boolean notificationsEnabled;
    private boolean endToastOneTime;
    private ToastNotification endToast;

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
        parseLightSettings();
        parseTimeSettings();
        parseNotificationSettings();
    }

    private void parseGeneralSettings() {
        eventEnabled = fileConfig.getBoolean(SETTINGS + "enabled", true);
        checkFrequency = fileConfig.getInt(SETTINGS + "check-frequency", 100);
        eventChance = fileConfig.getInt(SETTINGS + "event-chance", 5);
    }

    private void parseLightSettings() {
        darkness = fileConfig.getBoolean(LIGHT_SETTINGS + "darkness", false);
    }

    private void parseTimeSettings() {
        timeModifyFrequency = fileConfig.getInt(TIME_SETTINGS + "time-modification-frequency", 100);
    }

    private void parseNotificationSettings() {
        notificationsEnabled = fileConfig.getBoolean(NOTIFICATION_SETTINGS + "enabled", true);
        parseEndNotification();
    }

    private void parseEndNotification() {
        ConfigurationSection section = fileConfig.getConfigurationSection(NOTIFICATION_SETTINGS + "end");

        if (section == null) {
            LoggerUtility.warn(this, "End notification section is missing, using default values.");
            endToastOneTime = true;
            endToast = new ToastNotification(Material.COAL_BLOCK, "What was that?", AdvancementDisplay.AdvancementFrame.TASK);
            return;
        }

        endToastOneTime = section.getBoolean("one-time", true);
        endToast = getToastNotification(section);
    }

    @NotNull
    private ToastNotification getToastNotification(@NotNull ConfigurationSection section) {
        Material icon = getIcon(section.getString("icon", "COAL_BLOCK"), Material.COAL_BLOCK);
        String text = section.getString("text", "What was that?");
        AdvancementDisplay.AdvancementFrame frame = getFrame(section.getString("frame", "TASK"), AdvancementDisplay.AdvancementFrame.TASK);
        return new ToastNotification(icon, text, frame);
    }

    @NotNull
    private AdvancementDisplay.AdvancementFrame getFrame(@NotNull String frameName, @NotNull AdvancementDisplay.AdvancementFrame defaultFrame) {
        try {
            if (frameName.isBlank()) {
                LoggerUtility.warn(this, "Invalid frame for end toast: empty string");
                return defaultFrame;
            }
            return AdvancementDisplay.AdvancementFrame.valueOf(frameName);
        } catch (IllegalArgumentException e) {
            LoggerUtility.warn(this, "Invalid frame for end toast: %s".formatted(frameName));
            return defaultFrame;
        }
    }

    @NotNull
    private Material getIcon(@NotNull String materialName, @NotNull Material defaultMaterial) {
        try {
            if (materialName.isBlank()) {
                LoggerUtility.warn(this, "Invalid icon for end toast: empty string");
                return defaultMaterial;
            }
            return Material.valueOf(materialName.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            LoggerUtility.warn(this, "Invalid icon for end toast: %s".formatted(materialName));
            return defaultMaterial;
        }
    }

    @Override
    public void reload() {
        load();
    }
}
