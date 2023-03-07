public class BoardPositions {
    private static int[][] corners = null;
    private static int[][] edges = null;

    public static int[][] getCorners(int size) {
        if (corners == null) generateCorners(size);
        return corners;
    }

    public static void generateCorners(int size) {
        corners = new int[][] {
            {0, 0},
            {0, size-1},
            {size-1, 0},
            {size-1, size-1},
        };
    }

    public static int[][] getEdges(int size) {
        if (edges == null) generateEdges(size);
        return edges;
    }

    /*public static void generateEdges(int size) {
        for (int x = 1; x < size-2; x++) {
            edges[x][0] = 
        }
    }
    */
}