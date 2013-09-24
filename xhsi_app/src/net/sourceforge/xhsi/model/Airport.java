/**
* Airport.java
* 
* Model class for an airport
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


public class Airport extends NavigationObject {

	public String icao_code;
        public ArrayList<Runway> runways;
        public float longest;
        public int elev;
        public ArrayList<ComRadio> com_radios;
	
	public Airport(String name, String icao_code, float lat, float lon, ArrayList runways, float longest, int elev, ArrayList<ComRadio> com_radios) {
		super(name, lat, lon);
		this.icao_code = icao_code.trim();
                this.runways = runways;
                this.longest = longest;
                this.elev = elev;
                this.com_radios = com_radios;
	}
	
	public String toString() {
		return "Airport '" + this.name + "' @ (" + this.lat + "," + this.lon + ")";
	}	
}
