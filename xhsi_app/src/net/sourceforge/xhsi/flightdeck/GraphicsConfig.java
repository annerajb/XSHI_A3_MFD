/**
 * GraphicsConfig.java
 *
 * Calculates and provides access to screen positions and sizes based on the
 * size of HSIComponent.
 * General graphics config:
 * - fonts
 * - colors
 * - ...
 *
 * Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
 * Copyright (C) 2009-2010  Marc Rogiers (marrog.123@gmail.com)
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
package net.sourceforge.xhsi.flightdeck;

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
//import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.Map;

import net.sourceforge.xhsi.XHSIPreferences;
//import net.sourceforge.xhsi.model.Avionics;


public class GraphicsConfig implements ComponentListener {

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    public static int INITIAL_PANEL_SIZE = 560;
    public static int INITIAL_BORDER_SIZE = 16;


    public XHSIPreferences preferences;

    // for color inspiration: http://en.wikipedia.org/wiki/Internet_colors and http://en.wikipedia.org/wiki/X11_color_names

    // green
    public Color color_lime = new Color(0x00FF00);
    public Color color_limegreen = new Color(0x32CD32);
    public Color color_mediumspringgreen = new Color(0x00FA9A);
    public Color color_springgreen = new Color(0x00FF7F);
    public Color color_springbluegreen = new Color(0x00FF9F); // custom color
    public Color color_greenyellow = new Color(0xADFF2F);
    public Color color_yellowgreen = new Color(0x9ACD32);
    public Color color_green = new Color(0x008000);
    public Color color_lightgreen = new Color(0x90EE90);
    public Color color_darkgreen = new Color(0x006400);
    public Color color_seagreen = new Color(0x2E8B57);
    public Color color_palegreen = new Color(0x98FB98);
    public Color color_verypalegreen = new Color(0xCCFFCC);
    public Color color_verydarkgreen = new Color(0x003400);

    // red, orange, brown
    public Color color_darkred = new Color(0x8B0000);
    public Color color_saddlebrown = new Color(0x8B4513);
    public Color color_brown = new Color(0xA52A2A);
    public Color color_maroon = new Color(0x800000);
    public Color color_tomato = new Color(0xFF6347);
    public Color color_rosybrown = new Color(0xBC8F8F);

    // magenta
    public Color color_magenta = new Color(0xFF00FF);
    public Color color_orchid = new Color(0xDA70D6);
    public Color color_darkorchid = new Color(0x9933CC);
    public Color color_violet = new Color(0xEE82EE);
    public Color color_hotpink = new Color(0xFF69B4);
    public Color color_deeppink = new Color(0xFF1493);
    public Color color_lightpink = new Color(0xFFB6C1);
    public Color color_indigo = new Color(0x4B0082);
    public Color color_slateblue = new Color(0x6A5ACD);

    // blue
    public Color color_dodgerblue = new Color(0x1E90FF);
    public Color color_powderblue = new Color(0xB0E0E6);
    public Color color_lavender = new Color(0xE6E6FA);
    public Color color_lightskyblue = new Color(0x87CEFA);
    public Color color_skyblue = new Color(0x87CEEB);
    public Color color_deepskyblue = new Color(0x00BFFF);
    public Color color_mediumslateblue = new Color(0x7B68EE);
    public Color color_mediumpurple = new Color(0x9370DB);
    public Color color_lightsteelblue = new Color(0xB0C4DE);
    public Color color_cornflowerblue = new Color(0x6495ED);
    public Color color_midnightblue = new Color(0x191970);
    public Color color_cadetblue = new Color(0x5F9EA0);

    // cyan
    public Color color_lightcyan = new Color(0xE0FFFF);
    public Color color_mediumcyan = new Color(0x80FFFF);
    public Color color_cyan = new Color(0x00FFFF);
    public Color color_darkcyan = new Color(0x008B8B);
    public Color color_teal = new Color(0x008080);
    public Color color_paleturquoise = new Color(0xAFEEEE);
    public Color color_turquoise = new Color(0x40E0D0);
    public Color color_mediumturquoise = new Color(0x48D1CC);
    public Color color_darkturquoise = new Color(0x00CED1);

    // the cyan on the Boeing displays is blue-er than our RGB cyan
    public Color color_boeingcyan = color_darkturquoise; // color_darkturquoise !

    // aquamarine
    public Color color_aquamarine = new Color(0x7FFFD4);
    public Color color_mediumaquamarine = new Color(0x66CDAA);
    public Color color_lightaquamarine = new Color(0xCCFFDD);

    // other
    public Color color_slategray = new Color(0x708090);
    public Color color_khaki = new Color(0xF0E68C);
    public Color color_darkkhaki = new Color(0xBDB76B);
    public Color color_olive = new Color(0x808000);
    public Color color_tan = new Color(0xD2B48C);
    public Color color_darktan = color_tan.darker(); // custom color
    public Color color_amber = new Color(0xFFB400);
    public Color color_sky = new Color(0x0088FF); // custom color, was 0x0066CC
    public Color color_ground = new Color(0x884400); // custom color, was 0x006633
    public Color color_redmagenta = new Color(0xFF00A0);
    public Color color_mediumviolet = new Color(0xC020FF);
    public Color color_darkermediumviolet = new Color(0xB800FD);
    public Color color_pastelhotpink = new Color(0xC8A0B4); // hotpink, but with saturation at 60
    public Color color_purplegray = new Color(0x6E5A6E); // custom color
    public Color color_bluegray = new Color(0x404068); // custom color, was 0x5A5A78, 0x57608B
    public Color color_brightspringgreen = color_springgreen.brighter();


    public Color color_poweroff =  Color.BLACK;

    // B747-NG gray
    // RAL colors as found on http://www.tikkurila.com/industrial_coatings/metal_surfaces/ral_colour_cards/ral_classic_colour_card
    // Boeing doesn't use RAL colors, but RAL 7011 and 7040 come close enough
    public Color color_irongray = new Color(0x5A6066); // 737NG panel RAL7011
    public Color color_darkirongray = color_irongray.darker().darker();
    public Color color_windowgray = new Color(0x9CA2AA); // 737NG knobs RAL7040

    // B747-400 brown
    public Color color_jumbobrown = new Color(0x947D5B);
    public Color color_jumbodarkbrown = new Color(0x302720);
    public Color color_jumbolightbrown = new Color(0xA9A397);
    
    // Airbus blue
    public Color color_airbusback = new Color(0x99B0B3); // was : 0x98B0BC
    public Color color_airbusfront = new Color(0x516B7D); // was : 0x878E89
    public Color color_airbusknob = new Color(0xD1D2D3); // was : 0xEDEDEB
    
    
    // variables
    public Color backpanel_color;
    public Color frontpanel_color;
    public Color knobs_color;
    public Color background_color;
//    public Color border_color;
    public GradientPaint border_gradient;
    public Color tuned_vor_color;
    public Color tuned_localizer_color;
    public Color reference_localizer_color;
    public Color receiving_localizer_color;
    public Color silent_localizer_color;
    public Color tuned_ndb_color;
    public Color navaid_color;
    public Color term_wpt_color;
    public Color wpt_color;
    public Color awy_wpt_color;
    public Color arpt_color;
    public Color holding_color;
    public Color traffic_color;
    public Color faraway_color;
    public Color pos_label_color;
    public Color tcas_label_color;
    public Color data_label_color;
    public Color fmc_active_color;
    public Color fmc_disp_color;
    public Color fmc_other_color;
    public Color altitude_arc_color;
    public Color fmc_ll_active_color;
    public Color fmc_ll_disp_color;
    public Color fmc_ll_other_color;
    public Color heading_labels_color;
    public Color nav_needle_color;
    public Color deviation_scale_color;
    public Color markings_color;
    public Color dim_markings_color;
    public Color range_arc_color;
    public Color dim_label_color;
    public Color no_rcv_ndb_color;
    public Color no_rcv_vor_color;
    public Color unknown_nav_color;
    public Color normal_color;
    public Color unusual_color;
    public Color caution_color;
    public Color warning_color;
    public Color aircraft_color;
    public Color heading_bug_color;
    public Color wind_color;
    public Color efb_color;
    public Color top_text_color;
    public Color grass_color;
    public Color hard_color;
    public Color sand_color;
    public Color snow_color;
    public Color sky_color;
    public Color brightsky_color;
    public Color ground_color;
    public Color brightground_color;
    public Color instrument_background_color;
    public Color fpv_color;
    public Color clock_color;

    public Font font_statusbar;

    public Font font_tiny;
    public int line_height_tiny;
    public int max_char_advance_tiny;
    public int digit_width_tiny;

    public Font font_small;
    public int line_height_small;
    public int max_char_advance_small;
    public int digit_width_small;

    public Font font_medium;
    public int line_height_medium;
    public int max_char_advance_medium;
    public int digit_width_medium;

    public Font font_large;
    public int line_height_large;
    public int max_char_advance_large;
    public int digit_width_large;

    public Font font_zl;
    public int line_height_zl;
    public int max_char_advance_zl;
    public int digit_width_zl;

    public Font font_yl;
    public int line_height_yl;
    public int max_char_advance_yl;
    public int digit_width_yl;

    public Font font_xxl;
    public int line_height_xxl;
    public int max_char_advance_xxl;
    public int digit_width_xxl;

    public Font font_xl;
    public int line_height_xl;
    public int max_char_advance_xl;
    public int digit_width_xl;

    public Font font_l;
    public int line_height_l;
    public int max_char_advance_l;
    public int digit_width_l;

    public Font font_m;
    public int line_height_m;
    public int max_char_advance_m;
    public int digit_width_m;

    public Font font_s;
    public int line_height_s;
    public int max_char_advance_s;
    public int digit_width_s;

    public Font font_xs;
    public int line_height_xs;
    public int max_char_advance_xs;
    public int digit_width_xs;

    public Font font_xxs;
    public int line_height_xxs;
    public int max_char_advance_xxs;
    public int digit_width_xxs;

    public Font font_normal;
    public int line_height_normal;
    public int max_char_advance_normal;
    public int digit_width_normal;

    public Dimension component_size;
    public Dimension frame_size;
    public Point component_topleft;
    public Rectangle panel_rect;

    public float scaling_factor;
    public float shrink_scaling_factor;
    public float grow_scaling_factor;

    public int border = 0;
    public int border_left = border;
    public int border_right = border;
    public int border_top = border;
    public int border_bottom = border;

    public Area instrument_frame;
    public RoundRectangle2D inner_round_rect;
    public Area instrument_outer_frame;
    public Map rendering_hints;

    public boolean resized = false;
    public boolean reconfig = true;
    public boolean reconfigured = true;

    public int display_unit;

    public boolean powered;



    public GraphicsConfig(Component root_component) {
        // our children will call init()
        //init();
    }


    public void init() {

        this.preferences = XHSIPreferences.get_instance();

        set_colors(false, XHSIPreferences.BORDER_GRAY);

        this.rendering_hints = new HashMap();
        this.rendering_hints.put(RenderingHints.KEY_ANTIALIASING, preferences.get_anti_alias() ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
        this.rendering_hints.put(RenderingHints.KEY_TEXT_ANTIALIASING, preferences.get_anti_alias() ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        // VALUE_TEXT_ANTIALIAS_LCD_HRGB uses sub-pixel anti-aliasing, and is supposed to looks better than VALUE_TEXT_ANTIALIAS_ON on modern LCD dispalys
        // but I don't see any difference, and it doesn't work on JRE 5.

        if ( preferences.get_panels_locked() ) {
            this.component_size = new Dimension( preferences.get_panel_width(this.display_unit), preferences.get_panel_height(this.display_unit) );
            this.frame_size = new Dimension( preferences.get_panel_width(this.display_unit), preferences.get_panel_height(this.display_unit) );
        } else {
            this.component_size = new Dimension(INITIAL_PANEL_SIZE + border_left + border_right, INITIAL_PANEL_SIZE + border_top + border_bottom);
            this.frame_size = new Dimension(INITIAL_PANEL_SIZE + border_left + border_right, INITIAL_PANEL_SIZE + border_top + border_bottom);
        }

//        border_color = backpanel_color;
        border_gradient = new GradientPaint(
                0, 0, backpanel_color.darker().darker().darker(),
                this.frame_size.width, this.frame_size.height , backpanel_color.brighter(),
                true);

    }


    public void set_fonts(Graphics2D g2, float scale) {

            // fonts
            // Verdana is easier to read than Lucida Sans, and available on Win, Mac and Lin
            if ( XHSIPreferences.get_instance().get_bold_fonts() ) {
                this.font_statusbar = new Font("Verdana", Font.PLAIN, 9);
                this.font_tiny = new Font( "Verdana", Font.BOLD, 10);
                this.font_small = new Font( "Verdana", Font.BOLD, 12);
                this.font_medium = new Font( "Verdana", Font.BOLD, 16);
                this.font_large = new Font( "Verdana", Font.PLAIN, 24);
                this.font_zl = new Font( "Verdana", Font.PLAIN, Math.round(64.0f * scale));
                this.font_yl = new Font( "Verdana", Font.PLAIN, Math.round(32.0f * scale));
                this.font_xxl = new Font( "Verdana", Font.BOLD, Math.round(24.0f * scale));
                this.font_xl = new Font( "Verdana", Font.BOLD, Math.round(21.0f * scale));
                this.font_l = new Font( "Verdana", Font.BOLD, Math.round(18.0f * scale));
                this.font_m = new Font( "Verdana", Font.BOLD, Math.round(16.0f * scale));
                this.font_s = new Font( "Verdana", Font.BOLD, Math.round(14.0f * scale));
                this.font_xs = new Font( "Verdana", Font.BOLD, Math.round(12.0f * scale));
                this.font_xxs = new Font( "Verdana", Font.BOLD, Math.round(10.0f * scale));
                this.font_normal = new Font( "Verdana", Font.BOLD, Math.round(14.0f * scale));
            } else {
                this.font_statusbar = new Font("Verdana", Font.PLAIN, 9);
                this.font_tiny = new Font( "Verdana", Font.PLAIN, 10);
                this.font_small = new Font( "Verdana", Font.PLAIN, 12);
                this.font_medium = new Font( "Verdana", Font.PLAIN, 16);
                this.font_large = new Font( "Verdana", Font.PLAIN, 24);
                this.font_zl = new Font( "Verdana", Font.PLAIN, Math.round(64.0f * scale));
                this.font_yl = new Font( "Verdana", Font.PLAIN, Math.round(32.0f * scale));
                this.font_xxl = new Font( "Verdana", Font.PLAIN, Math.round(24.0f * scale));
                this.font_xl = new Font( "Verdana", Font.PLAIN, Math.round(21.0f * scale));
                this.font_l = new Font( "Verdana", Font.PLAIN, Math.round(18.0f * scale));
                this.font_m = new Font( "Verdana", Font.PLAIN, Math.round(16.0f * scale));
                this.font_s = new Font( "Verdana", Font.PLAIN, Math.round(14.0f * scale));
                this.font_xs = new Font( "Verdana", Font.PLAIN, Math.round(12.0f * scale));
                this.font_xxs = new Font( "Verdana", Font.PLAIN, Math.round(10.0f * scale));
                this.font_normal = new Font( "Verdana", Font.PLAIN, Math.round(14.0f * scale));
            }

            // calculate font metrics
            // W is probably the largest characher...
            FontMetrics fm;

            fm = g2.getFontMetrics(this.font_large);
            this.line_height_large = fm.getAscent();
            this.max_char_advance_large = fm.stringWidth("WW") - fm.stringWidth("W");
            this.digit_width_large =  fm.stringWidth("88") - fm.stringWidth("8");

            fm = g2.getFontMetrics(this.font_medium);
            this.line_height_medium = fm.getAscent();
            this.max_char_advance_medium = fm.stringWidth("WW") - fm.stringWidth("W");
            this.digit_width_medium =  fm.stringWidth("88") - fm.stringWidth("8");

            fm = g2.getFontMetrics(this.font_small);
            this.line_height_small = fm.getAscent();
            this.max_char_advance_small = fm.stringWidth("WW") - fm.stringWidth("W");
            this.digit_width_small =  fm.stringWidth("88") - fm.stringWidth("8");

            fm = g2.getFontMetrics(this.font_tiny);
            this.line_height_tiny = fm.getAscent();
            this.max_char_advance_tiny = fm.stringWidth("WW") - fm.stringWidth("W");
            this.digit_width_tiny =  fm.stringWidth("88") - fm.stringWidth("8");

            fm = g2.getFontMetrics(this.font_zl);
            this.line_height_zl = fm.getAscent();
            this.max_char_advance_zl = fm.stringWidth("WW") - fm.stringWidth("W");
            this.digit_width_zl =  fm.stringWidth("88") - fm.stringWidth("8");

            fm = g2.getFontMetrics(this.font_yl);
            this.line_height_yl = fm.getAscent();
            this.max_char_advance_yl = fm.stringWidth("WW") - fm.stringWidth("W");
            this.digit_width_yl =  fm.stringWidth("88") - fm.stringWidth("8");

            fm = g2.getFontMetrics(this.font_xxl);
            this.line_height_xxl = fm.getAscent();
            this.max_char_advance_xxl = fm.stringWidth("WW") - fm.stringWidth("W");
            this.digit_width_xxl =  fm.stringWidth("88") - fm.stringWidth("8");

            fm = g2.getFontMetrics(this.font_xl);
            this.line_height_xl = fm.getAscent();
            this.max_char_advance_xl = fm.stringWidth("WW") - fm.stringWidth("W");
            this.digit_width_xl =  fm.stringWidth("88") - fm.stringWidth("8");

            fm = g2.getFontMetrics(this.font_l);
            this.line_height_l = fm.getAscent();
            this.max_char_advance_l = fm.stringWidth("WW") - fm.stringWidth("W");
            this.digit_width_l =  fm.stringWidth("88") - fm.stringWidth("8");

            fm = g2.getFontMetrics(this.font_m);
            this.line_height_m = fm.getAscent();
            this.max_char_advance_m = fm.stringWidth("WW") - fm.stringWidth("W");
            this.digit_width_m =  fm.stringWidth("88") - fm.stringWidth("8");

            fm = g2.getFontMetrics(this.font_s);
            this.line_height_s = fm.getAscent();
            this.max_char_advance_s = fm.stringWidth("WW") - fm.stringWidth("W");
            this.digit_width_s =  fm.stringWidth("88") - fm.stringWidth("8");

            fm = g2.getFontMetrics(this.font_xs);
            this.line_height_xs = fm.getAscent();
            this.max_char_advance_xs = fm.stringWidth("WW") - fm.stringWidth("W");
            this.digit_width_xs =  fm.stringWidth("88") - fm.stringWidth("8");

            fm = g2.getFontMetrics(this.font_xxs);
            this.line_height_xxs = fm.getAscent();
            this.max_char_advance_xxs = fm.stringWidth("WW") - fm.stringWidth("W");
            this.digit_width_xxs =  fm.stringWidth("88") - fm.stringWidth("8");

            fm = g2.getFontMetrics(this.font_normal);
            this.line_height_normal = fm.getAscent();
            this.max_char_advance_normal = fm.stringWidth("WW") - fm.stringWidth("W");
            this.digit_width_normal =  fm.stringWidth("88") - fm.stringWidth("8");

    }


    public void set_colors(boolean custom_colors, String border_color) {

        if ( custom_colors ) {
            background_color = Color.BLACK;
            navaid_color = color_boeingcyan;
            term_wpt_color = color_cornflowerblue.darker();
            wpt_color = color_cornflowerblue;
            awy_wpt_color = color_cornflowerblue.brighter();
            arpt_color = color_mediumaquamarine;
            tuned_localizer_color = color_aquamarine;
            silent_localizer_color = color_mediumaquamarine.darker().darker();
            reference_localizer_color = color_lightaquamarine;
            receiving_localizer_color = color_aquamarine.darker();
            tuned_ndb_color = color_dodgerblue;
            no_rcv_ndb_color = color_dodgerblue.darker();
            tuned_vor_color = color_lime;
            no_rcv_vor_color = color_lime.darker();
            unknown_nav_color = color_cadetblue;
            holding_color = color_deeppink;
            traffic_color = color_lightsteelblue;
            faraway_color = color_lightsteelblue.darker().darker();
            pos_label_color = color_boeingcyan.darker();
            tcas_label_color = color_lightsteelblue;
            data_label_color = color_pastelhotpink;
            fmc_active_color = color_hotpink;
            fmc_disp_color = Color.WHITE;
            fmc_other_color = Color.GRAY;
            altitude_arc_color = color_yellowgreen;
            fmc_ll_active_color = color_yellowgreen.brighter();
            fmc_ll_disp_color = color_yellowgreen;
            fmc_ll_other_color = color_yellowgreen.darker();
            heading_labels_color = color_limegreen;
            nav_needle_color = color_mediumviolet;
            deviation_scale_color = Color.LIGHT_GRAY;
            markings_color = Color.WHITE;
//                float hsb[] = new float[3];
//                Color.RGBtoHSB(markings_color.getRed(), markings_color.getGreen(), markings_color.getBlue(), hsb);
//                hsb[2] *= 0.25f;
//                markings_color = Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
            dim_markings_color = Color.LIGHT_GRAY;
            range_arc_color = Color.GRAY; // was: Color.GRAY.darker()
            dim_label_color = Color.DARK_GRAY;
            normal_color = color_lime;
            unusual_color = color_deepskyblue;
            caution_color = color_amber;
            warning_color = Color.RED;
            aircraft_color = Color.WHITE;
            heading_bug_color = color_magenta;
            wind_color = color_palegreen; // was color_lavender
            efb_color = color_lavender;
            top_text_color = Color.WHITE;
            grass_color = color_darkgreen;
            hard_color = Color.GRAY;
            sand_color = color_darktan;
            snow_color = Color.LIGHT_GRAY;
            sky_color = color_sky;
            brightsky_color = color_sky.brighter();
            ground_color = color_ground;
            brightground_color = color_ground.brighter();
            instrument_background_color = color_bluegray;
            fpv_color = Color.LIGHT_GRAY;
            clock_color = color_khaki;
        } else {
            background_color = Color.BLACK;
            navaid_color = color_boeingcyan;
            term_wpt_color = color_boeingcyan;
            wpt_color = color_boeingcyan;
            awy_wpt_color = color_boeingcyan;
            arpt_color = color_boeingcyan;
            tuned_localizer_color = color_lime;
            silent_localizer_color = Color.GRAY;
            reference_localizer_color = Color.WHITE;
            receiving_localizer_color = Color.LIGHT_GRAY;
            tuned_ndb_color = color_dodgerblue;
            no_rcv_ndb_color = color_dodgerblue;
            tuned_vor_color = color_lime;
            no_rcv_vor_color = color_lime;
            unknown_nav_color = color_lime;
            holding_color = color_magenta;
            traffic_color = Color.WHITE;
            faraway_color = Color.DARK_GRAY;
            pos_label_color = color_boeingcyan;
            tcas_label_color = color_boeingcyan;
            data_label_color = color_boeingcyan;
            fmc_active_color = color_magenta;
            fmc_disp_color = Color.WHITE;
            fmc_other_color = Color.LIGHT_GRAY;
            altitude_arc_color = color_lime;
            fmc_ll_active_color = color_lime;
            fmc_ll_disp_color = color_lime;
            fmc_ll_other_color = color_lime;
            heading_labels_color = color_lime;
            nav_needle_color = color_magenta;
            deviation_scale_color = Color.LIGHT_GRAY;
            markings_color = Color.WHITE;
            dim_markings_color = Color.LIGHT_GRAY;
            range_arc_color = Color.GRAY.brighter();
            dim_label_color = Color.BLACK;
            normal_color = color_lime;
            unusual_color = color_deepskyblue;
            caution_color = color_amber;
            warning_color = Color.RED;
            aircraft_color = Color.WHITE;
            heading_bug_color = color_magenta;
            wind_color = Color.WHITE;
            efb_color = Color.WHITE;
            top_text_color = Color.WHITE;
            grass_color = color_darkgreen;
            hard_color = Color.GRAY;
            sand_color = color_darktan;
            snow_color = Color.LIGHT_GRAY;
            sky_color = color_sky;
            brightsky_color = color_sky.brighter();
            ground_color = color_ground;
            brightground_color = color_ground.brighter();
            instrument_background_color = color_bluegray;
            fpv_color = Color.WHITE;
            clock_color = Color.WHITE;
        }

        if ( border_color.equals(XHSIPreferences.BORDER_BROWN) ) {
            backpanel_color = color_jumbobrown;
            frontpanel_color = color_jumbodarkbrown;
            knobs_color = color_jumbolightbrown;
        } else if ( border_color.equals(XHSIPreferences.BORDER_BLUE) ) {
            backpanel_color = color_airbusback;
            frontpanel_color = color_airbusfront;
            knobs_color = color_airbusknob;
        } else {
            backpanel_color = color_irongray;
            frontpanel_color = color_darkirongray;
            knobs_color = color_windowgray;
        }
        
    }


    public void update_config(Graphics2D g2) {

        // we got here because one of the settings has been changed

        // clear the flags
        this.resized = false;
        this.reconfig = false;

        // anti-aliasing
        this.rendering_hints.put(RenderingHints.KEY_ANTIALIASING, preferences.get_anti_alias() ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
        this.rendering_hints.put(RenderingHints.KEY_TEXT_ANTIALIASING, preferences.get_anti_alias() ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        // define the colors
        set_colors( preferences.get_use_more_color(), preferences.get_border_color() );
        border_gradient = new GradientPaint(
                0, 0, backpanel_color.darker().darker().darker(),
                frame_size.width, frame_size.height , backpanel_color.brighter(),
                true);

        // switch width and height if the instrument is displayed on its side
        if ( ( preferences.get_panel_orientation(this.display_unit) == XHSIPreferences.Orientation.RIGHT )
                || ( preferences.get_panel_orientation(this.display_unit) == XHSIPreferences.Orientation.LEFT ) ) {
            this.frame_size.height = this.component_size.width;
            this.frame_size.width = this.component_size.height;
        }

        // calculate coordinates
        this.border_left = this.border;
        this.border_right = this.border;
        this.border_top = this.border;
        this.border_bottom = this.border;
        if ( preferences.get_panel_square(this.display_unit) ) {
            if ( this.frame_size.width > (this.frame_size.height ) ) {
                this.border_left += ( this.frame_size.width - ( this.frame_size.height  ) ) / 2;
                this.border_right = this.border_left;
            } else {
                this.border_top += ( this.frame_size.height  - this.frame_size.width ) / 2;
                this.border_bottom = this.border_top;
            }
        }
        //this.scaling_factor = Math.min( (float)this.frame_size.height, (float)this.frame_size.width ) / 600.0f;
        this.scaling_factor = Math.min( (float)(frame_size.width - border_left - border_right), (float)(frame_size.height - border_top - border_bottom) ) / 600.0f;
        // if the panel gets smaller than 600px, we _should_ try to reduce the size of fonts and images
        this.shrink_scaling_factor = Math.min(1.0f, this.scaling_factor);
        // things like the line thickness can grow when the panel gets bigger than 600px
        this.grow_scaling_factor = Math.max(1.0f, this.scaling_factor);
        // some screen elements like line widths and the border can grow with the power of 2
        if ( preferences.get_panels_locked() ) {
            this.border = preferences.get_panel_border(this.display_unit);
        } else {
            this.border = (int) (INITIAL_BORDER_SIZE * Math.max(1.0f, Math.pow(Math.min((float) this.frame_size.width, (float) this.frame_size.height) / 600.0f, 2)));
        }

        // the rectangle for our instrument
        panel_rect = new Rectangle(
                border_left,
                border_top,
                frame_size.width - border_left - border_right,
                frame_size.height - border_top - border_bottom
            );

        // a nice frame with rounded corners; it will be painted in InstrumentFrame.java
        Area inner_frame = new Area(new RoundRectangle2D.Float(
                border_left,
                border_top,
                frame_size.width - (border_left + border_right),
                frame_size.height - (border_top + border_bottom),
                (int)(30 * this.grow_scaling_factor),
                (int)(30 * this.grow_scaling_factor)));
        instrument_frame = new Area(new Rectangle2D.Float(0, 0, frame_size.width, frame_size.height));
        instrument_frame.subtract(inner_frame);
        // if the cpu can handle it: a double border
        Area outer_frame = new Area(new RoundRectangle2D.Float(
                border_left - border_left / 3,
                border_top - border_top / 3,
                frame_size.width - (border_left + border_right) + border_left / 3 + border_right / 3,
                frame_size.height - (border_top + border_bottom) + border_top / 3 + border_bottom / 3,
                (int)(30 * this.grow_scaling_factor),
                (int)(30 * this.grow_scaling_factor)));
        instrument_outer_frame = new Area(new Rectangle2D.Float(0, 0, frame_size.width, frame_size.height));
        instrument_outer_frame.subtract(outer_frame);

        // fonts
        set_fonts(g2, this.scaling_factor);

    }


    public int get_text_width(Graphics graphics, Font font, String text) {
        return graphics.getFontMetrics(font).stringWidth(text);
    }


    public int get_text_height(Graphics graphics, Font font) {
        return graphics.getFontMetrics(font).getHeight();
    }


    public void componentResized(ComponentEvent event) {
    }


    public void componentMoved(ComponentEvent event) {
    }


    public void componentShown(ComponentEvent arg0) {
        // TODO Auto-generated method stub
    }


    public void componentHidden(ComponentEvent arg0) {
        // TODO Auto-generated method stub
    }


}
