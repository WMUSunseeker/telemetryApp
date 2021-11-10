/**
 * Sunseeker Telemetry
 *
 * @author Alec Carpenter <alecgunnar@gmail.com>
 * @date July 21, 2016
 */

package Menu.Main;

import Frame.Error.AbstractErrorFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu extends AbstractMainMenu {
    /*
     * Menu labels
     */
    final protected String MENU_PROFILE = "Profile";
    final protected String MENU_ERROR = "Errors & Lims";

    /*
     * Menu option labels
     */
    final protected String LABEL_SAVE_PROFILE = "Save Profile";
    final protected String LABEL_EDIT_PROFILE = "Edit Profile";
//    final protected String LABEL_LOAD_PROFILE = "Load Profile";

    final protected String LABEL_ERROR_FRAME = "Open Error Panel";

    protected AbstractErrorFrame errorFrame;

    public MainMenu () {
        addProfileMenu();
    }

    protected void addProfileMenu () {
        JMenu menu = new JMenu(MENU_PROFILE);

        /*
         * Add menu items to the menu
         */
        JMenuItem editProfile = new JMenuItem(LABEL_EDIT_PROFILE);
        menu.add(editProfile);

        JMenuItem saveProfile = new JMenuItem(LABEL_SAVE_PROFILE);
        menu.add(saveProfile);

        /*
         * Trigger the observers when the menu items are clicked
         */
        editProfile.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                MainMenu.this.notifyShouldEdit();
            }
        });
        
        saveProfile.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                MainMenu.this.notifyShouldSave();
            }
        });

        JMenu errorMenu = new JMenu(MENU_ERROR);

        JMenuItem openErrors = new JMenuItem(LABEL_ERROR_FRAME);
        errorMenu.add(openErrors);

        openErrors.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                if (MainMenu.this.errorFrame != null) {
                    MainMenu.this.errorFrame.setVisible(true);
                }
            }
        });

        /*
         * Add the menu to the bar
         */
        add(menu);
        add(errorMenu);
    }

    public void addErrorFrame(AbstractErrorFrame errorFrame) {
        this.errorFrame = errorFrame;
    }
}