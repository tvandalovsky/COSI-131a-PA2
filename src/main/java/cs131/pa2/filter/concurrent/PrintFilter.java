package cs131.pa2.filter.concurrent;

/**
 * Implements printing as a {@link ConcurrentFilter} - overrides necessary
 * behavior of SequentialFilter
 * 
 * @author Chami Lamelas
 *
 */
public class PrintFilter extends ConcurrentFilter {

	/**
	 * Overrides SequentialFilter.processLine() to just print the line to stdout.
	 */
	@Override
	protected String processLine(String line) {

		System.out.println(line);
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public void run() {
//		// TODO Auto-generated method stub
//		
//	}

}
