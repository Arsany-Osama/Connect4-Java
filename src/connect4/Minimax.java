/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connect4;

import java.util.Scanner;

public class Minimax {

    private static final int ROWS = 6;
    private static final int COLS = 7;
    private static final char EMPTY = ' ';
    private static final char PLAYER_R = 'R';
    private static final char PLAYER_Y = 'Y';
    private int expandedNodesCount = 0;

    private char[][] board;
    private Scanner scanner;

    public Minimax(char [][]board) {
  this.board=board;
    }

    private void initializeBoard() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                board[i][j] = EMPTY;
            }
        }
    }
    public int getExpandedNodesCount() {
        return expandedNodesCount;
    }
     void printBoard() {
        for (int i = 0; i < ROWS; i++) {
            System.out.print("|");
            for (int j = 0; j < COLS; j++) {
                System.out.print(board[i][j] + "|");
            }
            System.out.println();
        }
        System.out.println(" 0 1 2 3 4 5 6  \n");
    }



 private boolean isConnect4(char player) {
        // Check horizontally
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j <= COLS - 4; j++) {
                if (board[i][j] == player && board[i][j + 1] == player
                        && board[i][j + 2] == player && board[i][j + 3] == player) {
                    return true;
                }
            }
        }

        // Check vertically
        for (int i = 0; i <= ROWS - 4; i++) {
            for (int j = 0; j < COLS; j++) {
                if (board[i][j] == player && board[i + 1][j] == player
                        && board[i + 2][j] == player && board[i + 3][j] == player) {
                    return true;
                }
            }
        }

        // Check diagonally (left to right)
        for (int i = 0; i <= ROWS - 4; i++) {
            for (int j = 0; j <= COLS - 4; j++) {
                if (board[i][j] == player && board[i + 1][j + 1] == player
                        && board[i + 2][j + 2] == player && board[i + 3][j + 3] == player) {
                    return true;
                }
            }
        }

        // Check diagonally (right to left)
        for (int i = 0; i <= ROWS - 4; i++) {
            for (int j = 3; j < COLS; j++) {
                if (board[i][j] == player && board[i + 1][j - 1] == player
                        && board[i + 2][j - 2] == player && board[i + 3][j - 3] == player) {
                    return true;
                }
            }
        }

        return false;
    }

