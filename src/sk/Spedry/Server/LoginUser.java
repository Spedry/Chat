package sk.Spedry.Server;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class LoginUser {
    private static String fileLocation;

    public static void main(String[] args) {

        FileInputStream file = null;
        try {
            file = new FileInputStream(new File(fileLocation));
            Workbook workbook = new XSSFWorkbook(file);
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            System.out.println("Súbor sa nenašiel");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
