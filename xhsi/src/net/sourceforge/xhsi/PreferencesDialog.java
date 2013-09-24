/**
 * PreferencesDialog.java
 * 
 * Dialog for setting preferences of XHSI_PLUGIN
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
package net.sourceforge.xhsi;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import net.sourceforge.xhsi.flightdeck.empty.EmptyComponent;
import net.sourceforge.xhsi.flightdeck.pfd.PFDComponent;
import net.sourceforge.xhsi.flightdeck.nd.NDComponent;
import net.sourceforge.xhsi.flightdeck.eicas.EICASComponent;
import net.sourceforge.xhsi.flightdeck.mfd.MFDComponent;
import net.sourceforge.xhsi.flightdeck.annunciators.AnnunComponent;
import net.sourceforge.xhsi.flightdeck.clock.ClockComponent;
import net.sourceforge.xhsi.flightdeck.uh60m.UH60MComponent;


public class PreferencesDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 1L;

    private XHSIPreferences preferences;

    private JComboBox simcom_combobox;
    private JTextField aptnav_dir_textfield;
    private JTextField port_textfield;
    private JComboBox loglevel_combobox;
    private JComboBox operator_combobox;
    private String operators[] = { XHSIPreferences.PILOT, XHSIPreferences.COPILOT, XHSIPreferences.INSTRUCTOR };

    private JCheckBox conwin_minimized_checkbox;
    private JCheckBox start_ontop_checkbox;
    private JCheckBox hide_window_frame_checkbox;
    private JCheckBox panel_locked_checkbox;
    private JButton get_button;

    private static final int MAX_WINS = 8; // Empty, PFD, ND, EICAS, MFD, Annunciators and Clock
    private JCheckBox panel_active_checkbox[] = new JCheckBox[MAX_WINS];
    private JTextField panel_pos_x_textfield[] = new JTextField[MAX_WINS];
    private JTextField panel_pos_y_textfield[] = new JTextField[MAX_WINS];
    private JTextField panel_width_textfield[] = new JTextField[MAX_WINS];
    private JTextField panel_height_textfield[] = new JTextField[MAX_WINS];
    private JTextField panel_border_textfield[] = new JTextField[MAX_WINS];
    private JCheckBox panel_square_checkbox[] = new JCheckBox[MAX_WINS];
    private JComboBox panel_orientation_combobox[] = new JComboBox[MAX_WINS];

    private JCheckBox anti_alias_checkbox;
    private JComboBox border_style_combobox;
    private String borderstyles[] = { XHSIPreferences.BORDER_RELIEF, XHSIPreferences.BORDER_LIGHT, XHSIPreferences.BORDER_DARK, XHSIPreferences.BORDER_NONE };
    private JComboBox border_color_combobox;
    private String bordercolors[] = { XHSIPreferences.BORDER_GRAY, XHSIPreferences.BORDER_BROWN, XHSIPreferences.BORDER_BLUE };
    
    private String orientations[] = {
        XHSIPreferences.Orientation.UP.get_rotation(),
        XHSIPreferences.Orientation.LEFT.get_rotation(),
        XHSIPreferences.Orientation.RIGHT.get_rotation(),
        XHSIPreferences.Orientation.DOWN.get_rotation(),
        };

    private JCheckBox use_power_checkbox;
    private JCheckBox auto_frontcourse_checkbox;
    private JComboBox hsi_source_combobox;
    private String hsi_sources[] = { XHSIPreferences.USER, XHSIPreferences.NAV1, XHSIPreferences.NAV2 };

    private JTextField min_rwy_textfield;
    //private Level[] loglevels = new Level[] { Level.OFF, Level.SEVERE, Level.WARNING, Level.CONFIG, Level.INFO, Level.FINE, Level.FINEST };
    private String[] simcoms = { XHSIPreferences.XHSI_PLUGIN, XHSIPreferences.SCS };
    private Level[] loglevels = { Level.OFF, Level.SEVERE, Level.WARNING, Level.CONFIG, Level.INFO, Level.FINE, Level.FINEST };
    private JComboBox rwy_units_combobox;
    private String units[] = { "meters", "feet" };
    private JCheckBox draw_rwy_checkbox;
    private JCheckBox draw_bezier_pavements_checkbox;
    private JCheckBox airbus_modes_checkbox;
    private JCheckBox draw_range_arcs_checkbox;
    private JCheckBox use_more_color_checkbox;
    private JCheckBox mode_mismatch_caution_checkbox;
    private JCheckBox tcas_always_on_checkbox;
    private JCheckBox classic_hsi_checkbox;
    private JCheckBox appvor_uncluttered_checkbox;
    private JCheckBox plan_aircraft_center_checkbox;
    private JCheckBox draw_inside_rose_checkbox;
    private JCheckBox bold_fonts_checkbox;

    private int du_pos_x[] = new int[MAX_WINS];
    private int du_pos_y[] = new int[MAX_WINS];
    private int du_width[] = new int[MAX_WINS];
    private int du_height[] = new int[MAX_WINS];

//    private JCheckBox draw_wide_horizon_checkbox;
    private JComboBox horizon_style_combobox;
    private String horizons[] = { XHSIPreferences.HORIZON_SQUARE, XHSIPreferences.HORIZON_ROUNDED, XHSIPreferences.HORIZON_FULLWIDTH, XHSIPreferences.HORIZON_FULLSCREEN };
    private JComboBox dial_transparency_combobox;
    private String transparencies[] = { "0", "25", "50", "75" };

    private JCheckBox draw_single_cue_fd_checkbox;
    private JCheckBox draw_aoa_checkbox;
    private JCheckBox pfd_hsi_checkbox;
    private JCheckBox colored_hsi_course_checkbox;
    private JCheckBox draw_radios_checkbox;

    private JCheckBox draw_eicas_primary_checkbox;
    private JComboBox engine_count_combobox;
    private JComboBox engine_type_combobox;
    private String engine_types[] = { XHSIPreferences.ENGINE_TYPE_N1, /*XHSIPreferences.ENGINE_TYPE_EPR,*/ XHSIPreferences.ENGINE_TYPE_TRQ, XHSIPreferences.ENGINE_TYPE_MAP };
    private JComboBox fuel_unit_combobox;
    private String fuel_units[] = { XHSIPreferences.FUEL_KG, XHSIPreferences.FUEL_LBS, XHSIPreferences.FUEL_USG, XHSIPreferences.FUEL_LTR };

    private JComboBox mfd_mode_combobox;
    private String mfd_modes[] = { XHSIPreferences.MFD_MODE_SWITCHABLE, /*XHSIPreferences.MFD_MODE_TAXI_CHART,*/ XHSIPreferences.MFD_MODE_ARPT_CHART , XHSIPreferences.MFD_MODE_FPLN, XHSIPreferences.MFD_MODE_LOWER_EICAS };
    private JComboBox arpt_chart_color_combobox;
    private String arpt_chart_colors[] = { XHSIPreferences.ARPT_DIAGRAM_COLOR_AUTO, XHSIPreferences.ARPT_DIAGRAM_COLOR_DAY, XHSIPreferences.ARPT_DIAGRAM_COLOR_NIGHT };


    private ArrayList<XHSIInstrument> flightdeck;

    private String field_validation_errors = null;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");


    public PreferencesDialog(JFrame owner_frame, ArrayList<XHSIInstrument> fd) {

        super(owner_frame, "XHSI Preferences");

        this.flightdeck = fd;

        this.preferences = XHSIPreferences.get_instance();

        this.setResizable(false);
        
        Container content_pane = getContentPane();
        content_pane.setLayout(new BorderLayout());
        content_pane.add(create_preferences_tabs(), BorderLayout.CENTER);
        content_pane.add(create_dialog_buttons_panel(), BorderLayout.SOUTH);

        init_preferences();
        enable_lock_fields();
        pack();

    }


    private void init_preferences() {

        // SYSTEM

        String instrument_position = preferences.get_preference(XHSIPreferences.PREF_INSTRUMENT_POSITION);
        for (int i=0; i<operators.length; i++) {
            if ( instrument_position.equalsIgnoreCase(operators[i]) ) {
                this.operator_combobox.setSelectedIndex(i);
            }
        }

        for (int i=0; i<simcoms.length; i++) {
            if ( preferences.get_preference(XHSIPreferences.PREF_SIMCOM).equals(simcoms[i]) ) {
                this.simcom_combobox.setSelectedIndex(i);
            }
        }

        this.aptnav_dir_textfield.setText(preferences.get_preference(XHSIPreferences.PREF_APTNAV_DIR));
        this.port_textfield.setText(preferences.get_preference(XHSIPreferences.PREF_PORT));

        for (int i=0; i<loglevels.length; i++) {
            if (logger.getLevel() == loglevels[i]) {
                this.loglevel_combobox.setSelectedIndex(i);
            }
        }

        
        // GRAPHICS

        this.bold_fonts_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_BOLD_FONTS).equalsIgnoreCase("true"));

        this.use_more_color_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_USE_MORE_COLOR).equalsIgnoreCase("true"));

        this.anti_alias_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_ANTI_ALIAS).equalsIgnoreCase("true"));

        String border_style = preferences.get_preference(XHSIPreferences.PREF_BORDER_STYLE);
        for (int i=0; i<borderstyles.length; i++) {
            if ( border_style.equals( borderstyles[i] ) ) {
                this.border_style_combobox.setSelectedIndex(i);
            }
        }

        String border_color = preferences.get_preference(XHSIPreferences.PREF_BORDER_COLOR);
        for (int i=0; i<bordercolors.length; i++) {
            if ( border_color.equals( bordercolors[i] ) ) {
                this.border_color_combobox.setSelectedIndex(i);
            }
        }

        this.draw_bezier_pavements_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_DRAW_BEZIER_PAVEMENTS).equalsIgnoreCase("true"));


        // WINDOWS

        this.start_ontop_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_START_ONTOP).equalsIgnoreCase("true"));

        this.hide_window_frame_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_HIDE_WINDOW_FRAMES).equalsIgnoreCase("true"));

        this.panel_locked_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_PANELS_LOCKED).equalsIgnoreCase("true"));

        for (int j = 0; j<MAX_WINS; j++) {

            this.panel_active_checkbox[j].setSelected( preferences.get_panel_active(j) );

            this.panel_pos_x_textfield[j].setText( "" + preferences.get_panel_pos_x(j) );
            this.panel_pos_y_textfield[j].setText( "" + preferences.get_panel_pos_y(j) );
            this.panel_width_textfield[j].setText( "" + preferences.get_panel_width(j) );
            this.panel_height_textfield[j].setText( "" + preferences.get_panel_height(j) );
            this.panel_border_textfield[j].setText( "" + preferences.get_panel_border(j) );

            this.panel_square_checkbox[j].setSelected( preferences.get_panel_square(j) );

            XHSIPreferences.Orientation pref_orientation = preferences.get_panel_orientation(j);
            for (int i=0; i<orientations.length; i++) {
                if ( pref_orientation.get_rotation().equals( orientations[i] ) ) {
                    this.panel_orientation_combobox[j].setSelectedIndex(i);
                }
            }

        }

        this.conwin_minimized_checkbox.setSelected(preferences.get_conwin_minimized());


        // Avionics Options

        this.use_power_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_USE_POWER).equalsIgnoreCase("true"));

        this.auto_frontcourse_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_AUTO_FRONTCOURSE).equalsIgnoreCase("true"));

        this.hsi_source_combobox.setSelectedIndex( preferences.get_hsi_source() );


        // ND Options (11)

        this.min_rwy_textfield.setText(preferences.get_preference(XHSIPreferences.PREF_MIN_RWY_LEN));

        String rwy_units = preferences.get_preference(XHSIPreferences.PREF_RWY_LEN_UNITS);
        for (int i=0; i<units.length; i++) {
            if ( rwy_units.equals( units[i] ) ) {
                this.rwy_units_combobox.setSelectedIndex(i);
            }
        }

        this.airbus_modes_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_AIRBUS_MODES).equalsIgnoreCase("true"));

        this.draw_rwy_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_DRAW_RUNWAYS).equalsIgnoreCase("true"));

        this.draw_range_arcs_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_DRAW_RANGE_ARCS).equalsIgnoreCase("true"));

        this.mode_mismatch_caution_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_MODE_MISMATCH_CAUTION).equalsIgnoreCase("true"));

        this.tcas_always_on_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_TCAS_ALWAYS_ON).equalsIgnoreCase("true"));

        this.classic_hsi_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_CLASSIC_HSI).equalsIgnoreCase("true"));

        this.appvor_uncluttered_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_APPVOR_UNCLUTTER).equalsIgnoreCase("true"));

        this.plan_aircraft_center_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_PLAN_AIRCRAFT_CENTER).equalsIgnoreCase("true"));

        this.draw_inside_rose_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_DRAW_INSIDE_ROSE).equalsIgnoreCase("true"));

        this.colored_hsi_course_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_COLORED_HSI_COURSE).equalsIgnoreCase("true"));


        // PFD Options (3)

        String horizonstyle = preferences.get_preference(XHSIPreferences.PREF_HORIZON_STYLE);
        for (int i=0; i<horizons.length; i++) {
            if ( horizonstyle.equals( horizons[i] ) ) {
                this.horizon_style_combobox.setSelectedIndex(i);
            }
        }

        String transp = preferences.get_preference(XHSIPreferences.PREF_DIAL_TRANSPARENCY);
        for (int i=0; i<transparencies.length; i++) {
            if ( transp.equals( transparencies[i] ) ) {
                this.dial_transparency_combobox.setSelectedIndex(i);
            }
        }

        this.draw_single_cue_fd_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_SINGLE_CUE_FD).equalsIgnoreCase("true"));

        this.draw_aoa_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_DRAW_AOA).equalsIgnoreCase("true"));

        this.pfd_hsi_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_PFD_DRAW_HSI).equalsIgnoreCase("true"));

        this.draw_radios_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_PFD_DRAW_RADIOS).equalsIgnoreCase("true"));


        // EICAS Options (4)

        this.draw_eicas_primary_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_EICAS_PRIMARY_ONLY).equalsIgnoreCase("true"));

        this.engine_count_combobox.setSelectedIndex( preferences.get_override_engine_count() );
        
        String engine = preferences.get_preference(XHSIPreferences.PREF_ENGINE_TYPE);
        for (int i=0; i<engine_types.length; i++) {
            if ( engine.equals( engine_types[i] ) ) {
                this.engine_type_combobox.setSelectedIndex(i);
            }
        }

        String fuel = preferences.get_preference(XHSIPreferences.PREF_FUEL_UNITS);
        for (int i=0; i<fuel_units.length; i++) {
            if ( fuel.equals( fuel_units[i] ) ) {
                this.fuel_unit_combobox.setSelectedIndex(i);
            }
        }


        // MFD Options (2)

        String mfd_mode = preferences.get_preference(XHSIPreferences.PREF_MFD_MODE);
        for (int i=0; i<mfd_modes.length; i++) {
            if ( mfd_mode.equals( mfd_modes[i] ) ) {
                this.mfd_mode_combobox.setSelectedIndex(i);
            }
        }

        String taxichart = preferences.get_preference(XHSIPreferences.PREF_ARPT_CHART_COLOR);
        for (int i=0; i<arpt_chart_colors.length; i++) {
            if ( taxichart.equals( arpt_chart_colors[i] ) ) {
                this.arpt_chart_color_combobox.setSelectedIndex(i);
            }
        }


    }


    private JTabbedPane create_preferences_tabs() {

        JTabbedPane tabs_panel = new JTabbedPane();
        tabs_panel.add( "System", create_system_tab() );
        tabs_panel.add( "Windows", create_windows_tab() );
        tabs_panel.add( "Graphics", create_graphics_tab() );
        tabs_panel.add( "Avionics", create_avionics_options_tab() );
        tabs_panel.add( "PFD", create_pfd_options_tab() );
        tabs_panel.add( "ND", create_nd_options_tab() );
        tabs_panel.add( "EICAS", create_eicas_options_tab() );
        tabs_panel.add( "MFD", create_mfd_options_tab() );

        return tabs_panel;

    }


    private JPanel create_system_tab() {

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints cons = new GridBagConstraints();
        JPanel system_panel = new JPanel(layout);

        cons.ipadx = 10;
        cons.ipady = 0;
        cons.insets = new Insets(2, 5, 0, 0);

        int dialog_line = 0;

        // Simulator communication
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        system_panel.add(new JLabel("Simulator communication", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.simcom_combobox = new JComboBox();
        this.simcom_combobox.addItem(XHSIPreferences.XHSI_PLUGIN);
// this.simcom_combobox.addItem(XHSIPreferences.SCS);
        this.simcom_combobox.addActionListener(this);
        system_panel.add(this.simcom_combobox, cons);
        dialog_line++;

        // Empty line for spacing
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        system_panel.add(new JLabel(" ", JLabel.TRAILING), cons);
        dialog_line++;

        // AptNav Resources directory
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        system_panel.add(new JLabel("AptNav Resources directory", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.aptnav_dir_textfield = new JTextField(40);
        system_panel.add(this.aptnav_dir_textfield, cons);
        dialog_line++;
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        JButton browse_button = new JButton("Browse");
        browse_button.setActionCommand("browse");
        browse_button.addActionListener(this);
        system_panel.add(browse_button, cons);
        dialog_line++;
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.gridwidth = 3;
        cons.anchor = GridBagConstraints.EAST;
        system_panel.add(new JLabel("(can be X-Plane base directory, or directory where AptNav20yymmXP900.zip has been unzipped)", JLabel.TRAILING), cons);
        cons.gridwidth = 1;
        dialog_line++;

        // Empty line for spacing
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        system_panel.add(new JLabel(" ", JLabel.TRAILING), cons);
        dialog_line++;

        // incoming UDP port
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        system_panel.add(new JLabel("Incoming UDP port (default 49020) (*)", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.port_textfield = new JTextField(5);
        system_panel.add(this.port_textfield, cons);
        dialog_line++;

        // some info concerning Incoming UDP port
        dialog_line++;
        cons.gridx = 2;
        cons.gridy = dialog_line;
        //cons.gridwidth = 3;
        cons.anchor = GridBagConstraints.WEST;
        system_panel.add(new JLabel("(must match XHSI_plugin's Destination UDP port)", JLabel.TRAILING), cons);
        cons.gridwidth = 1;
        dialog_line++;

        // Empty line for spacing
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        system_panel.add(new JLabel(" ", JLabel.TRAILING), cons);
        dialog_line++;

        // Logging Level
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        system_panel.add(new JLabel("Logging Level", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.loglevel_combobox = new JComboBox();
        this.loglevel_combobox.addItem("Off");
        this.loglevel_combobox.addItem("Severe");
        this.loglevel_combobox.addItem("Warning");
        this.loglevel_combobox.addItem("Configuration");
        this.loglevel_combobox.addItem("Info");
        this.loglevel_combobox.addItem("Fine");
        this.loglevel_combobox.addItem("Finest");
        this.loglevel_combobox.addActionListener(this);
        system_panel.add(this.loglevel_combobox, cons);
        dialog_line++;

        // Empty line for spacing
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        system_panel.add(new JLabel(" ", JLabel.TRAILING), cons);
        dialog_line++;

        // pilot/co-pilot/instructor
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        system_panel.add(new JLabel("Menu settings control the displays for ...", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.operator_combobox = new JComboBox();
        this.operator_combobox.addItem("Pilot (standard X-Plane settings)");
        this.operator_combobox.addItem("Copilot (XHSI's extra settings)");
        this.operator_combobox.addItem("Instructor (independent settings)");
        this.operator_combobox.addActionListener(this);
        system_panel.add(this.operator_combobox, cons);
        dialog_line++;

        // Empty line for spacing
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        system_panel.add(new JLabel(" ", JLabel.TRAILING), cons);
        dialog_line++;

        // A reminder
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        system_panel.add(new JLabel("(*) : requires a restart", JLabel.TRAILING), cons);
        dialog_line++;

        return system_panel;

    }


    private JPanel create_windows_tab() {

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints cons = new GridBagConstraints();
        JPanel windows_panel = new JPanel(layout);

        cons.ipadx = 10;
        cons.ipady = 0;
        cons.insets = new Insets(2, 5, 0, 0);

        int dialog_line = 0;

        // Start with "Keep window on top"
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        windows_panel.add(new JLabel("Set \"Windows on top\" at startup", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.start_ontop_checkbox = new JCheckBox();
        windows_panel.add(this.start_ontop_checkbox, cons);
        dialog_line++;

        // Draw window frame
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        windows_panel.add(new JLabel("Hide window title bar and frame", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 3;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.hide_window_frame_checkbox = new JCheckBox("  (requires a restart)");
        windows_panel.add(this.hide_window_frame_checkbox, cons);
        dialog_line++;

        // Lock the window positions and sizes
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        windows_panel.add(new JLabel("Lock instrument windows position and size", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 3;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.panel_locked_checkbox = new JCheckBox(" (values below...)");
        this.panel_locked_checkbox.setActionCommand("locktoggle");
        this.panel_locked_checkbox.addActionListener(this);
        windows_panel.add(this.panel_locked_checkbox, cons);
        dialog_line++;

        // Get current window positions and sizes
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        windows_panel.add(new JLabel("Get current window positions and sizes", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 3;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        get_button = new JButton("Get");
        get_button.setActionCommand("getwindow");
        get_button.addActionListener(this);
        windows_panel.add(get_button, cons);
        dialog_line++;


        JPanel sub_panel = new JPanel(new GridBagLayout());
        GridBagConstraints subcons = new GridBagConstraints();

        int col = 2;
        String[] colheader = { " Display ", " Left ", " Top ", " Width ", " Height ", " Border ", " Square ", " Orientation " };
        for (String head : colheader) {
                subcons.gridx = col;
                subcons.gridwidth = 1;
                subcons.gridy = 0;
                subcons.anchor = GridBagConstraints.CENTER;
                sub_panel.add(new JLabel(head, JLabel.TRAILING), subcons);
                col++;
        }

        for (XHSIInstrument.DU instrum : XHSIInstrument.DU.values() ) {

            int i = instrum.get_id();
            String descr = XHSIInstrument.DU.values()[i].get_name();
            int subdialog_column = 2;
            int subdialog_line = i + 1;

            // Instrument name
            subcons.gridx = 0;
            subcons.gridwidth = 1;
            subcons.gridy = subdialog_line;
            subcons.anchor = GridBagConstraints.EAST;
            sub_panel.add(new JLabel(descr, JLabel.TRAILING), subcons);

            // Activate
            subcons.gridx = subdialog_column;
            subcons.gridy = subdialog_line;
            subcons.anchor = GridBagConstraints.CENTER;
            this.panel_active_checkbox[i] = new JCheckBox();
            this.panel_active_checkbox[i].setActionCommand("drawtoggle");
            this.panel_active_checkbox[i].addActionListener(this);
            this.panel_active_checkbox[i].setToolTipText("Display the " + descr);
            sub_panel.add(this.panel_active_checkbox[i], subcons);
            subdialog_column++;

            // panel position x
            subcons.gridx = subdialog_column;
            subcons.gridy = subdialog_line;
            subcons.anchor = GridBagConstraints.CENTER;
            this.panel_pos_x_textfield[i] = new JTextField(4);
            this.panel_pos_x_textfield[i].setToolTipText("Horizontal window position for " + descr);
            sub_panel.add(this.panel_pos_x_textfield[i], subcons);
            subdialog_column++;

            // panel position y
            subcons.gridx = subdialog_column;
            subcons.gridy = subdialog_line;
            subcons.anchor = GridBagConstraints.CENTER;
            this.panel_pos_y_textfield[i] = new JTextField(4);
            this.panel_pos_y_textfield[i].setToolTipText("Vertical window position for " + descr);
            sub_panel.add(this.panel_pos_y_textfield[i], subcons);
            subdialog_column++;

            // panel width
            subcons.gridx = subdialog_column;
            subcons.gridy = subdialog_line;
            subcons.anchor = GridBagConstraints.CENTER;
            this.panel_width_textfield[i] = new JTextField(4);
            this.panel_width_textfield[i].setToolTipText("Window width for " + descr);
            sub_panel.add(this.panel_width_textfield[i], subcons);
            subdialog_column++;

            // panel height
            subcons.gridx = subdialog_column;
            subcons.gridy = subdialog_line;
            subcons.anchor = GridBagConstraints.CENTER;
            this.panel_height_textfield[i] = new JTextField(4);
            this.panel_height_textfield[i].setToolTipText("Window height for " + descr);
            sub_panel.add(this.panel_height_textfield[i], subcons);
            subdialog_column++;

            // panel border
            subcons.gridx = subdialog_column;
            subcons.gridy = subdialog_line;
            subcons.anchor = GridBagConstraints.CENTER;
            this.panel_border_textfield[i] = new JTextField(3);
            this.panel_border_textfield[i].setToolTipText("Border size for " + descr);
            if ( i > 0 ) sub_panel.add(this.panel_border_textfield[i], subcons);
            subdialog_column++;

            // Draw square window
            subcons.gridx = subdialog_column;
            subcons.gridy = subdialog_line;
            subcons.anchor = GridBagConstraints.CENTER;
            this.panel_square_checkbox[i] = new JCheckBox();
            this.panel_square_checkbox[i].setToolTipText("Keep the instrument display for " + descr + " square");
            if ( i > 0 ) sub_panel.add(this.panel_square_checkbox[i], subcons);
            subdialog_column++;

            // orientation
            subcons.gridx = subdialog_column;
            subcons.gridy = subdialog_line;
            subcons.anchor = GridBagConstraints.CENTER;
            this.panel_orientation_combobox[i] = new JComboBox();
            this.panel_orientation_combobox[i].addItem( XHSIPreferences.Orientation.UP.get_rotation() );
            this.panel_orientation_combobox[i].addItem( XHSIPreferences.Orientation.LEFT.get_rotation() );
            this.panel_orientation_combobox[i].addItem( XHSIPreferences.Orientation.RIGHT.get_rotation() );
            this.panel_orientation_combobox[i].addItem( XHSIPreferences.Orientation.DOWN.get_rotation() );
            this.panel_orientation_combobox[i].addActionListener(this);
            this.panel_orientation_combobox[i].setToolTipText("Window orientation for " + descr);
            if ( i > 0 ) sub_panel.add(this.panel_orientation_combobox[i], subcons);
            subdialog_column++;

        }

        cons.gridx = 0;
        cons.gridwidth = 3;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        //windows_panel.add(crewmembers_panel, cons);
        windows_panel.add(sub_panel, cons);
        dialog_line++;

        // Minimize control/command/status window at startup
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        windows_panel.add(new JLabel("Minimize command window at startup", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.conwin_minimized_checkbox = new JCheckBox();
        windows_panel.add(this.conwin_minimized_checkbox, cons);
        dialog_line++;

        
        return windows_panel;

    }


    private JPanel create_graphics_tab() {

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints cons = new GridBagConstraints();
        JPanel graphics_panel = new JPanel(layout);

        cons.ipadx = 10;
        cons.ipady = 0;
        cons.insets = new Insets(2, 5, 0, 0);

        int dialog_line = 0;

        // Border style
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        graphics_panel.add(new JLabel("Border style", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 3;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.border_style_combobox = new JComboBox();
        this.border_style_combobox.addItem(XHSIPreferences.BORDER_RELIEF);
        this.border_style_combobox.addItem(XHSIPreferences.BORDER_LIGHT);
        this.border_style_combobox.addItem(XHSIPreferences.BORDER_DARK);
        this.border_style_combobox.addItem(XHSIPreferences.BORDER_NONE);
        this.border_style_combobox.addActionListener(this);
        graphics_panel.add(this.border_style_combobox, cons);
        dialog_line++;

        // Border color
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        graphics_panel.add(new JLabel("Border color", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 3;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.border_color_combobox = new JComboBox();
        this.border_color_combobox.addItem(XHSIPreferences.BORDER_GRAY);
        this.border_color_combobox.addItem(XHSIPreferences.BORDER_BROWN);
        this.border_color_combobox.addItem(XHSIPreferences.BORDER_BLUE);
        this.border_color_combobox.addActionListener(this);
        graphics_panel.add(this.border_color_combobox, cons);
        dialog_line++;

        // Empty line for spacing
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        graphics_panel.add(new JLabel(" ", JLabel.TRAILING), cons);

        // Use more color variations
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        graphics_panel.add(new JLabel("Use more color nuances", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.use_more_color_checkbox = new JCheckBox();
        graphics_panel.add(this.use_more_color_checkbox, cons);
        dialog_line++;

        // Bold fonts
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        graphics_panel.add(new JLabel("Bold fonts", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.bold_fonts_checkbox = new JCheckBox();
        graphics_panel.add(this.bold_fonts_checkbox, cons);
        dialog_line++;

        // Anti-alias
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        graphics_panel.add(new JLabel("Anti-aliasing", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.anti_alias_checkbox = new JCheckBox();
        graphics_panel.add(this.anti_alias_checkbox, cons);
        dialog_line++;

        // Bezier curves for the pavements
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        graphics_panel.add(new JLabel("Draw taxiways and aprons using bezier curves", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.draw_bezier_pavements_checkbox = new JCheckBox();
        graphics_panel.add(this.draw_bezier_pavements_checkbox, cons);
        dialog_line++;

//        // A reminder
//        cons.gridx = 3;
//        cons.gridwidth = 1;
//        cons.gridy = dialog_line;
//        cons.anchor = GridBagConstraints.RIGHT;
//        graphics_panel.add(new JLabel("(*) : requires a restart", JLabel.TRAILING), cons);
//        dialog_line++;

        return graphics_panel;

    }


    private JPanel create_avionics_options_tab() {

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints cons = new GridBagConstraints();
        JPanel avionics_options_panel = new JPanel(layout);

        cons.ipadx = 10;
        cons.ipady = 0;
        cons.insets = new Insets(2, 5, 0, 0);

        int dialog_line = 0;

        // Use avionics power
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        avionics_options_panel.add(new JLabel("Use battery and avionics power", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.use_power_checkbox = new JCheckBox("  (annunciators need battery power; displays need avionics power)");
        avionics_options_panel.add(this.use_power_checkbox, cons);
        dialog_line++;

        // Empty line for spacing
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        avionics_options_panel.add(new JLabel(" ", JLabel.TRAILING), cons);
        dialog_line++;

        // Auto-set CRS for LOC/ILS
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        avionics_options_panel.add(new JLabel("Auto-sync CRS for LOC/ILS", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.auto_frontcourse_checkbox = new JCheckBox("  (set the CRS (=OBS) automatically to the Localizer or ILS frontcourse)");
        avionics_options_panel.add(this.auto_frontcourse_checkbox, cons);
        dialog_line++;
        dialog_line++;

        // Empty line for spacing
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        avionics_options_panel.add(new JLabel(" ", JLabel.TRAILING), cons);
        dialog_line++;

        // HSI source
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        avionics_options_panel.add(new JLabel("HSI source", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.hsi_source_combobox = new JComboBox();
        this.hsi_source_combobox.addItem("Pilot/Copilot/Instructor selection");
        this.hsi_source_combobox.addItem("Always NAV1");
        this.hsi_source_combobox.addItem("Always NAV2");
        this.hsi_source_combobox.addActionListener(this);
        avionics_options_panel.add(this.hsi_source_combobox, cons);
        dialog_line++;

        return avionics_options_panel;

    }


    private JPanel create_pfd_options_tab() {

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints cons = new GridBagConstraints();
        JPanel pfd_options_panel = new JPanel(layout);

        cons.ipadx = 10;
        cons.ipady = 0;
        cons.insets = new Insets(2, 5, 0, 0);

        int dialog_line = 0;

        // Horizon style
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        pfd_options_panel.add(new JLabel("Horizon style", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.horizon_style_combobox = new JComboBox();
        this.horizon_style_combobox.addItem("Square");
        this.horizon_style_combobox.addItem("Rounded square");
        this.horizon_style_combobox.addItem("Full width");
        this.horizon_style_combobox.addItem("Full screen");
        this.horizon_style_combobox.addActionListener(this);
        pfd_options_panel.add(this.horizon_style_combobox, cons);
        dialog_line++;
        dialog_line++;

        // Dial transparency
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        pfd_options_panel.add(new JLabel("Dial transparency %", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.dial_transparency_combobox = new JComboBox();
        this.dial_transparency_combobox.addItem(transparencies[0]);
        this.dial_transparency_combobox.addItem(transparencies[1]);
        this.dial_transparency_combobox.addItem(transparencies[2]);
        this.dial_transparency_combobox.addItem(transparencies[3]);
        this.dial_transparency_combobox.addActionListener(this);
        pfd_options_panel.add(this.dial_transparency_combobox, cons);
        dialog_line++;
        dialog_line++;

        // Draw an HSI below the AI
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        pfd_options_panel.add(new JLabel("Draw a full HSI below the attitude indicator", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.pfd_hsi_checkbox = new JCheckBox();
        pfd_options_panel.add(this.pfd_hsi_checkbox, cons);
        dialog_line++;
        dialog_line++;

        // V-bar FD instead of crosshairs
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        pfd_options_panel.add(new JLabel("V-bar FD instead of crosshairs", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.draw_single_cue_fd_checkbox = new JCheckBox();
        pfd_options_panel.add(this.draw_single_cue_fd_checkbox, cons);
        dialog_line++;
        dialog_line++;

        // Draw AOA-indicator, pushing a (crippled) RA-indicator to the bottom
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        pfd_options_panel.add(new JLabel("Draw AOA-indicator", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.draw_aoa_checkbox = new JCheckBox();
        pfd_options_panel.add(this.draw_aoa_checkbox, cons);
        dialog_line++;
        dialog_line++;

        // Draw Radios
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        pfd_options_panel.add(new JLabel("Draw Radio frequencies", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.draw_radios_checkbox = new JCheckBox("  (window aspect ratio must be 4/3 or wider)");
        pfd_options_panel.add(this.draw_radios_checkbox, cons);
        dialog_line++;
        dialog_line++;

//        // A reminder
//        cons.gridx = 2;
//        cons.gridwidth = 1;
//        cons.gridy = dialog_line;
//        cons.anchor = GridBagConstraints.RIGHT;
//        efb_options_panel.add(new JLabel("(*) : requires a restart", JLabel.TRAILING), cons);
//        dialog_line++;

        return pfd_options_panel;

    }


    private JPanel create_nd_options_tab() {

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints cons = new GridBagConstraints();
        JPanel nd_options_panel = new JPanel(layout);

        cons.ipadx = 10;
        cons.ipady = 0;
        cons.insets = new Insets(2, 5, 0, 0);

        int dialog_line = 0;

        // Pseudo Airbus display modes
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        nd_options_panel.add(new JLabel("Airbus display modes", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.airbus_modes_checkbox = new JCheckBox("  ( ROSE_ILS / ROSE_VOR / ROSE_NAV / ARC / PLAN )");
        nd_options_panel.add(this.airbus_modes_checkbox, cons);
        dialog_line++;

        // Display Centered APP and VOR as a classic HSI without moving map
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        nd_options_panel.add(new JLabel("Display Centered APP and VOR as a classic-style HSI", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.classic_hsi_checkbox = new JCheckBox("  (as in a real B737-NG)");
        nd_options_panel.add(this.classic_hsi_checkbox, cons);
        dialog_line++;

        // Classic-style HSI course pointer follows nav-source color
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        nd_options_panel.add(new JLabel("Classic-style HSI course pointer follows nav-source color", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.colored_hsi_course_checkbox = new JCheckBox();
        nd_options_panel.add(this.colored_hsi_course_checkbox, cons);
        dialog_line++;

        // Keep moving map in APP and VOR modes uncluttered
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        nd_options_panel.add(new JLabel("Display only tuned navaids in APP and VOR modes", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.appvor_uncluttered_checkbox = new JCheckBox("  (to keep the moving map in APP and VOR modes uncluttered)");
        nd_options_panel.add(this.appvor_uncluttered_checkbox, cons);
        dialog_line++;

        // Display app/vor frequency mismatch caution message
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        nd_options_panel.add(new JLabel("Warn for EFIS MODE/NAV FREQ mismatch", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.mode_mismatch_caution_checkbox = new JCheckBox();
        nd_options_panel.add(this.mode_mismatch_caution_checkbox, cons);
        dialog_line++;

        // Center PLAN mode on waypoint
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        nd_options_panel.add(new JLabel("Center PLAN mode on aircraft", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.plan_aircraft_center_checkbox = new JCheckBox();
        nd_options_panel.add(this.plan_aircraft_center_checkbox, cons);
        dialog_line++;

        // Draw map only inside the compass rose
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        nd_options_panel.add(new JLabel("Draw map only inside the compass rose", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.draw_inside_rose_checkbox = new JCheckBox();
        nd_options_panel.add(this.draw_inside_rose_checkbox, cons);
        dialog_line++;

        // Draw range arcs
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        nd_options_panel.add(new JLabel("Draw range arcs", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.draw_range_arcs_checkbox = new JCheckBox();
        nd_options_panel.add(this.draw_range_arcs_checkbox, cons);
        dialog_line++;

        // Draw runways at lowest map ranges
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        nd_options_panel.add(new JLabel("Draw runways at lowest map ranges", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.draw_rwy_checkbox = new JCheckBox();
        nd_options_panel.add(this.draw_rwy_checkbox, cons);
        dialog_line++;

        // Minimum runway length
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        nd_options_panel.add(new JLabel("Airport minimum runway length", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.min_rwy_textfield = new JTextField(4);
        nd_options_panel.add(this.min_rwy_textfield, cons);
        dialog_line++;
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        nd_options_panel.add(new JLabel("Runway length units", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.rwy_units_combobox = new JComboBox();
        this.rwy_units_combobox.addItem("meters");
        this.rwy_units_combobox.addItem("feet");
        this.rwy_units_combobox.addActionListener(this);
        nd_options_panel.add(this.rwy_units_combobox, cons);
        dialog_line++;

        // TCAS always ON
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        nd_options_panel.add(new JLabel("TCAS always ON", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.tcas_always_on_checkbox = new JCheckBox();
        nd_options_panel.add(this.tcas_always_on_checkbox, cons);
        dialog_line++;
        dialog_line++;

//        // A reminder
//        cons.gridx = 2;
//        cons.gridwidth = 1;
//        cons.gridy = dialog_line;
//        cons.anchor = GridBagConstraints.EAST;
//        nd_options_panel.add(new JLabel("(*) : requires a restart", JLabel.TRAILING), cons);
//        dialog_line++;

        return nd_options_panel;

    }


    private JPanel create_eicas_options_tab() {

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints cons = new GridBagConstraints();
        JPanel eicas_options_panel = new JPanel(layout);

        cons.ipadx = 10;
        cons.ipady = 0;
        cons.insets = new Insets(2, 5, 0, 0);

        int dialog_line = 0;

        // Draw only primary engine indications
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        eicas_options_panel.add(new JLabel("Draw only primary engine indications", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.draw_eicas_primary_checkbox = new JCheckBox();
        eicas_options_panel.add(this.draw_eicas_primary_checkbox, cons);
        dialog_line++;
        dialog_line++;

        // Number of engines
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        eicas_options_panel.add(new JLabel("Number of engines", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.engine_count_combobox = new JComboBox();
        this.engine_count_combobox.addItem("Auto");
        this.engine_count_combobox.addItem("1");
        this.engine_count_combobox.addItem("2");
        this.engine_count_combobox.addItem("3");
        this.engine_count_combobox.addItem("4");
        this.engine_count_combobox.addItem("5");
        this.engine_count_combobox.addItem("6");
        this.engine_count_combobox.addItem("7");
        this.engine_count_combobox.addItem("8");
        eicas_options_panel.add(this.engine_count_combobox, cons);
        dialog_line++;
        dialog_line++;

        // Engines type
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        eicas_options_panel.add(new JLabel("Engines type", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.engine_type_combobox = new JComboBox();
        this.engine_type_combobox.addItem(XHSIPreferences.ENGINE_TYPE_N1);
//        this.engine_type_combobox.addItem(XHSIPreferences.ENGINE_TYPE_EPR);
        this.engine_type_combobox.addItem(XHSIPreferences.ENGINE_TYPE_TRQ);
        this.engine_type_combobox.addItem(XHSIPreferences.ENGINE_TYPE_MAP);
        this.engine_type_combobox.addActionListener(this);
        eicas_options_panel.add(this.engine_type_combobox, cons);
        dialog_line++;
        dialog_line++;

        // Fuel units
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        eicas_options_panel.add(new JLabel("Fuel units", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.fuel_unit_combobox = new JComboBox();
        this.fuel_unit_combobox.addItem(XHSIPreferences.FUEL_KG);
        this.fuel_unit_combobox.addItem(XHSIPreferences.FUEL_LBS);
        this.fuel_unit_combobox.addItem(XHSIPreferences.FUEL_USG);
        this.fuel_unit_combobox.addItem(XHSIPreferences.FUEL_LTR);
        this.fuel_unit_combobox.addActionListener(this);
        eicas_options_panel.add(this.fuel_unit_combobox, cons);
        dialog_line++;
        dialog_line++;

//        // A reminder
//        cons.gridx = 2;
//        cons.gridwidth = 1;
//        cons.gridy = dialog_line;
//        cons.anchor = GridBagConstraints.RIGHT;
//        efb_options_panel.add(new JLabel("(*) : requires a restart", JLabel.TRAILING), cons);
//        dialog_line++;

        return eicas_options_panel;

    }


    private JPanel create_mfd_options_tab() {

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints cons = new GridBagConstraints();
        JPanel mfd_options_panel = new JPanel(layout);

        cons.ipadx = 10;
        cons.ipady = 0;
        cons.insets = new Insets(2, 5, 0, 0);

        int dialog_line = 0;

        // MFD display mode
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        mfd_options_panel.add(new JLabel("MFD display mode", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.mfd_mode_combobox = new JComboBox();
        this.mfd_mode_combobox.addItem("Switchable");
        //this.mfd_mode_combobox.addItem("Taxi Chart");
        this.mfd_mode_combobox.addItem("Airport Chart");
        this.mfd_mode_combobox.addItem("Flight Plan");
        this.mfd_mode_combobox.addItem("Lower EICAS");
        this.mfd_mode_combobox.addActionListener(this);
        mfd_options_panel.add(this.mfd_mode_combobox, cons);
        dialog_line++;
        dialog_line++;

        // Airport Diagram colors
        cons.gridx = 0;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        mfd_options_panel.add(new JLabel("Airport Diagram day/night colors", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridwidth = 1;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.arpt_chart_color_combobox = new JComboBox();
        this.arpt_chart_color_combobox.addItem("Auto");
        this.arpt_chart_color_combobox.addItem("Day");
        this.arpt_chart_color_combobox.addItem("Night");
        this.arpt_chart_color_combobox.addActionListener(this);
        mfd_options_panel.add(this.arpt_chart_color_combobox, cons);
        dialog_line++;
        dialog_line++;

        return mfd_options_panel;

    }


    private JPanel create_dialog_buttons_panel() {

        FlowLayout layout = new FlowLayout();
        JPanel preferences_panel = new JPanel(layout);

        JButton cancel_button = new JButton("Cancel");
        cancel_button.setActionCommand("cancel");
        cancel_button.addActionListener(this);

        JButton apply_button = new JButton("Apply");
        apply_button.setActionCommand("apply");
        apply_button.addActionListener(this);

        JButton ok_button = new JButton("OK");
        ok_button.setActionCommand("ok");
        ok_button.addActionListener(this);

        preferences_panel.add(cancel_button);
        preferences_panel.add(apply_button);
        preferences_panel.add(ok_button);

        return preferences_panel;

    }


    public void actionPerformed(ActionEvent event) {

        if ( event.getActionCommand().equals("browse") ) {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int ret = fc.showOpenDialog(this);

            if (ret == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                this.aptnav_dir_textfield.setText(file.getAbsolutePath());
            }
        } else if ( event.getActionCommand().equals("drawtoggle")) {
            enable_lock_fields();
        } else if ( event.getActionCommand().equals("locktoggle")) {
            enable_lock_fields();
        } else if ( event.getActionCommand().equals("getwindow")) {
            for (XHSIInstrument du : this.flightdeck) {
                int i = du.get_index();
                this.panel_pos_x_textfield[i].setText( "" + du.frame.getX() );
                this.panel_pos_y_textfield[i].setText( "" + du.frame.getY() );
                this.panel_width_textfield[i].setText( "" + du.frame.getWidth() );
                this.panel_height_textfield[i].setText( "" + du.frame.getHeight() );
            }
        } else if ( event.getActionCommand().equals("cancel") ) {
            this.setVisible(false);
            init_preferences();
            enable_lock_fields();
        } else if ( event.getActionCommand().equals("apply") ) {
            if ( set_preferences() ) {
                resize_frames();
            }
        } else if ( event.getActionCommand().equals("ok") ) {
            if ( set_preferences() ) {
                this.setVisible(false);
                resize_frames();
            }
        }

    }


    private void enable_lock_fields() {
        for (int i=0; i<MAX_WINS; i++) {
            boolean active = this.panel_active_checkbox[i].isSelected();
            boolean lock = this.panel_locked_checkbox.isSelected();
            //this.get_button.setEnabled( this.panel_locked_checkbox.isSelected() );
            this.panel_pos_x_textfield[i].setEnabled( active & lock );
            this.panel_pos_y_textfield[i].setEnabled( active & lock );
            this.panel_width_textfield[i].setEnabled( active & lock );
            this.panel_height_textfield[i].setEnabled( active & lock );
            this.panel_border_textfield[i].setEnabled( active & lock );
            this.panel_square_checkbox[i].setEnabled( active );
            this.panel_orientation_combobox[i].setEnabled( active );
        }
    }


    private void resize_frames() {

        if ( this.panel_locked_checkbox.isSelected() ) {
            for (XHSIInstrument du : this.flightdeck) {
                int i = du.get_index();
                du.frame.setBounds(this.du_pos_x[i], this.du_pos_y[i], this.du_width[i], this.du_height[i]);
                boolean active = this.panel_active_checkbox[i].isSelected();
                if ( du.frame.isVisible() != active ) {
                    du.frame.setVisible( active );
                }
                switch ( du.get_index() ) {
                    case XHSIInstrument.EMPTY_ID :
                        ((EmptyComponent)du.components).forceReconfig();
                        break;
                    case XHSIInstrument.PFD_ID :
                        ((PFDComponent)du.components).forceReconfig();
                        break;
                    case XHSIInstrument.ND_ID :
                        ((NDComponent)du.components).forceReconfig();
                        break;
                    case XHSIInstrument.EICAS_ID :
                        ((EICASComponent)du.components).forceReconfig();
                        break;
                    case XHSIInstrument.MFD_ID :
                        ((MFDComponent)du.components).forceReconfig();
                        break;
                        
                    case XHSIInstrument.ANNUN_ID :
                        ((AnnunComponent)du.components).forceReconfig();
                        break;
                    case XHSIInstrument.CLOCK_ID :
                        ((ClockComponent)du.components).forceReconfig();
                        break;
                    case XHSIInstrument.UH60M_ID :
                        ((UH60MComponent)du.components).forceReconfig();
                        break;
                }
            }
        }

    }


    private boolean set_preferences() {

        boolean valid = fields_valid();
        if ( ! valid ) {
            JOptionPane.showMessageDialog(this,
                    this.field_validation_errors,
                    "Invalid Preferences",
                    JOptionPane.ERROR_MESSAGE);
        } else {

            // SYSTEM

            int loglevel_index = this.loglevel_combobox.getSelectedIndex();
            Level loglevel = this.loglevels[loglevel_index];
            logger.setLevel(loglevel);
            this.preferences.set_preference(XHSIPreferences.PREF_LOGLEVEL, loglevel.toString());

            if ( ! simcoms[this.simcom_combobox.getSelectedIndex()].equals(this.preferences.get_preference(XHSIPreferences.PREF_SIMCOM)) )
                this.preferences.set_preference(XHSIPreferences.PREF_SIMCOM, borderstyles[this.simcom_combobox.getSelectedIndex()]);

            if ( this.aptnav_dir_textfield.getText().equals(this.preferences.get_preference(XHSIPreferences.PREF_APTNAV_DIR)) == false )
                this.preferences.set_preference(XHSIPreferences.PREF_APTNAV_DIR, this.aptnav_dir_textfield.getText());

            if ( this.port_textfield.getText().equals(this.preferences.get_preference(XHSIPreferences.PREF_PORT)) == false )
                this.preferences.set_preference(XHSIPreferences.PREF_PORT, this.port_textfield.getText());

            if ( ! operators[this.operator_combobox.getSelectedIndex()].equals(this.preferences.get_preference(XHSIPreferences.PREF_INSTRUMENT_POSITION)) )
                this.preferences.set_preference(XHSIPreferences.PREF_INSTRUMENT_POSITION, operators[this.operator_combobox.getSelectedIndex()]);


            // GRAPHICS

            if ( this.use_more_color_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_USE_MORE_COLOR).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_USE_MORE_COLOR, this.use_more_color_checkbox.isSelected()?"true":"false");

            if ( this.bold_fonts_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_BOLD_FONTS).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_BOLD_FONTS, this.bold_fonts_checkbox.isSelected()?"true":"false");

            if ( this.anti_alias_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_ANTI_ALIAS).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_ANTI_ALIAS, this.anti_alias_checkbox.isSelected()?"true":"false");

            if ( ! borderstyles[this.border_style_combobox.getSelectedIndex()].equals(this.preferences.get_preference(XHSIPreferences.PREF_BORDER_STYLE)) )
                this.preferences.set_preference(XHSIPreferences.PREF_BORDER_STYLE, borderstyles[this.border_style_combobox.getSelectedIndex()]);

            if ( ! bordercolors[this.border_color_combobox.getSelectedIndex()].equals(this.preferences.get_preference(XHSIPreferences.PREF_BORDER_COLOR)) )
                this.preferences.set_preference(XHSIPreferences.PREF_BORDER_COLOR, bordercolors[this.border_color_combobox.getSelectedIndex()]);

            if ( this.draw_bezier_pavements_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_DRAW_BEZIER_PAVEMENTS).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_DRAW_BEZIER_PAVEMENTS, this.draw_bezier_pavements_checkbox.isSelected()?"true":"false");


            // WINDOWS

            if ( this.start_ontop_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_START_ONTOP).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_START_ONTOP, this.start_ontop_checkbox.isSelected()?"true":"false");

            if ( this.hide_window_frame_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_HIDE_WINDOW_FRAMES).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_HIDE_WINDOW_FRAMES, this.hide_window_frame_checkbox.isSelected()?"true":"false");

            if ( this.panel_locked_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_PANELS_LOCKED).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_PANELS_LOCKED , this.panel_locked_checkbox.isSelected()?"true":"false");

            for (int i=0; i<MAX_WINS; i++) {
                if ( this.panel_active_checkbox[i].isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_DU_PREPEND + i + XHSIPreferences.PREF_DU_ACTIVE).equals("true") )
                    this.preferences.set_preference( XHSIPreferences.PREF_DU_PREPEND + i + XHSIPreferences.PREF_DU_ACTIVE, this.panel_active_checkbox[i].isSelected()?"true":"false" );
                if ( ! this.panel_pos_x_textfield[i].getText().equals( this.preferences.get_preference(XHSIPreferences.PREF_DU_PREPEND + i + XHSIPreferences.PREF_DU_POS_X) ) )
                    this.preferences.set_preference( XHSIPreferences.PREF_DU_PREPEND + i + XHSIPreferences.PREF_DU_POS_X , this.panel_pos_x_textfield[i].getText() );
                if ( ! this.panel_pos_y_textfield[i].getText().equals( this.preferences.get_preference(XHSIPreferences.PREF_DU_PREPEND + i + XHSIPreferences.PREF_DU_POS_Y) ) )
                    this.preferences.set_preference( XHSIPreferences.PREF_DU_PREPEND + i + XHSIPreferences.PREF_DU_POS_Y , this.panel_pos_y_textfield[i].getText() );
                if ( ! this.panel_width_textfield[i].getText().equals( this.preferences.get_preference(XHSIPreferences.PREF_DU_PREPEND + i + XHSIPreferences.PREF_DU_WIDTH) ) )
                    this.preferences.set_preference( XHSIPreferences.PREF_DU_PREPEND + i + XHSIPreferences.PREF_DU_WIDTH , this.panel_width_textfield[i].getText() );
                if ( ! this.panel_height_textfield[i].getText().equals( this.preferences.get_preference(XHSIPreferences.PREF_DU_PREPEND + i + XHSIPreferences.PREF_DU_HEIGHT) ) )
                    this.preferences.set_preference( XHSIPreferences.PREF_DU_PREPEND + i + XHSIPreferences.PREF_DU_HEIGHT , this.panel_height_textfield[i].getText() );
                if ( ! this.panel_border_textfield[i].getText().equals( this.preferences.get_preference(XHSIPreferences.PREF_DU_PREPEND + i + XHSIPreferences.PREF_DU_BORDER) ) )
                    this.preferences.set_preference( XHSIPreferences.PREF_DU_PREPEND + i + XHSIPreferences.PREF_DU_BORDER , this.panel_border_textfield[i].getText() );
                if ( this.panel_square_checkbox[i].isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_DU_PREPEND + i + XHSIPreferences.PREF_DU_SQUARE).equals("true") )
                    this.preferences.set_preference( XHSIPreferences.PREF_DU_PREPEND + i + XHSIPreferences.PREF_DU_SQUARE, this.panel_square_checkbox[i].isSelected()?"true":"false" );
                if ( ! orientations[this.panel_orientation_combobox[i].getSelectedIndex()].equals(this.preferences.get_preference(XHSIPreferences.PREF_DU_PREPEND + i + XHSIPreferences.PREF_DU_ORIENTATION)) )
                    this.preferences.set_preference( XHSIPreferences.PREF_DU_PREPEND + i + XHSIPreferences.PREF_DU_ORIENTATION, orientations[this.panel_orientation_combobox[i].getSelectedIndex()] );
            }

            if ( this.conwin_minimized_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_CONWIN_MINIMIZED).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_CONWIN_MINIMIZED , this.conwin_minimized_checkbox.isSelected()?"true":"false");


            // Avionics options

            if ( this.use_power_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_USE_POWER).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_USE_POWER, this.use_power_checkbox.isSelected()?"true":"false");

            if ( this.auto_frontcourse_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_AUTO_FRONTCOURSE).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_AUTO_FRONTCOURSE, this.auto_frontcourse_checkbox.isSelected()?"true":"false");

            if ( this.hsi_source_combobox.getSelectedIndex() != this.preferences.get_hsi_source() )
                this.preferences.set_preference(XHSIPreferences.PREF_HSI_SOURCE, hsi_sources[this.hsi_source_combobox.getSelectedIndex()]);


            // ND options

            if ( this.min_rwy_textfield.getText().equals(this.preferences.get_preference(XHSIPreferences.PREF_MIN_RWY_LEN)) == false )
                this.preferences.set_preference(XHSIPreferences.PREF_MIN_RWY_LEN, this.min_rwy_textfield.getText());

            if ( ! units[this.rwy_units_combobox.getSelectedIndex()].equals(this.preferences.get_preference(XHSIPreferences.PREF_RWY_LEN_UNITS)) )
                this.preferences.set_preference(XHSIPreferences.PREF_RWY_LEN_UNITS, units[this.rwy_units_combobox.getSelectedIndex()]);

            if ( this.draw_rwy_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_DRAW_RUNWAYS).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_DRAW_RUNWAYS, this.draw_rwy_checkbox.isSelected()?"true":"false");

            if ( this.airbus_modes_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_AIRBUS_MODES).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_AIRBUS_MODES, this.airbus_modes_checkbox.isSelected()?"true":"false");

            if ( this.draw_range_arcs_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_DRAW_RANGE_ARCS).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_DRAW_RANGE_ARCS, this.draw_range_arcs_checkbox.isSelected()?"true":"false");

            if ( this.mode_mismatch_caution_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_MODE_MISMATCH_CAUTION).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_MODE_MISMATCH_CAUTION, this.mode_mismatch_caution_checkbox.isSelected()?"true":"false");

            if ( this.tcas_always_on_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_TCAS_ALWAYS_ON).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_TCAS_ALWAYS_ON, this.tcas_always_on_checkbox.isSelected()?"true":"false");

            if ( this.classic_hsi_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_CLASSIC_HSI).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_CLASSIC_HSI, this.classic_hsi_checkbox.isSelected()?"true":"false");

            if ( this.appvor_uncluttered_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_APPVOR_UNCLUTTER).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_APPVOR_UNCLUTTER, this.appvor_uncluttered_checkbox.isSelected()?"true":"false");

            if ( this.plan_aircraft_center_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_PLAN_AIRCRAFT_CENTER).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_PLAN_AIRCRAFT_CENTER, this.plan_aircraft_center_checkbox.isSelected()?"true":"false");

            if ( this.draw_inside_rose_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_DRAW_INSIDE_ROSE).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_DRAW_INSIDE_ROSE, this.draw_inside_rose_checkbox.isSelected()?"true":"false");

            if ( this.colored_hsi_course_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_COLORED_HSI_COURSE).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_COLORED_HSI_COURSE, this.colored_hsi_course_checkbox.isSelected()?"true":"false");


            // PFD options

            if ( ! horizons[this.horizon_style_combobox.getSelectedIndex()].equals(this.preferences.get_preference(XHSIPreferences.PREF_HORIZON_STYLE)) )
                this.preferences.set_preference(XHSIPreferences.PREF_HORIZON_STYLE, horizons[this.horizon_style_combobox.getSelectedIndex()]);

            if ( ! transparencies[this.dial_transparency_combobox.getSelectedIndex()].equals(this.preferences.get_preference(XHSIPreferences.PREF_DIAL_TRANSPARENCY)) )
                this.preferences.set_preference(XHSIPreferences.PREF_DIAL_TRANSPARENCY, transparencies[this.dial_transparency_combobox.getSelectedIndex()]);

            if ( this.draw_single_cue_fd_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_SINGLE_CUE_FD).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_SINGLE_CUE_FD, this.draw_single_cue_fd_checkbox.isSelected()?"true":"false");

            if ( this.draw_aoa_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_DRAW_AOA).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_DRAW_AOA, this.draw_aoa_checkbox.isSelected()?"true":"false");

            if ( this.pfd_hsi_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_PFD_DRAW_HSI).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_PFD_DRAW_HSI, this.pfd_hsi_checkbox.isSelected()?"true":"false");

            if ( this.draw_radios_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_PFD_DRAW_RADIOS).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_PFD_DRAW_RADIOS, this.draw_radios_checkbox.isSelected()?"true":"false");


            // EICAS options

            if ( this.draw_eicas_primary_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_EICAS_PRIMARY_ONLY).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_EICAS_PRIMARY_ONLY, this.draw_eicas_primary_checkbox.isSelected()?"true":"false");

            if ( this.engine_count_combobox.getSelectedIndex() != this.preferences.get_override_engine_count() )
                this.preferences.set_preference(XHSIPreferences.PREF_OVERRIDE_ENGINE_COUNT, "" + this.engine_count_combobox.getSelectedIndex());

            if ( ! engine_types[this.engine_type_combobox.getSelectedIndex()].equals(this.preferences.get_preference(XHSIPreferences.PREF_ENGINE_TYPE)) )
                this.preferences.set_preference(XHSIPreferences.PREF_ENGINE_TYPE, engine_types[this.engine_type_combobox.getSelectedIndex()]);

            if ( ! fuel_units[this.fuel_unit_combobox.getSelectedIndex()].equals(this.preferences.get_preference(XHSIPreferences.PREF_FUEL_UNITS)) )
                this.preferences.set_preference(XHSIPreferences.PREF_FUEL_UNITS, fuel_units[this.fuel_unit_combobox.getSelectedIndex()]);


            // MFD options

            if ( ! mfd_modes[this.mfd_mode_combobox.getSelectedIndex()].equals(this.preferences.get_preference(XHSIPreferences.PREF_MFD_MODE)) )
                this.preferences.set_preference(XHSIPreferences.PREF_MFD_MODE, mfd_modes[this.mfd_mode_combobox.getSelectedIndex()]);

            if ( ! arpt_chart_colors[this.arpt_chart_color_combobox.getSelectedIndex()].equals(this.preferences.get_preference(XHSIPreferences.PREF_ARPT_CHART_COLOR)) )
                this.preferences.set_preference(XHSIPreferences.PREF_ARPT_CHART_COLOR, arpt_chart_colors[this.arpt_chart_color_combobox.getSelectedIndex()]);


        }

        return valid;

    }


    private boolean fields_valid() {

        this.field_validation_errors = new String();

        // Incoming port
        int port;
        try {
            port = Integer.parseInt(this.port_textfield.getText());
            if ((port < 1024) || (port > 65535)) {
                field_validation_errors += "Port out of range (1024-65535)!\n";
            }
        } catch (NumberFormatException nf) {
            field_validation_errors += "Port contains non-numeric characters!\n";
        }

        // minimum runway length
        int min_rwy;
        try {
            min_rwy = Integer.parseInt(this.min_rwy_textfield.getText());
            if ((min_rwy < 0) || (min_rwy > 9999)) {
                field_validation_errors += "Minimum Runway Length out of range (0-9999)!\n";
            }
        } catch (NumberFormatException nf) {
            field_validation_errors += "Minimum Runway Length contains non-numeric characters!\n";
        }

        for (int i=0; i<MAX_WINS; i++) {

            // Window horizontal position
            try {
                this.du_pos_x[i] = Integer.parseInt(this.panel_pos_x_textfield[i].getText());
                if ((this.du_pos_x[i] < -9999) || (this.du_pos_x[i] > 9999)) {
                    field_validation_errors += "Window horizontal position out of range!\n";
                }
            } catch (NumberFormatException nf) {
                field_validation_errors += "Window horizontal position contains non-numeric characters!\n";
            }
            // Window vertical position
            try {
                this.du_pos_y[i] = Integer.parseInt(this.panel_pos_y_textfield[i].getText());
                if ((this.du_pos_y[i] < -9999) || (this.du_pos_y[i] > 9999)) {
                    field_validation_errors += "Window horizontal position out of range!\n";
                }
            } catch (NumberFormatException nf) {
                field_validation_errors += "Window horizontal position contains non-numeric characters!\n";
            }
            // Window width
            try {
                this.du_width[i] = Integer.parseInt(this.panel_width_textfield[i].getText());
                if ((this.du_width[i] < this.flightdeck.get(i).du.get_min_width()) || (this.du_width[i] > 3999)) {
                    field_validation_errors += "Window width out of range!\n";
                }
            } catch (NumberFormatException nf) {
                field_validation_errors += "Window width contains non-numeric characters!\n";
            }
            // Window height
            try {
                this.du_height[i] = Integer.parseInt(this.panel_height_textfield[i].getText());
                if ((this.du_height[i] < this.flightdeck.get(i).du.get_min_height()) || (this.du_height[i] > 1999)) {
                    field_validation_errors += "Window height out of range!\n";
                }
            } catch (NumberFormatException nf) {
                field_validation_errors += "Window height contains non-numeric characters!\n";
            }
            // Border width
            int border;
            try {
                border = Integer.parseInt(this.panel_border_textfield[i].getText());
                if ( (border < 0) || (border > 399) || ((this.du_width[i] - 2*border) < this.flightdeck.get(i).du.get_min_width() - 20) || ((this.du_height[i] - 2*border) < this.flightdeck.get(i).du.get_min_height() - 20) ) {
                    field_validation_errors += "Instrument border out of range!\n";
                }
            } catch (NumberFormatException nf) {
                field_validation_errors += "Window height contains non-numeric characters!\n";
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
