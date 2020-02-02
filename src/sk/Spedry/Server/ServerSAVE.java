/*
package sk.Spedry.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.StringTokenizer;

public class ServerSAVE implements Runnable {
    // cesta k súboru kde je html
    static final File PATH = new File("src\\sk\\Peter_Tokovics\\Server\\Html_Chat");
    // hlavné telo html stránky
    static final String INDEX = "index2.html";
    // v pripade keby nastala chyba napr. index.html by nebol najdený/načítaný
    static final String ERROR = "404.html";
    // prichýdzajúca methoda nebola nerozoznaná
    static final String METHOD_NOT_FOUND = "method_error.hmtl";
    // proste port
    static final int PORT = 8000;

    static final boolean napojenie = true;

    private Socket pripojenie;

    public ServerSAVE(Socket c) {
        pripojenie = c;
    }
    // telo main
    public static void startServer() {
        try {
            ServerSocket serverConnect = new ServerSocket(PORT);
            System.out.println("Server nabieha.\nČaká na input na porte: " + PORT + "...");

            while(true) {
                ServerSAVE myServer = new ServerSAVE(serverConnect.accept());

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
        BufferedOutputStream dataVystup = null;
        String pozadovanySubor = null;

        try {
            // načítanie char input od klienta pomocou input stream na socket
            vstup = new BufferedReader(new InputStreamReader(pripojenie.getInputStream()));
            // zaslanie char output klientovi (for headers)
            vystup = new PrintWriter(pripojenie.getOutputStream());
            // zaslanie binary output klientovy (pre požadované dáta)
            dataVystup = new BufferedOutputStream(pripojenie.getOutputStream());

            // ziskať prvý riadok inputu/žiadosti od klienta
            String input = vstup.readLine();
            // rozdelenie inputu/žiadosti pomocou string tokenizer
            StringTokenizer rozdel = new StringTokenizer(input);
            String method = rozdel.nextToken().toUpperCase(); // zistíme o akú metódu sme prijali
            // vráti html súbor teda stránku
            pozadovanySubor = rozdel.nextToken().toLowerCase();

            */
/*System.out.println("vstup: " + vstup);
            System.out.println("vystup: " + vystup);
            System.out.println("dataVystup: " + dataVystup);*//*

            System.out.println(pozadovanySubor);
            switch (method) {
                case "GET":
                    System.out.println("Bola zaznamenaná metoda GET");
                    if (pozadovanySubor.endsWith("/")) {
                        pozadovanySubor += INDEX;
                    }

                    File file = new File(PATH, pozadovanySubor);
                    int fileLength = (int) file.length();
                    String content = Funkcie.getContentType(pozadovanySubor);

                    byte[] fileData = Funkcie.readFileData(file, fileLength);

                    // pošli HTTP header
                    vystup.println("HTTP/1.1 200 OK");
                    vystup.println("School txt chat");
                    vystup.println("Date: " + new Date());
                    vystup.println("Content-type: " + content);
                    vystup.println("Content-length: " + fileLength);
                    /////////////////////
                    vystup.println(); // PRÁZDY RIADOK MEDZI HEADERS A CONTENT VELICE DÔLEŽITÉ!!!!
                    ///////////////////
                    vystup.flush(); // vyprázdni character output stream buffer

                    dataVystup.write(fileData, 0, fileLength);
                    dataVystup.flush();
                    if (napojenie) {
                        System.out.println("Súbor " + pozadovanySubor + " typu " + content + " bol spätne odoslaný");
                    }
                    break;
                case "POST":
                    System.out.println("Bola zaznamenaná metoda POST");

                    if (pozadovanySubor.endsWith("/")) {
                        pozadovanySubor += INDEX;
                    }

                    file = new File(PATH, pozadovanySubor);
                    fileLength = (int) file.length();
                    fileData = Funkcie.readFileData(file, fileLength);
                    content = Funkcie.getContentType(pozadovanySubor);

                    // pošli HTTP header
                    vystup.println("HTTP/1.1 200 OK");
                    vystup.println("School txt chat");
                    vystup.println("Date: " + new Date());
                    vystup.println("Content-type: " + content);
                    vystup.println("Content-length: " + fileLength);
                    /////////////////////
                    vystup.println(); // PRÁZDY RIADOK MEDZI HEADERS A CONTENT VELICE DÔLEŽITÉ!!!!
                    ///////////////////
                    vystup.flush(); // vyprázdni character output stream buffer

                    String tabCell;
                    //Funkcie.write(vstup);
                    while ((tabCell = vstup.readLine()) != null) { //rob dokým nedojdeš na koniec
                        System.out.println(tabCell);
                        dataVystup.write(fileData, 0, fileLength);
                    }

                    dataVystup.flush();
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
                    if (napojenie) {
                        System.out.println("501 Not Implemented : " + method + " method.");
                    }

                    // we return the not supported file to the client
                    file = new File(PATH, ERROR);
                    fileLength = (int) file.length();
                    String contentMimeType = "text/html";
                    //read content to return to client
                    fileData = Funkcie.readFileData(file, fileLength);

                    // we send HTTP Headers with data to client
                    vystup.println("HTTP/1.1 501 Not Implemented");
                    vystup.println("Dátum: " + new Date());
                    vystup.println("Content-type: " + contentMimeType);
                    vystup.println("Content-length: " + fileLength);
                    vystup.println(); // blank line between headers and content, very important !
                    vystup.flush(); // flush character output stream buffer
                    // file
                    dataVystup.write(fileData, 0, fileLength);
                    dataVystup.flush();
                    break;
            }



        } catch (FileNotFoundException fnfe) { // catch pre to keby sa súbor neniašiel
            try {
                Funkcie.fileNotFound(vystup, dataVystup, pozadovanySubor); // ziszi ktorý zo suborov sa nenasiel
            } catch (IOException ioe) {
                System.err.println("Error pri pokuse načítať súbor: " + ioe.getMessage()); // vypíš ktorý súbor sa nenašiel
            }
        } catch (IOException ioe) {
            System.err.println("Server error:" + ioe);
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
*/
