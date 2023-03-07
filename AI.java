import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

public class AI implements IOthelloAI {
    
    public int maxDepth = 8;
    public int cornerWeight = 3;
    public int edgeWeight = 2;

    @Override
    public Position decideMove(GameState s) {
        GameState newState = new GameState(s.getBoard(), s.getPlayerInTurn());
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
        eval = WeighCornerTokens(s, eval);


        if ((black + white) < (s.getBoard().length / 4)) 
            return -eval;
        return eval;
    }

    private int WeighCornerTokens(GameState s, int eval){
        int size = s.getBoard().length;
        int[][] board = s.getBoard();

        var corners = BoardPositions.getCorners(size);
        
        for (int[] currentRow : corners) {
            if(board[currentRow[0]][currentRow[1]] == 1) eval -= cornerWeight;
            else if(board[currentRow[0]][currentRow[1]] == 2) eval += cornerWeight;
        }

        return eval;
    }
    
}

