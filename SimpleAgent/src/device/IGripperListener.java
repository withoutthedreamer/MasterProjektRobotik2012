package device;

public interface IGripperListener
{
	public void whenOpened();
	public void whenClosed();
	public void whenLifted();
	public void whenReleased();
	public void whenClosedLifted();
	public void whenReleasedOpened();
    public void whenError();
}
