/**
 * EmptyGraphicsConfig.java
 *
 * Calculates and provides access to screen positions and sizes based on the
 * size of HSIComponent.
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
package net.sourceforge.xhsi.flightdeck.empty;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.Map;

import net.sourceforge.xhsi.XHSIInstrument;
import net.sourceforge.xhsi.XHSIPreferences;

import net.sourceforge.xhsi.model.Avionics;

import net.sourceforge.xhsi.flightdeck.GraphicsConfig;


public class EmptyGraphicsConfig extends GraphicsConfig implements ComponentListener {

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");


    public EmptyGraphicsConfig(Component root_component, int du) {
        super(root_component);
        this.display_unit = du;
        init();
    }


//    public void init() {
//
//        super.init();
//
//    }


    public void update_config(Graphics2D g2) {

        if (this.resized
                || this.reconfig
            ) {
            // one of the settings has been changed

            super.update_config(g2);

            // some subcomponents need to be reminded to redraw imediately
            this.reconfigured = true;


        }

    }


//    public int get_text_width(Graphics graphics, Font font, String text) {
//        return graphics.getFontMetrics(font).stringWidth(text);
//    }


//    public int get_text_height(Graphics graphics, Font font) {
//        return graphics.getFontMetrics(font).getHeight();
//    }


    public void componentResized(ComponentEvent event) {
        this.component_size = event.getComponent().getSize();
        this.frame_size = event.getComponent().getSize();
        this.resized = true;
    }


    public void componentMoved(ComponentEvent event) {
        this.component_topleft = event.getComponent().getLocation();
    }


    public void componentShown(ComponentEvent arg0) {
        // TODO Auto-generated method stub
    }


    public void componentHidden(ComponentEvent arg0) {
        // TODO Auto-generated method stub
    }


}
