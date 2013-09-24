/**
* Radios.java
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

import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ModelFactory;
//import net.sourceforge.xhsi.model.NavigationRadio;

//import net.sourceforge.xhsi.panel.GraphicsConfig;
//import net.sourceforge.xhsi.panel.Subcomponent;



public class Radios extends PFDSubcomponent {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    private String xpdr_mode[] = { "OFF", "STBY", "ON", "TA", "TA/RA" };


    public Radios(ModelFactory model_factory, PFDGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
    }


    public void paint(Graphics2D g2) {
        if ( pfd_gc.powered && this.preferences.get_pfd_draw_radios() ) {
            drawFrequencies(g2);
        }
    }


    private void drawFrequencies(Graphics2D g2) {

        DecimalFormat navcom_format = new DecimalFormat("000.00");
        DecimalFormatSymbols format_symbols = navcom_format.getDecimalFormatSymbols();
        format_symbols.setDecimalSeparator('.');
        navcom_format.setDecimalFormatSymbols(format_symbols);
        DecimalFormat adf_format = new DecimalFormat("000");
        DecimalFormat xpdr_format = new DecimalFormat("0000");

        int line_1_l = pfd_gc.radios_top + pfd_gc.line_height_m * 5 / 4;
        int line_1_a = line_1_l + pfd_gc.line_height_l;
        int line_1_s = line_1_a + pfd_gc.line_height_m;

        int line_2_l = line_1_s + pfd_gc.line_height_m * 3 / 2;
        int line_2_a = line_2_l + pfd_gc.line_height_l;
        int line_2_s = line_2_a + pfd_gc.line_height_m;

        int line_3_l = line_2_s + pfd_gc.line_height_m * 3 / 2;
        int line_3_a = line_3_l + pfd_gc.line_height_l;
        int line_3_s = line_3_a + pfd_gc.line_height_m;

        int line_4_l = line_3_s + pfd_gc.line_height_m * 3 / 2;
        int line_4_a = line_4_l + pfd_gc.line_height_l;
        int line_4_s = line_4_a + pfd_gc.line_height_m;


        Shape original_clipshape = g2.getClip();



        // tape NAV & ADF
        pfd_gc.setTransparent(g2, this.preferences.get_draw_colorgradient_horizon());
        g2.setColor(pfd_gc.instrument_background_color);
        g2.fillRect(pfd_gc.navradios_left - 1, pfd_gc.radios_top - 1, pfd_gc.radios_width + 2, pfd_gc.radios_height + 2);
        pfd_gc.setOpaque(g2);

        g2.clipRect(pfd_gc.navradios_left, pfd_gc.radios_top, pfd_gc.radios_width, pfd_gc.radios_height);
        
        g2.setColor(pfd_gc.markings_color);
        g2.setFont(pfd_gc.font_m);
        g2.drawString("NAV1", pfd_gc.navradios_left + pfd_gc.digit_width_l/2, line_1_l);
        g2.setFont(pfd_gc.font_s);
        g2.drawString(navcom_format.format(this.avionics.get_nav_radio(1).get_frequency()), pfd_gc.navradios_left + pfd_gc.digit_width_l, line_1_a);
        g2.setColor(pfd_gc.dim_markings_color);
        g2.setFont(pfd_gc.font_xs);
        g2.drawString(navcom_format.format(this.avionics.get_radio_freq(Avionics.RADIO_NAV1_STDBY)/100.0f), pfd_gc.navradios_left + pfd_gc.digit_width_l, line_1_s);

        g2.setColor(pfd_gc.markings_color);
        g2.setFont(pfd_gc.font_m);
        g2.drawString("NAV2", pfd_gc.navradios_left + pfd_gc.digit_width_l/2, line_2_l);
        g2.setFont(pfd_gc.font_s);
        g2.drawString(navcom_format.format(this.avionics.get_nav_radio(2).get_frequency()), pfd_gc.navradios_left + pfd_gc.digit_width_l, line_2_a);
        g2.setColor(pfd_gc.dim_markings_color);
        g2.setFont(pfd_gc.font_xs);
        g2.drawString(navcom_format.format(this.avionics.get_radio_freq(Avionics.RADIO_NAV2_STDBY)/100.0f), pfd_gc.navradios_left + pfd_gc.digit_width_l, line_2_s);

        g2.setColor(pfd_gc.markings_color);
        g2.setFont(pfd_gc.font_m);
        g2.drawString("ADF1", pfd_gc.navradios_left + pfd_gc.digit_width_l/2, line_3_l);
        g2.setFont(pfd_gc.font_s);
        g2.drawString(adf_format.format(this.avionics.get_radio_freq(Avionics.RADIO_ADF1)), pfd_gc.navradios_left + pfd_gc.digit_width_l, line_3_a);
        g2.setColor(pfd_gc.dim_markings_color);
        g2.setFont(pfd_gc.font_xs);
        g2.drawString(adf_format.format(this.avionics.get_radio_freq(Avionics.RADIO_ADF1_STDBY)), pfd_gc.navradios_left + pfd_gc.digit_width_l, line_3_s);

        g2.setColor(pfd_gc.markings_color);
        g2.setFont(pfd_gc.font_m);
        g2.drawString("ADF2", pfd_gc.navradios_left + pfd_gc.digit_width_l/2, line_4_l);
        g2.setFont(pfd_gc.font_s);
        g2.drawString(adf_format.format(this.avionics.get_radio_freq(Avionics.RADIO_ADF2)), pfd_gc.navradios_left + pfd_gc.digit_width_l, line_4_a);
        g2.setColor(pfd_gc.dim_markings_color);
        g2.setFont(pfd_gc.font_xs);
        g2.drawString(adf_format.format(this.avionics.get_radio_freq(Avionics.RADIO_ADF2_STDBY)), pfd_gc.navradios_left + pfd_gc.digit_width_l, line_4_s);


        g2.setClip(original_clipshape);


        // tape COM & XPDR
        pfd_gc.setTransparent(g2, this.preferences.get_draw_colorgradient_horizon());
        g2.setColor(pfd_gc.instrument_background_color);
        g2.fillRect(pfd_gc.comradios_left - 1, pfd_gc.radios_top - 1, pfd_gc.radios_width + 2, pfd_gc.radios_height + 2);
        pfd_gc.setOpaque(g2);

        g2.clipRect(pfd_gc.comradios_left, pfd_gc.radios_top, pfd_gc.radios_width, pfd_gc.radios_height);

        g2.setColor(pfd_gc.markings_color);
        g2.setFont(pfd_gc.font_m);
        g2.drawString("COM1", pfd_gc.comradios_left + pfd_gc.digit_width_l/2, line_1_l);
        g2.setFont(pfd_gc.font_s);
        g2.drawString(navcom_format.format(this.avionics.get_radio_freq(Avionics.RADIO_COM1)/100.0f), pfd_gc.comradios_left + pfd_gc.digit_width_l, line_1_a);
        g2.setColor(pfd_gc.dim_markings_color);
        g2.setFont(pfd_gc.font_xs);
        g2.drawString(navcom_format.format(this.avionics.get_radio_freq(Avionics.RADIO_COM1_STDBY)/100.0f), pfd_gc.comradios_left + pfd_gc.digit_width_l, line_1_s);

        g2.setColor(pfd_gc.markings_color);
        g2.setFont(pfd_gc.font_m);
        g2.drawString("COM2", pfd_gc.comradios_left + pfd_gc.digit_width_l/2, line_2_l);
        g2.setFont(pfd_gc.font_s);
        g2.drawString(navcom_format.format(this.avionics.get_radio_freq(Avionics.RADIO_COM2)/100.0f), pfd_gc.comradios_left + pfd_gc.digit_width_l, line_2_a);
        g2.setColor(pfd_gc.dim_markings_color);
        g2.setFont(pfd_gc.font_xs);
        g2.drawString(navcom_format.format(this.avionics.get_radio_freq(Avionics.RADIO_COM2_STDBY)/100.0f), pfd_gc.comradios_left + pfd_gc.digit_width_l, line_2_s);

        g2.setColor(pfd_gc.markings_color);
        g2.setFont(pfd_gc.font_m);
        g2.drawString("XPDR", pfd_gc.comradios_left + pfd_gc.digit_width_l/2, line_3_l);
        g2.setFont(pfd_gc.font_s);
        g2.drawString(xpdr_format.format(this.avionics.transponder_code()), pfd_gc.comradios_left + pfd_gc.digit_width_l, line_3_a);
        g2.setColor(pfd_gc.dim_markings_color);
        g2.drawString(xpdr_mode[this.avionics.transponder_mode()], pfd_gc.comradios_left + pfd_gc.digit_width_l, line_3_s);

        g2.setClip(original_clipshape);

    }


}
