/**
* PositionTrendVector.java
* 
* Renders the position trend vector indicating the estimated flight path of
* the aircraft in 30, 60 and 90 seconds depending on the chosen map range.
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

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;

import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ModelFactory;

//import net.sourceforge.xhsi.panel.GraphicsConfig;
//import net.sourceforge.xhsi.panel.Subcomponent;

import net.sourceforge.xhsi.util.RunningAverager;



public class PositionTrendVector extends NDSubcomponent {

    private static final long serialVersionUID = 1L;
    // running average over 20 frames
    RunningAverager turn_speed_averager = new RunningAverager(20);


    public PositionTrendVector(ModelFactory model_factory, NDGraphicsConfig hsi_gc) {
        super(model_factory, hsi_gc);
    }


    public void paint(Graphics2D g2) {

        // update a running average, even when not drawn
        float turn_speed = turn_speed_averager.running_average(aircraft.turn_speed()); // turn speed in deg/s

        if ( ! nd_gc.map_zoomin && nd_gc.powered && ! ( nd_gc.mode_plan || nd_gc.mode_classic_hsi ) ) {
            // not in PLAN, APP CTR, VOR CTR

            AffineTransform original_at = g2.getTransform();

            if ( ! nd_gc.mode_map ) {
                AffineTransform rotate = AffineTransform.getRotateInstance(
                    Math.toRadians(-this.aircraft.drift()),
                    nd_gc.map_center_x,
                    nd_gc.map_center_y
                );
                g2.transform(rotate);
            }

            long  pixels_per_nm = (long)( (float)nd_gc.rose_radius / nd_gc.max_range );
            float ground_speed = aircraft.ground_speed();
            float turn_radius = turn_radius(turn_speed, ground_speed); // turn radius in nm
            int map_range = nd_gc.map_range;

            if (map_range > 20) {
                // first segment : 30sec
                draw_position_trend_vector_segment(g2, turn_radius, turn_speed, pixels_per_nm, 7.5f, 30.0f);
                // second segment : 60sec
                draw_position_trend_vector_segment(g2, turn_radius, turn_speed, pixels_per_nm, 37.5f, 60.0f);
                // third segment : 90sec
                draw_position_trend_vector_segment(g2, turn_radius, turn_speed, pixels_per_nm, 67.5f, 90.0f);
            } else if (map_range == 20) {
                // first segment : 30sec
                draw_position_trend_vector_segment(g2, turn_radius, turn_speed, pixels_per_nm, 5.0f, 30.0f);
                // second segment : 60sec
                draw_position_trend_vector_segment(g2, turn_radius, turn_speed, pixels_per_nm, 35.0f, 60.0f);
            } else {
                // first segment : 30sec
                draw_position_trend_vector_segment(g2, turn_radius, turn_speed, pixels_per_nm, 2.5f, 30.0f);
            }

            g2.setTransform(original_at);

        }

    }


    public void draw_position_trend_vector_segment(Graphics2D g2, float turn_radius, float turn_speed, long pixels_per_nm, float vector_start, float vector_end) {

        g2.setColor(nd_gc.aircraft_color);

        float turn_radius_pixels = turn_radius * pixels_per_nm;
        if (turn_speed >= 0) {
            // right turn
            g2.draw(new Arc2D.Float(
                    (float) nd_gc.map_center_x,
                    (float) (nd_gc.map_center_y - turn_radius_pixels),
                    turn_radius_pixels * 2.0f,
                    turn_radius_pixels * 2.0f,
                    180.0f - (vector_end * turn_speed),
                    (vector_end-vector_start) * turn_speed,
                    Arc2D.OPEN));
        } else {
            // left turn
            g2.draw(new Arc2D.Float(
                    (float) ((nd_gc.map_center_x) - (turn_radius_pixels * 2.0f)),
                    (float) (nd_gc.map_center_y - turn_radius_pixels),
                    turn_radius_pixels * 2.0f,
                    turn_radius_pixels * 2.0f,
                    vector_start * Math.abs(turn_speed),
                    (vector_end-vector_start) * Math.abs(turn_speed),
                    Arc2D.OPEN));
        }

        }


    public float turn_radius(float turn_speed, float speed) {

        return Math.abs(speed / (turn_speed * 20.0f * (float) Math.PI));

    }

}
