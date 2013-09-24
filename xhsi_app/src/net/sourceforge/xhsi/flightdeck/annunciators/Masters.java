/**
* Masters.java
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
package net.sourceforge.xhsi.flightdeck.annunciators;

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

//import net.sourceforge.xhsi.XHSISettings;

//import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ModelFactory;
//import net.sourceforge.xhsi.model.NavigationRadio;

//import net.sourceforge.xhsi.panel.GraphicsConfig;
//import net.sourceforge.xhsi.panel.Subcomponent;



public class Masters extends AnnunSubcomponent {

    private static final long serialVersionUID = 1L;

    
    Color off_warning;
    Color on_warning;
    Color off_caution;
    Color on_caution;
    Color off_border;
    Color on_border;

    
    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    private int loop = 0;

    public Masters(ModelFactory model_factory, AnnunGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
        
        off_warning = annun_gc.warning_color.darker().darker().darker().darker().darker();
        on_warning = annun_gc.warning_color;
        off_caution = annun_gc.caution_color.darker().darker().darker().darker().darker();
        on_caution = annun_gc.caution_color;
        off_border = annun_gc.instrument_background_color.brighter();
        on_border = annun_gc.markings_color.darker();
        
    }


    public void paint(Graphics2D g2) {
        if ( true ) {
            drawMasters(g2);
        }
    }


    private Color blendColor(Color off_color, Color on_color, float blend) {
        
        Color blend_color;
        
        if ( blend == 0.0f ) {
            blend_color = off_color;
        } else if ( blend == 1.0f ) {
            blend_color = on_color;
        } else {
            
            float off_red = (float)off_color.getRed() / 255.0f;
            float off_green = (float)off_color.getGreen() / 255.0f;
            float off_blue = (float)off_color.getBlue() / 255.0f;
            float on_red = (float)on_color.getRed() / 255.0f;
            float on_green = (float)on_color.getGreen() / 255.0f;
            float on_blue = (float)on_color.getBlue() / 255.0f;

            blend_color = new Color(
                    Math.min( 1.0f, off_red + (on_red - off_red) * blend ),
                    Math.min( 1.0f, off_green + (on_green - off_green) * blend ),
                    Math.min( 1.0f, off_blue + (on_blue - off_blue) * blend )
                    );
            
        }
        
        return blend_color;
        
    }
    
    
    private void drawMasters(Graphics2D g2) {

        float warning;
        float caution;
        
        if ( this.avionics.is_cl30() ) {
            warning = this.avionics.cl30_mast_warn();
            caution = this.avionics.cl30_mast_caut();
        } else {
            warning = ( this.aircraft.master_warning() && this.aircraft.battery() ) ? 1.0f : 0.0f;
            caution = ( this.aircraft.master_caution() && this.aircraft.battery() ) ? 1.0f : 0.0f;
        }

        int master_x = annun_gc.master_square.x + annun_gc.master_square.width/2 - annun_gc.master_square.width/2*3/4;
        int master_w = annun_gc.master_square.width*3/4;
        int warning_y = annun_gc.master_square.y + annun_gc.master_square.height/2 - annun_gc.line_height_xxl*4 - annun_gc.line_height_xxl/2;
        int caution_y = annun_gc.master_square.y + annun_gc.master_square.height/2 + annun_gc.line_height_xxl/2;
        int master_h = annun_gc.line_height_xxl*4;

        Stroke original_stroke = g2.getStroke();
        g2.setStroke(new BasicStroke(2.0f * master_h/16));
        g2.setFont(annun_gc.font_xxl);

        g2.setColor( blendColor(off_warning, on_warning, warning) );
        g2.fillRoundRect(master_x, warning_y, master_w, master_h, master_h/16, master_h/16);
        g2.setColor( blendColor(off_border, on_border, warning) );
        g2.drawRoundRect(master_x, warning_y, master_w, master_h, master_h/16, master_h/16);
        g2.drawString("MASTER", annun_gc.master_square.x + annun_gc.master_square.width/2 - annun_gc.get_text_width(g2, annun_gc.font_xxl, "MASTER")/2, annun_gc.master_square.y + annun_gc.master_square.height/2 - annun_gc.line_height_xxl*2 - annun_gc.line_height_xxl/2 - 2);
        g2.drawString("WARNING", annun_gc.master_square.x + annun_gc.master_square.width/2 - annun_gc.get_text_width(g2, annun_gc.font_xxl, "WARNING")/2, annun_gc.master_square.y + annun_gc.master_square.height/2 - annun_gc.line_height_xxl*1 - annun_gc.line_height_xxl/2 - 2);

        g2.setColor( blendColor(off_caution, on_caution, caution) );
        g2.fillRoundRect(master_x, caution_y, master_w, master_h, master_h/16, master_h/16);
        g2.setColor( blendColor(off_border, on_border, caution) );
        g2.drawRoundRect(master_x, caution_y, master_w, master_h, master_h/16, master_h/16);
        g2.drawString("MASTER", annun_gc.master_square.x + annun_gc.master_square.width/2 - annun_gc.get_text_width(g2, annun_gc.font_xxl, "MASTER")/2, annun_gc.master_square.y + annun_gc.master_square.height/2 + annun_gc.line_height_xxl/2 + annun_gc.line_height_xxl*2 - 2);
        g2.drawString("CAUTION", annun_gc.master_square.x + annun_gc.master_square.width/2 - annun_gc.get_text_width(g2, annun_gc.font_xxl, "CAUTION")/2, annun_gc.master_square.y + annun_gc.master_square.height/2 + annun_gc.line_height_xxl/2 + annun_gc.line_height_xxl*3 - 2);

        g2.setStroke(original_stroke);

    }


}
