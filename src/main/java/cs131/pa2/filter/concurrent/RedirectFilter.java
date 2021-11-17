package cs131.pa2.filter.concurrent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import cs131.pa2.filter.Filter;
import cs131.pa2.filter.Message;

/**
 * Implements redirection as a {@link ConcurrentFilter} - overrides necessary
 * behavior of SequentialFilter
 * 
 * @author Chami Lamelas
 *
 */
public class RedirectFilter extends ConcurrentFilter {

	/**
	 * destination of redirection
	 */
	private String dest;

	/**
	 * command that was used to construct this filter
	 */
	private String command;

	/**
	 * stream for writing - set in process(), leave as null till then
	 */
	private PrintStream appendStream;

	/**
	 * Constructs a RedirectFilter given a >.
	 * 
	 * @param cmd cmd is guaranteed to either be ">" or ">" followed by a space.
	 * @throws IllegalArgumentException if a file parameter was not provided
	 */
	public RedirectFilter(String cmd) {
		super();

		// save command as a field, we need it when we throw an exception in
		// setNextFilter
		command = cmd;

		// find index of space, if there isn't a space that means we got just ">" =>
		// > needs a parameter so throw IAE with the appropriate message
		int spaceIdx = cmd.indexOf(" ");
		if (spaceIdx == -1) {
			throw new IllegalArgumentException(Message.REQUIRES_PARAMETER.with_parameter(cmd));
		}

		// we have a space, filename will be trimmed string after space
		String relativeDest = cmd.substring(spaceIdx + 1).trim();

		// set redirection destination as cwd joined with relative destination file
		dest = ConcurrentREPL.currentWorkingDirectory + Filter.FILE_SEPARATOR + relativeDest;

		// check if the destination file exists, if so delete it b/c > overwrites the
		// destination file if one exists
		File destFile = new File(dest);
		if (destFile.isFile()) {
			destFile.delete();
		}
	}

	/**
	 * Overrides SequentialFilter.process to close write stream
	 * @throws InterruptedException 
	 */
	@Override
	public void process() throws InterruptedException {
		try {
			appendStream = new PrintStream(new FileOutputStream(dest, true));
			super.process();
			appendStream.close();
			output.put(PoisonPill); //added output.put(PoisonPull)
		} catch (FileNotFoundException e) {
			// do nothing we know file exists
		} 

	}

	/**
	 * Overrides SequentialFilter.processLine() to just write the line to the
	 * destination file. Returns null so {@link ConcurrentFilter#process()} doesn't
	 * add anything to the output.
	 */
	@Override
	protected String processLine(String line) {
		appendStream.println(line);
		return null;
	}

	/**
	 * Overrides SequentialFilter.setPrevFilter() to not allow a {@link Filter} to
	 * be placed after {@link RedirectFilter} objects.
	 * 
	 * @throws IllegalArgumentException - always
	 */
	@Override
	public void setNextFilter(Filter nextFilter) {
		throw new IllegalArgumentException(Message.CANNOT_HAVE_OUTPUT.with_parameter(command));
	}

//	@Override
//	public void run() {
//		// TODO Auto-generated method stub
//		
//	}

}
