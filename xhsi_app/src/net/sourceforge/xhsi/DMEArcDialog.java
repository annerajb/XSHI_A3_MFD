/**
 * DMEArcDialog.java
 * 
 * Dialog for setting DME arc
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

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class DMEArcDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 1L;

    private static final String SHOW = "show";
    private static final String HIDE = "hide";

    private JTextField radius_textfield;

    private String field_validation_errors = null;

    private int bank;

    private XHSISettings xhsi_settings;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");


    public DMEArcDialog(JFrame owner_frame, int radio) {
        super(owner_frame, "DME" + radio + " arc");

        this.bank = radio;

        this.xhsi_settings = XHSISettings.get_instance();

        this.setResizable(false);
        //this.setUndecorated(false);
        
        Container content_pane = getContentPane();
        content_pane.setLayout(new BorderLayout());
        content_pane.add(create_radius_panel(radio), BorderLayout.CENTER);
        content_pane.add(create_dialog_buttons_panel(), BorderLayout.SOUTH);

        init_arc();
        pack();

    }


    private void init_arc() {

        float radius = 0.0f;
        if (this.bank == 1)
            radius = this.xhsi_settings.dme1_radius;
        else
            radius = this.xhsi_settings.dme2_radius;
        if (radius == 0.0f)
            this.radius_textfield.setText("");
        else
            this.radius_textfield.setText("" + radius);

    }


    private JPanel create_radius_panel(int bank) {
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints cons = new GridBagConstraints();
        JPanel radius_panel = new JPanel(layout);

        cons.ipadx = 10;
        cons.ipady = 0;
        cons.insets = new Insets(5, 10, 0, 0);

        // radius
        cons.gridx = 0;
        cons.gridy = 0;
        cons.anchor = GridBagConstraints.EAST;
        JLabel radius_label = new JLabel("Radius :", JLabel.TRAILING);
        radius_panel.add(radius_label, cons);
        cons.gridx = 2;
        cons.gridy = 0;
        cons.anchor = GridBagConstraints.WEST;
        this.radius_textfield = new JTextField(5);
        radius_panel.add(this.radius_textfield, cons);

        return radius_panel;
    }


    private JPanel create_dialog_buttons_panel() {
        FlowLayout layout = new FlowLayout();
        JPanel radius_panel = new JPanel(layout);

        JButton clear_button = new JButton("Hide");
        clear_button.setActionCommand(DMEArcDialog.HIDE);
        clear_button.addActionListener(this);

        JButton set_button = new JButton("Show");
        set_button.setActionCommand(DMEArcDialog.SHOW);
        set_button.addActionListener(this);

        radius_panel.add(clear_button);
        radius_panel.add(set_button);

        return radius_panel;
    }


    public void actionPerformed(ActionEvent event) {
        this.setVisible(false);
        if (DMEArcDialog.HIDE.equals(event.getActionCommand())) {
            hide_radius();
        } else if (DMEArcDialog.SHOW.equals(event.getActionCommand())) {
            show_radius();
        }
    }


    private void show_radius() {
        float radius;
        if (fields_valid() == false) {
            JOptionPane.showMessageDialog(this,
                    this.field_validation_errors,
                    "Invalid radius",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            if ( this.radius_textfield.getText().equals("") ) {
                radius = 0.0f;
            } else {
                radius = Float.parseFloat(this.radius_textfield.getText());
            }
            if (this.bank == 1) {
                this.xhsi_settings.dme1_radius = radius;
                this.xhsi_settings.checkbox_dme1_arc.setSelected(true);
            } else {
                this.xhsi_settings.dme2_radius = radius;
                this.xhsi_settings.checkbox_dme2_arc.setSelected(true);
            }
        }
    }


    private void hide_radius() {
        if (this.bank == 1) {
            this.xhsi_settings.dme1_radius = 0.0f;
            this.xhsi_settings.checkbox_dme1_arc.setSelected(false);
        } else {
            this.xhsi_settings.dme2_radius = 0.0f;
            this.xhsi_settings.checkbox_dme2_arc.setSelected(false);
        }
    }


    private boolean fields_valid() {
        this.field_validation_errors = new String();

        // Radius
        if ( ! this.radius_textfield.getText().equals("") ) {
            try {
                Float radius = Float.parseFloat(this.radius_textfield.getText());
                if ((radius < 0.0f) || (radius > 999.9f)) {
                    field_validation_errors +="Radius outside of range (0-999)!\n";
                }
            } catch (NumberFormatException nf) {
                field_validation_errors += "Radius contains non-numeric characters!\n";
            }
        }

        if (field_validation_errors.equals("") == false) {
            field_validation_errors = field_validation_errors.trim();
            return false;
        } else {
            return true;
        }
    }


}
