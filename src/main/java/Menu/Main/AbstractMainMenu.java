/**
 * Sunseeker Telemetry
 *
 * @author Alec Carpenter <alecgunnar@gmail.com>
 * @date July 21, 2016
 */

package Menu.Main;

import Menu.AbstractMenu;
import Menu.Main.Observer.MainMenuObserverInterface;

import java.util.List;
import java.util.ArrayList;

public abstract class AbstractMainMenu extends AbstractMenu {
    protected List<MainMenuObserverInterface> observers;

    public AbstractMainMenu () {
        observers = new ArrayList<MainMenuObserverInterface>();
    }

    public void addObserver (MainMenuObserverInterface observer) {
        observers.add(observer);
    }

    public void notifyShouldSave () {
        for (MainMenuObserverInterface observer : observers)
            observer.doSaveProfile();
    }

    public void notifyShouldEdit () {
        for (MainMenuObserverInterface observer : observers)
            observer.doEditProfile();
    }
}