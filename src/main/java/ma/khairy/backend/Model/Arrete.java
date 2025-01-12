package ma.khairy.backend.Model;

public class Arrete {
    private final String source;
    private final String destination;
    private final int poids;

    public Arrete(String source, String destination, int poids) {
        this.source = source;
        this.destination = destination;
        this.poids = poids;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public int getPoids() {
        return poids;
    }

    @Override
    public String toString() {
        return "(" + source + " -> " + destination + ", poids: " + poids + ")";
    }
}