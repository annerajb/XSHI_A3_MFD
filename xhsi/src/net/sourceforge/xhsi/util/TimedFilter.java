/**
* TimedFilter.java
* 
* Used to filter out computations that need not to be performed for every
* displayed frame, such as distance calculations or checks for radio frequency
* changes.
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

public class TimedFilter {

    /**
     * The time in ms after which the filter times out
     */
    long filter_timeout;

    /**
     * The last time the filter was activated
     */
    long time_of_last_activation;

    public TimedFilter(long filter_timeout) {
        this.filter_timeout = filter_timeout;
        this.time_of_last_activation = 0;
    }

    public boolean time_to_perform() {
        long now = System.currentTimeMillis();
        if (((now - time_of_last_activation) > filter_timeout) || (time_of_last_activation == 0)) {
            time_of_last_activation = now;
            return true;
        } else
            return false;
    }
}
