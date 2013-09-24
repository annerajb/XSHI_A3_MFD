/**
* XPlaneUDPReceiver.java
* 
* Establishes a datagram socket and receives flight simulator data packages
* send by the XHSI plugin for X-Plane. The received data is forwarded to 
* XPlaneDataPacketDecoder. 
* 
* Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
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
package net.sourceforge.xhsi.model.xplane;

import java.net.*;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.io.*;


import net.sourceforge.xhsi.StoppableThread;

public class XPlaneUDPReceiver extends StoppableThread {

    DatagramSocket datagram_socket;
    byte[] receive_buffer;
    ArrayList reception_observers;
    boolean has_reception;
    boolean sender_known;
    //XPlaneUDPSender udp_sender = null;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");


    public XPlaneUDPReceiver(int listen_port) throws Exception {
        super();
        this.receive_buffer = new byte[5000];
        this.datagram_socket = new DatagramSocket(listen_port);
        this.datagram_socket.setSoTimeout(1000);
        this.reception_observers = new ArrayList();
        this.keep_running = true;
        this.has_reception = true;
        this.sender_known = false;
    }


    public void add_reception_observer(XPlaneDataPacketObserver observer) {
        this.reception_observers.add(observer);
    }


    public DatagramPacket receiveXPlanePacket() throws IOException {
        DatagramPacket packet = new DatagramPacket(receive_buffer, receive_buffer.length);
        datagram_socket.receive(packet);
        logger.fine("Receiving from port " + packet.getPort());
        if ( ! sender_known ) {
            // intercept the sender's (X-Plane's) address and port
            InetAddress orig_address = packet.getAddress();
            int orig_port = packet.getPort();
            XPlaneUDPSender.get_instance().setDestination(datagram_socket, orig_address, orig_port);
            sender_known = true;
        }
        return packet;
    }


    public void run() {
        logger.fine("X-Plane receiver listening on port " + datagram_socket.getLocalPort());
        DatagramPacket packet = null;
        while (this.keep_running) {
            try {
                // wait for packet or time-out
                packet = receiveXPlanePacket();

                if  (this.has_reception == false) {
                    this.has_reception = true;
                    logger.info("UDP reception re-established");
                }

                for (int i=0; i<this.reception_observers.size(); i++) {
                    ((XPlaneDataPacketObserver)this.reception_observers.get(i)).new_sim_data(packet.getData());
                }
            } catch (SocketTimeoutException ste) {
                if (this.has_reception == true) {
                    logger.warning("No UDP reception");
                    this.has_reception = false;
                }
            } catch(IOException ioe) {
                logger.warning("Caught I/O error while waiting for UDP packets! (" + ioe.toString() + ")");
            } catch(Exception e) {
                logger.warning("Caught error while waiting for UDP packets! (" + e.toString() + " / " + e.getMessage() + ")");
            }
        }
        logger.fine("X-Plane receiver stopped");
    }

}