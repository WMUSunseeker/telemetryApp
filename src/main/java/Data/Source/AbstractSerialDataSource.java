/**
 * Sunseeker Telemetry
 *
 * @author Alec Carpenter <alecgunnar@gmail.com>
 * @date July 9, 2016
 */

package Data.Source;

import App.Profile.ProfileInterface;
import Data.Type.DataTypeInterface;
import Data.Type.ErrorType;
import Serial.SerialClient;
import gnu.io.CommPortIdentifier;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

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

    protected ProfileInterface profile;
    protected FileWriter writer;
//    protected List<String> fieldsWritten = new ArrayList<>();
    protected List<String> fieldsList = new ArrayList<>();
    protected String[] lineToWrite;
    private boolean firstData = false;

    public AbstractSerialDataSource () {
        client = getClient();

        mappings = new HashMap<String, DataTypeInterface[]>();

        registerDataTypes();
    }

    // This is called soon after the constructor, but where the constructor is used, we don't have acceess to the profile
    // We can't get the profile either, because it is not set yet.
    public void setProfile(ProfileInterface profile) {
        this.profile = profile;

        if(profile.getAutoSave()) {
            try {
                writer = new FileWriter(profile.getFileName(), false);
                addCSVTitles();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setPort (CommPortIdentifier port) {
        this.port = port;
    }

    public CommPortIdentifier getPort () {
        return port;
    }

    public FileWriter getWriter() {
        return writer;
    }

    private void addCSVTitles() throws IOException {
        for (String type : mappings.keySet()) {
            if(type != null) {
                System.out.println("adding " + type);
                fieldsList.add(type);
            }
        }
        for (DataTypeInterface[] type : mappings.values()){
            if(type[0] != null) {
                writer.write(type[0].getName() + ",");
            }
            if(type[1] != null){
                writer.write(type[1].getName() + ",");
            }
        }
        lineToWrite = new String[fieldsList.size()];
        Arrays.fill(lineToWrite, null);
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
            errorSave(field, high, low);
            return;
        }

//        System.out.println("Received value for " + field);
//        System.out.println("High: " + highBuff.getFloat());
//        System.out.println("Low: " + lowBuff.getFloat());
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

            if (types[0] != null) {
                types[0].putValue(high);
            }

            if (types[1] != null)
                types[1].putValue(low);

            if(profile != null && profile.getAutoSave()){
                this.autoSave(field, high, low);
            }
        }
    }

    private void errorSave(String field, byte[] high, byte[] low){
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
            try {
                errorType0 = (ErrorType) types[0];
                errorType1 = (ErrorType) types[1];
            } catch (ClassCastException e) {
                System.out.println("Class cast exception");
                return;
            }

            if (errorType0 != null) {
//                   errorType0.putValue(hiBitString);
            }

            if (errorType1 != null) {
                errorType1.putValue(lowBitString);
            }
        }
    }

    protected void autoSave(String field, double high, double low){
        if (mappings.containsKey(field) && profile != null && profile.getAutoSave()) {
            try {
                System.out.println("Auto saving");
                if (!firstData) { // Makes sure there is a line break between data and titles (column headers).
                    writer.write("\n");
                    firstData = true;
                }
                for (String s : lineToWrite) {
                    System.out.println(s == null);
                }
                String toWrite = high + "," + low;
                System.out.println("Writing: " + toWrite);
                System.out.println("fieldIndex: " + fieldsList.indexOf(field));
                if(lineToWrite[fieldsList.indexOf(field)] == null) {
                    System.out.println("Writing " + toWrite + " to " + field);
                    lineToWrite[fieldsList.indexOf(field)] = toWrite;
                }
                else {
                    for (int i = 0; i < lineToWrite.length; i++) {
                        if (lineToWrite[i] == null) {
                            lineToWrite[i] = "null";
                        }
                    }
                    writer.write(Arrays.toString(lineToWrite).replace("[", "")
                            .replace("]", "").replace(", ", ","));
                    writer.write("\n");
                    Arrays.fill(lineToWrite, null);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void registerDataMapping (String field, DataTypeInterface high, DataTypeInterface low) {
        // Not sure what this does, updateId method is empty
//        updateId(field, HIGH_SUFFIX, high);
//        updateId(field, LOW_SUFFIX, low);

        mappings.put(field, new DataTypeInterface[] { // Array of data type interfaces
            high, low
        });
    }

//    protected void updateId (String field, String suffix, DataTypeInterface type) {
//
//    }

    abstract protected void registerDataTypes();

    abstract protected SerialClient getClient();

    abstract protected int getNumDataPoints();
}
