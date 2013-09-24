/**
* ClockFrame.java
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
package net.sourceforge.xhsi.flightdeck.clock;

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

import net.sourceforge.xhsi.XHSIPreferences;

//import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ModelFactory;
//import net.sourceforge.xhsi.model.NavigationRadio;

//import net.sourceforge.xhsi.panel.GraphicsConfig;
//import net.sourceforge.xhsi.panel.Subcomponent;



public class ClockFrame extends ClockSubcomponent {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");


    public ClockFrame(ModelFactory model_factory, ClockGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
    }


    public void paint(Graphics2D g2) {
        if ( XHSIPreferences.get_instance().get_relief_border() ) {
            drawRaisedPanel(g2);
        }
    }


    private void drawRaisedPanel(Graphics2D g2) {

        Stroke original_stroke = g2.getStroke();

        g2.setColor(clock_gc.frontpanel_color);
        g2.fillRoundRect(clock_gc.clock_square.x, clock_gc.clock_square.y, clock_gc.clock_square.width, clock_gc.clock_square.height, clock_gc.clock_square.width/8, clock_gc.clock_square.height/8);
        g2.setStroke(new BasicStroke(8.0f * clock_gc.scaling_factor));
        g2.setPaint(clock_gc.clock_gradient);
        g2.drawRoundRect(clock_gc.clock_square.x, clock_gc.clock_square.y, clock_gc.clock_square.width, clock_gc.clock_square.height, clock_gc.clock_square.width/8, clock_gc.clock_square.height/8);

        g2.setStroke(original_stroke);

    }


}
