package connect4;
import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class MinimaxTree {
    private char[][] gameState;
    private int depth;

    public MinimaxTree(char[][] gameState, int depth) {
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

    public void buildTreeAndSaveAsImage(String imagePath, int depth) {
        Node rootNode = new Node(gameState, depth, true); // Create the root node
        buildTree(rootNode, depth, true); // Build the tree using alpha-beta pruning

        // Create a GUI frame to visualize the tree
        JFrame frame = new JFrame("Minimax Tree");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 1000);

        // Create a panel to draw the tree
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawTree(g, rootNode, getWidth(), 0, 0, depth);
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

private void drawTree(Graphics g, Node node, int panelWidth, int x, int y, int currentDepth) {
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
        g.drawLine(x + panelWidth / 2, y, childX + childWidth / 2, childY);

        drawBoard(g, child, childX, childY, childWidth,currentDepth);

        if (currentDepth >1) {
            
            drawTree(g, child, childWidth, childX, childY, currentDepth - 1);
        }
    }
}

    private void drawBoard(Graphics g, Node node, int x, int y, int boxWidth,int depth) {
        char[][] board = node.getGameState();
        int cellSize = boxWidth / board.length;
        int margin = 5;

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                int cellX = x + col * cellSize + margin;
                int cellY = y + row * cellSize + margin;
                int cellSizeAdjusted = cellSize - 2 * margin;

                g.setColor(Color.WHITE);
                g.fillOval(cellX, cellY, cellSizeAdjusted, cellSizeAdjusted);
                g.setColor(Color.BLACK);
                g.drawOval(cellX, cellY, cellSizeAdjusted, cellSizeAdjusted);

                if (board[row][col] == 'R') {
                    g.setColor(Color.RED);
                    g.fillOval(cellX, cellY, cellSizeAdjusted, cellSizeAdjusted);
                } else if (board[row][col] == 'Y') {
                    g.setColor(Color.YELLOW);
                    g.fillOval(cellX, cellY, cellSizeAdjusted, cellSizeAdjusted);
                }
            }
        }
        
    }
    
        private boolean isColumnValid(char[][] state, int col) {
        return state[0][col] == ' ';
    }
    
        private boolean isValidMove(char[][] state, int col) {
    int rows = state.length;
    int cols = state[0].length;

    // Check if the column is within the valid range
    if (col < 0 || col >= cols) {
        return false;
    }

    // Check if the column is not full (i.e., there is an empty slot)
    return state[0][col] == ' ';
}

private char[][] getUpdatedGameState(char[][] state, int col, char currentPlayer) {
    int rows = state.length;
    int cols = state[0].length;

    char[][] newState = new char[rows][cols];

    // Copy the current state to the new state
    for (int row = 0; row < rows; row++) {
        for (int c = 0; c < cols; c++) {
            newState[row][c] = state[row][c];
        }
    }

    // Find the first empty row in the selected column and place the current player's token
    for (int row = rows - 1; row >= 0; row--) {
        if (newState[row][col] == ' ') {
            newState[row][col] = currentPlayer;
            break;
        }
    }

    return newState;
}
        
private void buildTree(Node node, int depth, boolean maximizingPlayer) {
    if (depth == 0) {
        node.setUtility(calculateUtility(node.getGameState()));
        return;
    }

    char currentPlayer = maximizingPlayer ? 'Y' : 'R';
    char opponentPlayer = maximizingPlayer ? 'R' : 'Y';

    List<Node> children = new ArrayList<>();

    for (int col = 0; col < node.getGameState()[0].length; col++) {
        if (isValidMove(node.getGameState(), col)) {
            char[][] childGameState = getUpdatedGameState(node.getGameState(), col, currentPlayer);
            Node childNode = new Node(childGameState, depth - 1, !maximizingPlayer);
            children.add(childNode);
            buildTree(childNode, depth - 1, !maximizingPlayer);
        }
    }

    node.setChildren(children);

    if (maximizingPlayer) {
        int maxUtility = Integer.MIN_VALUE;
        for (Node child : node.getChildren()) {
            maxUtility = Math.max(maxUtility, child.getUtility());
        }
        node.setUtility(maxUtility);
    } else {
        int minUtility = Integer.MAX_VALUE;
        for (Node child : node.getChildren()) {
            minUtility = Math.min(minUtility, child.getUtility());
        }
        node.setUtility(minUtility);
    }
}
private int calculateUtility(char[][] state) {
    int utility = 0;

    // Check for winning conditions
    if (checkWinningCondition(state, 'Y')) {
        // If the maximizing player ('Y') wins, assign a high utility value
        utility = 100;
    } else if (checkWinningCondition(state, 'R')) {
        // If the minimizing player ('R') wins, assign a low utility value
        utility = -100;
    } else {
        // If no player has won, calculate the utility based on other factors

        // Example: Count the number of 'Y' and 'R' tokens in the state
        int yellowCount = countTokens(state, 'Y');
        int redCount = countTokens(state, 'R');

        // Assign the utility based on the difference in token counts
        utility = yellowCount - redCount;
    }

    return utility;
}

