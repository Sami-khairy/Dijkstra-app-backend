package ma.khairy.backend.Service;

import ma.khairy.backend.Model.Arrete;
import ma.khairy.backend.Model.Graphe;
import ma.khairy.backend.Model.Sommet;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service


public class DijkstraService {
    private final Map<String, Integer> distances;
    private final Map<String, String> predecesseurs;
    private final Set<String> sommetsNonVisites;
    private final Set<String> sommetsVisites;
    private  Graphe graphe;


    public DijkstraService() {
        this.distances = new HashMap<>();
        this.predecesseurs = new HashMap<>();
        this.sommetsNonVisites = new HashSet<>();
        this.sommetsVisites = new HashSet<>();
        this.graphe = new Graphe();
    }

    public Map<String, Integer> calculerCheminMinimal(Graphe graphe, String sommetDepartNom, boolean details) {
        this.graphe = graphe;
        distances.put(sommetDepartNom, 0);
        sommetsNonVisites.addAll(graphe.getSommets().stream().map(Sommet::getNom).collect(Collectors.toSet()));

        // Affichage initial
        if (details) {
            afficherEtatInitial();
        }

        int iteration = 0;
        while (!sommetsNonVisites.isEmpty()) {
            String sommetCourantNom = obtenirSommetDistanceMinimale();
            if (sommetCourantNom == null) break; // Aucun chemin restant
            sommetsNonVisites.remove(sommetCourantNom);
            sommetsVisites.add(sommetCourantNom);

            // Afficher les détails après chaque itération
            if (details) {
                afficherEtatIteration(sommetCourantNom, iteration++);
            }

            Sommet sommetCourant = graphe.getSommet(sommetCourantNom);
            for (Arrete arrete : sommetCourant.getArretes()) {
                String voisinNom = arrete.getDestination();
                if (sommetsNonVisites.contains(voisinNom)) {
                    int nouvelleDistance = distances.getOrDefault(sommetCourantNom, Integer.MAX_VALUE) + arrete.getPoids();
                    if (nouvelleDistance < distances.getOrDefault(voisinNom, Integer.MAX_VALUE)) {
                        distances.put(voisinNom, nouvelleDistance);
                        predecesseurs.put(voisinNom, sommetCourantNom);
                    }
                }
            }
        }

        // Convertir les résultats pour retourner des noms de sommets avec leurs distances
        return new HashMap<>(distances);
    }

    private String obtenirSommetDistanceMinimale() {
        String sommetMin = null;
        int distanceMin = Integer.MAX_VALUE;

        for (String sommet : sommetsNonVisites) {
            int distance = distances.getOrDefault(sommet, Integer.MAX_VALUE);
            if (distance < distanceMin) {
                distanceMin = distance;
                sommetMin = sommet;
            }
        }

        return sommetMin;
    }

    private void afficherEtatInitial() {
        // Afficher l'initialisation
        Set<String> sommetsVisitesInitiaux = new HashSet<>();
        Set<String> sommetsNonVisitesInitiaux = new HashSet<>(graphe.getSommets().stream().map(Sommet::getNom).collect(Collectors.toSet()));

        // Ajouter le sommet de départ à S
        String sommetDepartNom = graphe.getSommets().stream()
                .filter(s -> distances.getOrDefault(s.getNom(), Integer.MAX_VALUE) == 0)
                .map(Sommet::getNom)
                .findFirst()
                .orElse(null);

        if (sommetDepartNom != null) {
            sommetsVisitesInitiaux.add(sommetDepartNom);
            sommetsNonVisitesInitiaux.remove(sommetDepartNom);
        }

        String S = "{" + (sommetDepartNom != null ? sommetDepartNom : "") + "}";
        String S_ = sommetsNonVisitesInitiaux.stream()
                .collect(Collectors.joining(", ", "[", "]"));

        String pi = calculerVecteurPi(); // Utiliser la méthode calculerVecteurPi pour afficher les distances

        System.out.println("Initialisation");
        System.out.println("S=" + S + " ; 𝑆−=" + S_ + " ; π=" + pi);
    }

    private void afficherEtatIteration(String sommetCourantNom, int iteration) {
        // Affichage de l'étape i, des successeurs et de la mise à jour des distances
        System.out.println(iteration + "ère Itération :");
        String pi = calculerVecteurPi();
        System.out.println("Les successeurs de " + sommetCourantNom + " dans 𝑆−");

        Sommet sommetCourant = graphe.getSommet(sommetCourantNom);
        for (Arrete arrete : sommetCourant.getArretes()) {
            String voisinNom = arrete.getDestination();
            int nouvelleDistance = distances.getOrDefault(sommetCourantNom, Integer.MAX_VALUE) + arrete.getPoids();
            if (nouvelleDistance < distances.getOrDefault(voisinNom, Integer.MAX_VALUE)) {
                System.out.println("π(" + voisinNom + ")=min(" +
                        (distances.containsKey(voisinNom) ?
                                (distances.get(voisinNom) == Integer.MAX_VALUE ? "∞" : distances.get(voisinNom)) : "∞") +
                        "," + distances.get(sommetCourantNom) + "+" + arrete.getPoids() + ")=" + nouvelleDistance);
                distances.put(voisinNom, nouvelleDistance);
                pi = calculerVecteurPi();
            }
        }

        System.out.println("Le nouveau vecteur π=" + pi);
        System.out.println("-----------------------------------------------------");
    }

    private String calculerVecteurPi() {
        // Calculer le vecteur π sous la forme (nom_sommet:valeur)
        StringBuilder pi = new StringBuilder("(");
        for (Sommet sommet : graphe.getSommets()) {
            int distance = distances.getOrDefault(sommet.getNom(), Integer.MAX_VALUE);
            pi.append(sommet.getNom()).append(":"); // Ajouter le nom du sommet et ":"
            if (distance == Integer.MAX_VALUE) {
                pi.append("∞"); // Utiliser le symbole infini
            } else {
                pi.append(distance); // Ajouter la distance
            }
            pi.append(",");
        }
        // Supprimer la dernière virgule et ajouter ")"
        if (pi.length() > 1) {
            pi.setLength(pi.length() - 1);
        }
        pi.append(")");
        return pi.toString();
    }
}