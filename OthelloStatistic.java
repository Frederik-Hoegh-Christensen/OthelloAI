public class OthelloStatistic {
    public static void main(String[] args) {
        int size = 8;				// Number of rows and columns on the board
        IOthelloAI ai1 = new DumAI();			// The AI for player 1 if there are no human player
        IOthelloAI ai2 = new AI();			// The AI for player 2
        int numberOfGames = 100;
        int blackWon = 0;
        int whiteWon = 0;
        int draws = 0;


        for (int i = 0; i < numberOfGames; i++) {
            System.out.printf("Playing game %d\n", i+1);
            GameState state = new GameState(size, 1);

            while (!state.isFinished()) {
                if (state.legalMoves().isEmpty()) state.changePlayer();
                Position move = null;
                if (state.getPlayerInTurn() == 1) {
                    System.out.println("Black's turn");
                    move = ai1.decideMove(state);
                }
                else {
                    System.out.println("White's turn");
                    move = ai2.decideMove(state);
                }
                state.insertToken(move);
            }
            int[] terminalState = state.countTokens();
            //black won
            if(terminalState[0] > terminalState[1]) {
                blackWon++;
            }
            else if(terminalState[0] < terminalState[1]) {
                whiteWon++;
            }
            else draws++;
        }
        System.out.printf("---------\nBlack won: %d\nWhite won: %d\nDraws: %d\n", blackWon,whiteWon,draws);
    }
}
