/**
* XPlaneFlightSessionRecorder.java
* 
* Records the data packets received by XPlaneUDPReceiver and writes them to
* a file. The data in the file can then be replayed with 
* XPlaneFlightSessionPlayer.
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

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;


public class XPlaneFlightSessionRecorder implements XPlaneDataPacketObserver {

    String filename;
    int recording_rate;
    int data_frame_counter;
    long time_at_begin_of_recording;
    ObjectOutputStream oos;
    boolean recording = false;


    public XPlaneFlightSessionRecorder(String filename, int recording_rate) {
        this.filename = filename;
        this.recording_rate = recording_rate;
    }


    public void new_sim_data(byte[] data) throws Exception {
        if (recording) {
            this.data_frame_counter -= 1;
            if (this.data_frame_counter <= 0) {
                System.out.print(".");
                byte[] data_clone = new byte[data.length];
                System.arraycopy(data,0,data_clone,0,data.length);
                this.oos.writeObject(data_clone);
                this.data_frame_counter = this.recording_rate;
            }
        }
    }


    public void start() {
        time_at_begin_of_recording = System.currentTimeMillis();
        try {
            this.data_frame_counter = this.recording_rate;
            this.oos = new ObjectOutputStream(new FileOutputStream(this.filename));
            this.recording = true;
            System.out.println("Recording started");
        } catch (Exception e) {
            System.out.println("Could not start recording! (" + e.toString() + ")");
        }
    }


    public void stop() {
        if (recording) {
            try {
                this.oos.close();
                System.out.println("\nRecording stopped after " + (System.currentTimeMillis() - time_at_begin_of_recording)/1000 + "s");
            } catch (Exception e) {
                System.out.println("Could not stop recording cleanly! (" + e.toString() + ")");
            }
            recording = false;
        }
        System.out.println("Flight session recorder stopped");
    }

}
