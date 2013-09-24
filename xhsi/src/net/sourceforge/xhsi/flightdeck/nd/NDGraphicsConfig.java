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
package net.sourceforge.xhsi.flightdeck.nd;

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


public class NDGraphicsConfig extends GraphicsConfig implements ComponentListener {

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    public static int INITIAL_EXPANDED_PLANE_Y_OFFSET = 100;
    public static int INITIAL_CENTER_BOTTOM = 55;

    public int left_label_x;
    public int left_label_arpt_y;
    public int left_label_wpt_y;
    public int left_label_vor_y;
    public int left_label_ndb_y;
    public int left_label_pos_y;
    public int left_label_tfc_y;
    public int left_label_data_y;
    public int left_label_taonly_y;
    public int left_label_xpdr_y;
    public int right_label_x;
    public int right_label_tcas_y;
    public int right_label_disagree_y;

    public int rose_radius;
    public int rose_thickness;
    public int rose_y_offset;
    public int plane_y_offset;
    public int map_center_x;
    public int map_center_y;

    public float pixels_per_nm;

    public int pixel_distance_plane_bottom_screen;
    public int pixel_distance_plane_lower_left_corner;
    public float half_view_angle;
    public int big_tick_length;
    public int small_tick_length;
    public Area inner_rose_area;

    private int map_mode;
    private int map_submode;

    public boolean mode_app;
    public boolean mode_vor;
    public boolean hdg_up;
    public boolean mode_classic_hsi;
    public boolean mode_map;
    public boolean mode_fullmap;
    public boolean mode_centered;
    public boolean mode_plan;
    public boolean trk_up;
    public int map_range;
    public boolean map_zoomin;

    public float max_range;


    public NDGraphicsConfig(Component root_component, int du) {
        super(root_component);
        this.display_unit = du;
        init();
    }


//    public void init() {
//
//        super.init();
//
//    }


