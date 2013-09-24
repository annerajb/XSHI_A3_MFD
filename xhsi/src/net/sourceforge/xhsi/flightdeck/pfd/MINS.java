/**
* MINS.java
* 
* Reference minimums : Radio Altimeter or Decision Altitude
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
package net.sourceforge.xhsi.flightdeck.pfd;

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



public class MINS extends PFDSubcomponent {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");


    public MINS(ModelFactory model_factory, PFDGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
    }


    public void paint(Graphics2D g2) {
        if ( pfd_gc.powered ) {
            if ( this.aircraft.mins_is_baro() )
                drawBaro(g2);
            else
                drawRadio(g2);
        }
    }


    private void drawRadio(Graphics2D g2) {

        int ra = Math.round(this.aircraft.agl_m() * 3.28084f);
//ra = 1234;
        int ra_r = pfd_gc.ra_r;
        int ra_x = pfd_gc.ra_x;
        int ra_y = this.preferences.get_draw_aoa() ? pfd_gc.ra_low_y : pfd_gc.ra_high_y;

        int ra_bug = this.aircraft.ra_bug();
//ra_bug = 1234;

        // minimums reached on descent?
        boolean minimums = ( ra < ra_bug ) && ( this.aircraft.vvi() < 0.0f ) && ( ! this.aircraft.on_ground() );
        boolean airborne = ! this.aircraft.on_ground();
        boolean ra_at_top = ! this.preferences.get_draw_aoa();
//minimums = true;
//airborne = true;
//ra_at_top = true;

        // Decision Height readout
        if ( ( ra_bug > 0 ) && ( ( ra > 1000 ) || ! airborne || ! ra_at_top ) ) {
            // not shown when RA is a round dial
            g2.setColor(pfd_gc.color_lime);
            g2.setFont(pfd_gc.font_m);
            g2.drawString(Integer.toString(ra_bug), ra_x, ra_y + ( ra_at_top ? ra_r*3/4 : 0 ) );
            g2.setFont(pfd_gc.font_xs);
            g2.drawString("RADIO", ra_x - pfd_gc.get_text_width(g2, pfd_gc.font_xs, "RADIO "), ra_y + ( ra_at_top ? ra_r*3/4 : 0 ) );
        }

        // Display Radar Altitude
        if ( ( ra < 2500 ) && airborne ) {

            // above 1000ft, round to 10
            if ( ra > 1000 ) {
                ra = ( ra + 5 ) / 10 * 10;
            }

            // the round dial should flash when descending below DH, but that will be for later
            // until then, we make the dial stand out like this:
            if ( ( ra < 1000 ) && minimums && ra_at_top ) {
                pfd_gc.setTransparent(g2, this.preferences.get_draw_colorgradient_horizon());
                g2.setColor(pfd_gc.instrument_background_color);
                g2.fillOval(ra_x-ra_r*21/20, ra_y-ra_r*21/20, 2*ra_r*21/20, 2*ra_r*21/20);
                pfd_gc.setOpaque(g2);
            }

            // if below bug: amber else: white
            String ra_str = "" + ra;
            if ( minimums ) {
                g2.setColor(pfd_gc.caution_color);
            } else {
                g2.setColor(pfd_gc.markings_color);
            }

            g2.setFont(pfd_gc.font_xl);
            if ( ( ra < 1000 ) && ra_at_top ) {

                // round dial below 1000ft, but only when AOA is not drawn
                // markings
                AffineTransform original_at = g2.getTransform();
                for ( int i=0; i<10; i++) {
                    g2.drawLine(ra_x, ra_y-ra_r+1, ra_x, ra_y-ra_r*17/20);
                    g2.rotate(Math.toRadians(36), ra_x, ra_y);
                }
                g2.setTransform(original_at);
                // ra_bug on round dial below 1000ft
                if ( ra_bug < 1000 ) {
                    g2.rotate(Math.toRadians(360*ra_bug/1000), ra_x, ra_y);
                    int bug_x[] = {
                        ra_x,
                        ra_x-ra_r*33/275,
                        ra_x+ra_r*33/275
                    };
                    int bug_y[] = {
                        ra_y-ra_r+2,
                        ra_y-ra_r*175/275,
                        ra_y-ra_r*175/275
                    };
                    //g2.setColor(pfd_gc.heading_bug_color);
                    g2.fillPolygon(bug_x, bug_y, 3);
                    g2.setTransform(original_at);
                }
                // the arc indicating current RA
                Stroke original_stroke = g2.getStroke();
                g2.setStroke(new BasicStroke(3.0f * pfd_gc.scaling_factor));
                g2.drawArc(ra_x-ra_r, ra_y-ra_r, 2*ra_r, 2*ra_r, 90, -ra*360/1000);
                g2.setStroke(original_stroke);
            }
            // digital readout of the current RA
            g2.drawString(ra_str, ra_x + 3*pfd_gc.digit_width_xl/2 - pfd_gc.get_text_width(g2, pfd_gc.font_xl, ra_str), ( ra_at_top ? ra_y + pfd_gc.line_height_xl/2 - 2 : ra_y + pfd_gc.line_height_xl ) );
        }

    }


    private void drawBaro(Graphics2D g2) {

        int ra_r = pfd_gc.ra_r;
        int baro_x = pfd_gc.ra_x;
        int baro_y = pfd_gc.ra_low_y;

        int da_bug = this.aircraft.da_bug();
//da_bug = 1234;

        // Decision Altitude readout
        if ( ( da_bug > 0 ) ) {
            // not shown when zero
            // below minimums?
            boolean minimums = ( this.aircraft.altitude_ind() < da_bug ) && ( ! this.aircraft.on_ground() );
//minimums = true;
            if ( minimums )
                g2.setColor(pfd_gc.caution_color);
            else
                g2.setColor(pfd_gc.color_lime);
            g2.setFont(pfd_gc.font_xs);
            g2.drawString("BARO", baro_x, baro_y);
            g2.setFont(pfd_gc.font_m);
            g2.drawString(Integer.toString(da_bug), baro_x, baro_y + pfd_gc.line_height_m);
        }


    }


}
