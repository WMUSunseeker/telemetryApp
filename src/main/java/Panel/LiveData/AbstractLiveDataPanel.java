/**
 * Sunseeker Telemetry
 *
 * @author Alec Carpenter <alecgunnar@gmail.com>
 * @date July 2, 2016
 */

package Panel.LiveData;

import Data.Type.Collection.DataTypeCollectionInterface;
import Panel.AbstractPanel;

public abstract class AbstractLiveDataPanel extends AbstractPanel {
    protected DataTypeCollectionInterface types;
    
    public void setTypes (DataTypeCollectionInterface types) {
        this.types = types;
    }

    abstract public void refresh();
}
