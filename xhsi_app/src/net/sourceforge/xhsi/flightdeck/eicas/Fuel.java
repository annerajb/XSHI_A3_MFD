/**
* Fuel.java
* 
* Renders EICAS Fuel level indicators
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
package net.sourceforge.xhsi.flightdeck.eicas;

import java.awt.BasicStroke;
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

import java.util.logging.Logger;

import net.sourceforge.xhsi.XHSIPreferences;
import net.sourceforge.xhsi.XHSISettings;
import net.sourceforge.xhsi.model.ModelFactory;



public class Fuel extends EICASSubcomponent {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");


    public Fuel(ModelFactory model_factory, EICASGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
    }


    public void paint(Graphics2D g2) {
        if ( eicas_gc.powered ) {
            drawFuel(g2);
        }
    }


    private void drawFuel(Graphics2D g2) {

        int tanks = this.aircraft.num_tanks();
        boolean primaries = this.preferences.get_eicas_primary();
//tanks = 3;
        if ( tanks == 3 ) {
            drawGauge(g2, 0, "CTR", this.aircraft.get_fuel(1), this.aircraft.get_tank_capacity(1), primaries);
            drawGauge(g2, 1, "1", this.aircraft.get_fuel(0), this.aircraft.get_tank_capacity(0), primaries);
            drawGauge(g2, 2, "2", this.aircraft.get_fuel(2), this.aircraft.get_tank_capacity(2), primaries);
        } else if ( tanks == 2 ) {
            drawGauge(g2, 1, "1", this.aircraft.get_fuel(0), this.aircraft.get_tank_capacity(0), primaries);
            drawGauge(g2, 2, "2", this.aircraft.get_fuel(1), this.aircraft.get_tank_capacity(1), primaries);
        } else if ( tanks == 1 ) {
            drawGauge(g2, 0, "", this.aircraft.get_fuel(0), this.aircraft.get_tank_capacity(0), primaries);
        } else if ( tanks > 3 ) {
            drawGauge(g2, 0, "ALL", this.aircraft.get_total_fuel(), this.aircraft.get_fuel_capacity(), primaries);
        }
        if ( tanks > 1 ) {
            g2.setColor(eicas_gc.color_boeingcyan);
            g2.setFont(eicas_gc.font_m);
//            String units_str = XHSISettings.get_instance().fuel_units.get_units();
            String units_str = this.preferences.get_preference(XHSIPreferences.PREF_FUEL_UNITS);
            if ( primaries ) {
                g2.drawString("FUEL", eicas_gc.fuel_primary_x[0] - eicas_gc.get_text_width(g2, eicas_gc.font_m, "FUEL")/2, eicas_gc.fuel_primary_y[0] + eicas_gc.fuel_r - 2);
                g2.drawString(units_str, eicas_gc.fuel_primary_x[0] - eicas_gc.get_text_width(g2, eicas_gc.font_m, units_str)/2, eicas_gc.fuel_primary_y[0] + eicas_gc.fuel_r + eicas_gc.line_height_m - 2);
            } else {
                g2.drawString(units_str, eicas_gc.fuel_compact_x[0] - eicas_gc.get_text_width(g2, eicas_gc.font_m, units_str)/2, eicas_gc.fuel_compact_y[0] + eicas_gc.fuel_r - 2);
            }
        }

    }


    private void drawGauge(Graphics2D g2, int tank, String tank_str, float quantity, float range, boolean prims) {
//quantity = 1750.0f;
//range = 2000.0f;

        int fuel_x = prims ? eicas_gc.fuel_primary_x[tank] : eicas_gc.fuel_compact_x[tank];
        int fuel_y = prims ? eicas_gc.fuel_primary_y[tank] : eicas_gc.fuel_compact_y[tank];

        g2.setColor(eicas_gc.dim_markings_color);
        AffineTransform original_at = g2.getTransform();
        g2.rotate(Math.toRadians(-225.0f), fuel_x, fuel_y);
        for (int i=0; i<=12; i++) {
            g2.drawLine(fuel_x + eicas_gc.fuel_r*13/16, fuel_y,
                    fuel_x + eicas_gc.fuel_r*1015/1000, fuel_y);
            g2.rotate(Math.toRadians(22.5f), fuel_x, fuel_y);
        }
        g2.setTransform(original_at);

        if ( this.aircraft.low_fuel() ) {
            g2.setColor(eicas_gc.caution_color);
        } else {
            g2.setColor(eicas_gc.markings_color);
        }
        Stroke orininal_stroke = g2.getStroke();
        g2.setStroke(new BasicStroke(6.0f * eicas_gc.scaling_factor, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
        g2.drawArc(fuel_x - eicas_gc.fuel_r, fuel_y - eicas_gc.fuel_r, eicas_gc.fuel_r*2, eicas_gc.fuel_r*2,
                -135, Math.round(-270*quantity/range));
        g2.setStroke(orininal_stroke);

//        String qty_str = "" + Math.round( quantity * XHSISettings.get_instance().fuel_units.get_multiplier() );
        String qty_str = "" + Math.round( quantity * this.preferences.get_fuel_multiplier() );
        g2.setFont(eicas_gc.font_xl);
        g2.drawString(qty_str, fuel_x - eicas_gc.get_text_width(g2, eicas_gc.font_xl, qty_str)/2, fuel_y + eicas_gc.line_height_xl/2 - 2);

        g2.setColor(eicas_gc.color_boeingcyan);
        g2.setFont(eicas_gc.font_m);
        g2.drawString(tank_str, fuel_x - eicas_gc.get_text_width(g2, eicas_gc.font_m, tank_str)/2, fuel_y - eicas_gc.fuel_r*3/8 - 2);

    }


}
