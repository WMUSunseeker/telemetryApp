/**
* Sunseeker Telemetry
*
* @author Alec Carpenter <alecgunnar@gmail.com>
* @date July 8, 2016
*/

package Controller;

import App.Profile.ProfileInterface;
import Data.Source.*;
import Data.Source.Collection.DataSourceCollection;
import Data.Source.Collection.DataSourceCollectionInterface;
import Serial.SerialHelper;

import javax.swing.*;

// This class handles asking the user which serial port they want to use and connecting to that port
public class DataController {
    protected DataSourceCollectionInterface dataSources;

    protected DataSourceInterface dataSource;

    protected Thread dataThread;

    public DataController () {
        dataSources = new DataSourceCollection();


        /*
         * Register the known data source types
         */
        registerDataSource(new PseudoRandomDataSource());
        registerDataSource(new TwentyOneCarDataSource());
        registerDataSource(new SixteenCarDataSource());
    }

    public boolean start (DataSourceInterface source) {
        /*
         * Cannot start if a source is already started
         */
        if (dataSource != null)
            return false;

        /*
         * If the data source requires a serial connection,
         * lets get it one
         * We do this by first checking if source needs a serial connection (source instanceof AbstactSerial...)
         * Then because && 'short circuits', the promptForSerialPort only runs if the first part is true.
         * promptForSerialPort returns true if everything went well, and thus we won't return false from this if.
         */
        if (source instanceof AbstractSerialDataSource && !promptForSerialPort(source)) {
            return false;
        }

        dataSource = source;

        /*
         * Start loading the data
         *
         * dataSource implements runnable, and has a run method, which connects to a Serial Port
         * Once connected to a serial port, it assigns a listener to the port, which will process data as it comes in
         * with an event based system.
         */
        dataThread = new Thread(dataSource, "DataSourceThread");
        dataThread.setDaemon(true);

        dataThread.start();

        return true;
    }

    public void stop () { // Pretty sure this is never used, because restart is also never called.
        /*
         * We can only stop if a data source has been set
         */
        if (dataSource == null)
            return;

        try {
            dataSource.stop();
            dataThread.join();
        } catch (InterruptedException e) {
            System.out.println("Could not stop the data source thread...");
        }

        dataSource = null;
    }

    public void restart () {
        DataSourceInterface source = dataSource;

        stop();
        start(source);
    }

    public DataSourceCollectionInterface getDataSources () {
        return dataSources;
    }

    protected void registerDataSource (DataSourceInterface source) {
        dataSources.put(source.getName(), source);
    }

    protected boolean promptForSerialPort (DataSourceInterface source) {
        SerialHelper helper = new SerialHelper();

        String port = (String) JOptionPane.showInputDialog(
            null,
            "Choose which serial port you would like to connect to:",
            "Choose a Serial Port",
            JOptionPane.PLAIN_MESSAGE,
            null,
            helper.getPortNames(),
            null
        );

        ((AbstractSerialDataSource) source).setPort(helper.getIdentifier(port));

        return port != null;
    }
}
