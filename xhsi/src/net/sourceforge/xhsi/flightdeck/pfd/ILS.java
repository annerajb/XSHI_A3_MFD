/**
* ILS.java
* 
* ILS CDI, GS and label on the PFD
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

//import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

//import net.sourceforge.xhsi.XHSIPreferences;
//import net.sourceforge.xhsi.XHSISettings;

import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.Localizer;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.NavigationRadio;
import net.sourceforge.xhsi.model.RadioNavigationObject;

//import net.sourceforge.xhsi.panel.GraphicsConfig;
//import net.sourceforge.xhsi.panel.Subcomponent;



public class ILS extends PFDSubcomponent {

    private static final long serialVersionUID = 1L;


    public ILS(ModelFactory model_factory, PFDGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
    }

    public void paint(Graphics2D g2) {
        if ( pfd_gc.powered ) {
            drawILS(g2);
        }
    }


    public void drawILS(Graphics2D g2) {

        int diamond_w = Math.round(7.0f * pfd_gc.scaling_factor); // half-width
        int diamond_h = Math.round(12.5f * pfd_gc.scaling_factor); // half-height
        int dot_r = Math.round(4.0f * pfd_gc.scaling_factor);

        boolean nav_receive = false;
        boolean nav1_receive = false;
        boolean nav2_receive = false;
        float cdi_value = 0.0f;
        float cdi1_value = 0.0f;
        float cdi2_value = 0.0f;
        NavigationRadio nav_radio;
        String nav_id = "";
        String nav1_id = "";
        String nav2_id = "";
        RadioNavigationObject nav_object;
        String nav_type = "";
        String nav1_type = "";
        String nav2_type = "";
        boolean gs_active = false;
        boolean gs1_active = false;
        boolean gs2_active = false;
        float gs_value = 0.0f;
        float gs1_value = 0.0f;
        float gs2_value = 0.0f;
        int obs = 999;
        int obs1 = 999;
        int obs2 = 999;
        int crs = 999999;
        int crs1 = 999999;
        int crs2 = 999999;
        float dme = 999.999f;
        float dme1 = 999.999f;
        float dme2 = 999.999f;
        boolean mismatch = false;

        nav_radio = this.avionics.get_nav_radio(1);
        if ( nav_radio.receiving() ) {
            nav_object = nav_radio.get_radio_nav_object();
            if (nav_object instanceof Localizer) {
                nav1_receive = true;
                nav1_id = nav_radio.get_nav_id() + "/";
                cdi1_value = this.avionics.nav1_hdef_dot();
                obs1 = Math.round( Math.round(this.avionics.nav1_obs()) );
                crs1 = Math.round( Math.round(this.avionics.nav1_course()) );
                if ( ((Localizer) nav_object).has_gs ) {
                    nav1_type = "ILS 1";
                    gs1_active = this.avionics.nav1_gs_active();
                    gs1_value = this.avionics.nav1_vdef_dot();
                } else {
                    nav1_type = "LOC 1";
                }
//                if ( ((Localizer) nav_object).has_dme ) {
                    dme1 = nav_radio.get_distance();
//                }
            }
        }

        nav_radio = this.avionics.get_nav_radio(2);
        if ( nav_radio.receiving() ) {
            nav_object = nav_radio.get_radio_nav_object();
            if (nav_object instanceof Localizer) {
                nav2_receive = true;
                nav2_id = nav_radio.get_nav_id() + "/";
                cdi2_value = this.avionics.nav2_hdef_dot();
                obs2 = Math.round( Math.round(this.avionics.nav2_obs()) );
                crs2 = Math.round( Math.round(this.avionics.nav2_course()) );
                if ( ((Localizer) nav_object).has_gs ) {
                    nav2_type = "ILS 2";
                    gs2_active = this.avionics.nav2_gs_active();
                    gs2_value = this.avionics.nav2_vdef_dot();
                } else {
                    nav2_type = "LOC 2";
                }
//                if ( ((Localizer) nav_object).has_dme ) {
                    dme2 = nav_radio.get_distance();
//                }
            }
        }

        int source = this.avionics.hsi_source();

        if (source == Avionics.HSI_SOURCE_NAV1) {
            if ( nav1_receive ) {
                nav_receive = true;
                nav_id = nav1_id;
                cdi_value = cdi1_value;
                obs = obs1;
                crs = crs1;
                nav_type = nav1_type;
                gs_active = gs1_active;
                gs_value = gs1_value;
                dme = dme1;
            } else if ( nav2_receive ) {
                mismatch = true;
                nav_type = nav2_type;
            }
        } else if (source == Avionics.HSI_SOURCE_NAV2) {
            if ( nav2_receive ) {
                nav_receive = true;
                nav_id = nav2_id;
                cdi_value = cdi2_value;
                obs = obs2;
                crs = crs2;
                nav_type = nav2_type;
                gs_active = gs2_active;
                gs_value = gs2_value;
                dme = dme2;
            } else if ( nav1_receive ) {
                mismatch = true;
                nav_type = nav1_type;
            }
        } else /* if (source == Avionics.HSI_SOURCE_GPS) */ {
            if ( nav1_receive ) {
                mismatch = true;
                nav_type = nav1_type;
            } else if ( nav2_receive ) {
                mismatch = true;
                nav_type = nav2_type;
            }
        }


