package connect4;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.util.Duration; //animated
import javafx.animation.TranslateTransition;//animated
import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
 import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javax.swing.JOptionPane;

public class Connect4 extends Application{
    Button B = new Button("Play !");
    private static final int TILE_SIZE = 80;
    private static final int COLUMNS = 7;
    private static final int ROWS = 6;
        private static final char SPACE = ' ';
        private int userColumn;
        
    private boolean redMove = true;
    private Disc[][] grid = new Disc[COLUMNS][ROWS];
    
    private Pane discRoot = new Pane();
    
      Text space1 = new Text("    ");
      Text red = new Text("0");
      Text space2 = new Text("                           "); 
      Text yellow = new Text("0"); 
    TextField tf = new TextField();
    
    
    RadioButton r1=new RadioButton("Minimax");    
    RadioButton r2=new RadioButton("Alpha-Beta");  
    final ToggleGroup group = new ToggleGroup();
    
    private Parent createContent() {
    Pane root = new Pane();
    root.getChildren().add(discRoot);

    Shape gridsShape = makeGrid();
    root.getChildren().add(gridsShape);
    root.getChildren().addAll(makeColumns());

    VBox vbox = new VBox(20);
    vbox.setPadding(new Insets(10));

    vbox.getChildren().add(t());

    vbox.getChildren().add(score());

    vbox.getChildren().add(enter());

    vbox.getChildren().add(createTextfield());
   
    r1.setToggleGroup(group);
    r2.setToggleGroup(group);
    group.selectToggle(r1);
    vbox.getChildren().add(r1);
    vbox.getChildren().add(r2);

    vbox.getChildren().add(B);

        vbox.getChildren().add(PoweredBy());
    
        vbox.getChildren().add(we());
        
         
    // Event listener for the "Play!" button
    B.setOnAction(e -> {
        String algorithm="";
    RadioButton selectedRadioButton = (RadioButton) group.getSelectedToggle();
         algorithm = selectedRadioButton.getText();
        
        if (algorithm.equals("Minimax")) {

                 int depth;
                 
        try{
         depth = Integer.parseInt(tf.getText());
        char[][] gameState = new char[ROWS][COLUMNS];
        for (int x = 0; x < ROWS; x++) {
            for (int y = 0; y < COLUMNS; y++) {
                Optional<Connect4.Disc> disc = getDisc(y, x);
                if (disc.isPresent()) {
                    gameState[x][y] = disc.get().red ? 'R' : 'Y';
                } else {
                    gameState[x][y] = ' ';
                }
            }
        }
        try{  
        //MinimaxTree Mgame = new MinimaxTree(gameState, depth);
        //Mgame.buildTreeAndSaveAsImage("D:\\connect4_tree.jpg",depth);
            
 
            Minimax game1 = new Minimax(gameState);
            long startTime = System.nanoTime();
            int aiMove = game1.findBestMove(depth);
            long endTime = System.nanoTime();
            System.out.println("AI chooses column " + aiMove);
            char ai;
            if(redMove){ai='R';} else {ai='Y';}
            game1.makeMove(aiMove, ai);
            game1.printBoard();
                 System.out.println("Time Taken: " + (endTime - startTime)/1000000 + "ms");
           
            System.out.println("Exanded Nodes: " + game1.getExpandedNodesCount());
            placeDisc(new Disc(redMove), aiMove);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null, "Game Over !"); System.out.println(ex.getMessage());
        }
         
        }catch(Exception exc){JOptionPane.showMessageDialog(null, "Enter Depth !");}


    }
 else if(algorithm.equals("Alpha-Beta")){ 
                 int depth;
        try{
         depth = Integer.parseInt(tf.getText());
        char[][] gameState = new char[ROWS][COLUMNS];
        for (int x = 0; x < ROWS; x++) {
            for (int y = 0; y < COLUMNS; y++) {
                Optional<Connect4.Disc> disc = getDisc(y, x);
                if (disc.isPresent()) {
                    gameState[x][y] = disc.get().red ? 'R' : 'Y';
                } else {
                    gameState[x][y] = ' ';
                }
            }
        }
        try{     
        //AlphaBetaTree game1 = new AlphaBetaTree(gameState, depth);
        //game1.buildTreeAndSaveAsImage("D:\\connect4_tree.jpg");
        
          AlphaBeta game=new AlphaBeta(gameState);
          game.printBoard();

            long startTime = System.nanoTime();
            int aiMove = game.findBestMove(depth);
            long endTime = System.nanoTime();
            System.out.println("AI chooses column " + aiMove);
 
            char ai;
            if(redMove){ai='R';} else {ai='Y';}
            game.MakeMove(aiMove, ai);
            game.printBoard();
                       System.out.println("Time Taken: " + (endTime - startTime)/1000000 + "ms");
            // Get Number Of Expanded Nodes
            System.out.println("Exanded Nodes: " + game.getExpandedNodesCount());
            placeDisc(new Disc(redMove), aiMove);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null, "Game Over !"); System.out.println(ex.getMessage());
           }
        }catch(Exception exc){JOptionPane.showMessageDialog(null, "Enter Depth !");}
        

        }
        
    });

    HBox hbox = new HBox(root, vbox);
    hbox.setSpacing(15);

    return hbox;
}


