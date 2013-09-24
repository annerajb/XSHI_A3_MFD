/**
* XPlaneDataPacketDecoder.java
* 
* Decodes the data contained in received data packets. Flight simulator data
* and FMS routes are extracted from received data packets and sent to the
* XPlaneSimDataRepository and the FMS respectively. XPlaneDataPacketDecoder
* also calls the tick_updates method of XPlaneSimDataRepository, which in turn
* triggers repainting of the UI.
* 
* Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
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
package net.sourceforge.xhsi.model.xplane;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.logging.Logger;

//import net.sourceforge.xhsi.XHSISettings;
import net.sourceforge.xhsi.model.CoordinateSystem;
//import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.FMS;
import net.sourceforge.xhsi.model.FMSEntry;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.SimDataRepository;
import net.sourceforge.xhsi.model.TCAS;


public class XPlaneDataPacketDecoder implements XPlaneDataPacketObserver {

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    private boolean received_adc_packet = false;
    private boolean received_fms_packet = false;
    private boolean received_tcas_packet = false;

    // list of sim data id's that need the anti-jitter filter
    private int[] jitter_id = { XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_MAGPSI,
        XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_HPATH,
        XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_LATITUDE,
        XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_LONGITUDE };
    // ... and the same number of vars to store previous value and delta
    private float[] last_value = { 0.0f, 0.0f, 0.0f, 0.0f };
    private float[] last_delta = { 0.0f, 0.0f, 0.0f, 0.0f };

    SimDataRepository xplane_data_repository = null;
    FMS fms = FMS.get_instance();
    TCAS tcas = TCAS.get_instance();

    public boolean beyond_active;
    public float last_lat;
    public float last_lon;
    public float total_ete;
    public FMSEntry prev_fms_entry;
    public boolean prev_level;
    public boolean prev_climbing;
    public boolean prev_descending;
    

    public XPlaneDataPacketDecoder(ModelFactory sim_model) {
        this.xplane_data_repository = sim_model.get_repository_instance();
    }


    private float anti_jitter( int id, float value ) {

        // somewhere between the encoding in the plugin and the decoding here,
        // the latitude, longitude and others make big jumps, causing the map to shake
        // this function tries to filter those out

        float new_value = value;

        int i;
        for ( i=0; i<this.jitter_id.length; i++ ) {

            if ( id == this.jitter_id[i] ) {
                float new_delta = Math.abs(value - this.last_value[i]);
                if (  new_delta > this.last_delta[i] * 5.0f ) {
                    // delta suddenly bigger; keep the old value
                    new_value = this.last_value[i];
                }
                this.last_delta[i] = new_delta;
                this.last_value[i] = value;
            }

        }

        return new_value;

    }


    public void new_sim_data( byte[] sim_data ) throws Exception {

        // these vars will be re-used several times, so define them here and not in a for-loop
        int data_point_id;
        // int int_data;
        float float_data;
        String string_data;

        // identify the packet type (identified by the first four bytes)
        String packet_type = new String(sim_data, 0, 4).trim();

        if ( packet_type.equals("ADCD")
                || packet_type.equals("AVIO")
                || packet_type.equals("ENGI")
                || packet_type.equals("STAT") ) {

            // Air Data Computer or Avionics or Engines or Static data packet

            if (this.received_adc_packet == false)
                logger.fine("Received first sim packet");
            logger.finest("Receiving sim packet");

            DataInputStream data_stream = new DataInputStream(new ByteArrayInputStream(sim_data));
            data_stream.skipBytes(4);    // skip the bytes containing the packet type id
            int nb_of_data_points = data_stream.readInt();

            for (int i=0; i<nb_of_data_points; i++) {
                data_point_id = data_stream.readInt();
                if ( data_point_id >= 10000 ) {
                    // a string of 4 bytes
                    string_data = new String(sim_data, 8+(i*8)+4, 4).trim();
                    data_stream.skipBytes(4);
                    this.xplane_data_repository.store_sim_string(data_point_id, string_data);
//                } else if ( data_point_id >= 5000 ) {
//                    // Int
//                    int_data = data_stream.readInt();
//                    //logger.warning("INT:"+data_point_id+"="+int_data);
//                    this.xplane_data_repository.store_sim_int(data_point_id, int_data);
                } else {
                    // Float
                    float_data = anti_jitter(data_point_id, data_stream.readFloat());
                    this.xplane_data_repository.store_sim_float(data_point_id, float_data);
                    logger.finest("ID:"+data_point_id+"="+float_data);
                }
            }
//this.xplane_data_repository.store_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_OVERFLOW_ACF_NUM_TANKS, 3.0f);
//this.xplane_data_repository.store_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_FUEL_QUANTITY_ + 0, 2000.0f);
//this.xplane_data_repository.store_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_FUEL_QUANTITY_ + 1, 1000.0f);
//this.xplane_data_repository.store_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_FUEL_QUANTITY_ + 2, 3000.0f);
//this.xplane_data_repository.store_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_OVERFLOW_ACF_TANK_RATIO_ + 0, 0.45f);
//this.xplane_data_repository.store_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_OVERFLOW_ACF_TANK_RATIO_ + 1, 0.10f);
//this.xplane_data_repository.store_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_OVERFLOW_ACF_TANK_RATIO_ + 2, 0.45f);
//this.xplane_data_repository.store_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_WEIGHT_M_FUEL_TOTAL, 6000.0f);

            // logger.warning("" + this.xplane_data_repository.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_LATITUDE) + ";" + this.xplane_data_repository.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_LONGITUDE));

            if ( (this.received_adc_packet == false) && packet_type.equals("ADCD") ) {
                logger.warning("Receiving from XHSI_plugin version " + decode_plugin_version(this.xplane_data_repository.get_sim_float(XPlaneSimDataRepository.PLUGIN_VERSION_ID)));
                logger.fine("... ADCD packet contains " + nb_of_data_points + " sim data values");
                this.received_adc_packet = true;
            }

            if ( packet_type.equals("ADCD") ) {
                logger.fine("Ticking updates");
                this.xplane_data_repository.tick_updates();
                logger.fine("Updates ticked");
            }

            
        } else if ( packet_type.startsWith("FMC") ) {

            // 1 out of 10 FMCx route data packets
            
            int offset = Character.digit( packet_type.charAt(3), 10 ) * 50;
//            if ( packet_type.equals("FMSR") ) offset = 0;

            if (this.received_fms_packet == false)
                logger.fine("Received first FMCx packet");
            logger.finest("Receiving " + packet_type);

            DataInputStream data_stream = new DataInputStream(new ByteArrayInputStream(sim_data));
            data_stream.skipBytes(4);    // skip the bytes containing the packet type id

            if ( offset == 0 ) {
// No, we will re-use the existing FMS instance
//                this.fms.init();
                beyond_active = false;
                last_lat = 0.0f;
                last_lon = 0.0f;
                total_ete = 0.0f;
                prev_fms_entry = null;
                prev_level = false;
                prev_climbing = false;
                prev_descending = false;
            }
            
            float ete_for_active = data_stream.readFloat(); // min
            float groundspeed = data_stream.readFloat() * 1.9438445f; // kts
//float ete_for_active = 9.0f; // min
//float groundspeed = 100.0f; // kts

            int nb_of_entries = data_stream.readInt();

            if (this.received_fms_packet == false)
                logger.fine("... FMCx contains " + nb_of_entries + " FMS entries");
            logger.finest("... FMC" + packet_type.charAt(3) + " contains " + nb_of_entries + " FMS entries");

            int displayed_entry_index = data_stream.readInt();
            int active_entry_index = data_stream.readInt();

            int type;
            int altitude;
            float lat;
            float lon;
            boolean is_displayed;
            boolean is_active;
            float leg_dist;

            FMSEntry new_fms_entry;
            boolean level = false;
            boolean climbing = false;
            boolean descending = false;

            int packet_entries = ( nb_of_entries - offset > 50 ) ? 50 : nb_of_entries - offset;
//logger.warning("... we will read " + packet_entries + " FMS entries");

            for (int i=0; i<packet_entries; i++) {

                type = data_stream.readInt();
                String id = new String(sim_data, 24+(i*24)+4, 8).trim();
                // packet_char4 + ete_float + groundspeed_float + nb_int + displayed_int + active_int + ( i * ( type_int + id_char8 + alt_float + lat_float + lon_float) ) + type_int
                data_stream.skipBytes(8);
                altitude = data_stream.readInt();
                lat = data_stream.readFloat();
                lon = data_stream.readFloat();
                
//                // do not append empty entries (should not happen anyway)
//                if ((lat != 0.0f) || (lon != 0.0f)) {

                    is_displayed = ( offset+i == displayed_entry_index );
                    is_active = ( offset+i == active_entry_index );

                    // leg distance
                    if ( offset+i == 0 ) {
                        leg_dist = 0;
                    } else {
                        leg_dist = CoordinateSystem.rough_distance(lat, lon, last_lat, last_lon);
                        last_lat = lat;
                        last_lon = lon;
                    }
                    
                    // estimate ete only when we have some speed
                    if ( groundspeed > 33.33f ) {
                        if ( is_active ) {
                            total_ete = ete_for_active;
                        } else if ( beyond_active ) {
                            total_ete += leg_dist / groundspeed * 60.0f;
                        } else {
                            total_ete = 0.0f;
                        }
                    } else {
                        total_ete = 0.0f;
                    }

                    logger.finest("FMC [" + (offset+i) + "] : " + id + " leg=" + leg_dist);

                    //new_fms_entry = new FMSEntry(offset + i, id, type, lat, lon, altitude, leg_dist, total_ete, is_active, is_displayed);
                    // No, we will re-use the existing FMSEntry[offset + i]
                    new_fms_entry = this.fms.get_entry(offset + i);

                    new_fms_entry.index = offset + i;
                    new_fms_entry.name = id;
                    new_fms_entry.type = type;
                    new_fms_entry.lat = lat;
                    new_fms_entry.lon = lon;
                    new_fms_entry.altitude = altitude;
                    new_fms_entry.leg_dist = leg_dist;
                    new_fms_entry.total_ete = total_ete;
                    new_fms_entry.active = is_active;
                    new_fms_entry.displayed = is_displayed;
                    this.fms.update_entry( offset + i );

                    //this.fms.add_entry(new_fms_entry, offset + i);

                    // was this the active waypoint?
                    if ( is_active ) {
                        beyond_active = true;
                    }
                    
                    // climbing, descending or level flight?
                    climbing = false;
                    descending = false;
                    level = false;
                    if ( prev_fms_entry != null ) {
                        if ( ( new_fms_entry.altitude > prev_fms_entry.altitude ) && ( prev_fms_entry.altitude != 0 ) ) {
                            climbing = true;
                        } else if ( ( new_fms_entry.altitude < prev_fms_entry.altitude ) && ( new_fms_entry.altitude != 0 ) ) {
                            descending = true;
                        } else {
                            level = true;
                        }
                    }

                    // a Lat/Lon waypoint climbing or descending through 10000ft?
                    if ( ( new_fms_entry.type == 2048 ) && ( new_fms_entry.altitude == 10000 ) ) {
                        if ( climbing ) {
                            new_fms_entry.name = "ACCEL";
                        } else if ( descending ) {
                            new_fms_entry.name = "DECEL";
                        }
                    }

                    // now can we know if the previous Lat/Lon waypoint was a T/C, T/D, E/D or S/C
                    if ( ( prev_fms_entry != null ) && ( prev_fms_entry.type == 2048 ) ) {
                        if ( prev_climbing && level ) {
                            prev_fms_entry.name = "T/C";
                        } else if ( prev_level && descending ) {
                            prev_fms_entry.name = "T/D";
                        } else if ( prev_descending && level ) {
                            prev_fms_entry.name = "E/D";
                        } else if ( prev_level && climbing ) {
                            prev_fms_entry.name = "S/C";
                        }
                    }

                    prev_fms_entry = new_fms_entry;
                    prev_level = level;
                    prev_descending = descending;
                    prev_climbing = climbing;

//                }

            }
            
            // all entries received, now set the count
            if ( offset + 50 >= nb_of_entries ) {
                this.fms.set_count(nb_of_entries);
            }

            this.received_fms_packet = true;
            
        } else if (packet_type.equals("MPAC")) {

            // multi-player aircraft data packet

            if (this.received_tcas_packet == false)
                logger.fine("Received first MPAC packet");
            logger.finest("Receiving MPAC packet");

            DataInputStream data_stream = new DataInputStream(new ByteArrayInputStream(sim_data));
            data_stream.skipBytes(4);    // skip the bytes containing the packet type id

            // maximum number of MP planes
            int mp_total = data_stream.readInt();
            // active number of MP planes
            int mp_active = data_stream.readInt();
            //logger.fine("MP total / active: " + mp_total + " / " + mp_active);
            // pfff... active seems always to be equal to total
            
            // precaution
            mp_total = Math.min(mp_total, TCAS.MAX_ENTRIES);
            mp_active = Math.min(mp_active, TCAS.MAX_ENTRIES);

            // then 4 floats with our own radar altitude, lat, lon and msl altitude
            this.tcas.new_data_start( mp_total, mp_active, data_stream.readFloat(), data_stream.readFloat(), data_stream.readFloat(),data_stream.readFloat() );

            if ( mp_total > 1 ) {
                for (int i = 1; i < mp_total; i++) {
                    this.tcas.mp_update(
                            i,
                            data_stream.readFloat(), // lat
                            data_stream.readFloat(), // lon
                            data_stream.readFloat() // msl alt
                            );
                }
            }

            this.received_tcas_packet = true;

        }

        // no, only for sim data packets
        //this.xplane_data_repository.tick_updates();

    }


    private String decode_plugin_version(float plugin_version) {

        logger.config("Plugin version " + plugin_version);

        if (plugin_version == 0.0f) {
            return "1.0 Beta ?";
        } else {
            String pv = "" + (int) plugin_version; // example: 1.0 Beta 8 is "10008"
            String pv_displayed = pv.substring(0, 1) + "." + pv.substring(1, 2); // "major.minor" (example: "1.0")
            if (pv.substring(2, 3).equals("0") == false)
                pv_displayed += "." + pv.substring(2, 3); // "major.minor.bugfix" if bugfix!=0
            if (pv.substring(3, 5).equals("00") == false)
                if (pv.substring(3, 4).equals("9"))
                    pv_displayed += " RC" + Integer.valueOf(pv.substring(4, 5)); // "major.minor[.bugfix] RCx" if xy>=90
                else
                    pv_displayed += " Beta " + Integer.valueOf(pv.substring(3, 5)); // "major.minor[.bugfix] Beta xx" if xx!=00
            return pv_displayed;
        }

    }


}
