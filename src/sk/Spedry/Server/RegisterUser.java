package sk.Spedry.Server;

import data.Overenie;
import java.io.IOException;
import java.io.ObjectInputStream;

public class RegisterUser {
    public RegisterUser(ObjectInputStream objectInputStream) {
        System.out.println("Bola prijatá správa od klienta...\nspráva ohladom prihlásenia uživatela");
        try {
            Overenie overenieRead = (Overenie) objectInputStream.readObject();
            objectInputStream.close();
            System.out.println(overenieRead.getMeno() + "\n" + overenieRead.getHeslo());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
            System.out.println("nebola najdená class Overenie");
        }
    }
}