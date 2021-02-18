package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//netstat -ano | findstr :<PORT> == netstat -ano | findstr :50000
//taskkill /PID <PID> /F == taskkill /PID XXXXXX /F
public class Server {

    private static final int port = 50000;

    private static final Logger logger = LogManager.getLogger(Server.class);

    public static void main(String[] args) throws IOException {
        logger.info("Server is loading...");
        ServerSocket serverConnect = new ServerSocket(port);
        InetAddress ip = InetAddress.getLocalHost();
        String hostname = ip.getHostName();
        logger.info("Info:"
                + "\n\t\t\tIP: " + ip
                + "\n\t\t\tName of host: " + hostname
                + "\n\t\t\tServer is starting..."
                + "\n\t\t\tWaiting for input on port: " + port + "...");
        MessageHandler messageHandler = new MessageHandler();
        try {
            while(true) {
                ClientHandler clientsSide = new ClientHandler(serverConnect.accept(), messageHandler);

                logger.info("Connection established. (" + new Date() + ")");
                logger.info("Number of active threads from the given thread: " + Thread.activeCount());

                Thread serverSideHandlerThread = new Thread(clientsSide);
                serverSideHandlerThread.start();
            }
        } catch (IOException e) {
            logger.error("Error during creating connection: " + e.getMessage());
        }
    }
}