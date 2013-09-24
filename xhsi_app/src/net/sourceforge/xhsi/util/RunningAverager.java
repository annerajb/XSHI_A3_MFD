/**
* RunningAverager.java
* 
* Computes a running average of a sequence of float values. Used to smooth
* flightsim values with high frequency noise (e. g. Frame rate).
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
package net.sourceforge.xhsi.util;


public class RunningAverager {

    float[] data_points;
    int nb_of_data_points;
    int index; // the position in data_points, where the next value is written


    public RunningAverager(int nb_of_data_points) {
        this.nb_of_data_points = nb_of_data_points;
        this.index = 0;
        data_points = new float[this.nb_of_data_points];
    }


    public float running_average(float new_value) {

        float sum = 0;
        data_points[index] = new_value;
        for (int i=0;i<nb_of_data_points;i++) {
            sum += data_points[i];
        }
        this.index = (this.index+1) % nb_of_data_points;
        return (sum/nb_of_data_points);

    }


}
