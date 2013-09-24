/**
* Avionics.java
* 
* Model for an aircraft's avionics systems
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

public interface Avionics {

    public static final int HSI_SOURCE_NAV1 = 0;
    public static final int HSI_SOURCE_NAV2 = 1;
    public static final int HSI_SOURCE_GPS = 2;

    public static final int EFIS_RADIO_ADF = 0;
    public static final int EFIS_RADIO_OFF = 1;
    public static final int EFIS_RADIO_NAV = 2;
    public static final int EFIS_RADIO_BOTH = 999; // for later, only available in override

    public static final int EFIS_MAP_CENTERED = 0;
    public static final int EFIS_MAP_EXPANDED = 1;
    public static final int EFIS_MAP_APP = 0;
    public static final int EFIS_MAP_VOR = 1;
    public static final int EFIS_MAP_MAP = 2;
    public static final int EFIS_MAP_NAV = 3;
    public static final int EFIS_MAP_PLN = 4;

    public static final int EFIS_MAP_RANGE[] = {10, 20, 40, 80, 160, 320, 640};

    public static final int XPDR_OFF = 0;
    public static final int XPDR_STBY = 1;
    public static final int XPDR_ON = 2;
    public static final int XPDR_TA = 3;
    public static final int XPDR_TARA = 4;

    //public static final int MFD_MODE_TAXI = 0;
    public static final int MFD_MODE_ARPT = 0;
    public static final int MFD_MODE_FPLN = 1;
    public static final int MFD_MODE_EICAS = 2;

    public static final int RADIO_NAV1 = 1;
    public static final int RADIO_NAV2 = 2;
    public static final int RADIO_ADF1 = 3;
    public static final int RADIO_ADF2 = 4;
    public static final int RADIO_COM1 = 5;
    public static final int RADIO_COM2 = 6;
    public static final int RADIO_NAV1_STDBY = -1;
    public static final int RADIO_NAV2_STDBY = -2;
    public static final int RADIO_ADF1_STDBY = -3;
    public static final int RADIO_ADF2_STDBY = -4;
    public static final int RADIO_COM1_STDBY = -5;
    public static final int RADIO_COM2_STDBY = -6;

    
    /**
     * @return boolean - do we have avionics power?
     */
    public boolean power();

    /**
     * @return boolean - is OM lit?
     */
    public boolean outer_marker();

    /**
     * @return boolean - is MM lit?
     */
    public boolean middle_marker();

    /**
     * @return boolean - is IM lit?
     */
    public boolean inner_marker();

    /**
     * @return int - selected range of map display in switch setting
     */
    public int map_range_index();

    /**
     * @return int - selected range of map display in NM
     */
    public int map_range();

    /**
     * @return boolean - map is close-up
     */
    public boolean map_zoomin();

    /**
     * @return int - map mode CENTERED or EXPANDED
     */
    public int map_mode();

    /**
     * @return int - map submode APP, VOR, MAP, NAV or PLN
     */
    public int map_submode();

    /**
     * @return int - HSI source selector - either HSI_SOURCE_NAV1, HSI_SOURCE_NAV2 or HSI_SOURCE_GPS
     */
    public int hsi_source();

    /**
     * @return int - EFIS Radio1 setting - either EFIS_RADIO_ADF, EFIS_RADIO_NAV or EFIS_RADIO_OFF
     */
    public int efis_radio1();

    /**
     * @return int - EFIS Radio2 setting - either EFIS_RADIO_ADF, EFIS_RADIO_NAV or EFIS_RADIO_OFF
     */
    public int efis_radio2();

    /**
     * @return boolean - true if EFIS displays waypoints, false otherwise
     */
    public boolean efis_shows_wpt();

    /**
     * @return boolean - true if EFIS displays VORs, false otherwise
     */
    public boolean efis_shows_vor();

    /**
     * @return boolean - true if EFIS displays NDBs, false otherwise
     */
    public boolean efis_shows_ndb();

    /**
     * @return boolean - true if EFIS displays airports, false otherwise
     */
    public boolean efis_shows_arpt();

    /**
     * @return boolean - true if EFIS displays TCAS information, false otherwise
     */
    public boolean efis_shows_tfc();

    /**
     * @return boolean - true if EFIS displays FMS altitude information, false otherwise
     */
    public boolean efis_shows_data();

    /**
     * @return boolean - true if EFIS displays bearing lines, false otherwise
     */
    public boolean efis_shows_pos();

    /**
     * @return NavigationRadio - model class representing the currently selected radio or null, if none is selected
     */
    public NavigationRadio get_selected_radio(int bank);

    /**
     * @return NavigationRadio - model class representing the NAV radio
     */
    public NavigationRadio get_nav_radio(int bank);

    /**
     * @return int - adf frequency
     */
    public float get_radio_freq(int radio_num);

    /**
     * @return NavigationRadio - model class representing the GPS
     */
    public NavigationRadio get_gps_radio();

    /**
     * @return Localizer - model class representing the currently selected localizer or null, if none is selected
     */
    public Localizer get_tuned_localizer(int bank);

    /**
     * @return VOR - model class representing the currently selected VOR or null, if none is selected
     */
    public RadioNavBeacon get_tuned_navaid(int bank);

    /**
     * @return float - selected OBS for NAV1 in degrees
     */
    public float nav1_obs();

    /**
     * @return float - selected OBS for NAV2 in degrees
     */
    public float nav2_obs();

    /**
     * @return float - selected course for NAV1 in degrees
     */
    public float nav1_course();

    /**
     * @return float - selected course for NAV2 in degrees
     */
    public float nav2_course();

    /**
     * @return float - selected course for GPS in degrees
     */
    public float gps_course();

    /**
     * @return float - deflection for NAV1 in dots
     */
    public float nav1_hdef_dot();

    /**
     * @return float - deflection for NAV2 in dots
     */
    public float nav2_hdef_dot();

    /**
     * @return float - deflection for GPS in dots
     */
    public float gps_hdef_dot();

    /**
     * @return float - NAV1 OFF/TO/FROM indicator
     */
    public int nav1_fromto();

    /**
     * @return float - NAV2 OFF/TO/FROM indicator
     */
    public int nav2_fromto();

    /**
     * @return float - GPS OFF/TO/FROM indicator
     */
    public int gps_fromto();

    /**
     * @return float - deflection for NAV1 GS in dots
     */
    public float nav1_vdef_dot();

    /**
     * @return float - deflection for NAV2 GS in dots
     */
    public float nav2_vdef_dot();

    /**
     * @return float - deflection for GPS GS in dots
     */
    public float gps_vdef_dot();

    /**
     * @return boolean - NAV1 GS active
     */

    public boolean nav1_gs_active();
    /**
     * @return boolean - NAV2 GS active
     */
    public boolean nav2_gs_active();

    /**
     * @return boolean - GPS GS active
     */
    public boolean gps_gs_active();


    /**
     * @return int - MFD mode (0=arpt, 1=fpln, 2=eicas)
     */
    public int get_mfd_mode();


    /**
     * TODO: constants for autopilot states need to be defined
     * 
     * @return int - bitmask for autopilot state
     */
    public int autopilot_state();

    /**
     * @return float - vertical velocity in feet per minute selected in autopilot
     */
    public float autopilot_vv();

    /**
     * @return float - autopilot altitude preselect
     */
    public float autopilot_altitude();

    /**
     * @return float - autopilot speed
     */
    public float autopilot_speed();

    /**
     * @return boolean - autopilot speed is mach
     */
    public boolean autopilot_speed_is_mach();

    /**
     * @return float - heading in degrees selected in autopilot
     */
    public float heading_bug();        // degrees

    /**
     * @return float - FD pitch command
     */
    public float fd_pitch();

    /**
     * @return float - FD roll command
     */
    public float fd_roll();

    /**
     * @return boolean - receiving X737 data
     */
    public boolean is_x737();

    public int x737_mcp_spd();

    public int x737_fmc_spd();

    public int x737_retard();

    public int x737_thr_hld();

    public int x737_lnav_armed();

    public int x737_vorloc_armed();

    public int x737_pitch_spd();

    public int x737_alt_hld();

    public int x737_vs_armed();

    public int x737_vs();

    public int x737_vnav_alt();

    public int x737_vnav_path();

    public int x737_vnav_spd();

    public int x737_gs_armed();

    public int x737_gs();

    public int x737_flare_armed();

    public int x737_flare();

    public int x737_toga();

    public int x737_lnav();

    public int x737_hdg();

    public int x737_vorloc();

    public int x737_n1();

    public boolean x737_athr_armed();


    /**
     * @return int - autopilot mode (0=Off/1=FD/2=CMD)
     */
    public int autopilot_mode();

    public boolean autothrottle_enabled();

    public boolean autothrottle_on();

    public boolean ap_hdg_sel_on();

    public boolean ap_vorloc_arm();

    public boolean ap_vorloc_on();

    public boolean ap_lnav_arm();

    public boolean ap_lnav_on();

    public boolean ap_vs_arm();

    public boolean ap_vs_on();

    public boolean ap_flch_on();

    public boolean ap_alt_hold_arm();

    public boolean ap_alt_hold_on();

    public boolean ap_gs_arm();

    public boolean ap_gs_on();

    public boolean ap_bc_arm();

    public boolean ap_bc_on();

    public boolean ap_vnav_arm();

    public boolean ap_vnav_on();

    public boolean ap_vtoga_arm();

    public boolean ap_vtoga_on();

    public boolean ap_ltoga_arm();

    public boolean ap_ltoga_on();

    public boolean ap_roll_on();

    public boolean ap_pitch_on();

    /**
     * @return Aircraft - reference to aircraft model class to which avionics belongs
     */
    public Aircraft get_aircraft();

    /**
     * @return FMS - reference to flight management system model class
     */
    public FMS get_fms();

    /**
     * @return TCAS - reference to tcas model class
     */
    public TCAS get_tcas();

    /**
     * @return int - transponder mode
     */
    public int transponder_mode();

    /**
     * @return int - transponder code
     */
    public int transponder_code();


    /**
     * @return boolean - receiving CL30 data
     */
    public boolean is_cl30();
    
    /**
     * @return int - CL30's sim/custom/xap/refspds
     */
    public int cl30_refspds();

    public int cl30_v1();
    public int cl30_vr();
    public int cl30_v2();
    public int cl30_vt();
    public int cl30_vga();
    public int cl30_vref();
    public float cl30_mast_warn();
    public float cl30_mast_caut();

    
    public boolean has_ufmc();

    public float ufmc_v1();
    public float ufmc_vr();
    public float ufmc_v2();
    public float ufmc_vref();
    public float ufmc_vf30();
    public float ufmc_vf40();
    
    

    //    public void set_power(boolean new_power);

    public void set_hsi_source(int new_source);

    public void set_nav1_obs(float new_obs1);

    public void set_nav2_obs(float new_obs2);

    public void set_radio1(int new_radio1);

    public void set_radio2(int new_radio2);

    public void set_zoomin(boolean new_closeup);

    public void set_submode(int new_submode);

    public void set_mode(int new_mode);

    public void set_range_index(int new_range_index);

    public void set_xpdr(int new_xpdr);

    public void set_show_arpt(boolean new_arpt);

    public void set_show_wpt(boolean new_wpt);

    public void set_show_vor(boolean new_vor);

    public void set_show_ndb(boolean new_ndb);

    public void set_show_tfc(boolean new_tfc);

    public void set_show_pos(boolean new_pos);

    public void set_show_data(boolean new_data);
    
    public void set_mfd_mode(int new_mode);



}
