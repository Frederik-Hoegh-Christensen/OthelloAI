import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * This class is a statistical tool we used for experimenting with different settings,
 * running many games without having to restart each.
 */
public class OthelloStatistic {
    public static void main(String[] args) {
        int aiDepth = 7;
        for (int i = 0; i < aiDepth; i++) {
            playGames(i+1);
        }
        
    }

    private static void playGames(int depth) {
        int size = 8;				        // Number of rows and columns on the board
        Timer aiTimer = new Timer();
        IOthelloAI ai1 = new DumAI();	    // The AI for player 1
        IOthelloAI ai2 = new AI(depth, aiTimer);			// The AI for player 2
        int numberOfGames = 20;            // Number of games to be simulated
        int blackWon = 0;                   // Counter for black wins
        int whiteWon = 0;                   // Counter for white wins
        int draws = 0;                      // Counter for draws
        int averageWhiteTokens = 0;
        int averageBlackTokens = 0;
        boolean writeToFile = true;        // Set to true if you want to write to ./statistics/Statistics.txt

        for (int i = 0; i < numberOfGames; i++) {
            System.out.printf("Playing game %d\n", i+1);
            GameState state = new GameState(size, 1);

            while (!state.isFinished()) {
                if (state.legalMoves().isEmpty()) state.changePlayer();
                Position move;
                if (state.getPlayerInTurn() == 1) {
                    //System.out.println("Black's turn");
                    move = ai1.decideMove(state);
                }
                else {
                    //System.out.println("White's turn");
                    move = ai2.decideMove(state);
                }
                state.insertToken(move);
            }
            int[] terminalState = state.countTokens();

            if(terminalState[0] > terminalState[1]) {
                blackWon++;
            }
            else if(terminalState[0] < terminalState[1]) {
                whiteWon++;
            }
            else draws++;

            averageWhiteTokens += terminalState[1];
            averageBlackTokens += terminalState[0];
        }
        averageWhiteTokens = averageWhiteTokens/numberOfGames;
        averageBlackTokens = averageBlackTokens/numberOfGames;


        // Handles printing and file writing
        if(!writeToFile) {
            System.out.printf("---------\nAI Depth %d\nBlack won: %d\nWhite won: %d\nDraws: %d\n", ((AI) ai2).maxDepth, blackWon,whiteWon,draws);
            System.out.printf("The average number of white tokens at the end of a game was: %d\n", averageWhiteTokens);
            System.out.printf("The average number of black tokens at the end of a game was: %d\n", averageBlackTokens);
            System.out.printf("The average time it took for a search was: %d\n", aiTimer.getAverage());
            System.out.printf("The maximum time it took for a search was: %d\n", aiTimer.getMaxTime());
            System.out.printf("The minimum time it took for a search was: %d\n", aiTimer.getMinTime());
        }
        else {
            try {
                FileWriter writer = new FileWriter("./statistics/Statistics.txt", true);
                var bf = new BufferedWriter(writer);
                PrintWriter out = new PrintWriter(bf);
                out.print(String.format("---------\nAI Depth %d\nBlack won: %d\nWhite won: %d\nDraws: %d\n", depth, blackWon,whiteWon,draws));
                out.print(String.format("The average number of white tokens at the end of a game was: %d\n", averageWhiteTokens));
                out.print(String.format("The average number of black tokens at the end of a game was: %d\n", averageBlackTokens));
                out.printf("The average time it took for a search was: %d\n", aiTimer.getAverage());
                out.printf("The maximum time it took for a search was: %d\n", aiTimer.getMaxTime());
                out.printf("The minimum time it took for a search was: %d\n", aiTimer.getMinTime());
                bf.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
