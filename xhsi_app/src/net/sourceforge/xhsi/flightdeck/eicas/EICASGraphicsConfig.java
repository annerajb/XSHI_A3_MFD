/**
 * NDGraphicsConfig.java
 *
 * Calculates and provides access to screen positions and sizes based on the
 * size of HSIComponent.
 *
 * Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
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
package net.sourceforge.xhsi.flightdeck.eicas;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.Map;

import net.sourceforge.xhsi.XHSIInstrument;
import net.sourceforge.xhsi.XHSIPreferences;

import net.sourceforge.xhsi.model.Avionics;

import net.sourceforge.xhsi.flightdeck.GraphicsConfig;


public class EICASGraphicsConfig extends GraphicsConfig implements ComponentListener {

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    public int eicas_size;
    public int dials_width;
    public int prim_dials_height;
    public int hyd_dials_height;
    public int dial_n1_y;
    public int dial_egt_y;
    public int dial_n2_y;
    public int dial_ff_y;
    public int dial_ng_y;
    public int dial_oil_p_y;
    public int dial_oil_t_y;
    public int dial_oil_q_y;
    public int dial_vib_y;
    public int dial_hyd_p_y;
    public int dial_hyd_q_y;
    public int dial_r[] = new int[9];
    public Font dial_font[] = new Font[9];
    public int dial_font_w[] = new int[9];
    public int dial_font_h[] = new int[9];
    public int alerts_w;
    public int alerts_x0;
    public int alert_y[] = new int[3];
    public int alert_h;
    public int fuel_r;
    public int fuel_primary_x[] = new int[3];
    public int fuel_primary_y[] = new int[3];
    public int fuel_compact_x[] = new int[3];
    public int fuel_compact_y[] = new int[3];
    public int divtext_y;
    public int tat_x;
    public int thrustmode_x;


    public EICASGraphicsConfig(Component root_component, int du) {
        super(root_component);
        this.display_unit = du;
        init();
    }


//    public void init() {
//
//        super.init();
//
//    }


    public void update_config(Graphics2D g2, boolean power) {

        if (this.resized
                || this.reconfig
                || (this.powered != power)
            ) {
            // one of the settings has been changed

            // remember the avionics power settings
            this.powered = power;

//logger.warning("EICAS update_config");
            // general instrument config
            super.update_config(g2);

            // some subcomponents need to be reminded to redraw imediately
            this.reconfigured = true;

            eicas_size = Math.min(panel_rect.width, panel_rect.height);

            // the dials take +/-60% of the width
            dials_width = panel_rect.width*575/1000;
            dial_n1_y = panel_rect.y + eicas_size*17/100;
            dial_egt_y = panel_rect.y + eicas_size*36/100;
            dial_n2_y = panel_rect.y + eicas_size*55/100;
            dial_ff_y = panel_rect.y + eicas_size*73/100;
            dial_ng_y = panel_rect.y + eicas_size*87/100;
            dial_oil_p_y = panel_rect.y + eicas_size*29/100;
            dial_oil_t_y = panel_rect.y + eicas_size*44/100;
            dial_oil_q_y = panel_rect.y + eicas_size*54/100;
            dial_vib_y = panel_rect.y + eicas_size*65/100;
            dial_hyd_p_y = panel_rect.y + panel_rect.height - eicas_size*16/100;
            dial_hyd_q_y = panel_rect.y + panel_rect.height - eicas_size*6/100;
            dial_r[0] = 0; // no radius for 0 engines
            dial_r[1] = Math.min(eicas_size*9/100, panel_rect.width*9/100); // dial radius when there is 1 engine
            dial_r[2] = Math.min(eicas_size*9/100, panel_rect.width*9/100); // dial radius when there are 2 engines
            dial_r[3] = Math.min(eicas_size*9/100, panel_rect.width*75/1000); // etc...
            dial_r[4] = Math.min(eicas_size*9/100, panel_rect.width*525/10000);
            dial_r[5] = Math.min(eicas_size*9/100, panel_rect.width*5/100);
            dial_r[6] = Math.min(eicas_size*9/100, panel_rect.width*45/1000);
            dial_r[7] = Math.min(eicas_size*9/100, panel_rect.width*4/100);
            dial_r[8] = Math.min(eicas_size*9/100, panel_rect.width*325/10000);
            prim_dials_height = eicas_size*73/100 + dial_r[2];
            hyd_dials_height = eicas_size*25/100;
            dial_font[0] = null;
            dial_font[1] = font_xxl;
            dial_font_w[1] = digit_width_xxl;
            dial_font_h[1] = line_height_xxl;
            dial_font[2] = font_xxl;
            dial_font_w[2] = digit_width_xxl;
            dial_font_h[2] = line_height_xxl;
            dial_font[3] = font_m;
            dial_font_w[3] = digit_width_m;
            dial_font_h[3] = line_height_m;
            dial_font[4] = font_s;
            dial_font_w[4] = digit_width_s;
            dial_font_h[4] = line_height_s;
            dial_font[5] = font_s;
            dial_font_w[5] = digit_width_s;
            dial_font_h[5] = line_height_s;
            dial_font[6] = font_xs;
            dial_font_w[6] = digit_width_xs;
            dial_font_h[6] = line_height_xs;
            dial_font[7] = font_xs;
            dial_font_w[7] = digit_width_xs;
            dial_font_h[7] = line_height_xs;
            dial_font[8] = font_xxs;
            dial_font_w[8] = digit_width_xxs;
            dial_font_h[8] = line_height_xxs;

            // TAT and Thrust Mode
            divtext_y = panel_rect.y + eicas_size*40/1000;
            tat_x = panel_rect.x + dials_width*12/16;
            thrustmode_x = panel_rect.x + dials_width*7/16;
            
            // the annunciators and fuel take 40% of the width
            alerts_w = panel_rect.width*39/100;
            alerts_x0 = panel_rect.x + panel_rect.width - alerts_w;
            alert_h = line_height_xs*25/10;
            alert_y[0] = panel_rect.y + eicas_size*5/100;
            alert_y[1] = alert_y[0] + alert_h;
            alert_y[2] = alert_y[1] + alert_h;

            fuel_r = eicas_size*40/100*20/100;
            fuel_primary_x[0] = alerts_x0 + alerts_w/2;
            fuel_primary_x[1] = alerts_x0 + alerts_w/2 - fuel_r*5/4;
            fuel_primary_x[2] = alerts_x0 + alerts_w/2 + fuel_r*5/4;
            fuel_primary_y[0] = panel_rect.y + panel_rect.height - fuel_r*3;
            fuel_primary_y[1] = panel_rect.y + panel_rect.height - fuel_r;
            fuel_primary_y[2] = fuel_primary_y[1];

            fuel_compact_x[0] = panel_rect.x + dials_width/2;
            fuel_compact_x[1] = fuel_compact_x[0] - fuel_r*9/4;
            fuel_compact_x[2] = fuel_compact_x[0] + fuel_r*9/4;
            fuel_compact_y[0] = panel_rect.y + panel_rect.height - fuel_r*19/16;
            fuel_compact_y[1] = panel_rect.y + panel_rect.height - fuel_r*14/16;
            fuel_compact_y[2] = fuel_compact_y[1];

        }

    }


//    public int get_text_width(Graphics graphics, Font font, String text) {
//        return graphics.getFontMetrics(font).stringWidth(text);
//    }


//    public int get_text_height(Graphics graphics, Font font) {
//        return graphics.getFontMetrics(font).getHeight();
//    }


    public void componentResized(ComponentEvent event) {
        this.component_size = event.getComponent().getSize();
        this.frame_size = event.getComponent().getSize();
        this.resized = true;
    }


    public void componentMoved(ComponentEvent event) {
        this.component_topleft = event.getComponent().getLocation();
    }


    public void componentShown(ComponentEvent arg0) {
        // TODO Auto-generated method stub
    }


    public void componentHidden(ComponentEvent arg0) {
        // TODO Auto-generated method stub
    }


}
