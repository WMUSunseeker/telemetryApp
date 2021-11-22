/**
 * Sunseeker Telemetry
 *
 * @author Alec Carpenter <alecgunnar@gmail.com>
 * @date July 18, 2016
 */

package App.Profile;

import Data.Source.DataSourceInterface;
import Data.Type.DataTypeInterface;

import java.io.File;

public interface ProfileInterface {
    public DataSourceInterface getDataSource();

    public void updateDataType(DataTypeInterface dataType);

    public void setFile(File file);

    public File getFile();

    public boolean hasChanged();

    public boolean getAutoSave();

    public String getFileName();
}
