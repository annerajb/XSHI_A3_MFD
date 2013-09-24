/**
* UIHeartbeat.java
* 
* Periodically calls the hearbeat method of NDComponent to trigger periodic
* tasks.
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
package net.sourceforge.xhsi.flightdeck;

import net.sourceforge.xhsi.conwin.ConWinComponent;
import net.sourceforge.xhsi.flightdeck.pfd.PFDComponent;
import net.sourceforge.xhsi.flightdeck.nd.NDComponent;
import net.sourceforge.xhsi.flightdeck.eicas.EICASComponent;
import net.sourceforge.xhsi.flightdeck.mfd.MFDComponent;
import java.util.logging.Logger;

import net.sourceforge.xhsi.StoppableThread;

public class UIHeartbeat extends StoppableThread {
	
	private int watch_interval;
	private ConWinComponent conwin;
	private PFDComponent pfd;
	private NDComponent nd;
	private EICASComponent eicas;
	private MFDComponent mfd;
	
	private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");
	
	/**
	 * 
	 * @param watch_interval	time in ms between invocations of this watchdog
	 */
	public UIHeartbeat(ConWinComponent conwin, PFDComponent pfd, NDComponent nd, EICASComponent eicas, MFDComponent mfd, int watch_interval) {
		this.watch_interval = watch_interval;
		this.conwin = conwin;
                this.pfd = pfd;
                this.nd = nd;
                this.eicas = eicas;
                this.mfd = mfd;
		this.keep_running = true;
	}
	
	public void run() {
		while (this.keep_running) {
			try {
				Thread.sleep(this.watch_interval);
				this.conwin.heartbeat();
				this.pfd.heartbeat();
				this.nd.heartbeat();
				this.eicas.heartbeat();
				this.mfd.heartbeat();
			} catch (Exception e) {
				this.keep_running = false;
				logger.warning("Caught exception in Watchdog! Stopping. (" + e.toString());
			}
		}
		logger.fine("UI-Heartbeat stopped");
	}
}
