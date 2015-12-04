/* 
 * Copyright (c) 2015, Rachel Orrell <rachel.orrell@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     - Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     - Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *
 *     - Neither the name of Rachel Orrell,  nor the names of its 
 *       contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package pattern_guess;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

/**
 * PatternGuess application
 * @author Rachel Orrell
 */
public class PatternGuess extends Application {
    private static final Logger LOG = Logger.getLogger(PatternGuess.class.getName());

    PatternPeg[] solution;
    PatternPeg[] guess = new PatternPeg[4];
    String helpText;
    int current_y = 90; //current y position within the game board for guesses
    int[] x_position = {160, 210, 260, 310}; //x positions for pattern pegs on the board (guesses and solutions)
    int[] feedback_pos = new int[2]; //x & y position for info peg being placed for feedback
    int pos_num = 0; //zero-based guess peg number (0-3 since guesses each contain 4 pegs)
    GraphicsContext gc;
    HBox bottomPane;
    
    @Override
    public void start(Stage primaryStage) {
        try {
            FileHandler fh = new FileHandler("log.xml");
            LOG.addHandler(fh);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        
        primaryStage.setTitle("Pattern Guess");
        BorderPane root = new BorderPane();
        root.setBackground(new Background(new BackgroundFill(Color.SEAGREEN, null, null)));
        Scene scene = new Scene(root, 400, 615);
        
        try {
            helpText = readTxtFile("help.txt"); //read text containing how-to guide for game
        }
        catch(IllegalArgumentException iae) {
            LOG.log(Level.SEVERE, iae.getMessage(), iae);
        }
        MenuBar mb = new MenuBar();
        setMenus(mb);
        
        Canvas myCanvas = new Canvas(400, 515); //canvas holds game board
        gc = myCanvas.getGraphicsContext2D();
        setBoard(gc); //draw game board
        FlowPane mainPane = new FlowPane();
        mainPane.getChildren().add(myCanvas);
        newSolution(); //create a new solution
        
        bottomPane = new HBox(); //bottom pane holds peg buttons
        bottomPane.setAlignment(Pos.CENTER);
        bottomPane.setSpacing(25);
        bottomPane.setPadding(new Insets(0, 0, 25, 0));
        setButtons();
        
        root.setTop(mb); //set menus as top
        root.setCenter(mainPane); //set game board as center
        root.setBottom(bottomPane); //set peg buttons as bottom
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
// <editor-fold defaultstate="collapsed" desc=" Handlers ">
    /**
     * Handles menu button click events
     * @param ae the ActionEvent object
     */
    public void menuHandler(ActionEvent ae) {
        switch (((MenuItem) ae.getTarget()).getText()) {
            case "New":
                newGame();
                break;
            case "Show Solution":
                showSolution();
                endGame();
                break;
            case "Help":
                showInfo("Help", helpText);
                break;
            case "Exit":
                System.exit(0);
                break;
        }
    }
    
    /**
     * Handles peg button click events
     * @param me the MouseEvent object - expected type Circle
     */
    public void pegClickHandler(MouseEvent me) {
        if(!(me.getTarget() instanceof Circle)) //make sure target is a Circle object
            throw new IllegalArgumentException("pegClickHandler should only be applied to objects of type Circle");
        Circle c = (Circle)me.getTarget(); //cast parameter as circle
        PatternPeg newPeg; 
        try {
           newPeg = new PatternPeg(c); //construct PatternPeg from circle 
           //will throw IllegalArgumentException if circle argument has a color that is not an allowable pattern peg color
        }
        catch(IllegalArgumentException iae) { 
            LOG.log(Level.SEVERE, iae.getMessage(), iae);
            return; //should transfer out of this function if this happens
        }
        if(pegsContainColor(new ArrayList<PatternPeg>(Arrays.asList(guess)), newPeg.getColorName())) {
            showError("Current guess already contains this color.");
            return; //solution will not have duplicate colors, so if this is a duplicate color, do not draw or add to guess
        }
        int radius = (int)c.getRadius();
        gc.setFill(c.getFill());
        gc.fillOval(x_position[pos_num] - radius, current_y - radius, radius * 2, radius * 2); //draw oval in appropriate location on board
        //the x_position and current_y are the coordinates of the center of the oval, but ovals are drawn from the top left, so coordinates must be adjusted by the radius
        //this is done for flexibility in case the diameter of pattern pegs is changed
        guess[pos_num] = newPeg; //add this peg to the current guess
        if(++pos_num > 3) { //increment the position within the guess, and if after that, we have 4 pegs in the guess (pos_num is zero-based)
            giveFeedback(); //show appropriate info pegs
            guess = new PatternPeg[4]; //create a new guess
            pos_num = 0; //reset guess position
            current_y += 50; //increment current_y to the center of the next row
            if(current_y == 540) { //if last possible guess has been made without winning
                showError("You lose.");
                showSolution();
                endGame();
            }
        }
    }
// </editor-fold>
    
// <editor-fold defaultstate="collapsed" desc=" Setup ">

    /**
     * Sets up application menus
     * @param mb the MenuBar object to which the menus will be added
     */
        public void setMenus(MenuBar mb) {
        Menu mFile = new Menu("File"); //create file menu
        String[] menuItems = {"New Game", "Show Solution", "Help", "Exit"}; //array of menu names to go under File
        for (String s : menuItems) { //for each menu name
            MenuItem mi = new MenuItem(s); //create new menu item from name
            mi.setOnAction(e -> menuHandler(e)); //set menuHandler as the action listener
            mFile.getItems().add(mi); //add menu item to file menu
        }
        mb.getMenus().add(mFile); //add file menu to menu bar
    }
    
    /**
     * Draws the game board
     * @param gc the GraphicsContext object on which the board will be drawn
     */
    public void setBoard(GraphicsContext gc) {
        gc.setFill(Color.SADDLEBROWN);
        gc.fillRect(50, 15, 300, 500); //draw brown rectangle
        gc.setStroke(Color.BLACK);
        gc.strokeLine(125, 25, 125, 505); //draw vertical line to separate feedback from guesses
        for (int i = 65; i <= 465; i += 50) {
            gc.strokeLine(60, i, 340, i); //draw horizontal lines to separate rows
        }
    }
    
    /**
     * Adds the peg buttons to global variable Hbox
     */
    public void setButtons() {
        for (Peg.AvailableColor ac : Peg.AvailableColor.values()) { //add one peg button for each possible color (PatternPeg uses Peg's AvailableColor)
            PatternPeg pp = new PatternPeg(ac);
            Circle ppCircle = pp.buildCircle(); //create circle since they can have actions assigned
            ppCircle.setOnMouseClicked(e -> pegClickHandler(e)); //assign pegClickHandler
            bottomPane.getChildren().add(ppCircle); //add circle to the HBox
        }
    }

// </editor-fold>
    
// <editor-fold defaultstate="collapsed" desc=" Helper Methods ">

    /**
     * Starts a new game
     */
    public void newGame() {
        newSolution(); //create a new solution        
        current_y = 90; //reset the current_y to the center of the first guess row
        pos_num = 0; //reset the zero-based guess peg number (position within a guess composed of 4 pegs)
        gc.clearRect(50, 15, 300, 500); //clear the board
        setBoard(gc); //redraw the board
        setButtons(); //redraw the peg buttons
    }
    
    /**
     * Creates a new solution
     */
    public void newSolution() {
        solution = new PatternPeg[4]; //initialize a solution composed of 4 pegs
        ArrayList existing = new ArrayList<>(); //will hold Integers representing the indices of colors from Peg's AvailableColor enum that currently exist in the solution
        Random rand;
        int color = 0;
        for (int i = 0; i < solution.length; i++) {
            do {
                rand = new Random();
                color = rand.nextInt(Peg.AvailableColor.values().length); //choose a random integer from 0 (inclusive) to the length of the Peg.AvailableColor enum (exclusive) 
            } while (existing.contains(color)); //if this integer is already in ArrayList existing, try again
            existing.add(color); //add he integer to existing
            solution[i] = new PatternPeg(Peg.AvailableColor.values()[color]); //add a pattern peg with this color to the solution
        }
    }
    
    /**
     * Remove peg buttons so the player can't continue making guesses
     */
    public void endGame() {
        bottomPane.getChildren().clear();
    }
    
    /**
     * Checks if the current guess matches the solution
     * @return
     */
    public boolean checkGuess() {
        if(guess.length != solution.length) return false; //can't be correct if it's not the same length as the solution
        for(int i=0; i<solution.length; i++) {
            if(!solution[i].equals(guess[i]))
                return false; //not correct if any individual guess peg does not match the corresponding solution peg
        }
        return true; //if it hasn't returned by now, it must be correct
    }
    
    /**
     * Selects info pegs to give the player feedback on the current guess
     */
    public void giveFeedback() {
        if(checkGuess()) { //first check to see if they won
            showInfo("Info", "You win!");
            endGame();
        }
        else {
            feedback_pos[0] = 70;
            feedback_pos[1] = current_y - 15;
            ArrayList feedback = new ArrayList<>();
            ArrayList<PatternPeg> guessList = new ArrayList<PatternPeg>(Arrays.asList(guess)); //create guess ArrayList to affect without affecting the original array
            ArrayList<PatternPeg> solutionList = new ArrayList<PatternPeg>(Arrays.asList(solution));  //create solution ArrayList to ease operations
            for(int i=0; i<solution.length; i++) {
                if(solution[i].equals(guess[i])) { //if guess peg is the right color and in the right position
                    feedback.add(getFeedbackPeg(InfoPeg.AvailableColor.WHITE, feedback.size())); //add white info peg
                    guessList.remove(guess[i]); //remove from guessList so as to not also apply a black info peg for this guess peg
                }
            }
            for(PatternPeg peg : guessList) { //check only guess pegs for which a white info peg has not been displayed
                if(pegsContainColor(solutionList, peg.getColorName())) //if the color of this guess peg is anywhere in the solution
                    feedback.add(getFeedbackPeg(InfoPeg.AvailableColor.BLACK, feedback.size())); //add black info peg
            }
        }
    }
    
    /**
     * Checks for the existence of a color within an ArrayList of PatternPegs
     * @param pegs an ArrayList of type PatternPeg
     * @param colorName a String
     * @return
     */
    public boolean pegsContainColor(ArrayList<PatternPeg> pegs, String colorName) {
        return pegs.stream().anyMatch(peg -> (peg != null && peg.getColorName().equals(colorName)));
    }
    
    /**
     * Draws an InfoPeg of the appropriate color and increments values in the global array feedback_pos
     * @param color an InfoPeg.AvailableColor - the color of peg to draw
     * @param feedbackSize an int representing the total number of info pegs in the feedback for the current guess
     * @return the InfoPeg that was drawn
     */
    public InfoPeg getFeedbackPeg(InfoPeg.AvailableColor color, int feedbackSize) {
        InfoPeg peg = new InfoPeg(color, feedback_pos[0], feedback_pos[1]); //create the peg with the given color and the appropriate x & y values
        peg.draw(gc); //draw the peg
        incrementFeedbackPosition(feedbackSize + 1); //increment the feedback x & y positions, incrementing the size before passing to include the peg that has just been drawn
        return peg;
    }
    
    /**
     * Increments the x and y position (global array feedback_pos) for the next info peg for feedback on a guess;
     * assumes a max size of 4
     * @param size an int representing the number of info pegs that have already been drawn for the feedback on this guess 
     */
    public void incrementFeedbackPosition(int size) {
        feedback_pos[0] = size % 2 == 0 ? 70 : 100; //if the size is even, position into 1st column, else 2nd column
        feedback_pos[1] = size < 2 ? current_y - 15 : current_y + 7; //if the size is less than two, position into 1st row, else 2nd row
    }
    
    /**
     * Displays the solution to the player
     */
    public void showSolution() {
        int i = 0;
        for(PatternPeg p : solution) {
            //x_position and 40 form the coordinates of the center position, and ovals are drawn from the top left, so must adjust by radius
            //this is done for flexibility in case the diameter of pattern pegs is changed
            p.setX_loc(x_position[i] - p.getDiameter() / 2);
            p.setY_loc(40 - p.getDiameter() / 2);
            p.draw(gc);
            i++;
        }
    }
    
    /**
     * Reads a .txt file into a String
     * @param fileName
     * @return a String containing the text from the file
     * @throws IllegalArgumentException
     */
    public String readTxtFile(String fileName) throws IllegalArgumentException {
        if (!fileName.endsWith(".txt")) { //check file type
            throw new IllegalArgumentException("Unexpected file type; .txt file expected.");
        }
        String line = "";
        String text = "";
        //try to read the file
        try (FileReader fReader = new FileReader(fileName); BufferedReader bReader = new BufferedReader(fReader)) {
            while ((line = bReader.readLine()) != null) { //assign non-null line of text to the line variable
                text += line; //add line to text
            }
        } catch (FileNotFoundException fnfe) {
            LOG.log(Level.SEVERE, null, fnfe);
        } catch (IOException ioe) {
            LOG.log(Level.SEVERE, null, ioe);
        }
        return text;
    }
    
    /**
     * Show an error alert to the player
     * @param text a String containing the text to display
     */
    public void showError(String text) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }
    
    /**
     * Show an information alert to the player
     * @param title a String containing the alert title
     * @param text a String containing the text to display
     */
    public void showInfo(String title, String text) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }

// </editor-fold>
}
