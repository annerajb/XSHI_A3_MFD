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

        int y1 = 50;
        int x1 = 10;

        g2.setColor(empty_gc.color_lime);
        g2.setFont(empty_gc.font_xl);
         y1+= 45;
        
        y1+= 45;
        y1+= 45;
        y1+= 45;
        y1+= 45;
        y1+= 45;
        y1 -= 45;
        g2.setFont(empty_gc.font_m);
        g2.drawString("ALT",105 , 15);
        g2.drawString("IAS", 180, 15);
        g2.drawString("FD1", 440, 15);
        g2.drawString("LNAV", 493, 15);
        
        
        String bank_str = "" + Math.round(this.aircraft.get_fuel(0)*2000);
        g2.drawString(bank_str, 190, 435);
        
        //mouse?
        /*PointerInfo a = MouseInfo.getPointerInfo();
        Point b = a.getLocation();
        g2.drawString("x: " +b.getX() + "y: "+b.getY(), 50, 50);
        */
        
        //GS -- no value yet...
        g2.setColor(Color.WHITE);
        bank_str = "" + Math.round(this.aircraft.agl_m());
        g2.drawString(bank_str, x1+500, y1+30);
        g2.drawString("RA", x1+500, y1+45);
        g2.drawString("FUEL", x1+175, y1+45);
        //TODO split function
        DrawUtils.drawFuelGraph(g2,empty_gc,x1+175,y1+50,this.aircraft.get_fuel(0));
        int bottom_text_y = 525;
        g2.drawString("PFD", 20, bottom_text_y);
        g2.drawString("FULL", 520, bottom_text_y);
        g2.drawString("TAC", 583, bottom_text_y);
        g2.drawString("JVMF", 650, bottom_text_y);
        g2.drawString("ND", 70, bottom_text_y);
        g2.drawString("EICAS", 100, bottom_text_y);
        g2.drawString("GS---", x1+175, y1-15);
        //windspeed
        bank_str = "" + Math.round(aircraft_environment.wind_speed());
        g2.drawString(bank_str, x1+185, y1);
        
        
        DrawUtils.drawArrow(g2, x1+190, y1+17, 20, aircraft_environment.wind_direction());
        DecimalFormat feet_format = new DecimalFormat("000");
        bank_str =  feet_format.format(Math.round(this.aircraft.heading()));
        DrawUtils.drawBoxedText(g2,empty_gc, bank_str, x1+350, y1-19, empty_gc.line_height_small, empty_gc.digit_width_s,empty_gc.font_xl);
        DrawUtils.drawStabilitator(g2,empty_gc,20,40,0.0f);
        DrawUtils.drawBoxedText(g2, empty_gc,"REF-BAR0", x1+190, bottom_text_y-20, empty_gc.line_height_medium, empty_gc.digit_width_medium,empty_gc.font_xl);
        g2.setFont(empty_gc.font_s);
        g2.drawString("FEET", x1+550, y1-165);
        g2.drawString("KTS", x1+100, y1-165);
        DrawUtils.drawAltReadOut(g2,empty_gc,Math.round(this.aircraft.true_air_speed_km()), x1+100, y1-150,false);
        
        DrawUtils.drawAltReadOut(g2,empty_gc,Math.round(this.aircraft.msl_m()), x1+550, y1-150,false);
        DrawUtils.drawCompassAircraft(g2,371,409);
        DrawUtils.drawUHCompassRose(g2,empty_gc,371,409,this.aircraft.heading());
    }


}
