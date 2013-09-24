/**
* HSI.java
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
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
//import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import java.util.logging.Logger;

//import net.sourceforge.xhsi.XHSISettings;

import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.FMS;
import net.sourceforge.xhsi.model.Localizer;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.NavigationObject;
import net.sourceforge.xhsi.model.NavigationRadio;
import net.sourceforge.xhsi.model.RadioNavigationObject;
import net.sourceforge.xhsi.model.RadioNavBeacon;



public class HSI extends PFDSubcomponent {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    private DecimalFormat degrees_formatter;
    private DecimalFormat integer_formatter;
    private DecimalFormat one_decimal_formatter;
    private DecimalFormatSymbols format_symbols;

    private static final int TYPE_NAV = 0;
    private static final int TYPE_VOR = 1;
    private static final int TYPE_DME = 2;
    private static final int TYPE_ILS = 3;
    private static final int TYPE_LOC = 4;
    private static final int TYPE_FMC = 5;
    private static final int TYPE_ERR = 6;
    private String type_list[] = { "NAV", "VOR", "DME", "ILS", "LOC", "FMC", "ERR" };

    private Color navsource_color;

    private static final float FIVEDEG = 7.5f;

    private AffineTransform original_at;


    public HSI(ModelFactory model_factory, PFDGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);

        degrees_formatter = new DecimalFormat("000");
        integer_formatter = new DecimalFormat("0");
        one_decimal_formatter = new DecimalFormat("0.0");
        format_symbols = one_decimal_formatter.getDecimalFormatSymbols();
        format_symbols.setDecimalSeparator('.');
        one_decimal_formatter.setDecimalFormatSymbols(format_symbols);

    }


    public void paint(Graphics2D g2) {
        if ( pfd_gc.powered ) {
            drawDisc(g2);
            drawRose(g2);
            drawTrack(g2);
            drawBug(g2);
            drawHSISource(g2);
            drawHSI(g2);
            drawWind(g2);
        }
    }


    private void drawDisc(Graphics2D g2) {

        // special cutout, only for full-width horizon with DG
        if ( this.preferences.get_draw_fullwidth_horizon() && ! pfd_gc.draw_hsi ) {
            g2.setColor(pfd_gc.background_color);
            g2.fillOval(pfd_gc.dg_cx - pfd_gc.dg_radius*10/9, pfd_gc.dg_cy - pfd_gc.dg_radius*10/9, pfd_gc.dg_radius*10/9*2, pfd_gc.dg_radius*10/9*2);
        }

        pfd_gc.setTransparent(g2, this.preferences.get_draw_fullscreen_horizon());
        if ( pfd_gc.draw_hsi && ! this.preferences.get_draw_fullscreen_horizon() ) {
            g2.setColor(pfd_gc.background_color);
        } else {
            g2.setColor(pfd_gc.instrument_background_color);
        }
        g2.fillOval(pfd_gc.dg_cx - pfd_gc.dg_radius - 1, pfd_gc.dg_cy - pfd_gc.dg_radius - 1, pfd_gc.dg_radius*2 + 2, pfd_gc.dg_radius*2 + 2);
        pfd_gc.setOpaque(g2);

    }


    private void drawRose(Graphics2D g2) {

        int bugtext_x;
        int text_dx;
        int bugtext_y;
        if ( pfd_gc.draw_hsi ) {
            text_dx = pfd_gc.dg_radius/4;
            bugtext_y = pfd_gc.dg_cy - pfd_gc.dg_radius*92/80;
        } else {
            text_dx = pfd_gc.dg_radius/16;
            bugtext_y = Math.min(
                pfd_gc.panel_rect.y + pfd_gc.panel_rect.height*990/1000,
                pfd_gc.dg_cy - pfd_gc.dg_radius/2
            );
        }

        g2.setColor(pfd_gc.heading_labels_color);
        g2.setFont(pfd_gc.font_s);
        bugtext_x = pfd_gc.dg_cx + text_dx;
        g2.drawString("MAG", bugtext_x, bugtext_y);

        if ( pfd_gc.draw_hsi && this.preferences.get_draw_fullscreen_horizon() ) {
            g2.clearRect(pfd_gc.dg_cx - text_dx - 3*pfd_gc.digit_width_l - pfd_gc.digit_width_l/2 - pfd_gc.digit_width_m, bugtext_y - pfd_gc.line_height_l*7/8, pfd_gc.digit_width_l/2 + 3*pfd_gc.digit_width_l + pfd_gc.digit_width_m + pfd_gc.digit_width_m/2, pfd_gc.line_height_l);
        }
        g2.setColor(pfd_gc.heading_bug_color);
        g2.setFont(pfd_gc.font_m);
        bugtext_x = pfd_gc.dg_cx - text_dx - pfd_gc.digit_width_m;
        g2.drawString("H", bugtext_x, bugtext_y);
//        DecimalFormat degrees_formatter = new DecimalFormat("000");
        String bugtext = degrees_formatter.format(  Math.round(this.avionics.heading_bug()) );
        //int bugtext_w = pfd_gc.get_text_width(g2, pfd_gc.font_medium, bugtext);
        g2.setFont(pfd_gc.font_l);
        bugtext_x = pfd_gc.dg_cx - text_dx - pfd_gc.digit_width_m - 3*pfd_gc.digit_width_l;
        g2.drawString(bugtext, bugtext_x, bugtext_y);

        float hdg = this.aircraft.heading();

        original_at = g2.getTransform();
        g2.setColor(pfd_gc.markings_color);

        if ( ! pfd_gc.full_rose ) {

            // when only the top of the HSI rose is shown, put the marks further apart like in the real B737-NG
            // round our HDG to to nearest 5deg
            int hdg5 = (int)Math.round( hdg / 5.0f ) * 5;
            // rotate to that mark
            g2.rotate(Math.toRadians((hdg5-hdg)*FIVEDEG/5.0f), pfd_gc.dg_cx, pfd_gc.dg_cy);
            // from that mark, we will draw marks 35deg left and right
            g2.rotate(Math.toRadians(-7*FIVEDEG), pfd_gc.dg_cx, pfd_gc.dg_cy);
            for (int i=-35; i<=35; i+=5) {
                int mark5 = hdg5 + i;
                drawMark(g2, (mark5+360)%360);
                g2.rotate(Math.toRadians(FIVEDEG), pfd_gc.dg_cx, pfd_gc.dg_cy);
            }

        } else {

            // normal circle
            g2.rotate(Math.toRadians(-hdg), pfd_gc.dg_cx, pfd_gc.dg_cy);
            for (int mark5=0; mark5<=355; mark5+=5) {
                drawMark(g2, mark5);
                g2.rotate(Math.toRadians(5.0f), pfd_gc.dg_cx, pfd_gc.dg_cy);
            }

        }

        g2.setTransform(original_at);

    }


    private void drawMark(Graphics2D g2, int mark) {

        if ( mark % 10 == 0 ) {
            String marktext = "" + mark/10;
            int w=0;
            int h=0;
            Stroke original_stroke = g2.getStroke();
            if ( mark % 30 == 0 ) {
                g2.setFont(pfd_gc.font_xxl);
                w = pfd_gc.get_text_width(g2, pfd_gc.font_xxl, marktext);
                h = pfd_gc.line_height_xxl;
                g2.setStroke(new BasicStroke(3.0f));
                g2.drawString(marktext, pfd_gc.dg_cx - w/2, pfd_gc.dg_cy - pfd_gc.dg_radius + pfd_gc.dg_radius*4/64 + h);
            } else if ( ! pfd_gc.full_rose ) {
                // the 10deg marks only when only the top of the HSI rose is shown
                g2.setFont(pfd_gc.font_l);
                w = pfd_gc.get_text_width(g2, pfd_gc.font_l, marktext);
                h = pfd_gc.line_height_l;
                g2.drawString(marktext, pfd_gc.dg_cx - w/2, pfd_gc.dg_cy - pfd_gc.dg_radius + pfd_gc.dg_radius*4/64 + h);
            }
            g2.drawLine(pfd_gc.dg_cx, pfd_gc.dg_cy - pfd_gc.dg_radius + 1, pfd_gc.dg_cx, pfd_gc.dg_cy - pfd_gc.dg_radius + pfd_gc.dg_radius*4/64);
            g2.setStroke(original_stroke);
        } else {
            g2.drawLine(pfd_gc.dg_cx, pfd_gc.dg_cy - pfd_gc.dg_radius + 1, pfd_gc.dg_cx, pfd_gc.dg_cy - pfd_gc.dg_radius + pfd_gc.dg_radius/64);
        }

    }


    private void drawTrack(Graphics2D g2) {

        float drift = this.aircraft.heading() - this.aircraft.track();
        if ( drift >  180.0f ) drift -= 360.0f;
        if ( drift < -180.0f ) drift += 360.0f;

        original_at = g2.getTransform();

        if ( pfd_gc.full_rose ) {
            // normal circle
            g2.rotate(Math.toRadians(-drift), pfd_gc.dg_cx, pfd_gc.dg_cy);
        } else {
            // scale is FIVEDEG for 5deg
            g2.rotate(Math.toRadians(-drift*FIVEDEG/5.0f), pfd_gc.dg_cx, pfd_gc.dg_cy);
        }

        g2.setColor(pfd_gc.markings_color);
        if ( pfd_gc.draw_hsi ) {
            // a diamond for the current track
            int pointer_height = (int)(pfd_gc.hsi_tick_w / 2);;
            int pointer_width = (int)(pfd_gc.hsi_tick_w / 3);
            g2.setColor(pfd_gc.aircraft_color);
            g2.drawLine(
                pfd_gc.dg_cx, pfd_gc.dg_cy - pfd_gc.dg_radius,
                pfd_gc.dg_cx + pointer_width, pfd_gc.dg_cy - pfd_gc.dg_radius + pointer_height
            );
            g2.drawLine(
                pfd_gc.dg_cx + pointer_width, pfd_gc.dg_cy - pfd_gc.dg_radius + pointer_height,
                pfd_gc.dg_cx, pfd_gc.dg_cy - pfd_gc.dg_radius + 2*pointer_height
            );
            g2.drawLine(
                pfd_gc.dg_cx, pfd_gc.dg_cy - pfd_gc.dg_radius + 2*pointer_height,
                pfd_gc.dg_cx - pointer_width, pfd_gc.dg_cy - pfd_gc.dg_radius + pointer_height
            );
            g2.drawLine(
                pfd_gc.dg_cx - pointer_width, pfd_gc.dg_cy - pfd_gc.dg_radius + pointer_height,
                pfd_gc.dg_cx, pfd_gc.dg_cy - pfd_gc.dg_radius
            );
        } else {
            // a track line
            g2.drawLine(pfd_gc.dg_cx, pfd_gc.dg_cy - pfd_gc.dg_radius + 1, pfd_gc.dg_cx, pfd_gc.dg_cy);
            g2.drawLine(pfd_gc.dg_cx - pfd_gc.dg_radius/32, pfd_gc.dg_cy - pfd_gc.dg_radius*3/4, pfd_gc.dg_cx + pfd_gc.dg_radius/32, pfd_gc.dg_cy - pfd_gc.dg_radius*3/4);
        }

        g2.setTransform(original_at);

    }


    private void drawBug(Graphics2D g2) {

        float bug = this.avionics.heading_bug() - this.aircraft.heading();

        if ( bug >  180.0f ) bug -= 360.0f;
        if ( bug < -180.0f ) bug += 360.0f;

        original_at = g2.getTransform();
        g2.setColor(pfd_gc.markings_color);

        if ( ! pfd_gc.full_rose ) {
            g2.rotate(Math.toRadians((bug)*FIVEDEG/5.0f), pfd_gc.dg_cx, pfd_gc.dg_cy);
        } else {
            g2.rotate(Math.toRadians(bug), pfd_gc.dg_cx, pfd_gc.dg_cy);
        }

        int b_w = pfd_gc.dg_radius/16;
        int bug_x[] = {
            pfd_gc.dg_cx,
            pfd_gc.dg_cx - b_w,
            pfd_gc.dg_cx - b_w,
            pfd_gc.dg_cx - b_w/2,
            pfd_gc.dg_cx,
            pfd_gc.dg_cx + b_w/2,
            pfd_gc.dg_cx + b_w,
            pfd_gc.dg_cx + b_w
        };
        int b_h = pfd_gc.dg_radius/16;
        int bug_y[] = {
            pfd_gc.dg_cy - pfd_gc.dg_radius,
            pfd_gc.dg_cy - pfd_gc.dg_radius,
            pfd_gc.dg_cy - pfd_gc.dg_radius - b_h,
            pfd_gc.dg_cy - pfd_gc.dg_radius - b_h,
            pfd_gc.dg_cy - pfd_gc.dg_radius,
            pfd_gc.dg_cy - pfd_gc.dg_radius - b_h,
            pfd_gc.dg_cy - pfd_gc.dg_radius - b_h,
            pfd_gc.dg_cy - pfd_gc.dg_radius
        };
        g2.setColor(pfd_gc.heading_bug_color);
        g2.drawPolygon(bug_x, bug_y, 8);

        g2.setTransform(original_at);

        if ( pfd_gc.draw_hsi ) {

            // 45 deg marks for HSI mode
            g2.setColor(pfd_gc.markings_color);
            int mark_length = pfd_gc.hsi_tick_w;
//            g2.drawLine(pfd_gc.dg_cx, pfd_gc.dg_cy - pfd_gc.dg_radius - mark_length, pfd_gc.dg_cx, pfd_gc.dg_cy - pfd_gc.dg_radius);
            g2.drawLine(pfd_gc.dg_cx, pfd_gc.dg_cy + pfd_gc.dg_radius, pfd_gc.dg_cx, pfd_gc.dg_cy + pfd_gc.dg_radius + mark_length);
            g2.drawLine(pfd_gc.dg_cx - pfd_gc.dg_radius - mark_length, pfd_gc.dg_cy, pfd_gc.dg_cx - pfd_gc.dg_radius, pfd_gc.dg_cy);
            g2.drawLine(pfd_gc.dg_cx + pfd_gc.dg_radius, pfd_gc.dg_cy, pfd_gc.dg_cx + pfd_gc.dg_radius + mark_length, pfd_gc.dg_cy);
            g2.transform(AffineTransform.getRotateInstance(Math.toRadians(45.0), pfd_gc.dg_cx, pfd_gc.dg_cy));
            g2.drawLine(pfd_gc.dg_cx, pfd_gc.dg_cy - pfd_gc.dg_radius - mark_length, pfd_gc.dg_cx, pfd_gc.dg_cy - pfd_gc.dg_radius);
            g2.drawLine(pfd_gc.dg_cx, pfd_gc.dg_cy + pfd_gc.dg_radius, pfd_gc.dg_cx, pfd_gc.dg_cy + pfd_gc.dg_radius + mark_length);
            g2.drawLine(pfd_gc.dg_cx - pfd_gc.dg_radius - mark_length, pfd_gc.dg_cy, pfd_gc.dg_cx - pfd_gc.dg_radius, pfd_gc.dg_cy);
            g2.drawLine(pfd_gc.dg_cx + pfd_gc.dg_radius, pfd_gc.dg_cy, pfd_gc.dg_cx + pfd_gc.dg_radius + mark_length, pfd_gc.dg_cy);
            g2.setTransform(original_at);

//        } else {
        }

            // current heading triangle at the top when not in HSI mode
            int t_w = pfd_gc.dg_radius*5/64;
            int triangle_x[] = {
                pfd_gc.dg_cx,
                pfd_gc.dg_cx - t_w/2,
                pfd_gc.dg_cx + t_w/2
            };
            int t_h = pfd_gc.dg_radius*5/64;
            int triangle_y[] = {
                pfd_gc.dg_cy - pfd_gc.dg_radius - 2,
                pfd_gc.dg_cy - pfd_gc.dg_radius - t_h - 2,
                pfd_gc.dg_cy - pfd_gc.dg_radius - t_h - 2
            };
            g2.setColor(pfd_gc.markings_color);
            g2.drawPolygon(triangle_x, triangle_y, 3);

//        }

    }


    private void drawWind(Graphics2D g2) {

        if ( pfd_gc.draw_hsi ) {

            int wind_speed = Math.round( aircraft_environment.wind_speed() );
            if ( wind_speed > 4 ) {
//if ( true ) {

                int wind_dir = ( Math.round(aircraft_environment.wind_direction() + this.aircraft.magnetic_variation()) + 360 ) % 360;

                g2.setColor(pfd_gc.wind_color);
                g2.setFont(pfd_gc.font_s);
                g2.drawString(degrees_formatter.format(wind_dir) + "\u00B0 / " + wind_speed, pfd_gc.dg_cx - pfd_gc.dg_radius - 2*pfd_gc.hsi_tick_w, pfd_gc.dg_cy - pfd_gc.dg_radius);

                int w_x = pfd_gc.dg_cx - pfd_gc.dg_radius*103/100;
                int w_y = pfd_gc.dg_cy - pfd_gc.dg_radius*84/100;
                int w_l = pfd_gc.dg_radius/10;
                int w_a = pfd_gc.dg_radius/30;

                int hdg = (Math.round(this.aircraft.heading()) + 360) % 360;
                g2.transform(AffineTransform.getRotateInstance(Math.toRadians(wind_dir - hdg), w_x, w_y));
//                g2.drawLine(w_x, w_y+w_l+1, w_x, w_y-w_l);
//                g2.drawLine(w_x, w_y+w_l, w_x-w_a, w_y+w_l-w_a);
//                g2.drawLine(w_x, w_y+w_l, w_x+w_a, w_y+w_l-w_a);
                GeneralPath polyline;
                polyline = new GeneralPath(GeneralPath.WIND_NON_ZERO, 2);
                polyline.moveTo(w_x, w_y-w_l);
                polyline.lineTo(w_x, w_y+w_l);
                polyline.lineTo(w_x-w_a, w_y+w_l-w_a);
                g2.draw(polyline);
                polyline = new GeneralPath(GeneralPath.WIND_NON_ZERO, 2);
                polyline.moveTo(w_x, w_y-w_l);
                polyline.lineTo(w_x, w_y+w_l);
                polyline.lineTo(w_x+w_a, w_y+w_l-w_a);
                g2.draw(polyline);
                g2.setTransform(original_at);

            }

        }

    }


    private void drawHSI(Graphics2D g2) {

        if ( pfd_gc.draw_hsi ) {

            int x_points_heading_box[] = { pfd_gc.dg_cx - pfd_gc.digit_width_xl*2, pfd_gc.dg_cx - pfd_gc.digit_width_xl*2, pfd_gc.dg_cx + pfd_gc.digit_width_xl*2, pfd_gc.dg_cx + pfd_gc.digit_width_xl*2 };
            int y_points_heading_box[] = { pfd_gc.dg_cy - pfd_gc.dg_radius*99/80, pfd_gc.dg_cy - pfd_gc.dg_radius*89/80, pfd_gc.dg_cy - pfd_gc.dg_radius*89/80, pfd_gc.dg_cy - pfd_gc.dg_radius*99/80 };
            g2.clearRect(pfd_gc.dg_cx - pfd_gc.digit_width_xl*2, pfd_gc.dg_cy - pfd_gc.dg_radius*99/80, pfd_gc.digit_width_xl*4, pfd_gc.dg_radius*10/80);
            g2.setColor(pfd_gc.markings_color);
            g2.drawPolyline(x_points_heading_box, y_points_heading_box, 4);
            int hdg = (Math.round(this.aircraft.heading()) + 360) % 360;
            String hdg_str = degrees_formatter.format(hdg);
            g2.setFont(pfd_gc.font_xl);
            g2.drawString(hdg_str, pfd_gc.dg_cx - pfd_gc.digit_width_xl*3/2, pfd_gc.dg_cy - pfd_gc.dg_radius*91/80);

            int dot_r = Math.round(4.0f * pfd_gc.grow_scaling_factor);
            int bar_w = (int)(5*pfd_gc.grow_scaling_factor);
            int dot_dist = pfd_gc.dg_radius/4;
            int fromto_h = (int)(21*pfd_gc.grow_scaling_factor);
            int fromto_w = (int)(15*pfd_gc.grow_scaling_factor);
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

            int c_x = pfd_gc.dg_cx;
            int c_y = pfd_gc.dg_cy;

            rotate(g2, course - this.aircraft.heading() );

            int radius = pfd_gc.dg_radius;

            g2.setColor(pfd_gc.markings_color);
            if (fromto != NavigationRadio.VOR_RECEPTION_OFF) {
                if (fromto == NavigationRadio.VOR_RECEPTION_TO) {
                    to_from = "TO";
                } else {
                    to_from = "FROM";
                }
                int p_y;
                int p_l;
                p_y = c_y-radius*3/8+3;
                p_l = radius*6/8-6;
                g2.setColor(pfd_gc.nav_needle_color);
                if (Math.abs(cdi_value) < 2.49f) {
                    g2.fillRect( c_x + cdi_pixels - bar_w, p_y, bar_w*2, p_l );
                } else {
                    g2.drawRect( c_x + cdi_pixels - bar_w, p_y, bar_w*2, p_l );
                }
                if ( is_vor && reception ) {
                    int triangle_x[] = { c_x, c_x+fromto_w, c_x-fromto_w };
                    int t_d;
                    t_d= radius*5/16;
                    int triangle_to_y[] = { c_y - t_d, c_y - t_d + fromto_h, c_y - t_d + fromto_h };
                    int triangle_from_y[] = { c_y + t_d, c_y + t_d - fromto_h, c_y + t_d - fromto_h };
                    if ( this.preferences.get_draw_colored_hsi_course() ) {
                        g2.setColor(this.navsource_color);
                    } else {
                        g2.setColor(pfd_gc.markings_color);
                    }
                    if (fromto == NavigationRadio.VOR_RECEPTION_TO) {
                        g2.drawPolygon(triangle_x, triangle_to_y, 3);
                    } else {
                        g2.drawPolygon(triangle_x, triangle_from_y, 3);
                    }
                }
            }
            g2.setColor(pfd_gc.markings_color);
            g2.drawOval(c_x-dot_dist-dot_r, c_y-dot_r, 2*dot_r, 2*dot_r);
            g2.drawOval(c_x+dot_dist-dot_r, c_y-dot_r, 2*dot_r, 2*dot_r);
            g2.drawOval(c_x-2*dot_dist-dot_r, c_y-dot_r, 2*dot_r, 2*dot_r);
            g2.drawOval(c_x+2*dot_dist-dot_r, c_y-dot_r, 2*dot_r, 2*dot_r);
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
                c_y-radius+pfd_gc.hsi_tick_w+2,
                c_y-radius+pfd_gc.hsi_tick_w+bar_w*2+2,
                c_y-radius*3/8-bar_w*4,
                c_y-radius*3/8-bar_w*4,
                c_y-radius*3/8-bar_w*2,
                c_y-radius*3/8-bar_w*2,
                c_y-radius*3/8,
                c_y-radius*3/8,
                c_y-radius*3/8-bar_w*2,
                c_y-radius*3/8-bar_w*2,
                c_y-radius*3/8-bar_w*4,
                c_y-radius*3/8-bar_w*4,
                c_y-radius+pfd_gc.hsi_tick_w+bar_w*2+2,
            };
            g2.setColor(pfd_gc.nav_needle_color);
            g2.drawLine(c_x, c_y-radius+1, c_x, c_y-radius+pfd_gc.hsi_tick_w+2);
            if ( this.preferences.get_draw_colored_hsi_course() ) {
                g2.setColor(this.navsource_color);
            } else {
                g2.setColor(pfd_gc.markings_color);
            }
            //g2.drawLine(c_x, c_y-radius+pfd_gc.small_tick_length+2, c_x, c_y-radius+pfd_gc.hsi_tick_w+2);
            g2.drawPolygon(front_x, front_y, 13);
            g2.drawRect( c_x-bar_w, c_y+radius*3/8, bar_w*2, radius*5/8-pfd_gc.hsi_tick_w-2 );

            unrotate(g2);

            if ( (fromto != NavigationRadio.VOR_RECEPTION_OFF) && is_vor && reception ) {
                int to_from_x;
                int to_from_y;
                    to_from_x = c_x+radius*7/16;
                    to_from_y = c_y+radius*7/16;
//                g2.setColor(pfd_gc.markings_color);
                g2.setFont(pfd_gc.font_l);
                g2.drawString(to_from, to_from_x, to_from_y);
            }

            int plane_width = (int) (30 * pfd_gc.scaling_factor);
            int plane_height = (int) (plane_width * 1.5);
            g2.setColor(pfd_gc.markings_color);
            g2.drawLine(c_x - plane_width/4, c_y - plane_height, c_x - plane_width/4, c_y + plane_height);
            g2.drawLine(c_x + plane_width/4, c_y - plane_height, c_x + plane_width/4, c_y + plane_height);
            g2.drawLine(c_x - plane_height, c_y, c_x - plane_width/4, c_y);
            g2.drawLine(c_x + plane_width/4, c_y, c_x + plane_height, c_y);
            g2.drawLine(c_x - plane_width/2 - plane_width/4, c_y + plane_height, c_x - plane_width/4, c_y + plane_height);
            g2.drawLine(c_x + plane_width/4, c_y + plane_height, c_x + plane_width/2 + plane_width/4, c_y + plane_height);

        }

    }

    
    private void drawHSISource(Graphics2D g2) {

        // pfff... a lot of code duplication from ND/RefSourceLabel and ND/DestinationLabel

        if ( pfd_gc.draw_hsi ) {

            int hsi_source = this.avionics.hsi_source();
            String source_str;
            String crs_label = "CRS ";
            String crs_value;
            float dest_dist;
            String dest_id;
            if ( hsi_source == Avionics.HSI_SOURCE_NAV1 ) {
                // get src_type and set this.navsource_color
                int src_type = nav_radio_type(1);
                source_str = type_list[ src_type ] + " 1";
//                crs_label = "OBS ";
                crs_value = degrees_formatter.format( Math.round(this.avionics.nav1_obs()) );
                dest_dist =  this.avionics.get_nav_radio(1).get_distance();
                dest_id = this.avionics.get_nav_radio(1).get_nav_id();
            } else if ( hsi_source == Avionics.HSI_SOURCE_NAV2 ) {
                // get src_type and set this.navsource_color
                int src_type = nav_radio_type(2);
                source_str = type_list[ src_type ] + " 2";
//                crs_label = "OBS ";
                crs_value = degrees_formatter.format( Math.round(this.avionics.nav2_obs()) );
                dest_dist =  this.avionics.get_nav_radio(2).get_distance();
                dest_id = this.avionics.get_nav_radio(2).get_nav_id();
            } else {
                this.navsource_color = pfd_gc.fmc_active_color;
                source_str = "FMC";
//                crs_label = "CRS ";
                crs_value = degrees_formatter.format( Math.round(this.avionics.gps_course()) );
                dest_dist =  this.avionics.get_gps_radio().get_distance();
                NavigationObject dest_navobj = (NavigationObject)FMS.get_instance().get_active_waypoint();
                if (dest_navobj != null) {
                    dest_id = dest_navobj.name;
                } else {
                    dest_id = "";
                }
            }
            String dist_text;
            if (dest_dist == 0.0f) {
                dist_text = "---";
            } else if (dest_dist < 99.5f) {
                dist_text = one_decimal_formatter.format(dest_dist);
            } else {
                dist_text = integer_formatter.format(dest_dist);
            }

            g2.setColor(this.navsource_color);
            int src_x = pfd_gc.dg_cx + pfd_gc.dg_radius*9/8;
            int src_x1;
            int src_y = pfd_gc.dg_cy - pfd_gc.dg_radius*3/4;
            // source
            g2.setFont(pfd_gc.font_l);
            g2.drawString(source_str, src_x, src_y);
            src_y += pfd_gc.line_height_m;
            // CRS
            g2.setFont(pfd_gc.font_xs);
            g2.drawString(crs_label, src_x, src_y);
            src_x1 = src_x + pfd_gc.get_text_width(g2, pfd_gc.font_xs, crs_label);
            g2.setFont(pfd_gc.font_s);
            g2.drawString(crs_value, src_x1, src_y);
            src_y += pfd_gc.line_height_m;
            // dist
            g2.setFont(pfd_gc.font_s);
            g2.drawString(dist_text, src_x, src_y);
            src_x1 = src_x + pfd_gc.get_text_width(g2, pfd_gc.font_s, dist_text);
            g2.setFont(pfd_gc.font_xs);
            g2.drawString(" NM", src_x1, src_y);
            src_y += pfd_gc.line_height_m;
            // id
            g2.setFont(pfd_gc.font_s);
            g2.drawString(dest_id, src_x, src_y);

        }

    }


    private int nav_radio_type(int bank) {

        this.navsource_color = pfd_gc.tuned_vor_color;
        int type = TYPE_NAV;
        NavigationRadio radio = this.avionics.get_nav_radio(bank);
        RadioNavigationObject rnav_object;
        if (radio != null) {
            rnav_object = radio.get_radio_nav_object();
            if (radio.receiving()) {
                if (rnav_object instanceof RadioNavBeacon) {
                    if (((RadioNavBeacon) rnav_object).type == RadioNavBeacon.TYPE_VOR) {
                        if (((RadioNavBeacon) rnav_object).has_dme) {
                            this.navsource_color = pfd_gc.tuned_vor_color;
                            type = TYPE_VOR;
                        }
                    } else if (((RadioNavBeacon) rnav_object).type == RadioNavBeacon.TYPE_STANDALONE_DME) {
                        type = TYPE_DME;
                    } else {
                        type = TYPE_ERR;
                    }
                } else if (rnav_object instanceof Localizer) {
                    this.navsource_color = pfd_gc.tuned_localizer_color;
                    if ( ((Localizer) rnav_object).has_gs )
                        type = TYPE_ILS;
                    else
                        type = TYPE_LOC;
                }
            }
        }

        return type;

    }


    private void rotate(Graphics2D g2, double angle) {
        this.original_at = g2.getTransform();
        AffineTransform rotate = AffineTransform.getRotateInstance(
            Math.toRadians(angle),
            pfd_gc.dg_cx,
            pfd_gc.dg_cy
        );
        g2.transform(rotate);
    }

    private void unrotate(Graphics2D g2) {
        g2.setTransform(this.original_at);
    }

}
