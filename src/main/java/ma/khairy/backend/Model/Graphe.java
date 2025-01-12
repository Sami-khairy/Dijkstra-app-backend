package ma.khairy.backend.Model;

import java.util.ArrayList;
import java.util.List;

public class Graphe {
    private final List<Sommet> sommets;

    public Graphe() {
        this.sommets = new ArrayList<>();
    }

    public void ajouterSommet(Sommet sommet) {
        sommets.add(sommet);
    }

    public Sommet getSommet(String nom) {
        for (Sommet sommet : sommets) {
            if (sommet.getNom().equals(nom)) {
                return sommet;
            }
        }
        // Créer le sommet s'il n'existe pas encore
        Sommet nouveauSommet = new Sommet(nom);
        ajouterSommet(nouveauSommet);
        return nouveauSommet;
    }

    public void ajouterArrete(String source, String destination, int poids) {
        Sommet sourceSommet = getSommet(source);
        sourceSommet.ajouterArrete(source, destination, poids);
    }

    public int getIndiceSommet(String nom) {
        for (int i = 0; i < sommets.size(); i++) {
            if (sommets.get(i).getNom().equals(nom)) {
                return i;
            }
        }
        return -1;
    }

    public List<Sommet> getSommets() {
        return sommets;
    }
}