/**
* ADI.java
* 
* Renders an Attitude & Director Indicator
* 
* Copyright (C) 2010  Marc Rogiers (marrog.123@gmail.com)
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
package net.sourceforge.xhsi.flightdeck.pfd;

import java.awt.BasicStroke;
//import java.awt.Color;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
//import java.awt.image.BufferedImage;

import java.util.logging.Logger;

//import net.sourceforge.xhsi.XHSISettings;

//import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ModelFactory;
//import net.sourceforge.xhsi.model.NavigationRadio;

//import net.sourceforge.xhsi.panel.GraphicsConfig;
//import net.sourceforge.xhsi.panel.Subcomponent;



public class ADI extends PFDSubcomponent {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");


    public ADI(ModelFactory model_factory, PFDGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
    }


    public void paint(Graphics2D g2) {
        if ( pfd_gc.powered ) {
            drawADI(g2);
            drawMarker(g2);
        }
    }


    private void drawADI(Graphics2D g2) {

        int cx = pfd_gc.adi_cx;
        int cy = pfd_gc.adi_cy;
        int left = pfd_gc.adi_size_left;
        int right = pfd_gc.adi_size_right;
        int up = pfd_gc.adi_size_up;
        int down = pfd_gc.adi_size_down;
        int p_90 = pfd_gc.adi_pitch90;
        int scale = pfd_gc.adi_pitchscale;

        boolean colorgradient_horizon = this.preferences.get_draw_colorgradient_horizon();

        float pitch = this.aircraft.pitch(); // radians? no, degrees!
        float bank = this.aircraft.bank(); // degrees
//logger.warning("pitch: " + pitch + " / " + Math.toDegrees(pitch));
//bank *= 2.0f;
//pitch = 11.75f;

        // full-scale pitch down = adi_pitchscale (eg: 22°)
        int pitch_y = cy + (int)(down * pitch / scale);

        Shape original_clipshape = g2.getClip();
        if ( ! colorgradient_horizon ) {
            g2.clipRect(cx - left, cy - up, left + right, up + down);
        } else if ( this.preferences.get_draw_fullwidth_horizon() ) {
            if ( pfd_gc.draw_hsi ) {
                g2.clipRect(pfd_gc.panel_rect.x, pfd_gc.panel_rect.y, pfd_gc.panel_rect.width, pfd_gc.dg_cy - pfd_gc.dg_radius - pfd_gc.hsi_tick_w - pfd_gc.line_height_xl*3/2 - pfd_gc.panel_rect.y);
            } else {
//                g2.clipRect(pfd_gc.panel_rect.x, pfd_gc.tape_top - 1, pfd_gc.panel_rect.width, pfd_gc.tape_height + 2);
                g2.clipRect(pfd_gc.panel_rect.x, pfd_gc.panel_rect.y + pfd_gc.panel_offset_y, pfd_gc.panel_rect.width, pfd_gc.panel_rect.width);
            }
        }
        
        AffineTransform original_at = g2.getTransform();
        g2.rotate(Math.toRadians(-bank), cx, cy);

        int diagonal = colorgradient_horizon ?
            (int)Math.hypot( Math.max(cx, pfd_gc.panel_rect.width - cx), Math.max(cy, pfd_gc.panel_rect.height - cy) ) :
            (int)Math.hypot( Math.max(left, right), Math.max(up, down) );

        if ( colorgradient_horizon ) {

            GradientPaint up_gradient = new GradientPaint(
                    cx - diagonal, pitch_y - p_90, pfd_gc.background_color,
                    cx - diagonal, pitch_y - p_90/2, pfd_gc.sky_color,
                    false);
            GradientPaint sky_gradient = new GradientPaint(
                    cx - diagonal, pitch_y - p_90/2, pfd_gc.sky_color,
                    cx - diagonal, pitch_y, pfd_gc.brightsky_color,
                    false);
            GradientPaint ground_gradient = new GradientPaint(
                    cx - diagonal, pitch_y, pfd_gc.brightground_color,
                    cx - diagonal, pitch_y + p_90/2, pfd_gc.ground_color,
                    false);
            GradientPaint down_gradient = new GradientPaint(
                    cx - diagonal, pitch_y + p_90/2, pfd_gc.ground_color,
                    cx - diagonal, pitch_y + p_90 , pfd_gc.background_color,
                    false);

            g2.setPaint(up_gradient);
            g2.fillRect(cx - diagonal, pitch_y - p_90, 2 * diagonal, p_90/2 + 2);
            g2.setPaint(sky_gradient);
            g2.fillRect(cx - diagonal, pitch_y - p_90/2, 2 * diagonal, p_90);

            g2.setPaint(ground_gradient);
            g2.fillRect(cx - diagonal, pitch_y, 2 * diagonal, p_90/2 + 2);
            g2.setPaint(down_gradient);
            g2.fillRect(cx - diagonal, pitch_y + p_90/2, 2 * diagonal, p_90/2);

        } else {
            g2.setColor(pfd_gc.sky_color);
            g2.fillRect(cx - diagonal, pitch_y - p_90, 2 * diagonal, p_90);
            g2.setColor(pfd_gc.ground_color);
            g2.fillRect(cx - diagonal, pitch_y, 2 * diagonal, p_90);
        }

        g2.setColor(pfd_gc.markings_color);
        g2.drawLine(cx - diagonal, pitch_y, cx + diagonal, pitch_y);

        g2.setTransform(original_at);

        if ( this.preferences.get_draw_roundedsquare_horizon() ) {
            g2.setColor(pfd_gc.background_color);
            Area adi_roundrectarea = new Area(new RoundRectangle2D.Float(
                    cx - left, cy - up, left + right, up + down,
                    (int)(60 * pfd_gc.scaling_factor),
                    (int)(60 * pfd_gc.scaling_factor)));
            Area adi_area = new Area(new Rectangle2D.Float(cx - left, cy - up, left + right, up + down));
            adi_area.subtract(adi_roundrectarea);
            g2.fill(adi_area);
        }


        // pitch marks
        g2.clipRect(
                cx - left + left/16,
                cy - up + up/16 + up/8 + up/24,
                left - left/16 + right - right/16,
                up - up/16 - up/8 - up/24 + down - down/16
            );

        g2.rotate(Math.toRadians(-bank), cx, cy);

        g2.setColor(pfd_gc.markings_color);
        drawPitchmark(g2, pitch, +175, pitch_y, p_90, cx, cy, left);
        drawPitchmark(g2, pitch, +150, pitch_y, p_90, cx, cy, left);
        drawPitchmark(g2, pitch, +125, pitch_y, p_90, cx, cy, left);
        drawPitchmark(g2, pitch, +100, pitch_y, p_90, cx, cy, left);
        drawPitchmark(g2, pitch,  +75, pitch_y, p_90, cx, cy, left);
        drawPitchmark(g2, pitch,  +50, pitch_y, p_90, cx, cy, left);
        drawPitchmark(g2, pitch,  +25, pitch_y, p_90, cx, cy, left);
        drawPitchmark(g2, pitch,    0, pitch_y, p_90, cx, cy, left);
        drawPitchmark(g2, pitch,  -25, pitch_y, p_90, cx, cy, left);
        drawPitchmark(g2, pitch,  -50, pitch_y, p_90, cx, cy, left);
        drawPitchmark(g2, pitch,  -75, pitch_y, p_90, cx, cy, left);
        drawPitchmark(g2, pitch, -100, pitch_y, p_90, cx, cy, left);
        drawPitchmark(g2, pitch, -125, pitch_y, p_90, cx, cy, left);
        drawPitchmark(g2, pitch, -150, pitch_y, p_90, cx, cy, left);
        drawPitchmark(g2, pitch, -175, pitch_y, p_90, cx, cy, left);
        drawPitchmark(g2, pitch, -200, pitch_y, p_90, cx, cy, left);

        // no more clipping ...
        g2.setClip(original_clipshape);
        // ... unless
        if ( ! colorgradient_horizon ) {
            g2.setTransform(original_at);
            g2.clipRect(cx - left, cy - up, left + right, up + down);
            g2.rotate(Math.toRadians(-bank), cx, cy);
        }


        // bank pointer
        int[] bank_pointer_x = {
            cx,
            cx - left/12,
            cx + right/12 };
        int[] bank_pointer_y = {
            cy - up + up/16,
            cy - up + up/16 + up/8,
            cy - up + up/16 + up/8 };
        if ( Math.abs(bank) > 35.0f ) {
            if ( Math.abs(bank) > 70.0f ) {
                g2.setColor(pfd_gc.warning_color);
            } else {
                g2.setColor(pfd_gc.caution_color);
            }
            g2.fillPolygon(bank_pointer_x, bank_pointer_y, 3);
        }
        g2.drawPolygon(bank_pointer_x, bank_pointer_y, 3);

        // slip/skid indicator
        float ss = - this.aircraft.sideslip();
        int ss_x;
        if ( Math.abs(ss) > 10.0f ) {
            ss = 10.0f * Math.signum(ss);
            ss_x = cx - Math.round(ss * left/6 / 10.0f);
            g2.fillRect(ss_x - left/12, cy - up + up/16 + up/8, left/12 + right/12, up/24);
        } else {
            ss_x = cx - Math.round(ss * left/6 / 10.0f);
        }
        g2.drawRect(ss_x - left/12, cy - up + up/16 + up/8, left/12 + right/12, up/24);

        g2.setTransform(original_at);

        Stroke original_stroke = g2.getStroke();

        
        // FPV
        if ( ! this.aircraft.on_ground() ) {

            int dx = (int)(down * this.aircraft.drift() / scale);
            int dy = (int)(down * this.aircraft.aoa() / scale);
            if ( (Math.abs(dx) < down) && (Math.abs(dy) < down) ) {

                int fpv_x = cx - dx;
                int fpv_y = cy + dy;
                int fpv_r = down/20;
                g2.setColor(pfd_gc.fpv_color);
                g2.setStroke(new BasicStroke(3.0f * pfd_gc.grow_scaling_factor));
                g2.drawOval(fpv_x - fpv_r, fpv_y - fpv_r, fpv_r*2, fpv_r*2);
                g2.drawLine(fpv_x, fpv_y - fpv_r, fpv_x, fpv_y - fpv_r*25/10);
                g2.drawLine(fpv_x - fpv_r, fpv_y, fpv_x - fpv_r*4, fpv_y);
                g2.drawLine(fpv_x + fpv_r, fpv_y, fpv_x + fpv_r*4, fpv_y);
                g2.setStroke(original_stroke);

            }

        }
      

        // airplane symbol and FD
        if ( ! this.preferences.get_single_cue_fd() ) {

            int wing_t = Math.round(4 * pfd_gc.grow_scaling_factor);
            int wing_i = left / 3;
            int wing_o = left * 7 / 8;
            int wing_h = down / 8;
            int left_wing_x[] = {
                cx - wing_i + wing_t,
                cx - wing_i + wing_t,
                cx - wing_i - wing_t,
                cx - wing_i - wing_t,
                cx - wing_o,
                cx - wing_o
            };
            int right_wing_x[] = {
                cx + wing_i - wing_t,
                cx + wing_i - wing_t,
                cx + wing_i + wing_t,
                cx + wing_i + wing_t,
                cx + wing_o,
                cx + wing_o
            };
            int wing_y[] = {
                cy - wing_t,
                cy + wing_h,
                cy + wing_h,
                cy + wing_t,
                cy + wing_t,
                cy - wing_t
            };
            g2.setColor(pfd_gc.background_color);
            g2.fillPolygon(left_wing_x, wing_y, 6);
            g2.fillPolygon(right_wing_x, wing_y, 6);
            g2.setColor(pfd_gc.markings_color);
            g2.drawPolygon(left_wing_x, wing_y, 6);
            g2.drawPolygon(right_wing_x, wing_y, 6);

        }


        // FD
        if ( this.avionics.autopilot_mode() >= 1 ) {
//if ( true ) {

            int fd_y;
            if ( this.avionics.is_x737() )
                fd_y = cy + (int)(down * (-this.avionics.fd_pitch()) / scale);
            else
                fd_y = cy + (int)(down * (pitch-this.avionics.fd_pitch()) / scale);

            g2.setColor(pfd_gc.heading_bug_color);

            if ( this.preferences.get_single_cue_fd() ) {
                
                // V-bar
                g2.rotate(Math.toRadians(-bank+this.avionics.fd_roll()), cx, fd_y);

                int bar_o = left * 9 / 16;
                int bar_d = down / 5;
                int bar_h = down / 28;
                int bar_w = left / 10;
                int left_bar_x[] = {
                    cx - 2,
                    cx - bar_o - 2,
                    cx - bar_o - bar_w - 1
                };
                int right_bar_x[] = {
                    cx + 2,
                    cx + bar_o + 2,
                    cx + bar_o + bar_w + 1
                };
                int bar_y[] = {
                    fd_y,
                    fd_y + bar_d,
                    fd_y + bar_d - bar_h
                };
                g2.drawPolygon(left_bar_x, bar_y, 3);
                g2.drawPolygon(right_bar_x, bar_y, 3);
                g2.fillPolygon(left_bar_x, bar_y, 3);
                g2.fillPolygon(right_bar_x, bar_y, 3);
                int left_tri_x[] = {
                    cx - bar_o - bar_w - 3,
                    cx - bar_o - 2,
                    cx - bar_o - bar_w - 3
                };
                int right_tri_x[] = {
                    cx + bar_o + bar_w + 3,
                    cx + bar_o + 2,
                    cx + bar_o + bar_w + 3
                };
                int tri_y[] = {
                    fd_y + bar_d + bar_h,
                    fd_y + bar_d,
                    fd_y + bar_d - bar_h
                };
                g2.setColor(pfd_gc.instrument_background_color);
                g2.fillPolygon(left_tri_x, tri_y, 3);
                g2.fillPolygon(right_tri_x, tri_y, 3);
                g2.setColor(pfd_gc.heading_bug_color);
                g2.drawPolygon(left_tri_x, tri_y, 3);
                g2.drawPolygon(right_tri_x, tri_y, 3);

                g2.setTransform(original_at);

            } else {

                // cross-hair
                int fd_x = cx + (int)(down * (-bank+this.avionics.fd_roll()) / scale) / 3; // divide by 3 to limit deflection
                int fd_bar = down * 5 /8;
                original_stroke = g2.getStroke();
                g2.setStroke(new BasicStroke(3.0f * pfd_gc.scaling_factor));
                // hor
                g2.drawLine(cx - fd_bar, fd_y, cx + fd_bar, fd_y);
                // vert
                g2.drawLine(fd_x, cy - fd_bar, fd_x, cy + fd_bar);
                g2.setStroke(original_stroke);

            }

        }


        if ( this.preferences.get_single_cue_fd() ) {

            // Delta airplane
            int delta_i = left / 4;
            int delta_o = left * 9 / 16;
            int delta_h = down / 5;
            int left_delta_x[] = {
                cx,
                cx - delta_i,
                cx - delta_o
            };
            int right_delta_x[] = {
                cx,
                cx + delta_i,
                cx + delta_o
            };
            int delta_y[] = {
                cy,
                cy + delta_h,
                cy + delta_h
            };
            g2.setColor(pfd_gc.background_color);
            g2.fillPolygon(left_delta_x, delta_y, 3);
            g2.fillPolygon(right_delta_x, delta_y, 3);
            g2.setColor(pfd_gc.markings_color);
            g2.drawPolygon(left_delta_x, delta_y, 3);
            g2.drawPolygon(right_delta_x, delta_y, 3);

        } else {
            // small square in the center
            g2.setColor(pfd_gc.markings_color);
            int wing_t = Math.round(4 * pfd_gc.grow_scaling_factor);
            g2.drawRect(cx - wing_t, cy - wing_t, wing_t * 2, wing_t * 2);
        }


        // bank marks
        int level_triangle_x[] = { cx, cx - left/20 - left/40, cx + right/20 + right/40 };
        int level_triangle_y[] = { cy - up + up/16, cy - up - up/32, cy - up - up/32 };
        g2.setColor(pfd_gc.markings_color);
        g2.fillPolygon(level_triangle_x, level_triangle_y, 3);
        g2.rotate(Math.toRadians(+10), cx, cy);
        g2.drawLine(cx, cy - up, cx, cy - up + up/16);
        g2.rotate(Math.toRadians(-10-10), cx, cy);
        g2.drawLine(cx, cy - up, cx, cy - up + up/16);
        g2.rotate(Math.toRadians(+10+20), cx, cy);
        g2.drawLine(cx, cy - up, cx, cy - up + up/16);
        g2.rotate(Math.toRadians(-20-20), cx, cy);
        g2.drawLine(cx, cy - up, cx, cy - up + up/16);
        g2.rotate(Math.toRadians(+20+45), cx, cy);
        g2.drawLine(cx, cy - up, cx, cy - up + up/16);
        g2.rotate(Math.toRadians(-45-45), cx, cy);
        g2.drawLine(cx, cy - up, cx, cy - up + up/16);
        g2.rotate(Math.toRadians(+45+30), cx, cy);
        g2.drawLine(cx, cy - up - up/12, cx, cy - up + up/16);
        g2.rotate(Math.toRadians(-30-30), cx, cy);
        g2.drawLine(cx, cy - up - up/12, cx, cy - up + up/16);
        g2.rotate(Math.toRadians(+30+60), cx, cy);
        g2.drawLine(cx, cy - up - up/12, cx, cy - up + up/16);
        g2.rotate(Math.toRadians(-60-60), cx, cy);
        g2.drawLine(cx, cy - up - up/12, cx, cy - up + up/16);

        g2.setTransform(original_at);

        g2.setClip(original_clipshape);

//        g2.setColor(pfd_gc.instrument_background_color);
//        g2.fillRect(pfd_gc.border_left + ( pfd_gc.frame_size.width - pfd_gc.border_left - pfd_gc.border_right ) / 32, pfd_gc.border_top + ( pfd_gc.frame_size.height - pfd_gc.border_top - pfd_gc.border_bottom ) / 8, ( pfd_gc.frame_size.width - pfd_gc.border_left - pfd_gc.border_right ) / 8, ( pfd_gc.frame_size.height - pfd_gc.border_top - pfd_gc.border_bottom ) / 8 * 6);
        
    }


//    private int markWidth(int p_m, int size) {
//        int p_w = size;
//        return p_w;
//    }


    private void drawPitchmark(Graphics2D g2, float pitch, int pitchmark, int p_y, int p_90, int cx, int cy, int size) {

        int p_m = Math.round(pitch / 2.5f) * 25 + pitchmark;
        int m_y = p_y - p_90 * p_m / 900;
        int p_w = size;
        if ( p_m % 100 == 0 ) {
            p_w = size * 7 / 16;
        } else if ( p_m % 50 == 0 ) {
            p_w = size / 4;
        } else if ( p_m % 25 == 0 ) {
            p_w = size / 12;
        }

        if ( ( p_m <= 900 ) && ( p_m != 0 ) && ( p_m >= -900 ) ) {
            g2.drawLine(cx - p_w, m_y, cx + p_w, m_y);
            if ( ( p_m != 0 ) && ( p_m % 100 == 0 ) ) {
                g2.setFont(pfd_gc.font_s);
                int f_w = pfd_gc.get_text_width(g2, pfd_gc.font_s, "00");
                int f_y = pfd_gc.line_height_s / 2 - 2;
                String pitch_str = "" + Math.abs(p_m/10);
                g2.drawString(pitch_str, cx - p_w - f_w - 3, m_y + f_y);
                g2.drawString(pitch_str, cx + p_w + 3, m_y + f_y);
            }
        }

    }


    private void drawMarker(Graphics2D g2) {

        if ( this.avionics.outer_marker() || this.avionics.middle_marker() || this.avionics.inner_marker() ) {

            int m_r = pfd_gc.adi_size_right*2/16;
            int m_x;
            int m_y;
            if ( this.preferences.get_draw_fullwidth_horizon() ) {
                m_x = pfd_gc.adi_cx - pfd_gc.adi_size_left;
                m_y = m_y = pfd_gc.adi_cy - pfd_gc.adi_size_up;
            } else {
                m_x = pfd_gc.adi_cx + pfd_gc.adi_size_right - pfd_gc.adi_size_right*1/16 - 2*m_r;
                m_y = pfd_gc.adi_cy - pfd_gc.adi_size_up + pfd_gc.adi_size_right*1/16;
            }

            g2.setColor(pfd_gc.background_color);
            g2.fillOval(m_x, m_y, 2*m_r, 2*m_r);

            String mstr = "";
            if ( this.avionics.outer_marker() ) {
                g2.setColor(Color.BLUE);
                mstr = "OM";
            } else if ( this.avionics.middle_marker() ) {
                g2.setColor(pfd_gc.caution_color);
                mstr = "MM";
            } else {
                g2.setColor(pfd_gc.markings_color);
                mstr = "IM";
            }

            Stroke original_stroke = g2.getStroke();
            g2.setStroke(new BasicStroke(4.0f * pfd_gc.grow_scaling_factor));
            g2.drawOval(m_x, m_y, 2*m_r, 2*m_r);
            g2.setStroke(original_stroke);

            g2.setFont(pfd_gc.font_m);
            g2.drawString(mstr, m_x + m_r - pfd_gc.get_text_width(g2, pfd_gc.font_m, mstr)/2, m_y + m_r + pfd_gc.line_height_m/2 - 2);

        }
        
    }


}
