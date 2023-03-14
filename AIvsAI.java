import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class AIvsAI {
    public static void main(String[] args) {
        int blackDepth = Integer.parseInt(args[0]);
        int whiteDepth = Integer.parseInt(args[1]);

        //To disable iterative simulations from depth 1 to aiDepth, comment this for loop out
        // for (int i = 0; i < aiDepth; i++) {
        //     playGames(i+1);
        // }
        
        // And remove the comment below
        playGames(blackDepth, whiteDepth);
        
    }

    private static void playGames(int ai1Depth, int ai2Depth) {
        int size = 8;				        // Number of rows and columns on the board
        Timer ai1Timer = new Timer();
        Timer ai2Timer = new Timer();
        IOthelloAI black = new AI(ai1Depth, ai1Timer);	    // The AI for player 1
        IOthelloAI white = new AI2(ai2Depth, ai2Timer);			// The AI for player 2
        int numberOfGames = 1;            // Number of games to be simulated
        int blackWon = 0;                   // Counter for black wins
        int whiteWon = 0;                   // Counter for white wins
        int draws = 0;                      // Counter for draws
        int averageWhiteTokens = 0;
        int averageBlackTokens = 0;
        boolean writeToFile = false;        // Set to true if you want to write to ./statistics/Statistics.txt

        for (int i = 0; i < numberOfGames; i++) {
            //System.out.printf("Playing game %d\n", i+1);
            GameState state = new GameState(size, 1);

            while (!state.isFinished()) {
                //System.out.println(state.legalMoves().size());
                if (state.legalMoves().isEmpty()) {
                    //System.out.println("No legal moves, changing player");
                    state.changePlayer();
                }
                Position move = null;
                if (state.getPlayerInTurn() == 1) {
                    //System.out.println("Black's turn");
                    move = black.decideMove(state);
                }
                else {
                    //System.out.println("White's turn");
                    move = white.decideMove(state);
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
            System.out.printf("---------\nBlack AI Depth %d\nWhite AI Depth %d\nBlack won: %d\nWhite won: %d\nDraws: %d\n", 
                ai1Depth, ai2Depth, blackWon,whiteWon,draws);
            System.out.printf("The average number of white tokens at the end of a game was: %d\n", averageWhiteTokens);
            System.out.printf("The average number of black tokens at the end of a game was: %d\n", averageBlackTokens);
        }
        else {
            try {
                FileWriter writer = new FileWriter("./statistics/AIVSAI_Statistics.txt", true);
                var bf = new BufferedWriter(writer);
                PrintWriter out = new PrintWriter(bf);
                out.print(String.format("---------\nBlack AI Depth %d\nWhite AI Depth %d\nBlack won: %d\nWhite won: %d\nDraws: %d\n", 
                    ai1Depth, ai2Depth, blackWon,whiteWon,draws));
                out.print(String.format("The average number of white tokens at the end of a game was: %d\n", averageWhiteTokens));
                out.print(String.format("The average number of black tokens at the end of a game was: %d\n", averageBlackTokens));
                bf.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
