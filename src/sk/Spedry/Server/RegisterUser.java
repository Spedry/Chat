package sk.Spedry.Server;

import data.Overenie;

import java.io.IOException;
import java.io.ObjectInputStream;

public class RegisterUser {
    public RegisterUser(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        System.out.println("lol");
        Overenie overenieRead = (Overenie) objectInputStream.readObject();
        objectInputStream.close();
        System.out.println(overenieRead.getMeno() + overenieRead.getHeslo());
    }
}
