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
package net.sourceforge.xhsi.model.xplane;

import java.util.ArrayList;
import java.util.logging.Logger;

import net.sourceforge.xhsi.model.Observer;
import net.sourceforge.xhsi.model.SimDataRepository;

public class XPlaneSimDataRepository implements SimDataRepository {

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    // Aircraft position
    public static final int SIM_FLIGHTMODEL_POSITION_GROUNDSPEED = 0;
    public static final int SIM_FLIGHTMODEL_POSITION_TRUE_AIRSPEED = 1;
    public static final int SIM_FLIGHTMODEL_POSITION_MAGPSI = 2; // mag heading
    public static final int SIM_FLIGHTMODEL_POSITION_HPATH = 3; // track (true or mag?)
    public static final int SIM_FLIGHTMODEL_POSITION_LATITUDE = 4;
    public static final int SIM_FLIGHTMODEL_POSITION_LONGITUDE = 5;
    public static final int SIM_FLIGHTMODEL_POSITION_PHI = 6;            // roll angle = bank
    public static final int SIM_FLIGHTMODEL_POSITION_R = 7;            // yaw rotation rate in deg/sec
    public static final int SIM_FLIGHTMODEL_POSITION_MAGVAR = 8; // XPSDK says that this dataref should be part of the environment, not the aircraft
    public static final int SIM_FLIGHTMODEL_POSITION_ELEVATION = 9; // meters
    public static final int SIM_FLIGHTMODEL_POSITION_Y_AGL = 10; // meters
    public static final int SIM_FLIGHTMODEL_POSITION_THETA = 11; // pitch (deg)
    public static final int SIM_FLIGHTMODEL_POSITION_VPATH = 12; // fpa
    public static final int SIM_FLIGHTMODEL_POSITION_ALPHA = 13; // aoa
    public static final int SIM_FLIGHTMODEL_POSITION_BETA = 14; // yaw ( = slip or drift ? )
    public static final int SIM_FLIGHTMODEL_FAILURES_ONGROUND_ANY = 15; // It was misplaced and is not really a failure, you can use that to indicate when the wheels are on the ground
    public static final int XHSI_FLIGHTMODEL_POSITION_NEAREST_ARPT = 10016;



    // Instruments
//    public static final int SIM_FLIGHTMODEL_POSITION_VH_IND_FPM = 50; // vertical velocity
//    public static final int SIM_FLIGHTMODEL_MISC_H_IND = 51; // indicated barometric altitude
    public static final int SIM_COCKPIT2_GAUGES_INDICATORS_AIRSPEED_KTS_PILOT = 52;
    public static final int SIM_COCKPIT2_GAUGES_INDICATORS_AIRSPEED_KTS_COPILOT = 53;
    public static final int SIM_COCKPIT2_GAUGES_INDICATORS_ALTITUDE_FT_PILOT = 54;
    public static final int SIM_COCKPIT2_GAUGES_INDICATORS_ALTITUDE_FT_COPILOT = 55;
    public static final int SIM_COCKPIT2_GAUGES_INDICATORS_VVI_FPM_PILOT = 56;
    public static final int SIM_COCKPIT2_GAUGES_INDICATORS_VVI_FPM_COPILOT = 57;
    public static final int SIM_COCKPIT2_GAUGES_INDICATORS_SIDESLIP_DEGREES = 58;
    public static final int SIM_COCKPIT2_GAUGES_ACTUATORS_RADIO_ALTIMETER_BUG_FT_PILOT = 59;
    public static final int SIM_COCKPIT2_GAUGES_ACTUATORS_RADIO_ALTIMETER_BUG_FT_COPILOT = 60;
    public static final int SIM_COCKPIT2_GAUGES_ACTUATORS_BAROMETER_SETTING_IN_HG_PILOT = 61;
    public static final int SIM_COCKPIT2_GAUGES_ACTUATORS_BAROMETER_SETTING_IN_HG_COPILOT = 62;
    public static final int SIM_COCKPIT2_GAUGES_INDICATORS_AIRSPEED_ACCELERATION = 63;
    public static final int XHSI_EFIS_PILOT_DA_BUG = 64;
    public static final int XHSI_EFIS_PILOT_MINS_MODE = 65;
    public static final int XHSI_EFIS_COPILOT_DA_BUG = 66;
    public static final int XHSI_EFIS_COPILOT_MINS_MODE = 67;


