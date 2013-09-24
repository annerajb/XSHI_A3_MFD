/**
* MovingMap.java
* 
* Renders all elements of the moving map display: fixes, VORs, NDBs, 
* Airports, Localizers and the programmed FMS route if any. This component
* also renders the map_range marker rings.
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

//import java.awt.AlphaComposite;
import java.awt.BasicStroke;
//import java.awt.Color;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
//import java.awt.GraphicsConfiguration;
import java.awt.Point;
//import java.awt.RenderingHints;
import java.awt.Stroke;
//import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
//import java.awt.geom.Rectangle2D;
//import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.logging.Logger;
//import java.util.HashMap;

import net.sourceforge.xhsi.XHSISettings;
import net.sourceforge.xhsi.XHSIPreferences;

import net.sourceforge.xhsi.model.Airport;
import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.CoordinateSystem;
import net.sourceforge.xhsi.model.FMSEntry;
import net.sourceforge.xhsi.model.Fix;
import net.sourceforge.xhsi.model.Localizer;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.NavigationObject;
import net.sourceforge.xhsi.model.NavigationObjectRepository;
import net.sourceforge.xhsi.model.NavigationRadio;
import net.sourceforge.xhsi.model.RadioNavigationObject;
import net.sourceforge.xhsi.model.RadioNavBeacon;
import net.sourceforge.xhsi.model.Runway;
import net.sourceforge.xhsi.model.TaxiChart;
import net.sourceforge.xhsi.model.TCAS;

import net.sourceforge.xhsi.model.aptnavdata.AptNavXP900DatTaxiChartBuilder;

//import net.sourceforge.xhsi.panel.GraphicsConfig;
//import net.sourceforge.xhsi.panel.Subcomponent;



public class MovingMap extends NDSubcomponent {

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    private static final long serialVersionUID = 1L;
    private static final boolean DRAW_LAT_LON_GRID = false;
    // private BufferedImage fix_image;

    int tfc_size = 7;

    NavigationObjectRepository nor;

    String active_chart_str;

    float map_up;
    float center_lon;
    float center_lat;
    float pixels_per_deg_lon;
    float pixels_per_deg_lat;
    float pixels_per_nm;
    
//    float longdashes[] = { 16.0f, 6.0f };
    float longdashes_1[] = { 16.0f, 6.0f };
    float longdashes_2[] = { 10.0f, 2.0f, 10.0f, 8.0f };
    
//    float shortdashes[] = { 4.0f, 12.0f };
    float shortdashes_1[] = { 4.0f, 12.0f };
    float shortdashes_2[] = { 3.0f, 2.0f, 3.0f, 14.0f };
    
    float dashdots[] = { 18.0f, 5.0f, 4.0f, 5.0f };
    float dashdotdots[] = { 18.0f, 5.0f, 4.0f, 5.0f, 4.0f, 5.0f };
    float dots[] = { 1.0f, 2.0f };
    
    Area panel = null;

    private static TaxiChart taxi = new TaxiChart();

    
    public MovingMap(ModelFactory model_factory, NDGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
        this.nor = NavigationObjectRepository.get_instance();
    }


    public void paint(Graphics2D g2) {

        // don't draw the map in classic-HSI-style APP CTR or VOR CTR
        if ( nd_gc.powered && ! nd_gc.mode_classic_hsi ) {

            // if (this.fix_image == null)
            //    render_navigation_object_images();

            this.active_chart_str = null;
            // taxichart not in plan mode ( ! nd_gc.mode_plan ) and only in zoomedin full map
            if ( nd_gc.map_zoomin && nd_gc.mode_fullmap )
                drawChart(g2);

            // draw the scale rings before drawing the map
            if ( ( ! nd_gc.mode_plan ) && ( this.preferences.get_draw_range_arcs() ) )
                draw_scale_rings(g2);

            // drawing the map over the scale rings
            drawMap(g2, nd_gc.max_range);

        }

        // area to display debug info...
        //// hdef_dot, course, deflection (=dir_degt)
//        int hdef_y = 110;
//        g2.setColor(Color.YELLOW);
//        g2.setFont(nd_gc.font_small);
//        XHSISettings xhsi_settings = XHSISettings.get_instance();
//        g2.drawString("" + (xhsi_settings.map_zoomin ? "TRUE" : "FALSE"), nd_gc.border_left+5, hdef_y);
        //// radio1
        //g2.drawString("" + this.avionics.gps_fromto(), nd_gc.border_left+5, hdef_y);
        //g2.drawString("" + ((float) Math.round(this.avionics.gps_hdef_dot()*10000.0f))/10000.0f, nd_gc.border_left+5, hdef_y+nd_gc.line_height_small);
        //g2.drawString("" + ((float) Math.round(this.avionics.nav1_course()*100.0f))/100.0f, nd_gc.border_left+5, hdef_y+2*nd_gc.line_height_small);
        //if (this.avionics.get_selected_radio(1) != null) g2.drawString("" + this.avionics.get_selected_radio(1).get_rel_bearing(), nd_gc.border_left+5, hdef_y+3*nd_gc.line_height_small);
        //// radio2
        //g2.drawString("" + this.avionics.nav2_fromto(), nd_gc.frame_size.width-nd_gc.border_right-50, hdef_y);
        //g2.drawString("" + ((float) Math.round(this.avionics.nav2_hdef_dot()*10000.0f))/10000.0f, nd_gc.frame_size.width-nd_gc.border_right-50, hdef_y+nd_gc.line_height_small);
        //g2.drawString("" + ((float) Math.round(this.avionics.nav2_course()*100.0f))/100.0f, nd_gc.frame_size.width-nd_gc.border_right-50, hdef_y+2*nd_gc.line_height_small);
        //if (this.avionics.get_selected_radio(2) != null) g2.drawString("" + this.avionics.get_selected_radio(2).get_rel_bearing(), nd_gc.frame_size.width-nd_gc.border_right-50, hdef_y+3*nd_gc.line_height_small);

//        // HDG, TRK, VAR debug info
//        int hdef_y = 300;
//        g2.setColor(Color.YELLOW);
//        g2.setFont(nd_gc.font_small);
//        g2.drawString("HDG mag", nd_gc.border_left+5, hdef_y);
//        g2.drawString(" = " + this.aircraft.heading(), nd_gc.border_left+62, hdef_y);
//        hdef_y += nd_gc.line_height_small;
//        g2.drawString("HPATH  ", nd_gc.border_left+5, hdef_y);
//        g2.drawString(" = " + this.aircraft.hpath(), nd_gc.border_left+62, hdef_y);
//        hdef_y += nd_gc.line_height_small;
//        g2.drawString("TRK mag", nd_gc.border_left+5, hdef_y);
//        g2.drawString(" = " + this.aircraft.track(), nd_gc.border_left+62, hdef_y);
//        hdef_y += nd_gc.line_height_small;
//        g2.drawString("DRIFT  ", nd_gc.border_left+5, hdef_y);
//        g2.drawString(" = " + this.aircraft.drift(), nd_gc.border_left+62, hdef_y);
//        hdef_y += nd_gc.line_height_small;
//        g2.drawString("MAGVAR ", nd_gc.border_left+5, hdef_y);
//        g2.drawString(" = " + this.aircraft.magnetic_variation(), nd_gc.border_left+62, hdef_y);
//        int hdef_y = 200;
//        g2.setColor(Color.YELLOW);
//        g2.setFont(nd_gc.font_small);
//        g2.drawString("MFD : " + this.avionics.get_mfd_mode(), nd_gc.border_left+5, hdef_y);
//        g2.drawString(" = " + this.aircraft.heading(), nd_gc.border_left+62, hdef_y);

    }


//    private void render_navigation_object_images() {
//            System.out.println("rendered images");
//            GraphicsConfiguration gc = this.parent_component.getGraphicsConfiguration();
//            this.fix_image = nd_gc.createCompatibleImage(12,12, Transparency.BITMASK);
//            Graphics2D gImg = (Graphics2D)this.fix_image.getGraphics();
//
//            HashMap rendering_hints = new HashMap();
//                    rendering_hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//            gImg.setRenderingHints(rendering_hints);
//                    gImg.setStroke(new BasicStroke(2.0f));
//            gImg.setComposite(AlphaComposite.Src);
//            gImg.setColor(new Color(0, 0, 0, 0));
//            gImg.fillRect(0, 0, 12,12);
//
//            int x_points_triangle[] = { 0, 11, 6 };
//            int y_points_triangle[] = { 11, 11, 0  };
//            gImg.setColor(Color.WHITE);
//            gImg.drawPolygon(x_points_triangle, y_points_triangle, 3);
//            gImg.dispose();
//    }


    private void drawChart(Graphics2D g2) {

        String nearest_arpt_str = this.aircraft.get_nearest_arpt();

        Color paper = nd_gc.background_color;
        Color field = nd_gc.color_verydarkgreen; // Color.GREEN.darker().darker().darker().darker().darker(); // nd_gc.color_lavender; //new Color(0xF0F0F0);
        Color taxi_ramp = nd_gc.hard_color.darker();
        Color hard_rwy = nd_gc.hard_color.brighter();
//        g2.setColor(paper);
//        g2.fillRect(nd_gc.panel_rect.x, nd_gc.panel_rect.y, nd_gc.panel_rect.width, nd_gc.panel_rect.height);


        if (nearest_arpt_str.length() >= 3) {

            if ( taxi.ready && ! taxi.icao.equals(nearest_arpt_str) /*&& (XHSIStatus.nav_db_status.equals(XHSIStatus.STATUS_NAV_DB_LOADED))*/ ) {

                // we need to load another airport chart
                this.active_chart_str = null;
                try {
                    AptNavXP900DatTaxiChartBuilder cb = new AptNavXP900DatTaxiChartBuilder(this.taxi, this.preferences.get_preference(XHSIPreferences.PREF_APTNAV_DIR));
                    logger.warning("\nRequesting "+nearest_arpt_str);
                    cb.get_chart(nearest_arpt_str);
                } catch (Exception e) {
                    logger.warning("\nProblem requesting TaxiChartBuilder "+nearest_arpt_str);
                }

            } else if ( taxi.ready && (taxi.airport!=null) && taxi.airport.icao_code.equals(nearest_arpt_str) ) {

                this.active_chart_str = nearest_arpt_str;

                // OK, we can draw the nearest airport
                float chart_lon_scale;
                float chart_lat_scale;
                float chart_metric_scale;

//                int map_width = nd_gc.panel_rect.width;
//                int map_height = nd_gc.panel_rect.height;
//                int map_size_px = Math.min(map_width, map_height);

                // double true_heading = Math.toRadians( this.aircraft.heading() - this.aircraft.magnetic_variation() );

                // rotate to TRUE! aircraft heading or track, or North
                AffineTransform original_at = g2.getTransform();
                if ( nd_gc.hdg_up ) {
                    // HDG UP
                    this.map_up = this.aircraft.heading() - this.aircraft.magnetic_variation();
                } else if ( nd_gc.trk_up ) {
                    // TRK UP
                    this.map_up = this.aircraft.track() - this.aircraft.magnetic_variation();
                } else {
                    // North UP
                    this.map_up = 0.0f;
                }
                g2.transform( AffineTransform.getRotateInstance(
                        Math.toRadians(-1.0f * this.map_up),
                        nd_gc.map_center_x,
                        nd_gc.map_center_y)
                );

                Point map_c = new Point( nd_gc.map_center_x, nd_gc.map_center_y );

                float map_range = (float)this.avionics.map_range() / 100.0f;

                chart_lat_scale = nd_gc.mode_centered ? nd_gc.rose_radius * 2.0f / map_range * 60.0f : nd_gc.rose_radius / map_range * 60.0f;
                chart_lon_scale = chart_lat_scale * taxi.lon_scale;
                chart_metric_scale = chart_lat_scale / 60.0f / 1851.851f;

                float acf_lat = this.aircraft.lat();
                float acf_lon = this.aircraft.lon();

// huh?
//                if ( this.avionics.map_mode() == Avionics.EFIS_MAP_EXPANDED ) {
//                }

                if ( taxi.border != null ) {

                    if ( ! this.preferences.get_draw_bezier_pavements() ) {

                        int poly_x[] = new int[taxi.border.nodes.size()];
                        int poly_y[] = new int[taxi.border.nodes.size()];

                        for (int h=0; h<taxi.border.nodes.size(); h++) {
                            TaxiChart.Node node1 = taxi.border.nodes.get(h);
                            poly_x[h] = map_c.x + (int)((node1.lon - acf_lon)*chart_lon_scale);
                            poly_y[h] = map_c.y - (int)((node1.lat - acf_lat)*chart_lat_scale);
                        }
                        g2.setColor(field);
                        g2.fillPolygon(poly_x, poly_y, taxi.border.nodes.size());
                        g2.drawPolygon(poly_x, poly_y, taxi.border.nodes.size());

                    } else {

                        // Bezier

                        Path2D taxishape = new Path2D.Float();
                        TaxiChart.Node last_node = null;
                        for (int h=0; h<taxi.border.nodes.size(); h++) {
                            TaxiChart.Node node1 = taxi.border.nodes.get(h);
                            if ( h == 0 ) {
                                taxishape.moveTo(
                                        map_c.x + ((node1.lon - acf_lon)*chart_lon_scale),
                                        map_c.y - ((node1.lat - acf_lat)*chart_lat_scale)
                                    );
                                last_node = node1;
                            } else {

                                if ( ( node1.lat != last_node.lat ) || ( node1.lon != last_node.lon ) ) {

                                    // this node is different from the previous, so we can draw ... something

                                    if ( node1.cubic_bezier ) {
                                        taxishape.curveTo(
                                                map_c.x + ((node1.quad_lon - acf_lon)*chart_lon_scale),
                                                map_c.y - ((node1.quad_lat - acf_lat)*chart_lat_scale),
                                                map_c.x + ((node1.cubic_lon - acf_lon)*chart_lon_scale),
                                                map_c.y - ((node1.cubic_lat - acf_lat)*chart_lat_scale),
                                                map_c.x + ((node1.lon - acf_lon)*chart_lon_scale),
                                                map_c.y - ((node1.lat - acf_lat)*chart_lat_scale)
                                            );
                                    } else if ( node1.quad_bezier ) {
                                        taxishape.quadTo(
                                                map_c.x + ((node1.quad_lon - acf_lon)*chart_lon_scale),
                                                map_c.y - ((node1.quad_lat - acf_lat)*chart_lat_scale),
                                                map_c.x + ((node1.lon - acf_lon)*chart_lon_scale),
                                                map_c.y - ((node1.lat - acf_lat)*chart_lat_scale)
                                            );
                                    } else {
                                        taxishape.lineTo(
                                                map_c.x + ((node1.lon - acf_lon)*chart_lon_scale),
                                                map_c.y - ((node1.lat - acf_lat)*chart_lat_scale)
                                            );
                                    }

                                    last_node = node1;

                                }

                            }
                        }
                        taxishape.closePath();
                        g2.setColor(field);
                        g2.fill(taxishape);
                        g2.draw(taxishape);

                    }

                    if ( ! taxi.border.holes.isEmpty() ) {

                        for (int k=0; k<taxi.border.holes.size(); k++) {

                            TaxiChart.Pavement hole1 = taxi.border.holes.get(k);
                            if ( ! this.preferences.get_draw_bezier_pavements() ) {

                                int poly_x[] = new int[hole1.nodes.size()];
                                int poly_y[] = new int[hole1.nodes.size()];
                                for (int l=0; l<hole1.nodes.size(); l++) {
                                    TaxiChart.Node node1 = hole1.nodes.get(l);
                                    poly_x[l] = map_c.x + (int)((node1.lon - acf_lon)*chart_lon_scale);
                                    poly_y[l] = map_c.y - (int)((node1.lat - acf_lat)*chart_lat_scale);
                                }
                                g2.setColor(paper);
                                g2.fillPolygon(poly_x, poly_y, hole1.nodes.size());
                                g2.drawPolygon(poly_x, poly_y, hole1.nodes.size());

                            } else {

                                // Bezier
                                Path2D taxishape = new Path2D.Float();
                                TaxiChart.Node last_node = null;
                                for (int h=0; h<hole1.nodes.size(); h++) {

                                    TaxiChart.Node node1 = hole1.nodes.get(h);
                                    if ( h == 0 ) {
                                        taxishape.moveTo(
                                                map_c.x + ((node1.lon - acf_lon)*chart_lon_scale),
                                                map_c.y - ((node1.lat - acf_lat)*chart_lat_scale)
                                            );
                                        last_node = node1;
                                    } else {

                                        if ( ( node1.lat != last_node.lat ) || ( node1.lon != last_node.lon ) ) {

                                            // this node is different from the previous, so we can draw ... something

                                            if ( node1.cubic_bezier ) {
                                                taxishape.curveTo(
                                                        map_c.x + ((node1.quad_lon - acf_lon)*chart_lon_scale),
                                                        map_c.y - ((node1.quad_lat - acf_lat)*chart_lat_scale),
                                                        map_c.x + ((node1.cubic_lon - acf_lon)*chart_lon_scale),
                                                        map_c.y - ((node1.cubic_lat - acf_lat)*chart_lat_scale),
                                                        map_c.x + ((node1.lon - acf_lon)*chart_lon_scale),
                                                        map_c.y - ((node1.lat - acf_lat)*chart_lat_scale)
                                                    );
                                            } else if ( node1.quad_bezier ) {
                                                taxishape.quadTo(
                                                        map_c.x + ((node1.quad_lon - acf_lon)*chart_lon_scale),
                                                        map_c.y - ((node1.quad_lat - acf_lat)*chart_lat_scale),
                                                        map_c.x + ((node1.lon - acf_lon)*chart_lon_scale),
                                                        map_c.y - ((node1.lat - acf_lat)*chart_lat_scale)
                                                    );
                                            } else {
                                                taxishape.lineTo(
                                                        map_c.x + ((node1.lon - acf_lon)*chart_lon_scale),
                                                        map_c.y - ((node1.lat - acf_lat)*chart_lat_scale)
                                                    );
                                            }

                                            last_node = node1;

                                        }

                                    }
                                }
                                taxishape.closePath();
                                g2.setColor(paper);
                                g2.fill(taxishape);
                                g2.draw(taxishape);

                            }
                        } // list of holes

                    } // ! empty list of holes

                } // ! null border


                if ( ! taxi.pavements.isEmpty() ) {

                    for (int i=0; i<taxi.pavements.size(); i++) {

                        TaxiChart.Pavement ramp1 = taxi.pavements.get(i);

                        if ( ! this.preferences.get_draw_bezier_pavements() ) {

                            int poly_x[] = new int[ramp1.nodes.size()];
                            int poly_y[] = new int[ramp1.nodes.size()];
                            for (int j=0; j<ramp1.nodes.size(); j++) {
                                TaxiChart.Node node1 = ramp1.nodes.get(j);
                                poly_x[j] = map_c.x + (int)((node1.lon - acf_lon)*chart_lon_scale);
                                poly_y[j] = map_c.y - (int)((node1.lat - acf_lat)*chart_lat_scale);
                            }
                            g2.setColor(taxi_ramp);
                            g2.fillPolygon(poly_x, poly_y, ramp1.nodes.size());
                            g2.drawPolygon(poly_x, poly_y, ramp1.nodes.size());

                        } else {

                            // Bezier
                            Path2D taxishape = new Path2D.Float();
                            TaxiChart.Node last_node = null;
                            for (int h=0; h<ramp1.nodes.size(); h++) {

                                TaxiChart.Node node1 = ramp1.nodes.get(h);
                                if ( h == 0 ) {
                                    taxishape.moveTo(
                                            map_c.x + ((node1.lon - acf_lon)*chart_lon_scale),
                                            map_c.y - ((node1.lat - acf_lat)*chart_lat_scale)
                                        );
                                    last_node = node1;
                                } else {

                                    if ( ( node1.lat != last_node.lat ) || ( node1.lon != last_node.lon ) ) {

                                        // this node is different from the previous, so we can draw ... something

                                        if ( node1.cubic_bezier ) {
                                            taxishape.curveTo(
                                                    map_c.x + ((node1.quad_lon - acf_lon)*chart_lon_scale),
                                                    map_c.y - ((node1.quad_lat - acf_lat)*chart_lat_scale),
                                                    map_c.x + ((node1.cubic_lon - acf_lon)*chart_lon_scale),
                                                    map_c.y - ((node1.cubic_lat - acf_lat)*chart_lat_scale),
                                                    map_c.x + ((node1.lon - acf_lon)*chart_lon_scale),
                                                    map_c.y - ((node1.lat - acf_lat)*chart_lat_scale)
                                                );
                                        } else if ( node1.quad_bezier ) {
                                            taxishape.quadTo(
                                                    map_c.x + ((node1.quad_lon - acf_lon)*chart_lon_scale),
                                                    map_c.y - ((node1.quad_lat - acf_lat)*chart_lat_scale),
                                                    map_c.x + ((node1.lon - acf_lon)*chart_lon_scale),
                                                    map_c.y - ((node1.lat - acf_lat)*chart_lat_scale)
                                                );
                                        } else {
                                            taxishape.lineTo(
                                                    map_c.x + ((node1.lon - acf_lon)*chart_lon_scale),
                                                    map_c.y - ((node1.lat - acf_lat)*chart_lat_scale)
                                                );
                                        }

                                        // remember this node
                                        last_node = node1;

                                    }

                                }

                            }
                            taxishape.closePath();
                            g2.setColor(taxi_ramp);
                            g2.fill(taxishape);
                            g2.draw(taxishape);

                        }

                        if ( ! ramp1.holes.isEmpty() ) {

                            for (int k=0; k<ramp1.holes.size(); k++) {

                                TaxiChart.Pavement hole1 = ramp1.holes.get(k);

                                if ( ! this.preferences.get_draw_bezier_pavements() ) {

                                    int poly_x[] = new int[hole1.nodes.size()];
                                    int poly_y[] = new int[hole1.nodes.size()];
                                    for (int l=0; l<hole1.nodes.size(); l++) {
                                        TaxiChart.Node node1 = hole1.nodes.get(l);
                                        poly_x[l] = map_c.x + (int)((node1.lon - acf_lon)*chart_lon_scale);
                                        poly_y[l] = map_c.y - (int)((node1.lat - acf_lat)*chart_lat_scale);
                                    }
                                    if ( taxi.border == null ) {
                                        g2.setColor(paper);
                                    } else {
                                        g2.setColor(field);
                                    }
                                    g2.fillPolygon(poly_x, poly_y, hole1.nodes.size());
                                    g2.drawPolygon(poly_x, poly_y, hole1.nodes.size());

                                } else {

                                    // Bezier
                                    Path2D taxishape = new Path2D.Float();
                                    TaxiChart.Node last_node = null;
                                    for (int h=0; h<hole1.nodes.size(); h++) {

                                        TaxiChart.Node node1 = hole1.nodes.get(h);
                                        if ( h == 0 ) {
                                            taxishape.moveTo(
                                                    map_c.x + ((node1.lon - acf_lon)*chart_lon_scale),
                                                    map_c.y - ((node1.lat - acf_lat)*chart_lat_scale)
                                                );
                                            last_node = node1;
                                        } else {

                                            if ( ( node1.lat != last_node.lat ) || ( node1.lon != last_node.lon ) ) {

                                                // this node is different from the previous, so we can draw ... something

                                                if ( node1.cubic_bezier ) {
                                                    taxishape.curveTo(
                                                            map_c.x + ((node1.quad_lon - acf_lon)*chart_lon_scale),
                                                            map_c.y - ((node1.quad_lat - acf_lat)*chart_lat_scale),
                                                            map_c.x + ((node1.cubic_lon - acf_lon)*chart_lon_scale),
                                                            map_c.y - ((node1.cubic_lat - acf_lat)*chart_lat_scale),
                                                            map_c.x + ((node1.lon - acf_lon)*chart_lon_scale),
                                                            map_c.y - ((node1.lat - acf_lat)*chart_lat_scale)
                                                        );
                                                } else if ( node1.quad_bezier ) {
                                                    taxishape.quadTo(
                                                            map_c.x + ((node1.quad_lon - acf_lon)*chart_lon_scale),
                                                            map_c.y - ((node1.quad_lat - acf_lat)*chart_lat_scale),
                                                            map_c.x + ((node1.lon - acf_lon)*chart_lon_scale),
                                                            map_c.y - ((node1.lat - acf_lat)*chart_lat_scale)
                                                        );
                                                } else {
                                                    taxishape.lineTo(
                                                            map_c.x + ((node1.lon - acf_lon)*chart_lon_scale),
                                                            map_c.y - ((node1.lat - acf_lat)*chart_lat_scale)
                                                        );
                                                }

                                                last_node = node1;

                                            }

                                        }
                                    }
                                    taxishape.closePath();
                                    if ( taxi.border == null ) {
                                        g2.setColor(paper);
                                    } else {
                                        g2.setColor(field);
                                    }
                                    g2.fill(taxishape);
                                    g2.draw(taxishape);

                                }

                            } // list of holes

                        } // ! empty list of holes

                    } // list of pavements

                } // ! empty list of pavements


                // APT810-style segments
                if ( taxi.airport != null ) {
                    AffineTransform current_at = g2.getTransform();
                    Stroke original_stroke = g2.getStroke();
                    g2.setColor(taxi_ramp);
                    for (int s=0; s<taxi.segments.size(); s++) {
                        TaxiChart.Segment seg0 = taxi.segments.get(s);
                        int s_x = map_c.x + (int)((seg0.lon - acf_lon)*chart_lon_scale);
                        int s_y = map_c.y - (int)((seg0.lat - acf_lat)*chart_lat_scale);
                        int s_l = (int)(seg0.length*chart_metric_scale/2.0f);
                        int s_y1 = s_y - s_l - 1;
                        int s_y2 = s_y + s_l + 1;
                        g2.setStroke(new BasicStroke(seg0.width * chart_metric_scale, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
                        g2.rotate( Math.toRadians( seg0.orientation ), s_x, s_y );
                        g2.drawLine( s_x, s_y1, s_x, s_y2 );
                        g2.setTransform(current_at);
                    }
                    g2.setStroke(original_stroke);
                }


                // runways
                if ( taxi.airport != null ) {

                    // make sure to draw the paved runways OVER the non-paved runways

                    // non-paved
                    for (int i=0; i<taxi.airport.runways.size(); i++) {
                        Runway rwy0 = taxi.airport.runways.get(i);
                        if ( (rwy0.surface!=Runway.RWY_ASPHALT) && (rwy0.surface!=Runway.RWY_CONCRETE) ) {
                            if (rwy0.surface==Runway.RWY_GRASS)
                                g2.setColor(nd_gc.grass_color);
                            else if ( (rwy0.surface==Runway.RWY_DIRT) || (rwy0.surface==Runway.RWY_GRAVEL) || (rwy0.surface==Runway.RWY_DRY_LAKEBED) )
                                g2.setColor(nd_gc.sand_color);
                            else if (rwy0.surface==Runway.RWY_SNOW)
                                g2.setColor(nd_gc.snow_color);
                            else
                                g2.setColor(nd_gc.hard_color.darker());
                            Stroke original_stroke = g2.getStroke();
                            g2.setStroke(new BasicStroke(rwy0.width * chart_metric_scale, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
                            int x1 = map_c.x + (int)((rwy0.lon1 - acf_lon)*chart_lon_scale);
                            int y1 = map_c.y - (int)((rwy0.lat1 - acf_lat)*chart_lat_scale);
                            int x2 = map_c.x + (int)((rwy0.lon2 - acf_lon)*chart_lon_scale);
                            int y2 = map_c.y - (int)((rwy0.lat2 - acf_lat)*chart_lat_scale);
                            g2.drawLine( x1, y1, x2, y2);
                            g2.setStroke(original_stroke);
                            if ( avionics.efis_shows_arpt() ) {
                                g2.setFont(nd_gc.font_small);
                                AffineTransform current_at = g2.getTransform();
                                g2.rotate( Math.toRadians( this.map_up ), x1, y1 );
                                int h0 = nd_gc.line_height_small;
                                int w1 = nd_gc.get_text_width(g2, nd_gc.font_small, rwy0.rwy_num1);
                                x1 -= w1 / 2;
                                y1 += h0 / 2;
                                g2.clearRect(x1 - 4, y1 - h0 - 3, w1 + 8, h0 + 6);
                                g2.drawRect(x1 - 4, y1 - h0 - 3, w1 + 8, h0 + 6);
                                g2.drawString(rwy0.rwy_num1, x1, y1 - 2);
                                g2.setTransform(current_at);
                                g2.rotate( Math.toRadians( this.map_up ), x2, y2 );
                                int w2 = nd_gc.get_text_width(g2, nd_gc.font_small, rwy0.rwy_num2);
                                x2 -= w2 / 2;
                                y2 += h0 / 2;
                                g2.clearRect(x2 - 4, y2 - h0 - 3, w2 + 8, h0 + 6);
                                g2.drawRect(x2 - 4, y2 - h0 - 3, w2 + 8, h0 + 6);
                                g2.drawString(rwy0.rwy_num2, x2, y2 - 2);
                                g2.setTransform(current_at);
                            }
                        }
                    }

                    // paved
                    for (int i=0; i<taxi.airport.runways.size(); i++) {
                        Runway rwy0 = taxi.airport.runways.get(i);
                        if ( (rwy0.surface==Runway.RWY_ASPHALT) || (rwy0.surface==Runway.RWY_CONCRETE) ) {
                            g2.setColor(hard_rwy);
                            Stroke original_stroke = g2.getStroke();
                            g2.setStroke(new BasicStroke(rwy0.width * chart_metric_scale, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
                            int x1 = map_c.x + (int)((rwy0.lon1 - acf_lon)*chart_lon_scale);
                            int y1 = map_c.y - (int)((rwy0.lat1 - acf_lat)*chart_lat_scale);
                            int x2 = map_c.x + (int)((rwy0.lon2 - acf_lon)*chart_lon_scale);
                            int y2 = map_c.y - (int)((rwy0.lat2 - acf_lat)*chart_lat_scale);
                            g2.drawLine( x1, y1, x2, y2);
                            g2.setStroke(original_stroke);
                            if ( avionics.efis_shows_arpt() ) {
                                g2.setFont(nd_gc.font_small);
                                AffineTransform current_at = g2.getTransform();
                                g2.rotate( Math.toRadians( this.map_up ), x1, y1 );
                                int h0 = nd_gc.line_height_small;
                                int w1 = nd_gc.get_text_width(g2, nd_gc.font_small, rwy0.rwy_num1);
                                x1 -= w1 / 2;
                                y1 += h0 / 2;
                                g2.clearRect(x1 - 4, y1 - h0 - 3, w1 + 8, h0 + 6);
                                g2.drawRect(x1 - 4, y1 - h0 - 3, w1 + 8, h0 + 6);
                                g2.drawString(rwy0.rwy_num1, x1, y1 - 2);
                                g2.setTransform(current_at);
                                g2.rotate( Math.toRadians( this.map_up ), x2, y2 );
                                int w2 = nd_gc.get_text_width(g2, nd_gc.font_small, rwy0.rwy_num2);
                                x2 -= w2 / 2;
                                y2 += h0 / 2;
                                g2.clearRect(x2 - 4, y2 - h0 - 3, w2 + 8, h0 + 6);
                                g2.drawRect(x2 - 4, y2 - h0 - 3, w2 + 8, h0 + 6);
                                g2.drawString(rwy0.rwy_num2, x2, y2 - 2);
                                g2.setTransform(current_at);
                            }
                        }
                    }

                }

                g2.setTransform(original_at);

            }

        }

    }


    private void drawMap(Graphics2D g2, float radius_scale) {

        this.center_lat = this.aircraft.lat();
        this.center_lon = this.aircraft.lon();

        if ( nd_gc.mode_plan ) {
            if ( ( ! this.preferences.get_plan_aircraft_center() ) && this.fms.is_active() ) {
                FMSEntry entry = (FMSEntry) this.fms.get_displayed_waypoint();
                if ( entry == null ) {
                    entry = (FMSEntry) this.fms.get_active_waypoint();
                }
                if ( entry != null ) {
                    // center of plan mode
                    this.center_lat = entry.lat;
                    this.center_lon = entry.lon;
                }
            }
        }
        
        this.pixels_per_nm = (float)nd_gc.rose_radius / radius_scale; // float for better precision
        if ( nd_gc.map_zoomin ) this.pixels_per_nm *= 100.0f;

        // determine max and min lat/lon in viewport to only draw those
        // elements that can be displayed
        float delta_lat = radius_scale * CoordinateSystem.deg_lat_per_nm();
        float delta_lon = radius_scale * CoordinateSystem.deg_lon_per_nm(this.center_lat);
        float range_multiply = this.preferences.get_draw_only_inside_rose() ? 1.0f : 1.5f;
        // multiply by 1.5f to draw symbols outside the rose
        float lat_max = this.center_lat + delta_lat * range_multiply;
        float lat_min = this.center_lat - delta_lat * range_multiply;
        float lon_max = this.center_lon + delta_lon * range_multiply;
        float lon_min = this.center_lon - delta_lon * range_multiply;

        // pixels per degree
        this.pixels_per_deg_lat = nd_gc.rose_radius / delta_lat;
        if ( nd_gc.map_zoomin ) this.pixels_per_deg_lat *= 100.0f;
        this.pixels_per_deg_lon = nd_gc.rose_radius / delta_lon;
        if ( nd_gc.map_zoomin ) this.pixels_per_deg_lon *= 100.0f;

        // rotate to TRUE! aircraft heading or track, or North
        AffineTransform original_at = g2.getTransform();
        if ( nd_gc.hdg_up ) {
            // HDG UP
            this.map_up = this.aircraft.heading() - this.aircraft.magnetic_variation();
        } else if ( nd_gc.trk_up ) {
            // TRK UP
            this.map_up = this.aircraft.track() - this.aircraft.magnetic_variation();
        } else {
            // North UP
            this.map_up = 0.0f;
        }
        g2.transform( AffineTransform.getRotateInstance(
                Math.toRadians(-1.0f * this.map_up),
                nd_gc.map_center_x,
                nd_gc.map_center_y)
        );

//        // a grid of latitude and longitude lines
//        if (DRAW_LAT_LON_GRID) {
//            g2.setFont(nd_gc.font_tiny);
//            g2.setColor(nd_gc.color_ground);
//            for (float i=(float)Math.round(this.center_lon)- 2.0f; i<= (float)Math.round(this.center_lon) + 2.0f; i+=0.1f) {
//                g2.drawLine(
//                    lon_to_x(i), -300,
//                    lon_to_x(i), nd_gc.frame_size.height+300);
//                g2.drawString("" + Math.round(i*10)/10.0f, lon_to_x(i) + 2, (int)(nd_gc.frame_size.height *0.7));
//            }
//            for (float i=(float)Math.round(this.center_lat)- 2.0f; i<= (float)Math.round(this.center_lat) + 2.0f; i+=0.1f) {
//                g2.drawLine(
//                    -300, lat_to_y(i),
//                    nd_gc.frame_size.width+300, lat_to_y(i));
//                g2.drawString("" + Math.round(i*10)/10.0f, (int)(nd_gc.frame_size.height*0.7), lat_to_y(i) -2);
//            }
//        } // end of DRAW_LAT_LON_GRID

        g2.setFont(nd_gc.font_small);

        if ( nd_gc.mode_fullmap ) {

            for (int lat=(int)lat_min; lat<=(int) lat_max; lat++) {
                for (int lon=(int)lon_min; lon<=(int)lon_max; lon++) {

                    if ( avionics.efis_shows_arpt() && ((nd_gc.map_range <= 20)||nd_gc.map_zoomin) && this.preferences.get_draw_runways() ) {
                        draw_nav_objects(
                                g2,
                                NavigationObject.NO_TYPE_RUNWAY,
                                nor.get_nav_objects(NavigationObject.NO_TYPE_RUNWAY, lat, lon)
                            );
                    }

                    if ( avionics.efis_shows_wpt() && ((nd_gc.map_range <= 40)||nd_gc.map_zoomin) ) {
                        draw_nav_objects(
                                g2,
                                NavigationObject.NO_TYPE_FIX,
                                nor.get_nav_objects(NavigationObject.NO_TYPE_FIX, lat, lon)
                            );
                    }

                    if ( avionics.efis_shows_ndb() && ((nd_gc.map_range <= 80)||nd_gc.map_zoomin) ) {
                        draw_nav_objects(
                                g2,
                                NavigationObject.NO_TYPE_NDB,
                                nor.get_nav_objects(NavigationObject.NO_TYPE_NDB, lat, lon)
                            );
                    }

                    if ( avionics.efis_shows_vor() && ((nd_gc.map_range <= 80)||nd_gc.map_zoomin) ) {
                        draw_nav_objects(
                                g2,
                                NavigationObject.NO_TYPE_VOR,
                                nor.get_nav_objects(NavigationObject.NO_TYPE_VOR, lat, lon)
                            );
                    }

                    if ( avionics.efis_shows_arpt() && ((nd_gc.map_range <= 160)||nd_gc.map_zoomin) ) {
                        draw_nav_objects(
                                g2,
                                NavigationObject.NO_TYPE_AIRPORT,
                                nor.get_nav_objects(NavigationObject.NO_TYPE_AIRPORT, lat, lon)
                            );
                    }
                }
            }

        }

        if ( nd_gc.mode_fullmap || ( nd_gc.mode_map /*&& this.avionics.efis_shows_pos()*/ ) ) {

            // whatever the map map_range, draw the tuned VORs and NDBs
            // NAV1 or ADF1
            RadioNavBeacon nav1 = this.avionics.get_tuned_navaid(1);
            if (nav1 != null) {
                float obs1 = this.avionics.nav1_obs();
                int x1 = lon_to_x(nav1.lon);
                int y1 = lat_to_y(nav1.lat);
                if ( nav1.type == RadioNavBeacon.TYPE_VOR) {
                    if ( nav1.has_dme )
                        drawVORDME(g2, x1, y1, nav1, 1, obs1, XHSISettings.get_instance().dme1_radius);
                    else
                        drawVOR(g2, x1, y1, nav1, 1, obs1);
                } else if ( nav1.type == RadioNavBeacon.TYPE_STANDALONE_DME)
                    drawDME(g2, x1, y1, nav1, 1, XHSISettings.get_instance().dme1_radius);
                else if ( nav1.type == RadioNavBeacon.TYPE_NDB)
                    drawNDB(g2, x1, y1, nav1, true);
                if ( avionics.efis_shows_pos() && ( ! nd_gc.mode_plan ) ) {
                    Stroke original_stroke = g2.getStroke();
                    g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dashdots, 0.0f));
                    g2.drawLine(nd_gc.map_center_x, nd_gc.map_center_y, x1, y1);
                    g2.setStroke(original_stroke);
                }
            }

            // NAV2 or ADF2
            RadioNavBeacon nav2 = this.avionics.get_tuned_navaid(2);
            if (nav2 != null) {
                float obs2 = this.avionics.nav2_obs();
                int x2 = lon_to_x(nav2.lon);
                int y2 = lat_to_y(nav2.lat);
                if ( nav2.type == RadioNavBeacon.TYPE_VOR) {
                    if ( nav2.has_dme )
                        drawVORDME(g2, x2, y2, nav2, 2, obs2, XHSISettings.get_instance().dme2_radius);
                    else
                        drawVOR(g2, x2, y2, nav2, 2, obs2);
                } else if ( nav2.type == RadioNavBeacon.TYPE_STANDALONE_DME)
                    drawDME(g2, x2, y2, nav2, 2, XHSISettings.get_instance().dme2_radius);
                else if ( nav2.type == RadioNavBeacon.TYPE_NDB)
                    drawNDB(g2, x2, y2, nav2, true);
                if ( avionics.efis_shows_pos() && ( ! nd_gc.mode_plan ) ) {
                    Stroke original_stroke = g2.getStroke();
                    g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dashdotdots, 0.0f));
                    g2.drawLine(nd_gc.map_center_x, nd_gc.map_center_y, x2, y2);
                    g2.setStroke(original_stroke);
                }
            }

        }

        // whatever the map map_range, draw the tuned (and selected) Localizer
        NavigationRadio nav_radio = null;
        Localizer loc_obj = null;
        Localizer twin_loc_obj = null;
        boolean selected;
        boolean loc_receiving = false;
        float loc_dme_radius = 0.0f;
        int hsi_source = this.avionics.hsi_source();
        if ( nd_gc.mode_fullmap || ( nd_gc.mode_map /*&& this.avionics.efis_shows_pos()*/ && ( hsi_source == Avionics.HSI_SOURCE_NAV1 ) ) ) {
            // Localizer 1
            loc_obj = this.avionics.get_tuned_localizer(1);
            nav_radio = this.avionics.get_nav_radio(1);
            selected = true;
            if ( nd_gc.mode_fullmap && ( nav_radio != this.avionics.get_selected_radio(1) ) ) {
                selected = false;
            }
            loc_dme_radius = XHSISettings.get_instance().dme1_radius;
            if (nav_radio != null) {
                // check reception of this localizer
                loc_receiving = nav_radio.receiving();
            }
            if (loc_obj != null) {
                // yes, the radio, selected by hsi_source, is tuned to a Localizer
                // but first we draw the Localizer with the same frequency, if there is one
                if ( loc_obj.has_twin ) {
                    twin_loc_obj = (Localizer) nor.find_tuned_nav_object(loc_obj.lat, loc_obj.lon, loc_obj.frequency, loc_obj.twin_ilt);
                    if (twin_loc_obj != null) {
                        drawLocalizer(g2, lon_to_x(twin_loc_obj.lon), lat_to_y(twin_loc_obj.lat), twin_loc_obj, 1, selected, false, true, loc_dme_radius);
                    }
                }
                drawLocalizer(g2, lon_to_x(loc_obj.lon), lat_to_y(loc_obj.lat), loc_obj, 1, selected, loc_receiving, false, loc_dme_radius);
//                if ( nd_gc.mode_fullmap ) {
//                    // whatever the map map_range, we draw the airport of this localizer
//                    ARPT dest_arpt = (ARPT)nor.get_airport(((Localizer) loc_obj).airport, loc_obj.lat, loc_obj.lon);
//                    if ( dest_arpt != null ) drawAirport(g2, lon_to_x(dest_arpt.lon), lat_to_y(dest_arpt.lat), dest_arpt, ((Localizer) loc_obj).rwy);
//                }
            }
        }
        if ( nd_gc.mode_fullmap || ( nd_gc.mode_map /*&& this.avionics.efis_shows_pos()*/ && ( hsi_source == Avionics.HSI_SOURCE_NAV2 ) ) ) {
            // Localizer 2
            loc_obj = this.avionics.get_tuned_localizer(2);
            nav_radio = this.avionics.get_nav_radio(2);
            selected = true;
            if ( nd_gc.mode_fullmap && ( nav_radio != this.avionics.get_selected_radio(2) ) ) {
                selected = false;
            }
            loc_dme_radius = XHSISettings.get_instance().dme2_radius;
            if (nav_radio != null) {
                // check reception of this localizer
                loc_receiving = nav_radio.receiving();
            }
            if (loc_obj != null) {
                // yes, the radio, selected by hsi_source, is tuned to a Localizer
                // but first we draw the Localizer with the same frequency, if there is one
                if ( loc_obj.has_twin ) {
                    twin_loc_obj = (Localizer) nor.find_tuned_nav_object(loc_obj.lat, loc_obj.lon, loc_obj.frequency, loc_obj.twin_ilt);
                    if (twin_loc_obj != null) {
                        drawLocalizer(g2, lon_to_x(twin_loc_obj.lon), lat_to_y(twin_loc_obj.lat), twin_loc_obj, 2, selected, false, true, loc_dme_radius);
                    }
                }
                drawLocalizer(g2, lon_to_x(loc_obj.lon), lat_to_y(loc_obj.lat), (Localizer) loc_obj, 2, selected, loc_receiving, false, loc_dme_radius);
//                if ( nd_gc.mode_fullmap ) {
//                    // whatever the map map_range, we draw the airport of this localizer
//                    ARPT dest_arpt = (ARPT)nor.get_airport(((Localizer) loc_obj).airport, loc_obj.lat, loc_obj.lon);
//                    if ( dest_arpt != null ) drawAirport(g2, lon_to_x(dest_arpt.lon), lat_to_y(dest_arpt.lat), dest_arpt, ((Localizer) loc_obj).rwy);
//                }
            }

        }

        // draw FMS route
        if ( this.fms.is_active() && ( nd_gc.mode_fullmap || ( this.avionics.hsi_source() == Avionics.HSI_SOURCE_GPS ) ) ) {
            int nb_of_entries = this.fms.get_nb_of_entries();
            FMSEntry entry;
            FMSEntry next_entry = null;
            boolean inactive = true;
            for (int i=0; i<nb_of_entries; i++) {
                entry = (FMSEntry) this.fms.get_entry(i);
                if ( i < (nb_of_entries-1) ) {
                    next_entry = (FMSEntry) this.fms.get_entry(i+1);
                    if ( ( next_entry != null ) && next_entry.active ) {
                        inactive = false;
                    }
                } else {
                    next_entry = null;
                }
                draw_FMS_entry(g2, entry, next_entry, inactive);
            }
        }

        //g2.setTransform(original_at);

        // TCAS
        //if ( (nd_gc.map_submode != Avionics.EFIS_MAP_PLN)
        if ( true
                && ( ( avionics.transponder_mode() >= Avionics.XPDR_TA) || this.preferences.get_tcas_always_on() )
                && avionics.efis_shows_tfc() ) {
            if ( this.tcas.total > 1 ) {
                // start with number 1, 0 is our own aircraft
                for (int i = 1; i < this.tcas.total; i++) {
                    if ( this.tcas.alarm[i] != TCAS.NONE ) {

                        int tfc_x = lon_to_x( this.tcas.lon[i] );
                        int tfc_y = lat_to_y( this.tcas.lat[i] );
                        AffineTransform pre_tcas_at = g2.getTransform();
                        g2.rotate(Math.toRadians(this.map_up), tfc_x, tfc_y);
                        int diamond_x[] = { tfc_x, tfc_x+tfc_size, tfc_x, tfc_x-tfc_size };
                        int diamond_y[] = { tfc_y-tfc_size-1, tfc_y, tfc_y+tfc_size+1, tfc_y };

                        if ( this.tcas.alarm[i] == TCAS.FARAWAY ) {
                            g2.setColor(nd_gc.faraway_color);
                            g2.drawPolygon(diamond_x, diamond_y, 4);
                        } else if ( this.tcas.alarm[i] == TCAS.OTHER ) {
                            g2.setColor(nd_gc.traffic_color);
                            g2.drawPolygon(diamond_x, diamond_y, 4);
                        } else if ( this.tcas.alarm[i] == TCAS.PROX ) {
                            g2.setColor(nd_gc.traffic_color);
                            g2.fillPolygon(diamond_x, diamond_y, 4);
                        } else if ( this.tcas.alarm[i] == TCAS.TA ) {
                            g2.setColor(nd_gc.caution_color);
                            g2.fillOval( tfc_x-tfc_size, tfc_y-tfc_size, 2*tfc_size, 2*tfc_size );
                        } else {
                            g2.setColor(nd_gc.warning_color);
                            g2.fillRect( tfc_x-tfc_size, tfc_y-tfc_size, 2*tfc_size, 2*tfc_size );
                        }
                        g2.setFont(nd_gc.font_small);
                        if ( this.tcas.rel_alt_100[i] < 0 ) {
                            g2.drawString("" + this.tcas.rel_alt_100[i], tfc_x - tfc_size - 3, tfc_y + tfc_size + 3 + 10);
                        } else {
                            g2.drawString("+" + this.tcas.rel_alt_100[i], tfc_x - tfc_size - 3, tfc_y - tfc_size - 3);
                        }
                        if ( this.tcas.climbing[i] ) {
                            g2.drawLine(tfc_x + tfc_size + 5, tfc_y - tfc_size - 1, tfc_x + tfc_size + 5, tfc_y + tfc_size + 1);
                            g2.drawLine(tfc_x + tfc_size + 5, tfc_y - tfc_size - 1, tfc_x + tfc_size + 5 + 3, tfc_y - tfc_size - 1 + 3);
                            g2.drawLine(tfc_x + tfc_size + 5, tfc_y - tfc_size - 1, tfc_x + tfc_size + 5 - 3, tfc_y - tfc_size - 1 + 3);
                        }
                        if ( this.tcas.descending[i] ) {
                            g2.drawLine(tfc_x + tfc_size + 5, tfc_y - tfc_size - 1, tfc_x + tfc_size + 5, tfc_y + tfc_size + 1);
                            g2.drawLine(tfc_x + tfc_size + 5, tfc_y + tfc_size + 1, tfc_x + tfc_size + 5 + 3, tfc_y + tfc_size + 1 - 3);
                            g2.drawLine(tfc_x + tfc_size + 5, tfc_y + tfc_size + 1, tfc_x + tfc_size + 5 - 3, tfc_y + tfc_size + 1 - 3);
                        }

                        g2.setTransform(pre_tcas_at);

                    }
                }
            }
        }

        g2.setTransform(original_at);

        if ( nd_gc.mode_plan ) {
            // moving aircraft symbol
            int px = lon_to_x(this.aircraft.lon());
            int py = lat_to_y(this.aircraft.lat());
            int ps = Math.round(2.0f * nd_gc.grow_scaling_factor);
            int cy = 105;
            int plan_x[] = {
                  0 * ps / 10 + px,
                 15 * ps / 10 + px,
                 15 * ps / 10 + px,
                 95 * ps / 10 + px,
                 95 * ps / 10 + px,
                 35 * ps / 10 + px,
                 15 * ps / 10 + px,
                 15 * ps / 10 + px,
                 30 * ps / 10 + px,
                 30 * ps / 10 + px,
                  0 * ps / 10 + px,
                -30 * ps / 10 + px,
                -30 * ps / 10 + px,
                -15 * ps / 10 + px,
                -15 * ps / 10 + px,
                -35 * ps / 10 + px,
                -95 * ps / 10 + px,
                -95 * ps / 10 + px,
                -15 * ps / 10 + px,
                -15 * ps / 10 + px
            };
            int plan_y[] = {
                (   0 - cy ) * ps / 10 + py,
                (  25 - cy ) * ps / 10 + py,
                (  75 - cy ) * ps / 10 + py,
                ( 140 - cy ) * ps / 10 + py,
                ( 155 - cy ) * ps / 10 + py,
                ( 125 - cy ) * ps / 10 + py,
                ( 125 - cy ) * ps / 10 + py,
                ( 185 - cy ) * ps / 10 + py,
                ( 200 - cy ) * ps / 10 + py,
                ( 215 - cy ) * ps / 10 + py,
                ( 200 - cy ) * ps / 10 + py,
                ( 215 - cy ) * ps / 10 + py,
                ( 200 - cy ) * ps / 10 + py,
                ( 185 - cy ) * ps / 10 + py,
                ( 125 - cy ) * ps / 10 + py,
                ( 125 - cy ) * ps / 10 + py,
                ( 155 - cy ) * ps / 10 + py,
                ( 140 - cy ) * ps / 10 + py,
                (  75 - cy ) * ps / 10 + py,
                (  25 - cy ) * ps / 10 + py
            };
            g2.rotate(
                    Math.toRadians( this.aircraft.heading() - this.aircraft.magnetic_variation() ),
                    px,
                    py
            );
            g2.setColor(nd_gc.aircraft_color);
            g2.drawPolygon(plan_x, plan_y, 20);
            g2.setTransform(original_at);
        }

    }


    private void draw_scale_rings(Graphics2D g2) {

        // dim the scale rings
        g2.setColor(nd_gc.range_arc_color);
        //for ( int radius=(nd_gc.rose_radius/4); radius < nd_gc.rose_radius; radius += (nd_gc.rose_radius/4) ) {
        for ( int i=1; i<=3; i++ ) {
            int radius = i * nd_gc.rose_radius/4;
            if ( nd_gc.mode_centered || nd_gc.mode_plan ) {
                g2.drawOval( nd_gc.map_center_x - radius, nd_gc.map_center_y - radius, radius*2, radius*2 );
            } else {
                g2.draw(new Arc2D.Float( nd_gc.map_center_x - radius, nd_gc.map_center_y - radius, radius*2, radius*2, 0, 180.0f, Arc2D.OPEN ) );
            }
        }

    }


    private void draw_nav_objects(Graphics2D g2, int type, ArrayList nav_objects) {

        NavigationObject navobj = null;
        RadioNavBeacon rnb;

        for (int i=0; i<nav_objects.size(); i++) {

            navobj = (NavigationObject) nav_objects.get(i);
            int x = lon_to_x(navobj.lon);
            int y = lat_to_y(navobj.lat);
            double dist = Math.hypot( x - nd_gc.map_center_x, y - nd_gc.map_center_y );
            float max_dist = this.preferences.get_draw_only_inside_rose() ? nd_gc.rose_radius : nd_gc.rose_radius * 1.5f;
            if ( dist < max_dist ) {
                
                if (type == NavigationObject.NO_TYPE_NDB) {
                    
                    drawNDB(g2, x, y, (RadioNavBeacon) navobj, false);
                    
                } else if (type == NavigationObject.NO_TYPE_VOR) {
                    
                    rnb = (RadioNavBeacon) navobj;
                    if (rnb.type == RadioNavBeacon.TYPE_VOR) {
                        if ( rnb.has_dme )
                            drawVORDME(g2, x, y, (RadioNavBeacon) navobj, 0, 0.0f, 0.0f);
                        else
                            drawVOR(g2, x, y, (RadioNavBeacon) navobj, 0, 0.0f);
                    } else if (rnb.type == RadioNavBeacon.TYPE_STANDALONE_DME)
                        drawDME(g2, x, y, (RadioNavBeacon) navobj, 0, 0);
                    
                } else if (type == NavigationObject.NO_TYPE_FIX) {
                    
                        drawFix(g2, x, y, (Fix) navobj);
                        
                } else if (type == NavigationObject.NO_TYPE_AIRPORT) {
                    
                        drawAirport(g2, x, y, (Airport) navobj, "");
                        
                } else if ( type == NavigationObject.NO_TYPE_RUNWAY )
                    
                    drawRunway(g2, x, y, (Runway) navobj);
                
            }
        }
    }


    private int lon_to_x(float lon) {
        return Math.round(nd_gc.map_center_x + ((lon - this.center_lon)*pixels_per_deg_lon));
    }


    private int lat_to_y(float lat) {
        return Math.round(nd_gc.map_center_y + ((this.center_lat - lat)*pixels_per_deg_lat));
    }


    private void drawVOR(Graphics2D g2, int x, int y, RadioNavBeacon vor, int bank, float course) {

        int course_line = (int) (vor.range * this.pixels_per_nm);

        // just a big hexagon for VOR without DME
        int x_points_hexagon[] = { x-4, x+4, x+8, x+4, x-4, x-8 };
        int y_points_hexagon[] = { y-7, y-7, y, y+7, y+7, y };

        AffineTransform original_at = g2.getTransform();
        g2.rotate(Math.toRadians(this.map_up), x, y);
        Graphics g = (Graphics) g2;
        if ( bank > 0 )
            g.setColor(nd_gc.tuned_vor_color);
        else
            g.setColor(nd_gc.navaid_color);
        g.drawPolygon(x_points_hexagon, y_points_hexagon, 6);
        g.drawString(vor.ilt, x + 11, y + 13);
        if ( ( bank > 0 ) && ! avionics.efis_shows_pos() ) {
            // the selected course and reciprocal
            g2.setTransform(original_at);
            g2.rotate(Math.toRadians( course + vor.offset ), x, y);
            Stroke original_stroke = g2.getStroke();
            //g2.setStroke(new BasicStroke(1.0f*nd_gc.grow_scaling_factor, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, longdashes, 0.0f));
            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, (bank==1)?longdashes_1:longdashes_2, 0.0f));
            g.drawLine(x, y, x, y + course_line);
            //g2.setStroke(new BasicStroke(1.0f*nd_gc.grow_scaling_factor, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, shortdashes, 0.0f));
            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, (bank==1)?shortdashes_1:shortdashes_2, 0.0f));
            g.drawLine(x, y - course_line, x, y);
            g2.setStroke(original_stroke);
        }
        g2.setTransform(original_at);

    }


    private void drawVORDME(Graphics2D g2, int x, int y, RadioNavBeacon vordme, int bank, float course, float dme_radius) {

        int course_line = (int) (vordme.range * this.pixels_per_nm);

        // a somewhat smaller hexagon with 3 leaves for VOR with DME
        int x_points_hexagon[] = { x-3, x+3, x+6, x+3, x-3, x-6 };
        int y_points_hexagon[] = { y-5, y-5, y, y+5, y+5, y };
        int x_points_ul_leaf[] = { x-6, x-3, x-8, x-11 };
        int y_points_ul_leaf[] = { y,   y-5, y-8, y-3 };
        int x_points_ur_leaf[] = { x+6, x+3, x+8, x+11 };
        int y_points_ur_leaf[] = { y,   y-5, y-8, y-3 };
        int x_points_b_leaf[] =  { x-3, x+3, x+3, x-3 };
        int y_points_b_leaf[] =  { y+5, y+5, y+11, y+11 };

        AffineTransform original_at = g2.getTransform();
        g2.rotate(Math.toRadians(this.map_up), x, y);
        Graphics g = (Graphics) g2;
        if ( bank > 0 )
            g.setColor(nd_gc.tuned_vor_color);
        else
            g.setColor(nd_gc.navaid_color);
        g.drawPolygon(x_points_hexagon, y_points_hexagon, 6);
        g.drawPolygon(x_points_ul_leaf, y_points_ul_leaf, 4);
        g.drawPolygon(x_points_ur_leaf, y_points_ur_leaf, 4);
        g.drawPolygon(x_points_b_leaf, y_points_b_leaf, 4);
        g.drawString(vordme.ilt, x + 11, y + 13);
        if ( bank > 0 ) {
            Stroke original_stroke = g2.getStroke();
            if ( ! avionics.efis_shows_pos() ) {
                // the selected course and reciprocal
                g2.setTransform(original_at);
                g2.rotate(Math.toRadians( course + vordme.offset ), x, y);
                //g2.setStroke(new BasicStroke(1.0f*nd_gc.grow_scaling_factor, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, shortdashes, 0.0f));
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, (bank==1)?shortdashes_1:shortdashes_2, 0.0f));
                g.drawLine(x, y - course_line, x, y);
                //g2.setStroke(new BasicStroke(1.0f*nd_gc.grow_scaling_factor, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, longdashes, 0.0f));
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, (bank==1)?longdashes_1:longdashes_2, 0.0f));
                g.drawLine(x, y, x, y + course_line);
            }
            if ( dme_radius > 0 ) {
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, (bank==1)?longdashes_1:longdashes_2, 0.0f));
                g2.drawOval(x-(int)(dme_radius*this.pixels_per_nm), y-(int)(dme_radius*this.pixels_per_nm), (int)(2*dme_radius*this.pixels_per_nm), (int)(2*dme_radius*this.pixels_per_nm));
            }
            g2.setStroke(original_stroke);
        }
        g2.setTransform(original_at);

    }


    private void drawDME(Graphics2D g2, int x, int y, RadioNavBeacon dme, int bank, float dme_radius) {

        // a sort-of-Y-symbol for a standalone DME or TACAN
        int x_points[] = { x+6, x+11, x+8, x+3, x-3, x-8, x-11, x-6, x-3, x-3,  x+3, x+3 };
        int y_points[] = { y,   y-3,  y-8, y-5, y-5, y-8, y-3,  y,   y+5, y+11, y+11, y+5 };

        AffineTransform original_at = g2.getTransform();
        g2.rotate(Math.toRadians(this.map_up), x, y);
        Graphics g = (Graphics) g2;
        if ( bank > 0 )
            g.setColor(nd_gc.tuned_vor_color);
        else
            g.setColor(nd_gc.navaid_color);
        g.drawPolygon(x_points, y_points, 12);
        g.drawString(dme.ilt, x + 11, y + 13);
        if ( bank > 0 ) {
            Stroke original_stroke = g2.getStroke();
            //g2.setStroke(new BasicStroke(1.0f*nd_gc.grow_scaling_factor, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, longdashes, 0.0f));
            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, (bank==1)?longdashes_1:longdashes_2, 0.0f));
            if ( dme_radius > 0 ) {
                // g2.rotate(Math.toRadians(-this.map_up), x, y);
                g2.drawOval(x-(int)(dme_radius*this.pixels_per_nm), y-(int)(dme_radius*this.pixels_per_nm), (int)(2*dme_radius*this.pixels_per_nm), (int)(2*dme_radius*this.pixels_per_nm));
            }
            g2.setStroke(original_stroke);
        }
        g2.setTransform(original_at);

    }


    private void drawNDB(Graphics2D g2, int x, int y, RadioNavBeacon ndb, boolean tuned) {

        AffineTransform original_at = g2.getTransform();
        g2.rotate(Math.toRadians(this.map_up), x, y);
        Graphics g = (Graphics) g2;
        if (tuned)
            g.setColor(nd_gc.tuned_ndb_color);
        else
            g.setColor(nd_gc.navaid_color);

        // alternative 1: two concentric circles
        //g.drawOval(x-2,y-2,4,4);
        //g.drawOval(x-8,y-8,16,16);

        // alternative 2: a small circle surrounded by dots
        Stroke original_stroke = g2.getStroke();
        g2.setStroke(new BasicStroke(2.0f));
        g2.drawOval(x-4, y-4, 8, 8);
        g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dots, 0.0f));
        g2.drawOval(x-7, y-7, 14, 14);
        g2.drawOval(x-10, y-10, 20, 20);

        g2.setStroke(original_stroke);
        g.drawString(ndb.ilt, x + 11, y + 13);
        g2.setTransform(original_at);

    }


    private void drawFix(Graphics2D g2, int x, int y, Fix fix) {

        int x_points_triangle[] = { x-5, x+5, x };
        int y_points_triangle[] = { y+3, y+3, y-6  };

        AffineTransform original_at = g2.getTransform();
        g2.rotate(Math.toRadians(this.map_up), x, y);
        //g2.drawImage(this.fix_image, x-6,y-6, null);
        if (fix.on_awy)
            g2.setColor(nd_gc.awy_wpt_color);
        else
            g2.setColor(nd_gc.term_wpt_color);
        g2.drawPolygon(x_points_triangle, y_points_triangle, 3);
        if ( (fix.on_awy) || (nd_gc.map_range <= 20) || nd_gc.map_zoomin ) {
            g2.drawString(fix.name, x + 11, y + 13);
        }
        g2.setTransform(original_at);

    }


    private void drawLocalizer(Graphics2D g2, int x, int y, Localizer localizer, int bank, boolean selected, boolean receiving, boolean is_the_twin, float dme_radius) {

        float stroke_width = 2.0f;

        int rwy_frontcourse = (int) (2.0f*this.pixels_per_nm);
        int rwy_backcourse = (int) (0.2f*this.pixels_per_nm);
        int rwy_halfwidth = (int) (0.2f*this.pixels_per_nm);
        // SmartCockpit.com says the line extends to 14.2NM,
        // but we use the map_range that we got from NavigationObjectRepository find_tuned_nav_object
        int localizer_extension = (int) (localizer.range * this.pixels_per_nm);

        int dme_x = 0;
        int dme_y = 0;
        if ( localizer.has_dme ) {
            dme_x = lon_to_x(localizer.dme_lon);
            dme_y = lat_to_y(localizer.dme_lat);
        }

        AffineTransform original_at = g2.getTransform();
        g2.rotate(Math.toRadians(this.map_up), x, y);

        //g2.setColor(nd_gc.receiving_localizer_color);
        if ( receiving ) {
            if ( selected ) {
                g2.setColor(nd_gc.reference_localizer_color);
            } else {
                g2.setColor(nd_gc.receiving_localizer_color);
            }
        } else {
            g2.setColor(nd_gc.silent_localizer_color);
        }

        // identifier
        //g2.drawString(localizer.ilt, x + 12, y + 3);
        int y_offset;
        if ( avionics.efis_shows_data() ) {
            y_offset = is_the_twin ? 4 + nd_gc.line_height_tiny + nd_gc.line_height_small : -4;
            g2.setFont(nd_gc.font_tiny);
            g2.drawString(localizer.name, x - 11 - nd_gc.get_text_width(g2, nd_gc.font_tiny, localizer.name), y + y_offset - nd_gc.line_height_small);
        } else {
            y_offset = is_the_twin ? 4 + nd_gc.line_height_small : -4;
        }
        g2.setFont(nd_gc.font_small);
        g2.drawString(localizer.ilt, x - 11 - nd_gc.get_text_width(g2, nd_gc.font_small, localizer.ilt), y + y_offset);

        Stroke original_stroke = g2.getStroke();
        g2.rotate(Math.toRadians(localizer.bearing - this.map_up), x, y);
        g2.setStroke(new BasicStroke(stroke_width));

        if ( ( nd_gc.map_range < 160 ) || nd_gc.map_zoomin ) {

            if ( ( nd_gc.map_range < 40 ) || nd_gc.map_zoomin ) {
                // the exact location of the Localizer
                g2.drawOval(x-4, y-4, 8, 8);
            }

            // Boeing style: the localizer centerline and two short paralel lines representing the runway
            if ( ! nd_gc.map_zoomin ) {
                g2.drawLine(x-rwy_halfwidth, y-rwy_backcourse, x-rwy_halfwidth, y+rwy_frontcourse);
                g2.drawLine(x+rwy_halfwidth, y-rwy_backcourse, x+rwy_halfwidth, y+rwy_frontcourse);
            }
            g2.setStroke(new BasicStroke(stroke_width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, (bank==1)?shortdashes_1:shortdashes_2, 0.0f));
            g2.drawLine(x, y-rwy_backcourse, x, y-localizer_extension/2);
            g2.setStroke(new BasicStroke(stroke_width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, (bank==1)?longdashes_1:longdashes_2, 0.0f));
            g2.drawLine(x, y+rwy_frontcourse, x, y+localizer_extension);

            g2.setTransform(original_at);
            if ( localizer.has_dme && ( ( nd_gc.map_range < 40 ) || nd_gc.map_zoomin ) ) {
                // the exact location of the DME
                g2.rotate(Math.toRadians(this.map_up), dme_x, dme_y);
                g2.setStroke(new BasicStroke(stroke_width));
                g2.drawRect(dme_x-3, dme_y-3, 6, 6);
            }
        } else {
            // just short line for the localizer when map map_range >= 160
            g2.drawLine(x, y, x, y+60);
        }

        // DME arc
        if ( localizer.has_dme && (dme_radius > 0) ) {
            g2.setStroke(new BasicStroke(stroke_width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, (bank==1)?longdashes_1:longdashes_2, 0.0f));
            //g2.rotate(Math.toRadians(localizer.bearing - this.map_up), x, y);
            g2.drawOval(dme_x-(int)(dme_radius*this.pixels_per_nm), dme_y-(int)(dme_radius*this.pixels_per_nm),
                    (int)(2*dme_radius*this.pixels_per_nm), (int)(2*dme_radius*this.pixels_per_nm));
        }

        g2.setStroke(original_stroke);
        g2.setTransform(original_at);

    }


    private void drawAirport(Graphics2D g2, int x, int y, Airport airport, String runway) {
        if ( airport.longest >= this.preferences.get_min_rwy_length() ) {
            AffineTransform original_at = g2.getTransform();
            Stroke original_stroke = g2.getStroke();
            g2.rotate(Math.toRadians(this.map_up), x, y);
            g2.setStroke(new BasicStroke(3.0f));
            g2.setColor(nd_gc.arpt_color);
            g2.drawOval(x-10, y-10, 20, 20); // with a thicker line and somewhat bigger symbol than the navaids...
            g2.setStroke(original_stroke);
            g2.drawString(airport.icao_code, x + 11, y + 13);
            g2.drawString(runway, x + 11, y + 13 + nd_gc.line_height_small);
            g2.setTransform(original_at);
        }
    }


    private void drawRunway(Graphics2D g2, int x, int y, Runway runway) {

        if ( ! runway.name.equals(this.active_chart_str) ) {

            //OK, this runway is not part of an already drawn airport chart

            Graphics g = (Graphics) g2;
            AffineTransform original_at = g2.getTransform();
            g2.rotate( Math.toRadians( (double) 0 ), x, y );
            Stroke original_stroke = g2.getStroke();
            g2.setStroke(new BasicStroke(runway.width / 10.0f * nd_gc.scaling_factor));
            if ( (runway.surface==Runway.RWY_ASPHALT) || (runway.surface==Runway.RWY_CONCRETE) )
                g.setColor(nd_gc.hard_color);
            else if (runway.surface==Runway.RWY_GRASS)
                g.setColor(nd_gc.grass_color);
            else if ( (runway.surface==Runway.RWY_DIRT) || (runway.surface==Runway.RWY_GRAVEL) || (runway.surface==Runway.RWY_DRY_LAKEBED) )
                g.setColor(nd_gc.sand_color);
            else if (runway.surface==Runway.RWY_SNOW)
                g.setColor(nd_gc.snow_color);
            else
                g.setColor(nd_gc.hard_color);
            g.drawLine(lon_to_x(runway.lon1), lat_to_y(runway.lat1), lon_to_x(runway.lon2), lat_to_y(runway.lat2));
            g2.setStroke(original_stroke);
            g2.setTransform(original_at);

        }

    }


    private void draw_FMS_entry(Graphics2D g2, FMSEntry entry, FMSEntry next_entry, boolean inactive) {

        DecimalFormat eta_hours_formatter = new DecimalFormat("00");
        DecimalFormat eta_minutes_formatter = new DecimalFormat("00");

        int x = lon_to_x(entry.lon);
        int y = lat_to_y(entry.lat);

        if ( (next_entry != null) && ( ! next_entry.name.equals("NTFND") ) ) {
            // draw a line to the next waypoint
            if ( inactive ) {
                g2.setColor(nd_gc.fmc_other_color);
            } else {
                g2.setColor(nd_gc.fmc_active_color);
            }
            Stroke original_stroke = g2.getStroke();
            //g2.setStroke(new BasicStroke(1.0f*nd_gc.grow_scaling_factor));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawLine(x,y, lon_to_x(next_entry.lon), lat_to_y(next_entry.lat));
            g2.setStroke(original_stroke);
        }

        double dist = Math.hypot( x-nd_gc.map_center_x, y-nd_gc.map_center_y );
        float max_dist = this.preferences.get_draw_only_inside_rose() ? nd_gc.rose_radius : nd_gc.rose_radius * 1.5f;
        if ( dist < max_dist ) {

            int x_points_star[] = { x-3, x, x+3, x+13, x+3, x, x-3, x-13, x-3 };
            int y_points_star[] = { y-3, y-13, y-3, y, y+3, y+13, y+3, y, y-3 };

            AffineTransform original_at = g2.getTransform();
            g2.rotate(Math.toRadians(this.map_up), x, y);

            boolean data_inhibit = false;
            int label_y = 13;

            if ( entry.type == 2048 ) {
                if ( entry.name.equals("Lat/Lon") || entry.name.equals("L/L") ) {
                    if (entry.active) {
                        // a small magenta circle for unnamed active waypoints
                        g2.setColor(nd_gc.fmc_active_color);
                        g2.drawOval(x-4, y-4, 8, 8);
                    } else if (entry.displayed)  {
                        g2.setColor(nd_gc.fmc_active_color);
                    } else {
                        g2.setColor(Color.RED);
                        // don't print ETA and altitude restrictions for non-active, non-displayed unnamed waypoints
                        data_inhibit = true;
                    }
                    // no text label for L/L or Lat/Lon
                    label_y -= 1;
                } else {
                    // T/C, T/D, B/D, S/C, ACCEL or DECEL
                    if (entry.active) {
                        g2.setColor(nd_gc.fmc_ll_active_color);
                    } else if (entry.displayed) {
                        g2.setColor(nd_gc.fmc_ll_disp_color);
                    } else {
                        g2.setColor(nd_gc.fmc_ll_other_color);
                    }
                    g2.drawOval(x-6, y-6, 12, 12);
                    g2.drawString(entry.name, x + 11, y + label_y);
                    label_y += nd_gc.line_height_tiny;
                }
            } else {
                // ARPT, VOR, NDB or FIX waypoints
                if (entry.active) {
                    g2.setColor(nd_gc.fmc_active_color);
                } else if (entry.displayed) {
                    g2.setColor(nd_gc.fmc_disp_color);
                } else {
                    g2.setColor(nd_gc.fmc_other_color);
                }
                g2.drawPolygon(x_points_star, y_points_star, 9);
                g2.drawString(entry.name, x + 11, y + label_y);
                label_y += nd_gc.line_height_tiny;
            }

            if ( avionics.efis_shows_data() && !data_inhibit ) {
                g2.setFont(nd_gc.font_tiny);
// the color is already set...
//                if (entry.active) {
//                    g2.setColor(nd_gc.fmc_active_color);
//                } else if (entry.displayed) {
//                    g2.setColor(nd_gc.fmc_disp_color);
//                } else {
//                    g2.setColor(nd_gc.fmc_other_color);
//                }
                if ( entry.altitude != 0 ) {
                    g2.drawString("" + entry.altitude, x + 10, y + label_y);
                    label_y += nd_gc.line_height_tiny;
                }
                if ( entry.total_ete != 0.0f ) {
                    int wpt_eta = Math.round( (float)this.aircraft.time_after_ete(entry.total_ete) / 60.0f );
                    int hours_at_arrival = (wpt_eta / 60) % 24;
                    int minutes_at_arrival = wpt_eta % 60;
                    String eta_text = "" + eta_hours_formatter.format(hours_at_arrival) + eta_minutes_formatter.format(minutes_at_arrival) + "z";
                    g2.drawString(eta_text, x + 10, y + label_y);
                }
                g2.setFont(nd_gc.font_small);
            }

            g2.setTransform(original_at);
        }
    }


}
