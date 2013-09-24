/**
* FlapsIndicator.java
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



public class FlapsIndicator extends AnnunSubcomponent {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");


    public FlapsIndicator(ModelFactory model_factory, AnnunGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
    }


    public void paint(Graphics2D g2) {
        if ( true ) {
            drawFlaps(g2);
        }
    }


    private void drawFlaps(Graphics2D g2) {

        float pos = this.aircraft.get_flap_position();
        float handle = this.aircraft.get_flap_handle();
        int detents = this.aircraft.get_flap_detents();
//pos=1.0f;
//detents=8;

        int f_r = annun_gc.flaps_square.width/2*7/8;
        int f_x = annun_gc.flaps_square.x + annun_gc.flaps_square.width/2;
        int f_y = annun_gc.flaps_square.y + annun_gc.flaps_square.height/2;

        g2.setColor(annun_gc.knobs_color);
        g2.fillOval(f_x-f_r, f_y-f_r, 2*f_r, 2*f_r);

        g2.setColor(annun_gc.background_color);
        g2.fillOval(f_x-f_r*280/300, f_y-f_r*280/300, 2*f_r*280/300, 2*f_r*280/300);

        g2.setColor(annun_gc.backpanel_color);
        g2.drawArc(f_x-f_r*205/300, f_y-f_r*205/300, 2*f_r*205/300, 2*f_r*205/300, 180, -270);
//        g2.drawLine(f_x-f_r*205/300, f_y, f_x, f_y);
//        g2.drawLine(f_x, f_y+f_r*205/300, f_x, f_y);

        AffineTransform original_at = g2.getTransform();

        g2.setColor(annun_gc.markings_color);
        int m_x[] = {
            f_x-f_r/10,
            f_x-f_r/8,
            f_x+f_r/8,
            f_x+f_r/10
        };
        int m_y[] = {
            f_y-f_r*220/300,
            f_y-f_r*200/300,
            f_y-f_r*200/300,
            f_y-f_r*220/300
        };
        for ( int i=0; i<=detents; i++ ) {
            g2.rotate(Math.toRadians(-90.0f + i*270.0f/detents), f_x, f_y);
            g2.fillPolygon(m_x, m_y, 4);
            g2.setTransform(original_at);
        }
        g2.setFont(annun_gc.font_l);
        g2.drawString("FLAPS", f_x - f_r*23/32, f_y + f_r*1/2);
        g2.setFont(annun_gc.font_s);
        g2.drawString("UP", f_x - f_r*220/300 - annun_gc.get_text_width(g2, annun_gc.font_s, "UP"), f_y + annun_gc.line_height_s/2 - 1);
        g2.drawString("DN", f_x - annun_gc.get_text_width(g2, annun_gc.font_s, "DN")/2, f_y + f_r*220/300 + annun_gc.line_height_s);

        g2.rotate(Math.toRadians(-90.0f + handle*270.0f), f_x, f_y);
        g2.setColor(annun_gc.dim_markings_color);
        g2.drawLine(f_x, f_y-f_r*200/300, f_x, f_y);
        g2.setTransform(original_at);

        int p_x[] = {
            f_x,
            f_x-f_r/10,
            f_x-f_r/10,
            f_x+f_r/10,
            f_x+f_r/10
        };
        int p_y[] = {
            f_y-f_r*200/300,
            f_y-f_r*125/300,
            f_y,
            f_y,
            f_y-f_r*125/300
        };
        g2.rotate(Math.toRadians(-90.0f + pos*270.0f), f_x, f_y);
        g2.setColor(annun_gc.markings_color);
        g2.fillPolygon(p_x, p_y, 5);
        g2.setTransform(original_at);

        g2.setColor(annun_gc.background_color);
        g2.fillOval(f_x-f_r*85/300, f_y-f_r*85/300, 2*f_r*85/300, 2*f_r*85/300);
        g2.setColor(annun_gc.backpanel_color);
        g2.drawOval(f_x-f_r*85/300, f_y-f_r*85/300, 2*f_r*85/300, 2*f_r*85/300);

    }


}
