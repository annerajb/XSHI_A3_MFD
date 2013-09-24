/**
 * HoldingDialog.java
 * 
 * Dialog for setting holding
 * 
 * Copyright (C) 2009  Marc Rogiers (marrog.123@gmail.com)
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


public class HoldingDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 1L;

    private static final String SHOW = "show";
    private static final String HIDE = "hide";

    private JTextField fix;
    private JTextField track;
    private JCheckBox standard;
    private JRadioButton leg60;
    private JRadioButton leg90;
    private JRadioButton leg120;
//    private JTextField radial;
//    private JTextField distance;

    private String field_validation_errors = null;

    private XHSISettings xhsi_settings = XHSISettings.get_instance();


    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    public HoldingDialog(JFrame owner_frame) {
        super(owner_frame, "Holding");

        this.setResizable(false);

        Container content_pane = getContentPane();
        content_pane.setLayout(new BorderLayout());
        content_pane.add(create_holding_panel(), BorderLayout.CENTER);
        content_pane.add(create_dialog_buttons_panel(), BorderLayout.SOUTH);

        init_holding();
        pack();
    }


    private void init_holding() {

        this.fix.setText(xhsi_settings.holding_fix);
        this.track.setText("" + xhsi_settings.holding_track);
        //this.time.setText("" + xhsi_settings.holding_legduration);
        this.leg60.setSelected(xhsi_settings.holding_legduration == 1.0f);
        this.leg90.setSelected(xhsi_settings.holding_legduration == 1.5f);
        this.leg120.setSelected(xhsi_settings.holding_legduration == 2.0f);
        this.standard.setSelected(xhsi_settings.holding_nonstandard);
//        if (xhsi_settings.holding_distance == 0.0f) {
//            this.radial.setText("");
//            this.distance.setText("");
//        } else {
//            this.radial.setText("" + xhsi_settings.holding_radial);
//            this.distance.setText("" + xhsi_settings.holding_distance);
//        }

    }


    private JPanel create_holding_panel() {
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints cons = new GridBagConstraints();
        JPanel holding_panel = new JPanel(layout);

        int dialog_line = 0;

        cons.ipadx = 10;
        cons.ipady = 0;
        cons.insets = new Insets(5, 10, 0, 0);

        // fix
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        JLabel fix_label = new JLabel("Holding fix (VOR, NDB, Waypoint)", JLabel.TRAILING);
        holding_panel.add(fix_label, cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.fix = new JTextField(5);
        holding_panel.add(this.fix, cons);
        dialog_line++;

        // inbound track
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        JLabel track_label = new JLabel("Inbound track", JLabel.TRAILING);
        holding_panel.add(track_label, cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.track = new JTextField(3);
        holding_panel.add(this.track, cons);
        dialog_line++;

        // leg duration
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        JLabel time_label = new JLabel("Leg duration", JLabel.TRAILING);
        holding_panel.add(time_label, cons);
        ButtonGroup leg_durations = new ButtonGroup();
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.leg60 = new JRadioButton("1 min");
        holding_panel.add(this.leg60, cons);
        leg_durations.add(this.leg60);
        dialog_line++;
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.leg90 = new JRadioButton("1 \u00BD min");
        holding_panel.add(this.leg90, cons);
        leg_durations.add(this.leg90);
        dialog_line++;
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.leg120 = new JRadioButton("2 min");
        holding_panel.add(this.leg120, cons);
        leg_durations.add(this.leg120);
        dialog_line++;

        // standard turns
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        holding_panel.add(new JLabel("Non-standard (left) turns", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.standard = new JCheckBox();
        holding_panel.add(standard, cons);
        dialog_line++;

// a displaced holding fix is not correctly placed on the map (at least for now)
// and anyway, it is probably not very useful
//        // radial
//        cons.gridx = 0;
//        cons.gridy = dialog_line;
//        cons.anchor = GridBagConstraints.EAST;
//        JLabel radial_label = new JLabel("Optional: radial from this fix", JLabel.TRAILING);
//        holding_panel.add(radial_label, cons);
//        cons.gridx = 2;
//        cons.gridy = dialog_line;
//        cons.anchor = GridBagConstraints.WEST;
//        this.radial = new JTextField(3);
//        holding_panel.add(this.radial, cons);
//        dialog_line++;
//
//        // distance
//        cons.gridx = 0;
//        cons.gridy = dialog_line;
//        cons.anchor = GridBagConstraints.EAST;
//        JLabel distance_label = new JLabel("Optional: distance from this fix", JLabel.TRAILING);
//        holding_panel.add(distance_label, cons);
//        cons.gridx = 2;
//        cons.gridy = dialog_line;
//        cons.anchor = GridBagConstraints.WEST;
//        this.distance = new JTextField(4);
//        holding_panel.add(this.distance, cons);
//        dialog_line++;

        return holding_panel;
    }


    private JPanel create_dialog_buttons_panel() {
        FlowLayout layout = new FlowLayout();
        JPanel holding_panel = new JPanel(layout);

        JButton hide_button = new JButton("Hide");
        hide_button.setActionCommand(HoldingDialog.HIDE);
        hide_button.addActionListener(this);

        JButton show_button = new JButton("Show");
        show_button.setActionCommand(HoldingDialog.SHOW);
        show_button.addActionListener(this);

        holding_panel.add(hide_button);
        holding_panel.add(show_button);

        return holding_panel;
    }


    public void actionPerformed(ActionEvent event) {
        this.setVisible(false);
        if (event.getActionCommand().equals(HoldingDialog.HIDE)) {
            hide_holding();
        } else if (event.getActionCommand().equals(HoldingDialog.SHOW)) {
            show_holding();
        }
    }


    private void show_holding() {

        if (fields_valid() == false) {
            JOptionPane.showMessageDialog(this,
                    this.field_validation_errors,
                    "Invalid holding",
                    JOptionPane.ERROR_MESSAGE);
        } else {

            // fix
            xhsi_settings.holding_fix = this.fix.getText();

            // track
            if ( this.track.getText().equals("") ) {
                xhsi_settings.holding_track = 0;
            } else {
                xhsi_settings.holding_track = Integer.parseInt(this.track.getText());
            }

            // leg duration
            if ( this.leg120.isSelected() ) {
                xhsi_settings.holding_legduration = 2.0f;
            } else if ( this.leg90.isSelected() ) {
                xhsi_settings.holding_legduration = 1.5f;
            } else {
                xhsi_settings.holding_legduration = 1.0f;
            }

            // standard
            xhsi_settings.holding_nonstandard = this.standard.isSelected();

//            // radial
//            if ( this.radial.getText().equals("") ) {
//                xhsi_settings.holding_radial = 0;
//            } else {
//                xhsi_settings.holding_radial = Integer.parseInt(this.radial.getText());
//            }
//
//            // distance
//            if ( this.distance.getText().equals("") ) {
//                xhsi_settings.holding_distance = 0.0f;
//            } else {
//                xhsi_settings.holding_distance = Float.parseFloat(this.distance.getText());
//            }

            xhsi_settings.draw_holding = true;
            xhsi_settings.radiobutton_holding_hide.setSelected(false);
            xhsi_settings.radiobutton_holding_show.setSelected(true);

        }

    }


    private void hide_holding() {
        xhsi_settings.draw_holding = false;
        xhsi_settings.radiobutton_holding_hide.setSelected(true);
        xhsi_settings.radiobutton_holding_show.setSelected(false);
    }


    private boolean fields_valid() {
        this.field_validation_errors = new String();

        // fix
        if ( this.fix.getText().length() == 0 || (this.fix.getText().length() > 5) ) {
            field_validation_errors += "Invalid fix name!\n";
        }

        // track
        if ( ! this.track.getText().equals("") ) {
            try {
                int hld_track = Integer.parseInt(this.track.getText());
                if ((hld_track < 0) || (hld_track > 360)) {
                    field_validation_errors += "Track out of range (0-360)!\n";
                }
            } catch (NumberFormatException nf) {
                field_validation_errors += "Track contains non-numeric characters!\n";
            }
        }

        // leg duration
        // nothing to check...

        // standard
        // nothig to check...

//        // radial
//        if ( ! this.radial.getText().equals("") ) {
//            try {
//                int radial = Integer.parseInt(this.radial.getText());
//                if ((radial < 0) || (radial > 360)) {
//                    field_validation_errors +="Radial outside of map_range (0-360)!\n";
//                }
//            } catch (NumberFormatException nf) {
//                field_validation_errors += "Radial contains non-numeric characters!\n";
//            }
//        }
//
//        // distance
//        if ( ! this.distance.getText().equals("") ) {
//            try {
//                Float distance = Float.parseFloat(this.distance.getText());
//                if ((distance < 0.0f) || (distance > 99.9f)) {
//                    field_validation_errors +="Distance outside of map_range (0-99.9)!\n";
//                }
//            } catch (NumberFormatException nf) {
//                field_validation_errors += "Distance contains non-numeric characters!\n";
//            }
//        }

        if ( ! field_validation_errors.equals("") ) {
            field_validation_errors = field_validation_errors.trim();
            return false;
        } else {
            return true;
        }
    }


}
