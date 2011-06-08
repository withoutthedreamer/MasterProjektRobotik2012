package device.external;

public interface IPlannerListener
{
	public void callWhenIsDone();
	public void callWhenAbort();
	public void callWhenNotValid();
}
