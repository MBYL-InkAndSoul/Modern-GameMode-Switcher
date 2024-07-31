package ins.gms;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.bsideup.jabel.Desugar;
import com.google.gson.Gson;

public class GMS_Config {

    public static final String CONFIG_FILE = "./config/gms.config.json";
    private static final Logger LOG = LogManager.getLogger(GMS_Config.class);
    private static Config CONFIG;
    private static final Config DEFAULT_CONFIG = new Config(
        "/gamemode survival",
        "/gamemode creative",
        "/gamemode adventure",
        (byte) 0);

    /**
     * @param Enum$iconStyle_0_vanilla_1_nei 0 = vanilla, 1 = nei
     */
    @Desugar
    private record Config(String String$survivalCommand, String String$creativeCommand, String String$adventureCommand,
        byte Enum$iconStyle_0_vanilla_1_nei) {}

    public static void _readConfig() {
        File configFile = new File(CONFIG_FILE);
        if (checkConfigFileAvailability(configFile)) {
            Gson gson = new Gson();
            // read config file
            for (int i = 0; i < 3; i++) {
                try {
                    BufferedReader json = Files.newBufferedReader(configFile.toPath());
                    CONFIG = gson.fromJson(json, Config.class);
                    json.close();
                    break; // success, exit loop
                } catch (IOException e) {
                    LOG.error("Error reading config file. Trying again... time: {}", i + 1);
                    // try again
                }
            }
        } else {
            // use default config file
            CONFIG = DEFAULT_CONFIG;
        }
    }

    public static String survivalCommand() {
        return CONFIG.String$survivalCommand();
    }

    public static String creativeCommand() {
        return CONFIG.String$creativeCommand();
    }

    public static String adventureCommand() {
        return CONFIG.String$adventureCommand();
    }

    public static byte iconStyle() {
        return CONFIG.Enum$iconStyle_0_vanilla_1_nei();
    }

    private static boolean checkConfigFileAvailability(File file) {
        return file.exists() && file.isFile() && file.canRead();
    }

    private static void checkConfigInvalidity(Config config) {
        if (config == null) {
            throw new RuntimeException("Config file not found or invalid.");
        } else {
            if (config.Enum$iconStyle_0_vanilla_1_nei < 0 || config.Enum$iconStyle_0_vanilla_1_nei > 1) {
                throw new RuntimeException("Invalid icon style.");
            }
        }
    }

}
