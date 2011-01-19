package device;

import javaclient3.structures.PlayerConstants;

public interface IDevice {
	// Current interface code assignment
	// This has to be i sync with the PlayerConstants definition!!
	// TODO as enum
	public final short DEVICE_NULL_CODE         = PlayerConstants.PLAYER_NULL_CODE;        // /dev/null analogue
    public final short DEVICE_DEVICE_CODE       = PlayerConstants.PLAYER_PLAYER_CODE;      // the server itself
    public final short DEVICE_POWER_CODE        = PlayerConstants.PLAYER_POWER_CODE;       // power subsystem
    public final short DEVICE_GRIPPER_CODE      = PlayerConstants.PLAYER_GRIPPER_CODE;     // gripper
    public final short DEVICE_POSITION2D_CODE   = PlayerConstants.PLAYER_POSITION2D_CODE;  // device that moves
    public final short DEVICE_SONAR_CODE        = PlayerConstants.PLAYER_SONAR_CODE;       // Ultra-sound range-finder
    public final short DEVICE_LASER_CODE        = PlayerConstants.PLAYER_LASER_CODE;       // scanning range-finder
    public final short DEVICE_BLOBFINDER_CODE   = PlayerConstants.PLAYER_BLOBFINDER_CODE;  // visual blobfinder
    public final short DEVICE_PTZ_CODE          = PlayerConstants.PLAYER_PTZ_CODE;         // pan-tilt-zoom unit
    public final short DEVICE_AUDIO_CODE        = PlayerConstants.PLAYER_AUDIO_CODE;       // audio I/O
    public final short DEVICE_FIDUCIAL_CODE     = PlayerConstants.PLAYER_FIDUCIAL_CODE;    // fiducial detector
    public final short DEVICE_SPEECH_CODE       = PlayerConstants.PLAYER_SPEECH_CODE;      // speech I/O
    public final short DEVICE_GPS_CODE          = PlayerConstants.PLAYER_GPS_CODE;         // GPS unit
    public final short DEVICE_BUMPER_CODE       = PlayerConstants.PLAYER_BUMPER_CODE;      // bumper array
    public final short DEVICE_TRUTH_CODE        = PlayerConstants.PLAYER_TRUTH_CODE;       // ground-truth (Stage)
    public final short DEVICE_DIO_CODE          = PlayerConstants.PLAYER_DIO_CODE;         // digital I/O
    public final short DEVICE_AIO_CODE          = PlayerConstants.PLAYER_AIO_CODE;         // analog I/O
    public final short DEVICE_IR_CODE           = PlayerConstants.PLAYER_IR_CODE;          // IR array
    public final short DEVICE_WIFI_CODE         = PlayerConstants.PLAYER_WIFI_CODE;        // wifi card status
    public final short DEVICE_WAVEFORM_CODE     = PlayerConstants.PLAYER_WAVEFORM_CODE;    // fetch raw waveforms
    public final short DEVICE_LOCALIZE_CODE     = PlayerConstants.PLAYER_LOCALIZE_CODE;    // localization
    public final short DEVICE_MCOM_CODE         = PlayerConstants.PLAYER_MCOM_CODE;        // multicoms
    public final short DEVICE_SOUND_CODE        = PlayerConstants.PLAYER_SOUND_CODE;       // sound file playback
    public final short DEVICE_AUDIODSP_CODE     = PlayerConstants.PLAYER_AUDIODSP_CODE;    // audio dsp I/O
    public final short DEVICE_AUDIOMIXER_CODE   = PlayerConstants.PLAYER_AUDIOMIXER_CODE;  // audio I/O
    public final short DEVICE_POSITION3D_CODE   = PlayerConstants.PLAYER_POSITION3D_CODE;  // 3-D position
    public final short DEVICE_SIMULATION_CODE   = PlayerConstants.PLAYER_SIMULATION_CODE;  // simulators
    public final short DEVICE_SERVICE_ADV_CODE  = PlayerConstants.PLAYER_SERVICE_ADV_CODE; // LAN advertisement
    public final short DEVICE_BLINKENLIGHT_CODE = PlayerConstants.PLAYER_BLINKENLIGHT_CODE;// blinking lights
    public final short DEVICE_NOMAD_CODE        = PlayerConstants.PLAYER_NOMAD_CODE;       // Nomad robot
    public final short DEVICE_CAMERA_CODE       = PlayerConstants.PLAYER_CAMERA_CODE;      // camera device(gazebo)
    public final short DEVICE_MAP_CODE          = PlayerConstants.PLAYER_MAP_CODE;         // get a map
    public final short DEVICE_PLANNER_CODE      = PlayerConstants.PLAYER_PLANNER_CODE;     // 2D motion planner
    public final short DEVICE_LOG_CODE          = PlayerConstants.PLAYER_LOG_CODE;         // log R/W control
    public final short DEVICE_ENERGY_CODE       = PlayerConstants.PLAYER_ENERGY_CODE;      // energy charging
    public final short DEVICE_JOYSTICK_CODE     = PlayerConstants.PLAYER_JOYSTICK_CODE;    // Joystick
    public final short DEVICE_SPEECH_RECOGNITION_CODE=PlayerConstants.PLAYER_SPEECH_RECOGNITION_CODE;// speech I/O
    public final short DEVICE_OPAQUE_CODE       = PlayerConstants.PLAYER_OPAQUE_CODE;      // plugin interface
    public final short DEVICE_POSITION1D_CODE   = PlayerConstants.PLAYER_POSITION1D_CODE;  // 1-D position
    public final short DEVICE_ACTARRAY_CODE     = PlayerConstants.PLAYER_ACTARRAY_CODE;    // Actuator Array interface
    public final short DEVICE_LIMB_CODE         = PlayerConstants.PLAYER_LIMB_CODE;        // Limb interface
    public final short DEVICE_GRAPHICS2D_CODE   = PlayerConstants.PLAYER_GRAPHICS2D_CODE;  // Graphics2D interface
    public final short DEVICE_RFID_CODE         = PlayerConstants.PLAYER_RFID_CODE;        // RFID reader interface
    public final short DEVICE_WSN_CODE          = PlayerConstants.PLAYER_WSN_CODE;         // WSN interface
    public final short DEVICE_GRAPHICS3D_CODE   = PlayerConstants.PLAYER_GRAPHICS3D_CODE;  // Graphics3D interface
    public final short DEVICE_HEALTH_CODE       = PlayerConstants.PLAYER_HEALTH_CODE;      // Statgrab Health interface
    public final short DEVICE_IMU_CODE          = PlayerConstants.PLAYER_IMU_CODE;         // Inertial Measurement Unit interface
    public final short DEVICE_POINTCLOUD3D_CODE = PlayerConstants.PLAYER_POINTCLOUD3D_CODE;// 3-D point cloud
    public final short DEVICE_RANGER_CODE       = PlayerConstants.PLAYER_RANGER_CODE;      // Array of generic range-finders
	
    public void runThreaded();
    public void shutdown();
}
