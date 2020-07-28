package sk.Spedry.server;

import data.Register;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.*;

public class RegisterUser {
    public RegisterUser(ObjectInputStream objectInputStream) {
        String url = "jdbc:mysql://localhost:3306/chat?useSSL=false&allowPublicKeyRetrieval=true";
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Register registerRead = null;
        try {
            registerRead = (Register) objectInputStream.readObject();
            objectInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
            System.out.println("nebola najdená class Register");
        }
        try (
                Connection conn = DriverManager.getConnection(url, "root", "admin");
                Statement stmt = conn.createStatement();
        ) {
            String strSelect = "select UserName from Users";

            ResultSet rset = stmt.executeQuery(strSelect);

            boolean possibleToReg = true;
            while(rset.next()) {

                String UserName = rset.getString("UserName");
                if (UserName.equals(registerRead.getMeno())) {
                    //odošly že sa zadane meno už používa a uživateľ si musí zvoliť iné
                    System.out.println("tento uživateľ už je...");
                    possibleToReg = false;
                    break;
                }
            }
            if (possibleToReg) {
                strSelect = "insert into users(UserName, UserH) values(\"" + registerRead.getMeno() + "\",\"" + registerRead.getHeslo() + "\")";
                int countInserted = stmt.executeUpdate(strSelect);
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }
}