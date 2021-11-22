/**
* Sunseeker Telemetry
*
* @author Alec Carpenter <alecgunnar@gmail.com>
* @date July 2, 2016
*/

package Controller;

import App.Profile.ProfileInterface;
import Data.Source.*;

import java.io.IOException;

// This handles when the user closes the application, run() method will be run when the user closes the application.
public class ShutdownController extends Thread {
    protected ProfileInterface profile;

    public ShutdownController (ProfileInterface profile) {
        this.profile = profile;
    }

    public void run () {
//        if (profile.hasChanged()) {
//            // Ask the user if they want to save the profile
//        }

        DataSourceInterface dataSource = profile.getDataSource();

        // Disconnects from serial port and stops the collection of data.
        if (dataSource instanceof AbstractSerialDataSource) {
            if (dataSource instanceof TwentyOneCarDataSource){
                try {
                    ((TwentyOneCarDataSource) dataSource).getWriter().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            dataSource.stop();
        }
    }
}
