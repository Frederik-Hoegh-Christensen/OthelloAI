import java.util.Date;

public class AI2 implements IOthelloAI {
    // Config options
    public int maxDepth = 8;        // The maximum search depth of the AI
    public int cornerWeight = 10;    // The weight a cornertoken has in the evaluation function
    public int edgeWeight = 8;      // The weight an edge has in the evaluation function
    public Timer timer;             // The timer instance
    private boolean max = false;    // Is the player a max or min player?
    
    public AI2() {
        timer = new Timer();
    }

    public AI2(int maxDepth, Timer timer) {
        this.maxDepth = maxDepth;
        this.timer = timer;
    }

    @Override
    public Position decideMove(GameState s) {
        // A gamestate that simulates the real gamestate
        GameState newState = new GameState(s.getBoard(), s.getPlayerInTurn());
        max = s.getPlayerInTurn() == 2;
        return miniMaxSearch(newState, 0);
    }

    // Essentially a tuple containing a utility value and a move
    private class Pair {
        public int utility;
        public Position move;
        
        public Pair(int utility, Position move) {
            this.utility = utility;
            this.move = move;
        }
    }

    private Position miniMaxSearch(GameState s, int depth) {
        // Used for timings
        Date start = new Date();
        // Start max search if max player, else min search
        var pair = max ? maxValue(s, Integer.MIN_VALUE, Integer.MAX_VALUE, depth) 
            : minValue(s, Integer.MIN_VALUE, Integer.MAX_VALUE, depth);
        // Used for timing
        Date end = new Date();
        //Print timings
        long time = timer.addTime(start, end);
        //System.out.println("Timer: " + time + " miliseconds");
        //System.out.println("Average: " + timer.getAverage() + " miliseconds");
        return pair.move;
    }

    private Pair maxValue(GameState s, int alpha, int beta, int depth) {
        // Check for terminal state or maxDepth reached, returning a utility function if reached.
        if (s.isFinished() || depth >= maxDepth) {
            return new Pair(utility(s), null);
        }

        int v = Integer.MIN_VALUE;
        Position move = null;
        // If there are no legal moves, continue to min's turn 
        if (s.legalMoves().isEmpty()) return minValue(s, alpha, beta, depth + 1);
        // Checking all legal moves and finding the best one, according to our utility function
        for (Position a : s.legalMoves()) {
            // Calling minValue, which will recursively call maxValue until we reach a terminal state or maxDepth
            Pair pair = minValue(result(s,a), alpha, beta, depth + 1);
            // Checking if this move is better than the ones previously found
            if (pair.utility > v) {
                v = pair.utility;
                move = a;
                alpha = Math.max(alpha, v);
            }

            if (v >= beta) {
                // Perform beta-cut
                return new Pair(v, move);
            }
        }
        return new Pair(v, move);
    }

    private Pair minValue(GameState s, int alpha, int beta, int depth) {
        // Check for terminal state or maxDepth reached, returning a utility function if reached.        
        if (s.isFinished() || depth >= maxDepth) {
            return new Pair(utility(s), null);
        }
        
        int v = Integer.MAX_VALUE;
        Position move = null;
         // If there are no legal moves, continue to max's turn 
        if (s.legalMoves().isEmpty()) return maxValue(s, alpha, beta, depth + 1);
        // Checking all legal moves and finding the best one, according to our utility function
        for (Position a : s.legalMoves()) {
            // Calling maxValue, which will recursively call minValue until we reach a terminal state or maxDepth
            Pair pair = maxValue(result(s,a), alpha, beta, depth + 1);
            // Checking if this move is better than the ones previously found
            if (pair.utility < v) {
                v = pair.utility;
                move = a;
                beta = Math.min(beta, v);
            }
            if (v <= alpha) {
                // Perform Alpha-cut
                return new Pair(v, move);
            }
        }
        return new Pair(v, move);
    }

    private GameState result(GameState s, Position p) {
        GameState newState = new GameState(s.getBoard(), s.getPlayerInTurn());
        newState.insertToken(p);
        return newState;
    }

