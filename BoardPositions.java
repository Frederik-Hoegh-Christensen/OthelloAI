import java.util.ArrayList;

public class BoardPositions {
    private static ArrayList<Position> corners = null;
    private static ArrayList<Position> edges = null;

    public static ArrayList<Position> getCorners(int size) {
        if (corners == null) generateCorners(size);
        return corners;
    }

    public static void generateCorners(int size) {
        
        corners = new ArrayList<>();
        corners.add(new Position(0, 0));
        corners.add(new Position(0, size-1));
        corners.add(new Position(size-1, 0));
        corners.add(new Position(size-1, size-1));
    }

    public static ArrayList<Position> getEdges(int size) {
        if (edges == null) generateEdges(size);
        return edges;
    }

    public static void generateEdges(int size) {
        edges = new ArrayList<>();
        for (int x = 2; x < size-3; x++) {
            edges.add(new Position(x, 0));
            edges.add(new Position(x, size-1));
            edges.add(new Position(0, x));
            edges.add(new Position(size-1, x));
        }
    }
    
}