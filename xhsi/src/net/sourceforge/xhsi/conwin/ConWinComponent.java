/**
* ConWinComponent.java
* 
* The root awt component. ConWinComponent creates and manages painting all
* elements of the HSI. ConWinComponent also creates and updates ConWinGraphicsConfig
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
package net.sourceforge.xhsi.conwin;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.logging.Logger;

import net.sourceforge.xhsi.PreferencesObserver;
import net.sourceforge.xhsi.XHSIStatus;

import net.sourceforge.xhsi.model.Aircraft;
import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.Observer;

import net.sourceforge.xhsi.flightdeck.Subcomponent;


public class ConWinComponent extends Component implements Observer, PreferencesObserver {

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
    ConWinGraphicsConfig conwin_gc;
    ModelFactory model_factory;
    boolean update_since_last_heartbeat = false;
    //StatusMessage status_message_comp;

    Aircraft aircraft;
    Avionics avionics;


    public ConWinComponent(ModelFactory model_factory) {

        this.conwin_gc = new ConWinGraphicsConfig(this);
        this.model_factory = model_factory;
        this.aircraft = this.model_factory.get_aircraft_instance();
        this.avionics = this.aircraft.get_avionics();

        addComponentListener(conwin_gc);
        subcomponents.add(new StatusBar(model_factory, conwin_gc, this));

    }


    public Dimension getPreferredSize() {
        return new Dimension(ConWinGraphicsConfig.INITIAL_PANEL_WIDTH, ConWinGraphicsConfig.INITIAL_PANEL_HEIGHT);
    }


    public void paint(Graphics g) {

        g2 = (Graphics2D)g;
        g2.setRenderingHints(conwin_gc.rendering_hints);
        g2.setStroke(new BasicStroke(2.0f));
        g2.setBackground(conwin_gc.background_color);

        // send Graphics object to conwin_gc to recompute positions, if necessary because the panel has been resized
        conwin_gc.update_config( g2 );

        g2.clearRect(0,0,conwin_gc.panel_size.width, conwin_gc.panel_size.height);

        long time = 0;
        long paint_time = 0;

        for (int i=0; i<this.subcomponents.size(); i++) {
            if (ConWinComponent.COLLECT_PROFILING_INFORMATION) {
                time = System.currentTimeMillis();
            }
            // paint each of the subcomponents
            ((ConWinSubcomponent) this.subcomponents.get(i)).paint(g2);

            if (ConWinComponent.COLLECT_PROFILING_INFORMATION) {
                paint_time = System.currentTimeMillis() - time;
                this.subcomponent_paint_times[i] += paint_time;
                this.total_paint_times += paint_time;
            }
        }

        this.nb_of_paints += 1;

        if (ConWinComponent.COLLECT_PROFILING_INFORMATION) {
            if (this.nb_of_paints % ConWinComponent.NB_OF_PAINTS_BETWEEN_PROFILING_INFO_OUTPUT == 0) {
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


    public void heartbeat() {
        if (this.update_since_last_heartbeat == false) {
            XHSIStatus.status = XHSIStatus.STATUS_NO_RECEPTION;
            repaint();
        } else {
            XHSIStatus.status = XHSIStatus.STATUS_RECEIVING;
            this.update_since_last_heartbeat = false;
            repaint();
        }
    }


    public void componentResized() {
    }


    public void preference_changed(String key) {

        logger.finest("Preference changed");
        // if (key.equals(XHSIPreferences.PREF_USE_MORE_COLOR)) {
        // Don't bother checking the preference key that was changed, just reconfig...
        this.conwin_gc.reconfig = true;

    }

}
