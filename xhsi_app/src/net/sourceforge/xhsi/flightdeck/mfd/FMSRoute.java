/**
* FMSRoute.java
* 
* Prints the first entries of the FMS route
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
package net.sourceforge.xhsi.flightdeck.mfd;

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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import java.util.logging.Logger;

//import net.sourceforge.xhsi.XHSISettings;

import net.sourceforge.xhsi.model.Airport;
import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ComRadio;
import net.sourceforge.xhsi.model.FMS;
import net.sourceforge.xhsi.model.FMSEntry;
import net.sourceforge.xhsi.model.Localizer;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.NavigationObjectRepository;
import net.sourceforge.xhsi.model.Runway;



public class FMSRoute extends MFDSubcomponent {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    private int rte_y;
    private int rte_x0;
    private int rte_x1;
    private int rte_x2;
    private int rte_x3;
    private int rte_x4;
    private int rte_x5;
    
    private float total_dist;

    private DecimalFormat one_decimal_formatter;
    private DecimalFormatSymbols format_symbols;
    private DecimalFormat eta_hours_formatter;
    private DecimalFormat eta_minutes_formatter;
     
    
    public FMSRoute(ModelFactory model_factory, MFDGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);

        one_decimal_formatter = new DecimalFormat("0.0");
        format_symbols = one_decimal_formatter.getDecimalFormatSymbols();
        format_symbols.setDecimalSeparator('.');
        one_decimal_formatter.setDecimalFormatSymbols(format_symbols);
        eta_hours_formatter = new DecimalFormat("00");
        eta_minutes_formatter = new DecimalFormat("00");

    }


    public void paint(Graphics2D g2) {
        if ( mfd_gc.powered && ( this.avionics.get_mfd_mode() == Avionics.MFD_MODE_FPLN ) ) {
            drawFMSRoute(g2);
        }
    }


    private void drawFMSEntry(Graphics2D g2, FMSEntry fms_entry) {

        float leg;
        
//        // an extra precaution
//        if ( fms_entry != null ) {
            
            if ( fms_entry == fms.get_active_waypoint() ) {
                g2.setColor(mfd_gc.fmc_active_color);
            } else if ( fms_entry == fms.get_displayed_waypoint() ) {
                g2.setColor(mfd_gc.fmc_disp_color);
            } else {
                g2.setColor(mfd_gc.fmc_other_color);
            }
            
            g2.drawString(fms_entry.name, rte_x0, rte_y);

            // altitude
            if ( fms_entry.altitude != 0 ) {
                String alt_str = Integer.toString(fms_entry.altitude);
                g2.drawString(alt_str, rte_x1 + mfd_gc.digit_width_xl*5 - mfd_gc.get_text_width(g2, mfd_gc.font_xl, alt_str), rte_y);
            }
            
            // leg and total distance
            if ( fms_entry.active ) {
                leg = this.avionics.get_gps_radio().get_distance();
                total_dist = leg;
            } else {
                leg = fms_entry.leg_dist;
                total_dist += leg;
            }
            String dist_str = one_decimal_formatter.format(leg);
            g2.drawString(dist_str, rte_x2 + mfd_gc.digit_width_xl*4 - mfd_gc.get_text_width(g2, mfd_gc.font_xl, dist_str), rte_y);
            dist_str = Integer.toString(Math.round(total_dist));
            g2.drawString(dist_str, rte_x3 + mfd_gc.digit_width_xl*4 - mfd_gc.get_text_width(g2, mfd_gc.font_xl, dist_str), rte_y);
            
            // ttg and eta
            if ( fms_entry.total_ete != 0.0f ) {
                float ttg = fms_entry.total_ete;
                String ttg_str = one_decimal_formatter.format(ttg);
                g2.drawString(ttg_str, rte_x4 + mfd_gc.digit_width_xl*4 - mfd_gc.get_text_width(g2, mfd_gc.font_xl, ttg_str), rte_y);
                int wpt_eta = Math.round( (float)this.aircraft.time_after_ete(fms_entry.total_ete) / 60.0f );
                int hours_at_arrival = (wpt_eta / 60) % 24;
                int minutes_at_arrival = wpt_eta % 60;
                String eta_str = eta_hours_formatter.format(hours_at_arrival) + eta_minutes_formatter.format(minutes_at_arrival) + "z";
                g2.drawString(eta_str, rte_x5, rte_y);
            }
            
//        }

    }
    
    
    private void drawFMSRoute(Graphics2D g2) {

        g2.setColor(mfd_gc.efb_color);

        String title = "FPLN";
        int cdu_size = Math.min(mfd_gc.panel_rect.width, mfd_gc.panel_rect.height);
        int title_x = mfd_gc.panel_rect.x + mfd_gc.panel_rect.width/2 - mfd_gc.get_text_width(g2, mfd_gc.font_xxl, title)/2;
        int title_y = mfd_gc.panel_rect.y + cdu_size/16;
        g2.setFont(mfd_gc.font_xxl);
        g2.drawString(title, title_x, title_y);
        title_x = mfd_gc.panel_rect.x + mfd_gc.panel_rect.width/16;
        g2.drawLine(title_x, title_y + mfd_gc.line_height_m/2, mfd_gc.panel_rect.x + mfd_gc.panel_rect.width*15/16, title_y + mfd_gc.line_height_m/2);

        g2.setFont(mfd_gc.font_xl);

        rte_y = title_y + mfd_gc.line_height_xl*2;
        rte_x0 = title_x;
        rte_x1 = rte_x0 + mfd_gc.get_text_width(g2, mfd_gc.font_xl, "WOXOW "); // alt
        rte_x2 = rte_x1 + mfd_gc.digit_width_xl*6; // leg dist
        rte_x3 = rte_x2 + mfd_gc.digit_width_xl*5; // total dist
        rte_x4 = rte_x3 + mfd_gc.digit_width_xl*6; // ttg
        rte_x5 = rte_x4 + mfd_gc.digit_width_xl*5; // eta

        g2.drawString("TO", rte_x0, rte_y);
        g2.drawString("ALT", rte_x1, rte_y);
        g2.drawString("LEG", rte_x2, rte_y);
        g2.drawString("DST", rte_x3, rte_y);
        g2.drawString("ETE", rte_x4, rte_y);
        g2.drawString("ETA", rte_x5, rte_y);
        rte_y += mfd_gc.line_height_xl*150/100;
        
        fms = this.avionics.get_fms();
        if ( fms.is_active() ) {

            int i = 1;
            
            total_dist = 0.0f;
            
            FMSEntry fms_entry = fms.get_active_waypoint();
            if ( fms_entry != null ) {
                drawFMSEntry(g2, fms_entry);
                i = fms_entry.index;
            }
        
            int max = 17;
            int n = 1;
            while ( ( n < max ) && ( i+1 < fms.get_nb_of_entries() ) ) {
                i += 1;
                fms_entry = fms.get_entry(i);
                if ( fms_entry != null ) {
                    rte_y += mfd_gc.line_height_xl*125/100;
                    drawFMSEntry(g2, fms_entry);
                }
                n += 1;
            }
        }
        
    }


}
