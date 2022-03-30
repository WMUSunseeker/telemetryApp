/**
 * Sunseeker Telemetry
 *
 * @author Alec Carpenter <alecgunnar@gmail.com>
 * @date July 9, 2016
 */

package Data.Source;

import Data.Processor.DataProcessorInterface;
import Data.Processor.GenericDataProcessor;
import Data.Processor.Observer.DataProcessorObserverInterface;
import Serial.Connection.ModemConnection;
import Serial.Listener.GenericListener;
import Serial.Listener.ListenerInterface;
import Serial.SerialClient;

public class TenCarDataSource extends AbstractSerialDataSource implements DataProcessorObserverInterface {
    /*
     * Data values output by this source
     */
    final protected String BP_VMAX = "BP_VMX";
    final protected String BP_VMIN = "BP_VMN";
    final protected String BP_TMX  = "BP_TMX";
    final protected String BP_ISH  = "BP_ISH";

    final protected String MC1_VEL = "MC1VEL";
    final protected String MC2_VEL = "MC2VEL";
    final protected String MC1_TP1 = "MC1TP1";

    public String getName () {
        return "2010 Sunseeker Solar Car";
    }

    protected void registerDataTypes () {
        registerDataMapping(
            BP_VMAX,
            registerDataType("Max. Cell Voltage", "Volts"),
            null
        );

        registerDataMapping(
            BP_VMIN,
            registerDataType("Min. Cell Voltage", "Volts"),
            null
        );

        registerDataMapping(
            BP_TMX,
            registerDataType("Max. Cell Temp.", "C"),
            null
        );

        registerDataMapping(
            BP_ISH,
            registerDataType("Shunt Current", "Amps"),
            null
        );

        registerDataMapping(
            MC1_VEL,
            registerDataType("Motor Controller 1 Speed", "m/s"),
            registerDataType("Motor Controller 1 Rotations", "rpm")
        );

        registerDataMapping(
            MC2_VEL,
            registerDataType("Motor Controller 2 Speed", "m/s"),
            registerDataType("Motor Controller 2 Rotations", "rpm")
        );

        registerDataMapping(
            MC1_TP1,
            registerDataType("Motor Controller 1 Heatsink Temp.", "C"),
            registerDataType("Motor Controller 1 Motor Temp.", "C")
        );
    }

    protected SerialClient getClient () {
        DataProcessorInterface processor = new GenericDataProcessor();
        processor.addObserver(this);

        ListenerInterface listener = new GenericListener();
        listener.addObserver(processor);

        return new SerialClient(new ModemConnection(), listener);
    }

    protected int getNumDataPoints() {
        return 0;
    }
}
