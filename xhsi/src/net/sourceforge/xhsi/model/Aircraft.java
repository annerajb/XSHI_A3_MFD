/**
* Aircraft.java
* 
* Model class for an aircraft.
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

public interface Aircraft {

    /**
     * @return boolean - battery power is on
     */
    public boolean battery();

    /**
     * @return boolean - cockpit_lights are on
     */
    public boolean cockpit_lights();

    /**
     * @return float - latitude in degrees
     */
    public float lat();

    /**
     * @return float - longitude in degrees
     */
    public float lon();

    /**
     * @return float - MSL in meters
     */
    public float msl_m();

    /**
     * @return float - AGL in meters
     */
    public float agl_m();

    /**
     * @return float - turn speed in degrees per second
     */
    public float turn_speed();

    /**
     * @return float - ground speed in knots
     */
    public float ground_speed();

    /**
     * @return float - true air speed in knots
     */
    public float true_air_speed();

    /**
     * @return float - magnetic heading of the aircraft in degrees
     */
    public float heading();

    /**
     * @return float - hpath in degrees
     */
    public float hpath();

//    /**
//     * @return float - altitude
//     */
//    public float indicated_altitude();

//    /**
//     * @return float - vertical velocity
//     */
//    public float indicated_vv();

    /**
     * @return float - roll angle of the aircraft in degrees
     */
    public float pitch();

    /**
     * @return float - roll angle of the aircraft in degrees
     */
    public float bank();

    /**
     * Returns the magnetic track of the aircraft in degrees. If ground_speed
     * is lower than 5 knots, returns heading of aircraft.
     *
     * @return float - magnetic track of the aircraft in degrees
     */
    public float track();

    /**
     * Returns the difference between the track and the heading
     * of the aircraft.
     *
     * @return float - difference of horizontal path and heading in degrees
     */
    public float drift();

    /**
     * @return float - sideslip in degrees
     */
    public float sideslip();

    /**
     * @return float - angle of attack
     */
    public float aoa();

    /**
     * @return float - IAS in kts
     */
    public float airspeed_ind();

    /**
     * @return float - altitude in ft
     */
    public float altitude_ind();

    /**
     * @return float - VVI in fpm
     */
    public float vvi();

    /**
     * @return int - radar altimeter bug for pilot or copilot
     */
    public int ra_bug();

    /**
     * @return int - decision altitude bug for pilot or copilot
     */
    public int da_bug();

    /**
     * @return boolean - minimums reference is decision altitude, not decision height
     */
    public boolean mins_is_baro();

    /**
     * @return int - qnh setting for pilot or copilot
     */
    public int qnh();

    /**
     * @return int - qnh setting for pilot or copilot
     */
    public float altimeter_in_hg();

    /**
     * @return float - ASI trend kts/s
     */
    public float airspeed_acceleration();

     /**
     * @return float - magnetic variation at current position
     */
    public float magnetic_variation();

    /**
     * @return float - outside air temperature
     */
    public float oat();

    /**
     * @return float - total air temperature
     */
    public float tat();

    /**
     * @return float - mach number
     */
    public float mach();

    /**
     * @return float - This is the speed of sound in Kts at the plane's location.
     */
    public float sound_speed();

    /**
     * Returns the distance between the aircraft and the given navigation
     * object in nautical miles.
     *
     * @param nav_object - the navigation object to which the distance should be computed
     * @return float - distance to nav_object in nautical miles
     */
    public float distance_to(NavigationObject nav_object);

    /**
     * Returns a rough distance between the aircraft and the given navigation
     * object in nautical miles. This calculation is less accurate but much
     * faster than <code>distance_to</code>.
     *
     * @param nav_object - the navigation object to which the distance should be computed
     * @return float - distance to nav_object in nautical miles
     */
    public float rough_distance_to(NavigationObject nav_object);

    /**
     * Returns the EET to arive at the given
     * navigation object based on the distance to the aircraft and its
     * current ground speed. Note: we assume the aircraft is on a direct
     * course to nav_object. I do not consider the actual closing speed.
     *
     * @param nav_object - the navigation object
     * @return long - the EET to the nav_object
     */
    public long ete_to(NavigationObject nav_object);

    /**
     * Returns the zulu ETA time when the aircraft will arive at the given
     * navigation object based on the distance to the aircraft and its
     * current ground speed. Note: we assume the aircraft is on a direct
     * course to nav_object. I do not consider the actual closing speed.
     *
     * @param nav_object - the navigation object
     * @return long - the arrival time at nav_object in zulu time
     */
    public long time_when_arriving_at(NavigationObject nav_object);

    /**
     * Returns the EET to fly the given distance at the current
     * ground speed.
     *
     * @param distance - the distance to cover
     * @return long - the time to fly the distance
     */
    public long ete_for_distance(float distance);

    /**
     * Returns the time in hours to fly the given distance at the current
     * ground speed.
     *
     * @param distance - the distance to cover
     * @return long - the time to fly the distance
     */
    public long time_after_distance(float distance);

    /**
     * Returns the current time + EET
     *
     * @param eet - the EET in minutes
     * @return long - current time + EET
     */
    public long time_after_ete(float eet);

    /**
     * @return float - simulator zulu time in seconds
     */
    public float sim_time_zulu();

    /**
     * @return boolean - timer is running (used for the CHR)
     */
    public boolean timer_is_running();
    
    /**
     * @return float - elapsed time (used for the CHR)
     */
    public float timer_elapsed_time();

    /**
     * @return float - total flight time (used for the ET)
     */
    public float total_flight_time();

    /**
     * @return Avionics - reference to avionics model of this aircraft
     */
    public Avionics get_avionics();

    /**
     * @return AircraftEnvironment - reference to environment model of this aircraft
     */
    public AircraftEnvironment get_environment();


    /**
     * @return int - number of gears
     */
    public int num_gears();


    /**
     * @return float - position of a gear
     */
    public float get_gear(int gear);


    /**
     * @return boolean - all gears down and locked?
     */
    public boolean gear_is_down();


    /**
     * @return boolean - all gears up?
     */
    public boolean gear_is_up();


    /**
     * @return boolean - are we on the ground?
     */
    public boolean on_ground();


    /**
     * @return boolean - master warning lit?
     */
    public boolean master_warning();


    /**
     * @return boolean - master warning lit?
     */
    public boolean master_caution();


    /**
     * @return float - flaps position
     */
    public float get_flap_position();


    /**
     * @return float - flaps handle position
     */
    public float get_flap_handle();


    /**
     * @return int - number of flap detents
     */
    public int get_flap_detents();


    /**
     * @return float - Actual Speed Brake position
     */
    public float get_speed_brake();


    /**
     * @return boolean - Speed Brake armed
     */
    public boolean speed_brake_armed();


    /**
     * @return float - Parking Brake
     */
    public float get_parking_brake();


    /**
     * @return boolean - Stall warning
     */
    public boolean stall_warning();


    /**
     * @return boolean - GPWS
     */
    public boolean terrain_warning();


    /**
     * @return boolean - low fuel
     */
    public boolean low_fuel();


    /**
     * @return boolean - AP disconnect
     */
    public boolean ap_disconnect();


    /**
     * @return boolean - icing detected
     */
    public boolean icing();


    /**
     * @return boolean - pitot heat is off
     */
    public boolean pitot_heat();


    /**
     * @return boolean - gear warning
     */
    public boolean gear_warning();


    /**
     * @return boolean - gear unsafe
     */
    public boolean gear_unsafe();


    /**
     * @return int - Autobrake level -1=RTO / 0=Off / 1..4=On
     */
    public int auto_brake();


    /**
     * @return float - Vso: stall landing configuration
     */
    public float get_Vso();


    /**
     * @return float - Vs: stall clean
     */
    public float get_Vs();


    /**
     * @return float - Vfe: max flaps extended
     */
    public float get_Vfe();


    /**
     * @return float - Vno: max normal
     */
    public float get_Vno();


    /**
     * @return float - Vne: never exceed
     */
    public float get_Vne();


    /**
     * @return float - Mmo: max mach
     */
    public float get_Mmo();


    /**
     * @return float - Vle: max landing gear extended
     */
    public float get_Vle();


    /**
     * @return int - number of engines
     */
    public int num_engines();


    /**
     * @return int - number of fuel tanks
     */
    public int num_tanks();

    /**
     * @return float - relative size of a fuel tank (total = 1.0f)
     */
    public float tank_ratio(int tank);


    /**
     * @return boolean - low (or high) oil pressure
     */
    public boolean oil_press_alert(int eng);


    /**
     * @return boolean - high oil temperature
     */
    public boolean oil_temp_alert(int eng);


    /**
     * @return boolean - low fuel pressure
     */
    public boolean fuel_press_alert(int eng);


    /**
     * @return float - Fuel quantity (kg) per tank
     */
    public float get_fuel(int tank);


    /**
     * @return float - Total Fuel quantity (kg)
     */
    public float get_total_fuel();

    public void estimate_fuel_capacity();

    public float get_fuel_capacity();

    public float get_tank_capacity(int tank);

    public void set_fuel_capacity(float capacity);


    /**
     * @return float - Engine N1 %
     */
    public float get_N1(int engine);


    /**
     * @return float - UFMC Reference N1 %
     */
    public float get_ref_N1(int engine);
    
    
    /**
     * @return String Thrust Mode : 0: ---, 1: TO, 2: R-TO, 3: R- CLB, 4: CLB, 4: CRZ, 6: GA, 7:CON, 8: MAX.
     */
    public String get_thrust_mode();


    /**
     * @return float - Engine EGT %
     */
    public float get_EGT_percent(int engine);


    /**
     * @return float - Engine EGT value
     */
    public float get_EGT_value(int engine);


    /**
     * @return float - Engine N2 %
     */
    public float get_N2(int engine);


    /**
     * @return float - Engine FF
     */
    public float get_FF(int engine);


    /**
     * @return float - Maximum recorded FF
     */
    public float get_max_FF();


    /**
     * @void - Rest maximum recorded FF
     */
    public void reset_max_FF();


    /**
     * @return float - Oil P ratio
     */
    public float get_oil_press_ratio(int engine);

    /**
     * @return float - Oil T ratio
     */
    public float get_oil_temp_ratio(int engine);

    /**
     * @return float - Oil Q ratio
     */
    public float get_oil_quant_ratio(int engine);

    /**
     * @return float - Engine vibration %
     */
    public float get_vib(int engine);

    /**
     * @return float - Hydraulics P ratio
     */
    public float get_hyd_press(int circuit);

    /**
     * @return float - Hydraulics Q ratios
     */
    public float get_hyd_quant(int circuit);


    /**
     * @return float - Maximum _available_ engine TRQ
     */
    public float get_max_TRQ();
    
    /**
     * @return float - Engine TRQ
     */
    public float get_TRQ(int engine);

    /**
     * @return float - ITT %
     */
    public float get_ITT_percent(int engine);

    /**
     * @return float - Engine ITT deg C
     */
    public float get_ITT_value(int engine);

    /**
     * @return float - prop RPM redline
     */
    public float get_max_prop_RPM();
    
    /**
     * @return float - prop RPM
     */
    public float get_prop_RPM(int engine);

    /**
     * @return int - prop mode (0=feather, 1=normal, 2=beta, 3=reverse)
     */
    public int get_prop_mode(int engine);
    
    /**
     * @return float - NG %
     */
    public float get_NG(int engine);

    
    /**
     * @return boolean - Is thrust reverser deployed
     */
    public boolean reverser(int engine);


    /**
     * @return float - Thrust reverser deploy ratio
     */
    public float reverser_position(int engine);


    /**
     * @return float - Engine MPR
     */
    public float get_MPR(int engine);


    /**
     * @return String - Nearest airport
     */
    public String get_nearest_arpt();


}
