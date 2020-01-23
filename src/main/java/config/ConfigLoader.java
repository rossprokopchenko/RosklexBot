package config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ConfigLoader {
    String load(File file) throws IOException {
        return new String(Files.readAllBytes(file.toPath()));
    }
}
