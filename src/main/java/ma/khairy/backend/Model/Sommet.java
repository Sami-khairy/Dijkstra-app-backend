package ma.khairy.backend.Model;

import java.util.ArrayList;
import java.util.List;

public class Sommet {
    private final String nom;
    private final List<Arrete> arretes;

    public Sommet(String nom) {
        this.nom = nom;
        this.arretes = new ArrayList<>();
    }

    public String getNom() {
        return nom;
    }

    public List<Arrete> getArretes() {
        return arretes;
    }

    public void ajouterArrete(String source, String destination, int poids) {
        Arrete arrete = new Arrete(source, destination, poids);
        this.arretes.add(arrete);
    }

    @Override
    public String toString() {
        return nom;
    }
}