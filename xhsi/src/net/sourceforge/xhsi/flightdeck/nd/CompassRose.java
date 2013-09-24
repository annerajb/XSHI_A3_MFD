/**
* CompassRose.java
* 
* Renders the visible compass rose.
* 
* Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ModelFactory;

//import net.sourceforge.xhsi.panel.GraphicsConfig;
//import net.sourceforge.xhsi.panel.Subcomponent;



public class CompassRose extends NDSubcomponent {
    
    private static final long serialVersionUID = 1L;

    private int two_digit_hdg_text_width = 0;
    private int one_digit_hdg_text_width = 0;
    private int hdg_text_height = 0;


    public CompassRose(ModelFactory model_factory, NDGraphicsConfig hsi_gc) {
        super(model_factory, hsi_gc);

    }


    public int round_to_ten(float number) {
        return Math.round(number / 10) * 10;
    }


    public void paint(Graphics2D g2) {

        if ( nd_gc.powered ) {

            // calculate text widths and heights only once
            if (this.hdg_text_height == 0) {
                two_digit_hdg_text_width = (int) nd_gc.get_text_width(g2, nd_gc.font_medium, "33");
                one_digit_hdg_text_width = (int) nd_gc.get_text_width(g2, nd_gc.font_medium, "8");
                hdg_text_height = (int) (nd_gc.get_text_height(g2, nd_gc.font_medium)*0.8f);
            }

            g2.setColor(nd_gc.markings_color);
            if ( ! nd_gc.mode_centered ) {
                g2.drawArc(
                        nd_gc.map_center_x - nd_gc.rose_radius,
                        nd_gc.map_center_y - nd_gc.rose_radius,
                        nd_gc.rose_radius*2,
                        nd_gc.rose_radius*2,
                        (int)(nd_gc.half_view_angle + 90),
                        (int)(nd_gc.half_view_angle * -2)
                );
            }

            if ( ! nd_gc.mode_plan ) {

                // Compass rose for all modes except PLAN

                int min_visible_heading = round_to_ten(this.aircraft.track() - nd_gc.half_view_angle);
                int max_visible_heading = round_to_ten(this.aircraft.track() + nd_gc.half_view_angle) + 5;

                float map_up;
                if ( nd_gc.hdg_up ) {
                    // HDG UP
                    map_up = this.aircraft.heading();
                } else if ( nd_gc.trk_up ) {
                    // TRK UP
                    map_up = this.aircraft.track();
                } else {
                    // North UP
                    map_up = 0.0f;
                }

                double rotation_offset = (-1 * nd_gc.half_view_angle)  + (min_visible_heading - (map_up - nd_gc.half_view_angle));

                AffineTransform original_at = g2.getTransform();

                // rotate according to horziontal path
                AffineTransform rotate_to_heading = AffineTransform.getRotateInstance(
                        Math.toRadians(rotation_offset),
                        nd_gc.map_center_x,
                        nd_gc.map_center_y
                );
                g2.transform(rotate_to_heading);

                Graphics g = (Graphics) g2;
                int tick_length = 0;
                g2.setFont(nd_gc.font_medium);
                for (int angle = min_visible_heading; angle <= max_visible_heading; angle += 5) {
                    if (angle % 10 == 0) {
                        tick_length = nd_gc.big_tick_length;
                    } else {
                        tick_length = nd_gc.small_tick_length;
                    }
                    g.drawLine(nd_gc.map_center_x, nd_gc.rose_y_offset + 1,
                               nd_gc.map_center_x, nd_gc.rose_y_offset + tick_length);

                    String text = "";
                    if (angle < 0) {
                        text = "" + (angle + 360)/10;
                    } else if (angle >=360) {
                        text = "" + (angle - 360)/10;
                    } else {
                        text = "" + angle/10;
                    }
                    if (angle % 30 == 0) {
                        int text_width;
                        if (text.length() == 1)
                            text_width = one_digit_hdg_text_width;
                        else
                            text_width = two_digit_hdg_text_width;

                        g.drawString(
                                text,
                                nd_gc.map_center_x - (text_width/2),
                                nd_gc.rose_y_offset + tick_length + hdg_text_height);
                    }

                    AffineTransform rotate = AffineTransform.getRotateInstance(Math.toRadians(5.0),
                            nd_gc.map_center_x,
                            nd_gc.map_center_y);
                    g2.transform(rotate);

                }
                g2.setTransform(original_at);

                if ( nd_gc.mode_centered ) {
                    // 45 degrees marks for APP CTR, VOR CTR and MAP CTR
                    int mark_length = nd_gc.big_tick_length;
                    g2.drawLine(nd_gc.map_center_x, nd_gc.map_center_y - nd_gc.rose_radius - mark_length, nd_gc.map_center_x, nd_gc.map_center_y - nd_gc.rose_radius);
                    g2.drawLine(nd_gc.map_center_x, nd_gc.map_center_y + nd_gc.rose_radius, nd_gc.map_center_x, nd_gc.map_center_y + nd_gc.rose_radius + mark_length);
                    g2.drawLine(nd_gc.map_center_x - nd_gc.rose_radius - mark_length, nd_gc.map_center_y, nd_gc.map_center_x - nd_gc.rose_radius, nd_gc.map_center_y);
                    g2.drawLine(nd_gc.map_center_x + nd_gc.rose_radius, nd_gc.map_center_y, nd_gc.map_center_x + nd_gc.rose_radius + mark_length, nd_gc.map_center_y);
                    g2.transform(AffineTransform.getRotateInstance(Math.toRadians(45.0), nd_gc.map_center_x, nd_gc.map_center_y));
                    g2.drawLine(nd_gc.map_center_x, nd_gc.map_center_y - nd_gc.rose_radius - mark_length, nd_gc.map_center_x, nd_gc.map_center_y - nd_gc.rose_radius);
                    g2.drawLine(nd_gc.map_center_x, nd_gc.map_center_y + nd_gc.rose_radius, nd_gc.map_center_x, nd_gc.map_center_y + nd_gc.rose_radius + mark_length);
                    g2.drawLine(nd_gc.map_center_x - nd_gc.rose_radius - mark_length, nd_gc.map_center_y, nd_gc.map_center_x - nd_gc.rose_radius, nd_gc.map_center_y);
                    g2.drawLine(nd_gc.map_center_x + nd_gc.rose_radius, nd_gc.map_center_y, nd_gc.map_center_x + nd_gc.rose_radius + mark_length, nd_gc.map_center_y);
                    g2.setTransform(original_at);
                }

            } else {

                // circles for PLAN mode
                g2.setColor(nd_gc.markings_color);
                g2.drawOval(
                        nd_gc.map_center_x - nd_gc.rose_radius,
                        nd_gc.map_center_y - nd_gc.rose_radius,
                        nd_gc.rose_radius*2,
                        nd_gc.rose_radius*2
                );
                g2.setColor(nd_gc.range_arc_color);
                g2.drawOval(
                        nd_gc.map_center_x - nd_gc.rose_radius/2,
                        nd_gc.map_center_y - nd_gc.rose_radius/2,
                        nd_gc.rose_radius,
                        nd_gc.rose_radius
                );

                String ctr_ranges[] = {"2.5", "5", "10", "20", "40", "80", "160"};
                String exp_ranges[] = {"5", "10", "20", "40", "80", "160", "320"};
                String zoomin_ctr_ranges[] = {"0.025", "0.05", "0.10", "0.20", "0.40", "0.80", "1.60"};
                String zoomin_exp_ranges[] = {"0.05", "0.10", "0.20", "0.40", "0.80", "1.60", "3.20"};
                String range_text;
                int range_index = this.avionics.map_range_index();
                if ( nd_gc.map_zoomin )
                    range_text = zoomin_exp_ranges[range_index];
                else
                    range_text = exp_ranges[range_index];
                g2.drawString(
                    range_text,
                    nd_gc.map_center_x - nd_gc.get_text_width(g2, nd_gc.font_medium, range_text) / 2,
                    nd_gc.map_center_y - nd_gc.rose_radius + nd_gc.line_height_medium
                );
                if ( nd_gc.map_zoomin )
                    range_text = zoomin_ctr_ranges[range_index];
                else
                    range_text = ctr_ranges[range_index];
                g2.drawString(
                    range_text,
                    nd_gc.map_center_x - nd_gc.get_text_width(g2, nd_gc.font_medium, range_text) / 2,
                    nd_gc.map_center_y - nd_gc.rose_radius/2 + nd_gc.line_height_medium
                );

                g2.setColor(nd_gc.heading_labels_color);
                g2.setFont(nd_gc.font_medium);
                g2.drawString("N", nd_gc.map_center_x - nd_gc.max_char_advance_medium/2, nd_gc.map_center_y - nd_gc.rose_radius - 10);
                g2.drawString("E", nd_gc.map_center_x + nd_gc.rose_radius + 10, nd_gc.map_center_y + nd_gc.line_height_medium/2);
                g2.drawString("S", nd_gc.map_center_x - nd_gc.max_char_advance_medium/2, nd_gc.map_center_y + nd_gc.rose_radius + 10 + nd_gc.line_height_medium - 3);
                g2.drawString("W", nd_gc.map_center_x - nd_gc.rose_radius - 10 - nd_gc.max_char_advance_medium, nd_gc.map_center_y + nd_gc.line_height_medium/2);

            }

        }

    }


}
