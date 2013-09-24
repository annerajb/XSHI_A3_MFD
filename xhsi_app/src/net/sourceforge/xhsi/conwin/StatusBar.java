/**
* StatusBar.java
* 
* Renders the status bar with data source and frame rate indicators.
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
package net.sourceforge.xhsi.conwin;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import net.sourceforge.xhsi.XHSIStatus;

import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.SimDataRepository;

import net.sourceforge.xhsi.flightdeck.GraphicsConfig;
import net.sourceforge.xhsi.flightdeck.Subcomponent;

import net.sourceforge.xhsi.util.RunningAverager;
//import net.sourceforge.xhsi.util.TimedFilter;



public class StatusBar extends ConWinSubcomponent {

    private static final long serialVersionUID = 1L;
    long time_of_last_call;
    float[] last_frame_rates = new float[10];
    RunningAverager averager = new RunningAverager(30);
    //TimedFilter timed_filter;
    BufferedImage buf_img;
    int frame_rate = 0;

    SimDataRepository data_source;

    int fps_x;
    int fps_y;
    int fps_pixels_per_frame;

    int src_x;
    int src_y;

    int nav_db_status_x;
    int nav_db_status_y;
    int nav_db_text_width = 0;

    int utc_x;
    int utc_y;
    int utc_text_width = 0;


    public StatusBar(ModelFactory model_factory, ConWinGraphicsConfig hsi_gc, Component parent_component) {

        super(model_factory, hsi_gc, parent_component);
        
        this.time_of_last_call = System.currentTimeMillis();
        //this.timed_filter = new TimedFilter(1000);
        this.data_source = model_factory.get_repository_instance();

    }


    public void paint(Graphics2D g2) {

        calculate_frame_rate();

//        //if (timed_filter.time_to_perform()) {
//            this.buf_img = create_buffered_image(gc.panel_size.width, ConWinGraphicsConfig.STATUS_BAR_HEIGHT);
//            Graphics2D gImg = get_graphics(this.buf_img);
//            render_status_bar(gImg);
//            gImg.dispose();
//        //}
//
//        g2.drawImage(this.buf_img, 0, gc.panel_size.height - ConWinGraphicsConfig.STATUS_BAR_HEIGHT, null);

        render_status_bar(g2);

    }


    private void calculate_frame_rate() {

        long current_time = System.currentTimeMillis();
        this.frame_rate = (int) this.averager.running_average(1000.0f/(current_time - this.time_of_last_call));
        this.time_of_last_call = current_time;

    }


    private void render_status_bar(Graphics2D g2) {

            g2.setColor(gc.color_windowgray);
            g2.fillRect(0, 0, gc.panel_size.width, ConWinGraphicsConfig.STATUS_BAR_HEIGHT);

            g2.setBackground(Color.BLACK);

            // compute nav db text width only once
            if (nav_db_text_width == 0) {
                nav_db_text_width = this.gc.get_text_width(g2, this.gc.font_statusbar, "NAV DB 2000.99");
            }

            // compute UTC time width only once
            if (utc_text_width == 0) {
                utc_text_width = this.gc.get_text_width(g2, this.gc.font_statusbar, "00:00:00");
            }

            fps_x = gc.border_left + 120;
            fps_y = ConWinGraphicsConfig.STATUS_BAR_HEIGHT - 25;
            fps_pixels_per_frame = 2;

            src_x = gc.border_left;
            src_y = ConWinGraphicsConfig.STATUS_BAR_HEIGHT - 21;

            nav_db_status_x = gc.panel_size.width - gc.border_right - nav_db_text_width;
            nav_db_status_y = ConWinGraphicsConfig.STATUS_BAR_HEIGHT - 25;

            utc_x = gc.panel_size.width - gc.border_right - nav_db_text_width - 65 - utc_text_width;
            utc_y = ConWinGraphicsConfig.STATUS_BAR_HEIGHT - 25;

            draw_data_source(g2);
            draw_frame_rate(g2);
            draw_nav_db_status(g2);
            draw_utc_clock(g2);
    }


    public void draw_data_source(Graphics2D g2) {

        int x_offs = src_x + 60;
        int x_points_caret[] = { x_offs + 4, x_offs+10, x_offs+16, x_offs+10, x_offs+4 };
        int y_points_caret[] = { src_y+6,    src_y,     src_y+6,   src_y+12,  src_y+6 };

        int x_points_arrowhead[] = { x_offs + 8, x_offs+13, x_offs+8, x_offs+8 };
        int y_points_arrowhead[] = { src_y+3,    src_y+6,   src_y+9,  src_y+3 };

        int x_points_playtriangle[] = { src_x+18, src_x+10,  src_x+10 };
        int y_points_playtriangle[] = { src_y+7,  src_y+7-5, src_y+7+5 };

        g2.setColor(Color.BLACK);
        g2.setFont(gc.font_statusbar);
        g2.drawPolygon(x_points_caret, y_points_caret, 5);
        g2.drawLine(x_offs, src_y+6, x_offs + 8, src_y+6);
        g2.fillPolygon(x_points_arrowhead, y_points_arrowhead, 4);
        if ( this.data_source.is_replaying() ) {
            g2.drawString("PLAY", src_x+25, src_y+11);
            g2.setColor(Color.BLUE);
            //g2.fillOval(src_x+10,src_y,11,11);
            g2.fillPolygon(x_points_playtriangle, y_points_playtriangle, 3);
        } else {
            g2.drawString("X-Plane", src_x, src_y+11);
            if ( XHSIStatus.status.equals(XHSIStatus.STATUS_NO_RECEPTION) ) {
                g2.setColor(Color.RED);
                // cross out text
                g2.drawLine(src_x+15, src_y+12, src_x+30, src_y);
                g2.drawLine(src_x+15, src_y, src_x+30, src_y+12);
            }
        }

    }


    public void draw_nav_db_status(Graphics2D g2) {

        g2.setColor(Color.BLACK);
        g2.setFont(gc.font_statusbar);
        g2.drawString("NAV DB " + XHSIStatus.nav_db_cycle, nav_db_status_x, nav_db_status_y+13);

        if (XHSIStatus.nav_db_status.equals(XHSIStatus.STATUS_NAV_DB_NOT_FOUND)) {
            g2.setColor(Color.RED);
            // cross out text
            g2.drawLine(nav_db_status_x+15, nav_db_status_y+14, nav_db_status_x+30, nav_db_status_y+2);
            g2.drawLine(nav_db_status_x+15, nav_db_status_y+2, nav_db_status_x+30, nav_db_status_y+14);
        }

    }


    public void draw_utc_clock(Graphics2D g2) {

        DecimalFormat hms_formatter = new DecimalFormat("00");

        float utc_time = this.aircraft.sim_time_zulu();
        long hh = (long) utc_time / 3600;
        long mm = (long) utc_time / 60 % 60;
        long ss = (long) utc_time % 60;
        String utc_text = hms_formatter.format(hh) + ":" + hms_formatter.format(mm) + ":" + hms_formatter.format(ss);

        g2.setColor(Color.WHITE);
        g2.setFont(gc.font_medium);

        g2.drawString(utc_text, utc_x, utc_y+15);

    }


    public void draw_frame_rate(Graphics2D g2) {

        if (( XHSIStatus.status.equals(XHSIStatus.STATUS_RECEIVING) ) || ( XHSIStatus.status.equals(XHSIStatus.STATUS_PLAYING_RECORDING) )) {
            g2.setColor(Color.BLACK);
            g2.setFont(gc.font_statusbar);

            // draw scale
            g2.drawString("FPS",fps_x, fps_y + 11);
            for (int i=0;i<=40;i+=10) {
                g2.drawLine(
                        fps_x + 30 + (i*fps_pixels_per_frame),
                        fps_y + 10,
                        fps_x + 30 + (i*fps_pixels_per_frame),
                        fps_y + 13);
            }
            if (this.frame_rate > 40) {
                this.frame_rate = 40;
                g2.drawString("+", fps_x + 33 + (40*fps_pixels_per_frame), fps_y + 11);
            }

            g2.setFont(gc.font_statusbar);
            g2.drawString("0", fps_x + 28, fps_y + 23);
            g2.drawString("20", fps_x + 25 + (20*fps_pixels_per_frame), fps_y + 23);
            g2.drawString("40", fps_x + 25 + (40*fps_pixels_per_frame), fps_y + 23);

            // draw bar
            if (this.frame_rate > 12) {
                g2.setColor(Color.GREEN.darker());
            } else if (this.frame_rate > 8) {
                g2.setColor(Color.YELLOW.darker());
            } else if (this.frame_rate > 4) {
                g2.setColor(Color.ORANGE);
            } else {
                g2.setColor(Color.RED);
            }
//            g2.fillRoundRect(
//                    fps_x+30,
//                    fps_y,
//                    (this.frame_rate * fps_pixels_per_frame),
//                    8,
//                                        5,
//                                        5);
            g2.fillRect(
                    fps_x+30,
                    fps_y,
                    (this.frame_rate * fps_pixels_per_frame),
                    8);
        }

    }


}