private int minimax(int depth, boolean maximizingPlayer) {
    expandedNodesCount++;
    if (depth == 0 || isFull()) {
       
        return evaluate(depth);
    }

    if (maximizingPlayer) {
        int maxEval = Integer.MIN_VALUE;
        
        for (int j = 0; j < COLS; j++) {
            if (isValidMove(j)) {
                makeMove(j, PLAYER_Y);
                int eval = minimax(depth - 1, false);
                maxEval = Math.max(maxEval, eval);
                //printBoard();
                undoMove(j);
            }
        }
        
        return maxEval;
        
    } else {
        int minEval = Integer.MAX_VALUE;
        
        for (int j = 0; j < COLS; j++) {
            if (isValidMove(j)) {
                makeMove(j, PLAYER_R);
                int eval = minimax(depth - 1, true);
                minEval = Math.min(minEval, eval);
                //printBoard();
                undoMove(j);
                
            }
        }
        
        return minEval;
    }
}

     int findBestMove(int depth) {
         expandedNodesCount=0;
        int bestMove = -1;
        int bestValue = Integer.MIN_VALUE;
        for (int j = 0; j < COLS; j++) {
            if (isValidMove(j)) {
                
                makeMove(j, PLAYER_Y);
                int moveValue = minimax(depth, false);
                undoMove(j);
                if (moveValue > bestValue) {
                    bestValue = moveValue;
                    bestMove = j;
                }
            }
        }
        return bestMove;
    }
     
        private int evaluate(int depth) {
     /* 
       int countY = countConnect4s(PLAYER_Y);
        int countR = countConnect4s(PLAYER_R);

        int scoreY = countY * 500;  // Weight for player Y's Connect 4s
        int scoreR = countR * 1000;  // Weight for player R's Connect 4s

        // Check for potential winning moves
        int countPotentialY = countConnect4s(PLAYER_Y);
        int countPotentialR = countConnect4s(PLAYER_R);

        int scorePotentialY = countPotentialY * 500;  // Weight for potential Connect 4s for player X
        int scorePotentialR = countPotentialR * 500;  // Weight for potential Connect 4s for player O

        // Check for blocking opponent's potential winning moves
        int countBlockY = countConnect4s(PLAYER_Y);
        int countBlockR = countConnect4s(PLAYER_R);

        int scoreBlockY = countBlockY * 200;  // Weight for blocking potential Connect 4s for player X
        int scoreBlockR = countBlockR * 200;  // Weight for blocking potential Connect 4s for player O

        int score = scoreY - scoreR + scorePotentialY - scorePotentialR + scoreBlockY - scoreBlockR;

        return score - depth;
       */
     
    
          int score = 0;
                 // Evaluate By Rows (Horizontally)طرنشات
        for(int row = 0 ; row < 6 ; row++){
            for(int col = 0 ; col <= 3 ; col++){
                score += evaluateWindow(board[row][col] , board[row][col + 1] , board[row][col + 2] , board[row][col + 3]);
            }
        }
                // Evaluate By Columns (Vertically)
        for(int col = 0 ; col < 7 ; col++){
            for(int row = 0 ; row <= 2 ; row++){
                score += evaluateWindow(board[row][col] , board[row+1][col] , board[row+2][col] , board[row+3][col]);
            }
        }
                // Evaluate Diagonally(From Top-Left To Bottom-Right)
        // I Want To Evaluate A Part Of Whole Board
        for (int row = 0; row <= 2; row++) {
            for (int col = 0; col <= 3; col++) {
                score += evaluateWindow(board[row][col], board[row + 1][col + 1], board[row + 2][col + 2], board[row + 3][col + 3]);
            }
        }
                // Evaluate diagonally (From Top-Right To Bottom-Left)
        // I Want To Evaluate A Part Of Whole Board
        for (int row = 0; row <= 2; row++) {
            for (int col = 3; col < 7; col++) {
                score += evaluateWindow(board[row][col], board[row + 1][col - 1], board[row + 2][col - 2], board[row + 3][col - 3]);
            }
        }
        return score; // Return The Final Score Of The Current State(Board)
        
    }
   
       private int evaluateWindow(char c1, char c2, char c3, char c4) {
        int score = 0;
        int numPlayer1 = 0;
        int numAI = 0;

        
        if (c1 == PLAYER_Y) numAI++;
        else if (c1 == PLAYER_R) numPlayer1++;

        if (c2 == PLAYER_Y) numAI++;
        else if (c2 == PLAYER_R) numPlayer1++;

        if (c3 == PLAYER_Y) numAI++;
        else if (c3 == PLAYER_R) numPlayer1++;

        if (c4 == PLAYER_Y) numAI++;
        else if (c4 == PLAYER_R) numPlayer1++;

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

    private int countConnect4s(char player) {
        int count = 0;

        // Check horizontally
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j <= COLS - 4; j++) {
                if (board[i][j] == player && board[i][j + 1] == player
                    && board[i][j + 2] == player && board[i][j + 3] == player) {
                    count++;
                }
            }
        }

        // Check vertically
        for (int i = 0; i <= ROWS - 4; i++) {
            for (int j = 0; j < COLS; j++) {
                if (board[i][j] == player && board[i + 1][j] == player
                        && board[i + 2][j] == player && board[i + 3][j] == player) {
                    count++;
                }
            }
        }

        // Check diagonally (left to right)
        for (int i = 0; i <= ROWS - 4; i++) {
            for (int j = 0; j <= COLS - 4; j++) {
                if (board[i][j] == player && board[i + 1][j + 1] == player
                        && board[i + 2][j + 2] == player && board[i + 3][j + 3] == player) {
                    count++;
                }
            }
        }

        // Check diagonally (right to left)
        for (int i = 0; i <= ROWS - 4; i++) {
            for (int j = 3; j < COLS; j++) {
                if (board[i][j] == player && board[i + 1][j - 1] == player
                        && board[i + 2][j - 2] == player && board[i + 3][j - 3] == player) {
                    count++;
                }
            }
        }

        return count;
    }
     
          boolean isFull() {
        for (int j = 0; j < COLS; j++) {
            if (board[0][j] == EMPTY) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidMove(int col) {
        return board[0][col] == EMPTY;
    }

     void makeMove(int col, char player) {
        for (int i = ROWS - 1; i >= 0; i--) {
            if (board[i][col] == EMPTY) {
                board[i][col] = player;
                break;
            }
        }
    }

     void undoMove(int col) {
        for (int i = 0; i < ROWS; i++) {
            if (board[i][col] != EMPTY) {
                board[i][col] = EMPTY;
                break;
            }
        }
    }
    public static void main(String[] args) {
        Scanner sc=new Scanner (System.in);
        System.out.println("Enter depth");
        int x=sc.nextInt();
                char board[][]=new char[6][7];
        for(int i = 0 ; i < ROWS ; i++){
            for(int j = 0 ; j < COLS ; j++){
               board[i][j] = EMPTY;
            }
        }
       long startTime=0,endTime=0;
               
              Minimax game = new Minimax(board);
             char currentPlayer = PLAYER_Y;

        while (!game.isFull()) {
            game.printBoard();

            if (currentPlayer == PLAYER_Y) {
                System.out.println("Human's turn (R). Enter column (0-6): ");
                int col = sc.nextInt();
                if (game.isValidMove(col)) {
                    game.makeMove(col, PLAYER_R);
                } else {
                    System.out.println("Invalid move. Try again.");
                    continue;
                }
            } else {
                System.out.println("AI's turn (Y).");
                 startTime = System.nanoTime();
                int aiMove = game.findBestMove(x);
                 endTime = System.nanoTime();
                game.makeMove(aiMove, PLAYER_Y);
                System.out.println("AI chooses column " + aiMove);
                            System.out.println("Time Taken: " + (endTime - startTime) / 1000000);
            System.out.println("Expanded Nodes: " + game.getExpandedNodesCount());
            }

            currentPlayer = (currentPlayer == PLAYER_R) ? PLAYER_Y : PLAYER_R;
        }

        game.printBoard();
        int countX = game.countConnect4s(PLAYER_Y);
        int countO = game.countConnect4s(PLAYER_R);

        if (countX > countO) {
            System.out.println("AI wins!with " + countX + " Connect 4s!");
            
            System.out.println("Time Taken: " + (endTime - startTime) / 1000000);
            System.out.println("Expanded Nodes: " + game.getExpandedNodesCount());
        } else if (countO > countX) {
            System.out.println("Human wins!" + countO + " Connect 4s!");
                
            System.out.println("Time Taken: " + (endTime - startTime) / 1000000);
            System.out.println("Expanded Nodes: " + game.getExpandedNodesCount());
        } else {
            System.out.println("It's a tie!");
              
            System.out.println("Time Taken: " + (endTime - startTime) / 1000000);
            System.out.println("Expanded Nodes: " + game.getExpandedNodesCount());
                   
        }

        sc.close();
        
    }
}
