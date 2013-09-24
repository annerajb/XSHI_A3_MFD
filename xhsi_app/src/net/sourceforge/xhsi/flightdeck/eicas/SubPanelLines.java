/**
* SubPanelLines.java
* 
* Draw the frame lines in Compact Mode
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
package net.sourceforge.xhsi.flightdeck.eicas;

//import java.awt.BasicStroke;
//import java.awt.Color;
//import java.awt.Color;
import java.awt.Component;
//import java.awt.GradientPaint;
import java.awt.Graphics2D;
//import java.awt.Shape;
//import java.awt.Stroke;
//import java.awt.geom.AffineTransform;
//import java.awt.geom.Area;
//import java.awt.geom.Rectangle2D;
//import java.awt.geom.RoundRectangle2D;
//import java.awt.image.BufferedImage;
//import java.text.DecimalFormat;
//import java.text.DecimalFormatSymbols;

import java.util.logging.Logger;

//import net.sourceforge.xhsi.XHSISettings;

import net.sourceforge.xhsi.model.ModelFactory;



public class SubPanelLines extends EICASSubcomponent {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");


    public SubPanelLines(ModelFactory model_factory, EICASGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
    }


    public void paint(Graphics2D g2) {

        if ( eicas_gc.powered && ! this.preferences.get_eicas_primary() ) {

            g2.setColor(eicas_gc.color_boeingcyan);
            g2.drawLine(eicas_gc.panel_rect.x + eicas_gc.panel_rect.width - eicas_gc.alerts_w*33/32, eicas_gc.panel_rect.y + eicas_gc.panel_rect.height/100, eicas_gc.panel_rect.x + eicas_gc.panel_rect.width - eicas_gc.alerts_w*33/32, eicas_gc.panel_rect.y + eicas_gc.panel_rect.height*99/100);
            g2.drawLine(eicas_gc.panel_rect.x + eicas_gc.panel_rect.width/100, eicas_gc.panel_rect.y + eicas_gc.prim_dials_height, eicas_gc.panel_rect.x + eicas_gc.panel_rect.width - eicas_gc.alerts_w*33/32 - eicas_gc.panel_rect.width/100, eicas_gc.panel_rect.y + eicas_gc.prim_dials_height);
            g2.drawLine(eicas_gc.panel_rect.x + eicas_gc.panel_rect.width - eicas_gc.alerts_w*33/32 + eicas_gc.panel_rect.width/100, eicas_gc.panel_rect.y + eicas_gc.panel_rect.height - eicas_gc.hyd_dials_height, eicas_gc.panel_rect.x + eicas_gc.panel_rect.width*99/100, eicas_gc.panel_rect.y + eicas_gc.panel_rect.height - eicas_gc.hyd_dials_height);

        }

    }


}
