/**
* ClipRoseArea.java
* 
* Erase everything that falls outside the rose area
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
package net.sourceforge.xhsi.flightdeck.nd;

//import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

import net.sourceforge.xhsi.model.ModelFactory;

//import net.sourceforge.xhsi.panel.GraphicsConfig;
//import net.sourceforge.xhsi.panel.Subcomponent;



public class ClipRoseArea extends NDSubcomponent {

//    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    private static final long serialVersionUID = 1L;

    Area panel = null;


    public ClipRoseArea(ModelFactory model_factory, NDGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
    }


    public void paint(Graphics2D g2) {

        if ( nd_gc.powered ) {

            if ( this.preferences.get_draw_only_inside_rose() ) {
                // blank out outer rose area
                panel = new Area(new Rectangle2D.Float(0,0, nd_gc.frame_size.width, nd_gc.frame_size.height));
                panel.subtract(nd_gc.inner_rose_area);
                g2.setColor(nd_gc.background_color);
                g2.fill(panel);
            } else {
                // leave at least the top of the window uncluttered
                g2.clearRect(0,0, nd_gc.frame_size.width, nd_gc.rose_y_offset);
            }

        }

    }


}