private TextFlow t() {
  Text t1 = new Text("Red ");
  t1.setFont(Font.font("Arial", FontWeight.BOLD, 30));
  t1.setFill(Color.RED);

  Text t2 = new Text("VS");  
  t2.setFont(Font.font("Blackadder ITC", FontWeight.BOLD, 25));
  t2.setFill(Color.BLACK);

  Text t3 = new Text(" yellow");
  t3.setFont(Font.font("Arial", FontWeight.BOLD, 30)); 
  t3.setFill(Color.YELLOWGREEN);

  TextFlow textFlow = new TextFlow(t1, t2, t3);

  return textFlow;
}
private Text enter() 
{
  Text t = new Text("Enter depth :");  
  t.setFont(Font.font("Arial", FontWeight.BOLD, 20));
  t.setFill(Color.BLACK);
  return t;
}

private TextFlow score() 
{
  red.setFont(Font.font("Arial", FontWeight.BOLD, 30));
  red.setFill(Color.RED);
 
  yellow.setFont(Font.font("Arial", FontWeight.BOLD, 30));
  yellow.setFill(Color.YELLOWGREEN);
    TextFlow textFlow = new TextFlow(space1,red,space2,yellow);
  return textFlow;
}

    private TextField createTextfield() {
        tf.setEditable(true);
        return tf;
    }
    
    private Text PoweredBy() 
{
  Text t1 = new Text("Powered By :");
  t1.setFont(Font.font("Yellowtail", FontWeight.BOLD, 19));
  t1.setFill(Color.RED);
  return t1;
}
        private Text we() 
{
  Text t2 = new Text("(: Marko & Arsto & S7to :)");  
  t2.setFont(Font.font("Script Mt Bold", FontWeight.EXTRA_LIGHT, 18));
  t2.setFill(Color.BLACK);
  return t2;
}

    
    private Shape makeGrid() {
        Shape shape = new Rectangle((COLUMNS + 1) * TILE_SIZE, (ROWS + 1) * TILE_SIZE);
        for(int y = 0; y < ROWS; y++){
            for(int x = 0; x < COLUMNS; x++){
                Circle circle = new Circle(TILE_SIZE / 2);
                circle.setCenterX(TILE_SIZE / 2);
                circle.setCenterY(TILE_SIZE / 2);
                circle.setTranslateX(x * (TILE_SIZE + 5) + TILE_SIZE / 4);
                circle.setTranslateY(y * (TILE_SIZE + 5) + TILE_SIZE / 4);
                
                shape = Shape.subtract(shape, circle);
            }
        }
        
        shape.setFill(Color.BLACK);
        
        return shape;
        
    }
    
    private List<Rectangle> makeColumns(){
        List<Rectangle> list = new ArrayList<>();
        
        for(int x = 0; x < COLUMNS; x++) {
            Rectangle rect = new Rectangle(TILE_SIZE, (ROWS + 1) * TILE_SIZE);
            rect.setTranslateX(x * (TILE_SIZE + 5) + TILE_SIZE / 4); 
            rect.setFill(Color.TRANSPARENT);
            
            rect.setOnMouseEntered(e -> rect.setFill(Color.rgb(128, 128, 128, 0.3)));
            rect.setOnMouseExited(e -> rect.setFill(Color.TRANSPARENT));
            
            final int column = x;
            userColumn=column;
            rect.setOnMouseClicked(e -> placeDisc(new Disc(redMove), column));
            
            list.add(rect);
        }
        return list;
    }
    
    private void placeDisc(Disc disc, int column){
        int row = ROWS - 1;
        do{
            if(!getDisc(column, row).isPresent())
                break;
            row--;
        }while(row >= 0);
        if(row < 0)
            return;
        
        grid[column][row] = disc;//prevent fall on each other
        discRoot.getChildren().add(disc);
        disc.setTranslateX(column * (TILE_SIZE + 5) + TILE_SIZE / 4);
        
        final int currentRow = row;
        
        TranslateTransition animation = new TranslateTransition(Duration.seconds(0.4), disc);
        animation.setToY(row * (TILE_SIZE + 5) + TILE_SIZE / 4); // fall down not still up
        animation.setOnFinished(e -> {
            if(gameEnded(column, currentRow)){
                gameOver();
            }
            
            redMove = !redMove;
        });
        animation.play();//fall smothely not still up constant
    }
    
    private boolean gameEnded(int column, int row){
        List<Point2D> vertical = IntStream.rangeClosed(row - 3, row + 3)
                .mapToObj(r -> new Point2D(column, r))
                .collect(Collectors.toList());
        
        List<Point2D> horizontal = IntStream.rangeClosed(column - 3, column + 3)
                .mapToObj(c -> new Point2D(c, row))
                .collect(Collectors.toList());
        
        Point2D topLeft = new Point2D(column - 3, row - 3);
        List<Point2D> diagonal1 = IntStream.rangeClosed(0, 6)
                .mapToObj(i -> topLeft.add(i, i))
                .collect(Collectors.toList());
        
        Point2D botLeft = new Point2D(column - 3, row + 3);
        List<Point2D> diagonal2 = IntStream.rangeClosed(0, 6)
                .mapToObj(i -> botLeft.add(i, -i))
                .collect(Collectors.toList());
        
        return checkRange(vertical) || checkRange(horizontal)
                || checkRange(diagonal1) || checkRange(diagonal2);
    }
    
    private boolean checkRange(List<Point2D> points){
        int chain = 0;
        for(Point2D p : points){
            int column = (int) p.getX();
            int row = (int) p.getY();
            
            Disc disc = getDisc(column, row).orElse(new Disc(!redMove));
            if(disc.red == redMove){
                chain++;
                if(chain == 4){
                    return true;
                }
            } else {
                chain = 0;
            }
        }
        return false;
    }
    
    private void gameOver(){
        if(redMove){
            System.out.println("Winner : Red");
           int r=Integer.parseInt(red.getText());
           r=r+1;
           red.setText( Integer.toString(r) );
        }
        else{
            System.out.println("Winner : Yellow");
           int y=Integer.parseInt(yellow.getText());
           y=y+1;
           yellow.setText( Integer.toString(y) );
        }
    }
    
    private Optional<Disc> getDisc(int column, int row){
        if(column < 0 || column >= COLUMNS
                || row < 0 || row >= ROWS)
            return Optional.empty();
        
        return Optional.ofNullable(grid[column][row]);
    }
    
    private static class Disc extends Circle {
        private final boolean red;
        public Disc(boolean red){
            super(TILE_SIZE / 2, red? Color.RED : Color.YELLOW);
            this.red = red;
            
            setCenterX(TILE_SIZE / 2);
            setCenterY(TILE_SIZE / 2);
        }
    }
    
    @Override
    public void start(Stage stage) throws Exception{
        stage.setScene(new Scene(createContent()));
        stage.show();
    }
    
    public static void main(String[] args){
        
        launch(args);
        
       
        
    }
    
}