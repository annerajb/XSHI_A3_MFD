/**
* ComRadio.java
* 
* Model class for a COM frequency.
* 
* Copyright (C) 2011  Marc Rogiers (marrog.123@gmail.com)
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


public class ComRadio {

    public String arpt;
    public String callsign;
    public float frequency;


    public ComRadio(String arpt, String callsign, float frequency) {
        this.arpt = arpt;
        this.callsign = callsign;
        this.frequency = frequency;
    }


    public String toString() {
        return "COM " + this.arpt + " " + this.callsign + " " + this.frequency;
    }
    
}