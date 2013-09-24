/**
* CDI.java
* 
* Renders a Course Deviation Indicator
* 
* Copyright (C) 2009  Marc Rogiers (marrog.123@gmail.com)
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
package net.sourceforge.xhsi.flightdeck.nd;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.Localizer;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.NavigationRadio;
import net.sourceforge.xhsi.model.RadioNavigationObject;


public class CDI extends NDSubcomponent {

    private static final long serialVersionUID = 1L;

    private AffineTransform original_at;

    public CDI(ModelFactory model_factory, NDGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
    }


    public void paint(Graphics2D g2) {

        if ( nd_gc.powered && ( nd_gc.mode_app || nd_gc.mode_vor ) ) {
            drawCDI(g2, nd_gc.mode_classic_hsi);
        }
    }


    private void drawCDI(Graphics2D g2, boolean steam_hsi) {

        int dot_r = Math.round(3.0f * nd_gc.grow_scaling_factor);
        int bar_w = (int)(5*nd_gc.grow_scaling_factor);
        int dot_dist = ( steam_hsi ) ? nd_gc.rose_radius/4 : Math.round(35.0f * nd_gc.grow_scaling_factor);
        int fromto_h = (int)(21*nd_gc.grow_scaling_factor);
        int fromto_w = (int)(15*nd_gc.grow_scaling_factor);
	float dash[] = { 24.0f, 6.0f };
        int source = this.avionics.hsi_source();
        float cdi_value = 0.0f;
        int fromto = 0;
        float course = 0.0f;
        boolean is_vor = false;
        boolean reception = true;

        String to_from = "";

        if (source == Avionics.HSI_SOURCE_GPS) {
            cdi_value = this.avionics.gps_hdef_dot();
            fromto = this.avionics.gps_fromto();
            course = this.avionics.gps_course();
        } else if (source == Avionics.HSI_SOURCE_NAV1) {
            cdi_value = this.avionics.nav1_hdef_dot();
            fromto = this.avionics.nav1_fromto();
            course = this.avionics.nav1_obs();
            is_vor = ( this.avionics.get_tuned_localizer(1) == null );
            reception = this.avionics.get_nav_radio(1).receiving();
        } else if(source == Avionics.HSI_SOURCE_NAV2) {
            cdi_value = this.avionics.nav2_hdef_dot();
            fromto = this.avionics.nav2_fromto();
            course = this.avionics.nav2_obs();
            is_vor = ( this.avionics.get_tuned_localizer(2) == null );
            reception = this.avionics.get_nav_radio(2).receiving();
        }
        int cdi_pixels = Math.round(cdi_value * (float)dot_dist);

        int c_x = nd_gc.map_center_x;
        int c_y = nd_gc.map_center_y;

        rotate(g2, course - this.aircraft.heading() );

        int radius = nd_gc.rose_radius;
        if ( nd_gc.mode_centered ) radius *= 2;

        g2.setColor(nd_gc.deviation_scale_color);
        if (fromto != NavigationRadio.VOR_RECEPTION_OFF) {
            if (fromto == NavigationRadio.VOR_RECEPTION_TO) {
                to_from = "TO";
            } else {
                to_from = "FROM";
            }
            int p_y;
            int p_l;
            if ( steam_hsi ) {
                p_y = c_y-nd_gc.rose_radius*3/8+3;
                p_l = nd_gc.rose_radius*6/8-6;
            } else {
                p_y = c_y-radius*3/16+2;
                p_l = radius*6/16-4;
            }
            g2.setColor(nd_gc.nav_needle_color);
            if (Math.abs(cdi_value) < 2.49f) {
                g2.fillRect( c_x + cdi_pixels - bar_w, p_y, bar_w*2, p_l );
            } else {
                g2.drawRect( c_x + cdi_pixels - bar_w, p_y, bar_w*2, p_l );
            }
            if ( is_vor && reception ) {
                int triangle_x[] = { c_x, c_x+fromto_w, c_x-fromto_w };
                int t_d;
                if ( steam_hsi ) {
                    t_d= nd_gc.rose_radius*5/16;
                } else {
                    t_d = radius*3/16 - 2;
                }
                int triangle_to_y[] = { c_y - t_d, c_y - t_d + fromto_h, c_y - t_d + fromto_h };
                int triangle_from_y[] = { c_y + t_d, c_y + t_d - fromto_h, c_y + t_d - fromto_h };
                g2.setColor(nd_gc.deviation_scale_color);
                if (fromto == NavigationRadio.VOR_RECEPTION_TO) {
                    g2.drawPolygon(triangle_x, triangle_to_y, 3);
                } else {
                    g2.drawPolygon(triangle_x, triangle_from_y, 3);
                }
            }
        }
        g2.setColor(nd_gc.deviation_scale_color);
        g2.drawOval(c_x-dot_dist-dot_r, c_y-dot_r, 2*dot_r, 2*dot_r);
        g2.drawOval(c_x+dot_dist-dot_r, c_y-dot_r, 2*dot_r, 2*dot_r);
        g2.drawOval(c_x-2*dot_dist-dot_r, c_y-dot_r, 2*dot_r, 2*dot_r);
        g2.drawOval(c_x+2*dot_dist-dot_r, c_y-dot_r, 2*dot_r, 2*dot_r);
        if ( steam_hsi ) {
            // Old-style HSI
            int front_x[] = {
                c_x,
                c_x+bar_w,
                c_x+bar_w,
                c_x+bar_w*3,
                c_x+bar_w*3,
                c_x+bar_w,
                c_x+bar_w,
                c_x-bar_w,
                c_x-bar_w,
                c_x-bar_w*3,
                c_x-bar_w*3,
                c_x-bar_w,
                c_x-bar_w
            };
            int front_y[] = {
                c_y-nd_gc.rose_radius+nd_gc.big_tick_length+2,
                c_y-nd_gc.rose_radius+nd_gc.big_tick_length+bar_w*2+2,
                c_y-nd_gc.rose_radius*3/8-bar_w*4,
                c_y-nd_gc.rose_radius*3/8-bar_w*4,
                c_y-nd_gc.rose_radius*3/8-bar_w*2,
                c_y-nd_gc.rose_radius*3/8-bar_w*2,
                c_y-nd_gc.rose_radius*3/8,
                c_y-nd_gc.rose_radius*3/8,
                c_y-nd_gc.rose_radius*3/8-bar_w*2,
                c_y-nd_gc.rose_radius*3/8-bar_w*2,
                c_y-nd_gc.rose_radius*3/8-bar_w*4,
                c_y-nd_gc.rose_radius*3/8-bar_w*4,
                c_y-nd_gc.rose_radius+nd_gc.big_tick_length+bar_w*2+2,
            };
            g2.setColor( get_navsource_color() );
            //g2.drawLine(c_x, c_y-nd_gc.rose_radius+nd_gc.small_tick_length+2, c_x, c_y-nd_gc.rose_radius+nd_gc.big_tick_length+2);
            g2.drawPolygon(front_x, front_y, 13);
            g2.drawRect( c_x-bar_w, c_y+nd_gc.rose_radius*3/8, bar_w*2, nd_gc.rose_radius*5/8-nd_gc.big_tick_length-2 );
        } else {
            // Centered or Expanded Map
            g2.setColor(nd_gc.nav_needle_color);
            if ( nd_gc.mode_centered ) {
                g2.drawLine(c_x, c_y-radius/2+2, c_x, c_y-radius*5/16-bar_w);
            } else {
                g2.drawLine(c_x, c_y-radius+2, c_x, c_y-radius*5/16-bar_w);
            }
            Stroke original_stroke = g2.getStroke();
            g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
            if ( nd_gc.mode_centered ) {
                g2.drawLine(c_x, c_y+radius*5/16, c_x, c_y+radius/2-2);
            } else {
                g2.drawLine(c_x, c_y+radius*5/16, c_x, c_y+radius-2);
            }
            g2.setStroke(original_stroke);
            int front_x[] = { c_x, c_x+bar_w, c_x+bar_w, c_x-bar_w, c_x-bar_w };
            int front_y[] = { c_y-radius*5/16-bar_w, c_y-radius*5/16+bar_w, c_y-radius*3/16, c_y-radius*3/16, c_y-radius*5/16+bar_w };
            g2.setColor(nd_gc.deviation_scale_color);
            g2.drawPolygon(front_x, front_y, 5);
            g2.drawRect( c_x-bar_w, c_y+radius*3/16, bar_w*2, radius*2/16 );
        }

        unrotate(g2);

        if ( (fromto != NavigationRadio.VOR_RECEPTION_OFF) && is_vor && reception ) {
            int to_from_x;
            int to_from_y;
            if ( steam_hsi ) {
                to_from_x = c_x+nd_gc.rose_radius*7/16;
                to_from_y = c_y+nd_gc.rose_radius*7/16;
            } else {
                to_from_x = c_x+radius*3/16;
                to_from_y = c_y+radius*1/16;
            }
            g2.setColor(nd_gc.deviation_scale_color);
            g2.setFont(nd_gc.font_medium);
            g2.drawString(to_from, to_from_x, to_from_y);
        }


    }


    private void rotate(Graphics2D g2, double angle) {
        this.original_at = g2.getTransform();
        AffineTransform rotate = AffineTransform.getRotateInstance(
            Math.toRadians(angle),
            nd_gc.map_center_x,
            nd_gc.map_center_y
        );
        g2.transform(rotate);
    }

    private void unrotate(Graphics2D g2) {
        g2.setTransform(original_at);
    }

    private Color get_navsource_color() {

        Color hsi_color;
        if ( this.preferences.get_draw_colored_hsi_course() ) {
            int hsi_source = this.avionics.hsi_source();
            if ( hsi_source == Avionics.HSI_SOURCE_GPS ) {
                hsi_color = nd_gc.fmc_active_color;
            } else {
                int bank;
                if ( hsi_source == Avionics.HSI_SOURCE_NAV1 ) {
                    bank = 1;
                } else /*if ( hsi_source == Avionics.HSI_SOURCE_NAV2 )*/ {
                    bank = 2;
                }

                hsi_color = nd_gc.tuned_vor_color;
                NavigationRadio radio = this.avionics.get_nav_radio(bank);
                if (radio != null) {
                    RadioNavigationObject rnav_object = radio.get_radio_nav_object();
                    if ( radio.receiving() && (rnav_object instanceof Localizer) ) {
                        hsi_color = nd_gc.tuned_localizer_color;
                    }
                }
            }
        } else {
            hsi_color = nd_gc.deviation_scale_color;
        }

        return hsi_color;

    }


}
