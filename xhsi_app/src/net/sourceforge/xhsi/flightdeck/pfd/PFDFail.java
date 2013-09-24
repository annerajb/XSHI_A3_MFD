/**
* Fail.java
* 
* Draws a red cross when reception from X-Plane is lost
* 
* Copyright (C) 2012  Marc Rogiers (marrog.123@gmail.com)
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

import java.awt.BasicStroke;
//import java.awt.Color;
import java.awt.Component;
//import java.awt.GradientPaint;
import java.awt.Graphics2D;
//import java.awt.Shape;
import java.awt.Stroke;
//import java.awt.geom.AffineTransform;
//import java.awt.geom.Area;
//import java.awt.geom.Rectangle2D;
//import java.awt.geom.RoundRectangle2D;
//import java.awt.image.BufferedImage;

//import java.util.logging.Logger;

//import net.sourceforge.xhsi.XHSIPreferences;
//import net.sourceforge.xhsi.XHSISettings;
import net.sourceforge.xhsi.XHSIStatus;
import net.sourceforge.xhsi.model.ModelFactory;



public class PFDFail extends PFDSubcomponent {

    private static final long serialVersionUID = 1L;

//    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");


    public PFDFail(ModelFactory model_factory, PFDGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
    }


    public void paint(Graphics2D g2) {
        if ( XHSIStatus.status.equals(XHSIStatus.STATUS_NO_RECEPTION) ) {
            drawFailCross(g2);
        }
    }


    private void drawFailCross(Graphics2D g2) {

        g2.setColor(pfd_gc.warning_color);
        Stroke original_stroke = g2.getStroke();
        g2.setStroke(new BasicStroke(8.0f * pfd_gc.scaling_factor));
        g2.drawLine(pfd_gc.border_left, pfd_gc.border_top, pfd_gc.frame_size.width - pfd_gc.border_right, pfd_gc.frame_size.height - pfd_gc.border_bottom);
        g2.drawLine(pfd_gc.frame_size.width - pfd_gc.border_right, pfd_gc.border_top, pfd_gc.border_left, pfd_gc.frame_size.height - pfd_gc.border_bottom);
        g2.setStroke(original_stroke);

    }


}