    // Electrical
    public static final int SIM_COCKPIT_ELECTRICAL_AVIONICS_ON = 80;
    public static final int SIM_COCKPIT_ELECTRICAL_BATTERY_ON = 81;
    public static final int SIM_COCKPIT_ELECTRICAL_COCKPIT_LIGHTS_ON = 82;


    // Radios
    public static final int SIM_COCKPIT_RADIOS_NAV1_FREQ_HZ = 100;
    public static final int SIM_COCKPIT_RADIOS_NAV2_FREQ_HZ = 101;
    public static final int SIM_COCKPIT_RADIOS_ADF1_FREQ_HZ = 102;
    public static final int SIM_COCKPIT_RADIOS_ADF2_FREQ_HZ = 103;
    public static final int SIM_COCKPIT_RADIOS_NAV1_DIR_DEGT = 104;
    public static final int SIM_COCKPIT_RADIOS_NAV2_DIR_DEGT = 105;
    public static final int SIM_COCKPIT_RADIOS_ADF1_DIR_DEGT = 106;
    public static final int SIM_COCKPIT_RADIOS_ADF2_DIR_DEGT = 107;
    public static final int SIM_COCKPIT_RADIOS_NAV1_DME_DIST_M = 108;
    public static final int SIM_COCKPIT_RADIOS_NAV2_DME_DIST_M = 109;
    public static final int SIM_COCKPIT_RADIOS_ADF1_DME_DIST_M = 110; // wtf?
    public static final int SIM_COCKPIT_RADIOS_ADF2_DME_DIST_M = 111; // wtf?
    public static final int SIM_COCKPIT_RADIOS_NAV1_OBS_DEGM = 112; // OBS is set manually
    public static final int SIM_COCKPIT_RADIOS_NAV2_OBS_DEGM = 113;
    public static final int SIM_COCKPIT_RADIOS_NAV1_COURSE_DEGM = 114; // Course=OBS for VORs; Course=localizer_frontcourse for LOC, ILS & IGS
    public static final int SIM_COCKPIT_RADIOS_NAV2_COURSE_DEGM = 115;
    public static final int SIM_COCKPIT_RADIOS_NAV1_CDI = 116; // CDI is not Course Deviation Indication, but "Receiving an expected glide slope"
    public static final int SIM_COCKPIT_RADIOS_NAV2_CDI = 117;
    public static final int SIM_COCKPIT_RADIOS_NAV1_HDEF_DOT = 118;
    public static final int SIM_COCKPIT_RADIOS_NAV2_HDEF_DOT = 119;
    public static final int SIM_COCKPIT_RADIOS_NAV1_FROMTO = 120;
    public static final int SIM_COCKPIT_RADIOS_NAV2_FROMTO = 121;
    public static final int SIM_COCKPIT_RADIOS_NAV1_VDEF_DOT = 122;
    public static final int SIM_COCKPIT_RADIOS_NAV2_VDEF_DOT = 123;

    public static final int SIM_COCKPIT_RADIOS_GPS_DIR_DEGT = 124;
    public static final int SIM_COCKPIT_RADIOS_GPS_DME_DIST_M = 125;
    public static final int SIM_COCKPIT_RADIOS_GPS_COURSE_DEGTM = 126;
    public static final int SIM_COCKPIT_RADIOS_GPS_HDEF_DOT = 127;
    public static final int SIM_COCKPIT_RADIOS_GPS_FROMTO = 128;
    public static final int SIM_COCKPIT_RADIOS_GPS_VDEF_DOT = 129;

    public static final int SIM_COCKPIT_RADIOS_NAV1_DME_TIME_SECS = 130;
    public static final int SIM_COCKPIT_RADIOS_NAV2_DME_TIME_SECS = 131;
    public static final int SIM_COCKPIT_RADIOS_GPS_DME_TIME_SECS = 132;

