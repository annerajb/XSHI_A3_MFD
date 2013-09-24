/**
* GearStatus.java
* 
* ...
* 
* Copyright (C) 2010  Marc Rogiers (marrog.123@gmail.com)
* 
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2 
* of the License, or (at your option) any later version.
*
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
*/
package net.sourceforge.xhsi.flightdeck.annunciators;

import java.awt.BasicStroke;
//import java.awt.Color;
//import java.awt.Color;
import java.awt.Component;
//import java.awt.GradientPaint;
import java.awt.Graphics2D;
//import java.awt.Shape;
import java.awt.Stroke;
//import java.awt.geom.AffineTransform;
//import java.awt.geom.Area;
//import java.awt.geom.Rectangle2D;
//import java.awt.geom.RoundRectangle2D;
//import java.awt.image.BufferedImage;

import java.util.logging.Logger;

//import net.sourceforge.xhsi.XHSISettings;

//import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ModelFactory;
//import net.sourceforge.xhsi.model.NavigationRadio;

//import net.sourceforge.xhsi.panel.GraphicsConfig;
//import net.sourceforge.xhsi.panel.Subcomponent;



public class GearStatus extends AnnunSubcomponent {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    public static enum WHEEL {
        Nose,
        Left,
        Right;
    }

    public static enum COL {
        Center,
        Left,
        Right;
    }

    public GearStatus(ModelFactory model_factory, AnnunGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
    }


    public void paint(Graphics2D g2) {

        if ( this.aircraft.num_gears() == 3 ) {
            // B737-NG style with text labels
            drawTricycle(g2);
        } else if ( this.aircraft.num_gears() > 0 ) {
            // simple annunciators with an icon
            drawAllGears(g2);
        }
//drawTricycle(g2);
    }


    private void drawAllGears(Graphics2D g2) {

        int gears = this.aircraft.num_gears();

        int firstleft;

        if ( gears % 2 == 1 ) {
            // odd number of gears; draw the first one at the top in the center
            drawAnyWheel(g2, 0, COL.Center, this.aircraft.get_gear(0));
            firstleft = 1;
        } else {
            firstleft = 0;
        }
        int row = 1;
        if ( gears > 1 ) {
            for ( int i=firstleft; i<gears; i+=2) {
                drawAnyWheel(g2, row, COL.Left, this.aircraft.get_gear(i));
                drawAnyWheel(g2, row, COL.Right, this.aircraft.get_gear(i+1));
                row++;
            }
        }

    }


    private void drawAnyWheel(Graphics2D g2, int row, COL col, float lowered) {

            int w_w = annun_gc.gear_square.width*2/8;
            int w_h = annun_gc.line_height_l * 2;
            int w_x;
            int w_y;

            if ( col == COL.Center ) {
                w_x = annun_gc.gear_square.x + annun_gc.gear_square.width/2 - w_w/2;
            } else if ( col == COL.Left ) {
                w_x = annun_gc.gear_square.x + annun_gc.gear_square.width/2 - w_w - w_w/24;
            } else {
                w_x = annun_gc.gear_square.x + annun_gc.gear_square.width/2 + w_w/24;
            }
            w_y = annun_gc.gear_square.y + annun_gc.gear_square.height/2 - (3-row)*(w_h+w_w/12);

            g2.setColor(annun_gc.instrument_background_color);
            g2.fillRect(w_x, w_y, w_w, w_h);

            if ( ( ! annun_gc.powered ) || (lowered == 0.0f) ) {
                g2.setColor(annun_gc.instrument_background_color.brighter());
            } else if ( lowered == 1.0f ) {
                g2.setColor(annun_gc.color_lime);
            } else {
                g2.setColor(annun_gc.color_amber);
            }
            g2.drawRect(w_x, w_y, w_w, w_h);

            Stroke original_stroke = g2.getStroke();
            g2.setStroke(new BasicStroke(w_h/6));
            g2.drawOval(w_x+w_w/2-w_h/4, w_y+w_h/2-w_h/4, w_h*2/4, w_h*2/4);
            g2.setStroke(original_stroke);

    }


    private void drawTricycle(Graphics2D g2) {

        g2.setStroke(new BasicStroke(3.0f * annun_gc.grow_scaling_factor));

        drawTrikeWheel(g2, WHEEL.Nose, this.aircraft.get_gear(0));
        drawTrikeWheel(g2, WHEEL.Left, this.aircraft.get_gear(1));
        drawTrikeWheel(g2, WHEEL.Right, this.aircraft.get_gear(2));

    }


