package sk.Spedry.server;

import data.Overenie;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.*;

public class LoginUser {

    public LoginUser(ObjectInputStream objectInputStream) {
        String url = "jdbc:mysql://localhost:3306/chat?useSSL=false";
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Overenie overenieRead = null;
        try {
            overenieRead = (Overenie) objectInputStream.readObject();
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

            while(rset.next()) {
                int UserID = rset.getInt("UserID");
                String UserName = rset.getString("UserName");
                if (UserName.equals(overenieRead.getMeno())) {
                    strSelect = "select UserH from Users where UserID =" + UserID;
                    rset = stmt.executeQuery(strSelect);
                    if (rset.getString("UserH").equals(overenieRead.getHeslo())) {
                        //odošly že príhlásenie prebehlo úspešne a umožni uživatelovy vstúpiť do APP
                    }
                    break;
                }
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }
}