    public static final int SIM_COCKPIT2_RADIOS_INDICATORS_OUTER_MARKER_LIT = 133;
    public static final int SIM_COCKPIT2_RADIOS_INDICATORS_MIDDLE_MARKER_LIT = 134;
    public static final int SIM_COCKPIT2_RADIOS_INDICATORS_INNER_MARKER_LIT = 135;

    public static final int SIM_COCKPIT_RADIOS_NAV1_STDBY_FREQ_HZ = 136;
    public static final int SIM_COCKPIT_RADIOS_NAV2_STDBY_FREQ_HZ = 137;
    public static final int SIM_COCKPIT_RADIOS_ADF1_STDBY_FREQ_HZ = 138;
    public static final int SIM_COCKPIT_RADIOS_ADF2_STDBY_FREQ_HZ = 139;

    public static final int SIM_COCKPIT2_RADIOS_INDICATORS_NAV1_NAV_ID = 10140;
    public static final int SIM_COCKPIT2_RADIOS_INDICATORS_NAV2_NAV_ID = 10141;
    public static final int SIM_COCKPIT2_RADIOS_INDICATORS_ADF1_NAV_ID = 10142;
    public static final int SIM_COCKPIT2_RADIOS_INDICATORS_ADF2_NAV_ID = 10143;

    public static final int SIM_COCKPIT_RADIOS_COM1_FREQ_HZ = 144;
    public static final int SIM_COCKPIT_RADIOS_COM1_STDBY_FREQ_HZ = 145;
    public static final int SIM_COCKPIT_RADIOS_COM2_FREQ_HZ = 146;
    public static final int SIM_COCKPIT_RADIOS_COM2_STDBY_FREQ_HZ = 147;


    // AP
    public static final int SIM_COCKPIT_AUTOPILOT_AUTOPILOT_STATE = 150;
    public static final int SIM_COCKPIT_AUTOPILOT_VERTICAL_VELOCITY = 151;
    public static final int SIM_COCKPIT_AUTOPILOT_ALTITUDE = 152;
    public static final int SIM_COCKPIT_AUTOPILOT_APPROACH_SELECTOR = 153;
    public static final int SIM_COCKPIT_AUTOPILOT_HEADING_MAG = 154;
    public static final int SIM_COCKPIT_AUTOPILOT_AIRSPEED = 155;
    public static final int SIM_COCKPIT_AUTOPILOT_AIRSPEED_IS_MACH = 156;
    public static final int SIM_COCKPIT_AUTOPILOT_FD_PITCH = 157;
    public static final int SIM_COCKPIT_AUTOPILOT_FD_ROLL = 158;
    public static final int SIM_COCKPIT_AUTOPILOT_MODE = 159;
    public static final int SIM_COCKPIT2_AUTOPILOT_AUTOTHROTTLE_ENABLED = 160;
    public static final int SIM_COCKPIT2_AUTOPILOT_AUTOTHROTTLE_ON = 161;
    public static final int SIM_COCKPIT2_AUTOPILOT_HEADING_STATUS = 162;
    public static final int SIM_COCKPIT2_AUTOPILOT_NAV_STATUS = 163;
    public static final int SIM_COCKPIT2_AUTOPILOT_VVI_STATUS = 164;
    public static final int SIM_COCKPIT2_AUTOPILOT_SPEED_STATUS = 165;
    public static final int SIM_COCKPIT2_AUTOPILOT_ALTITUDE_HOLD_STATUS = 166;
    public static final int SIM_COCKPIT2_AUTOPILOT_GLIDESLOPE_STATUS = 167;
    public static final int SIM_COCKPIT2_AUTOPILOT_VNAV_STATUS = 168;
    public static final int SIM_COCKPIT2_AUTOPILOT_TOGA_STATUS = 169;
    public static final int SIM_COCKPIT2_AUTOPILOT_TOGA_LATERAL_STATUS = 170;
    public static final int SIM_COCKPIT2_AUTOPILOT_ROLL_STATUS = 171;
    public static final int SIM_COCKPIT2_AUTOPILOT_PITCH_STATUS = 172;
    public static final int SIM_COCKPIT2_AUTOPILOT_BACKCOURSE_STATUS = 173;


