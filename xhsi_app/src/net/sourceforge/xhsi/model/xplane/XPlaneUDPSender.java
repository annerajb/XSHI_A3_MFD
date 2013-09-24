/**
* XPlaneUDPSender.java
* 
* Establishes a datagram socket and send settings data packages
* back to X-Plane.
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
import java.util.logging.Logger;
import java.io.*;


public class XPlaneUDPSender {

    DatagramSocket datagram_socket = null;
    byte[] send_buffer;
    boolean destination_known;
    InetAddress destination_address;
    int destination_port;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");


    public XPlaneUDPSender() {
        this.send_buffer = new byte[500];
        //this.destination_port = port;
        this.destination_known = false;
    }


    private static XPlaneUDPSender single_instance = null;


    public static XPlaneUDPSender get_instance() {
        if (XPlaneUDPSender.single_instance == null) {
            XPlaneUDPSender.single_instance = new XPlaneUDPSender();
        }
        return XPlaneUDPSender.single_instance;
    }


    public void setDestination(DatagramSocket socket, InetAddress address, int port) {

        // the UDP receiver intercepted X-Plane's IP-address and port
        this.destination_known = true;
        this.datagram_socket = socket;
        this.destination_address = address;
        this.destination_port = port;
        logger.warning("Received first packet from port : " + port);

    }


    public void sendDataPoint(int id, float value) {

        if ( this.destination_known ) {

            try {
                ByteArrayOutputStream byte_array_stream = new ByteArrayOutputStream();
                DataOutputStream data_stream = new DataOutputStream(byte_array_stream);
                data_stream.writeInt(1);
                data_stream.writeInt(id);
                data_stream.writeFloat(value);
                logger.finest("data_stream.size = " + data_stream.size());
                byte[] byte_array = byte_array_stream.toByteArray();
                logger.finest("byte_array_stream.size = " + byte_array_stream.size());
                logger.finest("byte_array.length = " + byte_array.length);
                //DatagramPacket packet = new DatagramPacket(byte_array, byte_array_stream.size(), InetAddress.getLocalHost(), 49019);
                DatagramPacket packet = new DatagramPacket(byte_array, byte_array_stream.size(), this.destination_address, this.destination_port);
                // this.datagram_socket = new DatagramSocket(); no, we already have an open socket...
                this.datagram_socket.send(packet);
                logger.info("Datapoint packet sent!");
            } catch (IOException ioe) {
                logger.warning("Caught error while sending a datapoint packet! (" + ioe.toString() + ")");
            }

        } else {
            logger.warning("Cannot send a datapoint packet! (destination unknown)");
        }

    }


}
