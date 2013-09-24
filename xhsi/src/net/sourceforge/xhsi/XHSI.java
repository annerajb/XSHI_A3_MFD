/**
 * XHSI.java
 * 
 * Main class starting and controls the UI and all threads of the horizontal 
 * situation indicator display.
 * The menu options with associated global settings to override the sim settings
 *
 * Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
 * Copyright (C) 2011-2013  Marc Rogiers (marrog.123@gmail.com)
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


import java.awt.Color;
//import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

//import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import net.sourceforge.xhsi.model.ModelFactory;

import net.sourceforge.xhsi.model.aptnavdata.AptNavXP900DatNavigationObjectBuilder;
//import net.sourceforge.xhsi.model.aptnavdata.AptNavXP900DatTaxiChartBuilder;

import net.sourceforge.xhsi.model.xplane.XPlaneDataPacketDecoder;
import net.sourceforge.xhsi.model.xplane.XPlaneFlightSessionPlayer;
import net.sourceforge.xhsi.model.xplane.XPlaneFlightSessionRecorder;
import net.sourceforge.xhsi.model.xplane.XPlaneModelFactory;
import net.sourceforge.xhsi.model.xplane.XPlaneSimDataRepository;
import net.sourceforge.xhsi.model.xplane.XPlaneUDPReceiver;
import net.sourceforge.xhsi.model.xplane.XPlaneUDPSender;

import net.sourceforge.xhsi.flightdeck.UIHeartbeat;

import net.sourceforge.xhsi.conwin.ConWinComponent;
import net.sourceforge.xhsi.flightdeck.annunciators.AnnunComponent;
import net.sourceforge.xhsi.flightdeck.clock.ClockComponent;
import net.sourceforge.xhsi.flightdeck.mfd.MFDComponent;
import net.sourceforge.xhsi.flightdeck.eicas.EICASComponent;
import net.sourceforge.xhsi.flightdeck.empty.EmptyComponent;
import net.sourceforge.xhsi.flightdeck.nd.NDComponent;
import net.sourceforge.xhsi.flightdeck.pfd.PFDComponent;

import net.sourceforge.xhsi.util.XHSILogFormatter;


public class XHSI implements ActionListener {


    private static final String RELEASE = "2.0 Beta 5";


    public enum Mode { REPLAY, LIVE, RECORD }

    private Image logo_image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("XHSI_logo32.png"));

    // menu item commands must be unique...
    public static final String ACTION_QUIT  = "Quit";
    public static final String ACTION_PREFERENCES = "Preferences ...";
    public static final String ACTION_ONTOP = "Windows on top";
    public static final String ACTION_ABOUT = "About XHSI ...";

    ModelFactory model_instance;

    private XHSIPreferences preferences;
    private ArrayList running_threads;

    private ConWinComponent xhsi_ui;
    private JFrame xhsi_frame;

    private ArrayList<XHSIInstrument> instruments;

    private PreferencesDialog preferences_dialog;
    private ProgressDialog nob_progress_dialog;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    private InetAddress ip_host;


    public static void main(String args[]) throws Exception {

        Handler handler = new ConsoleHandler();
        handler.setLevel(Level.ALL);
        handler.setFormatter(new XHSILogFormatter());
        handler.setFilter(null);
        logger.addHandler(handler);

        handler = new FileHandler("XHSI.log");
        handler.setLevel(Level.ALL);
        handler.setFormatter(new XHSILogFormatter());
        handler.setFilter(null);
        logger.addHandler(handler);

        logger.setLevel(Level.ALL);
        logger.setUseParentHandlers(false);

        logger.config("XHSI " + XHSI.RELEASE + " started");

        XHSIStatus.status = XHSIStatus.STATUS_STARTUP;

        if ((args.length >= 2) && (args[0].equals("--record"))) {
            int recording_rate = 1;
            if (args.length == 3)
                recording_rate = Integer.parseInt(args[2]);
            new XHSI(Mode.RECORD, args[1], recording_rate);
        } else if ((args.length == 2) && (args[0].equals("--replay"))) {
            new XHSI(Mode.REPLAY, args[1]);
        } else if ((args.length == 1) && (args[0].equals("--help"))) {
            display_usage_info();
        } else if ((args.length == 1) && (args[0].equals("--version"))) {
            System.out.println("Version of XHSI is " + XHSI.RELEASE);
        } else if (args.length == 0) {
            new XHSI(Mode.LIVE);
        } else {
            display_usage_info();
        }

    }


    public static void display_usage_info() {
        System.out.println(
        "Usage: java -jar XHSI.jar [--options]\n\n" +
        "where options include:\n" +
        "   --record <filename> [<frame_rate>] to record the current datastream\n" +
        "                                      received from X-Plane in the file\n" +
        "                                      <filename>. If <frame_rate>\n" +
        "                                      is given, records every <frame_rate>'th\n" +
        "                                      received data frame to save space.\n" +
        "   --replay <filename>                to replay the recording stored\n" +
        "                                      in <filename>\n" +
        "   --version                          to display the version of XHSI\n" +
        "   --help                             to display this help\n"
        );
    }


    public XHSI(Mode mode, String rec_file, int rec_rate) throws Exception {

        init();

        if (mode == Mode.RECORD) {

            logger.fine("recording flight session to '" + rec_file + "' ...");

            XPlaneFlightSessionRecorder recorder = new XPlaneFlightSessionRecorder(rec_file, rec_rate);
            XPlaneUDPReceiver udp_receiver = new XPlaneUDPReceiver( Integer.parseInt(preferences.get_preference(XHSIPreferences.PREF_PORT)) );
            udp_receiver.add_reception_observer(recorder);
            this.running_threads.add(recorder);
            recorder.start();
            this.running_threads.add(udp_receiver);
            udp_receiver.start();

        }

    }


    public XHSI(Mode mode, String filename) throws Exception {

        init();

        if (mode == Mode.REPLAY) {

            logger.fine("playing flight session recording from '" + filename + "' ...");

            XPlaneFlightSessionPlayer player = new XPlaneFlightSessionPlayer(filename, Long.parseLong(this.preferences.get_preference(XHSIPreferences.PREF_REPLAY_DELAY_PER_FRAME)));
            XPlaneDataPacketDecoder decoder = new XPlaneDataPacketDecoder(model_instance);
            player.add_sim_data_observer(decoder);
            this.running_threads.add(player);
            XPlaneSimDataRepository.replaying = true;
            XHSIStatus.status = XHSIStatus.STATUS_PLAYING_RECORDING;
            player.start();

            // thanks for your patience...
            for (int i=0; i<instruments.size(); i++) {
                XHSIInstrument duf = instruments.get(i);
                if ( duf.du.enabled() ) {
                    displayDUFrame(duf, true);
                }
            }

        }
    }


    public XHSI(Mode mode) throws Exception {
        
        init();

        if (mode == Mode.LIVE) {

            if ( this.preferences.get_preference(XHSIPreferences.PREF_SIMCOM).equals(XHSIPreferences.XHSI_PLUGIN) ) {

                // Communicating with X-Plane/XHSI_plugin
                XPlaneUDPSender udp_sender = new XPlaneUDPSender();
                XPlaneUDPReceiver udp_receiver = new XPlaneUDPReceiver( Integer.parseInt(preferences.get_preference(XHSIPreferences.PREF_PORT)) );
                XPlaneDataPacketDecoder decoder = new XPlaneDataPacketDecoder(model_instance);
                udp_receiver.add_reception_observer(decoder);
                XPlaneSimDataRepository.replaying = false;
                this.running_threads.add(udp_receiver);
                XHSIStatus.status = XHSIStatus.STATUS_RECEIVING;
                udp_receiver.start();

            } else if ( this.preferences.get_preference(XHSIPreferences.PREF_SIMCOM).equals(XHSIPreferences.SCS) ) {

                // Communicating with SCS
                XPlaneUDPSender udp_sender = new XPlaneUDPSender();
                XPlaneUDPReceiver udp_receiver = new XPlaneUDPReceiver( Integer.parseInt(preferences.get_preference(XHSIPreferences.PREF_PORT)) );
                XPlaneDataPacketDecoder decoder = new XPlaneDataPacketDecoder(model_instance);
                udp_receiver.add_reception_observer(decoder);
                XPlaneSimDataRepository.replaying = false;
                this.running_threads.add(udp_receiver);
                XHSIStatus.status = XHSIStatus.STATUS_RECEIVING;
                udp_receiver.start();

            }

            // thanks for your patience...
            for (int i=0; i<instruments.size(); i++) {
                XHSIInstrument duf = instruments.get(i);
                if ( duf.du.enabled() ) {
                    displayDUFrame(duf, true);
                }
            }

        } 
    }


    private void init() throws Exception {

        this.running_threads = new ArrayList();

        // load properties and create a new properties file, if none exists
        this.preferences = XHSIPreferences.get_instance();

        // set loglevel
        logger.config("Selected loglevel: " + this.preferences.get_preference(XHSIPreferences.PREF_LOGLEVEL));
        logger.setLevel(Level.parse(this.preferences.get_preference(XHSIPreferences.PREF_LOGLEVEL)));

        // get our IP-address for the About... box
        this.ip_host = InetAddress.getLocalHost();

        this.instruments = new ArrayList<XHSIInstrument>();
        for ( XHSIInstrument.DU du : XHSIInstrument.DU.values() ) {
            XHSIInstrument new_du = new XHSIInstrument( du );
            new_du.du.activate( this.preferences.get_panel_active( du.get_id() ) );
            this.instruments.add( new_du );
        }


        // create a sim model with Aircraft, Avionics, SimDataRepository, etc...
        model_instance = new XPlaneModelFactory();

        // create user interface
        create_UI();

        PFDComponent pfd_ui = null;
        NDComponent nd_ui = null;
        EICASComponent eicas_ui = null;
        MFDComponent mfd_ui = null;

        // some preferences require a reconfiguration
        for (int i=0; i<instruments.size(); i++) {
            switch (instruments.get(i).get_index()) {
                case XHSIInstrument.EMPTY_ID :
                    // Empty
                    EmptyComponent empty_ui = (EmptyComponent)instruments.get(i).components;
                    this.preferences.add_subsciption(empty_ui, XHSIPreferences.PREF_DU_PREPEND);
                    break;
                case XHSIInstrument.PFD_ID :
                    // PFD
                    pfd_ui = (PFDComponent)instruments.get(i).components;
                    this.preferences.add_subsciption(pfd_ui, XHSIPreferences.PREF_BOLD_FONTS);
                    this.preferences.add_subsciption(pfd_ui, XHSIPreferences.PREF_USE_MORE_COLOR);
                    this.preferences.add_subsciption(pfd_ui, XHSIPreferences.PREF_ANTI_ALIAS);
                    this.preferences.add_subsciption(pfd_ui, XHSIPreferences.PREF_DU_PREPEND);
                    this.preferences.add_subsciption(pfd_ui, XHSIPreferences.PREF_USE_POWER);
                    this.preferences.add_subsciption(pfd_ui, XHSIPreferences.PREF_PFD_DRAW_HSI);
                    break;
                case XHSIInstrument.ND_ID :
                    // ND
                    nd_ui = (NDComponent)instruments.get(i).components;
                    this.preferences.add_subsciption(nd_ui, XHSIPreferences.PREF_BOLD_FONTS);
                    this.preferences.add_subsciption(nd_ui, XHSIPreferences.PREF_USE_MORE_COLOR);
                    this.preferences.add_subsciption(nd_ui, XHSIPreferences.PREF_ANTI_ALIAS);
                    this.preferences.add_subsciption(nd_ui, XHSIPreferences.PREF_DU_PREPEND);
                    this.preferences.add_subsciption(nd_ui, XHSIPreferences.PREF_USE_POWER);
                    this.preferences.add_subsciption(nd_ui, XHSIPreferences.PREF_AIRBUS_MODES);
                    this.preferences.add_subsciption(nd_ui, XHSIPreferences.PREF_CLASSIC_HSI);
                    this.preferences.add_subsciption(nd_ui, XHSIPreferences.PREF_APPVOR_UNCLUTTER);
                    break;
                case XHSIInstrument.EICAS_ID :
                    // EICAS
                    eicas_ui = (EICASComponent)instruments.get(i).components;
                    this.preferences.add_subsciption(eicas_ui, XHSIPreferences.PREF_BOLD_FONTS);
                    this.preferences.add_subsciption(eicas_ui, XHSIPreferences.PREF_USE_MORE_COLOR);
                    this.preferences.add_subsciption(eicas_ui, XHSIPreferences.PREF_ANTI_ALIAS);
                    this.preferences.add_subsciption(eicas_ui, XHSIPreferences.PREF_DU_PREPEND);
                    this.preferences.add_subsciption(eicas_ui, XHSIPreferences.PREF_USE_POWER);
                    break;
                case XHSIInstrument.MFD_ID :
                    // EFB
                    mfd_ui = (MFDComponent)instruments.get(i).components;
                    this.preferences.add_subsciption(mfd_ui, XHSIPreferences.PREF_BOLD_FONTS);
                    this.preferences.add_subsciption(mfd_ui, XHSIPreferences.PREF_USE_MORE_COLOR);
                    this.preferences.add_subsciption(mfd_ui, XHSIPreferences.PREF_ANTI_ALIAS);
                    this.preferences.add_subsciption(mfd_ui, XHSIPreferences.PREF_DU_PREPEND);
                    this.preferences.add_subsciption(mfd_ui, XHSIPreferences.PREF_USE_POWER);
                    break;
                case XHSIInstrument.ANNUN_ID :
                    // Annunciators
                    AnnunComponent annun_ui = (AnnunComponent)instruments.get(i).components;
                    this.preferences.add_subsciption(annun_ui, XHSIPreferences.PREF_BOLD_FONTS);
                    this.preferences.add_subsciption(annun_ui, XHSIPreferences.PREF_ANTI_ALIAS);
                    this.preferences.add_subsciption(annun_ui, XHSIPreferences.PREF_DU_PREPEND);
                    this.preferences.add_subsciption(annun_ui, XHSIPreferences.PREF_USE_POWER);
                    break;
                case XHSIInstrument.CLOCK_ID :
                    // Clock
                    ClockComponent clock_ui = (ClockComponent)instruments.get(i).components;
                    this.preferences.add_subsciption(clock_ui, XHSIPreferences.PREF_BOLD_FONTS);
                    this.preferences.add_subsciption(clock_ui, XHSIPreferences.PREF_USE_MORE_COLOR);
                    this.preferences.add_subsciption(clock_ui, XHSIPreferences.PREF_ANTI_ALIAS);
                    this.preferences.add_subsciption(clock_ui, XHSIPreferences.PREF_DU_PREPEND);
                    this.preferences.add_subsciption(clock_ui, XHSIPreferences.PREF_USE_POWER);
                    break;
            }
        }

        // XHSISettings in ConWin
        this.preferences.add_subsciption(XHSISettings.get_instance(), XHSIPreferences.PREF_AIRBUS_MODES);

        // load AptNav databases
        AptNavXP900DatNavigationObjectBuilder nob = new AptNavXP900DatNavigationObjectBuilder(this.preferences.get_preference(XHSIPreferences.PREF_APTNAV_DIR));
        this.preferences.add_subsciption(nob, XHSIPreferences.PREF_APTNAV_DIR);
        nob.set_progress_observer((ProgressObserver) this.nob_progress_dialog);
        if ( ! XHSIStatus.nav_db_status.equals(XHSIStatus.STATUS_NAV_DB_NOT_FOUND) ) {
            nob.read_all_tables();
            XHSIStatus.nav_db_status = XHSIStatus.STATUS_NAV_DB_LOADED;
        }

//// test load TaxiChart
//AptNavXP900DatTaxiChartBuilder taxi = new AptNavXP900DatTaxiChartBuilder(this.preferences.get_preference(XHSIPreferences.PREF_APTNAV_DIR));
//taxi.get_chart("YMML");
        
        // add components update watchdog
        UIHeartbeat ui_heartbeat = new UIHeartbeat(this.xhsi_ui, pfd_ui, nd_ui, eicas_ui, mfd_ui, 1000);
        ui_heartbeat.start();
        this.running_threads.add(ui_heartbeat);

    }


    private void shutdown_threads() {

        StoppableThread thread;

        XHSIStatus.status = XHSIStatus.STATUS_SHUTDOWN;

        for (int i=0;i<this.running_threads.size();i++) {
            thread = (StoppableThread) this.running_threads.get(i);
            thread.signal_stop();
            try {
                thread.join(1000);
            } catch (Exception e) {
                logger.warning("Could not shutdown thread. (" + e.toString());
            }
        }

    }


    private boolean isMac() {
        return (System.getProperty("mrj.version") != null);
    }


    private void create_UI() throws Exception {

        boolean ui_specialization = false;
        
        // Sorry, Java is about being platform-independent; I disable the creation of Mac look and feel
        // at least until it can be added again without having to maintain any extra code

//        if (isMac()) {
//            logger.config("Mac detected. Create Menubar with Mac look and feel");
//
//            System.setProperty("apple.laf.useScreenMenuBar", "true");
//            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "XHSI_PLUGIN");
//
//            // try to load apple specific classes dynamically in order to avoid
//            // compilation problems on non-mac platforms
//            try {
//                Class Application = Class.forName("com.apple.eawt.Application");
//                Class ApplicationListener = Class.forName("com.apple.eawt.ApplicationListener");
//                Class ApplicationEvent = Class.forName("com.apple.eawt.ApplicationEvent");
//
//                Method getApplication = Application.getMethod("getApplication", new Class[0]);
//                Method addApplicationListener = Application.getMethod("addApplicationListener", new Class[] { ApplicationListener });
//                final Method setHandled = ApplicationEvent.getMethod("setHandled", new Class[] { Boolean.TYPE });
//                Method setEnabledPreferencesMenu = Application.getMethod("setEnabledPreferencesMenu", new Class[] { Boolean.TYPE });
//
//                InvocationHandler listenerHandler = new InvocationHandler() {
//                    public Object invoke(Object proxy, Method method, Object[] args) {
//                        String name = method.getName();
//                        if (name.equals("handleAbout")) {
//                            actionPerformed(new ActionEvent(this,0, XHSI_PLUGIN.ACTION_ABOUT));
//                        } else if (name.equals("handlePreferences")) {
//                            actionPerformed(new ActionEvent(this,0, XHSI_PLUGIN.ACTION_PREFERENCES));
//                        } else if (name.equals("handleQuit")) {
//                            actionPerformed(new ActionEvent(this,0, XHSI_PLUGIN.ACTION_QUIT));
//                        } else {
//                            return null;
//                        }
//
//                        try {
//                            setHandled.invoke(args[0], new Object[] { Boolean.TRUE });
//                        } catch (Exception ex) {
//                            // Ignore
//                        }
//                        return null;
//                    }
//                };
//
//                Object application = getApplication.invoke(null, (Object[]) null);
//                setEnabledPreferencesMenu.invoke(application, new Object[] { Boolean.TRUE });
//                Object listener = Proxy.newProxyInstance(XHSI_PLUGIN.class.getClassLoader(),
//                                                          new Class[] { ApplicationListener },
//                                                          listenerHandler);
//                addApplicationListener.invoke(application, new Object[] { listener });
//
//            } catch (Exception e) {
//                logger.warning("Could not create Mac specific UI! (" + e.toString() + ")");
//                ui_specialization = false;
//            }
//        }


        // XHSI_PLUGIN master instrument_window =================================================
        this.xhsi_frame = new JFrame("XHSI " + XHSI.RELEASE);

        // Exit on Close, otherwise the instrument_window will close, but java will still be running
        this.xhsi_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // an icon for the instrument_window title bar and task bar
        this.xhsi_frame.setIconImage(this.logo_image);

        if ((isMac() == false) || (ui_specialization == false)) {
            this.xhsi_frame.setJMenuBar(createMenu());
        }

        this.xhsi_ui = new ConWinComponent( model_instance );
        model_instance.get_repository_instance().add_observer(this.xhsi_ui);
        
        this.xhsi_frame.getContentPane().add(this.xhsi_ui);
        this.xhsi_frame.pack();
        this.xhsi_frame.setBackground( Color.WHITE );
        this.xhsi_frame.setMinimumSize( new Dimension(500, 70) );
        this.xhsi_frame.setAlwaysOnTop( this.preferences.get_start_ontop() );
        if ( this.preferences.get_conwin_minimized() ) this.xhsi_frame.setExtendedState(Frame.ICONIFIED);
        this.xhsi_frame.setVisible(true);


        // Each of the instrument windows
        for (int i=0; i<instruments.size(); i++) {

            XHSIInstrument instrument_window = instruments.get(i);
            int du_num = instrument_window.get_index();

            instrument_window.frame = new JFrame( instrument_window.get_description() );
            instrument_window.frame.setUndecorated( this.preferences.get_hide_window_frames() );

            // Exit on Close, otherwise the instrument_window will close, but java will still be running
//            instrument_window.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // could it be faster with enableInputMethods(false)?
            instrument_window.frame.enableInputMethods(false);

            // an icon for the instrument_window title bar and task bar
            instrument_window.frame.setIconImage(this.logo_image);

            boolean min_size = false;

            instrument_window.components = null;
//logger.warning("Adding component "+instrument_window.get_index());
            switch (instrument_window.get_index()) {
                case XHSIInstrument.EMPTY_ID :
                    instrument_window.components = new EmptyComponent(model_instance, du_num);
                    model_instance.get_repository_instance().add_observer( (EmptyComponent)instrument_window.components );
                    min_size = true;
                    break;
                case XHSIInstrument.PFD_ID :
                    instrument_window.components = new PFDComponent(model_instance, du_num);
                    model_instance.get_repository_instance().add_observer( (PFDComponent)instrument_window.components );
                    min_size = true;
                    break;
                case XHSIInstrument.ND_ID :
                    instrument_window.components = new NDComponent(model_instance, du_num);
                    model_instance.get_repository_instance().add_observer( (NDComponent)instrument_window.components );
                    min_size = true;
                    break;
                case XHSIInstrument.EICAS_ID :
                    instrument_window.components = new EICASComponent(model_instance, du_num);
                    model_instance.get_repository_instance().add_observer( (EICASComponent)instrument_window.components );
                    min_size = true;
                    break;
                case XHSIInstrument.MFD_ID :
                    instrument_window.components = new MFDComponent(model_instance, du_num);
                    model_instance.get_repository_instance().add_observer( (MFDComponent)instrument_window.components );
                    min_size = true;
                    break;
                case XHSIInstrument.ANNUN_ID :
                    instrument_window.components = new AnnunComponent(model_instance, du_num);
                    model_instance.get_repository_instance().add_observer( (AnnunComponent)instrument_window.components );
                    min_size = true;
                    break;
                case XHSIInstrument.CLOCK_ID :
                    instrument_window.components = new ClockComponent(model_instance, du_num);
                    model_instance.get_repository_instance().add_observer( (ClockComponent)instrument_window.components );
                    min_size = true;
                    break;
            }

            if ( instrument_window.components != null ) {
                instrument_window.frame.getContentPane().add(instrument_window.components);
            }

            if ( this.preferences.get_panels_locked() ) {
                instrument_window.frame.setBounds(
                        this.preferences.get_panel_pos_x( du_num ),
                        this.preferences.get_panel_pos_y( du_num ),
                        this.preferences.get_panel_width( du_num ),
                        this.preferences.get_panel_height( du_num )
                    );
            } else {
                instrument_window.frame.pack();
                instrument_window.frame.setLocation(instrument_window.frame.getX() + du_num * 20 + 20, instrument_window.frame.getY() + du_num * 20 + 20);
                if ( instrument_window.components != null ) {
                    instrument_window.frame.setSize( instrument_window.components.getPreferredSize() );
                }
            }
            instrument_window.frame.setBackground(Color.BLACK);

            // set minimum size
            if ( min_size ) {
//                if ( this.preferences.get_hide_window_frames() ) {
                    instrument_window.frame.setMinimumSize(new Dimension(instrument_window.du.get_min_width(), instrument_window.du.get_min_width()));
//                } else {
//                    // reserve some extra space for the title bar (it's platform dependent, so this is only an estimate)
//                    instrument_window.frame.setMinimumSize(new Dimension(340, 340 + 20));
//                }
            }

            //instrument_window.frame.setIgnoreRepaint(true);
            instrument_window.frame.setAlwaysOnTop(this.preferences.get_start_ontop());
            // much later or not?: instrument_window.frame.setVisible(true);
            instrument_window.frame.setVisible( instrument_window.du.enabled() );

        }


        // Preferences dialog
        this.preferences_dialog = new PreferencesDialog(this.xhsi_frame, this.instruments);
        this.nob_progress_dialog = new ProgressDialog(this.xhsi_frame);

        // define the frames for other dialog windows
        XHSISettings.get_instance().init_frames(this.xhsi_frame);

    }


    private void displayDUFrame(XHSIInstrument window, boolean display) {

//        if ( window.du.enabled() ) {

            switch (window.get_index()) {
                case XHSIInstrument.EMPTY_ID :
                    ((EmptyComponent)window.components).forceReconfig();
                    break;
                case XHSIInstrument.PFD_ID :
                    ((PFDComponent)window.components).forceReconfig();
                    break;
                case XHSIInstrument.ND_ID :
                    ((NDComponent)window.components).forceReconfig();
                    break;
                case XHSIInstrument.EICAS_ID :
                    ((EICASComponent)window.components).forceReconfig();
                    break;
                case XHSIInstrument.MFD_ID :
                    ((MFDComponent)window.components).forceReconfig();
                    break;
                case XHSIInstrument.ANNUN_ID :
                    ((AnnunComponent)window.components).forceReconfig();
                    break;
            }
    //        window.frame.setVisible(display);
            //this.pilot_nd_frame.setIgnoreRepaint(true);
            //this.pilot_nd_frame.repaint();
            window.frame.repaint();

//        }

    }


    private  JMenuBar createMenu() {

        JMenuItem menu_item;

        // define the menubar
        JMenuBar menu_bar = new JMenuBar();

        // define the "XHSI_PLUGIN" menu
        JMenu main_xhsi_menu = new JMenu("  XHSI    ");
        main_xhsi_menu.setMnemonic(KeyEvent.VK_X);

        // define the menu items, and add them to the "XHSI_PLUGIN" menu
        menu_item = new JMenuItem(XHSI.ACTION_ABOUT);
        menu_item.setToolTipText("Credits, our IP address, etc...");
        menu_item.addActionListener(this);
        menu_item.setMnemonic(KeyEvent.VK_A);
        main_xhsi_menu.add(menu_item);

        main_xhsi_menu.addSeparator();

        menu_item = new JMenuItem(XHSI.ACTION_PREFERENCES);
        menu_item.setToolTipText("Preferences dialog box");
        menu_item.addActionListener(this);
        menu_item.setMnemonic(KeyEvent.VK_P);
        main_xhsi_menu.add(menu_item);

        main_xhsi_menu.addSeparator();

        menu_item = new JCheckBoxMenuItem(XHSI.ACTION_ONTOP);
        menu_item.setToolTipText("Keep our windows in the foreground");
        menu_item.addActionListener(this);
        menu_item.setMnemonic(KeyEvent.VK_W);
        menu_item.setSelected(this.preferences.get_start_ontop());
        main_xhsi_menu.add(menu_item);
        main_xhsi_menu.addSeparator();

        menu_item = new JMenuItem(XHSI.ACTION_QUIT);
        menu_item.setToolTipText("Bye!");
        menu_item.setMnemonic(KeyEvent.VK_Q);
        menu_item.addActionListener(this);
        main_xhsi_menu.add(menu_item);

        // add the "XHSI_PLUGIN" menu to the menubar
        menu_bar.add(main_xhsi_menu);

        // add the settings menus to this bar...
        XHSISettings.get_instance().create_menu(menu_bar);

        return menu_bar;
    }


    public void actionPerformed(ActionEvent event) {

        String command = event.getActionCommand();

        if (command.equals(ACTION_QUIT)) {
            logger.fine("stopping threads");
            shutdown_threads();
            logger.fine("clean exit from threads");
            System.exit(0);
        } else if (command.equals(ACTION_PREFERENCES)) {
            // choose X-Plane directory etc
            this.preferences_dialog.setLocation(this.xhsi_frame.getX()+20, this.xhsi_frame.getY()+60);
            this.preferences_dialog.setVisible(true);
            this.preferences_dialog.pack();
        } else if (command.equals(ACTION_ONTOP)) {
            // keep windows always on top, or not
            this.xhsi_frame.setAlwaysOnTop( ! this.xhsi_frame.isAlwaysOnTop() );
            for (int i=0; i<instruments.size(); i++) {
                instruments.get(i).frame.setAlwaysOnTop( ! instruments.get(i).frame.isAlwaysOnTop() );
            }
        } else if (command.equals(ACTION_ABOUT)) {
            JOptionPane.showMessageDialog(this.xhsi_frame,
                    "XHSI " + XHSI.RELEASE + "\n" +
                    "\n" +
                    "XHSI - eXternal High-fidelity Simulator Instruments for X-Plane\n" +
                    "  PFD - Primary Flight Display\n" +
                    "  ND - Navigation Display\n" +
                    "  EICAS - Engine Instruments\n" +
                    "  MFD - Airport Chart / Flight Plan / Lower EICAS\n" +
                    "  Clock / Chronometer\n" +
                    "  Annunciators - Gear, Flaps, etc...\n" +
                    "\n" +
                    "http://xhsi.sourceforge.net\n" +
                    "\n" +
                    "Main contributors:\n" +
                    "2007-2009 Georg Gruetter\n" +
                    "2009 Sandy Barbour\n" +
                    "2009-2013 Marc Rogiers\n" +
                    "\n" +
                    "running on " + this.ip_host,
                    "About XHSI",
                    JOptionPane.INFORMATION_MESSAGE);

        }

    }


}
