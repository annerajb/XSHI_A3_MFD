/**
* CDUFixHolding.java
* 
* Draws a holding pattern or a CDU Fix
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

import java.awt.BasicStroke;
//import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.util.logging.Logger;

import net.sourceforge.xhsi.XHSISettings;

//import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.FMSEntry;
import net.sourceforge.xhsi.model.CoordinateSystem;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.NavigationObject;
import net.sourceforge.xhsi.model.NavigationObjectRepository;

//import net.sourceforge.xhsi.panel.GraphicsConfig;
//import net.sourceforge.xhsi.panel.Subcomponent;



public class CDUFixHolding extends NDSubcomponent {

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    private static final long serialVersionUID = 1L;

    private static final float HOLDING_HELPER = 15.0f;

    float map_up;
    NavigationObjectRepository nor;
    float center_lat;
    float center_lon;
    float pixels_per_deg_lon;
    float pixels_per_deg_lat;
    float pixels_per_nm;
    float lat_max;
    float lat_min;
    float lon_max;
    float lon_min;

    private XHSISettings xhsi_settings;


    public CDUFixHolding(ModelFactory model_factory, NDGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
        this.nor = NavigationObjectRepository.get_instance();
        xhsi_settings = XHSISettings.get_instance();
    }


    public void paint(Graphics2D g2) {

        if ( nd_gc.powered && ( xhsi_settings.draw_holding || xhsi_settings.draw_cdu_fix ) && ! nd_gc.mode_classic_hsi ) {

            this.center_lat = this.aircraft.lat();
            this.center_lon = this.aircraft.lon();

            if ( nd_gc.mode_plan ) {
                if ( this.fms.is_active() ) {
                    FMSEntry entry = (FMSEntry) this.fms.get_displayed_waypoint();
                    if ( entry == null ) {
                        entry = (FMSEntry) this.fms.get_active_waypoint();
                    }
                    // center of plan mode
                    this.center_lat = entry.lat;
                    this.center_lon = entry.lon;
                }
            }

            this.pixels_per_nm = (float)nd_gc.rose_radius / nd_gc.max_range; // float for better precision

            // determine max and min lat/lon in viewport to only draw those
            // elements that can be displayed
            float delta_lat = nd_gc.max_range * CoordinateSystem.deg_lat_per_nm();
            float delta_lon = nd_gc.max_range * CoordinateSystem.deg_lon_per_nm(this.center_lat);
            lat_max = this.center_lat + delta_lat;
            lat_min = this.center_lat - delta_lat;
            lon_max = this.center_lon + delta_lon;
            lon_min = this.center_lon - delta_lon;

            // pixels per degree
            this.pixels_per_deg_lat = nd_gc.rose_radius / delta_lat;
            this.pixels_per_deg_lon = nd_gc.rose_radius / delta_lon;

            // 100 times bigger?
            if ( nd_gc.map_zoomin ) {
                this.pixels_per_nm *= 100.0f;
                this.pixels_per_deg_lat *= 100.0f;
                this.pixels_per_deg_lon *= 100.0f;
            }

            if ( nd_gc.hdg_up ) {
                // HDG UP
                map_up = this.aircraft.heading() - this.aircraft.magnetic_variation();
            } else if ( nd_gc.trk_up ) {
                // TRK UP
                map_up = this.aircraft.track() - this.aircraft.magnetic_variation();
            } else {
                // North UP
                map_up = 0.0f;
            }

            // draw the holding
            if ( nd_gc.powered && xhsi_settings.draw_holding && ! nd_gc.mode_classic_hsi )
                drawHolding(g2);

            // draw the CDU fix
            if ( nd_gc.powered && xhsi_settings.draw_cdu_fix && ! nd_gc.mode_classic_hsi )
                drawCDUFix(g2);

        }

    }


    private void drawHolding(Graphics2D g2) {

        NavigationObject holding_fix = null;

        AffineTransform original_at = g2.getTransform();
        AffineTransform rotate_to_heading = AffineTransform.getRotateInstance(
                Math.toRadians(-1.0f * map_up),
                nd_gc.map_center_x,
                nd_gc.map_center_y);
        g2.transform(rotate_to_heading);


        // search for the holding fix in the current lat/lon cell first
        holding_fix = this.nor.get_navobj(xhsi_settings.holding_fix, this.center_lat, this.center_lon);
        // if not found in the current cell, search the cells that are in the map range
        if ( holding_fix == null ) {
            for (int lat=(int)lat_min; lat<=(int)lat_max; lat++) {
                for (int lon=(int)lon_min; lon<=(int)lon_max; lon++) {
                    if (xhsi_settings.draw_holding && ( holding_fix == null) ) {
                        holding_fix = this.nor.get_navobj(xhsi_settings.holding_fix, (float)lat, (float)lon);
                    }
                }
            }
        }

        // draw the holding pattern
        if ( holding_fix != null ) {

            Stroke original_stroke = g2.getStroke();
            g2.setColor(nd_gc.holding_color);
            //g2.setStroke(new BasicStroke(1.0f*nd_gc.grow_scaling_factor, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int holding_x = lon_to_x(holding_fix.lon);
            int holding_y = lat_to_y(holding_fix.lat);

// a displaced holding fix is not correctly placed on the map (at least for now)
// and anyway, it is probably not very useful
//            if ( xhsi_settings.holding_distance > 0.0f ) {
//                float radial = (float)Math.toRadians(xhsi_settings.holding_radial);
//                float distance = xhsi_settings.holding_distance * this.pixels_per_nm;
//                holding_x += distance * Math.sin(radial);
//                holding_y -= distance * Math.cos(radial);
//            }

            g2.rotate(Math.toRadians((float)xhsi_settings.holding_track - this.aircraft.magnetic_variation()), holding_x, holding_y);

            float turn_speed = 180.0f / xhsi_settings.holding_legduration / 60.0f; // turn speed in deg/s
            //long  pixels_per_nm = nd_gc.rose_radius / nd_gc.map_range;
            float tas = aircraft.true_air_speed();
            float turn_radius = Math.abs(tas / (turn_speed * 20.0f * (float)Math.PI)); // turn radius in nm
            // int map_range = nd_gc.map_range;
            float turn_radius_pixels = turn_radius * this.pixels_per_nm;
            int turn_diameter = (int)Math.round(turn_radius_pixels * 2.0f);
            int leg_pixels = (int)(tas * xhsi_settings.holding_legduration / 60.0f * this.pixels_per_nm);
            int entry_x = (int)Math.round(0.9397 * HOLDING_HELPER * nd_gc.grow_scaling_factor);
            int entry_y = (int)Math.round(0.3420 * HOLDING_HELPER * nd_gc.grow_scaling_factor);
            int entry_l = (int)Math.round(HOLDING_HELPER * nd_gc.grow_scaling_factor * 2.0f);
            if ( ! xhsi_settings.holding_nonstandard ) {
                // standard right turns
                g2.draw(new Arc2D.Float(
                        (float)holding_x,
                        (float)holding_y - turn_radius_pixels,
                        turn_radius_pixels * 2.0f,
                        turn_radius_pixels * 2.0f,
                        0.0f,
                        180.0f,
                        Arc2D.OPEN));
                g2.drawLine(holding_x, holding_y - entry_l, holding_x, holding_y + leg_pixels);
                g2.drawLine(holding_x + turn_diameter, holding_y, holding_x + turn_diameter, holding_y + leg_pixels);
                g2.draw(new Arc2D.Float(
                        (float)holding_x,
                        (float)holding_y + (float)leg_pixels - turn_radius_pixels,
                        turn_radius_pixels * 2.0f,
                        turn_radius_pixels * 2.0f,
                        180.0f,
                        180.0f,
                        Arc2D.OPEN));
                g2.drawLine(holding_x - entry_x, holding_y - entry_y, holding_x + entry_x, holding_y + entry_y);
            } else {
                // left turns
                g2.draw(new Arc2D.Float(
                        (float)holding_x - turn_radius_pixels * 2.0f,
                        (float)holding_y - turn_radius_pixels,
                        turn_radius_pixels * 2.0f,
                        turn_radius_pixels * 2.0f,
                        0.0f,
                        180.0f,
                        Arc2D.OPEN));
                g2.drawLine(holding_x, holding_y - entry_l, holding_x, holding_y + leg_pixels);
                g2.drawLine(holding_x - turn_diameter, holding_y, holding_x - turn_diameter, holding_y + leg_pixels);
                g2.draw(new Arc2D.Float(
                        (float)holding_x - turn_radius_pixels * 2.0f,
                        (float)holding_y + (float)leg_pixels - turn_radius_pixels,
                        turn_radius_pixels * 2.0f,
                        turn_radius_pixels * 2.0f,
                        180.0f,
                        180.0f,
                        Arc2D.OPEN));
                g2.drawLine(holding_x - entry_x, holding_y + entry_y, holding_x + entry_x, holding_y - entry_y);
            }

            g2.setStroke(original_stroke);
            g2.setTransform(original_at);
            g2.transform(rotate_to_heading);

        }

        g2.setTransform(original_at);

    }


    private void drawCDUFix(Graphics2D g2) {

        NavigationObject cdu_fix = null;

        AffineTransform original_at = g2.getTransform();
        AffineTransform rotate_to_heading = AffineTransform.getRotateInstance(
                Math.toRadians(-1.0f * map_up),
                nd_gc.map_center_x,
                nd_gc.map_center_y);
        g2.transform(rotate_to_heading);


        // search for the cdu fix in the current lat/lon cell first
        cdu_fix = this.nor.get_navobj(xhsi_settings.cdu_fix, this.center_lat, this.center_lon);
        // if not found in the current cell, search the cells that are in the map map_range
        if ( cdu_fix == null ) {
            for (int lat=(int)lat_min; lat<=(int) lat_max; lat++) {
                for (int lon=(int)lon_min; lon<=(int)lon_max; lon++) {
                    if (xhsi_settings.draw_cdu_fix && ( cdu_fix == null) ) {
                        cdu_fix = this.nor.get_navobj(xhsi_settings.cdu_fix, (float)lat, (float)lon);
                    }
                }
            }
        }

        // draw the cdu fix
        if ( cdu_fix != null ) {

            float longdashes[] = { 16.0f, 6.0f };
            Stroke original_stroke = g2.getStroke();
            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, longdashes, 0.0f));
            g2.setColor(nd_gc.wpt_color);

            int cdu_x = lon_to_x(cdu_fix.lon);
            int cdu_y = lat_to_y(cdu_fix.lat);

            g2.rotate(Math.toRadians((float)xhsi_settings.cdu_fix_radial - this.aircraft.magnetic_variation()), cdu_x, cdu_y);

            // int map_range = nd_gc.map_range;
            int cdu_fix_radius = Math.round(xhsi_settings.cdu_fix_dist * this.pixels_per_nm);

            g2.drawLine(cdu_x, cdu_y, cdu_x, cdu_y - 3*cdu_fix_radius);
            g2.drawOval(cdu_x - cdu_fix_radius, cdu_y - cdu_fix_radius,  2*cdu_fix_radius, 2*cdu_fix_radius);

            g2.setStroke(original_stroke);
            g2.setTransform(original_at);
            g2.transform(rotate_to_heading);

        }

        g2.setTransform(original_at);

    }


    private int lon_to_x(float lon) {
        return Math.round(nd_gc.map_center_x + ((lon - this.center_lon)*pixels_per_deg_lon));
    }


    private int lat_to_y(float lat) {
        return Math.round(nd_gc.map_center_y + ((this.center_lat - lat)*pixels_per_deg_lat));
    }


}
