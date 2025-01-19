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
    private Graphe graphe;
    private final List<String> detailsMessages; // Liste pour stocker les messages détaillés

    public DijkstraService() {
        this.distances = new HashMap<>();
        this.predecesseurs = new HashMap<>();
        this.sommetsNonVisites = new HashSet<>();
        this.sommetsVisites = new HashSet<>();
        this.graphe = new Graphe();
        this.detailsMessages = new ArrayList<>(); // Initialisation de la liste
    }

    public void reinitialiser() {
        this.distances.clear();
        this.predecesseurs.clear();
        this.sommetsNonVisites.clear();
        this.sommetsVisites.clear();
        this.detailsMessages.clear();
    }
    public Map<String, Object> calculerCheminMinimal(Graphe graphe, String sommetDepartNom, boolean details) {
        reinitialiser();


        this.graphe = graphe;
        distances.put(sommetDepartNom, 0);
        predecesseurs.put(sommetDepartNom, null); // Le nœud de départ n'a pas de prédécesseur
        sommetsNonVisites.addAll(graphe.getSommets().stream().map(Sommet::getNom).collect(Collectors.toSet()));

        // Affichage initial
        if (details) {
            afficherEtatInitial(sommetDepartNom);
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
                        predecesseurs.put(voisinNom, sommetCourantNom); // Mettre à jour le prédécesseur
                    }
                }
            }
        }

        // Construire les chemins
        Map<String, List<String>> chemins = reconstruireChemins(sommetDepartNom);

        // Retourner les résultats avec les messages détaillés
        Map<String, Object> result = new HashMap<>();
        result.put("distances", new HashMap<>(distances));
        result.put("chemins", chemins);

        if (details) {
            result.put("details", detailsMessages);
        }
        return result;
    }

    private Map<String, List<String>> reconstruireChemins(String sommetDepartNom) {
        Map<String, List<String>> chemins = new HashMap<>();
        for (String sommet : graphe.getSommets().stream().map(Sommet::getNom).collect(Collectors.toSet())) {
            List<String> chemin = new ArrayList<>();
            String courant = sommet;
            while (courant != null) {
                chemin.add(0, courant); // Ajouter au début de la liste pour inverser l'ordre
                courant = predecesseurs.get(courant);
            }
            if (!chemin.isEmpty() && chemin.get(0).equals(sommetDepartNom)) {
                chemins.put(sommet, chemin);
            } else {
                chemins.put(sommet, Collections.singletonList("Inaccessible"));
            }
        }
        return chemins;
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

    private void afficherEtatInitial(String sommetDepartNom) {
        String S = "{" + sommetDepartNom + "}";
        String S_ = sommetsNonVisites.stream()
                .filter(s -> !s.equals(sommetDepartNom))
                .collect(Collectors.joining(", ", "[", "]"));
        String pi = calculerVecteurPi();

        // Ajouter le message à la liste
        detailsMessages.add("Initialisation");
        detailsMessages.add("S=" + S + " ; 𝑆−=" + S_ + " ; π=" + pi);
    }

    private void afficherEtatIteration(String sommetCourantNom, int iteration) {
        // Affichage de l'étape i, des successeurs et de la mise à jour des distances
        detailsMessages.add(iteration + "ère Itération :");
        detailsMessages.add("Les successeurs de " + sommetCourantNom + " dans 𝑆−");

        Sommet sommetCourant = graphe.getSommet(sommetCourantNom);
        for (Arrete arrete : sommetCourant.getArretes()) {
            String voisinNom = arrete.getDestination();
            int nouvelleDistance = distances.getOrDefault(sommetCourantNom, Integer.MAX_VALUE) + arrete.getPoids();
            if (nouvelleDistance < distances.getOrDefault(voisinNom, Integer.MAX_VALUE)) {
                detailsMessages.add("π(" + voisinNom + ")=min(" +
                        (distances.containsKey(voisinNom) ?
                                (distances.get(voisinNom) == Integer.MAX_VALUE ? "∞" : distances.get(voisinNom)) : "∞") +
                        "," + distances.get(sommetCourantNom) + "+" + arrete.getPoids() + ")=" + nouvelleDistance);
            }
        }

        String pi = calculerVecteurPi();
        detailsMessages.add("Le nouveau vecteur π=" + pi);
        detailsMessages.add("-----------------------------------------------------");
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