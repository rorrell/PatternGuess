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

import javafx.scene.paint.Color;

/**
 * InfoPeg class
 * @author Rachel Orrell
 */
public class InfoPeg extends Peg {
    /**
     * Available InfoPeg colors
     */
    public static enum AvailableColor implements PegColor { 

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
    }
    
    { diameter = 10; }
    
    /**
     * Default InfoPeg Constructor
     */
    public InfoPeg() {
    }
    
    /**
     * Alternate InfoPeg Constructor
     * @param color use enum InfoPeg.AvailableColor
     */
    public InfoPeg(AvailableColor color) {
        super(color);
    }
    
    /**
     * Alternate InfoPeg Constructor
     * @param color use enum InfoPeg.AvailableColor
     * @param x_loc the x location
     * @param y_loc the y location
     */
    public InfoPeg(AvailableColor color, int x_loc, int y_loc) {
        super(color, x_loc, y_loc);
    }
}
