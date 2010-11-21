
public class Blackboard implements Runnable {

	// Every class of this type has it's own thread
	public Thread thread = new Thread ( this );

	public Blackboard() {
		// TODO make singleton
		try {
			// Automatically start own thread in constructor
			this.thread.start();
			System.out.println("Running "
					+ this.toString()
					+ " in thread: "
					+ this.thread.getName());

		} catch ( Exception e ) {
			e.printStackTrace();
			System.exit (1);
		}
}

	public void update () {
// TODO
	}
	
	@Override
	public void run() {
		while ( ! this.thread.isInterrupted()) {
			this.update ();
		}
		System.out.println("Shutdown of " + this.toString());
	}

}
