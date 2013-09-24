/**
 * ProgressDialog.java
 * 
 * Displays a progress bar if progress <> 100%. Is notified by classes through
 * the ProgressObserver interface.
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

package net.sourceforge.xhsi;


import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;


public class ProgressDialog extends JDialog implements ProgressObserver {

    private static final long serialVersionUID = 1L;

    private static final int WIDTH = 300;
    private static final int HEIGHT = 20;

    private JProgressBar progress_bar;
    private Graphics g;


    public ProgressDialog(JFrame owner_frame) {
        super(owner_frame, "XHSI starting...");

        this.setResizable(false);
        this.setUndecorated(true);
        
        this.g = null;

        this.progress_bar = new JProgressBar(0, 100);
        this.progress_bar.setStringPainted(true);
        this.progress_bar.setPreferredSize(new Dimension(ProgressDialog.WIDTH, ProgressDialog.HEIGHT));
        this.progress_bar.setIndeterminate(false);

        Container content_pane = this.getContentPane();
        content_pane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 10));

        content_pane.add(new JLabel(new ImageIcon(getClass().getResource("XHSI_logo64.png"))));
        content_pane.add(this.progress_bar);

        this.setLocation( owner_frame.getX() + owner_frame.getWidth()/2 - ProgressDialog.WIDTH/2 - 32, owner_frame.getY() + owner_frame.getHeight() );

        pack();
    }


    public void set_progress(String title, String task, float percent_complete) {
        if ( (percent_complete != 100.0f) && ( ! isVisible() ) ) {
            setVisible(true);
        }

        this.setTitle(title);
        this.progress_bar.setValue((int) percent_complete);
        this.progress_bar.setString(task);
        if (this.g != null)
            this.progress_bar.paint(this.g);

        if (percent_complete == 100.0f) {
            setVisible(false);
        }
    }


    public void paintAll(Graphics g) {
        this.g = g;
        super.paintAll(g);
    }

}