    // Transponder
    public static final int SIM_COCKPIT_RADIOS_TRANSPONDER_MODE = 180; // 0=OFF, 1=STDBY, 2=ON, 3=TEST
    public static final int SIM_COCKPIT_RADIOS_TRANSPONDER_CODE = 181;


    // EFIS
    public static final int SIM_COCKPIT_SWITCHES_HSI_SELECTOR = 200; // 0=NAV1, 1=NAV2, 2=GPS
    public static final int SIM_COCKPIT_SWITCHES_EFIS_MAP_RANGE_SELECTOR = 201; // 0=10NM, 1=20, 2=40, etc...
    public static final int SIM_COCKPIT_SWITCHES_EFIS_DME_1_SELECTOR = 202;
    public static final int SIM_COCKPIT_SWITCHES_EFIS_DME_2_SELECTOR = 203;
    public static final int SIM_COCKPIT_SWITCHES_EFIS_SHOWS_WEATHER = 204;
    public static final int SIM_COCKPIT_SWITCHES_EFIS_SHOWS_TCAS = 205;
    public static final int SIM_COCKPIT_SWITCHES_EFIS_SHOWS_AIRPORTS = 206;
    public static final int SIM_COCKPIT_SWITCHES_EFIS_SHOWS_WAYPOINTS = 207;
    public static final int SIM_COCKPIT_SWITCHES_EFIS_SHOWS_VORS = 208;
    public static final int SIM_COCKPIT_SWITCHES_EFIS_SHOWS_NDBS = 209;
    public static final int SIM_COCKPIT_SWITCHES_EFIS_MAP_MODE = 210; // 0=Centered, 1=Expanded
    public static final int SIM_COCKPIT_SWITCHES_EFIS_MAP_SUBMODE = 211; // 0=APP, 1=VOR, 2=MAP, 3=NAV, 4=PLN
    public static final int XHSI_EFIS_PILOT_STA = 212;
    public static final int XHSI_EFIS_PILOT_DATA = 213;
    public static final int XHSI_EFIS_PILOT_POS = 214;
    public static final int XHSI_EFIS_PILOT_MAP_ZOOMIN = 215;


    // Copilot EFIS
    public static final int XHSI_EFIS_COPILOT_HSI_SOURCE = 250;
    public static final int XHSI_EFIS_COPILOT_MAP_RANGE = 251;
    public static final int XHSI_EFIS_COPILOT_RADIO1 = 252;
    public static final int XHSI_EFIS_COPILOT_RADIO2 = 253;
    public static final int XHSI_EFIS_COPILOT_WXR = 254;
    public static final int XHSI_EFIS_COPILOT_TFC = 255;
    public static final int XHSI_EFIS_COPILOT_ARPT = 256;
    public static final int XHSI_EFIS_COPILOT_WPT = 257;
    public static final int XHSI_EFIS_COPILOT_VOR = 258;
    public static final int XHSI_EFIS_COPILOT_NDB = 259;
    public static final int XHSI_EFIS_COPILOT_MAP_CTR = 260;
    public static final int XHSI_EFIS_COPILOT_MAP_MODE = 261;
    public static final int XHSI_EFIS_COPILOT_STA = 262;
    public static final int XHSI_EFIS_COPILOT_DATA = 263;
    public static final int XHSI_EFIS_COPILOT_POS = 264;
    public static final int XHSI_EFIS_COPILOT_MAP_ZOOMIN = 265;


    // MFD
    public static final int XHSI_MFD_MODE = 290;


    // Environment
    public static final int SIM_WEATHER_WIND_SPEED_KT = 300;
    public static final int SIM_WEATHER_WIND_DIRECTION_DEGT = 301;
    public static final int SIM_TIME_ZULU_TIME_SEC = 302;
    public static final int SIM_TIME_LOCAL_TIME_SEC = 303;
    public static final int SIM_WEATHER_TEMPERATURE_AMBIENT_C = 304;
    public static final int SIM_WEATHER_SPEED_SOUND_MS = 305;
    // Timers
    public static final int SIM_TIME_TIMER_IS_RUNNING_SEC = 306;
    public static final int SIM_TIME_TIMER_ELAPSED_TIME_SEC = 307;
    public static final int SIM_TIME_TOTAL_FLIGHT_TIME_SEC = 308;


