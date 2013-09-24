/**
* HeadingLabel.java
*
* Displays the current heading or track in a box at the top in the middle
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
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;


import net.sourceforge.xhsi.XHSIPreferences;
//import net.sourceforge.xhsi.XHSISettings;

import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ModelFactory;

//import net.sourceforge.xhsi.panel.GraphicsConfig;
//import net.sourceforge.xhsi.panel.Subcomponent;



public class HeadingLabel extends NDSubcomponent {

    private static final long serialVersionUID = 1L;
    AffineTransform original_at = null;
    int old_hdg_text_length = 0;
    BufferedImage hdg_label_decoration_buf_img;


    public HeadingLabel(ModelFactory model_factory, NDGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
        this.hdg_label_decoration_buf_img = null;
    }


    public void paint(Graphics2D g2) {
        
        if ( nd_gc.powered && ! nd_gc.mode_plan ) {

            // int y = nd_gc.border_top + nd_gc.line_height_large;
            int heading_box_bottom_y = nd_gc.border_top + nd_gc.line_height_large;
            int rose_top_y = this.nd_gc.map_center_y - nd_gc.rose_radius;
            int center_x = this.nd_gc.map_center_x;
            int center_y = this.nd_gc.map_center_y;
            int plane_width = (int) (30 * nd_gc.shrink_scaling_factor);
            int plane_height = (int) (plane_width * 1.5);

            // heading or track
            String up_label;
            int mag_value;
            float map_up;
            float hdg_pointer;
            float trk_line;
            if ( nd_gc.hdg_up ) {
                // HDG UP
                mag_value = Math.round(this.aircraft.heading());
                map_up = this.aircraft.heading() - this.aircraft.magnetic_variation();
                up_label = "HDG";
                hdg_pointer = 0.0f;
                trk_line = this.aircraft.track() - this.aircraft.heading();
            } else if ( nd_gc.trk_up ) {
                // TRK UP
                mag_value = Math.round(this.aircraft.track());
                map_up = this.aircraft.track() - this.aircraft.magnetic_variation();
                up_label = "TRK";
                hdg_pointer = this.aircraft.heading() - this.aircraft.track();
                trk_line = 0.0f;
            } else {
                // North UP
                mag_value = 999;
                map_up = 0.0f;
                up_label = " N ";
                hdg_pointer = this.aircraft.heading() - this.aircraft.magnetic_variation();
                trk_line = this.aircraft.track() - this.aircraft.magnetic_variation();
            }

    // buffered image not used (for now?)
    //        // create heading information decoration
    //        if (this.hdg_label_decoration_buf_img == null) {
    //                this.hdg_label_decoration_buf_img = create_buffered_image(200, 40);
    //                Graphics2D gImg = get_graphics(this.hdg_label_decoration_buf_img);
    //                render_heading_decoration(gImg, 200);
    //        }
    //        g2.drawImage(this.hdg_label_decoration_buf_img,
    //            nd_gc.map_center_x - (this.hdg_label_decoration_buf_img.getWidth()/2),
    //            nd_gc.border_top,
    //            null);

            //int width = 200;
            int x_points_heading_box[] = { nd_gc.map_center_x-36, nd_gc.map_center_x-36, nd_gc.map_center_x+36, nd_gc.map_center_x+36 };
            int y_points_heading_box[] = { nd_gc.border_top+0, nd_gc.border_top+30, nd_gc.border_top+30, nd_gc.border_top+0 };

            // TRK and MAG labels
            g2.setColor(nd_gc.heading_labels_color);
            g2.setFont(nd_gc.font_medium);
            if ( nd_gc.panel_rect.width >= 405 ) {
                if (nd_gc.panel_rect.width < 450) {
                    up_label = up_label.substring(0, 1);
                }
                g2.drawString(up_label , nd_gc.map_center_x - 43 - nd_gc.get_text_width(g2, nd_gc.font_medium, up_label), nd_gc.border_top + nd_gc.line_height_medium);
            }
            if ( nd_gc.panel_rect.width >= 405 ) {
//                g2.drawString( (nd_gc.panel_rect.width >= 480) ? "MAG" : "M" , nd_gc.map_center_x + 43, nd_gc.border_top + nd_gc.line_height_medium);
                g2.drawString( (nd_gc.panel_rect.width >= 485) ? "MAG" : "M" , nd_gc.map_center_x + 43, nd_gc.border_top + nd_gc.line_height_medium);
            }

            // surrounding box and value
            g2.setColor(nd_gc.top_text_color);
            g2.drawPolyline(x_points_heading_box, y_points_heading_box, 4);
            g2.clearRect(center_x - 34, nd_gc.border_top, 68, heading_box_bottom_y - nd_gc.border_top);
            g2.setFont(nd_gc.font_large);
            DecimalFormat degrees_formatter = new DecimalFormat("000");
            String text = degrees_formatter.format( mag_value );
            g2.drawString(text , center_x - 3*nd_gc.digit_width_large/2, heading_box_bottom_y - 3);

            // current heading pointer
            if ( ! nd_gc.mode_classic_hsi ) {
                int hdg_pointer_height = (int) Math.min(16,18 * nd_gc.shrink_scaling_factor);
                int hdg_pointer_width = (int) (10.0f * nd_gc.shrink_scaling_factor);
                int x_points_hdg_pointer[] = { center_x, center_x-hdg_pointer_width, center_x+hdg_pointer_width };
                int y_points_hdg_pointer[] = { nd_gc.rose_y_offset - 1, nd_gc.rose_y_offset - hdg_pointer_height, nd_gc.rose_y_offset - hdg_pointer_height };
                rotate(g2, hdg_pointer);
                g2.setColor(nd_gc.aircraft_color);
                g2.drawPolygon(x_points_hdg_pointer, y_points_hdg_pointer, 3);
                unrotate(g2);
            }

            // plane symbol
            g2.setColor(nd_gc.aircraft_color);
            if ( nd_gc.mode_classic_hsi ) {
                g2.drawLine(center_x - plane_width/4, center_y - plane_height, center_x - plane_width/4, center_y + plane_height);
                g2.drawLine(center_x + plane_width/4, center_y - plane_height, center_x + plane_width/4, center_y + plane_height);
                g2.drawLine(center_x - plane_height, center_y, center_x - plane_width/4, center_y);
                g2.drawLine(center_x + plane_width/4, center_y, center_x + plane_height, center_y);
                g2.drawLine(center_x - plane_width/2 - plane_width/4, center_y + plane_height, center_x - plane_width/4, center_y + plane_height);
                g2.drawLine(center_x + plane_width/4, center_y + plane_height, center_x + plane_width/2 + plane_width/4, center_y + plane_height);
            } else {
                int x_points_airplane_symbol[] = { center_x, center_x - (plane_width/2), center_x + (plane_width/2) };
                int y_points_airplane_symbol[] = { center_y, center_y + plane_height, center_y + plane_height };
                g2.drawPolygon(x_points_airplane_symbol, y_points_airplane_symbol, 3);
            }

            // drift angle pointer or track line with map zoom indication
            rotate(g2, trk_line);
            if ( nd_gc.mode_classic_hsi ) {
                // old style HSI
                // drift angle pointer in APP CTR and VOR CTR modes
                int pointer_height = (int)(nd_gc.big_tick_length / 2);
                int pointer_width = (int)(nd_gc.big_tick_length / 3);
                g2.setColor(nd_gc.aircraft_color);
                g2.drawLine(
                    nd_gc.map_center_x, nd_gc.map_center_y - nd_gc.rose_radius,
                    nd_gc.map_center_x + pointer_width, nd_gc.map_center_y - nd_gc.rose_radius + pointer_height
                );
                g2.drawLine(
                    nd_gc.map_center_x + pointer_width, nd_gc.map_center_y - nd_gc.rose_radius + pointer_height,
                    nd_gc.map_center_x, nd_gc.map_center_y - nd_gc.rose_radius + 2*pointer_height
                );
                g2.drawLine(
                    nd_gc.map_center_x, nd_gc.map_center_y - nd_gc.rose_radius + 2*pointer_height,
                    nd_gc.map_center_x - pointer_width, nd_gc.map_center_y - nd_gc.rose_radius + pointer_height
                );
                g2.drawLine(
                    nd_gc.map_center_x - pointer_width, nd_gc.map_center_y - nd_gc.rose_radius + pointer_height,
                    nd_gc.map_center_x, nd_gc.map_center_y - nd_gc.rose_radius
                );
            } else {
                // map style ND
//                g2.setColor(nd_gc.markings_color);
//                g2.setColor(nd_gc.dim_markings_color);
                g2.setColor(nd_gc.range_arc_color);
                int tick_halfwidth = (int)(5 * nd_gc.scaling_factor);
                g2.drawLine(
                    nd_gc.map_center_x, nd_gc.map_center_y - (nd_gc.rose_radius*3/16),
                    nd_gc.map_center_x, rose_top_y + 2);
                if ( ! XHSIPreferences.get_instance().get_draw_range_arcs() ) {
                    g2.drawLine(
                        nd_gc.map_center_x - tick_halfwidth, nd_gc.map_center_y - (nd_gc.rose_radius*3/4),
                        nd_gc.map_center_x + tick_halfwidth, nd_gc.map_center_y - (nd_gc.rose_radius*3/4) );
                    g2.drawLine(
                        nd_gc.map_center_x - tick_halfwidth, nd_gc.map_center_y - (nd_gc.rose_radius/2),
                        nd_gc.map_center_x + tick_halfwidth, nd_gc.map_center_y - (nd_gc.rose_radius/2) );
                    g2.drawLine(
                        nd_gc.map_center_x - tick_halfwidth, nd_gc.map_center_y - (nd_gc.rose_radius/4),
                        nd_gc.map_center_x + tick_halfwidth, nd_gc.map_center_y - (nd_gc.rose_radius/4) );
                }
                // a label at half the range
                g2.setFont(nd_gc.font_medium);
                int range = nd_gc.map_range;
                String ctr_ranges[] = {"2.5", "5", "10", "20", "40", "80", "160"};
                String exp_ranges[] = {"5", "10", "20", "40", "80", "160", "320"};
                String zoomin_ctr_ranges[] = {"0.025", "0.05", "0.10", "0.20", "0.40", "0.80", "1.60"};
                String zoomin_exp_ranges[] = {"0.05", "0.10", "0.20", "0.40", "0.80", "1.60", "3.20"};
                String range_text;
                int range_index = this.avionics.map_range_index();
                if ( nd_gc.mode_centered ) {
                    if ( nd_gc.map_zoomin ) {
                       range_text = zoomin_ctr_ranges[range_index];
                    } else {
                       range_text = ctr_ranges[range_index];
                    }
                } else {
                    if ( nd_gc.map_zoomin ) {
                        range_text = zoomin_exp_ranges[range_index];
                    } else {
                        range_text = exp_ranges[range_index];
                    }
                }
                g2.drawString(
                    range_text,
                    nd_gc.map_center_x - nd_gc.get_text_width(g2, nd_gc.font_medium, range_text) - 4,
                    nd_gc.map_center_y - (nd_gc.rose_radius / 2) - (nd_gc.get_text_height(g2, g2.getFont()) / 2) + 5
                );
            }
            unrotate(g2);

        }

    }


// buffered image not used (for now?)
//    private void render_heading_decoration(Graphics2D g2, int width) {
//
//        int x_points_heading_box[] = { (width/2)-36, (width/2)-36, (width/2)+36, (width/2)+36 };
//        int y_points_heading_box[] = { 0, 30, 30, 0 };
//
//        // TRK and MAG labels
//        g2.setColor(nd_gc.heading_labels_color);
//        g2.setFont(nd_gc.font_medium);
//        g2.drawString(new String("TRK") , (width/2) - 43 - nd_gc.get_text_width(g2, nd_gc.font_medium, "TRK"), nd_gc.line_height_medium);
//        g2.drawString("MAG" , (width/2) + 43, nd_gc.line_height_medium);
//
//        // surrounding box
//        g2.setColor(Color.LIGHT_GRAY);
//        g2.drawPolyline(x_points_heading_box, y_points_heading_box, 4);
//
//    }


    private void rotate(Graphics2D g2, double angle) {
        this.original_at = g2.getTransform();
        AffineTransform rotate = AffineTransform.getRotateInstance(
            Math.toRadians(angle),
            nd_gc.map_center_x,
            nd_gc.map_center_y
        );
        g2.transform(rotate);
    }

    private void unrotate(Graphics2D g2) {
        g2.setTransform(original_at);
    }

}