private boolean checkWinningCondition(char[][] state, char player) {
    int rows = state.length;
    int cols = state[0].length;

    // Check for horizontal wins
    for (int row = 0; row < rows; row++) {
        for (int col = 0; col <= cols - 4; col++) {
            if (state[row][col] == player &&
                    state[row][col + 1] == player &&
                    state[row][col + 2] == player &&
                    state[row][col + 3] == player) {
                return true;
            }
        }
    }

    // Check for vertical wins
    for (int row = 0; row <= rows - 4; row++) {
        for (int col = 0; col < cols; col++) {
            if (state[row][col] == player &&
                    state[row + 1][col] == player &&
                    state[row + 2][col] == player &&
                    state[row + 3][col] == player) {
                return true;
            }
        }
    }

    // Check for diagonal wins (top-left to bottom-right)
    for (int row = 0; row <= rows - 4; row++) {
        for (int col = 0; col <= cols - 4; col++) {
            if (state[row][col] == player &&
                    state[row + 1][col + 1] == player &&
                    state[row + 2][col + 2] == player &&
                    state[row + 3][col + 3] == player) {
                return true;
            }
        }
    }

    // Check for diagonal wins (top-right to bottom-left)
    for (int row = 0; row <= rows - 4; row++) {
        for (int col = 3; col < cols; col++) {
            if (state[row][col] == player &&
                    state[row + 1][col - 1] == player &&
                    state[row + 2][col - 2] == player &&
                    state[row + 3][col - 3] == player) {
                return true;
            }
        }
    }

    return false;
}

private int countTokens(char[][] state, char token) {
    int count = 0;
    for (int row = 0; row < state.length; row++) {
        for (int col = 0; col < state[row].length; col++) {
            if (state[row][col] == token) {
                count++;
            }
        }
    }
    return count;
}

}

 class Node {
    private char[][] gameState;
    private int depth;
    private boolean maximizingPlayer;
    private int utility;
    private List<Node> children;

    public Node(char[][] gameState, int depth, boolean maximizingPlayer) {
        this.gameState = gameState;
        this.depth = depth;
        this.maximizingPlayer = maximizingPlayer;
        this.children = new ArrayList<>();
    }

    public char[][] getGameState() {
        return gameState;
    }

    public int getDepth() {
        return depth;
    }

    public boolean isMaximizingPlayer() {
        return maximizingPlayer;
    }

    public int getUtility() {
        return utility;
    }

    public void setUtility(int utility) {
        this.utility = utility;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }
public static void main(String[] args) {
    // Example usage
    char[][] gameState = {
            {' ', ' ', ' ', ' ', ' ', ' ',' '},
            {' ', ' ', ' ', ' ', ' ', ' ',' '},
            {' ', 'R', ' ', ' ', ' ', ' ',' '},
            {'Y', 'R', ' ', ' ', ' ', ' ',' '},
            {'Y', 'R', ' ', ' ', ' ', ' ',' '},
            {'Y', 'R', ' ', ' ', ' ', ' ',' '}
    };

    int depth = 2;

    MinimaxTree game = new MinimaxTree(gameState, depth);
    game.buildTreeAndSaveAsImage("D:\\connect4_tree.jpg",depth);
}
}