import java.util.Date;

public class AI implements IOthelloAI {
    
    public int maxDepth = 7;
    public int cornerWeight = 3;
    public int edgeWeight = 2;
    public boolean max = false;

    @Override
    public Position decideMove(GameState s) {
        GameState newState = new GameState(s.getBoard(), s.getPlayerInTurn());
        max = s.getPlayerInTurn() == 2;
        return miniMaxSearch(newState, 0);

    }
    
    private class Pair {
        public int utility;
        public Position move;
        
        public Pair(int utility, Position move) {
            this.utility = utility;
            this.move = move;
        }
    }
    
    private Position miniMaxSearch(GameState s, int depth) {
        Date start = new Date();
        var pair = maxValue(s, Integer.MIN_VALUE, Integer.MAX_VALUE, depth);
        Date end = new Date();
        System.out.println("Timer: " + Timer.addTime(start, end) + " miliseconds");
        System.out.println("Average: " + Timer.getAverage() + " miliseconds");
        return pair.move;
    }

    private Pair maxValue(GameState s, int alpha, int beta, int depth) {
        //System.out.println("Searching max");
        if (s.isFinished() || depth >= maxDepth) {
            //System.out.println("terminal state");
            return new Pair(utility(s), null);
        }
        
        int v = Integer.MIN_VALUE;
        Position move = null;
        //System.out.println("number of legal moves: " + s.legalMoves().size());
        for (Position a : s.legalMoves()) {
            Pair pair = minValue(result(s,a), alpha, beta, depth + 1);
            if (pair.utility > v) {
                v = pair.utility;
                move = a;
                alpha = Math.max(alpha, v);
            }
            if (v >= beta) {
                //System.out.println("beta cut");
                return new Pair(v, move);
            }
        }
        return new Pair(v, move);
    }

    private Pair minValue(GameState s, int alpha, int beta, int depth) {
        //System.out.println("Searching min");
        if (s.isFinished() || depth >= maxDepth) {
            //System.out.println("terminal state");
            return new Pair(utility(s), null);
        }
        
        int v = Integer.MAX_VALUE;
        Position move = null;
        //System.out.println("number of legal moves: " + s.legalMoves().size());
        for (Position a : s.legalMoves()) {
            Pair pair = maxValue(result(s,a), alpha, beta, depth + 1);
            if (pair.utility < v) {
                v = pair.utility;
                move = a;
                beta = Math.min(beta, v);
            }
            if (v <= alpha) {
                //System.out.println("alpha cut");
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

        var eval = black - white;
        eval = eval + nextToEdge(s);
        eval = WeighCornerTokens(s, eval);
        eval = WeighEdgeTokens(s, eval);


        if ((black + white) < (s.getBoard().length / 4)) 
            return max ? -eval : eval;

        return max ? eval : -eval;
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
            eval = eval - cornerWeight;
        if (board[1][0] == 1) 
            eval = eval - cornerWeight;
        if (board[0][length-2] == 1) 
            eval = eval - cornerWeight;
        if (board[1][length-1] == 1) 
            eval = eval - cornerWeight;
        if (board[length-2][0] == 1) 
            eval = eval - cornerWeight;
        if (board[length-2][length-1] == 1) 
            eval = eval - cornerWeight;
        if (board[length-1][1] == 1) 
            eval = eval - cornerWeight;
        if (board[length-1][length-2] == 1) 
            eval = eval - cornerWeight;

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