//mismatch = false;
//nav_receive = true;
//nav_type = "ILS 0";
//cdi_value = 2.45f;
//crs = 271;
//obs = 269;

        // we are receiving a LOC or ILS, but not on the selected source
        if ( mismatch ) {

            g2.setFont(pfd_gc.font_l);
            int ref_h = pfd_gc.line_height_l;
            int ref_x = pfd_gc.adi_cx - pfd_gc.adi_size_left*7/8;
            int ref_y = pfd_gc.adi_cy - pfd_gc.adi_size_up - 1*ref_h;
            g2.setColor(pfd_gc.caution_color);
            g2.drawString(nav_type, ref_x, ref_y);

        }


        // Localizer
        if ( nav_receive ) {

            // Approach reference label
            DecimalFormat degrees_formatter = new DecimalFormat("000");
            DecimalFormat dme_formatter = new DecimalFormat("0.0");
            DecimalFormatSymbols format_symbols = dme_formatter.getDecimalFormatSymbols();
            format_symbols.setDecimalSeparator('.');
            dme_formatter.setDecimalFormatSymbols(format_symbols);

            g2.setFont(pfd_gc.font_m);
            int ref_h_m = pfd_gc.line_height_m;
            int ref_h_l = pfd_gc.line_height_l;
            int ref_x = pfd_gc.adi_cx - pfd_gc.adi_size_left*7/8;
            int ref_y = pfd_gc.adi_cy - pfd_gc.adi_size_up - 2*ref_h_m - ref_h_l;
            // id + obs
            g2.setColor(pfd_gc.markings_color);
            g2.drawString(nav_id, ref_x, ref_y);
            int ref_x1 = ref_x + pfd_gc.get_text_width(g2, pfd_gc.font_m, nav_id);
            String crs_str = degrees_formatter.format( obs ) + "\u00B0  ";
            g2.drawString(crs_str, ref_x1, ref_y);
            if ( crs != obs ) {
                g2.setColor(pfd_gc.caution_color);
                int ref_x2 = ref_x1 + pfd_gc.get_text_width(g2, pfd_gc.font_m, crs_str);
                g2.setFont(pfd_gc.font_s);
                g2.drawString("F/C", ref_x2, ref_y);
                int ref_x3 = ref_x2 + pfd_gc.get_text_width(g2, pfd_gc.font_m, "F/C");
                g2.setFont(pfd_gc.font_m);
                g2.drawString(degrees_formatter.format( crs ) + "\u00B0", ref_x3, ref_y);
                g2.setColor(pfd_gc.markings_color);
            }
            // DME
            ref_y += ref_h_m;
            if ( ( dme == 0.0f ) || ( dme >= 99.0f ) ) {
                g2.drawString("DME ---", ref_x, ref_y);
            } else {
                g2.drawString("DME " + dme_formatter.format( dme ), ref_x, ref_y);
            }
            // Type
            ref_y += ref_h_l;
            g2.setFont(pfd_gc.font_l);
            g2.drawString(nav_type, ref_x, ref_y);


            // CDI
            int dot_dist = pfd_gc.cdi_width*2/13;
            int cdi_pixels = Math.round(cdi_value * (float)dot_dist);

            int cdi_x = pfd_gc.adi_cx;
            int cdi_y = pfd_gc.adi_cy + pfd_gc.adi_size_down + pfd_gc.cdi_height/2;

            if ( this.preferences.get_draw_colorgradient_horizon() ) {
                pfd_gc.setTransparent(g2, true);
                g2.setColor(pfd_gc.instrument_background_color);
                g2.fillRect(pfd_gc.adi_cx - pfd_gc.cdi_width/2, pfd_gc.adi_cy + pfd_gc.adi_size_down, pfd_gc.cdi_width, pfd_gc.cdi_height);
                pfd_gc.setOpaque(g2);
            }

            int diamond_x[] = { cdi_x + cdi_pixels, cdi_x + cdi_pixels + diamond_h, cdi_x + cdi_pixels, cdi_x + cdi_pixels - diamond_h };
            int diamond_y[] = { cdi_y - diamond_w, cdi_y, cdi_y + diamond_w, cdi_y };

            g2.setColor(pfd_gc.nav_needle_color);
            if (Math.abs(cdi_value) < 2.49f) {
                g2.drawPolygon(diamond_x, diamond_y, 4);
                g2.fillPolygon(diamond_x, diamond_y, 4);
            } else {
                g2.drawPolygon(diamond_x, diamond_y, 4);
            }

            g2.setColor(pfd_gc.markings_color);
            g2.drawLine(pfd_gc.adi_cx, pfd_gc.adi_cy + pfd_gc.adi_size_down + 1, pfd_gc.adi_cx, pfd_gc.adi_cy + pfd_gc.adi_size_down + pfd_gc.cdi_height - 1);
            g2.drawOval(cdi_x - dot_dist - dot_r, cdi_y - dot_r, 2*dot_r, 2*dot_r);
            g2.drawOval(cdi_x + dot_dist - dot_r, cdi_y - dot_r, 2*dot_r, 2*dot_r);
            g2.drawOval(cdi_x - 2*dot_dist - dot_r, cdi_y - dot_r, 2*dot_r, 2*dot_r);
            g2.drawOval(cdi_x + 2*dot_dist - dot_r, cdi_y - dot_r, 2*dot_r, 2*dot_r);


        }


        // Glideslope
        if ( nav_receive && gs_active ) {

            int dot_dist = pfd_gc.gs_height*2/13;
            int gs_pixels = Math.round(gs_value * (float)dot_dist);

            int gs_x = pfd_gc.adi_cx + pfd_gc.adi_size_right + pfd_gc.gs_width/2;
            int gs_y = pfd_gc.adi_cy;

            if ( this.preferences.get_draw_colorgradient_horizon() ) {
                pfd_gc.setTransparent(g2, true);
                g2.setColor(pfd_gc.instrument_background_color);
                g2.fillRect(pfd_gc.adi_cx + pfd_gc.adi_size_right, pfd_gc.adi_cy - pfd_gc.gs_height/2, pfd_gc.gs_width, pfd_gc.gs_height);
                pfd_gc.setOpaque(g2);
            }

            int diamond_x[] = { gs_x - diamond_w, gs_x, gs_x + diamond_w, gs_x };
            int diamond_y[] = { gs_y + gs_pixels, gs_y + gs_pixels + diamond_h, gs_y + gs_pixels, gs_y + gs_pixels - diamond_h };

            g2.setColor(pfd_gc.nav_needle_color);
            if (Math.abs(gs_value) < 2.49f) {
                g2.drawPolygon(diamond_x, diamond_y, 4);
                g2.fillPolygon(diamond_x, diamond_y, 4);
            } else {
                g2.drawPolygon(diamond_x, diamond_y, 4);
            }

            g2.setColor(pfd_gc.markings_color);
            g2.drawLine(pfd_gc.adi_cx + pfd_gc.adi_size_right + 1, gs_y, pfd_gc.adi_cx + pfd_gc.adi_size_right + pfd_gc.gs_width - 1, gs_y);
            g2.drawOval(gs_x - dot_r, gs_y - dot_dist - dot_r, 2*dot_r, 2*dot_r);
            g2.drawOval(gs_x - dot_r, gs_y + dot_dist - dot_r, 2*dot_r, 2*dot_r);
            g2.drawOval(gs_x - dot_r, gs_y - 2*dot_dist - dot_r, 2*dot_r, 2*dot_r);
            g2.drawOval(gs_x - dot_r, gs_y + 2*dot_dist - dot_r, 2*dot_r, 2*dot_r);

        }


    }


}
