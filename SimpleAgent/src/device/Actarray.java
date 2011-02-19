/**
 * 
 */
package device;

import javaclient3.ActarrayInterface;

/**
 * @author sebastian
 * 
 * TODO implement remaining interfaces from player actarrayproxy.cc
 */
public class Actarray extends RobotDevice {

    /**
     * 
     */
    public Actarray() { super(); }

    /**
     * @param roboClient
     * @param device
     */
    public Actarray(DeviceNode roboClient, Device device) {
        super(roboClient, device);
    }
    @Override protected void update () {}
    /**
     * 
     * @param joint
     */
    public void moveHome (int joint) {
        ( (ActarrayInterface) getDevice() ).homeCmd(joint);
    }
    /**
     * 
     * @param joint
     * @param position
     */
    public void moveTo (int joint, float position) {
        ( (ActarrayInterface) getDevice() ).setPosition(joint, position);
    }
}
