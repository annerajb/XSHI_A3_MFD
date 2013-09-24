/**
* CoordinateSystem.java
* 
* Provides various computation methods for calculating distances.
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

import java.util.logging.Logger;

public class CoordinateSystem {

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");


    public static float nm_per_deg_lat() {
        return 60.0f;
    }


    public static float nm_per_deg_lon(float lat) {
        return (float) (Math.cos(Math.toRadians(lat)) * 60.0f);
    }


    public static float deg_lon_per_nm(float lat) {
        return (float) (1.0f / nm_per_deg_lon(lat));
    }


    public static float deg_lat_per_nm() {
        return 1.0f / 60.0f;
    }


//    public static float km_per_deg_lon(float lat) {
//        //return (float) (Math.cos(Math.toRadians(lat)) * 2.0f * Math.PI * (6370.0f/360.0f));
//        return (float) (Math.cos(Math.toRadians(lat)) * 40000.0f / 360.0f);
//    }


//    public static float deg_lon_per_km(float lat) {
//        return (float) (1.0f / km_per_deg_lon(lat));
//    }


//    public static float deg_lat_per_km() {
//        return 1.0f / (60.0f * 1.852f);
//    }


//    public static float km_per_deg_lat() {
//        return 1.0f / deg_lat_per_km();
//    }


    public static float distance(float lat1, float lon1, float lat2, float lon2) {

        double a1 = Math.toRadians(lat1);
        double b1 = Math.toRadians(lon1);
        double a2 = Math.toRadians(lat2);
        double b2 = Math.toRadians(lon2);

        // why 3443.9f? Wikipedia says that the average radius of the earth is 3440.07NM

        return (float) Math.acos(
                    (Math.cos(a1) * Math.cos(b1) * Math.cos(a2) * Math.cos(b2)) +
                    (Math.cos(a1) * Math.sin(b1) * Math.cos(a2) * Math.sin(b2)) +
                    (Math.sin(a1) * Math.sin(a2))
                ) * 3443.9f;
    }


    public static float rough_distance(float lat1, float lon1, float lat2, float lon2) {

        return (float) Math.sqrt(
                Math.pow( nm_per_deg_lon( (lat1+lat2)/2) * (lon1-lon2), 2 ) +
                Math.pow( nm_per_deg_lat() * (lat1-lat2), 2 ) );

    }


}
