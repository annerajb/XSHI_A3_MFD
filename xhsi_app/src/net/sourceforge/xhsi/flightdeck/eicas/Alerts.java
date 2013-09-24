/**
* Alerts.java
* 
* Oil end Fuel annunciators
* 
* Copyright (C) 2010-2011  Marc Rogiers (marrog.123@gmail.com)
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



public class Alerts extends EICASSubcomponent {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    private static enum AlertType {
        OIL_P,
        OIL_T,
        FUEL_P
    }


    private int alert_w;
    private int alert_x[] = new int[8];


    public Alerts(ModelFactory model_factory, EICASGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
    }


    public void paint(Graphics2D g2) {
        if ( eicas_gc.powered ) {
            drawAlerts(g2);
        }
    }


    private void drawAlerts(Graphics2D g2) {

        int num_eng = this.aircraft.num_engines();
//num_eng=2;
        int n = Math.max(num_eng, 2);

        if ( num_eng > 0 ) {

            String eng_str;
            String low_fuel;
            String high_oil;
            String low_oil;
            String pres_str;
            String temp_str;
            if ( num_eng < 3 ) {
                eng_str = "ENG ";
                low_fuel = "LOW FUEL";
                high_oil = "HIGH OIL";
                low_oil = "LOW OIL";
                pres_str = "PRESSURE";
                temp_str = "TEMPERATURE";
            } else if ( num_eng < 5 ) {
                eng_str = "";
                low_fuel = "FUEL";
                high_oil = "OIL";
                low_oil = "OIL";
                pres_str = "PRES";
                temp_str = "TEMP";
            } else {
                eng_str = "";
                low_fuel = "F";
                high_oil = "O";
                low_oil = "O";
                pres_str = "P";
                temp_str = "T";
            }

            alert_w = eicas_gc.alerts_w/n * 15/16;

            for (int i=0; i<num_eng; i++) {

                alert_x[i] = eicas_gc.alerts_x0 + i*eicas_gc.alerts_w/num_eng;

                drawEngNum(g2, i, eng_str);
                draw1Alert(g2, i, 0, low_fuel, pres_str, this.aircraft.fuel_press_alert(i));
                draw1Alert(g2, i, 1, high_oil, temp_str, this.aircraft.oil_temp_alert(i));
                draw1Alert(g2, i, 2, low_oil, pres_str, this.aircraft.oil_press_alert(i));

            }

        }

    }


    private void draw1Alert(Graphics2D g2, int eng, int line, String line1_str, String line2_str, boolean lit) {
//lit=true;
        if ( lit ) {
            g2.setColor(eicas_gc.caution_color);
            g2.fillRect(alert_x[eng], eicas_gc.alert_y[line], alert_w, eicas_gc.alert_h);
            g2.setColor(eicas_gc.background_color);
            g2.setFont(eicas_gc.font_xs);
            g2.drawString(line1_str, alert_x[eng] + alert_w/2 -eicas_gc.get_text_width(g2, eicas_gc.font_xs, line1_str)/2, eicas_gc.alert_y[line] + eicas_gc.line_height_xs*45/40);
            g2.drawString(line2_str, alert_x[eng] + alert_w/2 -eicas_gc.get_text_width(g2, eicas_gc.font_xs, line2_str)/2, eicas_gc.alert_y[line] + eicas_gc.line_height_xs*85/40);
        }
        g2.setColor(eicas_gc.instrument_background_color);
        g2.drawRect(alert_x[eng], eicas_gc.alert_y[line], alert_w, eicas_gc.alert_h);

    }


    private void drawEngNum(Graphics2D g2, int eng, String prefix_str) {

        g2.setColor(eicas_gc.color_boeingcyan);
        g2.setFont(eicas_gc.font_s);
        String eng_str = prefix_str + (eng + 1);
        g2.drawString(eng_str, alert_x[eng] + alert_w/2 - eicas_gc.get_text_width(g2, eicas_gc.font_s, eng_str)/2, eicas_gc.alert_y[0] - eicas_gc.line_height_s*3/8 - 2);

    }


}
