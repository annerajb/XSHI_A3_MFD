/**
* NDComponent.java
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
package net.sourceforge.xhsi.flightdeck.nd;

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


public class NDComponent extends Component implements Observer, PreferencesObserver {

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
    NDGraphicsConfig nd_gc;
    ModelFactory model_factory;
    boolean update_since_last_heartbeat = false;
    //StatusMessage status_message_comp;

    Aircraft aircraft;
    Avionics avionics;


    public NDComponent(ModelFactory model_factory, int du) {

        this.nd_gc = new NDGraphicsConfig(this, du);
        this.model_factory = model_factory;
        this.aircraft = this.model_factory.get_aircraft_instance();
        this.avionics = this.aircraft.get_avionics();

        nd_gc.reconfig = true;

        addComponentListener(nd_gc);
        subcomponents.add(new MovingMap(model_factory, nd_gc, this));
        subcomponents.add(new CDI(model_factory, nd_gc, this));
        subcomponents.add(new AltitudeRangeArc(model_factory, nd_gc, this));
        subcomponents.add(new CDUFixHolding(model_factory, nd_gc, this));
        subcomponents.add(new PositionTrendVector(model_factory, nd_gc));
        subcomponents.add(new ClipRoseArea(model_factory, nd_gc, this));
        subcomponents.add(new CompassRose(model_factory, nd_gc));
        subcomponents.add(new GS(model_factory, nd_gc, this));
        subcomponents.add(new ForegroundMessages(model_factory, nd_gc, this));
        subcomponents.add(new SpeedsLabel(model_factory, nd_gc));
        subcomponents.add(new DestinationLabel(model_factory, nd_gc, this));
        subcomponents.add(new HeadingLabel(model_factory, nd_gc, this));
        subcomponents.add(new APHeading(model_factory, nd_gc));
        subcomponents.add(new RadioHeadingArrows(model_factory, nd_gc));
        subcomponents.add(new RadioLabel(model_factory, nd_gc, this));
        subcomponents.add(new RefSourceLabel(model_factory, nd_gc, this));
        subcomponents.add(new NDFail(model_factory, nd_gc, this));
        subcomponents.add(new NDInstrumentFrame(model_factory, nd_gc));

        this.repaint();

    }


    public Dimension getPreferredSize() {
        return new Dimension(NDGraphicsConfig.INITIAL_PANEL_SIZE + 2*NDGraphicsConfig.INITIAL_BORDER_SIZE, NDGraphicsConfig.INITIAL_PANEL_SIZE + 2*NDGraphicsConfig.INITIAL_BORDER_SIZE);
    }


    public void paint(Graphics g) {

        drawAll(g);

    }


    public void drawAll(Graphics g) {

        g2 = (Graphics2D)g;
        g2.setRenderingHints(nd_gc.rendering_hints);
        //g2.setStroke(new BasicStroke(2.0f));
        g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
        g2.setBackground(nd_gc.background_color);

        // send Graphics object to nd_gc to recompute positions, if necessary because the panel has been resized or a mode setting has been changed
        nd_gc.update_config( g2, this.avionics.map_mode(), this.avionics.map_submode(), this.avionics.map_range(), this.avionics.map_zoomin(), this.avionics.power() );

        // rotate the display
        XHSIPreferences.Orientation orientation = XHSIPreferences.get_instance().get_panel_orientation( this.nd_gc.display_unit );
        if ( orientation == XHSIPreferences.Orientation.LEFT ) {
            g2.rotate(-Math.PI/2.0, nd_gc.frame_size.width/2, nd_gc.frame_size.width/2);
        } else if ( orientation == XHSIPreferences.Orientation.RIGHT ) {
            g2.rotate(Math.PI/2.0, nd_gc.frame_size.height/2, nd_gc.frame_size.height/2);
        } else if ( orientation == XHSIPreferences.Orientation.DOWN ) {
            g2.rotate(Math.PI, nd_gc.frame_size.width/2, nd_gc.frame_size.height/2);
        }

//// adjustable brightness has some undesired side-effects
//float alpha = 0.5f;
//AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
//g2.setComposite(ac);

        g2.clearRect(0, 0, nd_gc.frame_size.width, nd_gc.frame_size.height);

        long time = 0;
        long paint_time = 0;

        for (int i=0; i<this.subcomponents.size(); i++) {
            if (NDComponent.COLLECT_PROFILING_INFORMATION) {
                time = System.currentTimeMillis();
            }

            // paint each of the subcomponents
            ((NDSubcomponent) this.subcomponents.get(i)).paint(g2);

            if (NDComponent.COLLECT_PROFILING_INFORMATION) {
                paint_time = System.currentTimeMillis() - time;
                this.subcomponent_paint_times[i] += paint_time;
                this.total_paint_times += paint_time;
            }
        }

        nd_gc.reconfigured = false;

        this.nb_of_paints += 1;

        if (NDComponent.COLLECT_PROFILING_INFORMATION) {
            if (this.nb_of_paints % NDComponent.NB_OF_PAINTS_BETWEEN_PROFILING_INFO_OUTPUT == 0) {
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
        repaint();
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
        this.nd_gc.reconfig = true;
        repaint();

    }


    public void forceReconfig() {

        componentResized();
        this.nd_gc.reconfig = true;
        repaint();
        
    }
}
