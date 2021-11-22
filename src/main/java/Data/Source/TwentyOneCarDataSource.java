/**
 * Sunseeker Telemetry
 *
 * @author by Grant Reamy <grant.a.reamy@wmich.edu>
 * @date October 5, 2021
 */

package Data.Source;

import App.Profile.ProfileInterface;
import Data.Processor.DataProcessorInterface;
import Data.Processor.GenericDataProcessor;
import Data.Processor.Observer.DataProcessorObserverInterface;
import Data.Type.Collection.ErrorTypeCollection;
import Data.Type.DataTypeInterface;
import Data.Type.ErrorType;
import Serial.Connection.ModemConnection;
import Serial.Listener.GenericListener;
import Serial.Listener.ListenerInterface;
import Serial.SerialClient;

import java.io.FileWriter;
import java.io.IOException;

public class TwentyOneCarDataSource extends AbstractSerialDataSource implements DataProcessorObserverInterface {

    // MOTOR CONTROLLER HEADERS
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
    final protected String MC1LIM = "MC1LIM";

    // DRIVER CONTROLLER HEADERS
    final protected String DC_DRV = "DC_DRV";
    final protected String DC_POW = "DC_POW";
    final protected String DC_SWC = "DC_SWC";

    // BATTERY PROTECTION SYSTEM HEADERS
    final protected String BP_VMX = "BP_VMX";
    final protected String BP_VMN = "BP_VMN";
    final protected String BP_TMX = "BP_TMX";
    final protected String BP_ISH = "BP_ISH";

    // ARRAY CONTROLLER HEADERS
    final protected String AC_MP1 = "AC_MP1";
    final protected String AC_MP2 = "AC_MP2";
//    final protected String AC_MP3 = "AC_MP3"; // Only 2 MPPTs on current car.
    final protected String AC_ISH = "AC_ISH";
    final protected String AC_TMX = "AC_TMX";
    final protected String AC_TV1 = "AC_TV1";
    final protected String AC_TV2 = "AC_TV2";

    final protected int NUM_DATA_POINTS = 14;

    protected ErrorTypeCollection errorTypes;

    protected ProfileInterface profile;
    protected FileWriter writer;
    protected int numWrittenOnLine = 0;

//    public TwentyOneCarDataSource(ProfileInterface profile){
//        Profile
//    }

    public String getName () {
        return "2021 Sunseeker Solar Car";
    }

    public void setProfile(ProfileInterface profile) {
        this.profile = profile;
    }
    public FileWriter getWriter() {
        return writer;
    }

    protected void registerDataTypes () {
        errorTypes = new ErrorTypeCollection();

        registerDataMapping(
                MC1BUS,
                registerDataType("MC Bus Current", "Amps"),
                registerDataType("MC Bus Voltage", "Volts")
        );

        registerDataMapping(
                MC1VEL,
                registerDataType("MC Velocity", "m/s"),
                registerDataType("MC rotation Velocity", "rpm")
        );

        registerDataMapping(
                MC1PHA,
                registerDataType("MC Phase C", "Amps"),
                registerDataType("MC Phase B", "Amps")
        );

        registerDataMapping(
                MC1VVC,
                registerDataType("MC Vector Vd", "Volts"),
                registerDataType("MC Vector Vq", "Volts")
        );

        registerDataMapping(
                MC1IVC,
                registerDataType("MC Vector Id", "Amps"),
                registerDataType("MC Vector Iq", "Amps")
        );

        registerDataMapping(
                MC1BEM,
                registerDataType("MC Vector BEMFd", "Volts"),
                registerDataType("MC Vector BEMFq", "Volts")
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
                registerDataType("MC Heatsink Temp", "deg C"),
                registerDataType("MC Motor Temp", " deg C")
        );

        registerDataMapping(
                MC1TP2,
                RESERVED,
                registerDataType("MC Processor Temp", "deg C")
        );

        registerDataMapping(
                MC1CUM,
                registerDataType("MC Bus Amp. Hrs.", "Ah"),
                registerDataType("MC Odometer", "m")
        );

        registerDataMapping(
                MC1SLS,
                registerDataType("MC Slip Speed", "Hz"),
                RESERVED
        );


        // ******************************
        registerDataMapping(
                MC1LIM,
                registerErrorType("MC Error Counts", "none"),
                registerErrorType("MC Errors", "boolean")
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
                registerDataType("MPPT 1 Avg. Voltage", "Volts"),
                registerDataType("MPPT 1 Avg. Current", "Amps")
        );

        registerDataMapping(
                AC_MP2,
                registerDataType("MPPT 2 Avg. Voltage", "Volts"),
                registerDataType("MPPT 2 Avg. Current", "Amps")
        );

        // 2021 car no longer has 3 MPPTs.
//        registerDataMapping(
//                AC_MP3,
//                registerDataType("MP 3 Avg. Voltage", "Volts"),
//                registerDataType("MP 3 Avg. Current", "Amps")
//        );

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

    // Called when we create this object (In parent constructor, AbstractSerialDataSource)
    protected SerialClient getClient () {
        DataProcessorInterface processor = new GenericDataProcessor();
        processor.addObserver(this);

        //
        ListenerInterface listener = new GenericListener();
        listener.addObserver(processor);

        return new SerialClient(new ModemConnection(), listener);
    }

    private DataTypeInterface registerErrorType(String name, String unit){
        ErrorType type = new ErrorType(name, unit);
        if (name.equals("MC Errors")){
            ErrorType.addMotorErrors(type);
            ErrorType.addMotorLimits(type);
        }
        errorTypes.put(name, type);
        return type;
    }

    protected void receiveValue(String field, double high, double low) {
        super.receiveValue(field, high, low);
        if (mappings.containsKey(field) && profile != null && profile.getAutoSave()) {
            DataTypeInterface[] types = mappings.get(field);
            numWrittenOnLine++;
            try {
                if (writer == null) {
                    writer = new FileWriter(profile.getFileName(), true);
                }
                writer.write(Double.toString(high) + "," + Double.toString(low) + ",");

                if (numWrittenOnLine % NUM_DATA_POINTS == 0) {
                    writer.write("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ErrorTypeCollection getErrorTypes() {
        return errorTypes;
    }
}
