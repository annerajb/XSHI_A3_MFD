/**
* DivText.java
* 
* A/T limit, Thrust mode and TAT
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
package net.sourceforge.xhsi.flightdeck.uh60m;


import net.sourceforge.xhsi.flightdeck.uh60m.*;
import java.awt.Color;
import java.awt.Component;

import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;

import java.awt.geom.AffineTransform;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import java.util.logging.Logger;

import net.sourceforge.xhsi.XHSISettings;
import net.sourceforge.xhsi.flightdeck.DrawUtils;

import net.sourceforge.xhsi.model.ModelFactory;



public class DebugText extends UH60MSubcomponent
{

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");


    public DebugText(ModelFactory model_factory, UH60MGraphicsConfig hsi_gc, Component parent_component) 
    {
        super(model_factory, hsi_gc, parent_component);
    }


    public void paint(Graphics2D g2)
    {
        drawThrustMode(g2);
          

    }


    private void drawThrustMode(Graphics2D g2)
    {

       g2.setColor(empty_gc.color_lime);
        
        
        g2.setFont(empty_gc.font_m);
        int top_b_y = (int)Math.round(empty_gc.panel_rect.y + empty_gc.instrument_size * 2.7 /100);
        g2.drawString("ALT", empty_gc.panel_rect.x + empty_gc.instrument_size * 18 /100 ,top_b_y );
        g2.drawString("IAS", (int)Math.round(empty_gc.panel_rect.x + empty_gc.instrument_size * 31.8 /100), top_b_y);
        g2.drawString("FD1", (int)Math.round(empty_gc.panel_rect.x + empty_gc.instrument_size * 77.5 /100), top_b_y);
        g2.drawString("LNAV", (int)Math.round(empty_gc.panel_rect.x + empty_gc.instrument_size * 86.3 /100), top_b_y);
        
        
        String bank_str = "" + Math.round(this.aircraft.get_fuel(0)*2000);
        g2.drawString(bank_str, (int)Math.round(empty_gc.panel_rect.x + empty_gc.instrument_size * 34.4 /100), (int)Math.round(empty_gc.panel_rect.y + empty_gc.instrument_size * 76.6 /100));
        
        //GS -- no value yet...
        g2.setColor(Color.WHITE);
        bank_str = "" + Math.round(this.aircraft.agl_m());
        g2.drawString(bank_str, (int)Math.round(empty_gc.panel_rect.x + empty_gc.instrument_size * 90 /100), (int)Math.round(empty_gc.panel_rect.y + empty_gc.instrument_size * 53.7 /100));
        g2.drawString("RA", (int)Math.round(empty_gc.panel_rect.x + empty_gc.instrument_size * 90 /100), (int)Math.round(empty_gc.panel_rect.y + empty_gc.instrument_size * 56.3 /100));
        g2.drawString("FUEL", (int)Math.round(empty_gc.panel_rect.x + empty_gc.instrument_size * 32.6 /100), (int)Math.round(empty_gc.panel_rect.y + empty_gc.instrument_size * 56.3 /100));
        //TODO split function
        DrawUtils.drawFuelGraph(g2,empty_gc,(int)Math.round(empty_gc.panel_rect.x + empty_gc.instrument_size * 32.6 /100),(int)Math.round(empty_gc.panel_rect.y + empty_gc.instrument_size * 57.2 /100),this.aircraft.get_fuel(0));
        int bottom_text_y = empty_gc.panel_rect.y + empty_gc.instrument_size * 99 /100;
        g2.drawString("PFD", empty_gc.panel_rect.x + empty_gc.instrument_size * 4 /100, bottom_text_y);
        g2.drawString("FULL", (int)Math.round(empty_gc.panel_rect.x + empty_gc.instrument_size * 92 /100), bottom_text_y);
        g2.drawString("TAC", (int)Math.round(empty_gc.panel_rect.x + empty_gc.instrument_size * 103 /100), bottom_text_y);
        g2.drawString("JVMF", (int)Math.round(empty_gc.panel_rect.x + empty_gc.instrument_size * 115 /100), bottom_text_y);
        g2.drawString("ND", empty_gc.panel_rect.x + empty_gc.instrument_size * 13 /100, bottom_text_y);
        g2.drawString("EICAS", empty_gc.panel_rect.x + empty_gc.instrument_size * 18 /100, bottom_text_y);
        
        g2.drawString("GS---",  (int)Math.round(empty_gc.panel_rect.x + empty_gc.instrument_size * 32.6 /100), (int)Math.round(empty_gc.panel_rect.y + empty_gc.instrument_size * 46 /100));
        //windspeed
        bank_str = "" + Math.round(aircraft_environment.wind_speed());
        g2.drawString(bank_str, empty_gc.panel_rect.x + empty_gc.instrument_size * 34, (int)Math.round(empty_gc.panel_rect.y + empty_gc.instrument_size * 48.5 /100));
        
        
        DrawUtils.drawArrow(g2, empty_gc.panel_rect.x + empty_gc.instrument_size * 35 /100, (int)Math.round(empty_gc.panel_rect.y + empty_gc.instrument_size * 51.9 /100), 20, aircraft_environment.wind_direction());
        DecimalFormat feet_format = new DecimalFormat("000");
        bank_str =  feet_format.format(Math.round(this.aircraft.heading()));
        DrawUtils.drawBoxedText(g2,empty_gc, bank_str, (int)Math.round(empty_gc.panel_rect.x + empty_gc.instrument_size * 63.4 /100), (int)Math.round(empty_gc.panel_rect.y + empty_gc.instrument_size * 45 /100), empty_gc.line_height_small, empty_gc.digit_width_s,empty_gc.font_xl);
        DrawUtils.drawStabilitator(g2,empty_gc,(int)Math.round(empty_gc.panel_rect.x + empty_gc.instrument_size * 3.5 /100),(int)Math.round(empty_gc.panel_rect.y + empty_gc.instrument_size * 8.8 /100),0.0f);
        DrawUtils.drawBoxedText(g2, empty_gc,"REF-BAR0", empty_gc.panel_rect.x + empty_gc.instrument_size * 35 /100, bottom_text_y-20, empty_gc.line_height_medium, empty_gc.digit_width_medium,empty_gc.font_xl);
        g2.setFont(empty_gc.font_s);
        g2.drawString("METER", (int)Math.round(empty_gc.panel_rect.x + empty_gc.instrument_size * 99 /100), (int)Math.round(empty_gc.panel_rect.y + empty_gc.instrument_size * 20 /100));
        g2.drawString("KM/H", empty_gc.panel_rect.x + empty_gc.instrument_size * 19 /100, (int)Math.round(empty_gc.panel_rect.y + empty_gc.instrument_size * 20 /100));
        DrawUtils.drawAltReadOut(g2,empty_gc,Math.round(this.aircraft.true_air_speed_km()), empty_gc.panel_rect.x + empty_gc.instrument_size * 19 /100, (int)Math.round(empty_gc.panel_rect.y + empty_gc.instrument_size * 22 /100),false);
        DrawUtils.drawAltReadOut(g2,empty_gc,Math.round(this.aircraft.msl_m()), (int)Math.round(empty_gc.panel_rect.x + empty_gc.instrument_size * 99 /100),(int)Math.round(empty_gc.panel_rect.y + empty_gc.instrument_size * 22 /100),false);
        
        DrawUtils.drawCompassAircraft(g2,(int)Math.round(empty_gc.panel_rect.x + empty_gc.instrument_size * 65.3 /100),(int)Math.round(empty_gc.panel_rect.y + empty_gc.instrument_size * 72 /100));
        DrawUtils.drawUHCompassRose(g2,empty_gc,(int)Math.round(empty_gc.panel_rect.x + empty_gc.instrument_size * 65.3 /100),(int)Math.round(empty_gc.panel_rect.y + empty_gc.instrument_size * 72 /100),(int)Math.round(empty_gc.panel_rect.y + empty_gc.instrument_size * 48.5 /100),this.aircraft.heading());
    }


}
