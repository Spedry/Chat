package sk.Spedry.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerMain {

    static final int PORT = 50000;
    private static InetAddress ip = null;
    private static String hostname = null;

    private static final Logger logger = LogManager.getLogger(ServerMain.class);

    public static void main(String[] args) throws IOException {
        logger.info("Server is loading...");
        ServerSocket serverConnect = new ServerSocket(PORT);
        ip = InetAddress.getLocalHost();
        hostname = ip.getHostName();
        logger.info("Info:"
                + "\n\t\t\tIP: " + ip
                + "\n\t\t\tName of host: " + hostname
                + "\n\t\t\tServer is starting..."
                + "\n\t\t\tWaiting for input on port: " + PORT + "...");
        //MessageHandler.getInstance();
        try {
            while(true) {
                Server startServer = new Server(serverConnect.accept());

                logger.info("Connection established. (" + new Date() + ")");
                logger.info("Number of active threads from the given thread: " + Thread.activeCount());

                Thread serverSideHandlerThread = new Thread(startServer);
                serverSideHandlerThread.start();
            }
        } catch (IOException e) {
            logger.error("Error during creating connection: " + e.getMessage());
        }
    }



}