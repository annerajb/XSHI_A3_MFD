/**
* RefSourceLabel.java
* 
* Displays the source name for the HSI at the top, to the right of the current heading - NAV1, NAV2 or GPS
* 
* Copyright (C) 2009-2011  Marc Rogiers (marrog.123@gmail.com)
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
import java.text.DecimalFormat;

import net.sourceforge.xhsi.XHSIPreferences;

import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.Localizer;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.NavigationRadio;
import net.sourceforge.xhsi.model.RadioNavBeacon;
import net.sourceforge.xhsi.model.RadioNavigationObject;

//import net.sourceforge.xhsi.panel.GraphicsConfig;
//import net.sourceforge.xhsi.panel.Subcomponent;



public class RefSourceLabel extends NDSubcomponent {

    private static final long serialVersionUID = 1L;

    private static final int TYPE_NAV = 0;
    private static final int TYPE_VOR = 1;
    private static final int TYPE_DME = 2;
    private static final int TYPE_ILS = 3;
    private static final int TYPE_LOC = 4;
    private static final int TYPE_FMC = 5;
    private static final int TYPE_ERR = 6;
    private String type_list[] = { "NAV", "VOR", "DME", "ILS", "LOC", "FMC", "ERR" };

    private Color label_color;


    public RefSourceLabel(ModelFactory model_factory, NDGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
    }


    private int nav_radio_type(int bank) {

        label_color = nd_gc.tuned_vor_color;
        int type = TYPE_NAV;
        NavigationRadio radio = this.avionics.get_nav_radio(bank);
        RadioNavigationObject rnav_object;
        if (radio != null) {
            rnav_object = radio.get_radio_nav_object();
            if (radio.receiving()) {
                if (rnav_object instanceof RadioNavBeacon) {
                    if (((RadioNavBeacon) rnav_object).type == RadioNavBeacon.TYPE_VOR) {
                        if (((RadioNavBeacon) rnav_object).has_dme) {
                            this.label_color = nd_gc.tuned_vor_color;
                            type = TYPE_VOR;
                        }
                    } else if (((RadioNavBeacon) rnav_object).type == RadioNavBeacon.TYPE_STANDALONE_DME) {
                        type = TYPE_DME;
                    } else {
                        type = TYPE_ERR;
                    }
                } else if (rnav_object instanceof Localizer) {
                    this.label_color = nd_gc.tuned_localizer_color;
                    if ( ((Localizer) rnav_object).has_gs )
                            type = TYPE_ILS;
                    else
                            type = TYPE_LOC;
                }
            }
        }

        return type;

    }


    public void paint(Graphics2D g2) {

        if ( nd_gc.powered
//                && ( ( this.avionics.map_submode() == Avionics.EFIS_MAP_APP ) || ( this.avionics.map_submode() == Avionics.EFIS_MAP_VOR ) )
                && ( nd_gc.panel_rect.width >= 450 ) ) {

            DecimalFormat degrees_formatter = new DecimalFormat("000");

            String source_label;
            String crs_label1 = "";
            String crs_text1 = "";
            String crs_label2 = "";
            String crs_text2 = "";
            //Color crs_color;
            int src_type = TYPE_ERR;

            // mode detail in APP and VOR modes
            boolean detailed = ( ( this.avionics.map_submode() == Avionics.EFIS_MAP_APP ) || ( this.avionics.map_submode() == Avionics.EFIS_MAP_VOR ) );

            int source = this.avionics.hsi_source();

            if ( detailed ) {

                if ( source == Avionics.HSI_SOURCE_NAV1 ) {
                    src_type = nav_radio_type(1);
                    source_label = type_list[ src_type ] + " 1";
                    crs_label1 = "CRS";
                    crs_text1 = degrees_formatter.format( Math.round(this.avionics.nav1_obs()) );
                    crs_label2 = "F/C";
                    crs_text2 = degrees_formatter.format( Math.round(this.avionics.nav1_course()) );
                    //crs_color = nd_gc.vor_course_color;
                } else if ( source == Avionics.HSI_SOURCE_NAV2 ) {
                    src_type = nav_radio_type(2);
                    source_label = type_list[ src_type ] + " 2";
                    crs_label1 = "CRS";
                    crs_text1 = degrees_formatter.format( Math.round(this.avionics.nav2_obs()) );
                    crs_label2 = "F/C";
                    crs_text2 = degrees_formatter.format( Math.round(this.avionics.nav2_course()) );
                    //crs_color = nd_gc.vor_course_color;
                } else if ( source == Avionics.HSI_SOURCE_GPS ) {
                    src_type = TYPE_FMC;
                    this.label_color = nd_gc.fmc_active_color;
                    source_label = type_list[ src_type ];
                    crs_label1 = "CRS";
                    crs_text1 = degrees_formatter.format( Math.round(this.avionics.gps_course()) );
                    //crs_color = nd_gc.fmc_active_color;
                } else {
                    src_type = TYPE_ERR;
                    this.label_color = nd_gc.warning_color;
                    source_label = type_list[ src_type ];
                    //crs_color = nd_gc.warning_color;
                }

            } else {

                if ( source == Avionics.HSI_SOURCE_NAV1 ) {
                    this.label_color = nd_gc.tuned_vor_color;
                    source_label = "NAV 1";
                } else if ( source == Avionics.HSI_SOURCE_NAV2 ) {
                    this.label_color = nd_gc.tuned_vor_color;
                    source_label = "NAV 2";
                } else if ( source == Avionics.HSI_SOURCE_GPS ) {
                    this.label_color = nd_gc.fmc_active_color;
                    source_label = "FMC";
                } else {
                    this.label_color = nd_gc.warning_color;
                    source_label = "ERR";
                }

            }

            int text_width = nd_gc.get_text_width(g2, nd_gc.font_medium, "XXX 9  ");
//            int source_label_x = (nd_gc.map_center_x + nd_gc.frame_size.width - nd_gc.border_right - text_width ) / 2 - 4;
//            int source_label_x = nd_gc.frame_size.width - nd_gc.border_right - (nd_gc.max_char_advance_medium * 5) - text_width;
            int source_label_x = Math.min(
                    nd_gc.frame_size.width - nd_gc.border_right - (nd_gc.max_char_advance_medium * 5) - text_width,
                    nd_gc.map_center_x + nd_gc.panel_rect.width/4
                    );
            int source_label_y = nd_gc.border_top + nd_gc.line_height_medium + 2;

            g2.setColor(this.label_color);
            g2.setFont(nd_gc.font_medium);
            g2.drawString(source_label, source_label_x, source_label_y);

            if ( detailed ) {

                source_label_y += nd_gc.line_height_small + 1;
                int crs_text_x = source_label_x + nd_gc.get_text_width(g2, nd_gc.font_small, crs_label1);
                //g2.setColor(crs_color);
                g2.setColor(nd_gc.top_text_color);
                if ( ( ! this.preferences.get_auto_frontcourse() ) && ( src_type == TYPE_ILS ) || ( src_type == TYPE_LOC ) ) {
                    g2.setFont(nd_gc.font_tiny);
                    g2.drawString(crs_label2, source_label_x, source_label_y);
                    g2.setFont(nd_gc.font_small);
                    g2.drawString(crs_text2, crs_text_x, source_label_y);
                    source_label_y += nd_gc.line_height_small + 1;
                }
                g2.setFont(nd_gc.font_tiny);
                g2.drawString(crs_label1, source_label_x, source_label_y);
                g2.setFont(nd_gc.font_small);
                g2.drawString(crs_text1, crs_text_x, source_label_y);

            }

        }
        
    }


}
