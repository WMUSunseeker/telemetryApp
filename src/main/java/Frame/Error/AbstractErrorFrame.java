package Frame.Error;

import Frame.AbstractFrame;
import Panel.Error.AbstractErrorPanel;

public abstract class AbstractErrorFrame extends AbstractFrame {

    final protected String FRAME_TITLE = "Error States";

    /*
     * Frame dimensions
     */
    final public static int FRAME_WIDTH  = 500;
    final public static int FRAME_HEIGHT = 400;

    public abstract void useErrorPanel(AbstractErrorPanel panel);
}