    private int utility(GameState s) {
        int[] tokens = s.countTokens();
        var black = tokens[0];
        var white = tokens[1];

        // Initial evaluation
        var eval = white - black;
        // If the game is done we do not care where the tokens are, so just return the token difference
        if(s.isFinished()) return eval;
        // Evaluating the positions next to edges and corners
        //eval = eval + nextToEdge(s);
        // Evaluating corner tokens
        eval = WeighCornerTokens(s, eval);
        // Evaluating edge tokens
        eval = WeighEdgeTokens(s, eval);

        return eval;
    }

    private int nextToEdge(GameState s) {
        int length = s.getBoard().length;
        int[][] board = s.getBoard();
        int eval = 0;

        // check if white has pos next to corners 
        if (board[0][1] == 2) 
            eval = eval - cornerWeight - 1;
        if (board[1][0] == 2) 
            eval = eval - cornerWeight - 1;
        if (board[0][length-2] == 2) 
            eval = eval - cornerWeight - 1;
        if (board[1][length-1] == 2) 
            eval = eval - cornerWeight - 1;
        if (board[length-2][0] == 2) 
            eval = eval - cornerWeight - 1;
        if (board[length-2][length-1] == 2) 
            eval = eval - cornerWeight - 1;
        if (board[length-1][1] == 2) 
            eval = eval - cornerWeight - 1;
        if (board[length-1][length-2] == 2) 
            eval = eval - cornerWeight - 1;

        // check if black has pos next to corners 
        if (board[0][1] == 1) 
            eval = eval + cornerWeight + 1;
        if (board[1][0] == 1) 
            eval = eval + cornerWeight  + 1;
        if (board[0][length-2] == 1) 
            eval = eval + cornerWeight  + 1;
        if (board[1][length-1] == 1) 
            eval = eval + cornerWeight  + 1;
        if (board[length-2][0] == 1) 
            eval = eval + cornerWeight  + 1;
        if (board[length-2][length-1] == 1) 
            eval = eval + cornerWeight + 1;
        if (board[length-1][1] == 1) 
            eval = eval + cornerWeight + 1;
        if (board[length-1][length-2] == 1) 
            eval = eval + cornerWeight + 1;

        
        for (int i = 2; i < length-3; i++) {

            // Check if white has pos next to edges
            if (board[2][i] == 2)  { // white not in good place
                eval = eval - edgeWeight - 1;
            }
            if (board[length-3][i] == 2)  { // white not in good place
                eval = eval - edgeWeight - 1;
            }
            if (board[i][2] == 2)  { // white not in good place
                eval = eval - edgeWeight - 1;
            }
            if (board[i][length-3] == 2)  { // white not in good place
                eval = eval - edgeWeight - 1;
            }

            // Check if black has pos next to edges
            if (board[2][i] == 1)  { // black not in good place
                eval = eval + edgeWeight + 1;
            }
            if (board[length-3][i] == 1)  { // black not in good place
                eval = eval + edgeWeight + 1;
            }
            if (board[i][2] == 1)  { // black not in good place
                eval = eval + edgeWeight + 1;
            }
            if (board[i][length-3] == 1)  { // black not in good place
                eval = eval + edgeWeight + 1;
            }
        }
        return eval;

    }

    private int WeighCornerTokens(GameState s, int eval){
        int size = s.getBoard().length;
        int[][] board = s.getBoard();

        var corners = BoardPositions.getCorners(size);
        
        for (Position position : corners) {
            var currentToken = board[position.col][position.row];
            if(currentToken == 1) eval -= cornerWeight + 1;
            else if(currentToken == 2) eval += cornerWeight - 1;
        }

        return eval;
    }
    
    private int WeighEdgeTokens(GameState s, int eval) {
        int size = s.getBoard().length;
        int[][] board = s.getBoard();

        var edges = BoardPositions.getEdges(size);

        for (Position position : edges) {
            var currentToken = board[position.col][position.row];
            if(currentToken == 1) eval -= edgeWeight + 1;
            else if(currentToken == 2) eval += edgeWeight - 1;
        }
        return eval;
    }
}

