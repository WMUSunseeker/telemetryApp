/**
 * Sunseeker Telemetry
 *
 * @author Alec Carpenter <alecgunnar@gmail.com>
 * @date July 9, 2016
 */

package Data.Source;

import Data.Type.DataTypeInterface;
import Data.Type.ErrorType;
import Serial.SerialClient;
import gnu.io.CommPortIdentifier;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

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
        if(field.equals("MC1LIM")){
            String lowBitString = "";
            for (byte b : low) {
                lowBitString += Integer.toBinaryString((b & 0xFF) + 0x100).substring(1);
            }

            String hiBitString = "";
            for (byte b : high) {
                hiBitString += Integer.toBinaryString((b & 0xFF) + 0x100).substring(1);
            }

            if (mappings.containsKey(field)) {
                // Gets pointer to key: value pair in mappings hashMap
                DataTypeInterface[] types = mappings.get(field);

                ErrorType errorType0;
                ErrorType errorType1;
                try{
                    errorType0 = (ErrorType) types[0];
                    errorType1 = (ErrorType) types[1];
                }
                catch (ClassCastException e){
                    System.out.println("Class cast exception");
                    return;
                }

                if (errorType0 != null) {
//                    errorType0.putValue(hiBitString);
                }

                if (errorType1 != null) {
                    errorType1.putValue(lowBitString);
                }
            }
            return;
        }


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
