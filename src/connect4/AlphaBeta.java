package connect4;
import java.util.Scanner;

class AlphaBeta{
    private static final int ROWS = 6;
    private static final int COLS = 7;
    
    private static int Pscore = 0;
    private static int AiScore = 0;

    private static final char SPACE = ' ';

    private static final char PLAYER = 'R';

    private static final char AI = 'Y';
    
    private int expandedNodesCount = 0;
    
    private char[][] board;

    public AlphaBeta(char[][]board){
       this.board=board;
    }

    public void printBoard(){ // Time O[1] , Space O[1]
        // print the column indexes
        System.out.print(" ");
        for(int i = 0 ; i < COLS ; i++) {
            System.out.print(i + " ");
        }
        System.out.println();

        // print the board itself
        for(int i = 0 ; i < ROWS ; i++) {
            System.out.print("|");
            for (int j = 0; j < COLS; j++ ){
                System.out.print(board[i][j] + "|");
            }
            System.out.println();
        }
        System.out.println("=======================");
    }
    
    public int getExpandedNodesCount() {
        return expandedNodesCount;
    }

    // Note: Player Insert In A Specific Column From Bottom To Top
    public boolean MakeMove(int col, char player) {
        for (int i = ROWS - 1; i >= 0; i--) {
            if (board[i][col] == SPACE) {
                board[i][col] = player;
                return true;
            }
        }
        return false;
    }
    
    //Question: When The Column Is Full?
    //Answer: When The First Row (Row Index 0) At Specific Col Is Full
    // [0][colIndex] row 0 because it is the last row can user insert his round
    public boolean isColumnFull(int colIndex){ // Time O[1] , Space O[1]
        return board[0][colIndex] != SPACE;
    }

    //Question: when the board is full?
    //Answer: when the first row is full (row index 0) ([0][0] is full , [0][1] is full , .... , [0][6] is full)
    private boolean isBoardFull(){ // Time O[1] , Space O[1]
        for(int col = 0 ; col < COLS ; col++){
            if(!isColumnFull(col))
                return false;
        }
        return true;
    }

    public char checkWinner(){ // Time O[1] , Space O[1]
        // Check By Rows (Horizontally) The Row Is Fixed While Col Is Changing
        for(int row = 0 ; row < ROWS ; row++){
            for(int col = 0 ; col <= COLS - 4 ; col++){
                // I Just Want Check Indices ([row][0] , [row][1] , [row][2] , [row][3]) Note The Whole Row
                // 7 - 4 = 3 (From 0 To 3)
                char token = board[row][col];
                if(
                        token != SPACE &&
                                token == board[row][col + 1] &&
                                token == board[row][col + 2] &&
                                token == board[row][col + 3]
                ) return token; // Then He Wins Horizontally
            }
        }

        // Check By Column (Vertically) The Column Is Fixed While Row Is Changing
        for(int col = 0 ; col < COLS ; col++){
            for(int row = 0 ; row <= ROWS - 4 ; row++) {
                // I Just Check Indices ([0][col] , [1][col] , [2][col])
                // 6 - 4 = 2 (From 0 To 2)
                char token = board[row][col];
                if(
                        token != SPACE &&
                                token == board[row + 1][col] &&
                                token == board[row + 2][col] &&
                                token == board[row + 3][col]
                ) return token; // Then He Wins Vertically
            }
        }

        // Check Diagonally(From Top-Left To Bottom-Right)
        for(int row = 0 ; row <= ROWS - 4 ; row++){
            for(int col = 0 ; col <= COLS - 4 ; col++){
                char token = board[row][col];
                if(
                        token != SPACE &&
                                token == board[row + 1][col + 1]   &&
                                token == board[row + 2][col + 2]   &&
                                token == board[row + 3][col + 3]
                ) return token; // Then He Wins Diagonally(From Top-Left To Bottom-Right)
            }
        }
        
        // Check Diagonally(From Top-Right To Bottom-Left)
        // The Row Index Is Increasing While The Column Index Is Decreasing (Pattern)
        for(int row = 0 ; row <= ROWS - 4 ; row++){
            for(int col = 3 ; col < COLS ; col++){
                // I Want Start From col Index 3 Because Before That Will Lead To Overflow (Draw And You Will Understand It)
                char token = board[row][col];
                if(
                        token != SPACE &&
                                token == board[row + 1][col - 1] &&
                                token == board[row + 2][col - 2] &&
                                token == board[row + 3][col - 3]
                ) return token; // Then He Wins Diagonally(From Top-Right To Bottom-Left)
            }
        }
        return SPACE; // If No One Wins
    }

