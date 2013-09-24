/**
* Engines.java
* 
* Engine instruments
* 
* Copyright (C) 2010-2011  Marc Rogiers (marrog.123@gmail.com)
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
package net.sourceforge.xhsi.flightdeck.eicas;

import java.awt.BasicStroke;
//import java.awt.Color;
//import java.awt.Color;
import java.awt.Component;
//import java.awt.GradientPaint;
import java.awt.Graphics2D;
//import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
//import java.awt.geom.Area;
//import java.awt.geom.Rectangle2D;
//import java.awt.geom.RoundRectangle2D;
//import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import java.util.logging.Logger;

import net.sourceforge.xhsi.XHSIPreferences;
import net.sourceforge.xhsi.XHSISettings;

import net.sourceforge.xhsi.model.ModelFactory;



public class Engines extends EICASSubcomponent {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");


    private Stroke original_stroke;

    private boolean inhibit;
    private DecimalFormat one_decimal_format;
    private DecimalFormat two_decimals_format;
    private DecimalFormatSymbols format_symbols;

    private int prim_dial_x[] = new int[8];
    private int seco_dial_x[] = new int[8];


    public Engines(ModelFactory model_factory, EICASGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);

        one_decimal_format = new DecimalFormat("##0.0");
        format_symbols = one_decimal_format.getDecimalFormatSymbols();
        format_symbols.setDecimalSeparator('.');
        one_decimal_format.setDecimalFormatSymbols(format_symbols);

        two_decimals_format = new DecimalFormat("#0.00");
        format_symbols = two_decimals_format.getDecimalFormatSymbols();
        format_symbols.setDecimalSeparator('.');
        two_decimals_format.setDecimalFormatSymbols(format_symbols);

    }


    public void paint(Graphics2D g2) {

        if ( eicas_gc.powered && ( this.aircraft.num_engines() > 0 ) ) {
//if ( true ) {

            this.inhibit = ( this.aircraft.agl_m() < 1000.0f / 3.28084f );
//inhibit = false;

            boolean piston = this.preferences.get_preference(XHSIPreferences.PREF_ENGINE_TYPE).equals(XHSIPreferences.ENGINE_TYPE_MAP);

            boolean turboprop = this.preferences.get_preference(XHSIPreferences.PREF_ENGINE_TYPE).equals(XHSIPreferences.ENGINE_TYPE_TRQ);
            
            int num_eng = this.aircraft.num_engines();
//num_eng = 1;
            int cols = Math.max(num_eng, 2);
            for (int i=0; i<cols; i++) {
                prim_dial_x[i] = eicas_gc.panel_rect.x + eicas_gc.dials_width*50/100/cols + i*eicas_gc.dials_width/cols;
                seco_dial_x[i] = eicas_gc.alerts_x0 + i*eicas_gc.alerts_w/cols + (eicas_gc.alerts_w/cols*15/16)/2;
            }

            if ( piston ) {

                for (int i=0; i<num_eng; i++) {
//                    prim_dial_x[i] = eicas_gc.panel_rect.x + eicas_gc.dials_width*50/100/cols + i*eicas_gc.dials_width/cols;
                    drawMAP(g2, i, num_eng);
                    drawEGT(g2, i, num_eng);
                    drawPROP(g2, i, num_eng);
                    if ( ! this.preferences.get_eicas_primary() ) {
                        drawFF(g2, i, num_eng);
                        drawNG(g2, i, num_eng);
//                        seco_dial_x[i] = eicas_gc.alerts_x0 + i*eicas_gc.alerts_w/num_eng + (eicas_gc.alerts_w/cols*15/16)/2;
                        drawOilP(g2, i, num_eng);
                        drawOilT(g2, i, num_eng);
                        drawOilQ(g2, i);
                    }
                }

            } else if ( turboprop ) {

                for (int i=0; i<num_eng; i++) {
//                    prim_dial_x[i] = eicas_gc.panel_rect.x + eicas_gc.dials_width*50/100/cols + i*eicas_gc.dials_width/cols;
                    drawTRQ(g2, i, num_eng);
                    drawITT(g2, i, num_eng);
                    drawPROP(g2, i, num_eng);
                    if ( ! this.preferences.get_eicas_primary() ) {
                        drawFF(g2, i, num_eng);
                        drawNG(g2, i, num_eng);
//                        seco_dial_x[i] = eicas_gc.alerts_x0 + i*eicas_gc.alerts_w/num_eng + (eicas_gc.alerts_w/cols*15/16)/2;
                        drawOilP(g2, i, num_eng);
                        drawOilT(g2, i, num_eng);
                        drawOilQ(g2, i);
                    }
                }

            } else {

                for (int i=0; i<num_eng; i++) {
//                    prim_dial_x[i] = eicas_gc.panel_rect.x + eicas_gc.dials_width*50/100/cols + i*eicas_gc.dials_width/cols;
                    drawN1(g2, i, num_eng);
                    drawEGT(g2, i, num_eng);
                    if ( ! this.preferences.get_eicas_primary() ) {
                        drawN2(g2, i, num_eng);
                        drawFF(g2, i, num_eng);
//                        seco_dial_x[i] = eicas_gc.alerts_x0 + i*eicas_gc.alerts_w/num_eng + (eicas_gc.alerts_w/cols*15/16)/2;
                        drawOilP(g2, i, num_eng);
                        drawOilT(g2, i, num_eng);
                        drawOilQ(g2, i);
                        drawVIB(g2, i, num_eng);
                    }
                }
                
            }

            String ind_str;
            int ind_x;
            g2.setColor(eicas_gc.color_boeingcyan);
            g2.setFont(eicas_gc.font_m);
            // N1
            ind_str = piston ? "MAP" : ( turboprop ? "TRQ" : "N1" );
            if ( cols == 2 ) {
                ind_x = (prim_dial_x[0] + prim_dial_x[1]) / 2 - eicas_gc.get_text_width(g2, eicas_gc.font_m, ind_str)/2;
            } else {
                ind_x = prim_dial_x[num_eng-1] + eicas_gc.dial_r[num_eng]*145/100 - eicas_gc.get_text_width(g2, eicas_gc.font_m, ind_str);
            }
            g2.drawString(ind_str, ind_x, eicas_gc.dial_n1_y + Math.min(eicas_gc.eicas_size*9/100 + eicas_gc.dial_font_h[num_eng], eicas_gc.dial_r[2]) - 2);
            // EGT
            ind_str = piston ? "EGT" : ( turboprop ? "ITT" : "EGT" );
            if ( cols == 2 ) {
                ind_x = (prim_dial_x[0] + prim_dial_x[1]) / 2 - eicas_gc.get_text_width(g2, eicas_gc.font_m, ind_str)/2;
            } else {
                ind_x = prim_dial_x[num_eng-1] + eicas_gc.dial_r[num_eng]*145/100 - eicas_gc.get_text_width(g2, eicas_gc.font_m, ind_str);
            }
            g2.drawString(ind_str, ind_x, eicas_gc.dial_egt_y + Math.min(eicas_gc.eicas_size*9/100 + eicas_gc.dial_font_h[num_eng], eicas_gc.dial_r[2]) - 2);

            if ( piston || turboprop || ! this.preferences.get_eicas_primary() ) {
                
                // N2
                ind_str = piston ? "RPM" : ( turboprop ? "PROP" : "N2" );
                if ( cols == 2 ) {
                    ind_x = (prim_dial_x[0] + prim_dial_x[1]) / 2 - eicas_gc.get_text_width(g2, eicas_gc.font_m, ind_str)/2;
                } else {
                    ind_x = prim_dial_x[num_eng-1] + eicas_gc.dial_r[num_eng]*145/100 - eicas_gc.get_text_width(g2, eicas_gc.font_m, ind_str);
                }
                g2.drawString(ind_str, ind_x, eicas_gc.dial_n2_y + Math.min(eicas_gc.eicas_size*9/100 + eicas_gc.dial_font_h[num_eng], eicas_gc.dial_r[2]) - 2);
                
                g2.setFont(eicas_gc.font_xs);

            }

            if ( ! this.preferences.get_eicas_primary() ) {
                
                // FF
                ind_str = "FF";
                if ( cols == 2 ) {
                    ind_x = (prim_dial_x[0] + prim_dial_x[1]) / 2 - eicas_gc.get_text_width(g2, eicas_gc.font_m, ind_str)/2;
                } else {
                    ind_x = prim_dial_x[num_eng-1] + eicas_gc.dial_r[num_eng]*145/100 - eicas_gc.get_text_width(g2, eicas_gc.font_m, ind_str);
                }
                g2.drawString(ind_str, ind_x, eicas_gc.dial_ff_y + Math.min(eicas_gc.eicas_size*9/100 + eicas_gc.dial_font_h[num_eng], eicas_gc.dial_r[2]*80/100) - 2);

                g2.setFont(eicas_gc.font_xs);

                // OIL P
                ind_str = "OIL P";
                if ( cols == 2 ) {
                    ind_x = (seco_dial_x[0] + seco_dial_x[1]) / 2 - eicas_gc.get_text_width(g2, eicas_gc.font_xs, ind_str)/2;
                } else {
                    ind_x = seco_dial_x[0] - eicas_gc.dial_r[num_eng]*85/100;
                }
                g2.drawString(ind_str, ind_x, eicas_gc.dial_oil_p_y + eicas_gc.dial_r[2]*70/100 + eicas_gc.line_height_xs);
                // OIL T
                ind_str = "OIL T";
                if ( cols == 2 ) {
                    ind_x = (seco_dial_x[0] + seco_dial_x[1]) / 2 - eicas_gc.get_text_width(g2, eicas_gc.font_xs, ind_str)/2;
                } else {
                    ind_x = seco_dial_x[0] - eicas_gc.dial_r[num_eng]*85/100;
                }
                g2.drawString(ind_str, ind_x, eicas_gc.dial_oil_t_y + eicas_gc.dial_r[2]*70/100 + eicas_gc.line_height_xs);
                // OIL Q
                ind_str = "OIL Q";
                if ( cols == 2 ) {
                    ind_x = (seco_dial_x[0] + seco_dial_x[1]) / 2 - eicas_gc.get_text_width(g2, eicas_gc.font_xs, ind_str)/2;
                } else {
                    ind_x = seco_dial_x[0] - eicas_gc.dial_r[num_eng]*85/100;
                }
                g2.drawString(ind_str, ind_x, eicas_gc.dial_oil_q_y + eicas_gc.line_height_xs*35/20);
                // VIB or NG or CHT
                ind_str = piston ? "CHT" : ( turboprop ? "NG" : "VIB" );
                if ( cols == 2 ) {
                    ind_x = (seco_dial_x[0] + seco_dial_x[1]) / 2 - eicas_gc.get_text_width(g2, eicas_gc.font_xs, ind_str)/2;
                } else {
                    ind_x = seco_dial_x[0] - eicas_gc.dial_r[num_eng]*85/100;
                }
                g2.drawString(ind_str, ind_x, eicas_gc.dial_vib_y + eicas_gc.dial_r[2]*70/100 + eicas_gc.line_height_xs);

            }

        }

    }


    private void drawN1(Graphics2D g2, int pos, int num) {

        AffineTransform original_at = g2.getTransform();
        scalePen(g2);

        float n1_value = this.aircraft.get_N1(pos);
        float n1_dial = Math.min(n1_value, 110.0f) / 100.0f;
        String n1_str = one_decimal_format.format(n1_value);

        int n1_y = eicas_gc.dial_n1_y;
        int n1_r = eicas_gc.dial_r[num];
        int n1_box_y = n1_y - n1_r/8;

        if ( ( n1_dial <= 1.0f ) || this.inhibit ) {
            // inhibit caution or warning below 1000ft
            g2.setColor(eicas_gc.instrument_background_color);
        } else if ( n1_dial < 1.1f ) {
            g2.setColor(eicas_gc.caution_color.darker().darker());
        } else {
            g2.setColor(eicas_gc.warning_color.darker().darker());
        }
        g2.fillArc(prim_dial_x[pos]-n1_r, n1_y-n1_r, 2*n1_r, 2*n1_r, 0, -Math.round(n1_dial*200.0f));

        g2.setColor(eicas_gc.dim_markings_color);
        for (int i=0; i<=10; i++) {
            g2.drawLine(prim_dial_x[pos]+n1_r*14/16, n1_y, prim_dial_x[pos]+n1_r-1, n1_y);
            g2.rotate(Math.toRadians(20), prim_dial_x[pos], n1_y);
        }
        g2.setTransform(original_at);
        g2.drawArc(prim_dial_x[pos]-n1_r, n1_y-n1_r, 2*n1_r, 2*n1_r, 0, -200);
        g2.setColor(eicas_gc.caution_color);
        g2.drawArc(prim_dial_x[pos]-n1_r, n1_y-n1_r, 2*n1_r, 2*n1_r, -200, -20);
        g2.rotate(Math.toRadians(220), prim_dial_x[pos], n1_y);
        g2.setColor(eicas_gc.warning_color);
        g2.drawLine(prim_dial_x[pos]+n1_r, n1_y, prim_dial_x[pos]+n1_r*19/16, n1_y);
        g2.setTransform(original_at);

        // needle
        g2.rotate(Math.toRadians(Math.round(n1_dial*200.0f)), prim_dial_x[pos], n1_y);
        g2.setColor(eicas_gc.markings_color);
        g2.drawLine(prim_dial_x[pos], n1_y, prim_dial_x[pos]+n1_r-2, n1_y);
        g2.setTransform(original_at);


        // value box
        if ( num < 5 ) {
            //g2.setColor(eicas_gc.markings_color);
            g2.setColor(eicas_gc.dim_markings_color);
            g2.drawRect(prim_dial_x[pos], n1_box_y - eicas_gc.dial_font_h[num]*140/100, eicas_gc.dial_font_w[num]*55/10, eicas_gc.dial_font_h[num]*140/100);
            if ( ( n1_dial <= 1.0f ) || this.inhibit ) {
                // inhibit caution or warning below 1000ft
                g2.setColor(eicas_gc.markings_color);
            } else if ( n1_dial < 1.1f ) {
                g2.setColor(eicas_gc.caution_color);
            } else {
                g2.setColor(eicas_gc.warning_color);
            }
            g2.setFont(eicas_gc.dial_font[num]);
            g2.drawString(n1_str, prim_dial_x[pos]+eicas_gc.dial_font_w[num]*51/10-eicas_gc.get_text_width(g2, eicas_gc.dial_font[num], n1_str), n1_box_y-eicas_gc.dial_font_h[num]*25/100-2);
        }


        // Reverser
        float rev = this.aircraft.reverser_position(pos);
//rev=1.0f;
        if ( rev > 0.0f ) {
            if ( rev == 1.0f ) {
                g2.setColor(eicas_gc.color_lime);
            } else {
                g2.setColor(eicas_gc.caution_color);
            }
            g2.drawString("REV", prim_dial_x[pos]+eicas_gc.dial_font_w[num], n1_box_y-eicas_gc.dial_font_h[num]*165/100-2);
        }

        // target N1 bug not for reverse
        if ( (rev==0.0f) ) {

            float ref_n1 = this.aircraft.get_ref_N1(pos);

            if ( ref_n1 > 0.0f ) {

if ( ref_n1 <= 1.0f ) {
    logger.warning("UFMC N1 is probably ratio, not percent");
    ref_n1 *= 100.0f;
}
                g2.setColor(eicas_gc.color_lime);
                g2.rotate(Math.toRadians(ref_n1*2.0f), prim_dial_x[pos], n1_y);
                g2.drawLine(prim_dial_x[pos]+n1_r+1, n1_y, prim_dial_x[pos]+n1_r+n1_r/10, n1_y);
                g2.drawLine(prim_dial_x[pos]+n1_r+n1_r/10, n1_y, prim_dial_x[pos]+n1_r+n1_r/10+n1_r/8, n1_y+n1_r/10);
                g2.drawLine(prim_dial_x[pos]+n1_r+n1_r/10, n1_y, prim_dial_x[pos]+n1_r+n1_r/10+n1_r/8, n1_y-n1_r/10);
                g2.setTransform(original_at);
                String ref_n1_str = one_decimal_format.format(ref_n1);
                g2.drawString(ref_n1_str, prim_dial_x[pos]+eicas_gc.dial_font_w[num]*51/10-eicas_gc.get_text_width(g2, eicas_gc.dial_font[num], ref_n1_str), n1_box_y-eicas_gc.dial_font_h[num]*165/100-2);

            }

        }

        resetPen(g2);

    }

    private void drawMAP(Graphics2D g2, int pos, int num) {

        AffineTransform original_at = g2.getTransform();
        scalePen(g2);

        float n1_value = this.aircraft.get_MPR(pos);
        float n1_dial = Math.min(n1_value, 44.0f) / 40.0f;
        String n1_str = one_decimal_format.format(n1_value);

        int n1_y = eicas_gc.dial_n1_y;
        int n1_r = eicas_gc.dial_r[num];
        int n1_box_y = n1_y - n1_r/8;

        if ( ( n1_dial <= 1.0f ) || this.inhibit ) {
            // inhibit caution or warning below 1000ft
            g2.setColor(eicas_gc.instrument_background_color);
        } else if ( n1_dial < 1.1f ) {
            g2.setColor(eicas_gc.caution_color.darker().darker());
        } else {
            g2.setColor(eicas_gc.warning_color.darker().darker());
        }
        g2.fillArc(prim_dial_x[pos]-n1_r, n1_y-n1_r, 2*n1_r, 2*n1_r, 0, -Math.round(n1_dial*200.0f));

        g2.setColor(eicas_gc.dim_markings_color);
//        for (int i=0; i<=10; i++) {
//            g2.drawLine(prim_dial_x[pos]+n1_r*14/16, n1_y, prim_dial_x[pos]+n1_r-1, n1_y);
//            g2.rotate(Math.toRadians(20), prim_dial_x[pos], n1_y);
//        }
        g2.setTransform(original_at);
        g2.drawArc(prim_dial_x[pos]-n1_r, n1_y-n1_r, 2*n1_r, 2*n1_r, 0, -200);
        g2.setColor(eicas_gc.caution_color);
        g2.drawArc(prim_dial_x[pos]-n1_r, n1_y-n1_r, 2*n1_r, 2*n1_r, -200, -20);
        g2.rotate(Math.toRadians(220), prim_dial_x[pos], n1_y);
        g2.setColor(eicas_gc.warning_color);
        g2.drawLine(prim_dial_x[pos]+n1_r, n1_y, prim_dial_x[pos]+n1_r*19/16, n1_y);
        g2.setTransform(original_at);

        // needle
        g2.rotate(Math.toRadians(Math.round(n1_dial*200.0f)), prim_dial_x[pos], n1_y);
        g2.setColor(eicas_gc.markings_color);
        g2.drawLine(prim_dial_x[pos], n1_y, prim_dial_x[pos]+n1_r-2, n1_y);
        g2.setTransform(original_at);


        // value box
        if ( num < 5 ) {
            //g2.setColor(eicas_gc.markings_color);
            g2.setColor(eicas_gc.dim_markings_color);
            g2.drawRect(prim_dial_x[pos], n1_box_y - eicas_gc.dial_font_h[num]*140/100, eicas_gc.dial_font_w[num]*55/10, eicas_gc.dial_font_h[num]*140/100);
            if ( ( n1_dial <= 1.0f ) || this.inhibit ) {
                // inhibit caution or warning below 1000ft
                g2.setColor(eicas_gc.markings_color);
            } else if ( n1_dial < 1.1f ) {
                g2.setColor(eicas_gc.caution_color);
            } else {
                g2.setColor(eicas_gc.warning_color);
            }
            g2.setFont(eicas_gc.dial_font[num]);
            g2.drawString(n1_str, prim_dial_x[pos]+eicas_gc.dial_font_w[num]*51/10-eicas_gc.get_text_width(g2, eicas_gc.dial_font[num], n1_str), n1_box_y-eicas_gc.dial_font_h[num]*25/100-2);
        }


        // Reverser
        float rev = this.aircraft.reverser_position(pos);
//rev=1.0f;
        if ( rev > 0.0f ) {
            if ( rev == 1.0f ) {
                g2.setColor(eicas_gc.color_lime);
            } else {
                g2.setColor(eicas_gc.caution_color);
            }
            g2.drawString("REV", prim_dial_x[pos]+eicas_gc.dial_font_w[num], n1_box_y-eicas_gc.dial_font_h[num]*165/100-2);
        }

        // target N1 bug not for reverse
        if ( (rev==0.0f) ) {

            float ref_n1 = this.aircraft.get_ref_N1(pos);

            if ( ref_n1 > 0.0f ) {

if ( ref_n1 <= 1.0f ) {
    logger.warning("UFMC N1 is probably ratio, not percent");
    ref_n1 *= 100.0f;
}
                g2.setColor(eicas_gc.color_lime);
                g2.rotate(Math.toRadians(ref_n1*2.0f), prim_dial_x[pos], n1_y);
                g2.drawLine(prim_dial_x[pos]+n1_r+1, n1_y, prim_dial_x[pos]+n1_r+n1_r/10, n1_y);
                g2.drawLine(prim_dial_x[pos]+n1_r+n1_r/10, n1_y, prim_dial_x[pos]+n1_r+n1_r/10+n1_r/8, n1_y+n1_r/10);
                g2.drawLine(prim_dial_x[pos]+n1_r+n1_r/10, n1_y, prim_dial_x[pos]+n1_r+n1_r/10+n1_r/8, n1_y-n1_r/10);
                g2.setTransform(original_at);
                String ref_n1_str = one_decimal_format.format(ref_n1);
                g2.drawString(ref_n1_str, prim_dial_x[pos]+eicas_gc.dial_font_w[num]*51/10-eicas_gc.get_text_width(g2, eicas_gc.dial_font[num], ref_n1_str), n1_box_y-eicas_gc.dial_font_h[num]*165/100-2);

            }

        }

        resetPen(g2);

    }

    private void drawTRQ(Graphics2D g2, int pos, int num) {

        AffineTransform original_at = g2.getTransform();
        scalePen(g2);

        float trq_value = Math.max(this.aircraft.get_TRQ(pos), 0.0f);
        float trq_max = this.aircraft.get_max_TRQ();
//trq_value=19500.0f;
//trq_max=10000.0f;
        float trq_dial = Math.min(trq_value/trq_max, 1.1f);
        // display TRQ in LbFt x100
//        String trq_str = Integer.toString(Math.round(trq_value/100.0f));
        String trq_str = one_decimal_format.format(trq_value/100.0f);
        int trq_y = eicas_gc.dial_n1_y;
        int trq_r = eicas_gc.dial_r[num];

        if ( trq_dial <= 1.0f ) {
            g2.setColor(eicas_gc.instrument_background_color);
        } else {
            g2.setColor(eicas_gc.warning_color.darker().darker());
        }
        g2.fillArc(prim_dial_x[pos]-trq_r, trq_y-trq_r, 2*trq_r, 2*trq_r, 0, -Math.round(trq_dial*200.0f));

        g2.setColor(eicas_gc.dim_markings_color);
        for (int i=0; i<=10; i++) {
            g2.drawLine(prim_dial_x[pos]+trq_r*14/16, trq_y, prim_dial_x[pos]+trq_r-1, trq_y);
            g2.rotate(Math.toRadians(20), prim_dial_x[pos], trq_y);
        }
        g2.setTransform(original_at);
        // arc 0..110
        g2.drawArc(prim_dial_x[pos]-trq_r, trq_y-trq_r, 2*trq_r, 2*trq_r, 0, -200);
        // redline at 100
        g2.setColor(eicas_gc.warning_color);
        g2.drawArc(prim_dial_x[pos]-trq_r, trq_y-trq_r, 2*trq_r, 2*trq_r, -200, -20);
        g2.rotate(Math.toRadians(200), prim_dial_x[pos], trq_y);
        g2.drawLine(prim_dial_x[pos]+trq_r, trq_y, prim_dial_x[pos]+trq_r*19/16, trq_y);
        g2.setTransform(original_at);

        // needle
        g2.rotate(Math.toRadians(Math.round(trq_dial*200.0f)), prim_dial_x[pos], trq_y);
        g2.setColor(eicas_gc.markings_color);
        g2.drawLine(prim_dial_x[pos], trq_y, prim_dial_x[pos]+trq_r-2, trq_y);
        g2.setTransform(original_at);

        // value box
        trq_y -= trq_r/8;
        if ( num < 5 ) {
            //g2.setColor(eicas_gc.markings_color);
            g2.setColor(eicas_gc.dim_markings_color);
            g2.drawRect(prim_dial_x[pos], trq_y - eicas_gc.dial_font_h[num]*140/100, eicas_gc.dial_font_w[num]*55/10, eicas_gc.dial_font_h[num]*140/100);
            if ( trq_dial <= 1.0f ) {
                g2.setColor(eicas_gc.markings_color);
            } else {
                g2.setColor(eicas_gc.warning_color);
            }
            g2.setFont(eicas_gc.dial_font[num]);
            g2.drawString(trq_str, prim_dial_x[pos]+eicas_gc.dial_font_w[num]*51/10-eicas_gc.get_text_width(g2, eicas_gc.dial_font[num], trq_str), trq_y-eicas_gc.dial_font_h[num]*25/100-2);
        }

        // Prop mode
        int prop_mode = this.aircraft.get_prop_mode(pos);
        String prop_mode_str = "";
//prop_mode=2;
        if ( prop_mode != 1 ) {
            if ( prop_mode == 0 ) {
                g2.setColor(eicas_gc.warning_color);
                prop_mode_str = "FTHR";
            } else if ( prop_mode == 2 ) {
                g2.setColor(eicas_gc.unusual_color);
                prop_mode_str = "BETA";
            } else if ( prop_mode == 3 ) {
                g2.setColor(eicas_gc.color_lime);
                prop_mode_str = "REV";
            }
            g2.drawString(prop_mode_str, prim_dial_x[pos]+eicas_gc.dial_font_w[num], trq_y-eicas_gc.dial_font_h[num]*165/100-2);
        }

        resetPen(g2);

    }

    
    private void drawEGT(Graphics2D g2, int pos, int num) {

        AffineTransform original_at = g2.getTransform();
        scalePen(g2);

        float egt_percent = this.aircraft.get_EGT_percent(pos);
        float egt_dial = Math.min(egt_percent, 110.0f) / 100.0f;
        int egt_value = Math.round(this.aircraft.get_EGT_value(pos));
//egt_value=500;

        int egt_x = prim_dial_x[pos];
        int egt_y = eicas_gc.dial_egt_y;
        int egt_r = eicas_gc.dial_r[num];

        if ( ( egt_dial <= 1.0f ) || this.inhibit ) {
            // inhibit caution or warning below 1000ft
            g2.setColor(eicas_gc.instrument_background_color);
        } else if ( egt_dial < 1.1f ) {
            g2.setColor(eicas_gc.caution_color.darker().darker());
        } else {
            g2.setColor(eicas_gc.warning_color.darker().darker());
        }
        g2.fillArc(egt_x-egt_r, egt_y-egt_r, 2*egt_r, 2*egt_r, 0, -Math.round(egt_dial*200.0f));

        g2.setColor(eicas_gc.dim_markings_color);
        g2.drawArc(egt_x-egt_r, egt_y-egt_r, 2*egt_r, 2*egt_r, 0, -200);
        g2.setColor(eicas_gc.caution_color);
        g2.drawArc(egt_x-egt_r, egt_y-egt_r, 2*egt_r, 2*egt_r, -200, -20);
        g2.rotate(Math.toRadians(220), egt_x, egt_y);
        g2.setColor(eicas_gc.warning_color);
        g2.drawLine(egt_x+egt_r, egt_y, egt_x+egt_r*19/16, egt_y);
        g2.setTransform(original_at);

        // needle
        g2.rotate(Math.toRadians(Math.round(egt_dial*200.0f)), egt_x, egt_y);
        g2.setColor(eicas_gc.markings_color);
        g2.drawLine(egt_x, egt_y, egt_x+egt_r-2, egt_y);
        g2.setTransform(original_at);

        // value box
        egt_y -= egt_r/8;
        if ( num < 5 ) {
            g2.setColor(eicas_gc.dim_markings_color);
            g2.drawRect(egt_x, egt_y - eicas_gc.dial_font_h[num]*140/100, eicas_gc.dial_font_w[num]*47/10, eicas_gc.dial_font_h[num]*140/100);
            if ( ( egt_dial <= 1.0f ) || this.inhibit ) {
                // inhibit caution or warning below 1000ft
                g2.setColor(eicas_gc.markings_color);
            } else if ( egt_dial < 1.1f ) {
                g2.setColor(eicas_gc.caution_color);
            } else {
                g2.setColor(eicas_gc.warning_color);
            }
            g2.setFont(eicas_gc.dial_font[num]);
            String egt_str = Integer.toString(egt_value);
            g2.drawString(egt_str, egt_x+eicas_gc.dial_font_w[num]*44/10-eicas_gc.get_text_width(g2, eicas_gc.dial_font[num], egt_str), egt_y-eicas_gc.dial_font_h[num]*25/100-2);
        }

        resetPen(g2);

    }
    
    private void drawITT(Graphics2D g2, int pos, int num) {

        AffineTransform original_at = g2.getTransform();
        scalePen(g2);

        float itt_percent = this.aircraft.get_ITT_percent(pos);
//itt_percent=105;
        float itt_dial = Math.min(itt_percent, 110.0f) / 100.0f;
        int itt_value = Math.round(this.aircraft.get_ITT_value(pos));
//itt_value=666;

        int itt_x = prim_dial_x[pos];
        int itt_y = eicas_gc.dial_egt_y;
        int itt_r = eicas_gc.dial_r[num];

        if ( itt_dial <= 1.0f ) {
            g2.setColor(eicas_gc.instrument_background_color);
        } else {
            g2.setColor(eicas_gc.warning_color.darker().darker());
        }
        g2.fillArc(itt_x-itt_r, itt_y-itt_r, 2*itt_r, 2*itt_r, 0, -Math.round(itt_dial*200.0f));

        g2.setColor(eicas_gc.dim_markings_color);
        g2.drawArc(itt_x-itt_r, itt_y-itt_r, 2*itt_r, 2*itt_r, 0, -200);
        g2.setColor(eicas_gc.warning_color);
        g2.drawArc(itt_x-itt_r, itt_y-itt_r, 2*itt_r, 2*itt_r, -200, -20);
        g2.rotate(Math.toRadians(200), itt_x, itt_y);
        g2.drawLine(itt_x+itt_r, itt_y, itt_x+itt_r*19/16, itt_y);
        g2.setTransform(original_at);

        // needle
        g2.rotate(Math.toRadians(Math.round(itt_dial*200.0f)), itt_x, itt_y);
        g2.setColor(eicas_gc.markings_color);
        g2.drawLine(itt_x, itt_y, itt_x+itt_r-2, itt_y);
        g2.setTransform(original_at);

        // value box
        itt_y -= itt_r/8;
        if ( num < 5 ) {
            g2.setColor(eicas_gc.dim_markings_color);
            g2.drawRect(itt_x, itt_y - eicas_gc.dial_font_h[num]*140/100, eicas_gc.dial_font_w[num]*47/10, eicas_gc.dial_font_h[num]*140/100);
            if ( itt_dial <= 1.0f ) {
                g2.setColor(eicas_gc.markings_color);
            } else {
                g2.setColor(eicas_gc.warning_color);
            }
            g2.setFont(eicas_gc.dial_font[num]);
            String itt_str = Integer.toString(itt_value);
            g2.drawString(itt_str, itt_x+eicas_gc.dial_font_w[num]*44/10-eicas_gc.get_text_width(g2, eicas_gc.dial_font[num], itt_str), itt_y-eicas_gc.dial_font_h[num]*25/100-2);
        }

        resetPen(g2);

    }
    

    private void drawN2(Graphics2D g2, int pos, int num) {

        AffineTransform original_at = g2.getTransform();
        scalePen(g2);

        float n2_value = this.aircraft.get_N2(pos);
//n1_test += 0.1f;
//if (n1_test > 123.0f) n1_test = 66.0f;
//ff_value=n1_test;
        float n2_dial = Math.min(n2_value, 110.0f) / 100.0f;

        int n2_y = eicas_gc.dial_n2_y;
        int n2_r = eicas_gc.dial_r[num];

        if ( ( n2_dial <= 1.0f ) || this.inhibit ) {
            // inhibit caution or warning below 1000ft
            g2.setColor(eicas_gc.instrument_background_color);
        } else if ( n2_dial < 1.1f ) {
            g2.setColor(eicas_gc.caution_color.darker().darker());
        } else {
            g2.setColor(eicas_gc.warning_color.darker().darker());
        }
        g2.fillArc(prim_dial_x[pos]-n2_r, n2_y-n2_r, 2*n2_r, 2*n2_r, 0, -Math.round(n2_dial*200.0f));

        g2.setColor(eicas_gc.dim_markings_color);
        g2.drawArc(prim_dial_x[pos]-n2_r, n2_y-n2_r, 2*n2_r, 2*n2_r, 0, -200);
        g2.setColor(eicas_gc.caution_color);
        g2.drawArc(prim_dial_x[pos]-n2_r, n2_y-n2_r, 2*n2_r, 2*n2_r, -200, -20);
        g2.rotate(Math.toRadians(220), prim_dial_x[pos], n2_y);
        g2.setColor(eicas_gc.warning_color);
        g2.drawLine(prim_dial_x[pos]+n2_r, n2_y, prim_dial_x[pos]+n2_r*19/16, n2_y);
        g2.setTransform(original_at);

        //needle
        g2.rotate(Math.toRadians(Math.round(n2_dial*200.0f)), prim_dial_x[pos], n2_y);
        g2.setColor(eicas_gc.markings_color);
        g2.drawLine(prim_dial_x[pos], n2_y, prim_dial_x[pos]+n2_r-2, n2_y);
        g2.setTransform(original_at);

        // value box
        n2_y -= n2_r/8;
        if ( num < 5 ) {
            g2.setColor(eicas_gc.dim_markings_color);
            g2.drawRect(prim_dial_x[pos], n2_y - eicas_gc.dial_font_h[num]*140/100, eicas_gc.dial_font_w[num]*55/10, eicas_gc.dial_font_h[num]*140/100);
            if ( ( n2_dial <= 1.0f ) || this.inhibit ) {
                // inhibit caution or warning below 1000ft
                g2.setColor(eicas_gc.markings_color);
            } else if ( n2_dial < 1.1f ) {
                g2.setColor(eicas_gc.caution_color);
            } else {
                g2.setColor(eicas_gc.warning_color);
            }
            g2.setFont(eicas_gc.dial_font[num]);
            String n2_str = one_decimal_format.format(n2_value);
            g2.drawString(n2_str, prim_dial_x[pos]+eicas_gc.dial_font_w[num]*51/10-eicas_gc.get_text_width(g2, eicas_gc.dial_font[num], n2_str), n2_y-eicas_gc.dial_font_h[num]*25/100-2);
        }

        resetPen(g2);

    }


    private void drawPROP(Graphics2D g2, int pos, int num) {

        AffineTransform original_at = g2.getTransform();
        scalePen(g2);

        float rpm_max = this.aircraft.get_max_prop_RPM();
//rpm_max=2000.0f;
        float rpm_value = this.aircraft.get_prop_RPM(pos);
//rpm_value=2399.0f;
        float rpm_dial = Math.min(rpm_value/rpm_max, 1.1f);

        int rpm_y = eicas_gc.dial_n2_y;
        int rpm_r = eicas_gc.dial_r[num];

        if ( rpm_dial <= 1.0f ) {
            g2.setColor(eicas_gc.instrument_background_color);
        } else {
            g2.setColor(eicas_gc.warning_color.darker().darker());
        }
        g2.fillArc(prim_dial_x[pos]-rpm_r, rpm_y-rpm_r, 2*rpm_r, 2*rpm_r, 0, -Math.round(rpm_dial*200.0f));

        g2.setColor(eicas_gc.dim_markings_color);
        g2.drawArc(prim_dial_x[pos]-rpm_r, rpm_y-rpm_r, 2*rpm_r, 2*rpm_r, 0, -200);
        g2.setColor(eicas_gc.warning_color);
        g2.drawArc(prim_dial_x[pos]-rpm_r, rpm_y-rpm_r, 2*rpm_r, 2*rpm_r, -200, -20);
        g2.rotate(Math.toRadians(200), prim_dial_x[pos], rpm_y);
        g2.drawLine(prim_dial_x[pos]+rpm_r, rpm_y, prim_dial_x[pos]+rpm_r*19/16, rpm_y);
        g2.setTransform(original_at);

        //needle
        g2.rotate(Math.toRadians(Math.round(rpm_dial*200.0f)), prim_dial_x[pos], rpm_y);
        g2.setColor(eicas_gc.markings_color);
        g2.drawLine(prim_dial_x[pos], rpm_y, prim_dial_x[pos]+rpm_r-2, rpm_y);
        g2.setTransform(original_at);

        // value box
        rpm_y -= rpm_r/8;
        if ( num < 5 ) {
            g2.setColor(eicas_gc.dim_markings_color);
            g2.drawRect(prim_dial_x[pos], rpm_y - eicas_gc.dial_font_h[num]*140/100, eicas_gc.dial_font_w[num]*55/10, eicas_gc.dial_font_h[num]*140/100);
            if ( rpm_dial <= 1.0f ) {
                g2.setColor(eicas_gc.markings_color);
            } else {
                g2.setColor(eicas_gc.warning_color);
            }
            g2.setFont(eicas_gc.dial_font[num]);
            String rpm_str = Integer.toString(Math.round(rpm_value));
            g2.drawString(rpm_str, prim_dial_x[pos]+eicas_gc.dial_font_w[num]*51/10-eicas_gc.get_text_width(g2, eicas_gc.dial_font[num], rpm_str), rpm_y-eicas_gc.dial_font_h[num]*25/100-2);
        }

        resetPen(g2);

    }


    private void drawFF(Graphics2D g2, int pos, int num) {

        AffineTransform original_at = g2.getTransform();
        scalePen(g2);

        // convert FF from kg/s to kg/h, lbs/h, usg/h or ltr/h
        float unit_multiplier = this.preferences.get_fuel_multiplier();
        float ff_value = this.aircraft.get_FF(pos) * 3600 * unit_multiplier;
        float ff_max = this.aircraft.get_max_FF() * 3600 * unit_multiplier;
//n1_test *= 1.1f;
//if (n1_test > 12345.0f) n1_test = 66.0f;
//ff_value= pos==1 ? 5000 : 50000;
//ff_max=99000.0f;
        float ff_dial = ff_value / ff_max;

        int ff_y = eicas_gc.dial_ff_y;
        int ff_r = eicas_gc.dial_r[num] * 85 / 100;

        g2.setColor(eicas_gc.instrument_background_color);
        g2.fillArc(prim_dial_x[pos]-ff_r, ff_y-ff_r, 2*ff_r, 2*ff_r, 0, -Math.round(ff_dial*200.0f));

        g2.setColor(eicas_gc.dim_markings_color);
        g2.drawArc(prim_dial_x[pos]-ff_r, ff_y-ff_r, 2*ff_r, 2*ff_r, 0, -220);
        g2.rotate(Math.toRadians(220), prim_dial_x[pos], ff_y);
        g2.drawLine(prim_dial_x[pos]+ff_r, ff_y, prim_dial_x[pos]+ff_r*19/16, ff_y);
        g2.setTransform(original_at);

        // needle
        g2.rotate(Math.toRadians(Math.round(ff_dial*200.0f)), prim_dial_x[pos], ff_y);
        g2.setColor(eicas_gc.markings_color);
        g2.drawLine(prim_dial_x[pos], ff_y, prim_dial_x[pos]+ff_r-2, ff_y);
        g2.setTransform(original_at);

        // value box
        ff_y -= ff_r/8;
        if ( num < 5 ) {
            g2.setColor(eicas_gc.dim_markings_color);
            g2.drawRect(prim_dial_x[pos], ff_y - eicas_gc.dial_font_h[num]*140/100, eicas_gc.dial_font_w[num]*55/10, eicas_gc.dial_font_h[num]*140/100);
            g2.setColor(eicas_gc.markings_color);
            g2.setFont(eicas_gc.dial_font[num]);
            if ( ff_max > 9999.9f ) {
                ff_value /= 1000.0f;
            }
            String ff_str;
//            if ( ff_value > 999.9f ) {
//                ff_str = "" + Math.round(ff_value);
//            } else if ( ff_value > 99.9f ) {
//                ff_str = one_decimal_format.format(ff_value);
//            } else {
//                ff_str = two_decimals_format.format(ff_value);
//            }
            if ( ff_value > 99.9f ) {
                ff_str = Integer.toString( Math.round(ff_value) );
            } else {
                ff_str = one_decimal_format.format(ff_value);
            }
            g2.drawString(ff_str, prim_dial_x[pos]+eicas_gc.dial_font_w[num]*51/10-eicas_gc.get_text_width(g2, eicas_gc.dial_font[num], ff_str), ff_y-eicas_gc.dial_font_h[num]*25/100-2);
        }

        resetPen(g2);

    }


    private void drawOilP(Graphics2D g2, int pos, int num) {

        AffineTransform original_at = g2.getTransform();
        scalePen(g2);

        float oil_p_dial = this.aircraft.get_oil_press_ratio(pos);
//egt_test++;
//egt_test++;
//if (egt_test > 2345) egt_test = 666;
//egt_value=egt_test;

        int oil_p_x = seco_dial_x[pos];
        int oil_p_y = eicas_gc.dial_oil_p_y;
        int oil_p_r = eicas_gc.dial_r[num] * 70 /100;

        g2.setColor(eicas_gc.dim_markings_color);
        g2.drawArc(oil_p_x-oil_p_r, oil_p_y-oil_p_r, 2*oil_p_r, 2*oil_p_r, -30-90, -300+90);
        g2.setColor(eicas_gc.caution_color);
        g2.drawArc(oil_p_x-oil_p_r, oil_p_y-oil_p_r, 2*oil_p_r, 2*oil_p_r, -30-45, -45);
        g2.setColor(eicas_gc.warning_color);
        g2.drawArc(oil_p_x-oil_p_r, oil_p_y-oil_p_r, 2*oil_p_r, 2*oil_p_r, -30, -45);

        // needle
        g2.rotate(Math.toRadians( Math.round(oil_p_dial*300.0f) + 30 ), oil_p_x, oil_p_y);
        g2.setColor(eicas_gc.markings_color);
        g2.drawLine(oil_p_x, oil_p_y, oil_p_x+oil_p_r-2, oil_p_y);
        g2.setTransform(original_at);

        resetPen(g2);

    }


    private void drawOilT(Graphics2D g2, int pos, int num) {

        AffineTransform original_at = g2.getTransform();
        scalePen(g2);

        float oil_t_dial = this.aircraft.get_oil_temp_ratio(pos);
//egt_test++;
//egt_test++;
//if (egt_test > 2345) egt_test = 666;
//egt_value=egt_test;

        int oil_t_x = seco_dial_x[pos];
        int oil_t_y = eicas_gc.dial_oil_t_y;
        int oil_t_r = eicas_gc.dial_r[num] * 70 /100;

        g2.setColor(eicas_gc.dim_markings_color);
        g2.drawArc(oil_t_x-oil_t_r, oil_t_y-oil_t_r, 2*oil_t_r, 2*oil_t_r, -45, -180);
        g2.setColor(eicas_gc.caution_color);
        g2.drawArc(oil_t_x-oil_t_r, oil_t_y-oil_t_r, 2*oil_t_r, 2*oil_t_r, -45-180, -15);
        g2.setColor(eicas_gc.warning_color);
        g2.drawArc(oil_t_x-oil_t_r, oil_t_y-oil_t_r, 2*oil_t_r, 2*oil_t_r, -45-180-15, -45);

        // needle
        g2.rotate(Math.toRadians( Math.round(oil_t_dial*240.0f) + 45), oil_t_x, oil_t_y);
        g2.setColor(eicas_gc.markings_color);
        g2.drawLine(oil_t_x, oil_t_y, oil_t_x+oil_t_r-2, oil_t_y);
        g2.setTransform(original_at);

        resetPen(g2);

    }


    private void drawOilQ(Graphics2D g2, int pos) {

        int oil_q_val = Math.round( this.aircraft.get_oil_quant_ratio(pos) * 100.0f );
        String oil_q_str = "" + oil_q_val;

        int oil_q_x = seco_dial_x[pos] - eicas_gc.get_text_width(g2, eicas_gc.font_l, oil_q_str)/2;
        int oil_q_y = eicas_gc.dial_oil_q_y + eicas_gc.line_height_l/2;

        g2.setColor(eicas_gc.markings_color);
        g2.setFont(eicas_gc.font_l);
        g2.drawString(oil_q_str, oil_q_x, oil_q_y);

    }


    private void drawVIB(Graphics2D g2, int pos, int num) {

        AffineTransform original_at = g2.getTransform();
        scalePen(g2);

        float vib_dial = this.aircraft.get_vib(pos) / 100.0f;
//vib_dial = 80.0f / 100.0f;

        int vib_x = seco_dial_x[pos];
        int vib_y = eicas_gc.dial_vib_y;
        int vib_r = eicas_gc.dial_r[num] * 70 /100;

        g2.setColor(eicas_gc.instrument_background_color);
        g2.fillArc(vib_x-vib_r, vib_y-vib_r, 2*vib_r, 2*vib_r, -120, -Math.round(vib_dial*270.0f));

        g2.setColor(eicas_gc.dim_markings_color);
        g2.drawArc(vib_x-vib_r, vib_y-vib_r, 2*vib_r, 2*vib_r, -120, -270);

        // needle
        g2.rotate(Math.toRadians( Math.round(vib_dial*270.0f) + 120 ), vib_x, vib_y);
        g2.setColor(eicas_gc.markings_color);
        g2.drawLine(vib_x, vib_y, vib_x+vib_r-2, vib_y);
        g2.setTransform(original_at);

        resetPen(g2);

    }


    private void drawNG(Graphics2D g2, int pos, int num) {

        AffineTransform original_at = g2.getTransform();
        scalePen(g2);

        float ng_dial = this.aircraft.get_NG(pos) / 100.0f;
//ng_dial = 80.0f / 100.0f;

        int ng_x;
        int ng_y;
        int ng_r = eicas_gc.dial_r[num] * 70 /100;
        if ( this.preferences.get_eicas_primary() ) {
            // left column
            ng_x = prim_dial_x[pos];
            ng_y = eicas_gc.dial_ng_y;
        } else {
            // right column
            ng_x = seco_dial_x[pos];
            ng_y = eicas_gc.dial_vib_y;
        }

        if ( ng_dial <= 1.0f ) {
            g2.setColor(eicas_gc.instrument_background_color);
        } else {
            g2.setColor(eicas_gc.warning_color.darker().darker());
        }
        g2.fillArc(ng_x-ng_r, ng_y-ng_r, 2*ng_r, 2*ng_r, 0, -Math.round(ng_dial*200.0f));

        g2.setColor(eicas_gc.dim_markings_color);
        g2.drawArc(ng_x-ng_r, ng_y-ng_r, 2*ng_r, 2*ng_r, 0, -200);
        g2.setColor(eicas_gc.warning_color);
        g2.drawArc(ng_x-ng_r, ng_y-ng_r, 2*ng_r, 2*ng_r, -200, -20);
        g2.rotate(Math.toRadians(200), ng_x, ng_y);
        g2.drawLine(ng_x+ng_r, ng_y, ng_x+ng_r*19/16, ng_y);
        g2.setTransform(original_at);

        //needle
        g2.rotate(Math.toRadians(Math.round(ng_dial*200.0f)), ng_x, ng_y);
        g2.setColor(eicas_gc.markings_color);
        g2.drawLine(ng_x, ng_y, ng_x+ng_r-2, ng_y);
        g2.setTransform(original_at);

        resetPen(g2);

    }


    private void scalePen(Graphics2D g2) {

        original_stroke = g2.getStroke();
        g2.setStroke(new BasicStroke(2.5f * eicas_gc.grow_scaling_factor, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));

    }


    private void resetPen(Graphics2D g2) {

        g2.setStroke(original_stroke);

    }


}
