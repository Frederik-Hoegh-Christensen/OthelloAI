import java.util.Date;

public class AI implements IOthelloAI {
    // Config options
    public int maxDepth = 4;        // The maximum search depth of the AI
    public int cornerWeight = 3;    // The weight a cornertoken has in the evaluation function
    public int edgeWeight = 2;      // The weight an edge has in the evaluation function
    public Timer timer;             // The timer instance
    private boolean max = false;    // Is the player a max or min player?
    
    public AI() {
        timer = new Timer();
    }

    public AI(int maxDepth, Timer timer) {
        this.maxDepth = maxDepth;
        this.timer = timer;
    }

    @Override
    public Position decideMove(GameState s) {
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
        // Check for terminal state or maxDepth reached
        if (s.isFinished() || depth >= maxDepth) {
            return new Pair(utility(s), null);
        }

        int v = Integer.MIN_VALUE;
        Position move = null;
        if (s.legalMoves().isEmpty()) return minValue(s, alpha, beta, depth + 1);
        for (Position a : s.legalMoves()) {
            Pair pair = minValue(result(s,a), alpha, beta, depth + 1);
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
        // Check for terminal state or maxDepth reached
        if (s.isFinished() || depth >= maxDepth) {
            return new Pair(utility(s), null);
        }
        
        int v = Integer.MAX_VALUE;
        Position move = null;
        if (s.legalMoves().isEmpty()) return maxValue(s, alpha, beta, depth + 1);
        for (Position a : s.legalMoves()) {
            Pair pair = maxValue(result(s,a), alpha, beta, depth + 1);
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

        var eval = white - black;
        eval = eval + nextToEdge(s);
        eval = WeighCornerTokens(s, eval);
        eval = WeighEdgeTokens(s, eval);


        if ((black + white) < (s.getBoard().length / 4)) 
            return -eval;

        return eval;
    }

    private int nextToEdge(GameState s) {
        int length = s.getBoard().length;
        int[][] board = s.getBoard();
        int eval = 0;

        // check if white has pos next to corners 
        if (board[0][1] == 2) 
            eval = eval - cornerWeight;
        if (board[1][0] == 2) 
            eval = eval - cornerWeight;
        if (board[0][length-2] == 2) 
            eval = eval - cornerWeight;
        if (board[1][length-1] == 2) 
            eval = eval - cornerWeight;
        if (board[length-2][0] == 2) 
            eval = eval - cornerWeight;
        if (board[length-2][length-1] == 2) 
            eval = eval - cornerWeight;
        if (board[length-1][1] == 2) 
            eval = eval - cornerWeight;
        if (board[length-1][length-2] == 2) 
            eval = eval - cornerWeight;

        // check if black has pos next to corners 
        if (board[0][1] == 1) 
            eval = eval + cornerWeight;
        if (board[1][0] == 1) 
            eval = eval + cornerWeight;
        if (board[0][length-2] == 1) 
            eval = eval + cornerWeight;
        if (board[1][length-1] == 1) 
            eval = eval + cornerWeight;
        if (board[length-2][0] == 1) 
            eval = eval + cornerWeight;
        if (board[length-2][length-1] == 1) 
            eval = eval + cornerWeight;
        if (board[length-1][1] == 1) 
            eval = eval + cornerWeight;
        if (board[length-1][length-2] == 1) 
            eval = eval + cornerWeight;

        for (int i = 2; i < length-3; i++) {
            if (board[2][i] == 2 || board[length-3][i] == 2 || board[i][2] == 2 || board[i][length-3] == 2) { // white not in good place
                eval = eval - edgeWeight;
            }
            else if (board[2][i] == 1 || board[length-3][i] == 1 || board[i][2] == 1 || board[i][length-3] == 1) { // black not in good place
                eval = eval + edgeWeight;
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
            if(currentToken == 1) eval -= cornerWeight;
            else if(currentToken == 2) eval += cornerWeight;
        }

        return eval;
    }
    
    private int WeighEdgeTokens(GameState s, int eval) {
        int size = s.getBoard().length;
        int[][] board = s.getBoard();

        var edges = BoardPositions.getEdges(size);

        for (Position position : edges) {
            var currentToken = board[position.col][position.row];
            if(currentToken == 1) eval -= edgeWeight;
            else if(currentToken == 2) eval += edgeWeight;
        }
        return eval;
    }
}

