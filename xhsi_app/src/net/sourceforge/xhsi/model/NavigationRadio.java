/**
* NavigationRadio.java
* 
* Facade for all flight simulator variables and aggregated information that 
* belongs to a navigation radio, like e. g. frequency, distance to tuned
* navigation object or reception flag.
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

import java.util.logging.Logger;

//import net.sourceforge.xhsi.model.xplane.XPlaneSimDataRepository; // TODO : create an Interface class SimDataRepository


public class NavigationRadio {

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    public static final int RADIO_TYPE_NAV = 0;
    public static final int RADIO_TYPE_ADF = 1;
    public static final int RADIO_TYPE_GPS = 9;

    public static final int VOR_RECEPTION_OFF = 0;
    public static final int VOR_RECEPTION_TO = 1;
    public static final int VOR_RECEPTION_FROM = 2;

    /**
     * number of bank. either 1 or 2
     */
    private int bank;

    /**
     * type of radio - must be a RADIO_TYPE constant
     */
    private int type;

    /**
     * frequency tuned into the radio
     */
    private float frequency;

    /**
     * the navaid's id
     */
    private String nav_id;

    private int sim_data_id_freq;
    private int sim_data_id_nav_id;
    private int sim_data_id_deflection; // relative bearing
    private int sim_data_id_dme_distance;
    private int sim_data_id_dme_time;
    private int sim_data_id_obs; // OBS
    private int sim_data_id_course; // OBS or the Localizer's frontcourse
    private int sim_data_id_fromto;
    private int sim_data_id_cdi_dots; // deviation in dots
    private int sim_data_id_gs_active;
    private int sim_data_id_gs_dots;

    // links to single_instances
    //private XPlaneSimDataRepository sim_data_repository;
    private SimDataRepository sim_data_repository;
    private Aircraft aircraft;
    private NavigationObjectRepository navobj_repository;
    public Avionics avionics;
    // link to the radionavaid that we are tuned to
    private RadioNavigationObject rnav_object;


    // GPS -------------------------------------------------------------------
    public NavigationRadio(
            int radio_type,
            int id_deflection,
            int id_dme_distance,
            int id_dme_time,
            int id_course,
            int id_fromto,
            int id_cdi_dots,
            int id_gs_dots,
            ModelFactory sim_model,
            Avionics avionics) {
        this.type = radio_type;
        this.frequency = 0;
        this.nav_id = "";
        this.sim_data_id_deflection = id_deflection;
        this.sim_data_id_dme_distance = id_dme_distance;
        this.sim_data_id_dme_time = id_dme_time;
        this.sim_data_id_course = id_course;
        this.sim_data_id_fromto = id_fromto;
        this.sim_data_id_cdi_dots = id_cdi_dots;
        this.sim_data_id_gs_dots = id_gs_dots;

        this.sim_data_repository = sim_model.get_repository_instance(); // TODO: a reference to the flightsim.xplane package should not occur from within flightsim!
        this.navobj_repository = NavigationObjectRepository.get_instance();
        this.avionics = avionics;
        this.aircraft = this.avionics.get_aircraft();
    }

    // VOR, VOR-DME, LOC, ILS, IGS or DME ------------------------------------
    public NavigationRadio(
            int bank,
            int radio_type,
            int id_freq,
            int id_nav_id,
            int id_deflection,
            int id_dme_distance,
            int id_dme_time,
            int id_obs,
            int id_course,
            int id_fromto,
            int id_cdi_dots,
            int id_gs_active,
            int id_gs_dots,
            ModelFactory sim_model,
            Avionics avionics) {
        this.bank = bank;
        this.type = radio_type;
        this.frequency = 0;
        this.nav_id = "";
        this.sim_data_id_freq = id_freq;
        this.sim_data_id_nav_id = id_nav_id;
        this.sim_data_id_deflection = id_deflection;
        this.sim_data_id_dme_distance = id_dme_distance;
        this.sim_data_id_dme_time = id_dme_time;
        this.sim_data_id_obs = id_obs;
        this.sim_data_id_course = id_course;
        this.sim_data_id_fromto = id_fromto;
        this.sim_data_id_cdi_dots = id_cdi_dots;
        this.sim_data_id_gs_active = id_gs_active;
        this.sim_data_id_gs_dots = id_gs_dots;

        this.sim_data_repository = sim_model.get_repository_instance(); // TODO: a reference to the flightsim.xplane package should not occur from within flightsim!
        this.navobj_repository = NavigationObjectRepository.get_instance();
        this.avionics = avionics;
        this.aircraft = this.avionics.get_aircraft();
    }

    // ADF -------------------------------------------------------------------
    public NavigationRadio(
            int bank,
            int radio_type,
            int id_freq,
            int id_nav_id,
            int id_deflection,
            int id_dme_distance,
            ModelFactory sim_model,
            Avionics avionics) {
        this.bank = bank;
        this.type = radio_type;
        this.frequency = 0;
        this.nav_id = "";
        this.sim_data_id_freq = id_freq;
        this.sim_data_id_nav_id = id_nav_id;
        this.sim_data_id_deflection = id_deflection;
        this.sim_data_id_dme_distance = id_dme_distance;
        this.sim_data_id_obs = -1;
        this.sim_data_id_course = -1;
        this.sim_data_id_fromto = -1;
        this.sim_data_id_cdi_dots = -1;
        this.sim_data_id_gs_active = -1;
        this.sim_data_id_gs_dots = -1;

        this.sim_data_repository = sim_model.get_repository_instance(); // TODO: a reference to the flightsim.xplane package should not occur from within flightsim!
        this.navobj_repository = NavigationObjectRepository.get_instance();
        this.avionics = avionics;
        this.aircraft = this.avionics.get_aircraft();
    }


    // -----------------------------------------------------------------------
    public boolean receiving() {

        update_radio_data();
//        if (this.rnav_object instanceof Localizer) {
//            // generic: we have to be in range, and within +/- 30 degrees of the Localizer axis
//            // XPlane : the TO/FROM flag should be TO
//            return ( (this.rnav_object != null) && (this.sim_data_repository.get_sim_float(this.sim_data_id_fromto) == NavigationRadio.VOR_RECEPTION_TO) );
//        } else if (this.type == RADIO_TYPE_NAV) {
//            // NAV excluding Localizer
//            // generic: we have to be in range, and not directly over the navaid
//            // XPlane : the TO/FROM flag should not be OFF
//            return ( (this.rnav_object != null)
//                    && ( (this.sim_data_repository.get_sim_float(this.sim_data_id_fromto) != NavigationRadio.VOR_RECEPTION_OFF)
//                        || (get_distance() > 0.0f)
//                    )
//                );
//        } else {
//            // ADF
//            // XPlane : when there is no reception, the RMI goes to 90
//            // generic: we have to be in range, and that is all
//            return ( (this.rnav_object != null) && (get_rel_bearing() != 90.0f) );
//        }
        // can it be so simple with X-Plane?
        // reception OK when we have a NAV_ID...
        return ! nav_id.equals("");

    }


    public RadioNavigationObject get_radio_nav_object() {
        update_radio_data();
        return this.rnav_object;
    }


    public float get_fromto() {
        // generic: when out of range: OFF, otherwise calculate the difference between radial selected with OBS and the bearing of the aircraft, and set TO/FROM accordingly
        // XPlane : just return what we get from the sim
        return this.sim_data_repository.get_sim_float(this.sim_data_id_fromto);
    }


    public float get_rel_bearing() {
        // XPlane : just return what we get from the sim
        return this.sim_data_repository.get_sim_float(this.sim_data_id_deflection);
    }


    public float get_frequency() {
        update_radio_data();
        return this.frequency;
    }


    public String get_nav_id() {
        update_radio_data();
        return this.nav_id;
    }


    public float get_radial() {
        // calculate
        //RadioNavigationObject beacon = get_radio_nav_object();
        float radial = this.aircraft.heading() + this.sim_data_repository.get_sim_float(this.sim_data_id_deflection) + 180.0f;
        radial %= 360.0f;
        return radial;
    }


    public float get_distance() {
        // XPlane : just return what we get from the sim
        return this.sim_data_repository.get_sim_float(this.sim_data_id_dme_distance);
    }


    public float get_ete() {
        // XPlane : just return what we get from the sim
        return this.sim_data_repository.get_sim_float(this.sim_data_id_dme_time);
    }


    public int get_bank() {
        return this.bank;
    }


    public boolean freq_is_nav() {
        update_radio_data();
        return (this.type == RADIO_TYPE_NAV);
    }


    public boolean freq_is_localizer() {
        update_radio_data();
        return ((this.type == RADIO_TYPE_NAV) && (this.rnav_object != null) && (this.rnav_object instanceof Localizer));
    }


    public boolean freq_is_adf() {
        update_radio_data();
        return (this.type == RADIO_TYPE_ADF);
    }


    // -----------------------------------------------------------------------
    private void update_radio_data() {
        float current_freq = this.sim_data_repository.get_sim_float(this.sim_data_id_freq);
        String current_nav_id = this.sim_data_repository.get_sim_string(this.sim_data_id_nav_id);
        if (current_freq > 1000.0f) {
            current_freq = (current_freq/100.0f);
        }
        // if the frequency has changed, or we were not tuned to any radionavaid, or we got out of (multiplied) range; then search for a radionavaid again...
        if ( (this.frequency != current_freq)
                || ( ! this.nav_id.equals(current_nav_id) )
                || ( this.rnav_object == null )
                || ( this.aircraft.rough_distance_to(this.rnav_object) > this.rnav_object.range * NavigationObjectRepository.RANGE_MULTIPLIER ) ) {
            this.frequency = current_freq;
            this.nav_id = current_nav_id;
            this.rnav_object = this.navobj_repository.find_tuned_nav_object(this.aircraft.lat(), this.aircraft.lon(), current_freq, current_nav_id);
        }
    }


}
