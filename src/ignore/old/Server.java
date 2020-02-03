package ignore.old;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.StringTokenizer;

public class Server implements Runnable {
    // proste port
    static final int PORT = 8000;

    static final boolean napojenie = true;

    private Socket pripojenie;

    public Server(Socket c) {
        pripojenie = c;
    }
    // telo main
    public static void startServer() {
        try {
            ServerSocket serverConnect = new ServerSocket(PORT);
            System.out.println("Server nabieha.\nČaká na input na porte: " + PORT + "...");

            while(true) {
                Server myServer = new Server(serverConnect.accept());

                if(napojenie) {
                    System.out.println("Pripojenie nadviazané. (" + new Date() + ")");
                }

                Thread thread = new Thread(myServer);
                thread.start();
            }
        } catch (IOException e) {
            System.out.println("Chyba pri pokuse nadviadzať spojenie: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        BufferedReader vstup = null;
        PrintWriter vystup = null;
        DataInputStream dataVstup = null;
        BufferedOutputStream dataVystup = null;
        String pozadovanySubor = null;

        try {
            // načítanie char input od klienta pomocou input stream na socket
            vstup = new BufferedReader(new InputStreamReader(pripojenie.getInputStream()));
            // zaslanie char output klientovi (for headers)
            vystup = new PrintWriter(pripojenie.getOutputStream());
            // načítanie binary output
            dataVstup = new DataInputStream(new BufferedInputStream(pripojenie.getInputStream()));
            // zaslanie binary output klientovy (pre požadované dáta)
            dataVystup = new BufferedOutputStream(pripojenie.getOutputStream());
            byte[] bytes = new byte[1024];

            dataVstup.read(bytes);
            System.out.println("bytes: " + bytes);
            System.out.println("dataVstup: " + dataVstup);

            FileOutputStream fos = new FileOutputStream("C:\\Users\\Spedry\\Desktop\\test2.xml");
            fos.write(bytes);

            // ziskať prvý riadok inputu/žiadosti od klienta
            String input = vstup.readLine();
            System.out.println(input);
            // rozdelenie inputu/žiadosti pomocou string tokenizer
            StringTokenizer rozdel = new StringTokenizer(input);
            String method = rozdel.nextToken().toUpperCase(); // zistíme o akú metódu sme prijali
            // vráti html súbor teda stránku
            pozadovanySubor = rozdel.nextToken().toLowerCase();

            /*System.out.println("vstup: " + vstup);
            System.out.println("vystup: " + vystup);
            System.out.println("dataVystup: " + dataVystup);*/
            System.out.println(pozadovanySubor);
            switch (method) {
                case "GET":
                    System.out.println("Bola zaznamenaná metoda GET");
                    break;
                case "POST":
                    String tabCell;

                    System.out.println("Bola zaznamenaná metoda POST");
                    System.out.println("vstup: " + vstup);
                    System.out.println("vystup: " + vystup);
                    System.out.println("Dvstup: " + dataVstup);
                    System.out.println("Dvystup: " + dataVystup);
                    System.out.println("POZsubor: " + pozadovanySubor);
                    System.out.print("Text: ");
                    while ((tabCell = vstup.readLine()) != null) { //rob dokým nedojdeš na koniec
                        System.out.println(tabCell);
                    }
                    break;
                case "PUT":
                    System.out.println("Bola zaznamenaná metoda PUT");
                    break;
                case "PATCH":
                    System.out.println("Bola zaznamenaná metoda GET");
                    break;
                case "DELETE":
                    System.out.println("Bola zaznamenaná metoda GET");
                    break;
                case "OPTION":
                    System.out.println("Bola zaznamenaná metoda GET");
                    break;
                case "HEAD":
                    System.out.println("Bola zaznamenaná metoda GET");
                    break;
                default:
                    System.out.println("Táto metóda nebola rozpoznaná!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // uzravry buffreader
                vstup.close();
                // uzavry printwritter
                vystup.close();
                // uzavry buffoutput
                dataVystup.close();
                // ukonči server
                pripojenie.close(); // we close socket connection
            } catch (Exception e) {
                System.err.println("Error uzavieram stream : " + e.getMessage());
            }
            if (napojenie) {
                System.out.println("Pripojenie ukončené.\n"); // ukonči server
            }
        }
    }
}