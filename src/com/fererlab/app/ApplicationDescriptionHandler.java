package com.fererlab.app;

import com.fererlab.dto.*;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.*;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * acm | 1/4/13
 */
public class ApplicationDescriptionHandler {

    private final Logger logger = Logger.getLogger(getClass().getSimpleName());

    private ConcurrentHashMap<String, Application> applicationsMap;
    private ConcurrentHashMap<String, ClassLoader> classLoaderMap;
    private ConcurrentHashMap<String, String> applicationPathMap;

    private ConcurrentHashMap<String, List<String>> domainApplicationsMap;
    private ConcurrentHashMap<String, String> domainDefaultApplicationMap;
    private List<String> domainNameList;

    private static ApplicationDescriptionHandler instance;

    public void reloadApplicationDescriptions(String applicationDescriptionsConfigFile) throws IOException {
        applicationsMap = new ConcurrentHashMap<String, Application>();
        classLoaderMap = new ConcurrentHashMap<String, ClassLoader>();
        applicationPathMap = new ConcurrentHashMap<String, String>();

        domainApplicationsMap = new ConcurrentHashMap<String, List<String>>();
        domainDefaultApplicationMap = new ConcurrentHashMap<String, String>();
        domainNameList = new ArrayList<String>();
        Properties properties = new Properties();

        properties.load(new FileReader(applicationDescriptionsConfigFile));

        // there should be domains key in properties
        if (properties.containsKey("domains")) {
            // read(remove) domains key
            // split with comma
            String[] domainNames = String.valueOf(properties.remove("domains")).split(",");
            // for each domain, check for .applications (remove)
            for (String domainName : domainNames) {
                if (domainName == null || domainName.trim().isEmpty()) {
                    continue;
                }
                domainNameList.add(domainName);
                if (properties.containsKey(domainName + ".applications")) {
                    // add each domain's applications to domainApplicationsMap
                    String[] applicationNames = String.valueOf(properties.remove(domainName + ".applications")).split(",");
                    for (String applicationName : applicationNames) {
                        if (!domainApplicationsMap.containsKey(domainName)) {
                            domainApplicationsMap.put(domainName, new ArrayList<String>());
                        }
                        domainApplicationsMap.get(domainName).add(applicationName);
                    }
                }
                // for each domain, check for .default (remove)
                if (properties.containsKey(domainName + ".default")) {
                    // add each domain with its default application to domainDefaultApplicationMap
                    String defaultApplication = String.valueOf(properties.remove(domainName + ".default"));
                    domainDefaultApplicationMap.put(domainName, defaultApplication);
                }
            }
        }
        for (String propertyName : properties.stringPropertyNames()) {
            applicationPathMap.put(propertyName.trim(), (String.valueOf(properties.get(propertyName))).trim());
        }
    }

    public boolean applicationExists(String domainName, String applicationName) {
        return domainApplicationsMap.containsKey(domainName) && domainApplicationsMap.get(domainName).contains(applicationName);
    }

