/**
 * Sunseeker Telemetry
 *
 * @author Alec Carpenter <alecgunnar@gmail.com>
 * @date July 16, 2016
 */

package Data.Processor;

import Data.Processor.Observer.DataProcessorObserverInterface;
import Serial.Listener.Observer.ListenerObserverInterface;

public interface DataProcessorInterface extends ListenerObserverInterface {
    public void addObserver (DataProcessorObserverInterface observer);
}
