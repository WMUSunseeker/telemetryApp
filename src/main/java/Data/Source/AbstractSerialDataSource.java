/**
 * Sunseeker Telemetry
 *
 * @author Alec Carpenter <alecgunnar@gmail.com>
 * @date July 9, 2016
 */

package Data.Source;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.HashMap;

import Data.Type.DataTypeInterface;
import Serial.SerialClient;
import gnu.io.CommPortIdentifier;

public abstract class AbstractSerialDataSource extends AbstractDataSource {
    /*
     * Some values are reserved and cannot be registered
     */
    final protected DataTypeInterface RESERVED = null;

    /*
     * Some values simply are not used
     */
    final protected DataTypeInterface UNUSED = null;

    /*
     * High value suffix
     */
    final protected String HIGH_SUFFIX = "HI";

    /*
     * Low value suffix
     */
    final protected String LOW_SUFFIX = "LO";

    /*
     * The port which is connected to
     */
    protected CommPortIdentifier port;

    /*
     * Facilitates the connecting to and disconnecting from the port
     */
    protected SerialClient client;

    /*
     * Incoming data fields and their corresponding types
     */
    protected Map<String, DataTypeInterface[]> mappings;

    public AbstractSerialDataSource () {
        client = getClient();

        mappings = new HashMap<String, DataTypeInterface[]>();

        registerDataTypes();
    }

    public void setPort (CommPortIdentifier port) {
        this.port = port;
    }

    public CommPortIdentifier getPort () {
        return port;
    }

    public void run () throws RuntimeException {
        if (port == null)
            throw new RuntimeException("A port has not been provided!");

        client.connect(port);
    }

    public void stop () {
        client.disconnect();
    }

    public void receiveValue (String field, byte[] high, byte[] low) {
        // ByteBuffer allows us to convert array of bytes into a number. Likely 4 bytes in the array, to create a float
        ByteBuffer highBuff = ByteBuffer.wrap(high);
        ByteBuffer lowBuff = ByteBuffer.wrap(low);

//        if (field.equals("MC1ERR")){
//            // Do stuff to decode bytes into bits, and figure out which flags are set.
//
//        }

        receiveValue(
            field,
            highBuff.getFloat(),
            lowBuff.getFloat()
        );
    }

    protected void receiveValue(String field, double high, double low) {
        // If string received is in our list of mappings, then lets update things.
        if (mappings.containsKey(field)) {
            // Gets pointer to key: value pair in mappings hashMap
            DataTypeInterface[] types = mappings.get(field);

            if (types[0] != null)
                types[0].putValue(high);

            if (types[1] != null)
                types[1].putValue(low);
        }
    }

    protected void registerDataMapping (String field, DataTypeInterface high, DataTypeInterface low) {
        // Not sure what this does, updateId method is empty (~ln 102)
        updateId(field, HIGH_SUFFIX, high);
        updateId(field, LOW_SUFFIX, low);

        mappings.put(field, new DataTypeInterface[] { // Array of data type interfaces
            high, low
        });
    }

    protected void updateId (String field, String suffix, DataTypeInterface type) {

    }

    abstract protected void registerDataTypes();

    abstract protected SerialClient getClient();
}
