/**
* DestinationAirport.java
* 
* raws the airport chart and shows runway, LOC/ILS and COMM info
* of the destination airport
* 
* Copyright (C) 2011-2013  Marc Rogiers (marrog.123@gmail.com)
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
package net.sourceforge.xhsi.flightdeck.mfd;

import java.awt.BasicStroke;
//import java.awt.Color;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
//import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import java.util.logging.Logger;

import net.sourceforge.xhsi.XHSIPreferences;
//import net.sourceforge.xhsi.XHSISettings;

import net.sourceforge.xhsi.model.Airport;
import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ComRadio;
import net.sourceforge.xhsi.model.FMS;
import net.sourceforge.xhsi.model.FMSEntry;
import net.sourceforge.xhsi.model.Localizer;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.NavigationObjectRepository;
import net.sourceforge.xhsi.model.Runway;
import net.sourceforge.xhsi.model.TaxiChart;

import net.sourceforge.xhsi.model.aptnavdata.AptNavXP900DatTaxiChartBuilder;



public class DestinationAirport extends MFDSubcomponent {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    private static TaxiChart taxi = new TaxiChart();


    private String surfaces[] = {"None0", "Asphalt", "Concrete", "Grass", "Dirt", "Gravel", "None6", "None7", "None8", "None9", "None10", "None11", "Dry lakebed", "Water", "Snow", "Transparent"};
    public static final int RWY_ASPHALT = 1;
    public static final int RWY_CONCRETE = 2;
    public static final int RWY_GRASS = 3;
    public static final int RWY_DIRT = 4;
    public static final int RWY_GRAVEL = 5;
    public static final int RWY_DRY_LAKEBED = 12;
    public static final int RWY_WATER = 13;
    public static final int RWY_SNOW = 14;
    public static final int RWY_TRNSPARENT = 15;

    private DecimalFormat freq_format;


    public DestinationAirport(ModelFactory model_factory, MFDGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);

        freq_format = new DecimalFormat("000.00");
        DecimalFormatSymbols format_symbols = freq_format.getDecimalFormatSymbols();
        format_symbols.setDecimalSeparator('.');
        freq_format.setDecimalFormatSymbols(format_symbols);

    }


    public void paint(Graphics2D g2) {
        if ( mfd_gc.powered && ( this.avionics.get_mfd_mode() == Avionics.MFD_MODE_ARPT ) ) {
            drawDestination(g2, getDestination());
        }
    }


    private String get_nav_dest() {

        // if we are tuned to a localizer, find the airport for that localizer

        String dest_str = "";

        Localizer dest_loc = null;
        int hsi_source = this.avionics.hsi_source();
        // try NAV1 first by default
        int bank = 1;
        if ( hsi_source == Avionics.HSI_SOURCE_NAV2 ) {
            // when NAV2 is our reference source, try that first
            bank = 2;
        }
        dest_loc = this.avionics.get_tuned_localizer(bank);
        if ( dest_loc == null ) {
            // try the other one now
            // (3-bank) to switch from 1 to 2 or from 2 to 1
            dest_loc = this.avionics.get_tuned_localizer(3 - bank);
        }
        if ( dest_loc != null ) {
            // we are tuned to a Localizer, now fetch the airport that goes with it
            dest_str = dest_loc.airport;
        }

        return dest_str;

    }


    private String get_fms_dest() {

       // see if the last waypoint in the FMS is an airport

        String dest_str = "";

        FMSEntry last_wpt = this.avionics.get_fms().get_last_waypoint();
         if ( ( last_wpt != null ) && ( last_wpt.type == FMSEntry.ARPT ) ) {
            dest_str = last_wpt.name;
        }

        return dest_str;

    }


    private String getDestination() {

        String dest_arpt_str = "";

        if ( this.aircraft.on_ground() ) {
            // when we are on the ground, take the nearest airport
            // (which is the airport that we are really at in 99.99% of the cases)
            dest_arpt_str = this.aircraft.get_nearest_arpt();
        }

        if ( dest_arpt_str.equals("") ) {
            // if not, get the airport of the LOC/ILS that we are we tuned to
            dest_arpt_str = get_nav_dest();
        }

        if ( dest_arpt_str.equals("") ) {
            // if not, the destination airport in the FMS
            dest_arpt_str = get_fms_dest();
        }

        if ( dest_arpt_str.equals("") ) {
            // if not, the nearest airport...
            dest_arpt_str = this.aircraft.get_nearest_arpt();
        }

        return dest_arpt_str;

    }


    private void drawDestination(Graphics2D g2, String dest_arpt_str) {

        if ( ! dest_arpt_str.equals("") ) {

            boolean daylight;
            if ( this.preferences.get_preference(XHSIPreferences.PREF_ARPT_CHART_COLOR).equals(XHSIPreferences.ARPT_DIAGRAM_COLOR_AUTO) ) {
                daylight = ! this.aircraft.cockpit_lights();
            } else if ( this.preferences.get_preference(XHSIPreferences.PREF_ARPT_CHART_COLOR).equals(XHSIPreferences.ARPT_DIAGRAM_COLOR_DAY) ) {
                daylight = true;
            } else {
                daylight = false;
            }
            Color text = daylight ? mfd_gc.background_color : mfd_gc.efb_color;
            Color paper = daylight ? Color.WHITE : mfd_gc.background_color;
            Color field = daylight ? mfd_gc.color_verypalegreen : mfd_gc.color_verydarkgreen; // Color.GREEN.darker().darker().darker().darker().darker(); // mfd_gc.color_lavender; //new Color(0xF0F0F0);
            Color taxi_ramp = mfd_gc.hard_color;
            Color hard_rwy = daylight ? mfd_gc.background_color : Color.WHITE;
            g2.setColor(paper);
            g2.fillRect(mfd_gc.panel_rect.x, mfd_gc.panel_rect.y, mfd_gc.panel_rect.width, mfd_gc.panel_rect.height);
            g2.setColor(text);

        
            int chart_x;
            int chart_w;
            int chart_y;
            int chart_h;

            int arpt_size = Math.min(mfd_gc.panel_rect.width, mfd_gc.panel_rect.height);
            int arpt_x = mfd_gc.panel_rect.x + mfd_gc.panel_rect.width*15/16 - mfd_gc.get_text_width(g2, mfd_gc.font_xxl, dest_arpt_str);
            int arpt_y = mfd_gc.panel_rect.y + arpt_size/16;
            g2.setFont(mfd_gc.font_xxl);
            g2.drawString(dest_arpt_str, arpt_x, arpt_y);

            NavigationObjectRepository nor = NavigationObjectRepository.get_instance();
            Airport airport = nor.get_airport(dest_arpt_str.trim());

            if ( airport != null ) {

                g2.setFont(mfd_gc.font_xl);
                arpt_x = mfd_gc.panel_rect.x + arpt_size/32;
                g2.drawString(airport.name, mfd_gc.panel_rect.x + mfd_gc.panel_rect.width/16, arpt_y);
//                g2.drawLine(mfd_gc.panel_rect.x + mfd_gc.panel_rect.width*1/16, arpt_y + mfd_gc.line_height_m/2, mfd_gc.panel_rect.x + mfd_gc.panel_rect.width*15/16, arpt_y + mfd_gc.line_height_m/2);
                g2.drawLine(arpt_x, arpt_y + mfd_gc.line_height_m/2, mfd_gc.panel_rect.x + mfd_gc.panel_rect.width*31/32, arpt_y + mfd_gc.line_height_m/2);
                chart_y = arpt_y + mfd_gc.line_height_m/2;
                g2.setFont(mfd_gc.font_xs);
                arpt_y += mfd_gc.line_height_xs*7/3;
                String elev_str = "elev: " + airport.elev + "ft";
                //g2.drawString(elev_str, mfd_gc.panel_rect.x + mfd_gc.panel_rect.width*31/32 - mfd_gc.get_text_width(g2, mfd_gc.font_s, elev_str), arpt_y);
                g2.drawString(elev_str, arpt_x + arpt_size*3/32, arpt_y);

                //arpt_y += mfd_gc.line_height_s*1/2;
                if ( ! airport.runways.isEmpty() ) {
                    for (int i=0; i<airport.runways.size(); i++) {
                        Runway rwy = (Runway)(airport.runways.get(i));
                        g2.setFont(mfd_gc.font_m);
                        arpt_y += mfd_gc.line_height_m*7/4;
                        g2.drawString(rwy.rwy_num1 + "/" + rwy.rwy_num2, arpt_x, arpt_y);
                        g2.setFont(mfd_gc.font_xxs);
                        String soft_field = ( (rwy.surface!=Runway.RWY_ASPHALT) && (rwy.surface!=Runway.RWY_CONCRETE) ) ? " (S) " : "  ";
                        if ( this.preferences.get_preference(XHSIPreferences.PREF_RWY_LEN_UNITS).equals("meters") ) {
                            g2.drawString(soft_field + Math.round(rwy.length) + " x " + Math.round(rwy.width) + " m", arpt_x + arpt_size*9/64, arpt_y);
                        }
                        if ( this.preferences.get_preference(XHSIPreferences.PREF_RWY_LEN_UNITS).equals("feet") ) {
                            g2.drawString(soft_field + Math.round(rwy.length/0.3048f) + " x " + Math.round(rwy.width/0.3048f) + " ft", arpt_x + arpt_size*9/64, arpt_y);
                        }
                        if ( ! rwy.localizers.isEmpty() ) {
                            arpt_y += mfd_gc.line_height_s*1/4;
                            g2.setFont(mfd_gc.font_xs);
                            for (int l=0; l<rwy.localizers.size(); l++) {
                                Localizer loc = rwy.localizers.get(l);
                                arpt_y += mfd_gc.line_height_s*5/4;
                                g2.drawString("- " + loc.description.substring(0, 3) + " " + loc.rwy, arpt_x + arpt_size*0/64, arpt_y);
                                g2.drawString("- " + loc.ilt, arpt_x + arpt_size*15/128, arpt_y);
                                g2.drawString("- " + freq_format.format(loc.frequency), arpt_x + arpt_size*25/128, arpt_y);
                            }
                        }
                    }
                }
                chart_x = arpt_x + arpt_size*20/64;
                chart_w = mfd_gc.panel_rect.x + mfd_gc.panel_rect.width*31/32 - chart_x;

                arpt_y = mfd_gc.panel_rect.y + mfd_gc.panel_rect.height*62/64;
                int radios = airport.com_radios.size();
                int lines = ( radios + 2 ) / 3;
                if ( ! airport.com_radios.isEmpty() ) {
                    arpt_y -= lines*mfd_gc.line_height_xs*5/4;
                    int com_y = arpt_y + mfd_gc.line_height_xs*3/2;
                    for (int c=0; c<airport.com_radios.size(); c++) {
                        ComRadio com_radio = airport.com_radios.get(c);
                        int com_x = mfd_gc.panel_rect.x+ mfd_gc.panel_rect.width/32 + (c%3)*mfd_gc.panel_rect.width*30/32/3;
                        g2.setFont(mfd_gc.font_xs);
                        g2.setColor(paper);
                        g2.fillRect(com_x, com_y - mfd_gc.line_height_xs, mfd_gc.panel_rect.width/3, mfd_gc.line_height_xs);
                        g2.setColor(text);
                        g2.drawString("  " + freq_format.format(com_radio.frequency), com_x, com_y);
                        g2.setFont(mfd_gc.font_xxs);
                        g2.drawString(" - " + com_radio.callsign, com_x + arpt_size*6/64, com_y);
                        if ( (c%3) == 2 ) com_y += mfd_gc.line_height_xs*5/4;
                    }
                }
                chart_h = arpt_y - chart_y;

                //g2.drawRect(chart_x + chart_w/100, chart_y + chart_h/100, chart_w*98/100, chart_h*98/100);
                g2.drawLine(mfd_gc.panel_rect.x + arpt_size/32, chart_y + chart_h, mfd_gc.panel_rect.x + mfd_gc.panel_rect.width*31/32, chart_y + chart_h);
                g2.drawLine(chart_x, chart_y, chart_x, chart_y + chart_h);
                


                if ( taxi.ready && ! taxi.icao.equals(dest_arpt_str) /*&& (XHSIStatus.nav_db_status.equals(XHSIStatus.STATUS_NAV_DB_LOADED))*/ ) {

                    // we need to load another airport chart

    //logger.warning("I have "+taxi.icao);
    //logger.warning("I request "+dest_arpt_str+" ("+dest_arpt_str.length()+" char)");
                    // redundant: taxi.ready = false;
                    try {
                        AptNavXP900DatTaxiChartBuilder cb = new AptNavXP900DatTaxiChartBuilder(taxi, this.preferences.get_preference(XHSIPreferences.PREF_APTNAV_DIR));
                        logger.warning("\nRequesting "+dest_arpt_str);
                        cb.get_chart(dest_arpt_str);
                    } catch (Exception e) {
                        logger.warning("\nProblem requesting TaxiChartBuilder "+dest_arpt_str);
                    }

                } else if ( taxi.ready && (taxi.airport!=null) && taxi.airport.icao_code.equals(dest_arpt_str) ) {

                    float arpt_lon_width = ( taxi.east_lon - taxi.west_lon ) * 1.1f;
                    float arpt_lat_height = ( taxi.north_lat - taxi.south_lat ) * 1.1f;
                    float arpt_center_lon = ( taxi.east_lon + taxi.west_lon ) / 2.0f;
                    float arpt_center_lat = ( taxi.north_lat + taxi.south_lat ) / 2.0f;

                    float chart_lon_scale;
                    float chart_lat_scale;
                    float chart_metric_scale;
                    int map_width = chart_w;
                    int map_height = chart_h;
//                    Point topleft;

                    if ( ( arpt_lon_width * taxi.lon_scale / map_width ) > ( arpt_lat_height / map_height ) ) {
                        // chart fits horizontally
                        chart_lon_scale = map_width / arpt_lon_width;
                        chart_lat_scale = chart_lon_scale / taxi.lon_scale;
//                        topleft = new Point( mfd_gc.panel_rect.x + mfd_gc.panel_rect.width/10/2, mfd_gc.panel_rect.y + mfd_gc.panel_rect.height/10 + mfd_gc.panel_rect.height*9/100/2 + ( map_height - (int)(arpt_lat_height*chart_lat_scale) ) / 2 );
                        chart_metric_scale = map_width / arpt_lon_width / taxi.lon_scale / 60.0f / 1851.851f;
                    } else {
                        // chart fits vertically
                        chart_lat_scale = map_height / arpt_lat_height;
                        chart_lon_scale = chart_lat_scale * taxi.lon_scale;
//                        topleft = new Point( mfd_gc.panel_rect.x + mfd_gc.panel_rect.width/10/2 + ( map_width - (int)(arpt_lon_width*chart_lon_scale) ) / 2, mfd_gc.panel_rect.y + mfd_gc.panel_rect.height/10 + mfd_gc.panel_rect.height*9/100/2 );
                        chart_metric_scale = map_height / arpt_lat_height / 60.0f / 1851.851f;
                    }

//                float chart_lon_scale;
//                float chart_lat_scale;
//                float chart_metric_scale;

//                int map_width = mfd_gc.panel_rect.width;
//                int map_height = mfd_gc.panel_rect.height;
                    int map_size_px = Math.min(map_width, map_height);

                    Point map_c;

                    AffineTransform original_at = g2.getTransform();

                    double true_heading = Math.toRadians( this.aircraft.heading() - this.aircraft.magnetic_variation() );

                    map_c = new Point( chart_x + chart_w/2, chart_y + chart_h/2 );
    //                    g2.rotate( -true_heading,  map_c.x, map_c.y );

    //                float map_range = (float)this.avionics.map_range() / 100.0f;
    //
    //                chart_lat_scale = map_size_px / map_range * 60.0f;
    //                chart_lon_scale = chart_lat_scale * taxi.lon_scale;
    //                chart_metric_scale = chart_lat_scale / 60.0f / 1851.851f;

                    float acf_lat = this.aircraft.lat();
                    float acf_lon = this.aircraft.lon();

                    if ( taxi.border != null ) {

                        int poly_x[] = new int[taxi.border.nodes.size()];
                        int poly_y[] = new int[taxi.border.nodes.size()];

                            for (int h=0; h<taxi.border.nodes.size(); h++) {
                                TaxiChart.Node node1 = taxi.border.nodes.get(h);
                                poly_x[h] = map_c.x + (int)((node1.lon - arpt_center_lon)*chart_lon_scale);
                                poly_y[h] = map_c.y - (int)((node1.lat - arpt_center_lat)*chart_lat_scale);
                            }
                            g2.setColor(field);
                            g2.fillPolygon(poly_x, poly_y, taxi.border.nodes.size());
                            g2.drawPolygon(poly_x, poly_y, taxi.border.nodes.size());

                            if ( ! taxi.border.holes.isEmpty() ) {

                                for (int k=0; k<taxi.border.holes.size(); k++) {

                                    TaxiChart.Pavement hole1 = taxi.border.holes.get(k);
                                    poly_x = new int[hole1.nodes.size()];
                                    poly_y = new int[hole1.nodes.size()];
                                    for (int l=0; l<hole1.nodes.size(); l++) {
                                        TaxiChart.Node node1 = hole1.nodes.get(l);
                                        poly_x[l] = map_c.x + (int)((node1.lon - arpt_center_lon)*chart_lon_scale);
                                        poly_y[l] = map_c.y - (int)((node1.lat - arpt_center_lat)*chart_lat_scale);
                                    }
                                    g2.setColor(paper);
                                    g2.fillPolygon(poly_x, poly_y, hole1.nodes.size());
                                    g2.drawPolygon(poly_x, poly_y, hole1.nodes.size());

                                } // list of holes

                            } // ! empty list of holes

                    } // ! null border


                    if ( ! taxi.pavements.isEmpty() ) {

                        for (int i=0; i<taxi.pavements.size(); i++) {

                            TaxiChart.Pavement ramp1 = taxi.pavements.get(i);
                            int poly_x[] = new int[ramp1.nodes.size()];
                            int poly_y[] = new int[ramp1.nodes.size()];
                            for (int j=0; j<ramp1.nodes.size(); j++) {
                                TaxiChart.Node node1 = ramp1.nodes.get(j);
                                poly_x[j] = map_c.x + (int)((node1.lon - arpt_center_lon)*chart_lon_scale);
                                poly_y[j] = map_c.y - (int)((node1.lat - arpt_center_lat)*chart_lat_scale);
                            }
                            g2.setColor(taxi_ramp);
                            g2.fillPolygon(poly_x, poly_y, ramp1.nodes.size());
                            g2.drawPolygon(poly_x, poly_y, ramp1.nodes.size());

                            if ( ! ramp1.holes.isEmpty() ) {

                                for (int k=0; k<ramp1.holes.size(); k++) {

                                    TaxiChart.Pavement hole1 = ramp1.holes.get(k);
                                    poly_x = new int[hole1.nodes.size()];
                                    poly_y = new int[hole1.nodes.size()];
                                    for (int l=0; l<hole1.nodes.size(); l++) {
                                        TaxiChart.Node node1 = hole1.nodes.get(l);
                                        poly_x[l] = map_c.x + (int)((node1.lon - arpt_center_lon)*chart_lon_scale);
                                        poly_y[l] = map_c.y - (int)((node1.lat - arpt_center_lat)*chart_lat_scale);
                                    }
                                    if ( taxi.border == null ) {
                                        g2.setColor(paper);
                                    } else {
                                        g2.setColor(field);
                                    }
                                    g2.fillPolygon(poly_x, poly_y, hole1.nodes.size());
                                    g2.drawPolygon(poly_x, poly_y, hole1.nodes.size());

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
                            int s_x = map_c.x + (int)((seg0.lon - arpt_center_lon)*chart_lon_scale);
                            int s_y = map_c.y - (int)((seg0.lat - arpt_center_lat)*chart_lat_scale);
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
                                Color rwy_color;
                                if (rwy0.surface==Runway.RWY_GRASS)
                                    rwy_color = mfd_gc.grass_color;
                                else if ( (rwy0.surface==Runway.RWY_DIRT) || (rwy0.surface==Runway.RWY_GRAVEL) || (rwy0.surface==Runway.RWY_DRY_LAKEBED) )
                                    rwy_color = mfd_gc.sand_color;
                                else if (rwy0.surface==Runway.RWY_SNOW)
                                    rwy_color = mfd_gc.snow_color;
                                else
                                    rwy_color = mfd_gc.hard_color.darker();
                                g2.setColor(rwy_color);
                                Stroke original_stroke = g2.getStroke();
                                g2.setStroke(new BasicStroke(rwy0.width * chart_metric_scale, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
                                int x1 = map_c.x + (int)((rwy0.lon1 - arpt_center_lon)*chart_lon_scale);
                                int y1 = map_c.y - (int)((rwy0.lat1 - arpt_center_lat)*chart_lat_scale);
                                int x2 = map_c.x + (int)((rwy0.lon2 - arpt_center_lon)*chart_lon_scale);
                                int y2 = map_c.y - (int)((rwy0.lat2 - arpt_center_lat)*chart_lat_scale);
                                g2.drawLine( x1, y1, x2, y2);
                                g2.setStroke(original_stroke);
                                //if ( avionics.efis_shows_arpt() ) {
                                g2.setFont(mfd_gc.font_xs);
                                //AffineTransform current_at = g2.getTransform();
                                //g2.rotate( Math.toRadians( this.map_up ), x1, y1 );
                                int h0 = mfd_gc.line_height_xs;
                                int w1 = mfd_gc.get_text_width(g2, mfd_gc.font_xs, rwy0.rwy_num1);
                                x1 -= w1 / 2;
                                y1 += h0 / 2;
                                g2.setColor(paper);
                                g2.fillRect(x1 - 4, y1 - h0 - 3, w1 + 8, h0 + 6);
                                g2.setColor(rwy_color);
                                g2.drawRect(x1 - 4, y1 - h0 - 3, w1 + 8, h0 + 6);
                                g2.drawString(rwy0.rwy_num1, x1, y1 - 2);
                                //g2.setTransform(current_at);
                                //g2.rotate( Math.toRadians( this.map_up ), x2, y2 );
                                int w2 = mfd_gc.get_text_width(g2, mfd_gc.font_xs, rwy0.rwy_num2);
                                x2 -= w2 / 2;
                                y2 += h0 / 2;
                                g2.setColor(paper);
                                g2.fillRect(x2 - 4, y2 - h0 - 3, w2 + 8, h0 + 6);
                                g2.setColor(rwy_color);
                                g2.drawRect(x2 - 4, y2 - h0 - 3, w2 + 8, h0 + 6);
                                g2.drawString(rwy0.rwy_num2, x2, y2 - 2);
                                //g2.setTransform(current_at);
                                //}
                            }
                        }

                        // paved
                        for (int i=0; i<taxi.airport.runways.size(); i++) {
                            Runway rwy0 = taxi.airport.runways.get(i);
                            if ( (rwy0.surface==Runway.RWY_ASPHALT) || (rwy0.surface==Runway.RWY_CONCRETE) ) {
                                g2.setColor(hard_rwy);
                                Stroke original_stroke = g2.getStroke();
                                g2.setStroke(new BasicStroke(rwy0.width * chart_metric_scale, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
                                int x1 = map_c.x + (int)((rwy0.lon1 - arpt_center_lon)*chart_lon_scale);
                                int y1 = map_c.y - (int)((rwy0.lat1 - arpt_center_lat)*chart_lat_scale);
                                int x2 = map_c.x + (int)((rwy0.lon2 - arpt_center_lon)*chart_lon_scale);
                                int y2 = map_c.y - (int)((rwy0.lat2 - arpt_center_lat)*chart_lat_scale);
                                g2.drawLine( x1, y1, x2, y2);
                                g2.setStroke(original_stroke);
                                //if ( avionics.efis_shows_arpt() ) {
                                g2.setFont(mfd_gc.font_xs);
                                //AffineTransform current_at = g2.getTransform();
                                //g2.rotate( Math.toRadians( this.map_up ), x1, y1 );
                                int h0 = mfd_gc.line_height_xs;
                                int w1 = mfd_gc.get_text_width(g2, mfd_gc.font_xs, rwy0.rwy_num1);
                                x1 -= w1 / 2;
                                y1 += h0 / 2;
                                g2.setColor(paper);
                                g2.fillRect(x1 - 4, y1 - h0 - 3, w1 + 8, h0 + 6);
                                g2.setColor(hard_rwy);
                                g2.drawRect(x1 - 4, y1 - h0 - 3, w1 + 8, h0 + 6);
                                g2.drawString(rwy0.rwy_num1, x1, y1 - 2);
                                //g2.setTransform(current_at);
                                //g2.rotate( Math.toRadians( this.map_up ), x2, y2 );
                                int w2 = mfd_gc.get_text_width(g2, mfd_gc.font_xs, rwy0.rwy_num2);
                                x2 -= w2 / 2;
                                y2 += h0 / 2;
                                g2.setColor(paper);
                                g2.fillRect(x2 - 4, y2 - h0 - 3, w2 + 8, h0 + 6);
                                g2.setColor(hard_rwy);
                                g2.drawRect(x2 - 4, y2 - h0 - 3, w2 + 8, h0 + 6);
                                g2.drawString(rwy0.rwy_num2, x2, y2 - 2);
                                //g2.setTransform(current_at);
                                //}
                            }
                        }

                    }

                    // moving aircraft symbol
                    Shape original_clipshape = g2.getClip();
                    g2.clipRect(chart_x, chart_y, chart_w, chart_h);
                    int px = map_c.x + (int)((acf_lon - arpt_center_lon)*chart_lon_scale);
                    int py = map_c.y - (int)((acf_lat - arpt_center_lat)*chart_lat_scale);
                    float ps = 1.5f * mfd_gc.grow_scaling_factor;
                    float cy = 105.0f;
                    int plan_x[] = {
                        Math.round(   0.0f * ps / 10.0f ) + px,
                        Math.round(  15.0f * ps / 10.0f ) + px,
                        Math.round(  15.0f * ps / 10.0f ) + px,
                        Math.round(  95.0f * ps / 10.0f ) + px,
                        Math.round(  95.0f * ps / 10.0f ) + px,
                        Math.round(  35.0f * ps / 10.0f ) + px,
                        Math.round(  15.0f * ps / 10.0f ) + px,
                        Math.round(  15.0f * ps / 10.0f ) + px,
                        Math.round(  30.0f * ps / 10.0f ) + px,
                        Math.round(  30.0f * ps / 10.0f ) + px,
                        Math.round(   0.0f * ps / 10.0f ) + px,
                        Math.round( -30.0f * ps / 10.0f ) + px,
                        Math.round( -30.0f * ps / 10.0f ) + px,
                        Math.round( -15.0f * ps / 10.0f ) + px,
                        Math.round( -15.0f * ps / 10.0f ) + px,
                        Math.round( -35.0f * ps / 10.0f ) + px,
                        Math.round( -95.0f * ps / 10.0f ) + px,
                        Math.round( -95.0f * ps / 10.0f ) + px,
                        Math.round( -15.0f * ps / 10.0f ) + px,
                        Math.round( -15.0f * ps / 10.0f ) + px
                    };
                    int plan_y[] = {
                        Math.round( (   0.0f - cy ) * ps / 10.0f ) + py,
                        Math.round( (  25.0f - cy ) * ps / 10.0f ) + py,
                        Math.round( (  75.0f - cy ) * ps / 10.0f ) + py,
                        Math.round( ( 140.0f - cy ) * ps / 10.0f ) + py,
                        Math.round( ( 155.0f - cy ) * ps / 10.0f ) + py,
                        Math.round( ( 125.0f - cy ) * ps / 10.0f ) + py,
                        Math.round( ( 125.0f - cy ) * ps / 10.0f ) + py,
                        Math.round( ( 185.0f - cy ) * ps / 10.0f ) + py,
                        Math.round( ( 200.0f - cy ) * ps / 10.0f ) + py,
                        Math.round( ( 215.0f - cy ) * ps / 10.0f ) + py,
                        Math.round( ( 200.0f - cy ) * ps / 10.0f ) + py,
                        Math.round( ( 215.0f - cy ) * ps / 10.0f ) + py,
                        Math.round( ( 200.0f - cy ) * ps / 10.0f ) + py,
                        Math.round( ( 185.0f - cy ) * ps / 10.0f ) + py,
                        Math.round( ( 125.0f - cy ) * ps / 10.0f ) + py,
                        Math.round( ( 125.0f - cy ) * ps / 10.0f ) + py,
                        Math.round( ( 155.0f - cy ) * ps / 10.0f ) + py,
                        Math.round( ( 140.0f - cy ) * ps / 10.0f ) + py,
                        Math.round( (  75.0f - cy ) * ps / 10.0f ) + py,
                        Math.round( (  25.0f - cy ) * ps / 10.0f ) + py
                    };
                    g2.rotate( true_heading, px, py );
                    g2.setColor(Color.MAGENTA.brighter());
                    g2.fillPolygon(plan_x, plan_y, 20);
                    g2.setColor(Color.MAGENTA.darker());
                    g2.drawPolygon(plan_x, plan_y, 20);
                    g2.setTransform(original_at);
                    g2.setClip(original_clipshape);
                }

            }

        }

    }


}
