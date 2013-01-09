package com.fererlab.app;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * acm | 1/4/13 10:38 AM
 */
public class ApplicationDescriptionHandler {

    private ConcurrentHashMap<String, Application> applicationsMap;
    private ConcurrentHashMap<String, String> applicationPathMap;

    private static ApplicationDescriptionHandler instance;

    private ApplicationDescriptionHandler() {
    }

    public static ApplicationDescriptionHandler getInstance() {
        if (instance == null) {
            instance = new ApplicationDescriptionHandler();
        }
        return instance;
    }

    public void reloadApplicationDescriptions(String applicationDescriptionsConfigFile) throws IOException {
        applicationsMap = new ConcurrentHashMap<>();
        applicationPathMap = new ConcurrentHashMap<>();
        Properties properties = new Properties();

        properties.load(new FileReader(applicationDescriptionsConfigFile));
        for (String propertyName : properties.stringPropertyNames()) {
            applicationPathMap.put(propertyName, (String) properties.get(propertyName));
        }
    }

    public boolean applicationExists(String key) {
        return applicationPathMap.containsKey(key);
    }

    public Application getApplication(String key) throws NoApplicationAvailableException {
        if (applicationExists(key)) {
            try {
                if (applicationsMap.containsKey(key)) {
                    return applicationsMap.get(key);
                } else {
                    String applicationName = applicationPathMap.get(key);
                    Application application = createApplication(applicationName);
                    applicationsMap.put(key, application);
                    return application;
                }
            } catch (Exception e) {
                throw new NoApplicationAvailableException(e.getMessage());
            }
        } else {
            throw new NoApplicationAvailableException("No Application found with this key: " + key);
        }
    }

    private Application createApplication(String applicationName) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class applicationClass = Class.forName(applicationName);
        return (Application) applicationClass.newInstance();
    }


}
