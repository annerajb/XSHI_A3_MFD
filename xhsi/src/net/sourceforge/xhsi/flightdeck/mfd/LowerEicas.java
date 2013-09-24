/**
* LowerEicas.java
* 
* Lower EICAS
* 
* Copyright (C) 2011  Marc Rogiers (marrog.123@gmail.com)
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
package net.sourceforge.xhsi.flightdeck.mfd;

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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import java.util.logging.Logger;

import net.sourceforge.xhsi.XHSIPreferences;
import net.sourceforge.xhsi.XHSISettings;

import net.sourceforge.xhsi.model.Airport;
import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ComRadio;
import net.sourceforge.xhsi.model.FMS;
import net.sourceforge.xhsi.model.FMSEntry;
import net.sourceforge.xhsi.model.Localizer;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.NavigationObjectRepository;
import net.sourceforge.xhsi.model.Runway;



public class LowerEicas extends MFDSubcomponent {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    private Stroke original_stroke;

    private boolean inhibit;

    private DecimalFormat one_decimal_format;
    private DecimalFormat two_decimals_format;
    private DecimalFormatSymbols format_symbols;

    private int dial_x[] = new int[8];
    private int tape_x[] = new int[8];
    

    public LowerEicas(ModelFactory model_factory, MFDGraphicsConfig hsi_gc, Component parent_component) {
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
        
        if ( mfd_gc.powered && ( this.avionics.get_mfd_mode() == Avionics.MFD_MODE_EICAS ) && ( this.aircraft.num_engines() > 0 ) ) {
            
            this.inhibit = ( this.aircraft.agl_m() < 1000.0f / 3.28084f );

            boolean turboprop = this.preferences.get_preference(XHSIPreferences.PREF_ENGINE_TYPE).equals(XHSIPreferences.ENGINE_TYPE_TRQ);
            
            int num_eng = this.aircraft.num_engines();
//num_eng = 4;
            int cols = Math.max(num_eng, 2);
            if ( cols == 2 ) {
                dial_x[0] = mfd_gc.panel_rect.x + mfd_gc.panel_rect.width*30/100;
                tape_x[0] = dial_x[0] + mfd_gc.dial_r[2]/2;
                dial_x[1] = mfd_gc.panel_rect.x + mfd_gc.panel_rect.width*70/100;
                tape_x[1] = dial_x[1] - mfd_gc.dial_r[2]/2;
            } else {
                for (int i=0; i<cols; i++) {
                    dial_x[i] = mfd_gc.panel_rect.x + mfd_gc.panel_rect.width*50/100/cols + i*mfd_gc.panel_rect.width*9/10/cols;
                    tape_x[i] = dial_x[i] - mfd_gc.dial_r[cols]/2;
                }
            }

            for (int i=0; i<num_eng; i++) {

                if ( turboprop ) {
                    drawNG(g2, i, num_eng);
                } else {
                    drawN2(g2, i, num_eng);
                }
                drawFF(g2, i, num_eng);
                drawOilP(g2, i, num_eng);
                drawOilT(g2, i, num_eng);
                drawOilQ(g2, i, num_eng);
                drawVIB(g2, i, num_eng);

                String ind_str;
                int ind_x;
                g2.setColor(mfd_gc.color_boeingcyan);
                g2.setFont(mfd_gc.font_m);
                // N2 or NG
                ind_str = turboprop ? "NG" : "N2";
                if ( cols == 2 ) {
                    ind_x = (dial_x[0] + dial_x[1]) / 2 - mfd_gc.get_text_width(g2, mfd_gc.font_m, ind_str)/2;
                } else {
                    ind_x = dial_x[num_eng-1] + mfd_gc.dial_r[num_eng]*245/100 - mfd_gc.get_text_width(g2, mfd_gc.font_m, ind_str);
                }
                g2.drawString(ind_str, ind_x, mfd_gc.dial_n2_y + mfd_gc.dial_r[2]);
                // FF
                if ( num_eng < 5 ) {
                    ind_str = "FF";
                    if ( cols == 2 ) {
                        ind_x = (dial_x[0] + dial_x[1]) / 2 - mfd_gc.get_text_width(g2, mfd_gc.font_m, ind_str)/2;
                    } else {
                        ind_x = dial_x[num_eng-1] + mfd_gc.dial_r[num_eng]*245/100 - mfd_gc.get_text_width(g2, mfd_gc.font_m, ind_str);
                    }
                    g2.drawString(ind_str, ind_x, mfd_gc.dial_ff_y + mfd_gc.line_height_m*5/8);
                }
                // OIL P
                ind_str = "OIL P";
                if ( cols == 2 ) {
                    ind_x = (tape_x[0] + tape_x[1]) / 2 - mfd_gc.get_text_width(g2, mfd_gc.font_m, ind_str)/2;
                } else {
                    ind_x = dial_x[num_eng-1] + mfd_gc.dial_r[num_eng]*245/100 - mfd_gc.get_text_width(g2, mfd_gc.font_m, ind_str);
                }
                g2.drawString(ind_str, ind_x, mfd_gc.dial_oilp_y + mfd_gc.line_height_m*5/8);
                // OIL T
                ind_str = "OIL T";
                if ( cols == 2 ) {
                    ind_x = (tape_x[0] + tape_x[1]) / 2 - mfd_gc.get_text_width(g2, mfd_gc.font_m, ind_str)/2;
                } else {
                    ind_x = dial_x[num_eng-1] + mfd_gc.dial_r[num_eng]*245/100 - mfd_gc.get_text_width(g2, mfd_gc.font_m, ind_str);
                }
                g2.drawString(ind_str, ind_x, mfd_gc.dial_oilt_y + mfd_gc.line_height_m*5/8);
                // OIL Q
                if ( num_eng < 5 ) {
                    ind_str = "OIL Q";
                    if ( cols == 2 ) {
                        ind_x = (tape_x[0] + tape_x[1]) / 2 - mfd_gc.get_text_width(g2, mfd_gc.font_m, ind_str)/2;
                    } else {
                        ind_x = dial_x[num_eng-1] + mfd_gc.dial_r[num_eng]*245/100 - mfd_gc.get_text_width(g2, mfd_gc.font_m, ind_str);
                    }
                    g2.drawString(ind_str, ind_x, mfd_gc.dial_oilq_y + mfd_gc.line_height_m*5/8);
                }
                // VIB
                ind_str = "VIB";
                if ( cols == 2 ) {
                    ind_x = (tape_x[0] + tape_x[1]) / 2 - mfd_gc.get_text_width(g2, mfd_gc.font_m, ind_str)/2;
                } else {
                    ind_x = dial_x[num_eng-1] + mfd_gc.dial_r[num_eng]*245/100 - mfd_gc.get_text_width(g2, mfd_gc.font_m, ind_str);
                }
                g2.drawString(ind_str, ind_x, mfd_gc.dial_vib_y + mfd_gc.line_height_m*5/8);

            }

        }

    }

    private void drawN2(Graphics2D g2, int pos, int num) {

        AffineTransform original_at = g2.getTransform();
        scalePen(g2);

        float n2_value = this.aircraft.get_N2(pos);
        float n2_dial = Math.min(n2_value, 110.0f) / 100.0f;

        int n2_y = mfd_gc.dial_n2_y;
        int n2_r = mfd_gc.dial_r[num];

        if ( ( n2_dial <= 1.0f ) || this.inhibit ) {
            // inhibit caution or warning below 1000ft
            g2.setColor(mfd_gc.instrument_background_color);
        } else if ( n2_dial < 1.1f ) {
            g2.setColor(mfd_gc.caution_color.darker().darker());
        } else {
            g2.setColor(mfd_gc.warning_color.darker().darker());
        }
        g2.fillArc(dial_x[pos]-n2_r, n2_y-n2_r, 2*n2_r, 2*n2_r, 0, -Math.round(n2_dial*200.0f));

        g2.setColor(mfd_gc.dim_markings_color);
        g2.drawArc(dial_x[pos]-n2_r, n2_y-n2_r, 2*n2_r, 2*n2_r, 0, -200);
        g2.setColor(mfd_gc.caution_color);
        g2.drawArc(dial_x[pos]-n2_r, n2_y-n2_r, 2*n2_r, 2*n2_r, -200, -20);
        g2.rotate(Math.toRadians(220), dial_x[pos], n2_y);
        g2.setColor(mfd_gc.warning_color);
        g2.drawLine(dial_x[pos]+n2_r, n2_y, dial_x[pos]+n2_r*19/16, n2_y);
        g2.setTransform(original_at);

        //needle
        g2.rotate(Math.toRadians(Math.round(n2_dial*200.0f)), dial_x[pos], n2_y);
        g2.setColor(mfd_gc.markings_color);
        g2.drawLine(dial_x[pos], n2_y, dial_x[pos]+n2_r-2, n2_y);
        g2.setTransform(original_at);

        // value box
        n2_y -= n2_r/8;
        if ( num < 5 ) {
            g2.setColor(mfd_gc.dim_markings_color);
            g2.drawRect(dial_x[pos], n2_y - mfd_gc.dial_font_h[num]*140/100, mfd_gc.dial_font_w[num]*55/10, mfd_gc.dial_font_h[num]*140/100);
            if ( ( n2_dial <= 1.0f ) || this.inhibit ) {
                // inhibit caution or warning below 1000ft
                g2.setColor(mfd_gc.markings_color);
            } else if ( n2_dial < 1.1f ) {
                g2.setColor(mfd_gc.caution_color);
            } else {
                g2.setColor(mfd_gc.warning_color);
            }
            g2.setFont(mfd_gc.dial_font[num]);
            String n2_str = one_decimal_format.format(n2_value);
            g2.drawString(n2_str, dial_x[pos]+mfd_gc.dial_font_w[num]*51/10-mfd_gc.get_text_width(g2, mfd_gc.dial_font[num], n2_str), n2_y-mfd_gc.dial_font_h[num]*25/100-2);
        }

        resetPen(g2);

    }


    private void drawNG(Graphics2D g2, int pos, int num) {

        AffineTransform original_at = g2.getTransform();
        scalePen(g2);

        float ng_value = this.aircraft.get_NG(pos);
        float ng_dial = Math.min(ng_value, 110.0f) / 100.0f;

        int ng_y = mfd_gc.dial_ng_y;
        int ng_r = mfd_gc.dial_r[num];

//        if ( ( ng_dial <= 1.0f ) || this.inhibit ) {
//            // inhibit caution or warning below 1000ft
//            g2.setColor(mfd_gc.instrument_background_color);
//        } else if ( ng_dial < 1.1f ) {
//            g2.setColor(mfd_gc.caution_color.darker().darker());
//        } else {
//            g2.setColor(mfd_gc.warning_color.darker().darker());
//        }
        g2.setColor(mfd_gc.instrument_background_color);
        g2.fillArc(dial_x[pos]-ng_r, ng_y-ng_r, 2*ng_r, 2*ng_r, 0, -Math.round(ng_dial*200.0f));

        g2.setColor(mfd_gc.dim_markings_color);
        g2.drawArc(dial_x[pos]-ng_r, ng_y-ng_r, 2*ng_r, 2*ng_r, 0, -200);
        g2.setColor(mfd_gc.caution_color);
        g2.drawArc(dial_x[pos]-ng_r, ng_y-ng_r, 2*ng_r, 2*ng_r, -200, -20);
        g2.rotate(Math.toRadians(220), dial_x[pos], ng_y);
        g2.setColor(mfd_gc.warning_color);
        g2.drawLine(dial_x[pos]+ng_r, ng_y, dial_x[pos]+ng_r*19/16, ng_y);
        g2.setTransform(original_at);

        //needle
        g2.rotate(Math.toRadians(Math.round(ng_dial*200.0f)), dial_x[pos], ng_y);
        g2.setColor(mfd_gc.markings_color);
        g2.drawLine(dial_x[pos], ng_y, dial_x[pos]+ng_r-2, ng_y);
        g2.setTransform(original_at);

        // value box
        ng_y -= ng_r/8;
        if ( num < 5 ) {
            g2.setColor(mfd_gc.dim_markings_color);
            g2.drawRect(dial_x[pos], ng_y - mfd_gc.dial_font_h[num]*140/100, mfd_gc.dial_font_w[num]*55/10, mfd_gc.dial_font_h[num]*140/100);
            if ( ( ng_dial <= 1.0f ) || this.inhibit ) {
                // inhibit caution or warning below 1000ft
                g2.setColor(mfd_gc.markings_color);
            } else if ( ng_dial < 1.1f ) {
                g2.setColor(mfd_gc.caution_color);
            } else {
                g2.setColor(mfd_gc.warning_color);
            }
            g2.setFont(mfd_gc.dial_font[num]);
            String ng_str = one_decimal_format.format(ng_value);
            g2.drawString(ng_str, dial_x[pos]+mfd_gc.dial_font_w[num]*51/10-mfd_gc.get_text_width(g2, mfd_gc.dial_font[num], ng_str), ng_y-mfd_gc.dial_font_h[num]*25/100-2);
        }

        resetPen(g2);

    }


    private void drawFF(Graphics2D g2, int pos, int num) {

        scalePen(g2);

        boolean mirror = (pos==1) && (num==2);

        // convert FF from kg/s to kg/h, lbs/h, usg/h or ltr/h
        float unit_multiplier = this.preferences.get_fuel_multiplier();
        float ff_value = this.aircraft.get_FF(pos) * 3600 * unit_multiplier;
        float ff_max = this.aircraft.get_max_FF() * 3600 * unit_multiplier;

        int ff_y = mfd_gc.dial_ff_y + mfd_gc.dial_font_h[num]*5/8;
        int offset_x = mirror ? 0 : -mfd_gc.dial_font_w[num]*55/10;

        // value box
        if ( num < 5 ) {
            g2.setColor(mfd_gc.dim_markings_color);
            g2.drawRect(tape_x[pos] + offset_x,
                    ff_y - mfd_gc.dial_font_h[num]*140/100,
                    mfd_gc.dial_font_w[num]*55/10,
                    mfd_gc.dial_font_h[num]*140/100);
            g2.setColor(mfd_gc.markings_color);
            g2.setFont(mfd_gc.dial_font[num]);
            if ( ff_max > 9999.9f ) {
                ff_value /= 1000.0f;
            }
            String ff_str;
            if ( ff_value > 99.9f ) {
                ff_str = Integer.toString( Math.round(ff_value) );
            } else {
                ff_str = one_decimal_format.format(ff_value);
            }
            g2.drawString(ff_str,
                    tape_x[pos] + offset_x + mfd_gc.dial_font_w[num]*51/10 - mfd_gc.get_text_width(g2, mfd_gc.dial_font[num], ff_str),
                    ff_y-mfd_gc.dial_font_h[num]*25/100-2);
        }

        resetPen(g2);

    }

    
    private void drawOilP(Graphics2D g2, int pos, int num) {

        scalePen(g2);

        boolean mirror = (pos==1) && (num==2);

        float oilp_dial = this.aircraft.get_oil_press_ratio(pos);

        int oilp_x = tape_x[pos];
        int oilp_y = mfd_gc.dial_oilp_y;
        int oilp_h = mfd_gc.tape_h;

        g2.setColor(mfd_gc.dim_markings_color);
        g2.drawLine(oilp_x, oilp_y - oilp_h/2, oilp_x, oilp_y + oilp_h/2);
        g2.setColor(mfd_gc.caution_color);
        g2.drawLine(oilp_x, oilp_y + oilp_h*3/10, oilp_x + (mirror ? -oilp_h/20 : oilp_h/20), oilp_y + oilp_h*3/10);
        g2.setColor(mfd_gc.warning_color);
        g2.drawLine(oilp_x - oilp_h/15, oilp_y + oilp_h/2, oilp_x + oilp_h/15, oilp_y + oilp_h/2);

        // arrow
        int a_x[] = {
            oilp_x,
            oilp_x + (mirror ? -oilp_h/4 : oilp_h/4),
            oilp_x + (mirror ? -oilp_h/4 : oilp_h/4)
        };
        int oilp_a = oilp_y + oilp_h/2 - Math.round(oilp_h * oilp_dial);
        int a_y[] = {
            oilp_a,
            oilp_a - oilp_h/10,
            oilp_a + oilp_h/10
        };
        if ( this.aircraft.oil_press_alert(pos) ) {
            g2.setColor(mfd_gc.warning_color);
        } else if ( oilp_dial < 0.1f ) {
            g2.setColor(mfd_gc.caution_color);
        } else {
            g2.setColor(mfd_gc.markings_color);
        }
        g2.fillPolygon(a_x, a_y, 3);

        resetPen(g2);

    }


    private void drawOilT(Graphics2D g2, int pos, int num) {

        scalePen(g2);

        boolean mirror = (pos==1) && (num==2);

        float oilt_dial = this.aircraft.get_oil_temp_ratio(pos);

        int oilt_x = tape_x[pos];
        int oilt_y = mfd_gc.dial_oilt_y;
        int oilt_h = mfd_gc.tape_h;

        g2.setColor(mfd_gc.dim_markings_color);
        g2.drawLine(oilt_x, oilt_y - oilt_h/2, oilt_x, oilt_y + oilt_h/2);
        g2.setColor(mfd_gc.caution_color);
        g2.drawLine(oilt_x, oilt_y - oilt_h*3/10, oilt_x + (mirror ? -oilt_h/20 : oilt_h/20), oilt_y - oilt_h*3/10);
        g2.setColor(mfd_gc.warning_color);
        g2.drawLine(oilt_x - oilt_h/15, oilt_y - oilt_h/2, oilt_x + oilt_h/15, oilt_y - oilt_h/2);

        // arrow
        int a_x[] = {
            oilt_x,
            oilt_x + (mirror ? -oilt_h/4 : oilt_h/4),
            oilt_x + (mirror ? -oilt_h/4 : oilt_h/4)
        };
        int oilt_a = oilt_y + oilt_h/2 - Math.round(oilt_h * oilt_dial);
        int a_y[] = {
            oilt_a,
            oilt_a - oilt_h/10,
            oilt_a + oilt_h/10
        };
        if ( this.aircraft.oil_temp_alert(pos) ) {
            g2.setColor(mfd_gc.warning_color);
        } else if ( oilt_dial > 0.9f ) {
            g2.setColor(mfd_gc.caution_color);
        } else {
            g2.setColor(mfd_gc.markings_color);
        }
        g2.fillPolygon(a_x, a_y, 3);

        resetPen(g2);

    }


    private void drawOilQ(Graphics2D g2, int pos, int num) {

        scalePen(g2);
        
        boolean mirror = (pos==1) && (num==2);

        int oilq_val = Math.round( this.aircraft.get_oil_quant_ratio(pos) * 100.0f );

        int oilq_y = mfd_gc.dial_oilq_y + mfd_gc.dial_font_h[num]*5/8;
        int offset_x = mirror ? 0 : -mfd_gc.dial_font_w[num]*35/10;

        // value box
        if ( num < 5 ) {
            g2.setColor(mfd_gc.dim_markings_color);
            g2.drawRect(tape_x[pos] + offset_x,
                    oilq_y - mfd_gc.dial_font_h[num]*140/100, mfd_gc.dial_font_w[num]*35/10, mfd_gc.dial_font_h[num]*140/100);
            g2.setColor(mfd_gc.markings_color);
            g2.setFont(mfd_gc.dial_font[num]);
            String oilq_str = Integer.toString(oilq_val);
            g2.drawString(oilq_str,
                    tape_x[pos] + offset_x + mfd_gc.dial_font_w[num]*31/10 - mfd_gc.get_text_width(g2, mfd_gc.dial_font[num], oilq_str),
                    oilq_y-mfd_gc.dial_font_h[num]*25/100-2);
        }

        resetPen(g2);

    }

    
    private void drawVIB(Graphics2D g2, int pos, int num) {

        scalePen(g2);

        boolean mirror = (pos==1) && (num==2);

        float vib_dial = this.aircraft.get_vib(pos) / 100.0f;
        int vib_x = tape_x[pos];
        int vib_y = mfd_gc.dial_vib_y;
        int vib_h = mfd_gc.tape_h;

        g2.setColor(mfd_gc.dim_markings_color);
        g2.drawLine(vib_x, vib_y - vib_h/2, vib_x, vib_y + vib_h/2);
        g2.setColor(mfd_gc.caution_color);
        g2.drawLine(vib_x - vib_h/20, vib_y - vib_h*3/10, vib_x + vib_h/20, vib_y - vib_h*3/10);

        // arrow
        int a_x[] = {
            vib_x,
            vib_x + (mirror ? -vib_h/4 : vib_h/4),
            vib_x + (mirror ? -vib_h/4 : vib_h/4)
        };
        int vib_a = vib_y + vib_h/2 - Math.round(vib_h * vib_dial);
        int a_y[] = {
            vib_a,
            vib_a - vib_h/10,
            vib_a + vib_h/10
        };
        g2.setColor(mfd_gc.markings_color);
        g2.fillPolygon(a_x, a_y, 3);

        resetPen(g2);

    }



    private void scalePen(Graphics2D g2) {

        original_stroke = g2.getStroke();
        g2.setStroke(new BasicStroke(2.5f * mfd_gc.grow_scaling_factor, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));

    }


    private void resetPen(Graphics2D g2) {

        g2.setStroke(original_stroke);

    }


}
