/**
* APHeading.java
* 
* Renders autopilot heading bug and line from airplane symbol to heading bug.
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

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;

//import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ModelFactory;

//import net.sourceforge.xhsi.panel.GraphicsConfig;
//import net.sourceforge.xhsi.panel.Subcomponent;



public class APHeading extends NDSubcomponent {

	private static final long serialVersionUID = 1L;
	float dash[] = { 11.0f, 22.0f };
	
	public APHeading(ModelFactory model_factory, NDGraphicsConfig hsi_gc) {
		super(model_factory, hsi_gc);
	}
		
	public void paint(Graphics2D g2) {

            if ( nd_gc.powered && ! nd_gc.mode_plan ) {

                GeneralPath polyline = null;

                float map_up;
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
                float ap_heading_offset = map_up - ( avionics.heading_bug() - this.aircraft.magnetic_variation() );

                // rotate according to heading bug
                AffineTransform original_at = g2.getTransform();
                g2.rotate(
                        Math.toRadians((double) (-1 * ap_heading_offset)),
                        nd_gc.map_center_x,
                        nd_gc.map_center_y
                );

                // heading bug
                int heading_bug_width = (int) Math.min(38, 40 * nd_gc.shrink_scaling_factor);
                int heading_bug_height = (int) Math.min(14, 16 * nd_gc.shrink_scaling_factor);

                g2.setColor(nd_gc.heading_bug_color);
                polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 9);
                polyline.moveTo(nd_gc.map_center_x - heading_bug_width/2, nd_gc.rose_y_offset);
                polyline.lineTo(nd_gc.map_center_x - heading_bug_width/2, nd_gc.rose_y_offset - heading_bug_height);
                polyline.lineTo(nd_gc.map_center_x - (heading_bug_width/3 - 1), nd_gc.rose_y_offset - heading_bug_height);
                polyline.lineTo(nd_gc.map_center_x, nd_gc.rose_y_offset);
                polyline.lineTo(nd_gc.map_center_x + (heading_bug_width/3 - 1), nd_gc.rose_y_offset - heading_bug_height);
                polyline.lineTo(nd_gc.map_center_x + heading_bug_width/2, nd_gc.rose_y_offset - heading_bug_height);
                polyline.lineTo(nd_gc.map_center_x + heading_bug_width/2, nd_gc.rose_y_offset);
                polyline.lineTo (nd_gc.map_center_x - heading_bug_width/2, nd_gc.rose_y_offset);
                g2.draw(polyline);

                if ( ! nd_gc.mode_classic_hsi ) {
                    // dotted line from plane to heading bug, not for APP CTR or VOR CTR
                    Stroke original_stroke = g2.getStroke();
                    g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
                    g2.draw(new Line2D.Double(nd_gc.map_center_x, nd_gc.map_center_y, nd_gc.map_center_x, nd_gc.rose_y_offset));
                    g2.setStroke(original_stroke);
                }

                // reset transformation
                g2.setTransform(original_at);

            }

	}	

}
