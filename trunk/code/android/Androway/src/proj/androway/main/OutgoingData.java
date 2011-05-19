/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package proj.androway.main;

/**
 *
 * @author Tymen
 */
public class OutgoingData
{
    public float drivingSpeed;
    public float drivingDirection;
    public short onHold;
    public short stopSession;

    public OutgoingData()
    {
        drivingSpeed = 0.0f;
        drivingDirection = 0.0f;
        onHold = 0;
        stopSession = 0;
    }
}