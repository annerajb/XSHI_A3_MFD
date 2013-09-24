/**
 * FixDialog.java
 * 
 * Dialog for setting CDU fix
 * 
 * Copyright (C) 2011  Marc Rogiers (marrog.123@gmail.com)
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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
//import javax.swing.JSlider;
import javax.swing.JTextField;


public class FixDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 1L;

    private static final String SHOW = "show";
    private static final String HIDE = "hide";

    private JTextField fix;
    private JTextField radial;
    private JTextField distance;

    private String field_validation_errors = null;

    private XHSISettings xhsi_settings = XHSISettings.get_instance();


    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    public FixDialog(JFrame owner_frame) {
        super(owner_frame, "CDU Fix");

        this.setResizable(false);

        Container content_pane = getContentPane();
        content_pane.setLayout(new BorderLayout());
        content_pane.add(create_fix_panel(), BorderLayout.CENTER);
        content_pane.add(create_dialog_buttons_panel(), BorderLayout.SOUTH);

        init_holding();
        pack();
    }


    private void init_holding() {

        this.fix.setText(xhsi_settings.cdu_fix);
        this.radial.setText("" + xhsi_settings.cdu_fix_radial);
        if (xhsi_settings.cdu_fix_dist == 0.0f) {
            this.radial.setText("");
            this.distance.setText("");
        } else {
            this.radial.setText("" + xhsi_settings.cdu_fix_radial);
            this.distance.setText("" + xhsi_settings.cdu_fix_dist);
        }

    }


    private JPanel create_fix_panel() {
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints cons = new GridBagConstraints();
        JPanel fix_panel = new JPanel(layout);

        int dialog_line = 0;

        cons.ipadx = 10;
        cons.ipady = 0;
        cons.insets = new Insets(5, 10, 0, 0);

        // fix
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        JLabel fix_label = new JLabel("Base fix (ARPT, VOR, NDB, Waypoint)", JLabel.TRAILING);
        fix_panel.add(fix_label, cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.fix = new JTextField(5);
        fix_panel.add(this.fix, cons);
        dialog_line++;

        // inbound radial
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        JLabel track_label = new JLabel("Radial", JLabel.TRAILING);
        fix_panel.add(track_label, cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.radial = new JTextField(3);
        fix_panel.add(this.radial, cons);
        dialog_line++;

        // distance
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        JLabel distance_label = new JLabel("Distance", JLabel.TRAILING);
        fix_panel.add(distance_label, cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.distance = new JTextField(4);
        fix_panel.add(this.distance, cons);
        dialog_line++;

        return fix_panel;
    }


    private JPanel create_dialog_buttons_panel() {
        FlowLayout layout = new FlowLayout();
        JPanel fix_panel = new JPanel(layout);

        JButton hide_button = new JButton("Hide");
        hide_button.setActionCommand(FixDialog.HIDE);
        hide_button.addActionListener(this);

        JButton show_button = new JButton("Show");
        show_button.setActionCommand(FixDialog.SHOW);
        show_button.addActionListener(this);

        fix_panel.add(hide_button);
        fix_panel.add(show_button);

        return fix_panel;
    }


    public void actionPerformed(ActionEvent event) {
        this.setVisible(false);
        if (event.getActionCommand().equals(FixDialog.HIDE)) {
            hide_fix();
        } else if (event.getActionCommand().equals(FixDialog.SHOW)) {
            show_fix();
        }
    }


    private void show_fix() {

        if (fields_valid() == false) {
            JOptionPane.showMessageDialog(this,
                    this.field_validation_errors,
                    "Invalid fix",
                    JOptionPane.ERROR_MESSAGE);
        } else {

            // fix
            xhsi_settings.cdu_fix = this.fix.getText();

            // radial
            if ( this.radial.getText().equals("") ) {
                xhsi_settings.cdu_fix_radial = 0;
            } else {
                xhsi_settings.cdu_fix_radial = Integer.parseInt(this.radial.getText());
            }

            // distance
            if ( this.distance.getText().equals("") ) {
                xhsi_settings.cdu_fix_dist = 0.0f;
            } else {
                xhsi_settings.cdu_fix_dist = Float.parseFloat(this.distance.getText());
            }

            xhsi_settings.draw_cdu_fix = true;

        }

    }


    private void hide_fix() {
        xhsi_settings.draw_cdu_fix = false;
    }


    private boolean fields_valid() {
        this.field_validation_errors = new String();

        // fix
        if ( this.fix.getText().length() == 0 || (this.fix.getText().length() > 5) ) {
            field_validation_errors += "Invalid fix name!\n";
        }

        // radial
        if ( ! this.radial.getText().equals("") ) {
            try {
                int hld_track = Integer.parseInt(this.radial.getText());
                if ((hld_track < 0) || (hld_track > 360)) {
                    field_validation_errors += "Radial out of range (0-360)!\n";
                }
            } catch (NumberFormatException nf) {
                field_validation_errors += "Radial contains non-numeric characters!\n";
            }
        }

        // distance
        if ( ! this.distance.getText().equals("") ) {
            try {
                Float distance = Float.parseFloat(this.distance.getText());
                if ((distance < 0.0f) || (distance > 999.9f)) {
                    field_validation_errors +="Distance outside of range (0-999.9)!\n";
                }
            } catch (NumberFormatException nf) {
                field_validation_errors += "Distance contains non-numeric characters!\n";
            }
        }

        if ( ! field_validation_errors.equals("") ) {
            field_validation_errors = field_validation_errors.trim();
            return false;
        } else {
            return true;
        }
    }


}
