package data;

import java.io.Serializable;

public class Overenie implements Serializable {
    private String meno;
    private String heslo;

    public Overenie(String meno, String heslo) {
        this.meno = meno;
        this.heslo = heslo;
    }

    public String getMeno() {
        return meno;
    }

    public String getHeslo() {
        return heslo;
    }
}
