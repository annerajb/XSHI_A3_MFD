/**
* RadioNavigationObject.java
* 
* Model superclass for radio navigation objects (VORs, NDBs and Localizers).
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

public class RadioNavigationObject extends NavigationObject {

    public float frequency;
    public int range;
    public String ilt;
    public int elevation;
    public boolean has_dme;
    public float dme_lat;
    public float dme_lon;

    public RadioNavigationObject(String name, String ilt, float lat, float lon, int elevation, float frequency, int range) {
        super(name, lat, lon);
        if (frequency < 1000) {
            this.frequency = frequency;
        } else {
            this.frequency = frequency/100;
        }
        this.range = range;
        this.ilt = ilt;
        this.elevation = elevation;
        this.has_dme = false;
        this.dme_lat = lat;
        this.dme_lon = lon;
    }

    public String toString() {
        return "Radio Navigation object '" + this.name + " Freq=" + this.frequency + " Range=" + this.range + " @ (" + this.lat + "," + this.lon + ") ";
    }


}
