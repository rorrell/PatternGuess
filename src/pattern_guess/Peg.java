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

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CircleBuilder;

/**
 * Peg class
 * @author Rachel Orrell
 */
public class Peg {

    /**
     * Available Peg colors
     */
    public static enum AvailableColor implements PegColor {

        RED(Color.RED), 
        YELLOW(Color.YELLOW), 
        BLUE(Color.BLUE), 
        GREEN(Color.GREEN), 
        BLACK(Color.BLACK), 
        WHITE(Color.WHITE);
        
        private Color c;
        
        private AvailableColor(Color c) {
            this.c = c;
        }
        /**
         * @return the color
         */
        @Override
        public Color getColor() {
            return this.c;
        }
        
        /**
         * Checks if this enum contains a given Color
         * @param c a Color object
         * @return the index of the given Color or -1 if the Color isn't found
         */
        public static int contains(Color c) {
            int index = 0;
            for(AvailableColor ac : values()) {
                if(ac.getColor().equals(c)) return index;
                index++;
            }
            return -1;
        }
    }
    
    protected Color color = Color.WHITE;
    protected int x_loc;
    protected int y_loc;
    protected int diameter;
    private String colorName;
    
    /**
     * @return the color
     */
    public Color getColor() {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(PegColor color) {
        this.color = color.getColor();
        colorName = color.name();
    }
    
    /**
     * @return the colorName
     */
    public String getColorName() {
        return colorName;
    }

    /**
     * @return the x_loc
     */
    public int getX_loc() {
        return x_loc;
    }

    /**
     * @param x_loc the x_loc to set
     */
    public void setX_loc(int x_loc) {
        this.x_loc = x_loc;
    }

    /**
     * @return the y_loc
     */
    public int getY_loc() {
        return y_loc;
    }

    /**
     * @param y_loc the y_loc to set
     */
    public void setY_loc(int y_loc) {
        this.y_loc = y_loc;
    }

    /**
     * @return the diameter
     */
    public int getDiameter() {
        return diameter;
    }

    /**
     * Default Peg constructor
     */
    public Peg() {
    }
    
    /**
     * Alternate Peg constructor
     * @param color use enum Peg.AvailableColor 
     */
    public Peg(PegColor color) {
        this.setColor(color);
    }
    
    /**
     * Alternate Peg constructor
     * @param color use enum Peg.AvailableColor
     * @param x_loc the x location
     * @param y_loc the y location
     */
    public Peg(PegColor color, int x_loc, int y_loc) {
        this(color);
        this.x_loc = x_loc;
        this.y_loc = y_loc;
    }
    
    /**
     * Alternate Peg constructor
     * @param circle
     * @throws IllegalArgumentException
     */
    public Peg(Circle circle) throws IllegalArgumentException {
        Color c = (Color)circle.getFill();
        int index = AvailableColor.contains(c);
        if(index > -1) {
            this.color = c;
            this.colorName = AvailableColor.values()[index].name();
        }
        else
            throw new IllegalArgumentException("Peg cannot be created from circle color " + c.toString());
    }
    
    /**
     * Draws the Peg as an oval in a graphics context
     * @param gc the graphics context in which to draw the Peg
     */
    public void draw(GraphicsContext gc) {
        gc.setStroke(Color.BLACK);
        gc.setFill(color);
        gc.fillOval(x_loc, y_loc, diameter, diameter);
    }
    
    /**
     * Creates a Circle object from the Peg
     * @return Circle object representing the Peg
     */
    public Circle buildCircle() {
        int radius = diameter / 2;
        Circle pegCircle = CircleBuilder.create()
                .radius(radius)
                .centerX(x_loc + radius)
                .centerY(y_loc + radius)
                .fill(color)
                .build();
        return pegCircle;
    }
    
    @Override
    /**
     * Tests equality based on color and diameter
     */
    public boolean equals(Object o) {
        if(o == this) return true;
        if(!(o instanceof Peg)) return false;
        Peg p = (Peg)o;
        return p.color.equals(this.color) && p.diameter == this.diameter;
    }
}
