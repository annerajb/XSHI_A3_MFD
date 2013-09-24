/**
* AptNavXP900DatNavigationObjectBuilder.java
*
* Reads X-Planes earth nav data databases nav.dat, fix.dat and apt.dat and
* stores extracted data in NavigationObjectRepository.
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
package net.sourceforge.xhsi.model.aptnavdata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.logging.Logger;

import net.sourceforge.xhsi.XHSIPreferences;
import net.sourceforge.xhsi.XHSIStatus;
import net.sourceforge.xhsi.PreferencesObserver;
import net.sourceforge.xhsi.ProgressObserver;

import net.sourceforge.xhsi.model.Airport;
import net.sourceforge.xhsi.model.ComRadio;
import net.sourceforge.xhsi.model.CoordinateSystem;
import net.sourceforge.xhsi.model.Fix;
import net.sourceforge.xhsi.model.Localizer;
import net.sourceforge.xhsi.model.NavigationObjectRepository;
import net.sourceforge.xhsi.model.RadioNavigationObject;
import net.sourceforge.xhsi.model.RadioNavBeacon;
import net.sourceforge.xhsi.model.Runway;


public class AptNavXP900DatNavigationObjectBuilder implements PreferencesObserver {

    private String NAV_file = "/earth_nav.dat";
    private String NAV_xplane = "/Resources/default data" + "/earth_nav.dat";
    private String FIX_file = "/earth_fix.dat";
    private String FIX_xplane = "/Resources/default data" + "/earth_fix.dat";
    private String AWY_file = "/earth_awy.dat";
    private String AWY_xplane = "/Resources/default data" + "/earth_awy.dat";
    private String APT_file = "/apt.dat";
    private String APT_xplane = "/Resources/default scenery/default apt dat/Earth nav data" + "/apt.dat";
    private String pathname_to_aptnav;
    private NavigationObjectRepository nor;
    private ProgressObserver progressObserver;
    private Fix fix;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");


    public AptNavXP900DatNavigationObjectBuilder(String pathname_to_aptnav) throws Exception {
        this.pathname_to_aptnav = pathname_to_aptnav;
        this.nor = NavigationObjectRepository.get_instance();
        this.progressObserver = null;
    }


    public void set_progress_observer(ProgressObserver observer) {
        this.progressObserver = observer;
    }


    public void read_all_tables() throws Exception {

        if (new File(this.pathname_to_aptnav).exists()) {
            logger.fine("Start reading AptNav resource files in " + XHSIPreferences.PREF_APTNAV_DIR);

//            this.nor.init();

            if (this.progressObserver != null) {
                this.progressObserver.set_progress("Loading databases", "loading APT", 0.0f);
            }
            read_apt_table();

            if (this.progressObserver != null) {
                this.progressObserver.set_progress("Loading databases", "loading NAV", 25.0f);
            }
            read_nav_table();

            if (this.progressObserver != null) {
                this.progressObserver.set_progress("Loading databases", "loading FIX", 50.0f);
            }
            read_fix_table();

            if (this.progressObserver != null) {
                this.progressObserver.set_progress("Loading databases", "loading AWY", 75.0f);
            }
            read_awy_table();

            if (this.progressObserver != null) {
                this.progressObserver.set_progress("Loading databases", "Done!", 100.0f);
            }
        } else {
            logger.warning("AptNav resources directory is wrong!");
        }

    }


    public void read_apt_table() throws Exception {

        File file;
        if ( new File( this.pathname_to_aptnav + this.APT_xplane ).exists() ) {
            logger.fine("Reading APT database ( " + this.pathname_to_aptnav + this.APT_xplane + " )");
            file = new File( this.pathname_to_aptnav + this.APT_xplane );
        } else {
            logger.fine("Reading APT database ( " + this.pathname_to_aptnav + this.APT_file + " )");
            file = new File( this.pathname_to_aptnav + this.APT_file );
        }
        BufferedReader reader = new BufferedReader( new FileReader( file ));
        String line;
        long line_number = 0;
        int info_type;
        String[] tokens;
        String airport_icao_code = "";
        String airport_name = "";
        boolean current_airport_saved = true;
        ArrayList runways = new ArrayList();
        float width;
        int surface;
        String rwy_num1;
        float thr1_lat;
        float thr1_lon;
        String rwy_num2;
        float thr2_lat;
        float thr2_lon;
        float length;
        float lat = 0;
        float lon = 0;
        float tower_lat = 0;
        float tower_lon = 0;
        float arp_lat = 0;
        float arp_lon = 0;
        float longest = 0;
        float rwy_count = 0;
        float lat_sum = 0;
        float lon_sum = 0;
        float hard_rwy_count = 0;
        float hard_lat_sum = 0;
        float hard_lon_sum = 0;
        boolean tower = false;
        int elev = 0;
        ArrayList<ComRadio> comms = new ArrayList<ComRadio>();

        while ((line = reader.readLine()) != null) {

            if ( line.length() > 0 ) {

                line_number++;

                line = line.trim();
                if ((line_number > 2) && ( ! line.equals("99") ) ) {
                    try {
                        //info_type = Integer.parseInt(line.substring(0, 3).trim());
                        tokens = line.split("\\s+",6);
                        info_type = Integer.parseInt(tokens[0]);
                        if (info_type == 1) {
                            // hold it, save the previous airport before proceeding with this one...
                            if ( ! current_airport_saved ) {
                                // position of the ARP (Aerodrome Reference Point)
                                if (tower) {
                                    // ARP = tower position
                                    arp_lat = tower_lat;
                                    arp_lon = tower_lon;
                                } else if (hard_rwy_count == 1) {
                                    // ARP = the center of the one and only hard runway
                                    arp_lat = hard_lat_sum;
                                    arp_lon = hard_lon_sum;
                                } else if (rwy_count == 1) {
                                    // ARP = the center of the one and only non-hard runway
                                    arp_lat = lat_sum;
                                    arp_lon = lon_sum;
                                } else if (hard_rwy_count > 1) {
                                    // no tower, but several hard runways
                                    // ARP = center of all hard runways
                                    arp_lat = hard_lat_sum / hard_rwy_count;
                                    arp_lon = hard_lon_sum / hard_rwy_count;
                                } else {
                                    // no hard runways and no tower
                                    // ARP = center of all non-hard runways
                                    arp_lat = lat_sum / rwy_count;
                                    arp_lon = lon_sum / rwy_count;
                                }
                                nor.add_nav_object(new Airport(airport_name, airport_icao_code, arp_lat, arp_lon, runways, longest, elev, comms));
                            }
                            // process the new airport header
                            //elev = Integer.parseInt(line.substring(5, 10).trim());
                            elev = Integer.parseInt(tokens[1]);
                            //airport_icao_code = line.substring(15, 19);
                            airport_icao_code = tokens[4];
                            //airport_name = line.substring(20);
                            airport_name = tokens[5];
                            current_airport_saved = false;
                            runways = new ArrayList();
                            arp_lat = 0;
                            arp_lon = 0;
                            longest = 0;
                            rwy_count = 0;
                            lat_sum = 0;
                            lon_sum = 0;
                            hard_rwy_count = 0;
                            hard_lat_sum = 0;
                            hard_lon_sum = 0;
                            tower = false;
                            comms = new ArrayList<ComRadio>();
                            // we dont't save this airport right away, we collect information about the runways first
                        } else if (info_type == 100) {
                            // a runway
                            //if (airport_icao_code.equals("LOWI")) logger.warning("<" + airport_icao_code + "> " + line);
//                            width = Float.parseFloat(line.substring(5, 11));
//                            surface = Integer.parseInt(line.substring(13, 15).trim());
//                            rwy_num1 = line.substring(31, 34).trim();
//                            thr1_lat = Float.parseFloat(line.substring(35, 47));
//                            thr1_lon = Float.parseFloat(line.substring(48, 61));
//                            rwy_num2 = line.substring(87, 90).trim();
//                            thr2_lat = Float.parseFloat(line.substring(91, 103));
//                            thr2_lon = Float.parseFloat(line.substring(104, 117));
                            // we need more tokens
                            tokens = line.split("\\s+",26);
                            width = Float.parseFloat(tokens[1]);
                            surface = Integer.parseInt(tokens[2]);
                            rwy_num1 = tokens[8];
                            thr1_lat = Float.parseFloat(tokens[9]);
                            thr1_lon = Float.parseFloat(tokens[10]);
                            rwy_num2 = tokens[17];
                            thr2_lat = Float.parseFloat(tokens[18]);
                            thr2_lon = Float.parseFloat(tokens[19]);
                            length = CoordinateSystem.rough_distance(thr1_lat, thr1_lon, thr2_lat, thr2_lon) * 1851.852f; // meters!
                            lat = ( thr1_lat + thr2_lat ) / 2;
                            lon = ( thr1_lon + thr2_lon ) / 2;
                            Runway new_rwy = new Runway(airport_icao_code, length, width, surface, rwy_num1, thr1_lat, thr1_lon, rwy_num2, thr2_lat, thr2_lon);
                            nor.add_nav_object(new_rwy);
                            //runways.add( nor.get_runway(airport_icao_code, lat, lon) );
                            runways.add(new_rwy);
                            // find the longest runway for this airport
                            if (length > longest) longest = length;
                            // calculate an average of all threshold lats and lons to define the ARP
                            rwy_count += 1;
                            lat_sum += lat;
                            lon_sum += lon;
                            if ( (surface==Runway.RWY_ASPHALT) || (surface==Runway.RWY_CONCRETE) ) {
                                hard_rwy_count += 1;
                                hard_lat_sum += lat;
                                hard_lon_sum += lon;
                            }
                            //lat = lat_sum / rwy_count;
                            //lon = lon_sum / rwy_count;
                        } else if (info_type == 14) {
                            // if defined in the file, the tower position can be used as the ARP
                            tower = true;
                            // we already have enough tokens tokens = line.split("\\s+",3);
                            tower_lat = Float.parseFloat(tokens[1]);
                            tower_lon = Float.parseFloat(tokens[2]);
                        } else if ( (info_type >= 50) && (info_type < 60) ) {
                            // COM Radio
                            // we need the name, which can include spaces, in the third token
                            tokens = line.split("\\s+",3);
                            comms.add(new ComRadio(airport_icao_code, tokens.length==3?tokens[2]:"", (float)Float.parseFloat(tokens[1]) / 100.0f));
                        } else if ((info_type == 99) && (current_airport_saved == false)) {
                            // end of file, save the last airport
                            if ( ! current_airport_saved ) {
                                // position of the ARP (Aerodrome Reference Point)
                                if (tower) {
                                    // ARP = tower position
                                    arp_lat = tower_lat;
                                    arp_lon = tower_lon;
                                } else if (hard_rwy_count == 1) {
                                    // ARP = the center of the one and only hard runway
                                    arp_lat = hard_lat_sum;
                                    arp_lon = hard_lon_sum;
                                } else if (rwy_count == 1) {
                                    // ARP = the center of the one and only non-hard runway
                                    arp_lat = lat_sum;
                                    arp_lon = lon_sum;
                                } else if (hard_rwy_count > 1) {
                                    // no tower, but several hard runways
                                    // ARP = center of all hard runways
                                    arp_lat = hard_lat_sum / hard_rwy_count;
                                    arp_lon = hard_lon_sum / hard_rwy_count;
                                } else {
                                    // no hard runways and no tower
                                    // ARP = center of all non-hard runways
                                    arp_lat = lat_sum / rwy_count;
                                    arp_lon = lon_sum / rwy_count;
                                }
                                nor.add_nav_object(new Airport(airport_name, airport_icao_code, lat, lon, runways, longest, elev, comms));
                                current_airport_saved = true;
                            }
                        }
                    } catch (Exception e) {
                        logger.warning("\nParse error in " +file.getName() + ":" + line_number + "(" + e + ") " + line);
                    }
                }

            } // line !isEmpty

        } // while readLine

        if (reader != null) {
            reader.close();
        }

    }


    public void read_nav_table() throws Exception {

        File file;
        if ( new File( this.pathname_to_aptnav + this.NAV_xplane ).exists() ) {
            logger.fine("Reading NAV database ( " + this.pathname_to_aptnav + this.NAV_xplane + " )");
            file = new File( this.pathname_to_aptnav + this.NAV_xplane );
        } else {
            logger.fine("Reading NAV database ( " + this.pathname_to_aptnav + this.NAV_file + " )");
            file = new File( this.pathname_to_aptnav + this.NAV_file );
        }
        BufferedReader reader = new BufferedReader( new FileReader( file ));
        String line;
        int info_type;
        String[] tokens;
        long line_number = 0;
        RadioNavigationObject coupled_rno;
        Localizer coupled_loc;
        RadioNavigationObject twin_rno;
        Localizer twin_loc;
        boolean has_a_twin;
        String twin_ilt;

        while ((line = reader.readLine()) != null) {

            if ( line.length() > 0) {
            // line.isEmpty() doesn't work on java 1.5

                line_number++;

                line = line.trim();

                if ( (line_number == 2) && (line.length() >= 32) ) {
                    // the version info is on line 2, hopefully in a fixed location
                    XHSIStatus.nav_db_cycle = line.substring(25, 32);
                } else if ( (line_number > 2)  && ( ! line.equals("99") ) ) {
                    try {
                        
                        tokens = line.split("\\s+",9);
                        info_type = Integer.parseInt(tokens[0]);
                        
                        if ( (info_type ==2) || (info_type == 3) || (info_type == 13) ) {
                            
                            // 2=NDB, 3=VOR (VOR, VOR-DME, VORTAC) 13=DME (Standalone DME, TACAN)
                            // tokens = line.split("\\s+",9);
                            nor.add_nav_object(new RadioNavBeacon(
                                    tokens[8], // name
                                    tokens[7], // ident
                                    info_type,
                                    Float.parseFloat(tokens[1]), // lat
                                    Float.parseFloat(tokens[2]), // lon
                                    Integer.parseInt(tokens[3]), // elev MSL
                                    Float.parseFloat(tokens[4]), // freq
                                    Integer.parseInt(tokens[5]), // range
                                    Float.parseFloat(tokens[6])  // NDB: zero , VOR: offset
                                ));
                            
                        } else if ((info_type == 4) || (info_type == 5)) {
                            
                            // ILS or LOC
                            tokens = line.split("\\s+",11);
                            // search for a twin, i.e. an ILS with the same frequency at the same airport
                            twin_rno = nor.find_tuned_nav_object(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[4])/100.0f, "");
                            has_a_twin = ( (twin_rno != null) && (twin_rno instanceof Localizer) );
                            twin_ilt = "";
                            if ( has_a_twin ) {
                                twin_loc = (Localizer) twin_rno;
                                twin_loc.has_twin = true;
                                twin_loc.twin_ilt = tokens[7];
                                twin_ilt = twin_loc.ilt;
                            }
                            Localizer new_loc = new Localizer(
                                    tokens[8] + " " + tokens[9], // arpt ICAO + RWY
                                    tokens[7], // ident
                                    info_type,
                                    Float.parseFloat(tokens[1]), // lat
                                    Float.parseFloat(tokens[2]), // lon
                                    Integer.parseInt(tokens[3]), // elev MSL
                                    Float.parseFloat(tokens[4]), // freq
                                    Integer.parseInt(tokens[5]), // range
                                    Float.parseFloat(tokens[6]), // bearing, true degrees
                                    tokens[8], // ICAO
                                    tokens[9], // RWY,
                                    tokens[10],
                                    has_a_twin,
                                    twin_ilt
                                );
                            nor.add_nav_object(new_loc);
                            // add this localizer to the runway
                            Runway rwy = nor.get_runway(tokens[8], tokens[9], Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), true);
                            if ( rwy != null ) {
                                rwy.localizers.add(new_loc);
//                                if ( rwy.rwy_num1.equals(tokens[9]) ) {
////if ( rwy.loc1 != null ) logger.warning(new_loc.ilt + " for " + rwy.name + "/" + rwy.rwy_num1 + " already defined");
//                                    rwy.loc1 = new_loc;
//                                } else if ( rwy.rwy_num2.equals(tokens[9]) ) {
////if ( rwy.loc2 != null ) logger.warning(new_loc.ilt + " for " + rwy.name + "/" + rwy.rwy_num2 + " already defined");
//                                    rwy.loc2 = new_loc;
//                                }
                            } else {
                                logger.warning("Error NAV.dat: no RWY found for " + tokens[8] + " " + tokens[9] + " " + tokens[7]);
                            }

                        } else if (info_type == 6) {

                            // update the ILS (or IGS) with this GS
                            // (we can do this in the same loop, since the file is sorted by info_type; the ILS will already be stored)
                            tokens = line.split("\\s+",11);
                            // tokens[] 0=type, 1=lat, 2=lon, 3=elev, 4=freq, 5=range, 6=glide_angle*100000+course, 7=ident, 8=arpt, 9=rwy, 10="GS"
                            coupled_rno = nor.find_tuned_nav_object(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[4])/100.0f, tokens[7]);
                            if ( (coupled_rno != null) && (coupled_rno instanceof Localizer) ) {
                                coupled_loc = (Localizer) coupled_rno;
                                coupled_loc.has_gs = true;
                                // when an ILS has a GS, we are more interested in the elev of the GS than the LOC
                                coupled_loc.elevation = Integer.parseInt(tokens[3]);
                            } else {
                                logger.warning("Error NAV.dat: no ILS for GS " + tokens[7] + " " + tokens[4]);
                            }
                            
                        } else if (info_type == 12) {
                            
                            // update the VOR, LOC, ILS or IGS with this DME
                            // (we can do this in the same loop, since the file is sorted by info_type)
                            tokens = line.split("\\s+",9);
                            // tokens[] 0=type, 1=lat, 2=lon, 3=elev, 4=freq, 5=range, 6=bias, 7=ident, 8=name
                            coupled_rno = nor.find_tuned_nav_object(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[4])/100.0f, tokens[7]);
                            if (coupled_rno != null) {
                                coupled_rno.has_dme = true;
                                coupled_rno.dme_lat = Float.parseFloat(tokens[1]);
                                coupled_rno.dme_lon = Float.parseFloat(tokens[2]);
                            } else {
                                logger.config("Error NAV.dat: no VOR or Loc for DME " + tokens[7] + " " + tokens[4]);
                            }
                        }
                        
                    } catch (Exception e) {
                        logger.warning("Parse error in " + file.getName() + ":" + line_number + " '" + line + "' (" + e + ")");
                    }
                    
                }

            } // line ! isEmpty

        } // while readLine

        if (reader != null) {
            reader.close();
        }

    }


    public void read_fix_table() throws Exception {

        File file;
        if ( new File( this.pathname_to_aptnav + this.FIX_xplane ).exists() ) {
            logger.fine("Reading FIX database ( " + this.pathname_to_aptnav + this.FIX_xplane + " )");
            file = new File( this.pathname_to_aptnav + this.FIX_xplane );
        } else {
            logger.fine("Reading FIX database ( " + this.pathname_to_aptnav + this.FIX_file + " )");
            file = new File( this.pathname_to_aptnav + this.FIX_file );
        }
        BufferedReader reader = new BufferedReader( new FileReader( file ));
        String line;
        String[] tokens;
        long line_number = 0;

        while ((line = reader.readLine()) != null) {

            if ( line.length() > 0 ) {

                line_number++;

                line = line.trim();
                if ( (line_number > 2) && ( ! line.equals("99") ) ) {
                    try {
                        tokens = line.split("\\s+",3);
                        nor.add_nav_object(new Fix(
                                tokens[2],
                                Float.parseFloat(tokens[0]),
                                Float.parseFloat(tokens[1]),
                                false));
                    } catch (Exception e) {
                        logger.warning("Parse error in " + file.getName() + ":" + line_number + " '" + line + "' (" + e + ")");
                    }
                }

            } // line !isEmpty

        } // while readLine

        if (reader != null) {
            reader.close();
        }

    }


    public void read_awy_table() throws Exception {

        File file;
        if ( new File( this.pathname_to_aptnav + this.AWY_xplane ).exists() ) {
            logger.fine("Reading AWY database ( " + this.pathname_to_aptnav + this.AWY_xplane + " )");
            file = new File( this.pathname_to_aptnav + this.AWY_xplane );
        } else {
            logger.fine("Reading AWY database ( " + this.pathname_to_aptnav + this.AWY_file + " )");
            file = new File( this.pathname_to_aptnav + this.AWY_file );
        }
        BufferedReader reader = new BufferedReader( new FileReader( file ));
        String line;
        String[] tokens;
        long line_number = 0;

        while ((line = reader.readLine()) != null) {

            if ( line.length() > 0 ) {

                line_number++;

                line = line.trim();
                if ((line_number > 2) && ( ! line.equals("99") ) ) {
                    try {
                        tokens = line.split("\\s+",10);
                        // tokens[] 0=WPT1, 1=lat1, 2=lon1, 3=WPT2, 4=lat2, 5=lon2, 6=low(1)/high(2), 7=bottom, 8=top, 9=ID(s)
                        fix = nor.get_fix( tokens[0], Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]) );
                        if ( fix != null ) fix.on_awy = true;
                    } catch (Exception e) {
                        logger.warning("Parse error in " + file.getName() + ":" + line_number + " '" + line + "' (" + e + ")");
                    }
                }

            } // line !isEmpty

        } // while readLine

        if (reader != null) {
            reader.close();
        }

    }


    public void preference_changed(String key) {

        logger.finest("Preference changed");
        if (key.equals(XHSIPreferences.PREF_APTNAV_DIR)) {
            // reload navigation databases
            this.pathname_to_aptnav = XHSIPreferences.get_instance().get_preference(XHSIPreferences.PREF_APTNAV_DIR);
            if (XHSIStatus.nav_db_status.equals(XHSIStatus.STATUS_NAV_DB_NOT_FOUND) == false) {
                try {
                    logger.fine("Reload navigation tables");
                    read_all_tables();
                } catch (Exception e) {
                    logger.warning("Could not read navigation tables! (" + e.toString() + ")");
                }
            } else {
                logger.warning("Could not find AptNav Resources! (Status:" + XHSIStatus.nav_db_status + ")");
            }
        }

    }


}
