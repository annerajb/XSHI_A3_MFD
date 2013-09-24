/**
* AltitudeRangeArc.java
* 
* Based on vertical speed and groundspeed, indicate the approximate map position
* where the selected AP altitude will be reached
* 
* Copyright (C) 2009-2010  Marc Rogiers (marrog.123@gmail.com)
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

//import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.util.logging.Logger;

//import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ModelFactory;

//import net.sourceforge.xhsi.panel.GraphicsConfig;
//import net.sourceforge.xhsi.panel.Subcomponent;



public class AltitudeRangeArc extends NDSubcomponent {

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    private static final long serialVersionUID = 1L;

    private static final float ARC = 5.0f;


    public AltitudeRangeArc(ModelFactory model_factory, NDGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
        //this.nor = NavigationObjectRepository.get_instance();
    }


    public void paint(Graphics2D g2) {

        if ( nd_gc.powered && nd_gc.mode_map ) {
            // only in map modes, not in PLAN, APP CTR, VOR CTR

            //float current_altitude = this.aircraft.indicated_altitude();
            float current_altitude = this.aircraft.altitude_ind();
            float target_altitude = this.avionics.autopilot_altitude();
            //float vertical_velocity = this.aircraft.indicated_vv();
            float vertical_velocity = this.aircraft.vvi();
            float groundspeed = this.aircraft.ground_speed();

            // dont't bother if we are almost there, or vv is too small
            if ( ( Math.abs(target_altitude - current_altitude) > 100.0f ) && ( Math.abs(vertical_velocity) > 100.0f ) ) {

                float distance = ( target_altitude - current_altitude ) / vertical_velocity / 60.0f * groundspeed;
                if ( ( distance > 0.0f ) && ( distance < nd_gc.max_range ) ) {

                    // OK, we are climbing or descending as intended, and our distance is within the map range
                    float arc_radius = distance * nd_gc.pixels_per_nm;

                    // keep the width +/- constant
                    float arc = ARC * nd_gc.max_range / distance;
                    if ( nd_gc.map_zoomin ) arc /= 100.0f;
                    // but never more than 45 degs
                    if ( arc > 45.0f ) arc = 45.0f;

                    g2.setColor(nd_gc.altitude_arc_color); // yellowgreen or lime
                    g2.draw(new Arc2D.Float(
                            (float)nd_gc.map_center_x - arc_radius,
                            (float)nd_gc.map_center_y - arc_radius,
                            arc_radius * 2.0f,
                            arc_radius * 2.0f,
                            90.0f - arc,
                            2.0f * arc,
                            Arc2D.OPEN));

                }

            }

        }

    }


}
