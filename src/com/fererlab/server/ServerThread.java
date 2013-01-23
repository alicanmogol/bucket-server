package com.fererlab.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * acm | 12/11/12
 */
public class ServerThread extends Thread {

    private final Logger logger = Logger.getLogger(getClass().getSimpleName());

    private Integer port = 9876;
    private ServerSocket serverSocket;
    private final Integer maximumThreadCount;
    private boolean running = true;

    public ServerThread(Integer port, Integer maximumThreadCount) {
        this.port = port;
        this.maximumThreadCount = maximumThreadCount;
    }

    @Override

    public void run() {
        try {
            log("ServerThread will listen port: " + port);
            serverSocket = new ServerSocket(port, Integer.MAX_VALUE);

            ExecutorService pool = Executors.newFixedThreadPool(maximumThreadCount);
            Socket socket = null;
            InputStream inputStream = null;
            OutputStream outputStream = null;
            while (running) {

                log("will wait and accept connections");

                // will block and wait here
                socket = serverSocket.accept();

                log("new client connection accepted, will create connection object and start ConnectionHandler");

                // get input and output streams
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();

                // create connection object
                Connection connection = new Connection();
                connection.setInputStream(inputStream);
                connection.setOutputStream(outputStream);
                connection.setSocket(socket);

                // pass the connection to handler and run
                ConnectionHandler connectionHandler = new ConnectionHandler(connection);

                // pool will execute this connection
                pool.execute(connectionHandler);

            }
        } catch (Exception e) {
            log("serverThread with port: " + port + " got an exception, will not listen anymore, needs restart, exception: " + e);
            e.printStackTrace();
        }
    }

    public void interrupt() {
        running = false;
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (Exception e) {
            }
        }
        super.interrupt();
    }

    private void log(String log) {
        logger.info(log);
    }

}