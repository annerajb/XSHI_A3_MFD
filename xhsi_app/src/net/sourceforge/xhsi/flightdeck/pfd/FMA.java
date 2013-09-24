/**
* FMA.java
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
package net.sourceforge.xhsi.flightdeck.pfd;

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



public class FMA extends PFDSubcomponent {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");


    public FMA(ModelFactory model_factory, PFDGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
    }


    public void paint(Graphics2D g2) {
        if ( pfd_gc.powered ) {
            drawBox(g2);
            drawSystemStatus(g2);
            if ( this.avionics.is_x737() ) {
                drawX737FMA(g2);
            } else {
                drawFMA(g2);
            }
        }
    }


    private void drawBox(Graphics2D g2) {

        pfd_gc.setTransparent(g2, this.preferences.get_draw_fullscreen_horizon() || ( this.preferences.get_draw_fullwidth_horizon() && pfd_gc.draw_hsi ) );
        g2.setColor(pfd_gc.instrument_background_color);
        g2.fillRect(pfd_gc.fma_left - 1, pfd_gc.fma_top - 1, pfd_gc.fma_width + 3, pfd_gc.fma_height + 3);
        pfd_gc.setOpaque(g2);
        g2.setColor(pfd_gc.markings_color);
        g2.drawLine(pfd_gc.fma_left + pfd_gc.fma_width*1/3, pfd_gc.fma_top, pfd_gc.fma_left + pfd_gc.fma_width*1/3, pfd_gc.fma_top + pfd_gc.fma_height);
        g2.drawLine(pfd_gc.fma_left + pfd_gc.fma_width*2/3, pfd_gc.fma_top, pfd_gc.fma_left + pfd_gc.fma_width*2/3, pfd_gc.fma_top + pfd_gc.fma_height);

    }


    private void drawSystemStatus(Graphics2D g2) {

        int ap_mode = this.avionics.autopilot_mode();

        if ( ap_mode > 0 ) {
            String ss_str = ap_mode == 1 ? "FD" : "CMD";
            int ss_x = pfd_gc.adi_cx - pfd_gc.get_text_width(g2, pfd_gc.font_xxl, ss_str) / 2;
            int ss_y = pfd_gc.adi_cy - pfd_gc.adi_size_up - pfd_gc.line_height_xxl/2;
            g2.setColor(pfd_gc.color_lime);
            g2.setFont(pfd_gc.font_xxl);
            g2.drawString(ss_str, ss_x, ss_y);
        }

    }


    private void draw1Mode(Graphics2D g2, int col, boolean armed, String mode, boolean framed) {

        int mode_w = pfd_gc.get_text_width(g2, pfd_gc.font_m, mode);
        int mode_x = pfd_gc.fma_left + pfd_gc.fma_width/6 + col*pfd_gc.fma_width/3 - mode_w/2;
        int mode_y;
        g2.setFont(pfd_gc.font_m);
        if ( armed ) {
            mode_y = pfd_gc.fma_top + pfd_gc.fma_height*3/4 + pfd_gc.line_height_m/2 - 2;
            g2.setColor(pfd_gc.markings_color);
        } else {
            mode_y = pfd_gc.fma_top + pfd_gc.fma_height*1/4 + pfd_gc.line_height_m/2 - 2;
            g2.setColor(pfd_gc.color_lime);
        }
        g2.drawString(mode, mode_x, mode_y);
        if ( framed ) g2.drawRect(mode_x-pfd_gc.digit_width_m/2, mode_y - pfd_gc.line_height_m*15/16, mode_w+pfd_gc.digit_width_m, pfd_gc.line_height_m*18/16);

    }


    private void drawX737FMA(Graphics2D g2) {

        // A/T
        if ( this.avionics.x737_mcp_spd() > 0 ) {
            draw1Mode(g2, 0, false, "MCP SPD", this.avionics.x737_mcp_spd()==2);
        } else if ( this.avionics.x737_fmc_spd() > 0 ) {
            draw1Mode(g2, 0, false, "FMC SPD", this.avionics.x737_fmc_spd()==2);
        } else if ( this.avionics.x737_retard() > 0 ) {
            draw1Mode(g2, 0, false, "RETARD", this.avionics.x737_retard()==2);
        } else if ( this.avionics.x737_thr_hld() > 0 ) {
            draw1Mode(g2, 0, false, "THR HOLD", this.avionics.x737_thr_hld()==2);
        } else if ( this.avionics.x737_n1() > 0 ) {
            draw1Mode(g2, 0, false, "N1", this.avionics.x737_n1()==2);
        } else if ( this.avionics.x737_athr_armed() ) {
            draw1Mode(g2, 0, true, "ARM", false);
        }

//logger.warning("HDG:"+this.avionics.x737_hdg());
        // Lateral
        if ( this.avionics.x737_vorloc() > 0 ) {
            draw1Mode(g2, 1, false, "VOR/LOC", this.avionics.x737_vorloc()==2);
        } else if ( this.avionics.x737_hdg() > 0 ) {
            draw1Mode(g2, 1, false, "HDG SEL", this.avionics.x737_hdg()==2);
        } else if ( this.avionics.x737_lnav() > 0 ) {
            draw1Mode(g2, 1, false, "LNAV", this.avionics.x737_lnav()==2);
        } else if ( this.avionics.x737_toga() > 0 ) {
            draw1Mode(g2, 1, false, "TO/GA", this.avionics.x737_toga()==2);
        }
        if ( this.avionics.x737_lnav_armed() > 0 ) {
            draw1Mode(g2, 1, true, "LNAV", this.avionics.x737_lnav_armed()==2);
        } else if ( this.avionics.x737_vorloc_armed() > 0 ) {
            draw1Mode(g2, 1, true, "VOR/LOC", this.avionics.x737_vorloc_armed()==2);
        }


//logger.warning("ALT:"+this.avionics.x737_alt_hld());
        // Vertical
        if ( this.avionics.x737_pitch_spd() > 0 ) {
            draw1Mode(g2, 2, false, "PTCH SPD", this.avionics.x737_pitch_spd()==2);
        } else if ( this.avionics.x737_alt_hld() > 0 ) {
            draw1Mode(g2, 2, false, "ALT HOLD", this.avionics.x737_alt_hld()==2);
        } else if ( this.avionics.x737_vs() > 0 ) {
            draw1Mode(g2, 2, false, "V/S", this.avionics.x737_vs()==2);
        } else if ( this.avionics.x737_vnav_alt() > 0 ) {
            draw1Mode(g2, 2, false, "VNAV ALT", this.avionics.x737_vnav_alt()==2);
        } else if ( this.avionics.x737_vnav_path() > 0 ) {
            draw1Mode(g2, 2, false, "VNAV PTH", this.avionics.x737_vnav_path()==2);
        } else if ( this.avionics.x737_vnav_spd() > 0 ) {
            draw1Mode(g2, 2, false, "VNAV SPD", this.avionics.x737_vnav_spd()==2);
        } else if ( this.avionics.x737_gs() > 0 ) {
            draw1Mode(g2, 2, false, "G/S", this.avionics.x737_gs()==2);
        } else if ( this.avionics.x737_flare() > 0 ) {
            draw1Mode(g2, 2, false, "FLARE", this.avionics.x737_flare()==2);
        } else if ( this.avionics.x737_toga() > 0 ) {
            draw1Mode(g2, 2, false, "TO/GA", this.avionics.x737_toga()==2);
        }
        if ( this.avionics.x737_vs_armed() > 0 ) {
            draw1Mode(g2, 2, true, "V/S", this.avionics.x737_vs_armed()==2);
        } else if ( this.avionics.x737_gs_armed() > 0 ) {
            draw1Mode(g2, 2, true, "G/S", this.avionics.x737_gs_armed()==2);
        } else if ( this.avionics.x737_flare_armed() > 0 ) {
            draw1Mode(g2, 2, true, "FLARE", this.avionics.x737_flare_armed()==2);
        }

    }


    private void drawFMA(Graphics2D g2) {

        String fma_str = "ERROR";

        // Autothrottle
        if ( this.avionics.autothrottle_on() ) {
            fma_str = "MCP SPD";
            draw1Mode(g2, 0, false, fma_str, false);
        } else if ( this.avionics.autothrottle_enabled() ) {
            fma_str = "ARM";
            draw1Mode(g2, 0, true, fma_str, false);
        }

        if ( this.avionics.autopilot_mode() > 0 ) {

            // Lateral

            boolean hdg_sel_on = this.avionics.ap_hdg_sel_on();
            boolean vorloc_on = this.avionics.ap_vorloc_on();
            boolean bc_on = this.avionics.ap_bc_on();
            boolean lnav_on = this.avionics.ap_lnav_on();
            boolean ltoga_on = this.avionics.ap_ltoga_on();
            boolean roll_on = this.avionics.ap_roll_on();

            if ( hdg_sel_on || vorloc_on || bc_on || lnav_on || ltoga_on || roll_on ) {
                if ( hdg_sel_on ) {
                    fma_str = "HDG SEL";
                } else if ( vorloc_on ) {
                    fma_str = "VOR/LOC";
                } else if ( bc_on ) {
                    fma_str = "B/C";
                } else if ( lnav_on ) {
                    fma_str = "LNAV";
                } else if ( ltoga_on ) {
                    fma_str = "TO/GA";
                } else /* if ( roll_on ) */ {
                    fma_str = "WLV";
                }
                draw1Mode(g2, 1, false, fma_str, false);
            }

            boolean vorloc_arm = this.avionics.ap_vorloc_arm();
            boolean bc_arm = this.avionics.ap_bc_arm();
            boolean lnav_arm = this.avionics.ap_lnav_arm();
            boolean ltoga_arm = this.avionics.ap_ltoga_arm(); // huh?

            if ( vorloc_arm || bc_arm || lnav_arm || ltoga_arm ) {
                if ( vorloc_arm ) {
                    fma_str = "VOR/LOC";
                } else if ( bc_arm ) {
                    fma_str = "B/C";
                } else if ( lnav_arm ) {
                    fma_str = "LNAV";
                } else /* if ( ltoga_arm ) */ {
                    fma_str = "TO/GA";
                }
                draw1Mode(g2, 1, true, fma_str, false);
            }

            // Vertical

            boolean alt_hold_on = this.avionics.ap_alt_hold_on();
            boolean vs_on = this.avionics.ap_vs_on();
            boolean gs_on = this.avionics.ap_gs_on();
            boolean vnav_on = this.avionics.ap_vnav_on();
            boolean vtoga_on = this.avionics.ap_vtoga_on();
            boolean flch_on = this.avionics.ap_flch_on();
            boolean pitch_on = this.avionics.ap_pitch_on();

            if ( alt_hold_on || vs_on || gs_on || vnav_on || vtoga_on || flch_on || pitch_on ) {
                if ( vnav_on )  {
                    fma_str = "VNAV PTH";
                } else if ( alt_hold_on ) {
                    fma_str = "ALT HOLD";
                } else if ( vs_on ) {
                    fma_str = "V/S";
                } else if ( gs_on ) {
                    fma_str = "G/S";
                } else if ( vtoga_on ) {
                    fma_str = "TO/GA";
                } else if ( flch_on ) {
                    fma_str = "MCP SPD";
                } else /* if ( pitch_on ) */ {
                    fma_str = "PTCH";
                }
                draw1Mode(g2, 2, false, fma_str, false);
            }

            boolean alt_hold_arm = this.avionics.ap_alt_hold_arm();
            boolean vs_arm = this.avionics.ap_vs_arm();
            boolean gs_arm = this.avionics.ap_gs_arm();
            boolean vnav_arm = this.avionics.ap_vnav_arm();
            boolean vtoga_arm = this.avionics.ap_vtoga_arm();

            if ( alt_hold_arm || vs_arm || gs_arm || vnav_arm || vtoga_arm ) {
                if ( vnav_arm ) {
                    fma_str = "VNAV PTH";
                } else if ( alt_hold_arm ) {
                    fma_str = "ALT HOLD";
                } else if ( vs_arm ) {
                    fma_str = "V/S";
                } else if ( gs_arm ) {
                    fma_str = "G/S";
                } else /* if ( vtoga_arm ) */ {
                    fma_str = "TO/GA";
                }
                draw1Mode(g2, 2, true, fma_str, false);
            }

        }
        
    }


}
