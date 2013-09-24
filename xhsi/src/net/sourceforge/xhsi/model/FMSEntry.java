/**
* FMSEntry.java
* 
* Model class for an entry in the flight management system (FMS)
* 
* Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
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

public class FMSEntry extends NavigationObject {

    public static final int ARPT = 1;
    public static final int NDB = 2;
    public static final int VOR = 4;
    public static final int FIX = 512;
    public static final int LATLON = 2048;


    public int index;
    public int type;
    public int altitude;
    public float leg_dist;
    public float total_ete;
    public boolean active;
    public boolean displayed;


    public FMSEntry() {
        super("", 0.0f, 0.0f);
        this.index = 0;
        this.type = 0;
        this.altitude = 0;
        this.leg_dist = 0.0f;
        this.total_ete = 0.0f;
        this.active = false;
        this.displayed = false;
    }


    public FMSEntry(int index, String name, int type, float lat, float lon, int altitude, float leg_dist, float total_ete, boolean active, boolean displayed) {
        super(name, lat, lon);
        this.index = index;
        this.type = type;
        this.altitude = altitude;
        this.leg_dist = leg_dist;
        this.total_ete = total_ete;
        this.active = active;
        this.displayed = displayed;
// "L/L" is already set by the plugin
//        if (type == 2048) {
//            this.name = "L/L";
//        }
    }

    
    public String toString() {
        return this.name + " @ (" + this.lat + "\u00B0, " + this.lon + "\u00B0)";
    }

}
