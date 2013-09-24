/**
* Hydraulics.java
* 
* Hydraulics instruments
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

import java.awt.BasicStroke;
//import java.awt.Color;
//import java.awt.Color;
import java.awt.Component;
//import java.awt.GradientPaint;
import java.awt.Graphics2D;
//import java.awt.Shape;
import java.awt.Stroke;
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



public class Hydraulics extends EICASSubcomponent {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");


    private Stroke original_stroke;

    private int seco_dial_x[] = new int[8];


    public Hydraulics(ModelFactory model_factory, EICASGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
    }


    public void paint(Graphics2D g2) {

        if ( eicas_gc.powered && ( this.aircraft.num_engines() > 0 ) && ! this.preferences.get_eicas_primary() ) {
//if ( true && ! this.preferences.get_eicas_primary() ) {

            for (int i=0; i<2; i++) {
                seco_dial_x[i] = eicas_gc.alerts_x0 + i*eicas_gc.alerts_w/2 + (eicas_gc.alerts_w/2*15/16)/2;
                drawHydP(g2, i);
                drawHydQ(g2, i);
            }

            String ind_str;
            int ind_x;
            g2.setColor(eicas_gc.color_boeingcyan);
            g2.setFont(eicas_gc.font_xs);

            // HYD P
            ind_str = "HYD P";
                ind_x = (seco_dial_x[0] + seco_dial_x[1]) / 2 - eicas_gc.get_text_width(g2, eicas_gc.font_xs, ind_str)/2;
            g2.drawString(ind_str, ind_x, eicas_gc.dial_hyd_p_y + eicas_gc.dial_r[2]*70/100 + eicas_gc.line_height_xs);
            // HYD Q
            ind_str = "HYD Q %";
                ind_x = (seco_dial_x[0] + seco_dial_x[1]) / 2 - eicas_gc.get_text_width(g2, eicas_gc.font_xs, ind_str)/2;
            g2.drawString(ind_str, ind_x, eicas_gc.dial_hyd_q_y + eicas_gc.line_height_xs*35/20);

        }

    }


    private void drawHydP(Graphics2D g2, int pos) {

        AffineTransform original_at = g2.getTransform();
        setPen(g2);

        float hyd_p_dial = this.aircraft.get_hyd_press(pos);

        int hyd_p_x = seco_dial_x[pos];
        int hyd_p_y = eicas_gc.dial_hyd_p_y;
        int hyd_p_r = eicas_gc.dial_r[2] * 70 /100;

        g2.setColor(eicas_gc.warning_color);
        g2.drawArc(hyd_p_x-hyd_p_r, hyd_p_y-hyd_p_r, 2*hyd_p_r, 2*hyd_p_r, -45, -75);
        g2.setColor(eicas_gc.caution_color);
        g2.drawArc(hyd_p_x-hyd_p_r, hyd_p_y-hyd_p_r, 2*hyd_p_r, 2*hyd_p_r, -45-75, -30);
        g2.setColor(eicas_gc.dim_markings_color);
        g2.drawArc(hyd_p_x-hyd_p_r, hyd_p_y-hyd_p_r, 2*hyd_p_r, 2*hyd_p_r, -45-75-30, -90);
        g2.setColor(eicas_gc.caution_color);
        g2.drawArc(hyd_p_x-hyd_p_r, hyd_p_y-hyd_p_r, 2*hyd_p_r, 2*hyd_p_r, -45-75-30-90, -30);
        g2.setColor(eicas_gc.warning_color);
        g2.drawArc(hyd_p_x-hyd_p_r, hyd_p_y-hyd_p_r, 2*hyd_p_r, 2*hyd_p_r, -45-75-30-90-30, -45);

        // needle
        g2.rotate(Math.toRadians( Math.round(hyd_p_dial*270.0f) + 45 ), hyd_p_x, hyd_p_y);
        g2.setColor(eicas_gc.markings_color);
        g2.drawLine(hyd_p_x, hyd_p_y, hyd_p_x+hyd_p_r, hyd_p_y);
        g2.setTransform(original_at);

        resetPen(g2);

    }


    private void drawHydQ(Graphics2D g2, int pos) {

        int hyd_q_val = Math.round( this.aircraft.get_hyd_quant(pos) * 100.0f );
        String hyd_q_str = "" + hyd_q_val;

        int hyd_q_x = seco_dial_x[pos] - eicas_gc.get_text_width(g2, eicas_gc.font_l, hyd_q_str)/2;
        int hyd_q_y = eicas_gc.dial_hyd_q_y + eicas_gc.line_height_l/2;

        g2.setColor(eicas_gc.markings_color);
        g2.setFont(eicas_gc.font_l);
        g2.drawString(hyd_q_str, hyd_q_x, hyd_q_y);

    }


    private void setPen(Graphics2D g2) {

        original_stroke = g2.getStroke();
        g2.setStroke(new BasicStroke(2.5f * eicas_gc.grow_scaling_factor, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));

    }


    private void resetPen(Graphics2D g2) {

        g2.setStroke(original_stroke);

    }


}
