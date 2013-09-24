/**
* DivText.java
* 
* A/T limit, Thrust mode and TAT
* 
* Copyright (C) 2011  Marc Rogiers (marrog.123@gmail.com)
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
package net.sourceforge.xhsi.flightdeck.eicas;

//import java.awt.BasicStroke;
//import java.awt.Color;
//import java.awt.Color;
import java.awt.Color;
import java.awt.Component;
//import java.awt.GradientPaint;
import java.awt.Graphics2D;
//import java.awt.Shape;
//import java.awt.Stroke;
import java.awt.geom.AffineTransform;
//import java.awt.geom.Area;
//import java.awt.geom.Rectangle2D;
//import java.awt.geom.RoundRectangle2D;
//import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import java.util.logging.Logger;

import net.sourceforge.xhsi.XHSISettings;

import net.sourceforge.xhsi.model.ModelFactory;



public class DivText extends EICASSubcomponent {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");


    public DivText(ModelFactory model_factory, EICASGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
    }


    public void paint(Graphics2D g2) {

        if ( eicas_gc.powered ) {
//if ( true ) {

            drawTAT(g2);
            drawThrustMode(g2);

        }

    }


    private void drawTAT(Graphics2D g2) {

//        float oat_value = this.aircraft.oat();
//        float tas = this.aircraft.true_air_speed();
//        float sound_speed = this.aircraft.sound_speed();
//        float mach = tas / sound_speed;
//        float tat_value = ( (oat_value + 273.0f ) * ( 1 + ( (1.4f-1.0f)/2.0f*1.0f*mach*mach ) ) ) - 273.0f;
        float tat_value = this.aircraft.tat();

        int y1 = eicas_gc.divtext_y;
        int x1 = eicas_gc.tat_x;

        g2.setColor(eicas_gc.color_boeingcyan);
        g2.setFont(eicas_gc.font_m);
        String str1 = "TAT ";
        g2.drawString(str1, x1, y1);

        x1 += eicas_gc.get_text_width(g2, eicas_gc.font_m, str1);

        g2.setColor(eicas_gc.markings_color);
        g2.setFont(eicas_gc.font_xl);
        str1 = "" + Math.round(tat_value);
        g2.drawString(str1, x1, y1);

        x1 += eicas_gc.get_text_width(g2, eicas_gc.font_xl, str1);

        g2.setColor(eicas_gc.markings_color);
        g2.setFont(eicas_gc.font_m);
        str1 = " C";
        g2.drawString(str1, x1, y1);

    }


    private void drawThrustMode(Graphics2D g2) {

        int y1 = eicas_gc.divtext_y;
        int x1 = eicas_gc.thrustmode_x;

        g2.setColor(eicas_gc.color_lime);
        g2.setFont(eicas_gc.font_xl);
        g2.drawString(this.aircraft.get_thrust_mode(), x1, y1);

    }


}
