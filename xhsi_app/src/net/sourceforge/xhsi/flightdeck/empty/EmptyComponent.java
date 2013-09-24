/**
* EmptyComponent.java
* 
* The root awt component. NDComponent creates and manages painting all
* elements of the HSI. NDComponent also creates and updates NDGraphicsConfig
* which is used by all HSI elements to determine positions and sizes.
* 
* This component is notified when new data packets from the flightsimulator
* have been received and performs a repaint. This component is also triggered
* by UIHeartbeat to detect situations without reception.
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

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.logging.Logger;
import javax.swing.JFrame;

import net.sourceforge.xhsi.PreferencesObserver;
import net.sourceforge.xhsi.XHSIPreferences;
import net.sourceforge.xhsi.XHSISettings;
import net.sourceforge.xhsi.XHSIStatus;

import net.sourceforge.xhsi.model.Aircraft;
import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.Observer;

//import net.sourceforge.xhsi.flightdeck.GraphicsConfig;


public class EmptyComponent extends Component implements Observer, PreferencesObserver {

    private static final long serialVersionUID = 1L;
    public static boolean COLLECT_PROFILING_INFORMATION = false;
    public static long NB_OF_PAINTS_BETWEEN_PROFILING_INFO_OUTPUT = 100;
    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");


    // subcomponents --------------------------------------------------------
    ArrayList subcomponents = new ArrayList();
    long[] subcomponent_paint_times = new long[15];
    long total_paint_times = 0;
    long nb_of_paints = 0;
    Graphics2D g2;
    EmptyGraphicsConfig empty_gc;
    ModelFactory model_factory;
    boolean update_since_last_heartbeat = false;
    //StatusMessage status_message_comp;

    Aircraft aircraft;
    Avionics avionics;


    public EmptyComponent(ModelFactory model_factory, int du) {

        this.empty_gc = new EmptyGraphicsConfig(this, du);
        this.model_factory = model_factory;
        this.aircraft = this.model_factory.get_aircraft_instance();
        this.avionics = this.aircraft.get_avionics();

        empty_gc.reconfig = true;

        addComponentListener(empty_gc);
//        subcomponents.add(new EmptyPanel(model_factory, empty_gc, this));

        this.repaint();

    }


    public Dimension getPreferredSize() {
        return new Dimension(EmptyGraphicsConfig.INITIAL_PANEL_SIZE + 2*EmptyGraphicsConfig.INITIAL_BORDER_SIZE, EmptyGraphicsConfig.INITIAL_PANEL_SIZE + 2*EmptyGraphicsConfig.INITIAL_BORDER_SIZE);
    }


    public void paint(Graphics g) {

        drawAll(g);

    }


    public void drawAll(Graphics g) {

        g2 = (Graphics2D)g;
        g2.setRenderingHints(empty_gc.rendering_hints);
        g2.setStroke(new BasicStroke(2.0f));

        if ( XHSIPreferences.get_instance().get_border_style().equalsIgnoreCase(XHSIPreferences.BORDER_LIGHT) ||
                XHSIPreferences.get_instance().get_relief_border() ) {
            g2.setBackground(empty_gc.backpanel_color);
        } else if ( XHSIPreferences.get_instance().get_border_style().equalsIgnoreCase(XHSIPreferences.BORDER_DARK) ) {
            g2.setBackground(empty_gc.frontpanel_color);
        } else {
            g2.setBackground(Color.BLACK);
        }

        // send Graphics object to empty_gc to recompute positions, if necessary because the panel has been resized or a mode setting has been changed
        empty_gc.update_config( g2 );

        // rotate the display
        XHSIPreferences.Orientation orientation = XHSIPreferences.get_instance().get_panel_orientation( this.empty_gc.display_unit );
        if ( orientation == XHSIPreferences.Orientation.LEFT ) {
            g2.rotate(-Math.PI/2.0, empty_gc.frame_size.width/2, empty_gc.frame_size.width/2);
        } else if ( orientation == XHSIPreferences.Orientation.RIGHT ) {
            g2.rotate(Math.PI/2.0, empty_gc.frame_size.height/2, empty_gc.frame_size.height/2);
        } else if ( orientation == XHSIPreferences.Orientation.DOWN ) {
            g2.rotate(Math.PI, empty_gc.frame_size.width/2, empty_gc.frame_size.height/2);
        }

// adjustable brightness is too slow
//        float alpha = 0.9f;
//        AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC, alpha);
//        g2.setComposite(ac);

        g2.clearRect(0, 0, empty_gc.frame_size.width, empty_gc.frame_size.height);

        long time = 0;
        long paint_time = 0;

        for (int i=0; i<this.subcomponents.size(); i++) {
            if (EmptyComponent.COLLECT_PROFILING_INFORMATION) {
                time = System.currentTimeMillis();
            }

            // paint each of the subcomponents
            ((EmptySubcomponent) this.subcomponents.get(i)).paint(g2);

            if (EmptyComponent.COLLECT_PROFILING_INFORMATION) {
                paint_time = System.currentTimeMillis() - time;
                this.subcomponent_paint_times[i] += paint_time;
                this.total_paint_times += paint_time;
            }
        }

        empty_gc.reconfigured = false;

        this.nb_of_paints += 1;

        if (EmptyComponent.COLLECT_PROFILING_INFORMATION) {
            if (this.nb_of_paints % EmptyComponent.NB_OF_PAINTS_BETWEEN_PROFILING_INFO_OUTPUT == 0) {
                logger.info("Paint profiling info");
                logger.info("=[ Paint profile info begin ]=================================");
                for (int i=0;i<this.subcomponents.size();i++) {
                    logger.info(this.subcomponents.get(i).toString() + ": " +
                            ((1.0f*this.subcomponent_paint_times[i])/(this.nb_of_paints*1.0f)) + "ms " +
                            "(" + ((this.subcomponent_paint_times[i] * 100) / this.total_paint_times) + "%)");
                //    this.subcomponent_paint_times[i] = 0;
                }
                logger.info("Total                    " + (this.total_paint_times/this.nb_of_paints) + "ms \n");
                logger.info("=[ Paint profile info end ]===================================");
                //this.total_paint_times = 0;
                //this.nb_of_paints = 0;
            }
        }
    }


    public void update() {
        repaint();
        this.update_since_last_heartbeat = true;
    }


//    public void heartbeat() {
//        if (this.update_since_last_heartbeat == false) {
//            XHSIStatus.status = XHSIStatus.STATUS_NO_RECEPTION;
//            repaint();
//        } else {
//            XHSIStatus.status = XHSIStatus.STATUS_RECEIVING;
//            this.update_since_last_heartbeat = false;
//            repaint();
//        }
//    }


    public void componentResized() {
    }


    public void preference_changed(String key) {

        logger.finest("Preference changed");
        // if (key.equals(XHSIPreferences.PREF_USE_MORE_COLOR)) {
        // Don't bother checking the preference key that was changed, just reconfig...
        this.empty_gc.reconfig = true;
        repaint();

    }


    public void forceReconfig() {

        componentResized();
        this.empty_gc.reconfig = true;
        repaint();
        
    }
}
