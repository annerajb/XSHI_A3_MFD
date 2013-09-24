/**
* Runway.java
* 
* Model class for a runway.
* 
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



public class Runway extends NavigationObject {

    public static final int RWY_ASPHALT = 1;
    public static final int RWY_CONCRETE = 2;
    public static final int RWY_GRASS = 3;
    public static final int RWY_DIRT = 4;
    public static final int RWY_GRAVEL = 5;
    public static final int RWY_DRY_LAKEBED = 12;
    public static final int RWY_WATER = 13;
    public static final int RWY_SNOW = 14;
    public static final int RWY_TRNSPARENT = 15;

    public float length;
    public float width;
    public int surface;
    public String rwy_num1;
    public float lat1;
    public float lon1;
//    public Localizer loc1;
    public String rwy_num2;
    public float lat2;
    public float lon2;
//    public Localizer loc2;
    public ArrayList<Localizer> localizers;


    public Runway(String name, float length, float width, int surface, String rwy_num1, float lat1, float lon1, String rwy_num2, float lat2, float lon2) {
        super(name, ( lat1 + lat2 ) / 2, ( lon1 + lon2 ) / 2);
        this.length = length;
        this.width = width;
        this.surface = surface;
        this.rwy_num1 = rwy_num1;
        this.lat1 = lat1;
        this.lon1 = lon1;
//        this.loc1 = null;
        this.rwy_num2 = rwy_num2;
        this.lat2 = lat2;
        this.lon2 = lon2;
//        this.loc2 = null;
        this.localizers = new ArrayList<Localizer>();
    }


    public String toString() {
        return "RWY " + this.name + " " + this.rwy_num1 + "/" + this.rwy_num2 + "' @ (" + this.lat + "," + this.lon + ")";
    }
    
}