package connect4;
import java.util.Optional;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.Optional;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;

public class AlphaBetaTree {
    private char[][] gameState;
    private int depth;

    public AlphaBetaTree(char[][] gameState, int depth) {
        this.gameState = gameState;
        this.depth = depth;
    }

    public void printBoard() {
        for (int row = 0; row < gameState.length; row++) {
            for (int col = 0; col < gameState[row].length; col++) {
                System.out.print(gameState[row][col] + " ");
            }
            System.out.println();
        }
    }

public void buildTreeAndSaveAsImage(String imagePath) {
    Node rootNode = new Node(gameState, depth, true); // Create the root node
    buildTree(rootNode, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, true); // Build the tree using alpha-beta pruning

    // Create a GUI frame to visualize the tree
    JFrame frame = new JFrame("Alpha-Beta Tree");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(12000, 10000);

    // Create a panel to draw the tree
    JPanel panel = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawTree(g, rootNode, getWidth(), 0, 0);
        }
    };

    frame.add(panel);
   frame.setVisible(true);

    // Save the panel as an image
    BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
    Graphics2D graphics2D = image.createGraphics();
    panel.paint(graphics2D);
    graphics2D.dispose();

    try {
        ImageIO.write(image, "jpg", new File(imagePath));
        System.out.println("Tree image saved successfully.");
    } catch (IOException e) {
        System.out.println("Failed to save the tree image: " + e.getMessage());
    }
}
private void drawTree(Graphics g, Node node, int panelWidth, int x, int y) {
    if (node.getChildren().isEmpty()) {
        return;
    }

    int childCount = node.getChildren().size();
    int childWidth = panelWidth / childCount;

    int startX = x + childWidth / 2;
    int startY = y + 100;

    for (int i = 0; i < childCount; i++) {
        Node child = node.getChildren().get(i);
        int childX = startX + i * childWidth - childWidth / 2;
        int childY = startY + 150;
        
 g.setColor(Color.BLACK);
        g.drawLine(x + 50, y + 50, childX + 50, childY);
       
        drawBoard(g, child, childX, childY, childWidth);  // Pass the child node

        if (!child.isWinningState()) { // Check for winning state
            drawTree(g, child, childWidth, childX, childY);
        }
    }
}

private void drawBoard(Graphics g, Node node, int x, int y, int boxWidth) {
    char[][] board = node.getGameState();
    int cellSize = boxWidth / board.length;
    int margin = 5; // Reduce the margin for spacing between states

    for (int row = 0; row < board.length; row++) {
        for (int col = 0; col < board[row].length; col++) {
            int cellX = x + col * cellSize + margin;
            int cellY = y + row * cellSize + margin;
            int cellSizeAdjusted = cellSize - 2 * margin; // Adjust the cell size by subtracting the margin

            // Draw the cell
            g.setColor(Color.WHITE);
            g.fillOval(cellX, cellY, cellSizeAdjusted, cellSizeAdjusted);
            g.setColor(Color.BLACK);
            g.drawOval(cellX, cellY, cellSizeAdjusted, cellSizeAdjusted);

            // Draw the disk
            if (board[row][col] == 'R') {
                g.setColor(Color.RED);
                g.fillOval(cellX, cellY, cellSizeAdjusted, cellSizeAdjusted);
            } else if (board[row][col] == 'Y') {
                g.setColor(Color.YELLOW);
                g.fillOval(cellX, cellY, cellSizeAdjusted, cellSizeAdjusted);
            }
        }
    }

    // Check if the current node represents a winning state
    if (node.isWinningState()) {
        int boxX = x + margin;
        int boxY = y + margin;
        int boxWidthAdjusted = boxWidth - 2 * margin;

        // Draw a green circle around the state
        g.setColor(Color.GREEN);
        g.drawOval(boxX, boxY, boxWidthAdjusted, boxWidthAdjusted);
    }
}

