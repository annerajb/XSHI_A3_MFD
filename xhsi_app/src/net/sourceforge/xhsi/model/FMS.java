/**
* FMS.java
* 
* Model class for a flight management system (FMS)
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
package net.sourceforge.xhsi.model;

import java.util.ArrayList;
import java.util.HashMap;
//import java.util.logging.Logger;


public class FMS {
    
//    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    // TASK: remove singleton code! Have Avionics create instance of FMS
    private static FMS single_instance;

    private FMSEntry active_waypoint = null;
    private FMSEntry displayed_waypoint = null;
//    private ArrayList<FMSEntry> entries;
    private FMSEntry entries[];
//    private HashMap<String,FMSEntry> entry_lookup;
    private int count;
    
//    private boolean active;

    public static FMS get_instance() {
        if (FMS.single_instance == null) {
            FMS.single_instance = new FMS();
        }
        return FMS.single_instance;
    }


    private FMS() {

//        this.entries = new ArrayList<FMSEntry>();
//        this.entry_lookup = new HashMap<String,FMSEntry>();
//        this.active = false;

        init();

    }


    /**
     * @return boolean - true if FMS contains entries, false, otherwise
     */
    public boolean is_active() {
//        return (this.entries.isEmpty() == false);
        return (this.count > 0 );
    }


    /**
     * Prepare for loading new FMS entries
     */
    public void init() {

        this.entries = new FMSEntry[500];
        for ( int i = 0; i < 500; i++ ) {
            this.entries[ i ] = new FMSEntry();
        }

        this.active_waypoint = null;
        this.displayed_waypoint = null;
        this.count = 0;
//        this.entries.clear();
//        this.entry_lookup.clear();
//        this.active = false;
    }


//    /**
//     * Appends entry to the current list of entries
//     *
//     * @param entry - the entry to be appended
//     */
//    public void append_entry(FMSEntry entry) {
//        this.entries.add(entry);
////        this.entry_lookup.put(entry.name, entry);
//        if (entry.active) {
//            this.next_waypoint = entry;
//        }
//        if (entry.displayed) {
//            this.displayed_waypoint = entry;
//        }
//    }

    
    /**
     * Notify that an entry has been modified
     *
     * @param idx - the entry index
     */
    public void update_entry(int idx) {

        if (this.entries[idx].active) {
            this.active_waypoint = this.entries[idx];
        }
        if (this.entries[idx].displayed) {
            this.displayed_waypoint = this.entries[idx];
        }

        // this entry can already be used, even if we don't have the final count
        this.count = Math.max(idx + 1, this.count);

    }


    /**
     * Adds an entry to the current array of entries
     *
     * @param entry - the entry to be added
     * @param idx - the entry index
     */
    public void add_entry(FMSEntry entry, int idx) {
        
//        this.entries.add(entry);
        this.entries[idx] = entry;
//        this.entry_lookup.put(entry.name, entry);
        if (entry.active) {
            this.active_waypoint = entry;
        }
        if (entry.displayed) {
            this.displayed_waypoint = entry;
        }
        
        // this entry can already be used, even if we don't have the final count
        this.count = Math.max(idx + 1, this.count);

    }

    
    /**
     * Sets the number of entries that have been loaded
     *
     * @param n - the number of entries that have been loaded
     */
    public void set_count(int n) {
        this.count = n;
    }

    
    /**
     * @return FMSEntry - the currently selected waypoint in the FMS
     */
    public FMSEntry get_active_waypoint() {
        return this.active_waypoint;
    }

    
    /**
     * @return FMSEntry - the last waypoint in the FMS
     */
    public FMSEntry get_last_waypoint() {
        
//        int n = this.entries.size();
//        if ( n == 0 ) {
//            return null;
//        } else {
//            return this.entries.get( n-1 );
//        }
        if ( this.count == 0 ) {
            return null;
        } else {
            return this.entries[ this.count - 1 ];
        }
        
    }

    
    /**
     * @return FMSEntry - the currently displayed waypoint in the FMS
     */
    public FMSEntry get_displayed_waypoint() {
        return this.displayed_waypoint;
    }

    
    public int get_nb_of_entries() {
//        return this.entries.size();
        return this.count;
    }

    
    public FMSEntry get_entry(int position) {
//        return this.entries.get(position);
        return this.entries[ position ];
    }

    
//    public boolean has_entry(String name) {
//        return this.entry_lookup.containsKey(name);
//    }

    
    public void print_entries() {
        System.out.println("==================================");
        System.out.println("FMS Entries:");
        for (int i=0;i<this.count;i++) {
            System.out.println("#" + i + ": " + ((FMSEntry)this.entries[i]).toString());
        }
    }

    
}