    private Application createApplication(String applicationName) throws ClassNotFoundException, IllegalAccessException, InstantiationException, MalformedURLException {
        //      key=value
        //      /sample-application=jar:///tmp/home/alican/projects/fererlab/SampleApplication/out/artifacts/SampleApplication.jar|com.sample.app.SampleApplication
        //
        //      key=value
        //      cms=dir
        //      cmd.dir=dir:///tmp/artifacts/SampleApplication.jar|com.sample.app.SampleApplication
        String applicationPathAndClass = applicationPathMap.get(applicationName);
        if (applicationPathMap.containsKey(applicationName + "." + applicationPathMap.get(applicationName))) {
            applicationPathAndClass = applicationPathMap.get(applicationName + "." + applicationPathMap.get(applicationName));
            log("applicationPathMap contains key.value, key: " + applicationName + " applicationPathAndClass: " + applicationPathAndClass);
        } else {
            log("applicationPathMap does not contain key.value, key: " + applicationName + " applicationPathAndClass: " + applicationPathAndClass + " applicationPathAndClass: " + applicationPathAndClass);
        }

        URL[] urlsToLoad = null;
        String className = null;
        // check if the class is in the classpath or somewhere else
        if (applicationPathAndClass.lastIndexOf('|') != -1) {
            final String[] pathAndClassName = applicationPathAndClass.split("\\|");
            if (applicationPathAndClass.startsWith("jar://")) {
                // TODO find all jars inside the jar file
                urlsToLoad = new URL[]{
                        new File(pathAndClassName[0].substring("jar://".length())).toURI().toURL()
                };
                className = pathAndClassName[1];
            } else if (applicationPathAndClass.startsWith("http://") || applicationPathAndClass.startsWith("https://")) {

                // if the application name start with https, either we need to import the SSL Certificate of the
                // other server or set the trust manager to trust anything,
                // actually it is not really important since the request object is encrypted
                if (applicationPathAndClass.startsWith("https://")) {
                    try {
                        TrustManager[] trustEverything = new TrustManager[]{
                                new X509TrustManager() {
                                    public X509Certificate[] getAcceptedIssuers() {
                                        return null;
                                    }

                                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                                    }

                                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                                    }
                                }
                        };
                        SSLContext sslContext = SSLContext.getInstance("SSL");
                        sslContext.init(null, trustEverything, null);
                        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                // define a map to hold the proxy related values
                final Map<String, String> proxyMap = new HashMap<String, String>();

                // set the default values
                proxyMap.put("useProxy", "SYSTEM");

                // first check if there is an entry for proxy settings
                if (applicationPathMap.containsKey(applicationName + "." + applicationPathMap.get(applicationName) + ".proxy")) {

                    // .proxy entry exists, it may be in some forms like; "", "NO_PROXY", or "PROTOCOL://USER:PASS@IP:PORT"
                    // "socks://username:password@127.0.0.1:8080"
                    // first check if there is a protocol exists
                    String[] typeAndProxy = applicationPathMap.get(applicationName + "." + applicationPathMap.get(applicationName) + ".proxy").split("://");

                    // this means either proxy entry is empty string or "NO_PROXY"
                    if (typeAndProxy.length == 1) {
                        // this is empty string
                        if (typeAndProxy[0].isEmpty()) {
                            proxyMap.put("useProxy", "SYSTEM");

                        } else if (typeAndProxy[0].equalsIgnoreCase("NO_PROXY")) {
                            proxyMap.put("useProxy", "NO_PROXY");
                        }
                    }

                    // this means there is an entry for proxy
                    else {
                        proxyMap.put("useProxy", "PROXY");
                        proxyMap.put("proxyType", typeAndProxy[0]); // type; http or socks
                        proxyMap.put("proxy", typeAndProxy[1]);
                        if (proxyMap.get("proxy").lastIndexOf("@") != -1) {
                            String[] credentialsAndServer = proxyMap.get("proxy").split("@");

                            if (credentialsAndServer[0].lastIndexOf(":") != -1) {
                                String[] proxyUsernamePassword = credentialsAndServer[0].split(":");
                                proxyMap.put("proxyUsername", proxyUsernamePassword[0]);
                                proxyMap.put("proxyPassword", proxyUsernamePassword[1]);
                            }

                            if (credentialsAndServer[1].lastIndexOf(":") != -1) {
                                String[] proxyHostPort = credentialsAndServer[0].split(":");
                                proxyMap.put("proxyHost", proxyHostPort[0]);
                                proxyMap.put("proxyPort", proxyHostPort[1]);
                            }
                        }
                    }

                }

                return new Application() {

                    @Override
                    public void setDevelopmentMode(boolean isDevelopment) {
                    }

                    @Override
                    public boolean isDevelopmentModeOn() {
                        return false;
                    }

                    @Override
                    public void start() {
                    }

                    @Override
                    public Response runApplication(Request request) {

                        // create an empty response with service unavailable error
                        Response response = new Response(
                                new ParamMap<String, Param<String, Object>>(),
                                request.getSession(),
                                Status.STATUS_SERVICE_UNAVAILABLE,
                                ""
                        );

                        // try to send Request and get the Response, otherwise the empty Response will return
                        try {
                            // read the request object
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                            objectOutputStream.writeObject(request);
                            objectOutputStream.close();
                            byteArrayOutputStream.close();

                            // encrypt the request
                            // pathAndClassName[1] Secret Key
                            byte[] secretKey = pathAndClassName[1].getBytes();
                            byte[] dataToSend = byteArrayOutputStream.toByteArray();
                            byte[] encryptedData;
                            String base64EncodedString = "";
                            sun.misc.BASE64Encoder base64Encoder = new BASE64Encoder();
                            try {
                                // encrypt
                                Cipher c = Cipher.getInstance("AES");
                                SecretKeySpec k = new SecretKeySpec(secretKey, "AES");
                                c.init(Cipher.ENCRYPT_MODE, k);
                                encryptedData = c.doFinal(dataToSend);

                                // encode
                                base64EncodedString = base64Encoder.encode(encryptedData);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            // pathAndClassName[0] request URL
                            URL url = new URL(pathAndClassName[0]);

                            // define the url connection
                            URLConnection urlConn;

                            // this opens the connection using NO PROXY, direct connection
                            if (proxyMap.get("useProxy").equals("NO_PROXY")) {
                                urlConn = url.openConnection(Proxy.NO_PROXY);
                            }

                            // if proxy is defined, create a Proxy for this connection
                            else if (proxyMap.containsKey("proxyHost") && proxyMap.containsKey("proxyPort") && proxyMap.containsKey("proxyType")) {

                                // create a proxy just for this connection
                                SocketAddress proxyAddress = new InetSocketAddress(proxyMap.get("proxyHost"), Integer.valueOf(proxyMap.get("proxyPort")));
                                Proxy httpProxy = new Proxy(proxyMap.get("proxyType").equalsIgnoreCase("socks") ? Proxy.Type.SOCKS : Proxy.Type.HTTP, proxyAddress);

                                // open connection to this url with proxy
                                urlConn = url.openConnection(httpProxy);

                                // if the proxy has username and password add the authorization
                                // currently there is only basic authorization supported for proxy
                                if (proxyMap.containsKey("proxyUsername") && proxyMap.containsKey("proxyPassword")) {
                                    String encoded = base64Encoder.encode((proxyMap.get("proxyUsername") + ":" + proxyMap.get("proxyPassword")).getBytes());
                                    urlConn.setRequestProperty("Proxy-Authorization", "Basic " + encoded);
                                }
                            }

                            // if no proxy defined than use system proxy settings
                            else {
                                urlConn = url.openConnection();
                            }

                            // set the APPLICATION_REQUEST property to the request, this property's value will be read at the other side
                            urlConn.addRequestProperty(RequestKeys.APPLICATION_REQUEST.getValue(), base64EncodedString);
                            // set attributes to url connection and then call connect
                            urlConn.setAllowUserInteraction(false);
                            urlConn.setDoOutput(true);
                            urlConn.setDoInput(true);
                            urlConn.connect();

                            // read and decode the request
                            BASE64Decoder base64Decoder = new BASE64Decoder();
                            byte[] encryptedResponse = base64Decoder.decodeBuffer(urlConn.getInputStream()); // send request and read response

                            // read the response, decode, decrypt, read to Response object
                            try {
                                Cipher c = Cipher.getInstance("AES");
                                SecretKeySpec k = new SecretKeySpec(secretKey, "AES");
                                c.init(Cipher.DECRYPT_MODE, k);
                                byte[] responseData = c.doFinal(encryptedResponse);
                                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(responseData);
                                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                                response = (Response) objectInputStream.readObject();
                                objectOutputStream.close();
                                byteArrayOutputStream.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    public void stop() {
                    }
                };
            } else if (applicationPathAndClass.startsWith("dir://")) {
                urlsToLoad = new URL[]{new File(pathAndClassName[0].substring("dir://".length())).toURI().toURL()};
                className = pathAndClassName[1];
                log("#urlsToLoad: " + urlsToLoad.length + " className: " + className);
            }
        }

        // class should be in the classpath
        else {
            className = applicationPathAndClass;
        }

        log("className: " + className);
        Class classToLoad;
        if (urlsToLoad == null) {
            classToLoad = Class.forName(className);
            log("urlsToLoad null, classToLoad: " + classToLoad);
        } else {
            URLClassLoader classLoader = new URLClassLoader(
                    urlsToLoad,
                    this.getClass().getClassLoader()
            );
            Thread.currentThread().setContextClassLoader(classLoader);
            classToLoad = Class.forName(className, true, classLoader);
            log("urlsToLoad not null, classToLoad: " + classToLoad);
        }

        return (Application) classToLoad.newInstance();
    }

    private void log(String log) {
        logger.info(log);
    }

    public boolean domainExists(String domainName) {
        return domainNameList.contains(domainName);
    }

    public boolean hasDefaultApplication(String domainName) {
        return domainDefaultApplicationMap.containsKey(domainName);
    }

    public String getDefaultApplication(String domainName) {
        return domainDefaultApplicationMap.get(domainName);
    }

    public Response runApplication(String domainName, String applicationName, Request request) throws NoApplicationAvailableException, ClassNotFoundException, MalformedURLException, InstantiationException, IllegalAccessException {
        // fererlab.com     cms     request
        Application application;
        // this application will be reloaded at every request
        if (applicationPathMap.containsKey(applicationName + ".reloadForEveryRequest") && applicationPathMap.get(applicationName + ".reloadForEveryRequest").equalsIgnoreCase("true")) {
            // will return Application here
            log("reloadForEveryRequest set to true, application in development mode");
            application = createApplication(applicationName);
            application.setDevelopmentMode(true);
            application.start();
            Response response = application.runApplication(request);
            if (application.isDevelopmentModeOn()) {
                application.stop();
            }
            return response;
        } else {
            // key should be:   fererlab.com.cms
            String key = domainName + "." + applicationName;
            // check if the applications map has this Application
            if (!applicationsMap.containsKey(key)) {
                // if not create and put it to the map
                ClassLoader classLoaderPre = Thread.currentThread().getContextClassLoader();
                application = createApplication(applicationName);
                ClassLoader classLoaderCurrent = Thread.currentThread().getContextClassLoader();
                if (!classLoaderPre.equals(classLoaderCurrent)) {
                    classLoaderMap.put(key, classLoaderCurrent);
                }
                application.setDevelopmentMode(false);
                application.start();
                applicationsMap.put(key, application);
            }
            // set if this application has its own ClassLoader
            if (classLoaderMap.containsKey(key)) {
                Thread.currentThread().setContextClassLoader(
                        classLoaderMap.get(key)
                );
            }
            // application already in map return it
            application = applicationsMap.get(key);
            return application.runApplication(request);
        }
    }

    public void stopApplications() {
        for (String key : applicationsMap.keySet()) {
            Application application = applicationsMap.get(key);
            application.stop();
        }
    }
}
