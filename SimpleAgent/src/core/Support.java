/**
 * 
 */
package core;

import java.util.HashMap;

import device.Device;
import device.external.IDevice;

/**
 * @author sebastian
 * Contains information on supported devices.
 */
public abstract class Support
{
    /**
     * HashMap containing supported devices information.
     */
    public final static HashMap<Short,Boolean> supHm = new HashMap<Short,Boolean>();
    static{
        supHm.put(IDevice.DEVICE_NULL_CODE, false); 
        supHm.put(IDevice.DEVICE_DEVICE_CODE, false); 
        supHm.put(IDevice.DEVICE_POWER_CODE, false); 
        supHm.put(IDevice.DEVICE_GRIPPER_CODE, true); 
        supHm.put(IDevice.DEVICE_POSITION2D_CODE, true); 
        supHm.put(IDevice.DEVICE_SONAR_CODE, true); 
        supHm.put(IDevice.DEVICE_LASER_CODE, true); 
        supHm.put(IDevice.DEVICE_BLOBFINDER_CODE, true); 
        supHm.put(IDevice.DEVICE_PTZ_CODE, false); 
        supHm.put(IDevice.DEVICE_AUDIO_CODE, false); 
        supHm.put(IDevice.DEVICE_FIDUCIAL_CODE, false); 
        supHm.put(IDevice.DEVICE_SPEECH_CODE, false); 
        supHm.put(IDevice.DEVICE_GPS_CODE, false); 
        supHm.put(IDevice.DEVICE_BUMPER_CODE, false); 
        supHm.put(IDevice.DEVICE_TRUTH_CODE, false); 
        supHm.put(IDevice.DEVICE_DIO_CODE, true); 
        supHm.put(IDevice.DEVICE_AIO_CODE, false); 
        supHm.put(IDevice.DEVICE_IR_CODE, false); 
        supHm.put(IDevice.DEVICE_WIFI_CODE, false); 
        supHm.put(IDevice.DEVICE_WAVEFORM_CODE, false); 
        supHm.put(IDevice.DEVICE_LOCALIZE_CODE, true); 
        supHm.put(IDevice.DEVICE_MCOM_CODE, false); 
        supHm.put(IDevice.DEVICE_SOUND_CODE, false); 
        supHm.put(IDevice.DEVICE_AUDIODSP_CODE, false); 
        supHm.put(IDevice.DEVICE_AUDIOMIXER_CODE, false); 
        supHm.put(IDevice.DEVICE_POSITION3D_CODE, false); 
        supHm.put(IDevice.DEVICE_SIMULATION_CODE, true); 
        supHm.put(IDevice.DEVICE_SERVICE_ADV_CODE, false); 
        supHm.put(IDevice.DEVICE_BLINKENLIGHT_CODE, false); 
        supHm.put(IDevice.DEVICE_NOMAD_CODE, false); 
        supHm.put(IDevice.DEVICE_CAMERA_CODE, false); 
        supHm.put(IDevice.DEVICE_MAP_CODE, true); 
        supHm.put(IDevice.DEVICE_PLANNER_CODE, true); 
        supHm.put(IDevice.DEVICE_LOG_CODE, false); 
        supHm.put(IDevice.DEVICE_ENERGY_CODE, false); 
        supHm.put(IDevice.DEVICE_JOYSTICK_CODE, false); 
        supHm.put(IDevice.DEVICE_SPEECH_RECOGNITION_CODE, false);
        supHm.put(IDevice.DEVICE_OPAQUE_CODE, false); 
        supHm.put(IDevice.DEVICE_POSITION1D_CODE, false); 
        supHm.put(IDevice.DEVICE_ACTARRAY_CODE, true); 
        supHm.put(IDevice.DEVICE_LIMB_CODE, false); 
        supHm.put(IDevice.DEVICE_GRAPHICS2D_CODE, false); 
        supHm.put(IDevice.DEVICE_RFID_CODE, false); 
        supHm.put(IDevice.DEVICE_WSN_CODE, false); 
        supHm.put(IDevice.DEVICE_GRAPHICS3D_CODE, false); 
        supHm.put(IDevice.DEVICE_HEALTH_CODE, false); 
        supHm.put(IDevice.DEVICE_IMU_CODE, false); 
        supHm.put(IDevice.DEVICE_POINTCLOUD3D_CODE, false); 
        supHm.put(IDevice.DEVICE_RANGER_CODE, true); 
    }
    /**
     * Checks if the given device is supported.
     * @param dev The device to check.
     * @return true if device is known and supported, false else.
     */
    public static boolean check(Device dev)
    {
        Boolean support = supHm.get(new Integer(dev.getName()).shortValue());
       
        if (support == null)
        {
            /** Device code not found */
            return false;
        }
        else
        {
            return (boolean) support;
        }
    }
}
