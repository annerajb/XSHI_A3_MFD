/**
* XPlaneAircraft.java
* 
* The X-Plane specific implementation of Aircraft.
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
package net.sourceforge.xhsi.model.xplane;

import net.sourceforge.xhsi.XHSIPreferences;
import net.sourceforge.xhsi.model.Aircraft;
import net.sourceforge.xhsi.model.AircraftEnvironment;
import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.CoordinateSystem;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.NavigationObject;
import net.sourceforge.xhsi.model.SimDataRepository;


public class XPlaneAircraft implements Aircraft {

    private SimDataRepository sim_data;
    private Avionics avionics;
    private AircraftEnvironment environment;
    private XHSIPreferences xhsi_preferences;

    private float fuel_capacity;
    private float max_fuel_flow;

    private static String x737_thrust_modes[] = { "---", "TO", "R-TO", "R-CLB", "CLB", "CRZ", "G/A", "CON", "MAX" };
    private static String cl30_thrust_modes[] = { "---", "CRZ", "CLB", "TO", "APR" };


    public XPlaneAircraft(ModelFactory sim_model) {
        this.sim_data = sim_model.get_repository_instance();
        this.environment = new XPlaneAircraftEnvironment(sim_model);
        this.avionics = new XPlaneAvionics(this, sim_model);
        this.xhsi_preferences = XHSIPreferences.get_instance();
    }

    public Avionics get_avionics() {
        return this.avionics;
    }

    public AircraftEnvironment get_environment() {
        return this.environment;
    }

    public boolean battery() {
        if ( XHSIPreferences.get_instance().get_instrument_operator().equals( XHSIPreferences.INSTRUCTOR ) ||
                ! XHSIPreferences.get_instance().get_use_power() ) {
            return true;
        } else {
            if ( this.avionics.is_x737() )
               return ( sim_data.get_sim_float(XPlaneSimDataRepository.X737_PFD_PWR) != 0.0f );
            else
               return ( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_ELECTRICAL_BATTERY_ON) != 0.0f );
        }
    }

    public boolean cockpit_lights() {
        return ( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_ELECTRICAL_COCKPIT_LIGHTS_ON) != 0.0f );
    }

    public float lat() {return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_LATITUDE); } // degrees
    public float lon() { return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_LONGITUDE); } // degrees
    public float msl_m() { return (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_ELEVATION)); } // meters
    public float agl_m() { return (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_Y_AGL)); } // meters
    public float ground_speed() { return (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_GROUNDSPEED) * 1.9438445f); } // m/s to knots
    public float true_air_speed() { return (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_TRUE_AIRSPEED) * 1.94385f); } // m/s to knots
    public float heading() { return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_MAGPSI); } // degrees magnetic
    public float hpath() { return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_HPATH); }
//    public float indicated_altitude() { return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_MISC_H_IND); }
//    public float indicated_vv() { return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_VH_IND_FPM); }
    public float pitch() { return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_THETA); }
    public float bank() { return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_PHI); }


    public float track() {
        // degrees magnetic
        if (ground_speed() < 5) {
            return heading();
        } else {
            float path = (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_HPATH) +
                           sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_MAGVAR));
            if (path < 0)
                path += 360;
            else if (path > 360)
                path -= 360;

            return path;
        }
    }

    public float drift() {
        return heading() - track();
    }

    public float sideslip() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_GAUGES_INDICATORS_SIDESLIP_DEGREES);
    }

    public float aoa() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_ALPHA);
    }

    public float airspeed_ind() {
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            // copilot
            return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_GAUGES_INDICATORS_AIRSPEED_KTS_COPILOT);
        } else {
            // pilot or instructor
            return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_GAUGES_INDICATORS_AIRSPEED_KTS_PILOT);
        }
    }

    public float altitude_ind() {
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            // copilot
            return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_GAUGES_INDICATORS_ALTITUDE_FT_COPILOT);
        } else {
            // pilot or instructor
            return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_GAUGES_INDICATORS_ALTITUDE_FT_PILOT);
        }
    }

    public float vvi() {
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            // copilot
            return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_GAUGES_INDICATORS_VVI_FPM_COPILOT);
        } else {
            // pilot or instructor
            return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_GAUGES_INDICATORS_VVI_FPM_PILOT);
        }
    }

    public int ra_bug() {
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            // copilot
            return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_GAUGES_ACTUATORS_RADIO_ALTIMETER_BUG_FT_COPILOT));
        } else {
            // pilot or instructor
            return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_GAUGES_ACTUATORS_RADIO_ALTIMETER_BUG_FT_PILOT));
        }
    }

    public int da_bug() {
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            // copilot
            return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_DA_BUG));
        } else {
            // pilot or instructor
            return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_PILOT_DA_BUG));
        }
    }

    public boolean mins_is_baro() {
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            // copilot
            return ( sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_MINS_MODE) == 1.0);
        } else {
            // pilot or instructor
            return ( sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_PILOT_MINS_MODE) == 1.0);
        }
    }

    public int qnh() {
        return Math.round( altimeter_in_hg() * 1013.0f / 29.92f );
    }

    public float altimeter_in_hg() {
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            // copilot
            return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_GAUGES_ACTUATORS_BAROMETER_SETTING_IN_HG_COPILOT);
        } else {
            // pilot or instructor
            return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_GAUGES_ACTUATORS_BAROMETER_SETTING_IN_HG_PILOT);
        }
    }

    public float airspeed_acceleration() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_GAUGES_INDICATORS_AIRSPEED_ACCELERATION);
    }

    public float turn_speed() { return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_R); } // degrees per second

    public float distance_to(NavigationObject nav_object) {
        return CoordinateSystem.distance(lat(), lon(), nav_object.lat, nav_object.lon);
    }

    public float rough_distance_to(NavigationObject nav_object) {
        return CoordinateSystem.rough_distance(lat(), lon(), nav_object.lat, nav_object.lon);
    }

    public long ete_to(NavigationObject nav_object) {
        return ete_for_distance(distance_to(nav_object));
    }

    public long time_when_arriving_at(NavigationObject nav_object) {
        return time_after_distance(distance_to(nav_object));
    }

    public long time_after_distance(float distance_nm) {
        return (long) (sim_time_zulu() + (distance_nm / ground_speed()) * 3600.0f);
    }

    public long time_after_ete(float eet_min) {
        return (long) (sim_time_zulu() + eet_min * 60.0f);
    }

    public long ete_for_distance(float distance_nm) {
        return (long) (distance_nm / ground_speed() * 3600.0f);
    }

    
    public float sim_time_zulu() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_TIME_ZULU_TIME_SEC);
    }

    public boolean timer_is_running() {
        return (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_TIME_TIMER_IS_RUNNING_SEC) != 0.0f );
    }
    
    public float timer_elapsed_time() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_TIME_TIMER_ELAPSED_TIME_SEC);
    }

    public float total_flight_time() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_TIME_TOTAL_FLIGHT_TIME_SEC);
    }


    public float magnetic_variation() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_MAGVAR);
    }

    public float oat() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_WEATHER_TEMPERATURE_AMBIENT_C);
    }

    public float tat() {
        return ( (oat() + 273.15f ) * ( 1 + ( (1.4f-1.0f)/2.0f*1.0f*mach()*mach()) ) ) - 273.15f;
    }

    public float mach() {
        return true_air_speed() / sound_speed();
    }

    public float sound_speed() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_WEATHER_SPEED_SOUND_MS) * 1.944f;
    }

    public int num_gears() { return (int)sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_AIRCRAFT_GEAR_COUNT); }

    public float get_gear(int gear) {
        if ( ( gear >= 0 ) && ( gear < num_gears() ) ) {
            return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL2_GEAR_DEPLOY_RATIO_ + gear);
        } else {
            return -999.999f;
        }
    }

    public boolean gear_is_down() {
        boolean all_down = true;
        int n = num_gears();
        if (n > 0) {
            for (int i=0; i<n; i++) {
                if (get_gear(i) < 1.0f) {
                    all_down = false;
                }
            }
        }
        return all_down;
    }

    public boolean gear_is_up() {
        boolean all_up = true;
        int n = num_gears();
        if (n > 0) {
            for (int i=0; i<n; i++) {
                if (get_gear(i) > 0.0f) {
                    all_up = false;
                }
            }
        }
        return all_up;
    }

    public boolean on_ground() {
        return ( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_FAILURES_ONGROUND_ANY) == 1.0f );
    }

    public boolean master_warning() {
        return ( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_ANNUNCIATORS_MASTER_WARNING) == 1.0f );
    }

    public boolean master_caution() {
        return ( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_ANNUNCIATORS_MASTER_CAUTION) == 1.0f );
    }

    public float get_flap_position() {
        // sim data for handle and real position are reversed
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_CONTROLS_FLAP_HANDLE_DEPLOY_RATIO);
    }

    public float get_flap_handle() {
        // sim data for handle and real position are reversed
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_CONTROLS_FLAP_RATIO);
    }

    public int get_flap_detents() {
        return (int)sim_data.get_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_CONTROLS_ACF_FLAP_DETENTS);
    }

    public float get_speed_brake() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL2_CONTROLS_SPEEDBRAKE_RATIO);
    }

    public boolean speed_brake_armed() {
        return ( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_CONTROLS_SPEEDBRAKE_RATIO) == -0.5f );
    }

    public float get_parking_brake() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_CONTROLS_PARKING_BRAKE_RATIO);
    }

    public boolean stall_warning() {
        return ( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_ANNUNCIATORS_STALL_WARNING) == 1.0f );
    }

    public boolean terrain_warning() {
        return ( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_ANNUNCIATORS_GPWS) == 1.0f );
    }

    public boolean low_fuel() {
        return ( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_ANNUNCIATORS_FUEL_QUANTITY) == 1.0f );
    }

    public boolean ap_disconnect() {
        return ( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_ANNUNCIATORS_AUTOPILOT_DISCONNECT) == 1.0f );
    }

    public boolean icing() {
        return ( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_ANNUNCIATORS_ICE) == 1.0f );
    }

    public boolean pitot_heat() {
        return ( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_ANNUNCIATORS_PITOT_HEAT) == 1.0f );
    }

    public boolean gear_warning() {
        return ( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_ANNUNCIATORS_GEAR_WARNING) == 1.0f );
    }

    public boolean gear_unsafe() {
        return ( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_ANNUNCIATORS_GEAR_UNSAFE) == 1.0f );
    }

    public int auto_brake() {
        return ((int)sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_SWITCHES_AUTO_BRAKE_LEVEL)) - 1;
    }

    public float get_Vso() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_VIEW_ACF_VSO);
    }

    public float get_Vs() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_VIEW_ACF_VS);
    }

    public float get_Vfe() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_VIEW_ACF_VFE);
    }

    public float get_Vno() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_VIEW_ACF_VNO);
    }

    public float get_Vne() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_VIEW_ACF_VNE);
    }

    public float get_Mmo() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_VIEW_ACF_MMO);
    }

    public float get_Vle() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_OVERFLOW_ACF_VLE);
    }

    public int num_engines() {
        //return (int)sim_data.get_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_ENGINE_ACF_NUM_ENGINES);
        int xp_engines = (int)sim_data.get_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_ENGINE_ACF_NUM_ENGINES);
//xp_engines = 8;
        int override_count = this.xhsi_preferences.get_override_engine_count();
        if ( override_count == 0 ) {
            return xp_engines;
        } else {
            return Math.min(xp_engines, override_count);
        }
    }

    public int num_tanks() { return (int)sim_data.get_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_OVERFLOW_ACF_NUM_TANKS); }

    public float tank_ratio(int tank) {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_OVERFLOW_ACF_TANK_RATIO_ + tank);
    }

    public boolean oil_press_alert(int eng) {return ( ( (int)sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_ANNUNCIATORS_OIL_PRESSURE) & (1<<eng) ) != 0 );}
    public boolean oil_temp_alert(int eng) {return ( ( (int)sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_ANNUNCIATORS_OIL_TEMPERATURE) & (1<<eng) ) != 0 );}
    public boolean fuel_press_alert(int eng) {return ( ( (int)sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_ANNUNCIATORS_FUEL_PRESSURE) & (1<<eng) ) != 0 );}

    public float get_fuel(int tank) {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_FUEL_QUANTITY_ + tank);
    }

    public float get_total_fuel() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_WEIGHT_M_FUEL_TOTAL);
    }

    public void estimate_fuel_capacity() {
        // in any case, it cannot be less than the total fuel currently on board...
        this.fuel_capacity = get_total_fuel();
        // suppose at least one tank is full...
        int n = num_tanks();
        for (int i=0; i<n; i++) {
            this.fuel_capacity = Math.max( this.fuel_capacity, get_fuel(i) / tank_ratio(i) );
        }
    }

    public float get_fuel_capacity() {
        if ( get_total_fuel() > this.fuel_capacity ) estimate_fuel_capacity();
        return this.fuel_capacity;
    }

    public float get_tank_capacity(int tank) {
        if ( get_total_fuel() > this.fuel_capacity ) estimate_fuel_capacity();
        return this.fuel_capacity * tank_ratio(tank);
    }

    public void set_fuel_capacity(float capacity) {
        this.fuel_capacity = capacity;
    }


    public float get_N1(int engine) {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_ENGINE_ENGN_N1_ + engine);
    }

    public float get_ref_N1(int engine) {
        float ref;
        if ( this.avionics.is_x737() ) {
            if ( engine == 0 ) {
                ref = sim_data.get_sim_float(XPlaneSimDataRepository.X737_N1_LIMIT_ENG1);
            } else if ( engine == 1 ) {
                ref = sim_data.get_sim_float(XPlaneSimDataRepository.X737_N1_LIMIT_ENG2);
            } else {
                ref = 0.0f;
            }
        } else if ( this.avionics.is_cl30() ) {
            int cl_caret = (int) sim_data.get_sim_float(XPlaneSimDataRepository.CL30_CARETS);
            if ( cl_caret == 2 ) {
                // should be : ref = sim_data.get_sim_float(XPlaneSimDataRepository.CL30_CLB_N1);
                ref = sim_data.get_sim_float(XPlaneSimDataRepository.CL30_TO_N1);
            } else if ( cl_caret == 3 ) {
                ref = sim_data.get_sim_float(XPlaneSimDataRepository.CL30_TO_N1);
            } else {
                ref = 0.0f;
            }
        } else if ( this.avionics.has_ufmc() ) {
            if ( engine == 0 ) {
                ref = sim_data.get_sim_float(XPlaneSimDataRepository.UFMC_N1_1);
            } else if ( engine == 1 ) {
                ref = sim_data.get_sim_float(XPlaneSimDataRepository.UFMC_N1_2);
            } else if ( engine == 2 ) {
                ref = sim_data.get_sim_float(XPlaneSimDataRepository.UFMC_N1_3);
            } else if ( engine == 3 ) {
                ref = sim_data.get_sim_float(XPlaneSimDataRepository.UFMC_N1_4);
            } else {
                ref = 0.0f;
            }
        } else {
            ref = 0.0f;
        }
        return ref;
    }

    public String get_thrust_mode() {

        if ( this.avionics.is_x737() ) {
            return XPlaneAircraft.x737_thrust_modes[ (int) sim_data.get_sim_float(XPlaneSimDataRepository.X737_N1_PHASE) ];
        } else if ( ( this.avionics.is_cl30() ) && ( this.reverser_position(0) == 0.0f ) ) {
            return XPlaneAircraft.cl30_thrust_modes[ (int) sim_data.get_sim_float(XPlaneSimDataRepository.CL30_CARETS) ];
        } else {
            return "";
        }

    }

    public float get_EGT_percent(int engine) {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_ENGINE_ENGN_EGT_ + engine) * 100.0f;
    }

    public float get_EGT_value(int engine) {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_ENGINE_ENGN_EGT_C_ + engine);
    }

    public float get_N2(int engine) {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_ENGINE_ENGN_N2_ + engine);
    }

    public float get_FF(int engine) {
        float ff = sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_ENGINE_ENGN_FF_ + engine);
        max_fuel_flow = Math.max(ff, max_fuel_flow);
        return ff;
    }

    public float get_max_FF() {
        return max_fuel_flow;
    }

    public void reset_max_FF() {
        max_fuel_flow = 0.0f;
    }

    public float get_oil_press_ratio(int engine) {
        float o_p = sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_ENGINE_ENGN_OIL_PRESS_ + engine);
        if ( o_p > 1.0f )
            return 1.0f;
        else
            return o_p;
    }

    public float get_oil_temp_ratio(int engine) {
        float o_t = sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_ENGINE_ENGN_OIL_TEMP_ + engine);
        if ( o_t > 1.0f )
            return 1.0f;
        else
            return o_t;
    }

    public float get_oil_quant_ratio(int engine) {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_ENGINE_INDICATORS_OIL_QUANTITY_RATIO_ + engine);
    }

    public float get_vib(int engine) {
        return sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_FLIGHTMODEL_ENGINE_VIB_ + engine);
    }

    public float get_hyd_press(int circuit) {
        float h_p;
        if ( circuit == 1 )
            h_p = sim_data.get_sim_float(XPlaneSimDataRepository.SIM_OPERATION_FAILURES_HYDRAULIC_PRESSURE_RATIO1);
        else
            h_p = sim_data.get_sim_float(XPlaneSimDataRepository.SIM_OPERATION_FAILURES_HYDRAULIC_PRESSURE_RATIO2);
        // most values seem to be in the range 3000-3600
        if ( h_p > 5000.0f )
            return 3333.0f / 5000.0f;
        else
            return h_p / 5000.0f;
    }

    public float get_hyd_quant(int circuit) {
        if ( circuit == 1 )
            return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_HYDRAULICS_INDICATORS_HYDRAULIC_FLUID_RATIO_1);
        else
            return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_HYDRAULICS_INDICATORS_HYDRAULIC_FLUID_RATIO_2);
    }

    public float get_TRQ(int engine) {
        // NM = LbFt * 1.35581794884f
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_ENGINE_ENGN_TRQ_ + engine) / 1.35581794884f;
    }

    public float get_max_TRQ() {
        // NM = LbFt * 1.35581794884f
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_CONTROLS_ACF_TRQ_MAX_ENG) / 1.35581794884f;
    }

    public float get_ITT_percent(int engine) {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_ENGINE_ENGN_ITT_ + engine) * 100.0f;
    }

    public float get_ITT_value(int engine) {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_ENGINE_ENGN_ITT_C_ + engine);
    }

    public float get_max_prop_RPM() {
        // rev/min = rad/s * 60 / (2xpi)
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_CONTROLS_ACF_RSC_REDLINE_PRP) * 30.0f / (float)Math.PI;
    }

    public float get_prop_RPM(int engine) {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_ENGINE_INDICATORS_PROP_SPEED_RPM_ + engine);
    }

    public int get_prop_mode(int engine) {
        return (int)sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_ENGINE_ENGN_PROPMODE_ + engine);
    }

    public float get_NG(int engine) {
        // as seen on several turboprops in X-Plane
//        return get_N1(engine) * 1.04f;
        // well, just to get an idea...
        return get_N1(engine);
    }


    public boolean reverser(int engine) {return ( ( (int)sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_ANNUNCIATORS_REVERSER_DEPLOYED) & (1<<engine) ) != 0 );}
    public float reverser_position(int engine) {
        float ratio = sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL2_ENGINES_THRUST_REVERSER_DEPLOY_RATIO_ + engine);
        if ( ( ratio > 0.99f ) || ( ( ratio == 0.0f ) && reverser(engine) ) ) {
            ratio = 1.0f;
        } else if ( ( ratio < 0.01f ) && ! reverser(engine) ) {
            ratio = 0.0f;
        }
        return ratio;
    }


    public float get_MPR(int engine) {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_ENGINE_ENGN_MPR_ + engine);
    }


    public String get_nearest_arpt() {
        return sim_data.get_sim_string(XPlaneSimDataRepository.XHSI_FLIGHTMODEL_POSITION_NEAREST_ARPT);
    }


}
