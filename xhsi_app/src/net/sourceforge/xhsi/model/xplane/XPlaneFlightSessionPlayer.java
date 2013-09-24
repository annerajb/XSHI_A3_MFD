/**
* XPlaneFlightSessionPlayer.java
* 
* Plays a flight session from a file recorded by XPlaneFlightSessionRecorder.
* The read data packets are sent to XPlaneDataPacketDecoder which in turn
* updates the UI.
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

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.io.EOFException;

import net.sourceforge.xhsi.StoppableThread;

public class XPlaneFlightSessionPlayer extends StoppableThread {

    String filename;
    ObjectInputStream ois;
    ArrayList sim_data_observers;
    long delay_between_packets;

    public XPlaneFlightSessionPlayer(String filename, long delay_between_packets) {
        this.filename = filename;
        this.sim_data_observers = new ArrayList();
        this.delay_between_packets = delay_between_packets;
        this.keep_running = true;
    }

    public void add_sim_data_observer(XPlaneDataPacketObserver observer) {
        this.sim_data_observers.add(observer);
    }

    public void run() {
        while (this.keep_running) {
            try {
                this.ois = new ObjectInputStream(new FileInputStream(this.filename));

                try {
                    while (this.keep_running) {
                        byte[] sim_data = (byte[])ois.readObject();
                        for (int i=0;i<this.sim_data_observers.size();i++) {
                            ((XPlaneDataPacketObserver)this.sim_data_observers.get(i)).new_sim_data(sim_data);
                        }
                        Thread.sleep(this.delay_between_packets);
                        //System.out.print(".");
                    }
                } catch (EOFException e) {
                    // reached end of recording. ignore
                }
            } catch (Exception e) {
                System.out.print("could not replay flight session (" + e.toString() + "). will stop now ... ");
                keep_running = false;
            } finally {
                if (this.ois != null) {
                    try {
                        this.ois.close();
                    } catch (Exception e) {
                        System.out.println("could not close flight session file (" + e.toString() + "). will stop now ... ");
                    }
                }
            }
        }
        System.out.println("Flight Session player stopped");
    }
}
