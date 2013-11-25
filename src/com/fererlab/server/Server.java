package com.fererlab.server;

import com.fererlab.app.ApplicationDescriptionHandler;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * acm 11/23/12 10:54 AM
 */
public class Server {

    private final Logger logger = Logger.getLogger(getClass().getSimpleName());
    private HashMap<String, String> argMap = new HashMap<String, String>();
    private Properties properties = null;
    private static List<ServerThread> serverThreads = new ArrayList<ServerThread>();
    private static String[] args = new String[0];

    public static void main(String[] args) {
        new Server(args);
    }

    public Server(String[] args) {
        Server.args = args;
        parseArgs(args);
        prepare();
        runServer();
    }

    private void parseArgs(String[] args) {
        if (args != null && args.length > 0) {
            for (String arg : args) {
                if (arg.startsWith("-") && arg.lastIndexOf("=") != -1) { // -config="server.config"
                    arg = arg.substring(1, arg.length());
                    String[] keyValuePair = arg.split("=");
                    if (keyValuePair.length == 2) {
                        log("adding arg to map, key: '" + keyValuePair[0] + "' value: '" + keyValuePair[1] + "'");
                        argMap.put(keyValuePair[0], keyValuePair[1]);
                    } else {
                        log("arg contains multiple '=', arg: " + arg);
                    }
                } else {
                    log("arg does not starts with '-' and does not contains '=',  arg: " + arg);
                }
            }
        } else {
            log("no args, using defaults");
        }
    }

    private void prepare() {
        // Prepare the application descriptions
        String applicationDescriptionFile = getProperties().getProperty(PropertyKeys.APP_DESC_FILE.getValue());
        if (applicationDescriptionFile == null) {
            applicationDescriptionFile = getClass().getResource(".").getPath() + "applications.properties";
            log("no applications description file in arg map, using default, configFileName: " + applicationDescriptionFile);
        }
        try {
            ApplicationDescriptionHandler.getInstance().reloadApplicationDescriptions(applicationDescriptionFile);
        } catch (IOException e) {
            log("could not load the application description file, e: " + e.getMessage());
            e.printStackTrace();
        }

        // prepare the logging
    }

    private void runServer() {
        try {
            String[] ports = getProperties().getProperty(PropertyKeys.LISTEN_PORTS.getValue()).split(",");
            String maximumThreadCount = getProperties().getProperty(PropertyKeys.MAXIMUM_THREAD_COUNT.getValue());
            for (String port : ports) {
                log("will listen port: " + port);
                ServerThread serverThread = new ServerThread(Integer.valueOf(port), Integer.parseInt(maximumThreadCount));
                serverThreads.add(serverThread);
                serverThread.start();
            }
        } catch (Exception e) {
            log("server got exception: " + e);
            e.printStackTrace();
        }
    }

    private Properties getProperties() {
        if (properties == null) {
            properties = new Properties();
            String configFileName;
            if (argMap.containsKey(PropertyKeys.CONFIG_FILE.getValue())) {
                configFileName = argMap.get(PropertyKeys.CONFIG_FILE.getValue());
                log("using config file from arg map, configFileName: " + configFileName);
            } else {
                configFileName = getClass().getResource(".").getPath() + "server.properties";
                log("no config file in arg map, using default, configFileName: " + configFileName);
            }
            try {
                properties.load(new FileReader(configFileName));
            } catch (IOException e) {
                log("server could not read the file, configFileName: " + configFileName);
                e.printStackTrace();
            }
        }
        return properties;
    }

    public static void shutdown() {
        for (ServerThread serverThread : serverThreads) {
            serverThread.interrupt();
        }
        System.exit(0);
    }

    private void log(String log) {
        logger.info(log);
    }

}
