/**
* AltiTape.java
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

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
//import java.awt.Color;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
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

import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.Localizer;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.NavigationRadio;
import net.sourceforge.xhsi.model.RadioNavigationObject;

//import net.sourceforge.xhsi.panel.GraphicsConfig;
//import net.sourceforge.xhsi.panel.Subcomponent;



public class AltiTape extends PFDSubcomponent {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");


    public AltiTape(ModelFactory model_factory, PFDGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
    }


    public void paint(Graphics2D g2) {
        if ( pfd_gc.powered ) {
            drawTape(g2);
        }
    }


    private void drawTape(Graphics2D g2) {

        pfd_gc.setTransparent(g2, this.preferences.get_draw_colorgradient_horizon());
        g2.setColor(pfd_gc.instrument_background_color);
        g2.fillRect(pfd_gc.altitape_left - 1, pfd_gc.tape_top - 1, pfd_gc.tape_width + 2, pfd_gc.tape_height + 2);
        pfd_gc.setOpaque(g2);

        Shape original_clipshape = g2.getClip();
        // left and right don't matter...
        g2.clipRect(pfd_gc.altitape_left - pfd_gc.tape_width, pfd_gc.tape_top, pfd_gc.tape_width*3, pfd_gc.tape_height);

        g2.setColor(pfd_gc.markings_color);
        g2.setFont(pfd_gc.font_xxl);

        // Altitude scale
        float alt = this.aircraft.altitude_ind();
//alt = 39660;
//float utc_time = this.aircraft.sim_time_zulu();
//alt = (utc_time) % 10000 -5300;


        // Landing altitude
        boolean loc_receive = false;
        NavigationRadio nav_radio;
        RadioNavigationObject nav_object;
        int dest_alt = 0;

        int source = this.avionics.hsi_source();
        if (source == Avionics.HSI_SOURCE_NAV1) {
            nav_radio = this.avionics.get_nav_radio(1);
            if ( nav_radio.receiving() ) {
                nav_object = nav_radio.get_radio_nav_object();
                if (nav_object instanceof Localizer) {
                    loc_receive = true;
                    dest_alt = nav_object.elevation;
                }
            }
        } else if(source == Avionics.HSI_SOURCE_NAV2) {
            nav_radio = this.avionics.get_nav_radio(2);
            if ( nav_radio.receiving() ) {
                nav_object = nav_radio.get_radio_nav_object();
                if (nav_object instanceof Localizer) {
                    loc_receive = true;
                    dest_alt = nav_object.elevation;
                }
            }
        }
        int gnd_y;
        if ( loc_receive ) {
            int loc_y = pfd_gc.adi_cy - Math.round( ((float)dest_alt - alt) * pfd_gc.tape_height / 800.0f );
            int h1000_y = pfd_gc.adi_cy - Math.round( ((float)dest_alt + (float)1000 - alt) * pfd_gc.tape_height / 800.0f );
            int h500_y = pfd_gc.adi_cy - Math.round( ((float)dest_alt + (float)500 - alt) * pfd_gc.tape_height / 800.0f );
            // between 500 and 1000ft
            g2.setColor(pfd_gc.markings_color);
            g2.drawLine(pfd_gc.altitape_left - 4, h1000_y, pfd_gc.altitape_left - 4, h500_y);
            // between 500 and 1000ft
            g2.setColor(pfd_gc.caution_color);
            g2.drawLine(pfd_gc.altitape_left - 4, h500_y, pfd_gc.altitape_left - 4, loc_y);
            // localizer altitude
            gnd_y = loc_y;
        } else {
            // ground elevation from the radar altitude
            gnd_y = pfd_gc.adi_cy + Math.round( (this.aircraft.agl_m() * 3.28084f) * pfd_gc.tape_height / 800.0f );
        }
        g2.setColor(pfd_gc.caution_color);
        g2.drawLine(pfd_gc.altitape_left - 4, gnd_y, pfd_gc.altitape_left + pfd_gc.tape_width - 2, gnd_y);
        g2.drawLine(pfd_gc.altitape_left, gnd_y + 1 + (pfd_gc.tape_width-2)/6, pfd_gc.altitape_left + (pfd_gc.tape_width-2)*1/6, gnd_y + (pfd_gc.tape_width-2)/3);
        for (int i=0; i<5; i++) {
            g2.drawLine(pfd_gc.altitape_left + (pfd_gc.tape_width-2)*i/6, gnd_y + 1, pfd_gc.altitape_left + (pfd_gc.tape_width-2)*(i+2)/6, gnd_y + (pfd_gc.tape_width-2)/3);
        }
        g2.drawLine(pfd_gc.altitape_left + (pfd_gc.tape_width-2)*5/6, gnd_y + 1, pfd_gc.altitape_left + (pfd_gc.tape_width-2), gnd_y + 1 + (pfd_gc.tape_width-2)/6);


        // scale markings
        // round to nearest multiple of 100
        int alt100 = Math.round(alt / 100.0f) * 100;
        // From there, go 400ft up and down
        for (int alt_mark = alt100 - 400; alt_mark <= alt100 + 400; alt_mark += 100) {

            int alt_y = pfd_gc.adi_cy - Math.round( ((float)alt_mark - alt) * pfd_gc.tape_height / 800.0f );
            g2.setColor(pfd_gc.dim_markings_color);
            g2.drawLine(pfd_gc.altitape_left, alt_y, pfd_gc.altitape_left + pfd_gc.tape_width*1/8, alt_y);

            if (alt_mark % 200 == 0) {
                g2.setFont(pfd_gc.font_m);
                if (alt_mark % 1000 == 0) {
                    g2.setColor(pfd_gc.markings_color);
                    g2.drawLine(pfd_gc.altitape_left - pfd_gc.tape_width*1/16, alt_y, pfd_gc.altitape_left + pfd_gc.tape_width*1/8, alt_y);
                    g2.drawLine(pfd_gc.altitape_left + pfd_gc.tape_width*1/8, alt_y - pfd_gc.line_height_m*2/3, pfd_gc.altitape_left + pfd_gc.tape_width - 2, alt_y - pfd_gc.line_height_m*2/3);
                    g2.drawLine(pfd_gc.altitape_left + pfd_gc.tape_width*1/8, alt_y + pfd_gc.line_height_m*2/3, pfd_gc.altitape_left + pfd_gc.tape_width - 2, alt_y + pfd_gc.line_height_m*2/3);
                }
                if ( ( alt_mark >= 1000 ) || ( alt_mark <= -1000 ) ) {
                    String mark_str = "" + (alt_mark / 1000);
                    g2.drawString(mark_str, pfd_gc.altitape_left + pfd_gc.tape_width*31/32 - pfd_gc.get_text_width(g2, pfd_gc.font_m, mark_str) - 3*pfd_gc.digit_width_s, alt_y + pfd_gc.line_height_m/2 - 2);
                }
                g2.setFont(pfd_gc.font_s);
                if ( ( alt_mark < 0 ) && ( alt_mark > -1000 ) ) {
                    g2.drawString("-", pfd_gc.altitape_left + pfd_gc.tape_width*31/32 - 4*pfd_gc.digit_width_s, alt_y + pfd_gc.line_height_m/2 - 2);
                }
                g2.drawString("" + Math.abs((alt_mark/100)%10), pfd_gc.altitape_left + pfd_gc.tape_width*31/32 - 3*pfd_gc.digit_width_s, alt_y + pfd_gc.line_height_m/2 - 2);
                g2.drawString("00", pfd_gc.altitape_left + pfd_gc.tape_width*31/32 - 2*pfd_gc.digit_width_s, alt_y + pfd_gc.line_height_m/2 - 2);
            }

        }


        // DA arrow
        if ( this.aircraft.mins_is_baro() ) {

            int da_bug = this.aircraft.da_bug();
            if ( da_bug > 0 ) {
                float da = (float)da_bug;
                int da_y = pfd_gc.adi_cy - Math.round( (da - alt) * pfd_gc.tape_height / 800.0f );
                if ( ( alt > da ) || this.aircraft.on_ground() ) {
                    g2.setColor(pfd_gc.color_lime);
                } else {
                    g2.setColor(pfd_gc.color_amber);
                }
                g2.drawLine(pfd_gc.altitape_left - 2, da_y, pfd_gc.altitape_left + pfd_gc.tape_width - 1, da_y);
                int[] da_triangle_x = {
                    pfd_gc.altitape_left - 1,
                    pfd_gc.altitape_left - 1 - pfd_gc.tape_width*5/20,
                    pfd_gc.altitape_left - 1 - pfd_gc.tape_width*5/20
                };
                int[] da_triangle_y = {
                    da_y,
                    da_y + pfd_gc.tape_width*6/20,
                    da_y - pfd_gc.tape_width*6/20
                };
                g2.drawPolygon(da_triangle_x, da_triangle_y, 3);
            }

        }


        // AP Alt bug
        int alt_y = pfd_gc.adi_cy - Math.round( (this.avionics.autopilot_altitude() - alt) * pfd_gc.tape_height / 800.0f );
        if ( alt_y < pfd_gc.tape_top ) {
            alt_y = pfd_gc.tape_top;
        } else if ( alt_y > pfd_gc.tape_top + pfd_gc.tape_height ) {
            alt_y = pfd_gc.tape_top + pfd_gc.tape_height;
        }
        int[] bug_x = {
            pfd_gc.altitape_left,
            pfd_gc.altitape_left - pfd_gc.tape_width*1/8,
            pfd_gc.altitape_left - pfd_gc.tape_width*1/8,
            pfd_gc.altitape_left + pfd_gc.tape_width*1/8 + pfd_gc.tape_width*3/16,
            pfd_gc.altitape_left + pfd_gc.tape_width*1/8 + pfd_gc.tape_width*3/16,
            pfd_gc.altitape_left - pfd_gc.tape_width*1/8,
            pfd_gc.altitape_left - pfd_gc.tape_width*1/8
        };
        int[] bug_y = {
            alt_y,
            alt_y + pfd_gc.tape_width*3/20,
            alt_y + pfd_gc.line_height_xxl,
            alt_y + pfd_gc.line_height_xxl,
            alt_y - pfd_gc.line_height_xxl,
            alt_y - pfd_gc.line_height_xxl,
            alt_y - pfd_gc.tape_width*3/20
        };
        g2.setColor(pfd_gc.heading_bug_color);
        g2.drawPolygon(bug_x, bug_y, 7);

//        // a small bug with the _current_ AP Alt
//        alt_y = pfd_gc.adi_cy - Math.round( (this.avionics.autopilot_current_altitude() - alt) * pfd_gc.tape_height / 800.0f );
//        if ( alt_y < pfd_gc.tape_top ) {
//            alt_y = pfd_gc.tape_top;
//        } else if ( alt_y > pfd_gc.tape_top + pfd_gc.tape_height ) {
//            alt_y = pfd_gc.tape_top + pfd_gc.tape_height;
//        }
//        int[] cur_bug_x = {
//            pfd_gc.altitape_left - pfd_gc.tape_width*1/16,
//            pfd_gc.altitape_left,
//            pfd_gc.altitape_left - pfd_gc.tape_width*1/16
//        };
//        int[] cur_bug_y = {
//            alt_y - pfd_gc.tape_width*3/40,
//            alt_y,
//            alt_y + pfd_gc.tape_width*3/40
//        };
//        g2.drawPolyline(cur_bug_x, cur_bug_y, 3);

        g2.setClip(original_clipshape);


        // AP ALT preselect
        DecimalFormat feet_format = new DecimalFormat("000");
        int ap_alt = Math.round(this.avionics.autopilot_altitude());
//ap_alt=41000;
        g2.setColor(pfd_gc.heading_bug_color);
        g2.setFont(pfd_gc.font_l);
        String alt_str = feet_format.format(ap_alt % 1000);
        int alt_str_x = pfd_gc.altitape_left + pfd_gc.tape_width - pfd_gc.tape_width*1/16 - 3*pfd_gc.digit_width_l;
        int alt_str_y = pfd_gc.tape_top - pfd_gc.tape_width/6;
        int alt_str_w = pfd_gc.get_text_width(g2, pfd_gc.font_l, alt_str);
        g2.clearRect(alt_str_x - pfd_gc.digit_width_l/3, alt_str_y - pfd_gc.line_height_xl*7/8, alt_str_w + pfd_gc.digit_width_l*2/3, pfd_gc.line_height_xl);
        g2.drawString(alt_str, alt_str_x, alt_str_y);
        //ap_alt = Math.round(this.avionics.autopilot_altitude()) / 1000;
        int ap1000 = ap_alt / 1000;
        if ( ap1000 > 0 ) {
            int i = ap1000 >= 10 ? 2 : 1;
            g2.setFont(pfd_gc.font_xl);
            alt_str = "" + ap1000;
            alt_str_w = pfd_gc.get_text_width(g2, pfd_gc.font_xl, alt_str);
            alt_str_x -=  alt_str_w;
            g2.clearRect(alt_str_x - pfd_gc.digit_width_xl/3, alt_str_y - pfd_gc.line_height_xl*7/8, alt_str_w + pfd_gc.digit_width_xl/3, pfd_gc.line_height_xl);
            g2.drawString(alt_str, alt_str_x, alt_str_y);
        }


        // QNH setting
        int qnh = this.aircraft.qnh();
        float alt_inhg = this.aircraft.altimeter_in_hg();
        boolean std = ( Math.round(alt_inhg * 100.0f) == 2992 );
        String qnh_str;
        if ( std ) {
            qnh_str = "STD";
        } else {
            qnh_str = "" + qnh;
        }
        g2.setColor(pfd_gc.color_lime);
        g2.setFont(pfd_gc.font_xl);
        g2.drawString(qnh_str, pfd_gc.altitape_left + 4*pfd_gc.digit_width_xl - pfd_gc.get_text_width(g2, pfd_gc.font_xl, qnh_str), pfd_gc.tape_top + pfd_gc.tape_height + pfd_gc.line_height_xl*9/8);
        if ( ! std ) {
            g2.setFont(pfd_gc.font_l);
            g2.drawString(" HPA", pfd_gc.altitape_left + 4*pfd_gc.digit_width_xl, pfd_gc.tape_top + pfd_gc.tape_height + pfd_gc.line_height_xl*9/8);
            DecimalFormat inhg_format = new DecimalFormat("00.00");
            DecimalFormatSymbols format_symbols = inhg_format.getDecimalFormatSymbols();
            format_symbols.setDecimalSeparator('.');
            inhg_format.setDecimalFormatSymbols(format_symbols);
            String inhg_str = inhg_format.format(alt_inhg);
            g2.setFont(pfd_gc.font_m);
            g2.drawString(inhg_str, pfd_gc.altitape_left + 4*pfd_gc.digit_width_xl - pfd_gc.get_text_width(g2, pfd_gc.font_m, inhg_str), pfd_gc.tape_top + pfd_gc.tape_height + pfd_gc.line_height_xl*9/8 + pfd_gc.line_height_m);
            g2.setFont(pfd_gc.font_s);
            g2.drawString(" IN", pfd_gc.altitape_left + 4*pfd_gc.digit_width_xl, pfd_gc.tape_top + pfd_gc.tape_height + pfd_gc.line_height_xl*9/8 + pfd_gc.line_height_m);
        }


        // alt readout
        int[] box_x = {
            pfd_gc.altitape_left + pfd_gc.tape_width*1/8,
            pfd_gc.altitape_left + pfd_gc.tape_width*1/8 + pfd_gc.tape_width*3/16,
            pfd_gc.altitape_left + pfd_gc.tape_width*1/8 + pfd_gc.tape_width*3/16,
            pfd_gc.altitape_left + pfd_gc.tape_width + pfd_gc.tape_width*35/80,
            pfd_gc.altitape_left + pfd_gc.tape_width + pfd_gc.tape_width*35/80,
            pfd_gc.altitape_left + pfd_gc.tape_width*1/8 + pfd_gc.tape_width*3/16,
            pfd_gc.altitape_left + pfd_gc.tape_width*1/8 + pfd_gc.tape_width*3/16,
        };
        int[] box_y = {
            pfd_gc.adi_cy,
            pfd_gc.adi_cy + pfd_gc.tape_width*3/20,
            pfd_gc.adi_cy + pfd_gc.line_height_xxl,
            pfd_gc.adi_cy + pfd_gc.line_height_xxl,
            pfd_gc.adi_cy - pfd_gc.line_height_xxl,
            pfd_gc.adi_cy - pfd_gc.line_height_xxl,
            pfd_gc.adi_cy - pfd_gc.tape_width*3/20
        };
//Composite oricomp = g2.getComposite();
//g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        g2.setColor(pfd_gc.background_color);
        g2.fillPolygon(box_x, box_y, 7);
//g2.setComposite(oricomp);
        g2.setColor(pfd_gc.markings_color);
        g2.drawPolygon(box_x, box_y, 7);

        g2.clipRect(pfd_gc.altitape_left, pfd_gc.adi_cy - pfd_gc.line_height_xxl, pfd_gc.tape_width*2, 2 * pfd_gc.line_height_xxl);

        if ( alt >= 0.0f ) {

//            int alt_int = alt.intValue();
            int alt_int = (int)alt;
            int alt_20 = alt_int / 20 * 20;
            float alt_frac = (alt - (float)alt_20) / 20.0f;
            int alt_100 = (alt_int / 100) % 10;
            int alt_1k = (alt_int / 1000) % 10;
            int alt_10k = (alt_int / 10000) % 10;

            int x10k = pfd_gc.altitape_left + pfd_gc.tape_width*1/8 + pfd_gc.tape_width*3/16 + 4;
            int x1k = x10k + pfd_gc.digit_width_xxl;
            int x100 = x1k + pfd_gc.digit_width_xxl;
            int x20 = x100 + pfd_gc.digit_width_l;
            int ydelta = Math.round( pfd_gc.line_height_l*alt_frac );

            DecimalFormat decaform = new DecimalFormat("00");
            g2.setFont(pfd_gc.font_l);
            g2.drawString(decaform.format( (alt_20 + 40) % 100 ), x20, pfd_gc.adi_cy + pfd_gc.line_height_l/2 - 2 + ydelta - pfd_gc.line_height_l*2);
            g2.drawString(decaform.format( (alt_20 + 20) % 100 ), x20, pfd_gc.adi_cy + pfd_gc.line_height_l/2 - 2 + ydelta - pfd_gc.line_height_l);
            g2.drawString(decaform.format( alt_20 % 100 ), x20, pfd_gc.adi_cy + pfd_gc.line_height_l/2 - 2 + ydelta);
            if (alt_20 == 0) {
                g2.drawString(decaform.format( (alt_20 + 20) % 100 ), x20, pfd_gc.adi_cy + pfd_gc.line_height_l/2 - 2 + ydelta + pfd_gc.line_height_l);
            } else {
                g2.drawString(decaform.format( (alt_20 - 20) % 100 ), x20, pfd_gc.adi_cy + pfd_gc.line_height_l/2 - 2 + ydelta + pfd_gc.line_height_l);
            }

            alt_20 %= 100;

            // hundreds
            g2.setFont(pfd_gc.font_l);
            if ( alt_20 == 80 ) {
                ydelta = Math.round( pfd_gc.line_height_l*alt_frac );
                g2.drawString("" + alt_100, x100, pfd_gc.adi_cy + pfd_gc.line_height_l/2 - 3 + ydelta);
                g2.drawString("" + (alt_100 + 1) % 10, x100, pfd_gc.adi_cy + pfd_gc.line_height_l/2 - 3 + ydelta - pfd_gc.line_height_l);
            } else {
                g2.drawString("" + alt_100, x100, pfd_gc.adi_cy + pfd_gc.line_height_l/2 - 3);
            }

            // thousands
            g2.setFont(pfd_gc.font_xxl);
            if ( ( alt_100 == 9 ) && ( alt_20 == 80 ) ) {
                ydelta = Math.round( pfd_gc.line_height_xxl*alt_frac );
                g2.drawString("" + alt_1k, x1k, pfd_gc.adi_cy + pfd_gc.line_height_xxl/2 - 4 + ydelta);
                g2.drawString("" + (alt_1k + 1) % 10, x1k, pfd_gc.adi_cy + pfd_gc.line_height_xxl/2 - 4 + ydelta - pfd_gc.line_height_xxl);
            } else {
                g2.drawString("" + alt_1k, x1k, pfd_gc.adi_cy + pfd_gc.line_height_xxl/2 - 4);
            }

            // ten-thousands
            if ( ( alt_1k == 9 ) && ( alt_100 == 9 ) && ( alt_20 == 80 ) ) {
                // already done: ydelta = Math.round( pfd_gc.line_height_xxl*alt_frac );
                if ( alt_10k == 0) {
                    g2.setColor(pfd_gc.heading_labels_color.darker());
                    g2.fillRoundRect(x10k + pfd_gc.digit_width_xxl/8, pfd_gc.adi_cy + pfd_gc.line_height_xxl/2 - pfd_gc.line_height_xxl*3/4 - 4 + ydelta, pfd_gc.digit_width_xxl*3/4, pfd_gc.line_height_xxl*3/4, (int)(8.0f*pfd_gc.scaling_factor), (int)(8.0f*pfd_gc.scaling_factor));
                    g2.setColor(pfd_gc.markings_color);
                } else {
                    g2.drawString("" + alt_10k, x10k, pfd_gc.adi_cy + pfd_gc.line_height_xxl/2 - 4 + ydelta);
                }
                g2.drawString("" + (alt_10k + 1) % 10, x10k, pfd_gc.adi_cy + pfd_gc.line_height_xxl/2 - 4 + ydelta - pfd_gc.line_height_xxl);
            } else {
                if ( alt_10k == 0) {
                    g2.setColor(pfd_gc.heading_labels_color.darker());
                    g2.fillRoundRect(x10k + pfd_gc.digit_width_xxl/8, pfd_gc.adi_cy + pfd_gc.line_height_xxl/2 - pfd_gc.line_height_xxl*3/4 - 4, pfd_gc.digit_width_xxl*3/4, pfd_gc.line_height_xxl*3/4, (int)(8.0f*pfd_gc.scaling_factor), (int)(8.0f*pfd_gc.scaling_factor));
                    g2.setColor(pfd_gc.markings_color);
                } else {
                    g2.drawString("" + alt_10k, x10k, pfd_gc.adi_cy + pfd_gc.line_height_xxl/2 - 4);
                }
            }

        } else {

            // the same for negative altitudes, except that the vertical positions have to be reversed

//            int alt_int = -alt.intValue();
            int alt_int = - (int)alt;
            int alt_20 = alt_int / 20 * 20;
            float alt_frac = (-alt - (float)alt_20) / 20.0f;
            int alt_100 = (alt_int / 100) % 10;
            int alt_1k = (alt_int / 1000) % 10;
            int alt_10k = (alt_int / 10000) % 10;

            int x10k = pfd_gc.altitape_left + pfd_gc.tape_width*1/8 + pfd_gc.tape_width*3/16 + 4;
            int x1k = x10k + pfd_gc.digit_width_xxl;
            int x100 = x1k + pfd_gc.digit_width_xxl;
            int x20 = x100 + pfd_gc.digit_width_xl;
            int ydelta = Math.round( pfd_gc.line_height_l*alt_frac );

            DecimalFormat decaform = new DecimalFormat("00");
            g2.setFont(pfd_gc.font_l);
            g2.drawString(decaform.format( (alt_20 + 40) % 100 ), x20, pfd_gc.adi_cy + pfd_gc.line_height_l/2 - 2 - ydelta + pfd_gc.line_height_l*2);
            g2.drawString(decaform.format( (alt_20 + 20) % 100 ), x20, pfd_gc.adi_cy + pfd_gc.line_height_l/2 - 2 - ydelta + pfd_gc.line_height_l);
            g2.drawString(decaform.format( alt_20 % 100 ), x20, pfd_gc.adi_cy + pfd_gc.line_height_l/2 - 2 - ydelta);
            if (alt_20 == 0) {
                g2.drawString(decaform.format( (alt_20 + 20) % 100 ), x20, pfd_gc.adi_cy + pfd_gc.line_height_l/2 - 2 - ydelta - pfd_gc.line_height_l);
            } else {
                g2.drawString(decaform.format( (alt_20 - 20) % 100 ), x20, pfd_gc.adi_cy + pfd_gc.line_height_l/2 - 2 - ydelta - pfd_gc.line_height_l);
            }

            alt_20 %= 100;

            g2.setFont(pfd_gc.font_xl);
            if ( alt_20 == 80 ) {
                ydelta = Math.round( pfd_gc.line_height_xl*alt_frac );
                g2.drawString("" + alt_100, x100, pfd_gc.adi_cy + pfd_gc.line_height_xl/2 - 3 - ydelta);
                g2.drawString("" + (alt_100 + 1) % 10, x100, pfd_gc.adi_cy + pfd_gc.line_height_xl/2 - 3 - ydelta + pfd_gc.line_height_xl);
            } else {
                g2.drawString("" + alt_100, x100, pfd_gc.adi_cy + pfd_gc.line_height_xl/2 - 3);
            }

            g2.setFont(pfd_gc.font_xxl);
            if ( ( alt_100 == 9 ) && ( alt_20 == 80 ) ) {
                ydelta = Math.round( pfd_gc.line_height_xxl*alt_frac );
                g2.drawString("" + alt_1k, x1k, pfd_gc.adi_cy + pfd_gc.line_height_xxl/2 - 4 - ydelta);
                g2.drawString("" + (alt_1k + 1) % 10, x1k, pfd_gc.adi_cy + pfd_gc.line_height_xxl/2 - 4 - ydelta + pfd_gc.line_height_xxl);
            } else {
                g2.drawString("" + alt_1k, x1k, pfd_gc.adi_cy + pfd_gc.line_height_xxl/2 - 4);
            }

            if ( ( alt_1k == 9 ) && ( alt_100 == 9 ) && ( alt_20 == 80 ) ) {
                // already done: ydelta = Math.round( pfd_gc.line_height_xxl*alt_frac );
                if ( alt_10k == 0) {
                    g2.setColor(pfd_gc.heading_labels_color);
                    g2.drawString("\u25CF", x10k, pfd_gc.adi_cy + pfd_gc.line_height_xxl/2 - 4 - ydelta);
                    g2.setColor(pfd_gc.markings_color);
                } else {
                    g2.drawString("" + alt_10k, x10k, pfd_gc.adi_cy + pfd_gc.line_height_xxl/2 - 4 - ydelta);
                }
                g2.drawString("" + (alt_10k + 1) % 10, x10k, pfd_gc.adi_cy + pfd_gc.line_height_xxl/2 - 4 - ydelta + pfd_gc.line_height_xxl);
            } else {
                if ( alt_10k == 0) {
                    g2.drawString("-", x10k, pfd_gc.adi_cy + pfd_gc.line_height_xxl/2 - 4);
                } else {
                    g2.drawString("" + alt_10k, x10k, pfd_gc.adi_cy + pfd_gc.line_height_xxl/2 - 4);
                }
            }

        }

        g2.setClip(original_clipshape);

    }


}