    // Aircraft constants
    public static final int SIM_AIRCRAFT_VIEW_ACF_VSO = 320;
    public static final int SIM_AIRCRAFT_VIEW_ACF_VS = 321;
    public static final int SIM_AIRCRAFT_VIEW_ACF_VFE = 322;
    public static final int SIM_AIRCRAFT_VIEW_ACF_VNO = 323;
    public static final int SIM_AIRCRAFT_VIEW_ACF_VNE = 324;
    public static final int SIM_AIRCRAFT_VIEW_ACF_MMO = 325;
    public static final int SIM_AIRCRAFT_OVERFLOW_ACF_VLE = 326;



    // Controls & annunciators
    public static final int SIM_COCKPIT2_ANNUNCIATORS_MASTER_CAUTION = 350;
    public static final int SIM_COCKPIT2_ANNUNCIATORS_MASTER_WARNING = 351;
    public static final int SIM_COCKPIT2_CONTROLS_GEAR_HANDLE_DOWN = 352;
    public static final int SIM_COCKPIT2_ANNUNCIATORS_GEAR_UNSAFE = 353;
    public static final int XHSI_AIRCRAFT_GEAR_COUNT = 354; // Calculated from sim/aircraft/parts/acf_gear_type int[10]
    public static final int SIM_COCKPIT2_CONTROLS_PARKING_BRAKE_RATIO = 355;
    //public static final int XHSI_FREE_FOR_FUTURE_USE = 356;
    public static final int SIM_COCKPIT2_CONTROLS_FLAP_RATIO = 357; // this is supposed to be the handle location
    public static final int SIM_COCKPIT2_CONTROLS_FLAP_HANDLE_DEPLOY_RATIO = 358; // this is supposed to be the indicator
    public static final int SIM_AIRCRAFT_CONTROLS_ACF_FLAP_DETENTS = 359;
    public static final int SIM_COCKPIT2_ANNUNCIATORS_AUTOPILOT_DISCONNECT = 360;
    public static final int SIM_COCKPIT2_ANNUNCIATORS_FUEL_QUANTITY = 361;
    public static final int SIM_COCKPIT2_ANNUNCIATORS_GPWS = 362;
    public static final int SIM_COCKPIT2_ANNUNCIATORS_ICE = 363;
    public static final int SIM_COCKPIT2_ANNUNCIATORS_PITOT_HEAT = 364;
    public static final int SIM_COCKPIT2_ANNUNCIATORS_STALL_WARNING = 365;
    public static final int SIM_COCKPIT2_ANNUNCIATORS_GEAR_WARNING = 366;
    public static final int SIM_COCKPIT2_SWITCHES_AUTO_BRAKE_LEVEL = 367;
    public static final int SIM_COCKPIT2_CONTROLS_SPEEDBRAKE_RATIO = 368;
    public static final int SIM_FLIGHTMODEL2_CONTROLS_SPEEDBRAKE_RATIO = 369;
    public static final int SIM_FLIGHTMODEL2_GEAR_DEPLOY_RATIO_ = 370; // array of 10 floats


