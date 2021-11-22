/**
 * Sunseeker Telemetry
 *
 * @author Alec Carpenter <alecgunnar@gmail.com>
 * @date July 19, 2016
 */

package App.Profile.Loader.GUI;

import App.Profile.Loader.GUI.Observer.ProfileLoaderGUIObserverInterface;
import Data.Source.Collection.DataSourceCollectionInterface;
import Data.Source.DataSourceInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProfileLoaderGUI extends JFrame implements ActionListener {
    final protected int FRAME_WIDTH  = 300;
    final protected int FRAME_HEIGHT = 150;

    /*
     * The data sources we have available
     */
    protected DataSourceCollectionInterface dataSources;

    /*
     * Who cares to know?
     */
    protected ProfileLoaderGUIObserverInterface observer;

    /*
     * Frame title
     */
    final protected String FRAME_TITLE = "Choose a Profile";

    /*
     * Title of the file chooser dialog
     */
    final protected String FILE_DIALOG_TITLE = "Choose where to save to";

    /*
     * Default data source option
     */
    final protected String OPTION_DEFAULT_DATA_SOURCE = "Choose a data source";

    /*
     * Profile source options
     */
    final protected String CHOICE_CREATE = "Create a new profile";
    final protected String CHOICE_LOAD   = "Load a saved profile";

    final protected String[] PROFILE_LOAD_OPTIONS = {
        CHOICE_CREATE,
        CHOICE_LOAD
    };

    /*
     * Button texts
     */
    protected String BUTTON_LOAD_FILE = "Choose a File";
    final protected String BUTTON_CONTINUE  = "Continue";
    final protected String BUTTON_QUIT      = "Quit";

    final protected String CHECKBOX_AUTO_SAVE = "Auto Save";

    /*
     * Errors
     */
    final protected String ERROR_TITLE           = "Error!";
    final protected String ERROR_CHOOSE_A_FILE   = "You must choose a file before you can continue.";
    final protected String ERROR_CHOOSE_A_SOURCE = "You must choose a data source before you can continue.";

    /*
     * Data sources from which to pull data
     */
    protected List<String> dataSourcesOptions;

    /*
     * Choosen values
     */
    protected File loadProfileFrom;
    protected DataSourceInterface createProfileUsing;
    protected boolean autoSave;

    /*
     * Frame components
     */
    protected JComboBox loadOrCreate;
    protected JPanel optionsPanel;
    protected JButton loadProfile;

    public ProfileLoaderGUI (DataSourceCollectionInterface dataSources, ProfileLoaderGUIObserverInterface observer) {
        /*
         * Set the title of the frame
         */
        setTitle(FRAME_TITLE);

        /*
         * Set the size of the frame
         */
        setSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));

        /*
         * This frame cannot be resized
         */
        setResizable(false);

        /*
         * Set the layout for the frame
         */
        setLayout(new FlowLayout());

        /*
         * Quit the application if this window is closed
         */
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        /*
         * Compile a list of data sources to be presented as options
         */
        loadDataSourceOptions(dataSources);

        /*
         * Add the option to create or load a profile
         */
        addOptionToChooseLoadOrCreate();

        /*
         * Add the options to load a profile
         */
        addLoadAndCreateOptions();

        addAutoSaveCheckbox();

        /*
         * Add the options to create a profile
         */
        addOptionsToContinueOrQuit();

        this.observer = observer;
    }

    public void prompt () {
        setVisible(true);
    }

    public void actionPerformed (ActionEvent e) {
        String choice     = (String) loadOrCreate.getSelectedItem();
        CardLayout layout = (CardLayout) optionsPanel.getLayout();

        layout.show(optionsPanel, choice);
    }

    protected void loadDataSourceOptions (DataSourceCollectionInterface dataSources) {
        this.dataSources = dataSources;

        dataSourcesOptions = new ArrayList<String>();

        dataSourcesOptions.add(OPTION_DEFAULT_DATA_SOURCE);

        for (DataSourceInterface dataSource : dataSources.values())
            dataSourcesOptions.add(dataSource.getName());
    }

    protected void addOptionToChooseLoadOrCreate () {
        loadOrCreate = new JComboBox<String>(PROFILE_LOAD_OPTIONS);

        add(loadOrCreate);

        loadOrCreate.addActionListener(this);
    }

    protected void addLoadAndCreateOptions () {
        CardLayout layout = new CardLayout();
        optionsPanel = new JPanel(layout);

        optionsPanel.setSize(new Dimension(FRAME_WIDTH, 50));

        addOptionsToLoad();
        addOptionsToCreate();

        layout.show(optionsPanel, PROFILE_LOAD_OPTIONS[0]);

        add(optionsPanel);
    }

    protected void addOptionsToLoad () {
        JPanel card = new JPanel(new BorderLayout());

        loadProfile = new JButton(BUTTON_LOAD_FILE);

        loadProfile.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                FileDialog fileChooser = new FileDialog(ProfileLoaderGUI.this, FILE_DIALOG_TITLE, FileDialog.LOAD);

                fileChooser.setVisible(true);

                File[] files = fileChooser.getFiles();

                if (files.length > 0) {
                    ProfileLoaderGUI.this.loadProfileFrom = files[0];

                    loadProfile.setText(files[0].toString());
                }
            }
        });

        card.add(loadProfile);

        optionsPanel.add(card, CHOICE_LOAD);
    }

    protected void addOptionsToCreate () {
        JPanel card = new JPanel();

        String[] options = new String[dataSourcesOptions.size()];
        dataSourcesOptions.toArray(options);

        JComboBox chooseDataSource = new JComboBox<String>(options);

        chooseDataSource.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                String choice = (String) chooseDataSource.getSelectedItem();

                // If user didn't change the choice from default, do nothing
                createProfileUsing = !choice.equals(OPTION_DEFAULT_DATA_SOURCE)
                    ? dataSources.get(choice)
                    : null;
            }
        });

        card.add(chooseDataSource);

