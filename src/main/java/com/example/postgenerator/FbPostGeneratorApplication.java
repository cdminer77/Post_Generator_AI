package com.example.postgenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

@SpringBootApplication
public class FbPostGeneratorApplication {

    public static void main(String[] args) {
        loadDotEnvIfPresent();
        SpringApplication.run(FbPostGeneratorApplication.class, args);
    }

    private static void loadDotEnvIfPresent() {
        try {
            File envFile = new File(".env");
            if (envFile.exists()) {
                List<String> lines = Files.readAllLines(envFile.toPath());
                for (String line : lines) {
                    String trimmed = line.trim();
                    if (!trimmed.startsWith("#") && trimmed.contains("=")) {
                        int eqIdx = trimmed.indexOf('=');
                        String key = trimmed.substring(0, eqIdx).trim();
                        String value = trimmed.substring(eqIdx + 1).trim();
                        if (System.getProperty(key) == null && System.getenv(key) == null) {
                            System.setProperty(key, value);
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }
}