private Color getColor(char symbol) {
    if (symbol == 'R') {
        return Color.RED;
    } else if (symbol == 'Y') {
        return Color.YELLOW;
    } else {
        return Color.WHITE;
    }
}
    
    private void buildTree(Node node, int depth, int alpha, int beta, boolean maximizingPlayer) {
        if (depth == 0) {
            node.setUtility(calculateUtility(node.getGameState()));
            return;
        }

        char currentPlayer = maximizingPlayer ? 'Y' : 'R';
        char opponentPlayer = maximizingPlayer ? 'R' : 'Y';

        for (int col = 0; col < gameState[0].length; col++) {
            // Check if the column is valid for the move
            if (isColumnValid(node.getGameState(), col)) {
                // Create a child node with the new state
                char[][] childState = copyArray(node.getGameState());
                int row = dropDisc(childState, col, currentPlayer);
                Node childNode = new Node(childState, depth - 1, !maximizingPlayer);
                node.addChild(childNode);

                // Recursive call to build the tree for the child node
                buildTree(childNode, depth - 1, alpha, beta, !maximizingPlayer);

                // Update alpha and beta values based on the child node's utility
                int childUtility = childNode.getUtility();
                if (maximizingPlayer) {
                    alpha = Math.max(alpha, childUtility);
                    node.setUtility(alpha);
                } else {
                    beta = Math.min(beta, childUtility);
                    node.setUtility(beta);
                }

                // Perform alpha-beta pruning
                if (alpha >= beta) {
                    break;
                }
            }
        }
    }

    
    
    private boolean isColumnValid(char[][] state, int col) {
        return state[0][col] == ' ';
    }

    private int dropDisc(char[][] state, int col, char player) {
        int row = state.length - 1;
        while (row >= 0 && state[row][col] != ' ') {
            row--;
        }
        if (row >= 0) {
            state[row][col] = player;
        }
        return row;
    }

    private char[][] copyArray(char[][] original) {
        char[][] copy = new char[original.length][original[0].length];
        for (int i = 0; i < original.length; i++) {
            System.arraycopy(original[i], 0, copy[i], 0, original[i].length);
        }
        return copy;
    }

    private int calculateUtility(char[][] state) {
        // TODO: Implement your utility calculation logic here
        return 0;
    }

    // Inner class representing a tree node
private static class Node {
       private char[][] gameState;
        private int utility;
        private int depth;
        private boolean maximizingPlayer;
        private List<Node> children;

    public Node(char[][] state, int depth, boolean maximizingPlayer) {
    this.gameState = state; // Assign the "state" parameter to "gameState"
    this.depth = depth;
    this.maximizingPlayer = maximizingPlayer;
    this.children = new ArrayList<>();
}

        public char[][]getGameState() {
          return gameState;
        }

        public int getUtility() {
            return utility;
        }

        public void setUtility(int utility) {
            this.utility = utility;
        }

        public void addChild(Node child) {
            children.add(child);
        }

        public List<Node> getChildren() {
            return children;
        }
        
        public boolean isWinningState() {
        // Check for winning conditions in the gameState
        
        // Check rows for a winning condition
        for (int row = 0; row < gameState.length; row++) {
            for (int col = 0; col < gameState[row].length - 3; col++) {
                char symbol = gameState[row][col];
                if (symbol != ' ') {
                    if (symbol == gameState[row][col + 1] &&
                        symbol == gameState[row][col + 2] &&
                        symbol == gameState[row][col + 3]) {
                        return true;
                    }
                }
            }
        }
        
        // Check columns for a winning condition
        for (int col = 0; col < gameState[0].length; col++) {
            for (int row = 0; row < gameState.length - 3; row++) {
                char symbol = gameState[row][col];
                if (symbol != ' ') {
                    if (symbol == gameState[row + 1][col] &&
                        symbol == gameState[row + 2][col] &&
                        symbol == gameState[row + 3][col]) {
                        return true;
                    }
                }
            }
        }
        
        // Check diagonals (from top-left to bottom-right) for a winning condition
        for (int row = 0; row < gameState.length - 3; row++) {
            for (int col = 0; col < gameState[row].length - 3; col++) {
                char symbol = gameState[row][col];
                if (symbol != ' ') {
                    if (symbol == gameState[row + 1][col + 1] &&
                        symbol == gameState[row + 2][col + 2] &&
                        symbol == gameState[row + 3][col + 3]) {
                        return true;
                    }
                }
            }
        }
        
        // Check diagonals (from top-right to bottom-left) for a winning condition
        for (int row = 0; row < gameState.length - 3; row++) {
            for (int col = 3; col < gameState[row].length; col++) {
                char symbol = gameState[row][col];
                if (symbol != ' ') {
                    if (symbol == gameState[row + 1][col - 1] &&
                        symbol == gameState[row + 2][col - 2] &&
                        symbol == gameState[row + 3][col - 3]) {
                        return true;
                    }
                }
            }
        }
        
        return false; // No winning condition found
    }
    }
    public static void main(String[] args) {
        // Example usage
        char[][] gameState = {
                {' ', ' ', ' ', ' ', ' ', ' ',' '},
                {' ', ' ', ' ', ' ', ' ', ' ',' '},
                {' ', ' ', ' ', ' ', ' ', ' ',' '},
                {'Y', 'R', ' ', ' ', ' ', ' ',' '},
                {'Y', 'R', ' ', ' ', ' ', ' ',' '},
                {'Y', 'R', 'R', ' ', ' ', ' ',' '}
        };

        int depth = 2;

        AlphaBetaTree game = new AlphaBetaTree(gameState, depth);
        game.buildTreeAndSaveAsImage("D:\\connect4_tree.jpg");
    }
}