//        JCheckBox autoSaveBox = new JCheckBox(CHECKBOX_AUTO_SAVE);
//
//        autoSaveBox.addActionListener(new ActionListener() {
//            public void actionPerformed (ActionEvent e) {
//                autoSave = autoSaveBox.isSelected();
//            }
//        });
//
//        card.add(autoSaveBox);

        optionsPanel.add(card, CHOICE_CREATE);
    }

    protected void addOptionsToContinueOrQuit () {
        addContinueButton();
        addQuitButton();
    }

    protected void addContinueButton () {
        JButton button = new JButton(BUTTON_CONTINUE);

        /*
         * When valid let the observer know otherwise, show an error
         */
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String error = null;

                setVisible(false);

                switch ((String) loadOrCreate.getSelectedItem()) {
                    case CHOICE_LOAD:
                        if (loadProfileFrom != null) {
                            observer.loadSaved(loadProfileFrom);
                        } else {
                            error = ERROR_CHOOSE_A_FILE;
                        }
                        break;

                    case CHOICE_CREATE:
                        if (createProfileUsing != null) {
                            observer.createNew(createProfileUsing, autoSave);
                        } else {
                            error = ERROR_CHOOSE_A_SOURCE;
                        }
                        break;
                }

                if (error != null) {
                    JOptionPane.showMessageDialog(ProfileLoaderGUI.this,
                        error,
                        ERROR_TITLE,
                        JOptionPane.WARNING_MESSAGE);

                    prompt();
                }
            }
        });

        add(button, BorderLayout.SOUTH);
    }

    protected void addQuitButton () {
        JButton button = new JButton(BUTTON_QUIT);

        /*
         * Quit the app when this button is clicked
         */
        button.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                System.exit(0);
            }
        });

        add(button, BorderLayout.SOUTH);
    }

    protected void addAutoSaveCheckbox() {
        JCheckBox autoSaveBox = new JCheckBox(CHECKBOX_AUTO_SAVE);

        autoSaveBox.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                autoSave = autoSaveBox.isSelected();
            }
        });

        add(autoSaveBox);
    }

    protected void resetFrame () {
        /*
         * Get rid of all components in the frame
         */
        getContentPane().removeAll();
    }
}
