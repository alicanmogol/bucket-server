package com.fererlab.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * acm | 12/11/12 10:19 PM
 */
public class ServerThread extends Thread {

    private final Logger logger = Logger.getLogger(getClass().getSimpleName());

    private Integer port = 9876;
    private Vector<Socket> sockets = new Vector<>();
    private ServerSocket serverSocket;
    private final Integer maximumThreadCount;

    public ServerThread(Integer port, Integer maximumThreadCount) {
        this.port = port;
        this.maximumThreadCount = maximumThreadCount;
    }

    @Override

    public void run() {
        try {
            log("ServerThread will listen port: " + port);
            serverSocket = new ServerSocket(port);

            ExecutorService pool = Executors.newFixedThreadPool(maximumThreadCount);
            Socket socket = null;
            InputStream inputStream = null;
            OutputStream outputStream = null;
            while (true) {

                log("will wait and accept connections");

                // will block and wait here
                socket = serverSocket.accept();

                log("new client connection accepted, will create connection object and start ConnectionHandler");

                // add this connection to client to sockets
                sockets.add(socket);

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
                log("--------before");
                pool.execute(connectionHandler);
                log("--------after");
            }
        } catch (Exception e) {
            log("serverThread with port: " + port + " got an exception, will not listen anymore, needs restart, exception: " + e);
            e.printStackTrace();

        }
    }

    public void interrupt() {
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (Exception e) {
            }
        }
        if (sockets != null) {
            try {
                Iterator<Socket> iterator = sockets.iterator();
                while (iterator.hasNext()) {
                    Socket socket = iterator.next();
                    try {
                        iterator.remove();
                    } catch (Exception e) {
                    }
                    if (socket != null) {
                        if (socket.getInputStream() != null) {
                            try {
                                socket.getInputStream().close();
                            } catch (Exception e) {
                            }
                        }
                        if (socket.getOutputStream() != null) {
                            try {
                                socket.getOutputStream().close();
                            } catch (Exception e) {
                            }
                        }
                        try {
                            socket.close();
                        } catch (Exception e) {
                        }
                    }
                }
            } catch (Exception e) {
            }

        }
        sockets = new Vector<>();
        super.interrupt();
    }

    private void log(String log) {
        logger.info(log);
    }

}