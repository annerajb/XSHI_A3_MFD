/*
 * XHSIInstrument.java
 *
 * Definition of each of the instruments
 *
 * Copyright (C) 2010  Marc Rogiers (marrog.123@gmail.com)
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

package net.sourceforge.xhsi;

import java.awt.Component;
import javax.swing.JFrame;


public class XHSIInstrument {

    public static enum DU {

        Empty (EMPTY_ID, "Empty", 160, 160),
        PFD (PFD_ID, "PFD", 320, 320),
        ND (ND_ID, "ND", 320, 320),
        EICAS (EICAS_ID, "EICAS", 320, 320),
        MFD (MFD_ID, "MFD", 320, 320),
        Annunciators (ANNUN_ID, "Annunciators", 160, 160),
        Clock (CLOCK_ID, "Clock", 160, 160);

        private boolean active;
        private final int id;
        private final String name;
        private final int min_width;
        private final int min_height;

        DU(int id, String name, int min_width, int min_height) {
            this.active = true;
            this.id = id;
            this.name = name;
            this.min_width = min_width;
            this.min_height = min_height;
        }

        public void activate(boolean show) {
            this.active = show;
        }

        public boolean enabled() {
            return this.active;
        }

        public int get_id() {
            return this.id;
        }

        public String get_name() {
            return this.name;
        }

        public int get_min_width() {
            return this.min_width;
        }

        public int get_min_height() {
            return this.min_height;
        }

    }


    public DU du;

    public JFrame frame;
    public Component components;


    public static final int EMPTY_ID = 0;
    public static final int PFD_ID = 1;
    public static final int ND_ID = 2;
    public static final int EICAS_ID = 3;
    public static final int MFD_ID = 4;
    public static final int ANNUN_ID = 5;
    public static final int CLOCK_ID = 6;


    public XHSIInstrument(DU du) {

        this.du = du;

    }

    public int get_index() {
        return this.du.get_id();
    }

    public String get_description() {
        return this.du.get_name();
    }

}
