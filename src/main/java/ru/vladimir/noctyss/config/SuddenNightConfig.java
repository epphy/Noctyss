package ru.vladimir.noctyss.config;

import eu.endercentral.crazy_advancements.advancement.AdvancementDisplay;
import eu.endercentral.crazy_advancements.advancement.ToastNotification;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import lombok.Getter;
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
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public final class SuddenNightConfig implements AbstractConfig {
    private static final String FILE_NAME = "SuddenNight.yml";
    private static final String SETTINGS = "settings.";
    private static final String TIME_SETTINGS = SETTINGS + "time.";
    private static final String EFFECT_SETTINGS = SETTINGS + "effect.";
    private static final String SOUND_SETTINGS = SETTINGS + "sound.";
    private static final String NOTIFICATION_SETTINGS = SETTINGS + "notification.";
    private final JavaPlugin plugin;
    private File file;
    private FileConfiguration fileConfig;
    private final Set<Sound> disallowedSounds = new HashSet<>();
    private final Sound rewindSound = Sound.UI_TOAST_IN;
    private final long ambientStopFrequency = 20L;
    private boolean isEventEnabled;
    private double eventChance;
    private long checkFrequencyTicks;
    private long cooldownDays;
    private long nightLength;
    private long timeUpdateFrequencyTicks;
    private boolean isFadeEffectEnabled;
    private boolean isMusicEnabled;
    private boolean isEndToastEnabled;
    private boolean isEndToastOneTime;
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
        parseTime();
        parseEffect();
        parseSound();
        parseNotification();
    }

    private void loadInternalSettings() {
        loadDisallowedSounds();
    }

    private void loadDisallowedSounds() {
        loadSoundsFromRegistry();
        loadHardcodedSounds();
    }

    private void loadSoundsFromRegistry() {
        final Registry<@NotNull Sound> soundRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.SOUND_EVENT);
        disallowedSounds.addAll(soundRegistry.stream()
                .map(soundRegistry::getKey)
                .filter(Objects::nonNull)
                .map(NamespacedKey::toString)
                .filter(this::isDisallowedSound)
                .map(NamespacedKey::fromString)
                .filter(Objects::nonNull)
                .map(soundRegistry::get)
                .collect(Collectors.toSet()));
    }

    private boolean isDisallowedSound(String soundName) {
        return soundName.startsWith("minecraft:music_disc") ||
                soundName.startsWith("minecraft:music_overworld");
    }

        private void loadHardcodedSounds() {
        disallowedSounds.addAll(Set.of(
                Sound.MUSIC_CREATIVE, Sound.MUSIC_GAME, Sound.MUSIC_CREDITS, Sound.MUSIC_DRAGON,
                Sound.MUSIC_END, Sound.MUSIC_MENU, Sound.MUSIC_UNDER_WATER));
    }

    private void parseGeneral() {
        isEventEnabled = fileConfig.getBoolean(SETTINGS + "enabled", true);
        eventChance = fileConfig.getDouble(SETTINGS + "event-chance", 0.05);
        checkFrequencyTicks = fileConfig.getInt(SETTINGS + "check-frequency", 3000);
        cooldownDays = fileConfig.getInt(SETTINGS + "cooldown-in-days", 10);
    }

    private void parseTime() {
        nightLength = fileConfig.getInt(TIME_SETTINGS + "night-length", 2400);
        timeUpdateFrequencyTicks = fileConfig.getInt(TIME_SETTINGS + "time-update-frequency", 50);
    }

    private void parseEffect() {
        isFadeEffectEnabled = fileConfig.getBoolean(EFFECT_SETTINGS + "fade-effect", true);
    }

    private void parseSound() {
        isMusicEnabled = fileConfig.getBoolean(SOUND_SETTINGS + "music", true);
    }

    private void parseNotification() {
        parseEndToastNotification();
    }

    private void parseEndToastNotification() {
        final ConfigurationSection section = fileConfig.getConfigurationSection(NOTIFICATION_SETTINGS + "end");
        if (section == null) {
            LoggerUtility.warn(this, "Failed to parse end notification because its section is null");
            isEndToastEnabled = true;
            isEndToastOneTime = true;
            endToast = new ToastNotification(Material.CLOCK, "Did it seem to me?", AdvancementDisplay.AdvancementFrame.TASK);
        } else {
            isEndToastEnabled = section.getBoolean("enabled", true);
            isEndToastOneTime = section.getBoolean("one-time", true);
            endToast = getToastNotification(section);
        }
    }

    @NotNull
    private ToastNotification getToastNotification(ConfigurationSection section) {
        final Material icon = getIcon(section.getString("icon", "clock"));
        final String text = section.getString("text", "Did it seem to me?");
        final AdvancementDisplay.AdvancementFrame frame = getFrame(section.getString("frame", "task"));
        return new ToastNotification(icon, text, frame);
    }

    @NotNull
    private Material getIcon(String materialName) {
        try {
            return Material.valueOf(materialName.toUpperCase().trim());
        } catch (IllegalArgumentException | NullPointerException e) {
            LoggerUtility.warn(this, "Invalid icon for end toast: %s".formatted(materialName));
            return Material.CLOCK;
        }
    }

    @NotNull
    private AdvancementDisplay.AdvancementFrame getFrame(String frameName) {
        try {
            return AdvancementDisplay.AdvancementFrame.valueOf(frameName.toUpperCase().trim());
        } catch (IllegalArgumentException | NullPointerException e) {
            LoggerUtility.warn(this, "Invalid frame for end toast: %s".formatted(frameName));
            return AdvancementDisplay.AdvancementFrame.TASK;
        }
    }

    @Override
    public void reload() {
        load();
    }
}
