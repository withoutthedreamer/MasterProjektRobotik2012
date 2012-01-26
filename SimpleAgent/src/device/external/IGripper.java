package device.external;


public interface IGripper
{
    /**
     * Taken from the player IF documentation.
     */
    public static enum stateType
    {
        OPEN_LIFT,
        RELEASE_OPEN,
        CLOSE_LIFT,
        CLOSE_RELEASE,
        OPEN,
        CLOSE,
        MOVING,
        LIFT,
        RELEASE,
        ERROR,
        IDLE,
        STOP
    }
    

    public abstract void open(IGripperListener cb);

    public abstract void close(IGripperListener cb);

    public abstract void lift(IGripperListener cb);

    public abstract void releaseOpen(IGripperListener cb);

    public abstract void closeLift(IGripperListener cb);

    public abstract void release(IGripperListener cb);

    public abstract void addIsDoneListener(IGripperListener cb);

    public abstract void removeIsDoneListener(IGripperListener cb);

}