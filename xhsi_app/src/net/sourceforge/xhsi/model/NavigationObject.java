/**
* NavigationObject.java
* 
* Model class for a navigation object (VOR, NDB, FIX, AIRPORT, Localizer)
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

public class NavigationObject {

	public String name;
	public float lat;
	public float lon;
	
	public static int NO_TYPE_RUNWAY = 4;
	public static int NO_TYPE_AIRPORT = 3;
	public static int NO_TYPE_FIX = 2;
	public static int NO_TYPE_NDB = 1;
	public static int NO_TYPE_VOR = 0;
	
	public NavigationObject(String name, float lat, float lon) {
		this.name = name;
		this.lat = lat;
		this.lon = lon;
	}
	
	public String toString() {
		return "Navigation object '" + this.name + " @ (" + this.lat + "," + this.lon + ") ";
	}
	
}