    private void drawTrikeWheel(Graphics2D g2, WHEEL gearpos, float lowered) {

            int w_w = annun_gc.gear_square.width*3/8;
            int w_h = annun_gc.line_height_l * 3;
            int w_x = 999;
            int w_y = 999;
            String w1_str = "ERROR";
            int w1_x = 999;
            int w1_y = 999;
            String w2_str = "GEAR";
            int w2_x = 999;
            int w2_y = 999;

            switch (gearpos) {
                case Nose:
                    w_x = annun_gc.gear_square.x + annun_gc.gear_square.width/2 - w_w/2;
                    w_y = annun_gc.gear_square.y + annun_gc.gear_square.height/2 - w_h;
                    w1_str = "NOSE";
                    w1_x = annun_gc.gear_square.x + annun_gc.gear_square.width/2 - annun_gc.get_text_width(g2, annun_gc.font_l, w1_str)/2;
                    w1_y = annun_gc.gear_square.y + annun_gc.gear_square.height/2 - w_h + annun_gc.line_height_l*28/20;
                    w2_x = annun_gc.gear_square.x + annun_gc.gear_square.width/2 - annun_gc.get_text_width(g2, annun_gc.font_l, w2_str)/2;
                    w2_y = annun_gc.gear_square.y + annun_gc.gear_square.height/2 - w_h + annun_gc.line_height_l*48/20;
                    break;
                case Left:
                    w_x = annun_gc.gear_square.x + annun_gc.gear_square.width/2 - w_w - w_w/32;
                    w_y = annun_gc.gear_square.y + annun_gc.gear_square.height/2 + w_w/16;
                    w1_str = "LEFT";
                    w1_x = annun_gc.gear_square.x + annun_gc.gear_square.width/2 - w_w/2 - w_w/32 - annun_gc.get_text_width(g2, annun_gc.font_l, w1_str)/2;
                    w1_y = annun_gc.gear_square.y + annun_gc.gear_square.height/2 + w_w/16 + annun_gc.line_height_l*28/20;
                    w2_x = annun_gc.gear_square.x + annun_gc.gear_square.width/2 - w_w/2 - w_w/32 - annun_gc.get_text_width(g2, annun_gc.font_l, w2_str)/2;
                    w2_y = annun_gc.gear_square.y + annun_gc.gear_square.height/2 + w_w/16 + annun_gc.line_height_l*48/20;
                    break;
                case Right:
                    w_x = annun_gc.gear_square.x + annun_gc.gear_square.width/2 + w_w/32;
                    w_y = annun_gc.gear_square.y + annun_gc.gear_square.height/2 + w_w/16;
                    w1_str = "RIGHT";
                    w1_x = annun_gc.gear_square.x + annun_gc.gear_square.width/2 + w_w/2 + w_w/32 - annun_gc.get_text_width(g2, annun_gc.font_l, w1_str)/2;
                    w1_y = annun_gc.gear_square.y + annun_gc.gear_square.height/2 + w_w/16 + annun_gc.line_height_l*28/20;
                    w2_x = annun_gc.gear_square.x + annun_gc.gear_square.width/2 + w_w/2 + w_w/32 - annun_gc.get_text_width(g2, annun_gc.font_l, w2_str)/2;
                    w2_y = annun_gc.gear_square.y + annun_gc.gear_square.height/2 + w_w/16 + annun_gc.line_height_l*48/20;
                    break;
            }

            g2.setColor(annun_gc.instrument_background_color);
            g2.fillRect(w_x+2, w_y+2, w_w-4, w_h-4);

            if ( ( ! annun_gc.powered ) || (lowered == 0.0f) ) {
                g2.setColor(annun_gc.instrument_background_color.brighter());
            } else if ( lowered == 1.0f ) {
                g2.setColor(annun_gc.color_lime);
            } else {
                g2.setColor(annun_gc.color_amber);
            }
            g2.drawRect(w_x+2, w_y+2, w_w-4, w_h-4);
            g2.setFont(annun_gc.font_l);
            g2.drawString(w1_str, w1_x, w1_y);
            g2.drawString(w2_str, w2_x, w2_y);

    }


}
