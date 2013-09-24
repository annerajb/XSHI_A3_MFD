/**
* TCAS.java
* 
* Model class for TCAS data
* inspired by TCAS II Version 7 http://www.arinc.com/downloads/tcas/tcas.pdf
* 
* Copyright (C) 2009-2010  Marc Rogiers (marrog.123@gmail.com)
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

import java.util.logging.Logger;



public class TCAS {

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    public static final int NONE = 0;
    public static final int FARAWAY = 1; // far away
    public static final int OTHER = 2; // Other Traffic
    public static final int PROX = 3; // Proximate Traffic
    public static final int TA = 4; // Traffic Advisory
    public static final int RA = 5; // Resolution Advisory

    public static final int MAX_ENTRIES = 20;

    // public use
    public boolean ra;
    public boolean ta;
    public int total;
    public int active;
    public float lat[] = new float[MAX_ENTRIES];
    public float lon[] = new float[MAX_ENTRIES];
    public float rel_altitude[] = new float[MAX_ENTRIES];
    public int rel_alt_100[] = new int[MAX_ENTRIES];
    public int alarm[] = new int[MAX_ENTRIES];
    public boolean climbing[] = new boolean[MAX_ENTRIES];
    public boolean descending[] = new boolean[MAX_ENTRIES];

    // internal use
    private float my_agl;
    private float my_lat;
    private float my_lon;
    private float my_msl;
    private long last_time;
    private long system_time;
    private float delta_time;
    private float dist_nm[] = new float[MAX_ENTRIES];
    private float rel_alt_ft[] = new float[MAX_ENTRIES];
    private float elev[] = new float[MAX_ENTRIES];
    private boolean tau_ra[] = new boolean[MAX_ENTRIES];
    private boolean tau_ta[] = new boolean[MAX_ENTRIES];
    private float sl_tau_ta, sl_tau_ra, sl_dmod_ta, sl_dmod_ra, sl_alt_ta, sl_alt_ra;


    // TASK: remove singleton code! Have Avionics create instance of TCAS
    private static TCAS single_instance;


    public static TCAS get_instance() {
        if (TCAS.single_instance == null) {
            TCAS.single_instance = new TCAS();
        }
        return TCAS.single_instance;
    }


    private TCAS() {

        new_data_start(0, 0, 0.0f, 0.0f, 0.0f, 0.0f);

        last_time = System.currentTimeMillis();

    }


    /**
     * prepare for a new set of multiplayer aircraft data
     */
    public void new_data_start(int max, int act, float agl_alt, float lat, float lon, float msl_alt) {

//        logger.warning("new TCAS");
//        logger.warning("max="+max);
//        logger.warning("act="+act);
//        logger.warning("agl="+agl_alt);
//        logger.warning("lat="+lat);
//        logger.warning("lon="+agl_alt);
//        logger.warning("msl="+msl_alt);
        
        total = max;
        active = act;

        my_agl = agl_alt;

        my_lat = lat;
        my_lon = lon;
        my_msl = msl_alt;

        // elapsed time since last data set
        last_time = system_time;
        system_time = System.currentTimeMillis();
        delta_time = (float)(system_time - last_time) / 1000.0f;

        // sensitivity levels
        if ( my_agl < 1000.0f ) {
            // SL2
            sl_tau_ta = 20.0f;
            sl_tau_ra = 10.0f; // in reality N/A
            sl_dmod_ta = 0.30f;
            sl_dmod_ra = 0.15f; // in reality N/A
            sl_alt_ta = 850.0f;
            sl_alt_ra = 200.0f; // in reality N/A
        } else if ( my_agl < 2350.0f ) {
            // SL3
            sl_tau_ta = 25.0f;
            sl_tau_ra = 15.0f;
            sl_dmod_ta = 0.33f;
            sl_dmod_ra = 0.20f;
            sl_alt_ta = 850.0f;
            sl_alt_ra = 300.0f;
        } else if ( my_msl < 5000.0f ) {
            // SL4
            sl_tau_ta = 30.0f;
            sl_tau_ra = 20.0f;
            sl_dmod_ta = 0.48f;
            sl_dmod_ra = 0.35f;
            sl_alt_ta = 850.0f;
            sl_alt_ra = 300.0f;
        } else if ( my_msl < 10000.0f ) {
            // SL5
            sl_tau_ta = 40.0f;
            sl_tau_ra = 25.0f;
            sl_dmod_ta = 0.75f;
            sl_dmod_ra = 0.55f;
            sl_alt_ta = 850.0f;
            sl_alt_ra = 350.0f;
        } else if ( my_msl < 20000.0f ) {
            // SL6
            sl_tau_ta = 45.0f;
            sl_tau_ra = 30.0f;
            sl_dmod_ta = 1.00f;
            sl_dmod_ra = 0.80f;
            sl_alt_ta = 850.0f;
            sl_alt_ra = 400.0f;
        } else if ( my_msl < 42000.0f ) {
            // SL7
            sl_tau_ta = 48.0f;
            sl_tau_ra = 35.0f;
            sl_dmod_ta = 1.30f;
            sl_dmod_ra = 1.10f;
            sl_alt_ta = 850.0f;
            sl_alt_ra = 600.0f;
        } else {
            // SL7
            sl_tau_ta = 48.0f;
            sl_tau_ra = 35.0f;
            sl_dmod_ta = 1.30f;
            sl_dmod_ra = 1.10f;
            sl_alt_ta = 1200.0f;
            sl_alt_ra = 700.0f;
        }

        // clear the global ra and ta flags
        ra = false;
        ta = false;

    }



    /**
     * Update an entry - tcas data packet
     */
    public void mp_update(int i, float mp_lat, float mp_lon, float mp_msl) {

        if ( /*( i < active ) ||*/ ( mp_lat != lat[i] ) || ( mp_lon != lon[i] ) || ( mp_msl != elev[i] ) ) {
            // if this MP aircraft is one of the active aircraft, draw it
            // if it is not one of the active aircraft, draw it only when lat/lon/alt data is different from previous iteration
            // nope, active seems always to be equal to total

            float dist = (float)Math.hypot(
                        (double)(mp_lat - my_lat),
                        (double)(mp_lon - my_lon) * Math.cos( (double)(mp_lon + my_lon) / 2.0 ) )
                    * 60.0f; // degrees to NM

            // horizontal closure rate and time-to-go to CPA (closest point of approach)
            float closure_rate_h = ( dist_nm[i] - dist ) / delta_time;
            float tau_h = dist / closure_rate_h;

            float alt_diff = (mp_msl - my_msl );
            int alt_diff_100 = Math.round( alt_diff / 100.0f ); // rounded to hundreds of feet

            // vertital closure rate and time to-to-go CPA
            float closure_rate_v = ( rel_alt_ft[i] - alt_diff ) / delta_time;
            float tau_v = alt_diff / closure_rate_v;

            // calculate vv and decide if the up or down arrow should be displayed
            float fpm = 60.0f * ( mp_msl - elev[i] ) / delta_time;
            climbing[i] = ( fpm >= 500.0f );
            descending[i] = ( fpm <= -500.0f );

            float delta_alt = Math.abs(alt_diff);

            if ( ( dist < sl_dmod_ra ) && ( delta_alt < sl_alt_ra ) ) {
                // Position Resolution Advisory (well, not really a TCAS-RA, just a Red Alert)
                alarm[i] = RA;
            } else if ( ( tau_h < sl_tau_ra ) && ( tau_h > 0.0f ) && ( tau_v < sl_tau_ra ) && ( tau_v > 0.0f ) ) {
                // TAU Resolution Advisory (well not really a TCAS-RA, just a Red Alert)
                // try to filter out false alerts by requiring 2 consecutive TAU triggers
                if ( tau_ra[i] ) {
                    alarm[i] = RA;
                } else {
                    tau_ra[i] = true;
                }
            } else {
                tau_ra[i] = false;
                if ( ( dist < sl_dmod_ta ) && ( delta_alt < sl_alt_ta ) ) {
                    // Taffic Alert
                    alarm[i] = TA;
                } else if ( ( tau_h < sl_tau_ta ) && ( tau_h > 0.0f ) && ( tau_v < sl_tau_ta ) && ( tau_v > 0.0f ) ) {
                    // Taffic Alert
                    if ( tau_ta[i] ) {
                        alarm[i] = TA;
                    } else {
                        tau_ta[i] = true;
                    }
                } else {
                    tau_ta[i] = false;
                    if ( ( dist < 6.0f ) && ( delta_alt < 1200.0f ) ) {
                        // Proximate Traffic
                        alarm[i] = PROX;
                    } else if ( ( dist < 40.0f ) && ( delta_alt < 2700.0f ) ) {
                        // Other Traffic
                        alarm[i] = OTHER;
                    } else {
                        // Not shown on a real TCAS
                        alarm[i] = FARAWAY;
                    }
                }
            }

            // in reality, RA is inhibited below 1000ft AGL,
            // but we disable the "TRAFFIC" message for TA and RA
            if ( ( my_agl >= 1000.0f ) && ( alarm[i] == RA ) ) {
                // raise the global RA
                ra = true;
            }
            if ( ( my_agl >= 1000.0f ) && ( alarm[i] == TA ) ) {
                // raise the global TA
                ta = true;
            }

            // store the new values for comparison on the next iteration
            lat[i] = mp_lat;
            lon[i] = mp_lon;
            elev[i] = mp_msl;
            dist_nm[i] = dist;
            rel_alt_ft[i] = alt_diff;
            rel_alt_100[i] = alt_diff_100;

        } else {
            // the tcas data has not changed at all since last iteration
            alarm[i] = NONE;
        }

    }

}
