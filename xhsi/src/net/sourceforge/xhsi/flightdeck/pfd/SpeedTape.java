/**
* SpeedTape.java
* 
* ...
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

//import java.awt.AlphaComposite;
import java.awt.BasicStroke;
//import java.awt.Color;
import java.awt.Color;
import java.awt.Component;
//import java.awt.Composite;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
//import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import java.util.logging.Logger;

//import net.sourceforge.xhsi.XHSISettings;

//import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ModelFactory;
//import net.sourceforge.xhsi.model.NavigationRadio;

//import net.sourceforge.xhsi.panel.GraphicsConfig;
//import net.sourceforge.xhsi.panel.Subcomponent;



public class SpeedTape extends PFDSubcomponent {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");


    public SpeedTape(ModelFactory model_factory, PFDGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
    }


    public void paint(Graphics2D g2) {
        if ( pfd_gc.powered ) {
            drawTape(g2);
        }
    }


    private void drawTape(Graphics2D g2) {

        DecimalFormat mach_format = new DecimalFormat("#.00");
        DecimalFormatSymbols format_symbols = mach_format.getDecimalFormatSymbols();
        format_symbols.setDecimalSeparator('.');
        mach_format.setDecimalFormatSymbols(format_symbols);


        // speeds
        float ias = this.aircraft.airspeed_ind();
        float tas = this.aircraft.true_air_speed();
        float sound_speed = this.aircraft.sound_speed();
        float mach = this.aircraft.mach();


        // tape
        pfd_gc.setTransparent(g2, this.preferences.get_draw_colorgradient_horizon());
        g2.setColor(pfd_gc.instrument_background_color);
        g2.fillRect(pfd_gc.speedtape_left - 1, pfd_gc.tape_top - 1, pfd_gc.tape_width + 2, pfd_gc.tape_height + 2);
        pfd_gc.setOpaque(g2);

        Shape original_clipshape = g2.getClip();
        g2.clipRect(pfd_gc.speedtape_left, pfd_gc.tape_top, pfd_gc.tape_width*2, pfd_gc.tape_height);


        // scale markings
        g2.setColor(pfd_gc.dim_markings_color);
        // round to nearest multiple of 10
        int ias5 = Math.round(ias / 10.0f) * 10;
        // From there, go 50kts up and down
        for (int ias_mark = ias5 - 50; ias_mark <= ias5 + 50; ias_mark += 10) {
            if (ias_mark >= 0) {

                int ias_y = pfd_gc.adi_cy - Math.round( ((float)ias_mark - ias) * pfd_gc.tape_height / 100.0f );
                g2.drawLine(pfd_gc.speedtape_left + pfd_gc.tape_width*7/8, ias_y, pfd_gc.speedtape_left + pfd_gc.tape_width - 1, ias_y);
                
                if (ias_mark % 20 == 0) {
                    g2.setFont(pfd_gc.font_l);
                    String mark_str = "" + ias_mark;
                    g2.drawString(mark_str, pfd_gc.speedtape_left + pfd_gc.tape_width*7/8 - pfd_gc.get_text_width(g2, pfd_gc.font_l, mark_str) - pfd_gc.tape_width*3/16, ias_y + pfd_gc.line_height_l/2 - 2);
                }

            }
        }


        // 10sec speed trend vector
        float ias_trend = this.aircraft.airspeed_acceleration() * 10.0f;
        if ( Math.abs(ias_trend) > 5.0f ) {

            if ( ( ias + ias_trend ) < 0.0f ) {
                ias_trend = - ias;
            }
            int asi10_y = pfd_gc.adi_cy - Math.round( ias_trend * pfd_gc.tape_height / 100.0f );
            g2.setColor(pfd_gc.color_lime);
            g2.drawLine(pfd_gc.speedtape_left + pfd_gc.tape_width*7/8, pfd_gc.adi_cy, pfd_gc.speedtape_left + pfd_gc.tape_width*7/8, asi10_y);
            int arrow_dx = pfd_gc.tape_width*1/16;
            int arrow_dy = pfd_gc.tape_width*2/16 * (int)Math.signum(ias_trend);
            //g2.drawLine(pfd_gc.speedtape_left + pfd_gc.tape_width*7/8, asi10_y, pfd_gc.speedtape_left + pfd_gc.tape_width*7/8 - arrow_dx, asi10_y + arrow_dy);
            //g2.drawLine(pfd_gc.speedtape_left + pfd_gc.tape_width*7/8, asi10_y, pfd_gc.speedtape_left + pfd_gc.tape_width*7/8 + arrow_dx, asi10_y + arrow_dy);
            int[] arrow_x = {
                pfd_gc.speedtape_left + pfd_gc.tape_width*7/8,
                pfd_gc.speedtape_left + pfd_gc.tape_width*7/8 - arrow_dx,
                pfd_gc.speedtape_left + pfd_gc.tape_width*7/8 + arrow_dx
            };
            int[] arrow_y = {
                asi10_y,
                asi10_y + arrow_dy,
                asi10_y + arrow_dy
            };
            g2.drawPolygon(arrow_x, arrow_y, 3);
            g2.fillPolygon(arrow_x, arrow_y, 3);

        }


        // red max
        Stroke original_stroke = g2.getStroke();
        int halfstroke = pfd_gc.tape_width/16;
        float vmmo = 999.9f;
        float mmo = this.aircraft.get_Mmo();
        if ( ( ias > 100.0f ) && ( mmo > 0.1f ) ) {
            vmmo = mmo * sound_speed * ias / tas;
        }
        float vmax = Math.min(this.aircraft.get_Vne(), vmmo);
        if ( this.aircraft.get_flap_position() > 0.0f ) {
            // flaps extended
            //float vfe = this.aircraft.get_Vfe() + ( 1.0f - this.aircraft.get_flap_position() ) * ( this.aircraft.get_Vno() - this.aircraft.get_Vfe() );
            vmax = Math.min(vmax, this.aircraft.get_Vfe());
        }
        if ( ! this.aircraft.gear_is_up() ) {
            // landing gear extended
            vmax = Math.min(vmax, this.aircraft.get_Vle());
        }
        int red_max_y = pfd_gc.adi_cy - Math.round( (vmax - ias) * pfd_gc.tape_height / 100.0f );
        if ( red_max_y > pfd_gc.tape_top ) {
            // draw a thick red dashed line *from* red_max_y *to* the top
            g2.setColor(pfd_gc.warning_color);
            g2.setStroke(new BasicStroke(2.0f * halfstroke));
            float red_dashes[] = { halfstroke*2.0f, halfstroke*2.0f };
            g2.setStroke(new BasicStroke(2.0f * halfstroke, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, red_dashes, 0.0f));
            g2.drawLine(pfd_gc.speedtape_left + pfd_gc.tape_width + halfstroke + 1, red_max_y, pfd_gc.speedtape_left + pfd_gc.tape_width + halfstroke + 1, pfd_gc.tape_top);
            g2.setStroke(original_stroke);
        } else {
            // we don't draw, but set the top for the amber line
            red_max_y = pfd_gc.tape_top;
        }

        // amber max
        float vno = this.aircraft.get_Vno();
        if ( vno < vmax ) {
            int amber_max_y = pfd_gc.adi_cy - Math.round( (vno - ias) * pfd_gc.tape_height / 100.0f );
            if ( amber_max_y > pfd_gc.tape_top ) {
                // draw an amber line between red_max_y and amber_max_y
                g2.setColor(pfd_gc.caution_color);
                g2.drawLine(pfd_gc.speedtape_left + pfd_gc.tape_width + halfstroke + 2, red_max_y, pfd_gc.speedtape_left + pfd_gc.tape_width + halfstroke + 2, amber_max_y);
                g2.drawLine(pfd_gc.speedtape_left + pfd_gc.tape_width + 1, amber_max_y, pfd_gc.speedtape_left + pfd_gc.tape_width + halfstroke + 1, amber_max_y);
            }
        }


        // red min and amber min only when airborne
        float vs_est = this.aircraft.get_Vso() + ( 1.0f - this.aircraft.get_flap_position() ) * ( this.aircraft.get_Vs() - this.aircraft.get_Vso() );
        if ( ! this.aircraft.on_ground() ) {

            // red min
            // estimate stall speed by interpolating between Vs (clean) and Vso (landing configuration)
            int red_min_y = pfd_gc.adi_cy - Math.round( (vs_est - ias) * pfd_gc.tape_height / 100.0f );
            if ( red_min_y < pfd_gc.tape_top + pfd_gc.tape_height ) {
                // draw a thick red dashed line *from* red_min_y *to* zero
                int red_zero_y = pfd_gc.adi_cy - Math.round( (0.0f - ias) * pfd_gc.tape_height / 100.0f );
                g2.setColor(pfd_gc.warning_color);
                g2.setStroke(new BasicStroke(2.0f * halfstroke));
                float red_dashes[] = { halfstroke*2.0f, halfstroke*2.0f };
                g2.setStroke(new BasicStroke(2.0f * halfstroke, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, red_dashes, 0.0f));
                g2.drawLine(pfd_gc.speedtape_left + pfd_gc.tape_width + halfstroke + 1, red_min_y, pfd_gc.speedtape_left + pfd_gc.tape_width + halfstroke + 1, red_zero_y);
                g2.setStroke(original_stroke);
            } else {
                // we don't draw, but set the top for the amber line
                red_min_y = pfd_gc.tape_top + pfd_gc.tape_height;
            }

            int amber_min_y = pfd_gc.adi_cy - Math.round( (vs_est*1.1f - ias) * pfd_gc.tape_height / 100.0f );
            if ( amber_min_y < pfd_gc.tape_top + pfd_gc.tape_height ) {
                // draw an amber line between red_min_y and amber_min_y
                g2.setColor(pfd_gc.caution_color);
                g2.drawLine(pfd_gc.speedtape_left + pfd_gc.tape_width + halfstroke + 2, red_min_y, pfd_gc.speedtape_left + pfd_gc.tape_width + halfstroke + 2, amber_min_y);
                g2.drawLine(pfd_gc.speedtape_left + pfd_gc.tape_width + 1, amber_min_y, pfd_gc.speedtape_left + pfd_gc.tape_width + halfstroke + 1, amber_min_y);
            }

        }

        // V-speeds
        boolean take_off = this.aircraft.on_ground() ||
                ( ( this.aircraft.agl_m() < 762.0f /* 2500ft */ ) && ( this.aircraft.vvi() > 250.0f ) );
        boolean landing = ! this.aircraft.on_ground() &&
                ( ( this.aircraft.agl_m() < 1524.0f /* 5000ft */ ) && ( this.aircraft.vvi() < -250.0f ) );

        if ( this.avionics.has_ufmc() ) {

            // V-speeds from UFMC

            if ( take_off ) {
                float v1 = this.avionics.ufmc_v1();
                if ( v1 > 0.0f ) drawVspeed(g2, v1, ias, "V1");
                float vr = this.avionics.ufmc_vr();
                if ( vr > 0.0f ) drawVspeed(g2, vr, ias, "VR");
                float v2 = this.avionics.ufmc_v2();
                if ( v2 > 0.0f ) drawVspeed(g2, v2, ias, "V2");
            }
            if ( landing ) {
                float vref = this.avionics.ufmc_vref();
                if ( vref > 0.0f ) {
                    drawVspeed(g2, vref, ias, "REF");
                } else {
                    vref = this.avionics.ufmc_vf30();
                    if ( vref > 0.0f ) drawVspeed(g2, vref, ias, "F30");
                    vref = this.avionics.ufmc_vf40();
                    if ( vref > 0.0f ) drawVspeed(g2, vref, ias, "F40");
                }
            }
            
        } else if ( this.avionics.is_cl30() ) {
            
            // V-speeds from CL30 (without UFMC)
            
            if ( this.avionics.cl30_refspds() == 1 ) {

                drawVspeed(g2, (float)this.avionics.cl30_v1(), ias, "V1");
                drawVspeed(g2, (float)this.avionics.cl30_vr(), ias, "VR");
                drawVspeed(g2, (float)this.avionics.cl30_v2(), ias, "V2");
                
            } else {
                
                drawVspeed(g2, (float)this.avionics.cl30_vt(), ias, "VT");
                drawVspeed(g2, (float)this.avionics.cl30_vga(), ias, "VGA");
                drawVspeed(g2, (float)this.avionics.cl30_vref(), ias, "REF");
                
            }

        } else {

            // estimate V-speeds
            
            if ( take_off ) {
                float vr = vs_est * 1.15f; // very rough estimate
                drawVspeed(g2, vr, ias, "VR");
            }
            if ( landing ) {
                float vref = vs_est * 1.3f; // rough estimate
                drawVspeed(g2, vref, ias, "REF");
            }
            
        }

        
        // AP speed bug and value readout
        float ap_ias;
        String ap_spd_str;
        if ( this.avionics.autopilot_speed_is_mach() ) {
        } else {
        }
        if ( this.avionics.autopilot_speed_is_mach() ) {
            // AP SPD is Mach
            float ap_tas = this.avionics.autopilot_speed() * sound_speed;
            if ( ( ias < 10.0f ) || ( tas < 10.0f ) ) {
                ap_ias = ap_tas;
            } else {
                ap_ias = ap_tas * ias / tas;
            }
            ap_spd_str = mach_format.format( this.avionics.autopilot_speed() );
        } else {
            // AP SPD is Kts
            ap_ias = this.avionics.autopilot_speed();
            ap_spd_str = "" + Math.round( ap_ias );
        }

        int ap_spdbug_y = pfd_gc.adi_cy - Math.round( (ap_ias - ias) * pfd_gc.tape_height / 100.0f );
        if ( ap_spdbug_y < pfd_gc.tape_top ) {
            ap_spdbug_y = pfd_gc.tape_top;
        } else if ( ap_spdbug_y > pfd_gc.tape_top + pfd_gc.tape_height ) {
            ap_spdbug_y = pfd_gc.tape_top + pfd_gc.tape_height;
        }
        int[] bug_x = {
            pfd_gc.speedtape_left + pfd_gc.tape_width - pfd_gc.tape_width*1/8,
            pfd_gc.speedtape_left + pfd_gc.tape_width,
            pfd_gc.speedtape_left + pfd_gc.tape_width + pfd_gc.tape_width*5/16,
            pfd_gc.speedtape_left + pfd_gc.tape_width + pfd_gc.tape_width*5/16,
            pfd_gc.speedtape_left + pfd_gc.tape_width
        };
        int[] bug_y = {
            ap_spdbug_y,
            ap_spdbug_y - pfd_gc.tape_width*1/8,
            ap_spdbug_y - pfd_gc.tape_width*1/8,
            ap_spdbug_y + pfd_gc.tape_width*1/8,
            ap_spdbug_y + pfd_gc.tape_width*1/8
        };
        g2.setColor(pfd_gc.heading_bug_color);
        g2.drawPolygon(bug_x, bug_y, 5);

        g2.setClip(original_clipshape);

        g2.setFont(pfd_gc.font_xxl);
        int str_w = pfd_gc.get_text_width(g2, pfd_gc.font_xxl, ap_spd_str);
        int str_x = pfd_gc.speedtape_left + pfd_gc.tape_width*7/8 - str_w;
        int str_y = pfd_gc.tape_top - pfd_gc.tape_width/8 - 3;
        g2.clearRect(str_x - pfd_gc.digit_width_xxl/3, str_y - pfd_gc.line_height_xxl*7/8, str_w + pfd_gc.digit_width_xxl*2/3, pfd_gc.line_height_xxl);
        g2.drawString(ap_spd_str, str_x, str_y);


        // speed readout
        int[] box_x = {
            pfd_gc.speedtape_left + pfd_gc.tape_width*7/8,
            pfd_gc.speedtape_left + pfd_gc.tape_width*7/8 - pfd_gc.tape_width*3/16,
            pfd_gc.speedtape_left + pfd_gc.tape_width*7/8 - pfd_gc.tape_width*3/16,
            pfd_gc.speedtape_left - Math.round(5.0f*pfd_gc.grow_scaling_factor),
            pfd_gc.speedtape_left - Math.round(5.0f*pfd_gc.grow_scaling_factor),
            pfd_gc.speedtape_left + pfd_gc.tape_width*7/8 - pfd_gc.tape_width*3/16,
            pfd_gc.speedtape_left + pfd_gc.tape_width*7/8 - pfd_gc.tape_width*3/16
        };
        int[] box_y = {
            pfd_gc.adi_cy,
            pfd_gc.adi_cy + pfd_gc.tape_width*3/20,
            pfd_gc.adi_cy + pfd_gc.line_height_xxl,
            pfd_gc.adi_cy + pfd_gc.line_height_xxl,
            pfd_gc.adi_cy - pfd_gc.line_height_xxl,
            pfd_gc.adi_cy - pfd_gc.line_height_xxl,
            pfd_gc.adi_cy - pfd_gc.tape_width*3/20
        };
        g2.setColor(pfd_gc.background_color);
        g2.fillPolygon(box_x, box_y, 7);
        g2.setColor(pfd_gc.markings_color);
        g2.drawPolygon(box_x, box_y, 7);

        g2.clipRect(pfd_gc.speedtape_left - 2, pfd_gc.adi_cy - pfd_gc.line_height_xxl, pfd_gc.tape_width, 2 * pfd_gc.line_height_xxl);

        g2.setFont(pfd_gc.font_xxl);

        int ias_int = (int)ias; // alternative: ias_int = Math.round(ias - 0.5f);
        int ias_units = ias_int % 10;
        int ias_deca = (ias_int / 10) % 10;
        int ias_hecto = (ias_int / 100) % 10;
        float ias_frac = ias - (float)ias_int;
        int ydelta = Math.round( pfd_gc.line_height_xxl * ias_frac );

        g2.drawString("" + (ias_int + 2) % 10, pfd_gc.speedtape_left + pfd_gc.tape_width*7/8 - pfd_gc.tape_width*3/16 - pfd_gc.digit_width_xxl - 2, pfd_gc.adi_cy + pfd_gc.line_height_xxl/2 - 2 + ydelta - pfd_gc.line_height_xxl*2);
        g2.drawString("" + (ias_int + 1) % 10, pfd_gc.speedtape_left + pfd_gc.tape_width*7/8 - pfd_gc.tape_width*3/16 - pfd_gc.digit_width_xxl - 2, pfd_gc.adi_cy + pfd_gc.line_height_xxl/2 - 2 + ydelta - pfd_gc.line_height_xxl);
        g2.drawString("" + ias_int % 10, pfd_gc.speedtape_left + pfd_gc.tape_width*7/8 - pfd_gc.tape_width*3/16 - pfd_gc.digit_width_xxl - 2, pfd_gc.adi_cy + pfd_gc.line_height_xxl/2 - 2 + ydelta);
        if ( ias_int >  0 ) {
            g2.drawString("" + (ias_int - 1) % 10, pfd_gc.speedtape_left + pfd_gc.tape_width*7/8 - pfd_gc.tape_width*3/16 - pfd_gc.digit_width_xxl - 2, pfd_gc.adi_cy + pfd_gc.line_height_xxl/2 - 2 + ydelta + pfd_gc.line_height_xxl);
        }

        if ( ias_units == 9 ) {
            if ( ias > 9.99f ) {
                g2.drawString("" + ias_deca, pfd_gc.speedtape_left + pfd_gc.tape_width*7/8 - pfd_gc.tape_width*3/16 - 2*pfd_gc.digit_width_xxl - 2, pfd_gc.adi_cy + pfd_gc.line_height_xxl/2 - 2 + ydelta);
            }
            g2.drawString("" + (ias_deca + 1) % 10, pfd_gc.speedtape_left + pfd_gc.tape_width*7/8 - pfd_gc.tape_width*3/16 - 2*pfd_gc.digit_width_xxl - 2, pfd_gc.adi_cy + pfd_gc.line_height_xxl/2 - 2 + ydelta - pfd_gc.line_height_xxl);
        } else if (ias > 9.99f) {
            g2.drawString("" + ias_deca, pfd_gc.speedtape_left + pfd_gc.tape_width*7/8 - pfd_gc.tape_width*3/16 - 2*pfd_gc.digit_width_xxl - 2, pfd_gc.adi_cy + pfd_gc.line_height_xxl/2 - 2);
        }

        if ( ( ias_deca == 9 ) && ( ias_units == 9 ) ) {
            if ( ias > 99.9f ) {
                g2.drawString("" + ias_hecto, pfd_gc.speedtape_left + pfd_gc.tape_width*7/8 - pfd_gc.tape_width*3/16 - 3*pfd_gc.digit_width_xxl - 2, pfd_gc.adi_cy + pfd_gc.line_height_xxl/2 - 2 + ydelta);
            }
            g2.drawString("" + (ias_hecto + 1) % 10, pfd_gc.speedtape_left + pfd_gc.tape_width*7/8 - pfd_gc.tape_width*3/16 - 3*pfd_gc.digit_width_xxl - 2, pfd_gc.adi_cy + pfd_gc.line_height_xxl/2 - 2 + ydelta - pfd_gc.line_height_xxl);
        } else if (ias > 99.9f) {
            g2.drawString("" + ias_hecto, pfd_gc.speedtape_left + pfd_gc.tape_width*7/8 - pfd_gc.tape_width*3/16 - 3*pfd_gc.digit_width_xxl - 2, pfd_gc.adi_cy + pfd_gc.line_height_xxl/2 - 2);
        }

        g2.setClip(original_clipshape);

        // Mach value
        if ( mach >= 0.40f ) {
            String mach_str = mach_format.format( mach );
            g2.setFont(pfd_gc.font_xxl);
            g2.setColor(pfd_gc.markings_color);
            g2.drawString(mach_str, pfd_gc.speedtape_left + pfd_gc.tape_width*7/8 - pfd_gc.get_text_width(g2, pfd_gc.font_xxl, mach_str), pfd_gc.tape_top + pfd_gc.tape_height + pfd_gc.tape_width/8 + pfd_gc.line_height_xxl - 3);
        }

    }

    
    private void drawVspeed(Graphics2D g2, float v, float ias, String v_str) {
        
        int v_y = pfd_gc.adi_cy - Math.round( (v - ias) * pfd_gc.tape_height / 100.0f );

        g2.setColor(pfd_gc.color_lime);
        g2.drawLine(pfd_gc.speedtape_left + pfd_gc.tape_width*7/8, v_y, pfd_gc.speedtape_left + pfd_gc.tape_width*9/8, v_y);
        g2.setFont(pfd_gc.font_normal);
        g2.drawString(v_str, pfd_gc.speedtape_left + pfd_gc.tape_width + 2, v_y - 3);
        
    }

}
