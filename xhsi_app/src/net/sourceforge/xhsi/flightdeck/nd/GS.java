/**
* GS.java
* 
* Renders a Glideslope Deviation Indicator at the right of the screen
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import net.sourceforge.xhsi.XHSISettings;

import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ModelFactory;
//import net.sourceforge.xhsi.model.NavigationRadio;

//import net.sourceforge.xhsi.panel.GraphicsConfig;
//import net.sourceforge.xhsi.panel.Subcomponent;



public class GS extends NDSubcomponent {

    private static final long serialVersionUID = 1L;

    BufferedImage cdi_box_buf_image;

//    private class CDIBoxInfo {
//        public int cdi_x;
//        public int cdi_y;
//        public String dme;
//        public String radial;
//        public Color color;
//        public boolean draw_arrow;
//        public float frequency;
//        public RadioNavigationObject rnav_object;
//        public Localizer loc_object;
//        public boolean receiving;
//        public boolean is_adf;
//        public int distance_by_10;        // compare only the first fraction point
//
//        public CDIBoxInfo(NavigationRadio radio) {
//
//            draw_arrow = true;
//
//            if (radio != null) {
//                this.rnav_object = radio.get_radio_nav_object();
//                this.frequency = radio.get_frequency();
//                this.distance_by_10 = 0;
//
//                this.is_adf = radio.freq_is_adf();
//
//                this.receiving = radio.receiving();
//                this.radial = "";
//
//                if (this.receiving == false) {
//                    // no reception, display the labels in gray
//                    this.color = Color.GRAY;
//                    if (radio.freq_is_nav()) {
//                        this.type ="NAV " + radio.get_bank();
//                        this.id = GS.nav_freq_formatter.format(radio.get_frequency());
//                        this.dme = "---";
//                    } else if (radio.freq_is_adf()) {
//                        this.type ="ADF " + radio.get_bank();
//                        this.id = GS.adf_freq_formatter.format(radio.get_frequency());
//                    this.dme = "";
//                    }
//                } else {
//                    // we are receiving a signal; set the text of the label and its color
//                    if (rnav_object instanceof RadioNavBeacon) {
//                        if (((RadioNavBeacon) rnav_object).type == RadioNavBeacon.TYPE_NDB) {
//                            this.type = "ADF " + radio.get_bank();
//                            this.color = nd_gc.tuned_ndb_color;
//                        } else if (((RadioNavBeacon) rnav_object).type == RadioNavBeacon.TYPE_VOR) {
//                            this.type = "VOR " + radio.get_bank();
//                            this.color = nd_gc.tuned_vor_color;
//                            this.radial = "R " + Math.round(radio.get_radial());
//                            // Too confusing to add the TO/FROM indicator here
//                            //if (radio.get_fromto() == NavigationRadio.VOR_RECEPTION_TO)
//                            //    this.radial += "   TO";
//                            //else
//                            //    this.radial += "  FROM";
//                        } else if (((RadioNavBeacon) rnav_object).type == RadioNavBeacon.TYPE_STANDALONE_DME) {
//                            this.type = "DME " + radio.get_bank();
//                            this.color = nd_gc.tuned_vor_color;
//                            this.draw_arrow = false;
//                        } else {
//                            this.type = "ERR " + radio.get_bank();
//                            this.color = Color.RED;
//                        }
//                        this.id = rnav_object.ilt;
//                    } else if (rnav_object instanceof Localizer) {
//                        loc_object = (Localizer) rnav_object;
//                        if ( loc_object.has_gs )
//                                this.type = "ILS " + radio.get_bank();
//                        else
//                                this.type = "LOC " + radio.get_bank();
//                        this.color = nd_gc.receiving_localizer_color;
//                        this.id = rnav_object.ilt;
//                        this.draw_arrow = false;
//                    } else {
//                        this.type = "ERR " + radio.get_bank();
//                        this.id = "";
//                        this.dme = "";
//                        this.color = Color.RED;
//                    }
//
//                    if ( radio.freq_is_adf() ) {
//                        this.dme = "";
//                    } else {
//                        float dme_distance = radio.get_distance();
//                        if (dme_distance != 0) {
//                            if (dme_distance > 99) {
//                                this.dme = GS.far_dme_formatter.format(dme_distance);
//                            } else {
//                                this.dme = GS.near_dme_formatter.format(dme_distance);
//                            }
//                            this.distance_by_10 = (int) (dme_distance * 10);
//                        } else {
//                            this.dme = "---";
//                            this.distance_by_10 = 0;
//                        }
//                    }
//                }
//            } else {
//                this.type = "";
//                this.id = "";
//                this.dme = "";
//                this.rnav_object = null;
//                this.frequency = 0;
//                this.receiving = false;
//            }
//        }

//        public boolean equals(NavigationRadio radio) {
//            // return true if nothing has changed
//            if (radio != null) {
//                return ((this.rnav_object == radio.get_radio_nav_object()) &&
//                        (this.frequency == radio.get_frequency()) &&
//                        (this.receiving == radio.receiving()) &&
//                        (this.distance_by_10 == ((int) (radio.get_distance() * 10)))
//                        );
//            } else {
//                return ((this.rnav_object == null) &&
//                        (this.frequency == 0.0f) &&
//                        (this.receiving == false));
//            }
//        }
//    }


    public GS(ModelFactory model_factory, NDGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
    }

    public void paint(Graphics2D g2) {
        if ( nd_gc.powered && nd_gc.mode_app ) {
            drawGS(g2);
        }
    }


    public void drawGS(Graphics2D g2) {

        int bar_l = Math.round(10.0f * nd_gc.grow_scaling_factor);
        int diamond_w = nd_gc.mode_centered ? Math.round(6.0f * nd_gc.grow_scaling_factor) : Math.round(5.0f * nd_gc.grow_scaling_factor); // half-width
        int diamond_h = nd_gc.mode_centered ? Math.round(11.0f * nd_gc.grow_scaling_factor) : Math.round(9.0f * nd_gc.grow_scaling_factor); // half-height

        int dot_r = Math.round(3.0f * nd_gc.grow_scaling_factor);
        int dot_dist = nd_gc.mode_centered ? nd_gc.rose_radius/5 : Math.round(35.0f * nd_gc.grow_scaling_factor);
        dot_dist = Math.round(35.0f * nd_gc.grow_scaling_factor);
        int cdi_box_height = dot_dist * 7; // just long enough
        int cdi_box_y = nd_gc.mode_centered ? nd_gc.map_center_y - cdi_box_height/2 : nd_gc.map_center_y - nd_gc.rose_radius/4 - cdi_box_height/2;
        int cdi_box_width = bar_l*2 + 8*2;
        int cdi_box_x = nd_gc.frame_size.width - nd_gc.border_right - cdi_box_width;

        int source = this.avionics.hsi_source();
        boolean gs_active = false;
        float gs_value = 0.0f;

        //NavigationRadio navradio;

        if (source == Avionics.HSI_SOURCE_GPS) {
            gs_active = this.avionics.gps_gs_active();
            gs_value = this.avionics.gps_vdef_dot();
        } else if (source == Avionics.HSI_SOURCE_NAV1) {
            gs_active = this.avionics.nav1_gs_active();
            gs_value = this.avionics.nav1_vdef_dot();
            //navradio = this.avionics.get_selected_radio(1);
        } else if(source == Avionics.HSI_SOURCE_NAV2) {
            gs_active = this.avionics.nav2_gs_active();
            gs_value = this.avionics.nav2_vdef_dot();
            //navradio = this.avionics.get_selected_radio(2);
        }

        int cdi_pixels = Math.round(gs_value * (float)dot_dist);

        this.cdi_box_buf_image = create_buffered_image(cdi_box_width, cdi_box_height);
        Graphics2D gImg = get_graphics(this.cdi_box_buf_image);

        int cdi_x = cdi_box_width/2;
        int cdi_y = cdi_box_height/2;
        gImg.setBackground(nd_gc.background_color);
        gImg.clearRect(0, 0, cdi_box_width, cdi_box_height);

        int diamond_x[] = { cdi_x-diamond_w, cdi_x, cdi_x+diamond_w, cdi_x };
        int diamond_y[] = { cdi_y+cdi_pixels, cdi_y+cdi_pixels+diamond_h, cdi_y+cdi_pixels, cdi_y+cdi_pixels-diamond_h };
        if (gs_active) {
            gImg.setColor(nd_gc.nav_needle_color);
            if (Math.abs(gs_value) < 2.49f) {
                gImg.drawPolygon(diamond_x, diamond_y, 4);
                gImg.fillPolygon(diamond_x, diamond_y, 4);
            } else {
                gImg.drawPolygon(diamond_x, diamond_y, 4);
            }
        }
        gImg.setColor(nd_gc.deviation_scale_color);
        gImg.drawLine(cdi_x-bar_l, cdi_y, cdi_x+bar_l, cdi_y);
        gImg.drawOval(cdi_x-dot_r, cdi_y-dot_dist-dot_r, 2*dot_r, 2*dot_r);
        gImg.drawOval(cdi_x-dot_r, cdi_y+dot_dist-dot_r, 2*dot_r, 2*dot_r);
        gImg.drawOval(cdi_x-dot_r, cdi_y-2*dot_dist-dot_r, 2*dot_r, 2*dot_r);
        gImg.drawOval(cdi_x-dot_r, cdi_y+2*dot_dist-dot_r, 2*dot_r, 2*dot_r);

        gImg.dispose();

        g2.drawImage(cdi_box_buf_image, cdi_box_x, cdi_box_y, null);

    }


}
