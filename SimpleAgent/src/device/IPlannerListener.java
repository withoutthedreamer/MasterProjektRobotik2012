package device;

public interface IPlannerListener
{
	public void callWhenIsDone();
	public void callWhenAbort();
	public void callWhenNotValid();
}
