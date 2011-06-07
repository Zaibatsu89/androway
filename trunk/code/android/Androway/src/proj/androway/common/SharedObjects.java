package proj.androway.common;

import android.app.Application;
import proj.androway.main.Controller;
import proj.androway.connection.bluetooth.IncomingData;
import proj.androway.connection.bluetooth.OutgoingData;
import proj.androway.session.Session;

/**
 * The SharedObjects class keeps some common used objects which
 * may not be destroyed when a certain activity is closed
 * @author Rinse Cramer & Tymen Steur
 * @since 06-06-2011
 * @version 0.5
 */
public class SharedObjects extends Application
{
    /**
     * The application Controller object
     */
    public Controller controller;

    /**
     * The common used Session object
     */
    public Session session;

    /**
     * The common used IncomingData object (stores the data received through bluetooth)
     */
    public IncomingData incomingData;

    /**
     * The common used OutgoingData object (contains the data to send through bluetooth)
     */
    public OutgoingData outgoingData;
}
