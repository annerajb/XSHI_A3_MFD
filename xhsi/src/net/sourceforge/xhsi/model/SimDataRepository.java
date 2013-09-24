/**
* XPlaneSimDataRepository.java
* 
* Stores and provides access to all simulation data variables. This repository
* is updated by XPlaneDataPacketDecoder. Observers can subscribe to changes
* in this repository. All observers are updated by calling the tick_updates
* method of this repository.
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


public interface SimDataRepository {

    public boolean is_replaying();

    public void add_observer(Observer observer);

    public void store_sim_float(int id, float value);

//    public void store_sim_int(int id, int value);

    public void store_sim_string(int id, String value);

    public float get_sim_float(int id);

//    public int get_sim_int(int id);

    public String get_sim_string(int id);

    public void tick_updates();

    public long get_nb_of_updates();

}
