/**
* XHSIStatus.java
* 
* Maintains the state of the HSI application.
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
package net.sourceforge.xhsi;

public class XHSIStatus {

    public static final String STATUS_STARTUP = "Startup";
    public static final String STATUS_PLAYING_RECORDING = "Playing recording";
    public static final String STATUS_NO_RECEPTION = "No reception";
    public static final String STATUS_RECEIVING = "Receiving";
    public static final String STATUS_SHUTDOWN = "Shutdown";

    public static final String STATUS_NAV_DB_LOADED = "NAV Databases loaded";
    public static final String STATUS_NAV_DB_NOT_LOADED = "NAV Databases not loaded";
    public static final String STATUS_NAV_DB_NOT_FOUND = "NAV Databases not found";

    public static String status = STATUS_STARTUP;
    public static String nav_db_status = STATUS_NAV_DB_LOADED;
    public static String nav_db_cycle = "";

}
