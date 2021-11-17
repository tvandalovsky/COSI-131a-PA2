package cs131.pa2.filter.concurrent;

/**
 * Implements head command - overrides necessary behavior of SequentialFilter
 * 
 * @author Chami Lamelas
 *
 */
public class HeadFilter extends ConcurrentFilter {

	/**
	 * number of lines read so far
	 */
	private int numRead;

	/**
	 * number of lines passed to output via head
	 */
	private static int LIMIT = 10;

	/**
	 * Constructs a head filter.
	 */
	public HeadFilter() {
		super();
		numRead = 0;
	}

	/**
	 * Overrides {@link ConcurrentFilter#process()} to only add up to 10 lines to
	 * the output queue.
	 * @throws InterruptedException 
	 */
	@Override
	public void process() throws InterruptedException {
		while (!input.isEmpty() && numRead < LIMIT) {
			String line = input.poll();
			output.put(line);
			numRead++;
		}
		
		output.put(PoisonPill); //output.put(PoisonPill)
	}

	/**
	 * Overrides SequentialFilter.processLine() - doesn't do anything.
	 */
	@Override
	protected String processLine(String line) {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public void run() {
//		// TODO Auto-generated method stub
//		
//	}
}
