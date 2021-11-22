/**
 * Sunseeker Telemetry
 *
 * @author Alec Carpenter <alecgunnar@gmail.com>
 * @date July 19, 2016
 */

package App.Profile.Loader.GUI.Observer;

import Data.Source.DataSourceInterface;

import java.io.File;

public interface ProfileLoaderGUIObserverInterface {
    public void loadSaved(File file);

    public void createNew(DataSourceInterface dataSource, boolean autoSave);

    public void canceled();
}