    public boolean isGameOver(){
        // I Have Two Cases If The Game Is Over
        // Case 1: If There Is A Win Player(Human Or AI)
        // Case 2: The Board Is Full But No One Wins
        return checkWinner() != SPACE && isBoardFull();
    }
    
    
    //This Is The Heuristic Function For The Connect 4 Game
    /*
     * Idea
     * AI Will Score 100 If It Accumulates 4 Consecutive Cells Which Is Means The AI(Wins)
     * If The AI Accumulates 3 Consecutive Cells , It Will Score 5
     * If The AI Accumulates 2 Consecutive Cells , It Will Score 2
     *
     * If The Human Player Score 100 (He/She Accumulates 4 Consecutive Cells) The AI Will Lose (Score += -100)
     * If The Human Player Accumulates 3 Consecutive Cells . The AI Will Lose 5 Points (Score += -5)
     * If The Human Player Accumulates 2 Consecutive Cells . The AI Will Lose 2 Points (Score += -2)
     * */
    private int evaluateWindow(char c1, char c2, char c3, char c4) {
        int score = 0;
        int numPlayer1 = 0;
        int numAI = 0;

        
        if (c1 == AI) numAI++;
        else if (c1 == PLAYER) numPlayer1++;

        if (c2 == AI) numAI++;
        else if (c2 == PLAYER) numPlayer1++;

        if (c3 == AI) numAI++;
        else if (c3 == PLAYER) numPlayer1++;

        if (c4 == AI) numAI++;
        else if (c4 == PLAYER) numPlayer1++;

        // Evaluate for AI
        if (numAI == 4) {
            score += 100;
        } else if (numAI == 3 && numPlayer1 == 0) {
            score += 5;
        } else if (numAI == 2 && numPlayer1 == 0) {
            score += 2;
        }

        // Evaluate for PLAYER
        if (numPlayer1 == 4) {
            score -= 100;
        } else if (numPlayer1 == 3 && numAI == 0) {
            score -= 5;
        } else if (numPlayer1 == 2 && numAI == 0) {
            score -= 2;
        }

         // Return The Final Score Of Specific Cells (C1 , C2 , C3 , C4)
        return score;
    }
    
    // The Same Logic As checkWinner() Function But Here I Want To Calculate The Total Score Of The Current State
    private int evaluateBoard(){ // Time O[1] , Space O[1]
        int score = 0;
        
        // Evaluate By Rows (Horizontally)
        // I Just Want Evaluate Indices
        /*
         * [row][0] And Three Indices After it
         * [row][1] And Three Indices After it
         * [row][2] And Three Indices After it
         * [row][3] And Three Indices After it
         * */
        for(int row = 0 ; row < ROWS ; row++){
            for(int col = 0 ; col <= COLS - 4 ; col++){
                score += evaluateWindow(board[row][col] , board[row][col + 1] , board[row][col + 2] , board[row][col + 3]);
            }
        }
        
        // Evaluate By Columns (Vertically)
        // I Just Want Evaluate Indices
        /*
         * [0][col] And Three Indices Under it
         * [1][col] And Three Indices Under it
         * [2][col] And Three Indices Under it
         * */
        for(int col = 0 ; col < COLS ; col++){
            for(int row = 0 ; row <= ROWS - 4 ; row++){
                score += evaluateWindow(board[row][col] , board[row+1][col] , board[row+2][col] , board[row+3][col]);
            }
        }
        
        // Evaluate Diagonally(From Top-Left To Bottom-Right)
        // I Want To Evaluate A Part Of Whole Board
        /*
         * [0][0]  [0][1]  [0][2]  [0][3]
         * [1][0]  [1][1]  [1][2]  [1][3]
         * [2][0]  [2][1]  [2][2]  [2][3]
         * */
        for (int row = 0; row <= ROWS - 4; row++) {
            for (int col = 0; col <= COLS - 4; col++) {
                score += evaluateWindow(board[row][col], board[row + 1][col + 1], board[row + 2][col + 2], board[row + 3][col + 3]);
            }
        }
        
        // Evaluate diagonally (From Top-Right To Bottom-Left)
        // I Want To Evaluate A Part Of Whole Board
        /*
         * [0][3]  [0][4]  [0][5]  [0][6]
         * [1][3]  [1][4]  [1][5]  [1][6]
         * [2][3]  [2][4]  [2][5]  [2][6]
         * */
        for (int row = 0; row <= ROWS - 4; row++) {
            for (int col = 3; col < COLS; col++) {
                score += evaluateWindow(board[row][col], board[row + 1][col - 1], board[row + 2][col - 2], board[row + 3][col - 3]);
            }
        }
        return score; // Return The Final Score Of The Current State(Board)
    }

    //For The Evaluation
    private void undoMove(int col){
        for(int row = 0 ; row < ROWS ; row++){
            if(board[row][col] != SPACE){
                board[row][col] = SPACE;
                break;
            }
        }
    }
    
