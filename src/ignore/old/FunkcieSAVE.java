/*
package sk.Spedry.Server;

import java.io.*;
import java.util.Date;

public class FunkcieSAVE {
    public static void fileNotFound(PrintWriter vystup, OutputStream dataOut, String pozadovanySubor) throws IOException {
        File file = new File(Server.PATH, Server.ERROR);
        int fileLength = (int) file.length();
        String content = "text/html";
        byte[] fileData = readFileData(file, fileLength);

        vystup.println("HTTP/1.1 404 File Not Found");
        vystup.println("Dátum: " + new Date());
        vystup.println("Content-type: " + content);
        vystup.println("Content-length: " + fileLength);
        /////////////////////
        vystup.println(); // PRÁZDY RIADOK MEDZI HEADERS A CONTENT VELICE DÔLEŽITÉ!!!!
        ///////////////////
        vystup.flush(); // vyprázdni character output stream buffer

        dataOut.write(fileData, 0, fileLength);
        dataOut.flush();

        if (Server.napojenie) {
            System.out.println("Požadovaný súbor " + pozadovanySubor + " nebol najdený.");
        }
    }

    // return supported MIME Types
    public static String getContentType(String fileRequested) { // zistuje o aký súbor ide a nasledne ho vráti
        if (fileRequested.endsWith(".htm") || fileRequested.endsWith(".html"))
            return "text/html"; // vráti html
        else if ((fileRequested.endsWith(".css")))
            return "text/css"; // vráti css pre html
        else
            return "text/plain";
    }

    public static byte[] readFileData(File file, int fileLength) throws IOException { // čitanie dat
        FileInputStream fileIn = null;
        byte[] fileData = new byte[fileLength];

        try {
            fileIn = new FileInputStream(file);
            fileIn.read(fileData);
        } finally {
            if (fileIn != null)
                fileIn.close();
        }

        return fileData;
    }

    public static void write(BufferedReader vstup) throws IOException {
        String TabCell;

        //vstup = new BufferedReader(new FileReader("src\\sk\\Peter_Tokovics\\Server\\Html_Chat\\index2.html")); //čitač dokumenta CSV

        for (int i = 0; (TabCell = vstup.readLine()) != null; i++) { //rob dokým nedojdeš na koniec
            String[] read = TabCell.split(",");
            System.out.println(read[i]);
        }
    }
}

*/
