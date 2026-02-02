//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.konpekiestudios.konpekistudios.hytale.plugins.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class ConfigManager {
    private ConfigData configData;
    private File configFile;

    public void setup() {
        File folder = new File("KonpekiEconomy");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        this.configFile = new File(folder, "config.yml");
        if (!this.configFile.exists()) {
            try (InputStream in = this.getClass().getResourceAsStream("/config.yml")) {
                Files.copy(in, this.configFile.toPath(), new CopyOption[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        this.load();
    }

    public void load() {
        LoaderOptions options = new LoaderOptions();
        Constructor constructor = new Constructor(ConfigData.class, options);
        Yaml yaml = new Yaml(constructor);

        try (InputStream inputStream = new FileInputStream(this.configFile)) {
            this.configData = (ConfigData)yaml.loadAs(inputStream, ConfigData.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public ConfigData getData() {
        return this.configData;
    }
}
