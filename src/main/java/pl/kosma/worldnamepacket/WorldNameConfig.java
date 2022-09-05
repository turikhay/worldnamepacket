package pl.kosma.worldnamepacket;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class WorldNameConfig {
    private final Properties properties;

    private WorldNameConfig(Properties properties) {
        this.properties = properties;
    }

    public String getNamePrefix() {
        return properties.getProperty("prefix");
    }

    public static WorldNameConfig load(Path path) {
        Properties properties;
        try {
            properties = loadProperties(path);
        } catch (IOException e) {
            throw new RuntimeException("Cannot load config", e);
        }
        return new WorldNameConfig(properties);
    }

    private static Properties loadProperties(Path path) throws IOException {
        Properties properties = new Properties();
        if (Files.exists(path)) {
            try (InputStreamReader reader = new InputStreamReader(Files.newInputStream(path), StandardCharsets.UTF_8)) {
                properties.load(reader);
            }
        } else {
            populateDefaultConfig(properties);
            saveConfig(path, properties);
        }
        return properties;
    }

    private static void populateDefaultConfig(Properties properties) {
        properties.put("prefix", "default");
    }

    private static void saveConfig(Path path, Properties properties) throws IOException {
        createDirectoriesFor(path);
        try (OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(path), StandardCharsets.UTF_8)) {
            properties.store(writer, null);
        }
    }

    private static void createDirectoriesFor(Path path) throws IOException {
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
    }
}
