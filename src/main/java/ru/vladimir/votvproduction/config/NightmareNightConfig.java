package ru.vladimir.votvproduction.config;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import java.io.File;
import java.util.List;
import java.util.Objects;

@Getter
@RequiredArgsConstructor
public class NightmareNightConfig implements Config {
    private static final String FILE_CONFIG_NAME = "NightmareNight.yml";
    private static final String SETTINGS = "settings.";
    private static final String EFFECT_SETTINGS = "settings.effect.";
    private final JavaPlugin plugin;
    private FileConfiguration fileConfig;
    private File file;
    private long checkFrequency;
    private List<World> allowedWorlds;
    private int eventChance;
    private long effectGiveFrequency;
    private List<PotionEffect> effects;

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
        checkFrequency = fileConfig.getInt(SETTINGS + "check-frequency", 100);
        allowedWorlds = getAllowedWorlds(
                fileConfig.getStringList(SETTINGS + "allowed-worlds"));
        eventChance = fileConfig.getInt(SETTINGS + "event-chance", 5);
        effectGiveFrequency = fileConfig.getInt(EFFECT_SETTINGS + "give-effect-frequency", 200);
        effects = getEffects(
                fileConfig.getStringList(EFFECT_SETTINGS + "effects"));
    }

    private List<World> getAllowedWorlds(List<String> worldNames) {
        return worldNames.stream()
                .map(Bukkit::getWorld)
                .filter(Objects::nonNull)
                .toList();
    }

    private List<PotionEffect> getEffects(List<String> effectNames) {
        return effectNames.stream()
                .filter(Objects::nonNull)
                .map(this::getEffectKey)
                .map(key -> RegistryAccess.registryAccess().getRegistry(RegistryKey.MOB_EFFECT).get(key))
                .filter(Objects::nonNull)
                .map(type -> new PotionEffect(type, (int) effectGiveFrequency + 100, 0))
                .toList();
    }

    private NamespacedKey getEffectKey(String effectName) {
        return new NamespacedKey("minecraft", effectName.toLowerCase().trim());
    }

    @Override
    public void reload() {
        load();
    }
}
