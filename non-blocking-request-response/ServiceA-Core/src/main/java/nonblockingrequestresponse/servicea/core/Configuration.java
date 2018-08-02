package nonblockingrequestresponse.servicea.core;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Configuration {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Object lock = new Object();

    private String hostname;

    private int port;

    private String serviceUrl;

    private final HashMap<String, Object> subConfigurations = new HashMap<>();

    public Configuration() {
        //Used for serialization purposes
    }

    public Configuration(String source){
        this.source = source;
    }

    public Configuration parse(){
        Gson gson = new Gson();
        Configuration configuration = gson.fromJson(this.source, Configuration.class);
        configuration.source = this.source;
        return configuration;
    }

    public String getServiceUrl() {
        return this.serviceUrl;
    }

    public String getHostname() {
        return this.hostname;
    }

    public int getPort() {
        return this.port;
    }

    private String source;

    public  <TConfiguration> TConfiguration getSubConfiguration(String key, Class<TConfiguration> type){
        synchronized (lock) {
            if (subConfigurations.containsKey(key)) {
                return (TConfiguration) subConfigurations.get(key);
            }

            Gson gson = new Gson();
            JsonElement root = new JsonParser().parse(this.source);
            TConfiguration configObject = gson.fromJson(root.getAsJsonObject().get(key), type);
            subConfigurations.put(key, configObject);

            return configObject;
        }
    }

    public synchronized <TConfiguration> void addSubConfiguration(String key, TConfiguration config){
        synchronized (lock) {
            subConfigurations.put(key, config);
        }
    }

    public static Configuration readConfiguration() throws IOException {
        Map<String, String> env = System.getenv();

        String configPath = env.get("config");

        MDC.put("configPath", configPath);
        if (configPath != null && !configPath.isEmpty()) {
            String config = new String(Files.readAllBytes(Paths.get(configPath)),
                    StandardCharsets.UTF_8);
            Configuration configuration = new Configuration(config).parse();

            MDC.put("config", config);
            logger.info("Configuration loaded from environment");
            return configuration;
        } else {
            String message = "Missing Configuration";
            logger.error(message, configPath);
            throw new RuntimeException(message);
        }
    }
}