    // Fuel, engines, etc...
    public static final int SIM_AIRCRAFT_OVERFLOW_ACF_NUM_TANKS = 400;
    public static final int SIM_AIRCRAFT_ENGINE_ACF_NUM_ENGINES = 401;
    public static final int SIM_COCKPIT2_ANNUNCIATORS_REVERSER_DEPLOYED = 402;  // bitfield
    public static final int SIM_COCKPIT2_ANNUNCIATORS_OIL_PRESSURE = 403;  // bitfield
    public static final int SIM_COCKPIT2_ANNUNCIATORS_OIL_TEMPERATURE = 404;  // bitfield
    public static final int SIM_COCKPIT2_ANNUNCIATORS_FUEL_PRESSURE = 405;  // bitfield
    public static final int SIM_FLIGHTMODEL_WEIGHT_M_FUEL_TOTAL = 406;
    public static final int SIM_COCKPIT2_FUEL_QUANTITY_ = 410; // 410..418 : array
    public static final int SIM_FLIGHTMODEL_ENGINE_ENGN_N1_ = 420; // array
    public static final int SIM_FLIGHTMODEL_ENGINE_ENGN_EGT_ = 430; // array
    public static final int SIM_FLIGHTMODEL_ENGINE_ENGN_EGT_C_ = 440; // array
    public static final int SIM_FLIGHTMODEL2_ENGINES_THRUST_REVERSER_DEPLOY_RATIO_ = 450; //array
    public static final int SIM_AIRCRAFT_OVERFLOW_ACF_TANK_RATIO_ = 460; //array
    public static final int SIM_FLIGHTMODEL_ENGINE_ENGN_N2_ = 470; // array
    public static final int SIM_FLIGHTMODEL_ENGINE_ENGN_FF_ = 480; // array
    public static final int SIM_FLIGHTMODEL_ENGINE_ENGN_OIL_PRESS_ = 490; // array
    public static final int SIM_FLIGHTMODEL_ENGINE_ENGN_OIL_TEMP_ = 500; // array
    public static final int SIM_COCKPIT2_ENGINE_INDICATORS_OIL_QUANTITY_RATIO_ = 510; // array
    public static final int XHSI_FLIGHTMODEL_ENGINE_VIB_ = 520; // array
    public static final int SIM_OPERATION_FAILURES_HYDRAULIC_PRESSURE_RATIO1 = 531;
    public static final int SIM_OPERATION_FAILURES_HYDRAULIC_PRESSURE_RATIO2 = 532;
    public static final int SIM_COCKPIT2_HYDRAULICS_INDICATORS_HYDRAULIC_FLUID_RATIO_1 = 533;
    public static final int SIM_COCKPIT2_HYDRAULICS_INDICATORS_HYDRAULIC_FLUID_RATIO_2 = 534;
    public static final int SIM_AIRCRAFT_CONTROLS_ACF_TRQ_MAX_ENG = 539;
    public static final int SIM_FLIGHTMODEL_ENGINE_ENGN_TRQ_ = 540;
    public static final int SIM_FLIGHTMODEL_ENGINE_ENGN_ITT_ = 550;
    public static final int SIM_FLIGHTMODEL_ENGINE_ENGN_ITT_C_ = 560;
    public static final int SIM_AIRCRAFT_CONTROLS_ACF_RSC_REDLINE_PRP = 569;
    public static final int SIM_COCKPIT2_ENGINE_INDICATORS_PROP_SPEED_RPM_ = 570;
    public static final int SIM_FLIGHTMODEL_ENGINE_ENGN_PROPMODE_ = 580;
    public static final int SIM_FLIGHTMODEL_ENGINE_ENGN_MPR_ = 590;


    // UFMC
    public static final int UFMC_STATUS = 700;
    // UFMC V-speeds
    public static final int UFMC_V1 = 701;
    public static final int UFMC_VR = 702;
    public static final int UFMC_V2 = 703;
    public static final int UFMC_VREF = 704;
    public static final int UFMC_VF30 = 705;
    public static final int UFMC_VF40 = 706;
    // UFMC N1
    public static final int UFMC_N1_1 = 707;
    public static final int UFMC_N1_2 = 708;
    public static final int UFMC_N1_3 = 709;
    public static final int UFMC_N1_4 = 710;


