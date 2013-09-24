/**
* NavigationObjectRepository.java
* 
* Manages and provides access to navigation objects (VORs, NDBs, fixes,
* arpts via various accessors and search methods.
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
package net.sourceforge.xhsi.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;


public class NavigationObjectRepository {

    // find the RadioNavigationObject, even at <RANGE_MULTIPLIER> times the published range
    public static final float RANGE_MULTIPLIER = 1.25f;
    // let's hope that no other RadioNavigationObject with the same frequency is in that multiplied range

    private ArrayList vors[][];
    private ArrayList ndbs[][];
    private ArrayList fixes[][];
    private ArrayList arpts[][];
    private ArrayList rwys[][];
    private HashMap frequencies;
    private HashMap airports;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    private static NavigationObjectRepository single_instance;


    public static NavigationObjectRepository get_instance() {
        if (NavigationObjectRepository.single_instance == null) {
            NavigationObjectRepository.single_instance = new NavigationObjectRepository();
        }
        return NavigationObjectRepository.single_instance;
    }


    private NavigationObjectRepository() {
        init();
    }


    public void init() {

        vors = new ArrayList[181][361];
        ndbs = new ArrayList[181][361];
        fixes = new ArrayList[181][361];
        arpts = new ArrayList[181][361];
        rwys = new ArrayList[181][361];
        frequencies = new HashMap();
        airports = new HashMap();

        for (int lat=0;lat<181;lat++) {
            for (int lon=0;lon<361;lon++) {
                //dmes[lat][lon] = new ArrayList();
                vors[lat][lon] = new ArrayList();
                ndbs[lat][lon] = new ArrayList();
                fixes[lat][lon] = new ArrayList();
                arpts[lat][lon] = new ArrayList();
                rwys[lat][lon] = new ArrayList();
            }
        }

    }


    public ArrayList get_nav_objects(int type, float lat, float lon) {
        if (type == NavigationObject.NO_TYPE_VOR) {
            return this.vors[get_lat_index(lat)][get_lon_index(lon)];
        } else if (type == NavigationObject.NO_TYPE_NDB) {
            return this.ndbs[get_lat_index(lat)][get_lon_index(lon)];
        } else if (type == NavigationObject.NO_TYPE_FIX) {
            return this.fixes[get_lat_index(lat)][get_lon_index(lon)];
        } else if (type == NavigationObject.NO_TYPE_AIRPORT) {
            return this.arpts[get_lat_index(lat)][get_lon_index(lon)];
        } else if (type == NavigationObject.NO_TYPE_RUNWAY) {
            return this.rwys[get_lat_index(lat)][get_lon_index(lon)];
        } else {
            return new ArrayList();
        }
    }


    public ArrayList get_nav_objects(int type, NavigationObject nav_object) {
        return get_nav_objects(type, nav_object.lat, nav_object.lon);
    }


    public void add_nav_object(NavigationObject nav_object) {

        if (nav_object instanceof RadioNavBeacon) {
            RadioNavBeacon vor = (RadioNavBeacon) nav_object;
            if (vor.type == RadioNavBeacon.TYPE_NDB) {
                get_nav_objects(NavigationObject.NO_TYPE_NDB, nav_object).add(nav_object);
            } else if (vor.type == RadioNavBeacon.TYPE_VOR) {
                get_nav_objects(NavigationObject.NO_TYPE_VOR, nav_object).add(nav_object);
            } else if (vor.type == RadioNavBeacon.TYPE_STANDALONE_DME) {
                get_nav_objects(NavigationObject.NO_TYPE_VOR, nav_object).add(nav_object);
            }
        } else if (nav_object instanceof Fix) {
            get_nav_objects(NavigationObject.NO_TYPE_FIX, nav_object).add(nav_object);
        } else if (nav_object instanceof Airport) {
            get_nav_objects(NavigationObject.NO_TYPE_AIRPORT, nav_object).add(nav_object);
            add_arpt(nav_object);
        } else if (nav_object instanceof Runway) {
            get_nav_objects(NavigationObject.NO_TYPE_RUNWAY, nav_object).add(nav_object);
        }

        if (nav_object instanceof RadioNavigationObject) {
            RadioNavigationObject rno = (RadioNavigationObject) nav_object;
            if (rno.frequency != 0.0) {
                add_freq(rno.frequency, rno);
            }
        }

    }

    private void add_freq(float freq, NavigationObject nav_object) {

        //Float freq_key = new Float(freq);
        ArrayList nos;
        if (this.frequencies.containsKey(freq)) {
            nos = (ArrayList) this.frequencies.get(freq);
        } else {
            nos = new ArrayList();
        }
        nos.add(nav_object);
        this.frequencies.put(freq, nos);

    }


    private void add_arpt(NavigationObject arpt_object) {

        String arpt_str = ((Airport)arpt_object).icao_code;
        this.airports.put(arpt_str, arpt_object);

    }


    private ArrayList get_nav_objects_by_freq(float freq) {

        Float freq_key = new Float(freq);
        if (this.frequencies.containsKey(freq_key)) {
            return (ArrayList) this.frequencies.get(freq_key);
        } else {
            return new ArrayList();
        }

    }


    public Airport get_airport(String arpt_str) {

        if (this.airports.containsKey(arpt_str)) {
            return (Airport)this.airports.get(arpt_str);
        } else {
            return null;
        }

    }


    public RadioNavigationObject find_tuned_nav_object(float aircraft_lat, float aircraft_lon, float freq, String nav_id) {

        ArrayList nos = get_nav_objects_by_freq(freq);
        RadioNavigationObject rno = null;
        boolean found_rno = false;
        int index = 0;
        int distance;

        if ( nav_id.equals("") ) {
            // no NAV_ID received, what can we find that is within range?
            while ((found_rno == false) && (index<nos.size())) {
                rno = (RadioNavigationObject) nos.get(index);
                distance = (int)CoordinateSystem.rough_distance(aircraft_lat, aircraft_lon, rno.lat, rno.lon);
                if ( distance < (rno.range * NavigationObjectRepository.RANGE_MULTIPLIER) ) {
                    found_rno = true;
                } else {
                    index += 1;
                }
            }
        } else {
            while ((found_rno == false) && (index<nos.size())) {
                rno = (RadioNavigationObject) nos.get(index);
                if ( rno.ilt.equals(nav_id)) {
                    // freq and id match, do we really have to check if we are within range?
                    distance = (int)CoordinateSystem.rough_distance(aircraft_lat, aircraft_lon, rno.lat, rno.lon);
                    if ( distance < (rno.range * NavigationObjectRepository.RANGE_MULTIPLIER) ) {
                        found_rno = true;
                    } else {
                        index += 1;
                    }
                } else {
                    index += 1;
                }
            }
        }

        if (found_rno)
            return rno;
        else
            return null;

    }


    public Fix get_fix(String ilt, float lat, float lon) {

        // find the fix with the supplied name that is at lat/lon
        // TODO : a hashmap might be more efficient
        ArrayList fix_list = get_nav_objects(NavigationObject.NO_TYPE_FIX, lat, lon);
        Fix fix = null;
        boolean found_fix = false;
        int index = 0;

        while ( (found_fix == false) && (index < fix_list.size()) ) {
            fix = (Fix) fix_list.get(index);
            if ( ilt.equalsIgnoreCase(fix.name) ) {
                found_fix = true;
            } else {
                index += 1;
            }
        }

        if (found_fix)
            return fix;
        else
            return null;

    }


//    public Airport get_airport(String ilt, float lat, float lon) {
//
//        // find the airport with the supplied name that is at lat/lon
//        // TODO : a hashmap might be more efficient
//        ArrayList airport_list = get_nav_objects(NavigationObject.NO_TYPE_AIRPORT, lat, lon);
//        Airport airport = null;
//        boolean found_airport = false;
//        int index = 0;
//
//        while ( (found_airport == false) && (index < airport_list.size()) ) {
//            airport = (Airport) airport_list.get(index);
//            if ( ilt.equalsIgnoreCase(airport.icao_code) ) {
//                found_airport = true;
//            } else {
//                index += 1;
//            }
//        }
//
//        if (found_airport)
//            return airport;
//        else
//            return null;
//
//    }


    public NavigationObject get_navobj(String ilt, float lat, float lon) {

        // find the VOR, NDB, Waypoint or Airport with the supplied name that is close to lat/lon
        // TODO : a hashmap might be more efficient, and avoid code duplication

        ArrayList<NavigationObject> navobj_list;
        int index;

        boolean found_beacon = false;
        RadioNavBeacon beacon = null;

        boolean found_fix = false;
        Fix waypoint = null;

        boolean found_arpt = false;
        Airport arpt = null;

        if ( ilt.length() == 4 ) {
            // the name has 4 characters, search for an ARPT
            navobj_list = get_nav_objects(NavigationObject.NO_TYPE_AIRPORT, lat, lon);
            index = 0;

            while ( (found_arpt == false) && (index < navobj_list.size()) ) {
                arpt = (Airport) navobj_list.get(index);
                if ( ilt.equalsIgnoreCase(arpt.icao_code) ) {
                    found_arpt = true;
                } else {
                    index += 1;
                }
            }
        } else if ( ilt.length() < 4 ) {
            // the name has less than 4 characters, search for a VOR or NDB
            navobj_list = get_nav_objects(NavigationObject.NO_TYPE_VOR, lat, lon);
            index = 0;

            while ( (found_beacon == false) && (index < navobj_list.size()) ) {
                beacon = (RadioNavBeacon) navobj_list.get(index);
                if ( ilt.equalsIgnoreCase(beacon.ilt) ) {
                    found_beacon = true;
                } else {
                    index += 1;
                }
            }
            if ( ! found_beacon ) {
                navobj_list = get_nav_objects(NavigationObject.NO_TYPE_NDB, lat, lon);
                index = 0;

                while ( (found_beacon == false) && (index < navobj_list.size()) ) {
                    beacon = (RadioNavBeacon) navobj_list.get(index);
                    if ( ilt.equalsIgnoreCase(beacon.ilt) ) {
                        found_beacon = true;
                    } else {
                        index += 1;
                    }
                }
            }
        } else if ( ilt.length() == 5 ) {
            // the name has 5 characters, search for a Waypoint
            navobj_list = get_nav_objects(NavigationObject.NO_TYPE_FIX, lat, lon);
            index = 0;

            while ( (found_fix == false) && (index < navobj_list.size()) ) {
                waypoint = (Fix) navobj_list.get(index);
                if ( ilt.equalsIgnoreCase(waypoint.name) ) {
                    found_fix = true;
                } else {
                    index += 1;
                }
            }
        }

        if (found_arpt)
            return arpt;
        else if (found_beacon)
            return beacon;
        else if (found_fix)
            return waypoint;
        else
            return null;

    }


    public Runway get_runway(String arpt, String rwy_id, float lat, float lon, boolean wide_search) {

        // find the runway with the supplied name that is at (or close to) lat/lon
        // TODO : a hashmap might be more efficient
        ArrayList rwy_list = get_nav_objects(NavigationObject.NO_TYPE_RUNWAY, lat, lon);
        Runway rwy = null;
        boolean found_rwy = false;
        int index = 0;

        while ( ( ! found_rwy ) && ( index < rwy_list.size() ) ) {
            rwy = (Runway) rwy_list.get(index);
            if ( rwy.name.equalsIgnoreCase(arpt) && ( rwy.rwy_num1.equalsIgnoreCase(rwy_id) || rwy.rwy_num2.equalsIgnoreCase(rwy_id) ) ) {
                found_rwy = true;
            } else {
                index += 1;
            }
        }

        if ( found_rwy )
            return rwy;
        else {
            if ( wide_search ) {
                int lat_index = get_lat_index(lat);
                int lon_index = get_lon_index(lon);
                boolean north = lat_index==0 ? false : true;
                boolean west = lon_index==0 ? false : true;
                boolean south = lat_index==180 ? false : true;
                boolean east = lat_index==360 ? false : true;
                if ( north ) {
                    rwy = get_runway(arpt, rwy_id, lat - 1.0f, lon, false);
                }
                if ( ( rwy == null ) && west ) {
                    rwy = get_runway(arpt, rwy_id, lat, lon - 1.0f, false);
                }
                if ( ( rwy == null ) && south ) {
                    rwy = get_runway(arpt, rwy_id, lat + 1.0f, lon, false);
                }
                if ( ( rwy == null ) && east ) {
                    rwy = get_runway(arpt, rwy_id, lat, lon + 1.0f, false);
                }
                if ( ( rwy == null ) && north && west ) {
                    rwy = get_runway(arpt, rwy_id, lat - 1.0f, lon - 1.0f, false);
                }
                if ( ( rwy == null ) && south && west ) {
                    rwy = get_runway(arpt, rwy_id, lat + 1.0f, lon - 1.0f, false);
                }
                if ( ( rwy == null ) && south && east ) {
                    rwy = get_runway(arpt, rwy_id, lat + 1.0f, lon + 1.0f, false);
                }
                if ( ( rwy == null ) && north && east ) {
                    rwy = get_runway(arpt, rwy_id, lat - 1.0f, lon + 1.0f, false);
                }
                return rwy;
            } else {
                return null;
            }
        }

    }


    private int get_lat_index(float lat) {
        int lat_index = (int)lat + 90;
        if ( lat_index < 1 ) lat_index += 179;
        if ( lat_index > 179 ) lat_index -= 179;
        return lat_index;
    }


    private int get_lon_index(float lon) {
        int lon_index = (int)lon + 180;
        if ( lon_index < 1 ) lon_index += 359;
        if ( lon_index > 359 ) lon_index -= 359;
        return lon_index;
    }


}
