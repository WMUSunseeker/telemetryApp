package Data.Source;

import Data.Processor.DataProcessorInterface;
import Data.Processor.GenericDataProcessor;
import Data.Processor.Observer.DataProcessorObserverInterface;
import Serial.Connection.ModemConnection;
import Serial.Listener.GenericListener;
import Serial.Listener.ListenerInterface;
import Serial.SerialClient;

public class TwentyCarDataSource extends AbstractSerialDataSource implements DataProcessorObserverInterface{
    final protected String MC1BUS = "MC1BUS";
    final protected String MC1VEL = "MC1VEL";
    final protected String MC1PHA = "MC1PHA";
    final protected String MC1VVC = "MC1VVC";
    final protected String MC1IVC = "MC1IVC";
    final protected String MC1BEM = "MC1BEM";
    final protected String MC1RL1 = "MC1RL1";
    final protected String MC1RL2 = "MC1RL2";
    final protected String MC1TP1 = "MC1TP1";
    final protected String MC1TP2 = "MC1TP2";
    final protected String MC1CUM = "MC1CUM";
    final protected String MC1SLS = "MC1SLS";

    final protected String DC_DRV = "DC_DRV";
    final protected String DC_POW = "DC_POW";
    final protected String DC_SWC = "DC_SWC";

    final protected String BP_VMX = "BP_VMX";
    final protected String BP_VMN = "BP_VMN";
    final protected String BP_TMX = "BP_TMX";
    final protected String BP_ISH = "BP_ISH";

    final protected String AC_MP1 = "AC_MP1";
    final protected String AC_MP2 = "AC_MP2";
    final protected String AC_MP3 = "AC_MP3";
    final protected String AC_ISH = "AC_ISH";
    final protected String AC_TMX = "AC_TMX";
    final protected String AC_TV1 = "AC_TV1";
    final protected String AC_TV2 = "AC_TV2";

    public String getName () {
        return "2020 Sunseeker Solar Car";
    }

    protected void registerDataTypes () {
        registerDataMapping(
                MC1BUS,
                registerDataType("MC 1 Bus Current", "Amps"),
                registerDataType("MC 1 Bus Voltage", "Volts")
        );

        registerDataMapping(
                MC1VEL,
                registerDataType("MC 1 Velocity", "m/s"),
                registerDataType("MC 1 Velocity", "rpm")
        );

        registerDataMapping(
                MC1PHA,
                registerDataType("MC 1 Phase C", "Amps"),
                registerDataType("MC 1 Phase B", "Amps")
        );

        registerDataMapping(
                MC1VVC,
                registerDataType("MC 1 Vector Vd", "Volts"),
                registerDataType("MC 1 Vector Vq", "Volts")
        );

        registerDataMapping(
                MC1IVC,
                registerDataType("MC 1 Vector Id", "Amps"),
                registerDataType("MC 1 Vector Iq", "Amps")
        );

        registerDataMapping(
                MC1BEM,
                registerDataType("MC 1 Vector BEMFd", "Volts"),
                registerDataType("MC 1 Vector BEMFq", "Volts")
        );

        registerDataMapping(
                MC1RL1,
                null,
                null
        );

        registerDataMapping(
                MC1RL2,
                null,
                null
        );

        registerDataMapping(
                MC1TP1,
                registerDataType("MC 1 Heatsink Temp", "deg C"),
                registerDataType("MC 1 Motor Temp", " deg C")
        );

        registerDataMapping(
                MC1TP2,
                RESERVED,
                registerDataType("MC 1 Processor Temp", "deg C")
        );

        registerDataMapping(
                MC1CUM,
                registerDataType("MC 1 Bus Amp. Hrs.", "Ah"),
                registerDataType("MC 1 Odometer", "m")
        );

        registerDataMapping(
                MC1SLS,
                registerDataType("MC 1 Slip Speed", "Hz"),
                RESERVED
        );

        registerDataMapping(
                DC_DRV,
                registerDataType("Motor Current Setpoint", "%"),
                registerDataType("Motor Velocity Setpoint", "m/s")
        );

        registerDataMapping(
                DC_POW,
                registerDataType("Bus Current Setpoint", "%"),
                UNUSED
        );

        registerDataMapping(
                BP_VMX,
                registerDataType("Maximum Voltage", "Volts"),
                registerDataType("Maximim Cell", "Volts")
        );

        registerDataMapping(
                BP_VMN,
                registerDataType("Minimum Voltage", "Volts"),
                registerDataType("Minimum Cell", "Volts")
        );

        registerDataMapping(
                BP_TMX,
                registerDataType("Maximum Tempurature", "deg C"),
                registerDataType("Maximum Tempurature Cell", "deg C")
        );

        registerDataMapping(
                BP_ISH,
                registerDataType("Battery Shunt Current", "Amps"),
                registerDataType("Battery Voltage", "Volts")
        );

        registerDataMapping(
                AC_MP1,
                registerDataType("MP 1 Avg. Voltage", "Volts"),
                registerDataType("MP 1 Avg. Current", "Amps")
        );

        registerDataMapping(
                AC_MP2,
                registerDataType("MP 2 Avg. Voltage", "Volts"),
                registerDataType("MP 2 Avg. Current", "Amps")
        );

        registerDataMapping(
                AC_MP3,
                registerDataType("MP 3 Avg. Voltage", "Volts"),
                registerDataType("MP 3 Avg. Current", "Amps")
        );

        registerDataMapping(
                AC_ISH,
                registerDataType("Array Shunt Current", "Amps"),
                registerDataType("Array Battery Voltage", "Volts")
        );

        registerDataMapping(
                AC_TMX,
                registerDataType("Array Max. Temp.", "deg C"),
                registerDataType("Array Max. Temp. MPPT", "deg C")
        );

        registerDataMapping(
                AC_TV1,
                registerDataType("AC 1 Temp. 1", "deg C"),
                registerDataType("AC 1 Temp. 2", "deg C")
        );

        registerDataMapping(
                AC_TV2,
                registerDataType("AC 2 Temp. 1", "deg C"),
                registerDataType("AC 2 Temp. 1", "deg C")
        );

    }

    protected SerialClient getClient () {
        DataProcessorInterface processor = new GenericDataProcessor();
        processor.addObserver(this);

        //
        ListenerInterface listener = new GenericListener();
        listener.addObserver(processor);

        return new SerialClient(new ModemConnection(), listener);
    }
}
