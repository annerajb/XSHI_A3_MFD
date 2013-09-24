/**
 * FuelDialog.java
 * 
 * Dialog for setting fuel capacity
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



public class FuelDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 1L;

    private static final String SET = "Set";
    private static final String EST = "Estimate";

    private JTextField capacity;
    private String units;
    private JLabel units_text;

    private String field_validation_errors = null;

    private XHSISettings xhsi_settings = XHSISettings.get_instance();
    private XHSIPreferences xhsi_preferences = XHSIPreferences.get_instance();
    


    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    public FuelDialog(JFrame owner_frame) {
        super(owner_frame, "Fuel capacity");

        this.setResizable(false);

        Container content_pane = getContentPane();
        content_pane.setLayout(new BorderLayout());
        content_pane.add(create_capacity_panel(), BorderLayout.CENTER);
        content_pane.add(create_dialog_buttons_panel(), BorderLayout.SOUTH);

        init_capacity();
        pack();
    }


    public void init_capacity() {

        this.units = xhsi_preferences.get_preference(XHSIPreferences.PREF_FUEL_UNITS);
        units_text.setText(this.units);

        float capacity_kg = xhsi_settings.avionics.get_aircraft().get_fuel_capacity();
        float fuel_multiplier = xhsi_preferences.get_fuel_multiplier();
        this.capacity.setText( Float.toString(capacity_kg * fuel_multiplier) );

    }


    private JPanel create_capacity_panel() {

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints cons = new GridBagConstraints();
        JPanel capacity_panel = new JPanel(layout);

        int dialog_line = 0;

        cons.ipadx = 10;
        cons.ipady = 0;
        cons.insets = new Insets(5, 10, 0, 0);

        // capacity
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        JLabel track_label = new JLabel("Total fuel capacity :", JLabel.TRAILING);
        capacity_panel.add(track_label, cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.capacity = new JTextField(8);
        capacity_panel.add(this.capacity, cons);
        dialog_line++;

        // units
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        JLabel units_label = new JLabel("Units :", JLabel.TRAILING);
        capacity_panel.add(units_label, cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        units_text = new JLabel(this.units, JLabel.TRAILING);
//        {
//            // For now, only in KG !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//            units_text.setText("KG");
//        }
        capacity_panel.add(units_text, cons);
        dialog_line++;

        return capacity_panel;

    }


    private JPanel create_dialog_buttons_panel() {
        FlowLayout layout = new FlowLayout();
        JPanel capacity_panel = new JPanel(layout);

        JButton estimate_button = new JButton(FuelDialog.EST);
        estimate_button.setActionCommand(FuelDialog.EST);
        estimate_button.addActionListener(this);

        JButton set_button = new JButton(FuelDialog.SET);
        set_button.setActionCommand(FuelDialog.SET);
        set_button.addActionListener(this);

        capacity_panel.add(estimate_button);
        capacity_panel.add(set_button);

        return capacity_panel;
    }


    public void actionPerformed(ActionEvent event) {
        
        if (event.getActionCommand().equals(FuelDialog.EST)) {
            
            // force an estimate
            xhsi_settings.avionics.get_aircraft().estimate_fuel_capacity();
            // get that value
            float capacity_kg = xhsi_settings.avionics.get_aircraft().get_fuel_capacity();
            float fuel_multiplier = xhsi_preferences.get_fuel_multiplier();
            this.capacity.setText( Float.toString(capacity_kg * fuel_multiplier) );
            
        } else if (event.getActionCommand().equals(FuelDialog.SET)) {
            
            set_capacity();
            
        }
        
    }


    private void set_capacity() {

        if (fields_valid() == false) {
            JOptionPane.showMessageDialog(this,
                    this.field_validation_errors,
                    "Invalid fuel capacity",
                    JOptionPane.ERROR_MESSAGE);
        } else {

            // capacity
            if ( ! this.capacity.getText().equals("") ) {
                xhsi_settings.avionics.get_aircraft().set_fuel_capacity(
                        Float.parseFloat(this.capacity.getText()) / xhsi_preferences.get_fuel_multiplier()
                    );
            }

            this.setVisible(false);

        }

    }

    
    private boolean fields_valid() {
        this.field_validation_errors = new String();

        // capacity
        if ( ! this.capacity.getText().equals("") ) {
            try {
                float fuel = Float.parseFloat(this.capacity.getText());
                if ((fuel < 0.0f) || (fuel > 1000000.0f)) {
                    field_validation_errors += "Fuel capacity out of range (0-1000000)!\n";
                }
            } catch (NumberFormatException nf) {
                field_validation_errors += "Fuel capacity contains non-numeric characters!\n";
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
