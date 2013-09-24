/**
* Annunciators.java
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
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
//import java.awt.image.BufferedImage;

import java.util.logging.Logger;

//import net.sourceforge.xhsi.XHSISettings;

//import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ModelFactory;
//import net.sourceforge.xhsi.model.NavigationRadio;

//import net.sourceforge.xhsi.panel.GraphicsConfig;
//import net.sourceforge.xhsi.panel.Subcomponent;



public class Annunciators extends AnnunSubcomponent {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");


    private static final int ROWS = 6;
    private static final int COLS = 2;



    public Annunciators(ModelFactory model_factory, AnnunGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
    }


    public void paint(Graphics2D g2) {
        if ( true ) {
            drawAnnunciators(g2);
        }
    }


    private void drawAnnunciators(Graphics2D g2) {

        Color off_color = annun_gc.instrument_background_color.brighter();
        Color annun_color;
        g2.setStroke(new BasicStroke(3.0f * annun_gc.grow_scaling_factor));

        draw1Annun(g2, 0, 0, "STALL", ( this.aircraft.stall_warning() && this.aircraft.battery() ) ? annun_gc.warning_color : off_color);

        draw1Annun(g2, 0, 1, "TERRAIN", ( this.aircraft.terrain_warning() && this.aircraft.battery() ) ? annun_gc.warning_color : off_color);

        draw1Annun(g2, 1, 0, "AP DISCON", ( this.aircraft.ap_disconnect() && this.aircraft.battery() ) ? annun_gc.warning_color : off_color);

        draw1Annun(g2, 1, 1, "LOW FUEL", ( this.aircraft.low_fuel() && this.aircraft.battery() ) ? annun_gc.caution_color: off_color);

        draw1Annun(g2, 2, 0, "ICE DETECT", ( this.aircraft.icing() && this.aircraft.battery() ) ? annun_gc.caution_color: off_color);

        draw1Annun(g2, 2, 1, "PITOT HEAT", ( this.aircraft.pitot_heat() && this.aircraft.battery() ) ? annun_gc.caution_color: off_color);

        // GEAR
        if ( this.aircraft.battery() ) {
            if ( this.aircraft.gear_warning() ) {
                // || ( ( this.aircraft.agl_m() < 250.0f/3.28084f ) && ( ! this.aircraft.gear_is_down() ) && (...descenging...) )
                annun_color = annun_gc.warning_color;
            } else if ( this.aircraft.gear_unsafe() ) {
                // || ( ( this.aircraft.agl_m() < 500.0f/3.28084f ) && ( ! this.aircraft.gear_is_down() ) && (...descenging...) )
                annun_color = annun_gc.caution_color;
            } else {
                annun_color = off_color;
            }
        } else {
            annun_color = off_color;
        }
        draw1Annun(g2, 3, 0, "GEAR", annun_color);

        // SPEED BRK
        if ( this.aircraft.battery() ) {
            float speed_brake = this.aircraft.get_speed_brake();
            if ( speed_brake > 0.51f ) {
                annun_color = annun_gc.caution_color;
            } else if ( ( ( ! this.avionics.is_cl30() ) && ( speed_brake > 0.01f ) ) || ( ( this.avionics.is_cl30() ) && ( speed_brake > 0.033f ) ) ) {
                annun_color = annun_gc.unusual_color;
            } else if ( this.aircraft.speed_brake_armed() ) {
                annun_color = annun_gc.normal_color;
            } else {
                annun_color = off_color;
            }
        } else {
            annun_color = off_color;
        }
        draw1Annun(g2, 3, 1, "SPEED BRK", annun_color);

        // PARK BRK
        if ( this.aircraft.battery() ) {
            float parking_brake = this.aircraft.get_parking_brake();
            if ( ! this .aircraft.on_ground() && ( parking_brake > 0.01f ) ) {
                annun_color = annun_gc.warning_color;
            } else if ( parking_brake > 0.51f ) {
                annun_color = annun_gc.caution_color;
            } else if ( parking_brake > 0.01f ) {
                annun_color = annun_gc.unusual_color;
            } else {
                annun_color = off_color;
            }
        } else {
            annun_color = off_color;
        }
        draw1Annun(g2, 4, 0, "PARK BRK", annun_color);

        // AUTO BRK
        if ( this.aircraft.battery() ) {
            if ( ( ! this.aircraft.on_ground() && ( this.aircraft.auto_brake() == -1 ) ) ||
                    ( this.aircraft.on_ground() && ( this.aircraft.auto_brake() > 0 ) ) ) {
                // RTO in the air or 1,2,3,max on the ground : caution
                annun_color = annun_gc.caution_color;
            } else if ( ( this.aircraft.on_ground() && ( this.aircraft.auto_brake() == -1 ) ) ||
                    ( ! this.aircraft.on_ground() && ( this.aircraft.auto_brake() > 0 ) ) ) {
                // RTO on the ground or 1,2,3,max in the air : armed
                annun_color = annun_gc.normal_color;
            } else {
                annun_color = off_color;
            }
        } else {
            annun_color = off_color;
        }
        draw1Annun(g2, 4, 1, "AUTO BRK", annun_color);

        draw1Annun(g2, 5, 0, "", off_color);
        draw1Annun(g2, 5, 1, "", off_color);

    }


    private void draw1Annun(Graphics2D g2, int row, int col, String annun_str, Color annun_color) {

            int a_w = annun_gc.annun_square.width*7/16;
            int a_h = annun_gc.line_height_m * 2;
            int a_x;
            int a_y;
            int a1_x;
            int a1_y;

            if ( col == 0 ) {
                    a_x = annun_gc.annun_square.x + annun_gc.annun_square.width/2 - a_w - a_w/32;
                    a1_x = annun_gc.annun_square.x + annun_gc.annun_square.width/2 - a_w/2 - a_w/32 - annun_gc.get_text_width(g2, annun_gc.font_m, annun_str)/2;
            } else {
                    a_x = annun_gc.annun_square.x + annun_gc.annun_square.width/2 + a_w/32;
                    a1_x = annun_gc.annun_square.x + annun_gc.annun_square.width/2 + a_w/2 + a_w/32 - annun_gc.get_text_width(g2, annun_gc.font_m, annun_str)/2;
            }

            a_y = annun_gc.annun_square.y + annun_gc.annun_square.height/2 - (a_h + a_w/16)*ROWS/2 + (a_h + a_w/16)*row;
            a1_y = a_y + annun_gc.line_height_m*28/20;

            g2.setColor(annun_gc.instrument_background_color);
            g2.fillRect(a_x, a_y, a_w, a_h);

            if ( annun_gc.powered ) {
                g2.setColor(annun_color);
            } else {
                g2.setColor(annun_gc.instrument_background_color.brighter());
            }
            g2.drawRect(a_x+1, a_y+1, a_w-2, a_h-2);
            g2.setFont(annun_gc.font_m);
            g2.drawString(annun_str, a1_x, a1_y);

    }


}
