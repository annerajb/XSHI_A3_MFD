/**
* XPlaneAircraftEnvironment.java
* 
* The X-Plane specific implementation of AircraftEnvironment.
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
package net.sourceforge.xhsi.model.xplane;

import net.sourceforge.xhsi.model.AircraftEnvironment;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.SimDataRepository;


public class XPlaneAircraftEnvironment implements AircraftEnvironment {
	
	private SimDataRepository sim_data;
	
	public XPlaneAircraftEnvironment(ModelFactory sim_model) {
		sim_data = sim_model.get_repository_instance();
	}
		
	public float wind_speed() { return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_WEATHER_WIND_SPEED_KT) * 1.94385f; } // m/s to kts

        public float wind_direction() { return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_WEATHER_WIND_DIRECTION_DEGT); }

}