    public void update_config(Graphics2D g2, int mode, int submode, int range, boolean zoomin, boolean power) {

        if (this.resized
                || this.reconfig
                || (this.map_mode != mode)
                || (this.map_submode != submode)
                || (this.map_range != range)
                || (this.map_zoomin != zoomin)
                || (this.powered != power)
            ) {
            // one of the settings has been changed

            // remember the avionics power settings
            this.powered = power;
//logger.warning("ND update_config");
            super.update_config(g2);

            // remember the mode/submode/range settings
            this.map_mode = mode;
            this.map_submode = submode;
            this.map_range = range;
            this.map_zoomin = zoomin;

            // set some booleans for easy checking
            if ( preferences.get_airbus_modes() ) {
                // limit to Airbus display modes
                mode_plan = (submode == Avionics.EFIS_MAP_PLN);
                mode_app = (submode == Avionics.EFIS_MAP_APP);
                mode_vor = (submode == Avionics.EFIS_MAP_VOR);
                mode_classic_hsi = ( (mode_app || mode_vor) && preferences.get_classic_hsi() );
                //mode_classic_hsi = false;
                mode_map = ( (submode == Avionics.EFIS_MAP_MAP) || (submode == Avionics.EFIS_MAP_NAV) || ! preferences.get_classic_hsi() );
                //mode_map = true;
                mode_fullmap = preferences.get_appvor_fullmap() || (submode == Avionics.EFIS_MAP_MAP) || (submode == Avionics.EFIS_MAP_NAV) || mode_plan;
                mode_centered = ( mode_app || mode_vor || (submode == Avionics.EFIS_MAP_MAP) || (submode == Avionics.EFIS_MAP_PLN) );
            } else {
                mode_plan = (submode == Avionics.EFIS_MAP_PLN);
                mode_app = (submode == Avionics.EFIS_MAP_APP);
                mode_vor = (submode == Avionics.EFIS_MAP_VOR);
                mode_classic_hsi = ( (mode_app || mode_vor) && (mode == Avionics.EFIS_MAP_CENTERED) && preferences.get_classic_hsi() );
                // mode_map = ( ( (submode == Avionics.EFIS_MAP_APP) || (submode == Avionics.EFIS_MAP_VOR) || (submode == Avionics.EFIS_MAP_MAP) || (submode == Avionics.EFIS_MAP_NAV) ) && ! mode_classic_hsi);
                mode_map = ( ! mode_classic_hsi ) && ( ! mode_plan );
                mode_fullmap = ( ( ( (submode == Avionics.EFIS_MAP_MAP) || (submode == Avionics.EFIS_MAP_NAV) ) && ! mode_classic_hsi) || mode_plan );
                if ( mode_map && preferences.get_appvor_fullmap() ) {
                    // APP and VOR are normally not fullmap, this is an override
                    mode_fullmap = true;
                }
                // mode_centered = ( ( (mode_app || mode_vor) && (mode == Avionics.EFIS_MAP_CENTERED) ) || ( (submode == Avionics.EFIS_MAP_MAP) && (mode == Avionics.EFIS_MAP_CENTERED) ) || (submode == Avionics.EFIS_MAP_PLN) );
                mode_centered = ( (mode == Avionics.EFIS_MAP_CENTERED) || (submode == Avionics.EFIS_MAP_PLN) );
                // for NAV submode, invert the centered/expanded
                if ( submode == Avionics.EFIS_MAP_NAV ) mode_centered = ! mode_centered;
            }
            hdg_up = mode_app || mode_vor;
            trk_up = ! ( hdg_up || mode_plan );


            // position of the plane and size of the rose
            this.map_center_x = this.frame_size.width / 2;
            this.rose_y_offset = 50 + 4 + this.border_top;
            //if ( ( (this.map_mode == Avionics.EFIS_MAP_CENTERED) && (this.map_submode != Avionics.EFIS_MAP_NAV) ) || (this.map_submode == Avionics.EFIS_MAP_PLN) ) {
            if ( this.mode_centered || this.mode_plan ) {
                // CENTERED (or PLAN)
                this.max_range = (float)this.map_range / 2.0f;
                this.map_center_y = (this.rose_y_offset + (this.frame_size.height  - this.border_bottom - INITIAL_CENTER_BOTTOM)) / 2;
                this.rose_radius = this.map_center_y - this.rose_y_offset;
                this.plane_y_offset = INITIAL_CENTER_BOTTOM + this.rose_radius;
            } else {
                // EXPANDED
                this.max_range = (float)this.map_range;
                this.plane_y_offset = (int) (INITIAL_EXPANDED_PLANE_Y_OFFSET * this.scaling_factor);
                this.map_center_y = this.frame_size.height - this.border_bottom - this.plane_y_offset;
                this.rose_radius = this.map_center_y - this.rose_y_offset;
            }
            this.pixels_per_nm = (float)this.rose_radius / this.max_range; // float for better precision
            if ( zoomin ) this.pixels_per_nm *= 100.0f;


            // labels at the left
            this.left_label_x = this.border_left + 10;
            //this.left_label_arpt_y = Math.max( this.frame_size.height - this.border_bottom - 240, this.frame_size.height/2 + 24 );
            this.left_label_arpt_y = this.frame_size.height - this.border_bottom - 240;
            this.left_label_wpt_y = this.left_label_arpt_y + this.line_height_small + 3;
            this.left_label_vor_y = this.left_label_wpt_y + this.line_height_small + 3;
            this.left_label_ndb_y = this.left_label_vor_y + this.line_height_small + 3;
            this.left_label_pos_y = this.left_label_ndb_y + this.line_height_small + 3;
            this.left_label_data_y = this.left_label_pos_y + this.line_height_small + 3;
            this.left_label_tfc_y = this.left_label_data_y + this.line_height_small + 3;
            this.left_label_taonly_y = this.left_label_tfc_y + this.line_height_tiny + 2;
            this.left_label_xpdr_y = this.left_label_taonly_y + this.line_height_tiny + 2;

            // labels at the right
            this.right_label_x = this.frame_size.width - this.border_right - 20;
            this.right_label_tcas_y = this.frame_size.height * 7 / 16;
            this.right_label_disagree_y = this.frame_size.height / 3;

            // calculate pixel distances. Needed for determining which
            // part of the rose needs to be drawn
            pixel_distance_plane_bottom_screen = this.frame_size.height - this.map_center_y ;
            pixel_distance_plane_lower_left_corner =
                    (int) Math.sqrt(
                    Math.pow(this.pixel_distance_plane_bottom_screen, 2) +
                    Math.pow(this.frame_size.width / 2, 2));

            
            // compass rose ticks get shorter when the frame is smaller than 600px
            this.big_tick_length = (int) (20 * shrink_scaling_factor);
            this.small_tick_length = this.big_tick_length / 3;

            if (this.pixel_distance_plane_bottom_screen >= (this.rose_radius - this.big_tick_length)) {
                // Complete rose
                this.half_view_angle = 180.0f;
            } else if (this.pixel_distance_plane_lower_left_corner > (this.rose_radius - this.big_tick_length)) {
                // Rose visible below aircraft position
                half_view_angle = (float) (180.0f - Math.toDegrees(Math.acos((1.0f * pixel_distance_plane_bottom_screen) / (1.0f * (this.rose_radius - this.big_tick_length)))));
            } else {
                // Rose visible only above aircraft position
                half_view_angle = (float) (90.0f - Math.toDegrees(Math.acos((1.0f * this.frame_size.width) / (2.0f * (this.rose_radius - this.big_tick_length)))));
            }

            rose_thickness = 1;
            this.inner_rose_area = new Area(new Ellipse2D.Float(
                    map_center_x - rose_radius + rose_thickness,
                    map_center_y - rose_radius + rose_thickness,
                    (rose_radius * 2) - (rose_thickness * 2),
                    (rose_radius * 2) - (rose_thickness * 2)));


            // clear the flags
            this.resized = false;
            this.reconfig = false;
            // some subcomponents need to be reminded to redraw imediately
            this.reconfigured = true;

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
