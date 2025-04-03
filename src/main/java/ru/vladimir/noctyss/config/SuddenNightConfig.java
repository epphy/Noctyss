package ru.vladimir.noctyss.config;

import eu.endercentral.crazy_advancements.advancement.AdvancementDisplay;
import eu.endercentral.crazy_advancements.advancement.ToastNotification;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import ru.vladimir.noctyss.utility.LoggerUtility;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public final class SuddenNightConfig implements IConfig {
    private static final String FILE_NAME = "SuddenNight.yml";

    // Sections
    private static final String SETTINGS = "settings.";
    private static final String LIGHT_SETTINGS = SETTINGS + "light.";
    private static final String SOUND_SETTINGS = SETTINGS + "sound.";
    private static final String NOTIFICATION_SETTINGS = SETTINGS + "notification.";

    // Dependency
    private final @NonNull JavaPlugin plugin;

    // Configs
    private File file;
    private FileConfiguration fileConfig;

    // General
    private boolean eventEnabled;
    private double eventChance;
    private long checkFrequencyTicks;
    private long cooldownDays;

    // Sound
    private final long[] ambientPlayFrequencyTicks = {2400L, 3600L};
    private final long[] ambientPlayDelayTicks = {200L, 1200L};
    private final long ambientStopFrequency = 20L;
    private final Sound rewindSound = Sound.UI_TOAST_IN;

    private boolean musicEnabled;
    private final List<Sound> allowedSounds = new ArrayList<>();
    private final Set<Sound> disallowedSounds = new HashSet<>();

    // Time
    private final long nightModifyFrequency = 100L;
    private final long[] nightLength = {2400L, 8400L};

    // Light
    private boolean lightDimEnabled;

    // Notification
    private boolean endToastEnabled;
    private boolean endToastOneTime;
    private ToastNotification endToast;

    @Override
    public void load() {
        save();
        parse();
    }

    private void save() {
        if (file == null) {
            file = new File(plugin.getDataFolder(), FILE_NAME);
        }

        if (!file.exists()) {
            plugin.saveResource(FILE_NAME, false);
        }

        fileConfig = YamlConfiguration.loadConfiguration(file);
    }

    private void parse() {
        loadInternalSettings();

        parseGeneral();
        parseLight();
        parseSound();
        parseNotification();
    }

    //    INTERNAL
    private void loadInternalSettings() {
        Registry<@NotNull Sound> soundRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.SOUND_EVENT);
        loadAllowedSounds(soundRegistry);
        loadDisallowedSounds(soundRegistry);
    }

    private void loadAllowedSounds(@NotNull Registry<@NotNull Sound> soundRegistry) {
        allowedSounds.addAll(soundRegistry.stream()
                .map(soundRegistry::getKey)
                .filter(Objects::nonNull)
                .map(NamespacedKey::toString)
                .filter(this::isAllowedSound)
                .map(NamespacedKey::fromString)
                .filter(Objects::nonNull)
                .map(soundRegistry::get)
                .collect(Collectors.toSet())
        );
    }

    private boolean isAllowedSound(@NotNull String soundName) {
        return soundName.startsWith("minecraft:music.nether");
    }

    private void loadDisallowedSounds(@NotNull Registry<@NotNull Sound> soundRegistry) {
        disallowedSounds.addAll(soundRegistry.stream()
                .map(soundRegistry::getKey)
                .filter(Objects::nonNull)
                .map(NamespacedKey::toString)
                .filter(this::isDisallowedSound)
                .map(NamespacedKey::fromString)
                .filter(Objects::nonNull)
                .map(soundRegistry::get)
                .collect(Collectors.toSet()));
        addHardcodedDisallowedSounds();
    }

    private boolean isDisallowedSound(@NotNull String soundName) {
        return soundName.startsWith("minecraft:music_disc") ||
               soundName.startsWith("minecraft:music.overworld");
    }

    private void addHardcodedDisallowedSounds() {
        disallowedSounds.addAll(Set.of(
                Sound.MUSIC_CREATIVE, Sound.MUSIC_GAME, Sound.MUSIC_CREDITS, Sound.MUSIC_DRAGON,
                Sound.MUSIC_END, Sound.MUSIC_MENU, Sound.MUSIC_UNDER_WATER)
        );
    }

    //    EXTERNAL
    private void parseGeneral() {
        eventEnabled = fileConfig.getBoolean(SETTINGS + "enabled", true);
        eventChance = fileConfig.getDouble(SETTINGS + "event-chance", 0.05);
        checkFrequencyTicks = fileConfig.getInt(SETTINGS + "check-frequency", 3000);
        cooldownDays = fileConfig.getInt(SETTINGS + "cooldown-in-days", 10);
    }

    private void parseLight() {
        lightDimEnabled = fileConfig.getBoolean(LIGHT_SETTINGS + "dim-light", true);
    }

    private void parseSound() {
        musicEnabled = fileConfig.getBoolean(SOUND_SETTINGS + "music", true);
    }

    private void parseNotification() {
        ConfigurationSection section = fileConfig.getConfigurationSection(NOTIFICATION_SETTINGS + "end");
        if (section == null) {
            LoggerUtility.warn(this, "End notification section is missing, using defaults.");
            endToastEnabled = true;
            endToastOneTime = true;
            endToast = new ToastNotification(Material.CLOCK, "Did it seem to me?", AdvancementDisplay.AdvancementFrame.TASK);
            return;
        }
        endToastEnabled = section.getBoolean("enabled", true);
        endToastOneTime = section.getBoolean("one-time", true);
        endToast = getToastNotification(section);
    }

    @NotNull
    private ToastNotification getToastNotification(@NotNull ConfigurationSection section) {
        Material icon = getIcon(section.getString("icon", "clock"), Material.CLOCK);
        String text = section.getString("text", "Did it seem to me?");
        AdvancementDisplay.AdvancementFrame frame = getFrame(section.getString("frame", "task"), AdvancementDisplay.AdvancementFrame.TASK);
        return new ToastNotification(icon, text, frame);
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

    @NotNull
    private AdvancementDisplay.AdvancementFrame getFrame(@NotNull String frameName, @NotNull AdvancementDisplay.AdvancementFrame defaultFrame) {
        try {
            if (frameName.isBlank()) {
                LoggerUtility.warn(this, "Invalid frame for end toast: empty string");
                return defaultFrame;
            }
            return AdvancementDisplay.AdvancementFrame.valueOf(frameName.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            LoggerUtility.warn(this, "Invalid frame for end toast: %s".formatted(frameName));
            return defaultFrame;
        }
    }

    @Override
    public void reload() {
        load();
    }
}
