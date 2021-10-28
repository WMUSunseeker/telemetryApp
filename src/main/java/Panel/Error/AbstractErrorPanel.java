package Panel.Error;

import Data.Type.Collection.DataTypeCollectionInterface;
import Data.Type.Collection.ErrorTypeCollection;
import Panel.AbstractPanel;

public abstract class AbstractErrorPanel extends AbstractPanel {
    protected ErrorTypeCollection types;
//
    public void setTypes (ErrorTypeCollection types) {
        this.types = types;
    }

    public abstract void refresh();
}