    // X737
    public static final int X737_STATUS = 800;
    // X737 AP
    public static final int X737_AFDS_FD_A = 801;
    public static final int X737_AFDS_FD_B = 802;
    public static final int X737_AFDS_CMD_A = 803;
    public static final int X737_AFDS_CMD_B = 804;
    public static final int X737_PFD_MCPSPD = 805;
    public static final int X737_PFD_FMCSPD = 806;
    public static final int X737_PFD_RETARD = 807;
    public static final int X737_PFD_THRHLD = 808;
    public static final int X737_PFD_LNAVARMED = 809;
    public static final int X737_PFD_VORLOCARMED = 810;
    public static final int X737_PFD_PITCHSPD = 811;
    public static final int X737_PFD_ALTHLD = 812;
    public static final int X737_PFD_VSARMED = 813;
    public static final int X737_PFD_VS = 814;
    public static final int X737_PFD_VNAVALT = 815;
    public static final int X737_PFD_VNAVPATH = 816;
    public static final int X737_PFD_VNAVSPD = 817;
    public static final int X737_PFD_GSARMED = 818;
    public static final int X737_PFD_GS = 819;
    public static final int X737_PFD_FLAREARMED = 820;
    public static final int X737_PFD_FLARE = 821;
    public static final int X737_PFD_TOGA = 822;
    public static final int X737_PFD_LNAV = 823;
    public static final int X737_PFD_HDG = 824;
    public static final int X737_PFD_VORLOC = 825;
    public static final int X737_PFD_N1 = 826;
    public static final int X737_AFDS_A_PITCH = 827;
    public static final int X737_AFDS_B_PITCH = 828;
    public static final int X737_AFDS_A_ROLL = 829;
    public static final int X737_AFDS_B_ROLL = 830;
    public static final int X737_ATHR_ARMED = 831;
// X737 N1
    public static final int X737_N1_PHASE = 832;
    public static final int X737_N1_LIMIT_ENG1 = 833;
    public static final int X737_N1_LIMIT_ENG2 = 834;
    public static final int X737_STBY_PWR = 835;
    public static final int X737_PFD_PWR = 836;

    
    // CL30
    public static final int CL30_STATUS = 850;
    // CL30 REF
    public static final int CL30_V1 = 851;
    public static final int CL30_VR = 852;
    public static final int CL30_V2 = 853;
    public static final int CL30_VT = 854;
    public static final int CL30_VGA = 855;
    public static final int CL30_VREF = 856;
    public static final int CL30_REFSPDS = 857;
    // CL30 ANNUN
    public static final int CL30_MAST_WARN = 858;
    public static final int CL30_MAST_CAUT = 859;
    // CL30 Thrust Mode
    public static final int CL30_CARETS = 860;
    public static final int CL30_TO_N1 = 861;
    public static final int CL30_CLB_N1 = 862;
    

    // Plugin Version
    public static final int PLUGIN_VERSION_ID = 999;


    // array with sim data for all sim data points defined above
    float[] sim_values_float = new float[1000];
    int[] sim_values_int = new int[1000];
    String[] sim_values_string = new String[1000];

    long updates = 0;
    ArrayList observers;
    public static boolean replaying = false;

//    private static XPlaneSimDataRepository single_instance;
//
//    public static XPlaneSimDataRepository get_instance() {
//        if (XPlaneSimDataRepository.single_instance == null) {
//            XPlaneSimDataRepository.single_instance = new XPlaneSimDataRepository();
//        }
//        return XPlaneSimDataRepository.single_instance;
//    }

    public XPlaneSimDataRepository() {
        observers = new ArrayList();
        for (int i=0; i<1000; i++) {
            sim_values_string[i] = "";
        }
    }

    public boolean is_replaying() {
        return replaying;
    }

    public void add_observer(Observer observer) {
        this.observers.add(observer);
    }

    public void store_sim_float(int id, float value) {
        sim_values_float[id] = value;
    }

//    public void store_sim_int(int id, int value) {
//        sim_values_int[id % 5000] = value;
//    }

    public void store_sim_string(int id, String value) {
        sim_values_string[id % 10000] = value;
    }

    public float get_sim_float(int id) {
        return sim_values_float[id];
    }

//    public int get_sim_int(int id) {
//        return sim_values_int[id % 5000];
//    }

    public String get_sim_string(int id) {
        return sim_values_string[id % 10000];
    }

    public void tick_updates() {
        this.updates += 1;
        for (int i=0; i<this.observers.size(); i++) {
            logger.fine("Updating observer "+i);
            ((Observer) this.observers.get(i)).update();
        }
    }

    public long get_nb_of_updates() {
        return this.updates;
    }

}