    public int minimax(int depth , int alpha , int beta , boolean isMaximizingPlayer){
        expandedNodesCount++;        
        // If I Reach To Depth 0 That Is Means I Reach The Terminal Nodes
        // If The Game Is Over That Is Means I Also Reach To The Terminal Nodes
        // If I Reach To The Terminal Nodes , The Evaluation Function Will Decide Which One Is Wins
        if(depth == 0 || isGameOver())
            return evaluateBoard();

        if(isMaximizingPlayer) { // AI Player Will Play His Round (Maximizer Player)
            int maxEval = Integer.MIN_VALUE;
            for (int col = 0; col < COLS; col++) {
                // We Should Check First If The Column Not Full Before Making a Move
                if (!isColumnFull(col)) {
                    MakeMove(col, AI);

                    int eval = minimax(depth - 1, alpha, beta, false); // Human Player's Turn
                    // Undo The Move To Explore Other Possibilities (Other States)
                    undoMove(col); // ==> Generates Other Board States

                    maxEval = Math.max(maxEval , eval); 
                    alpha = Math.max(alpha , eval); 

                    if (beta <= alpha) // Pruning Condition
                        break;
                }
            }
            return maxEval; // Return His Maximum Optimal Solution For The Current State
        }

        else{ // Human Turn (Minimizer Player)
            int minEval = Integer.MAX_VALUE;
            for(int col = 0 ; col < COLS ; col++){
                // We Should Check First If The Column Not Full Before Making a Move
                if(!isColumnFull(col)){
                    MakeMove(col , PLAYER);

                    int eval = minimax(depth - 1 , alpha , beta , true); // AI's Turn
                    // Undo The Move To Explore Other Possibilities (Other States)
                    undoMove(col);  // ==> Generates Other Board States

                    minEval = Math.min(minEval , eval); 
                    beta = Math.min(beta , eval); 

                    if(beta <= alpha) // Pruning Condition
                        break;
                }
            }
            return minEval;  // Return His Maximum Optimal Solution For The Current State
        }
    }

     int findBestMove(int depth) {
        expandedNodesCount = 0;
        int bestMove = -1; // Best Optimal Move
        int bestScore = Integer.MIN_VALUE; // Best Optimal Score

        for (int col = 0; col < COLS; col++) {
            if (!isColumnFull(col)) {
                
                MakeMove(col, AI); 
                int score = minimax(depth, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
                undoMove(col); // Generate Other Board States

                if (score > bestScore) { 
                    // If The Score That Minimax Generates > The BestScore 
                    // Then Update Its BestScore And BestMove Will Take The Best Col Can Play On It
                    bestScore = score;
                    bestMove = col;
                }
            }
        }

        return bestMove;
    }


  
    public static void main(String[] args) {
        char board[][]=new char[6][7];
        for(int i = 0 ; i < ROWS ; i++){
            for(int j = 0 ; j < COLS ; j++){
               board[i][j] = SPACE;
            }
        }
        Scanner scanner = new Scanner(System.in);
        AlphaBeta game=new AlphaBeta(board);
        System.out.println("Enter depth");
        int d=scanner.nextInt();
        while (!game.isGameOver()) {
            game.printBoard();

            int col;
            do {
                System.out.print("Your Turn, enter your move (column 0-6): ");
                col = scanner.nextInt();
                while(!(col >= 0 && col <= 6)){
                    System.out.print("Please Enter From 0 To 6: ");
                    col = scanner.nextInt();
                }
            } while (game.isColumnFull(col));

            game.MakeMove(col, PLAYER);

            if (game.checkWinner()=='R') {
                game.printBoard();
                System.out.println("Player 1 wins!");
                Pscore++;
                System.out.println("Player : "+Pscore+" VS Ai :"+AiScore);
            }
            
            game.printBoard();
            
            // Calculate Time Decision
            long startTime = System.nanoTime();
            int aiMove = game.findBestMove(d);
            game.MakeMove(aiMove, AI);
            long endTime = System.nanoTime();
            
            
            
            System.out.println("AI chooses column " + aiMove);
            System.out.println("Time Taken: " + (endTime - startTime) / 1000000);
            System.out.println("Expanded Nodes: " + game.getExpandedNodesCount());
            
           

            if (game.checkWinner()=='Y') {
                game.printBoard();
                System.out.println("AI wins!");
                AiScore++;
                System.out.println("Player : "+Pscore+" VS Ai :"+AiScore);
                
            }
            if(game.isGameOver())
            {
              game.printBoard();
                System.out.println("Game Over!");
                System.out.println("Player : "+Pscore+" VS Ai :"+AiScore);
            }
        }
    }
}