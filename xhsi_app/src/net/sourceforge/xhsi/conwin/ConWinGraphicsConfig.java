/**
 * ConWinGraphicsConfig.java
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
package net.sourceforge.xhsi.conwin;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
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

import net.sourceforge.xhsi.XHSISettings;
import net.sourceforge.xhsi.XHSIPreferences;

import net.sourceforge.xhsi.model.Avionics;

import net.sourceforge.xhsi.flightdeck.GraphicsConfig;


public class ConWinGraphicsConfig implements ComponentListener {

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    public static int INITIAL_PANEL_WIDTH = 600;
    public static int INITIAL_PANEL_HEIGHT = 30;
    public static int STATUS_BAR_HEIGHT = 30;
    public static int INITIAL_BORDER_SIZE = 10;

    public XHSIPreferences preferences;

    public Color background_color;
    // RAL colors as found on http://www.tikkurila.com/industrial_coatings/metal_surfaces/ral_colour_cards/ral_classic_colour_card
    // Boeing doesn't use RAL colors, but RAL 7011 and 7040 come close enough
    public Color color_irongray = new Color(0x5A6066); // 737NG panel RAL7011
    public Color color_windowgray = new Color(0x9CA2AA); // 737NG knobs RAL7040

    public Font font_statusbar;
    public Font font_tiny;
    public Font font_small;
    public Font font_medium;
    public Font font_large;

    public int line_height_tiny;
    public int max_char_advance_tiny;
    public int line_height_small;
    public int max_char_advance_small;
    public int line_height_medium;
    public int max_char_advance_medium;
    public int line_height_large;
    public int max_char_advance_large;

    public Dimension component_size;
    public Dimension panel_size;
    public Point panel_topleft;

    public int border = 0;
    public int border_left = border;
    public int border_right = border;
    public int border_top = border;
    public int border_bottom = border;

    public Map rendering_hints;

    public boolean resized = false;
    public boolean reconfig = true;
    public boolean reconfigured = true;



    public ConWinGraphicsConfig(Component root_component) {
//        super(root_component);
        init();
    }


    public void init() {

//        super.init();

        this.preferences = XHSIPreferences.get_instance();

        this.rendering_hints = new HashMap();
        this.rendering_hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        this.rendering_hints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        // VALUE_TEXT_ANTIALIAS_LCD_HRGB uses sub-pixel anti-aliasing, and is supposed to looks better than VALUE_TEXT_ANTIALIAS_ON on modern LCD dispalys
        // but I don't see any difference, and it doesn't work on JRE 5.

        this.panel_size = new Dimension(ConWinGraphicsConfig.INITIAL_PANEL_WIDTH, ConWinGraphicsConfig.STATUS_BAR_HEIGHT);
        this.border_left = ConWinGraphicsConfig.INITIAL_BORDER_SIZE;
        this.border_right = ConWinGraphicsConfig.INITIAL_BORDER_SIZE;

        background_color = Color.WHITE;

    }


    public void update_config(Graphics2D g2) {

//        super.update_config(g2);

        if (this.resized
                || this.reconfig
            ) {
            // one of the settings has been changed

            // clear the flags
            this.resized = false;
            this.reconfig = false;

            // fonts
            // Verdana is easier to read than Lucida Sans, and available on Win, Mac and Lin
            if ( XHSIPreferences.get_instance().get_bold_fonts() ) {
                this.font_statusbar = new Font("Verdana", Font.PLAIN, 9);
                this.font_tiny = new Font( "Verdana", Font.BOLD, 10);
                this.font_small = new Font( "Verdana", Font.BOLD, 12);
                this.font_medium = new Font( "Verdana", Font.BOLD, 16);
                this.font_large = new Font( "Verdana", Font.PLAIN, 24);
            } else {
                this.font_statusbar = new Font("Verdana", Font.PLAIN, 9);
                this.font_tiny = new Font( "Verdana", Font.PLAIN, 10);
                this.font_small = new Font( "Verdana", Font.PLAIN, 12);
                this.font_medium = new Font( "Verdana", Font.PLAIN, 16);
                this.font_large = new Font( "Verdana", Font.PLAIN, 24);
            }

            // calculate font metrics
            // W is probably the largest characher...
            FontMetrics fm;
            fm = g2.getFontMetrics(this.font_large);
            this.line_height_large = fm.getAscent();
            this.max_char_advance_large = fm.stringWidth("WW") - fm.stringWidth("W");
            fm = g2.getFontMetrics(this.font_medium);
            this.line_height_medium = fm.getAscent();
            this.max_char_advance_medium = fm.stringWidth("WW") - fm.stringWidth("W");
            fm = g2.getFontMetrics(this.font_small);
            this.line_height_small = fm.getAscent();
            this.max_char_advance_small = fm.stringWidth("WW") - fm.stringWidth("W");
            fm = g2.getFontMetrics(this.font_tiny);
            this.line_height_tiny = fm.getAscent();
            this.max_char_advance_tiny = fm.stringWidth("WW") - fm.stringWidth("W");

        }

    }


    public int get_text_width(Graphics graphics, Font font, String text) {
        return graphics.getFontMetrics(font).stringWidth(text);
    }


    public int get_text_height(Graphics graphics, Font font) {
        return graphics.getFontMetrics(font).getHeight();
    }


    public void componentResized(ComponentEvent event) {
        this.panel_size = event.getComponent().getSize();
        this.resized = true;
    }


    public void componentMoved(ComponentEvent arg0) {
        // TODO Auto-generated method stub
    }


    public void componentShown(ComponentEvent arg0) {
        // TODO Auto-generated method stub
    }


    public void componentHidden(ComponentEvent arg0) {
        // TODO Auto-generated method stub
    }


}
