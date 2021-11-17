package cs131.pa2.filter.concurrent;

/**
 * Implements wc command - overrides necessary behavior of SequentialFilter
 * 
 * @author Chami Lamelas
 *
 */
public class WordCountFilter extends ConcurrentFilter {

	/**
	 * word count in input - words are strings separated by space in the input
	 */
	private int wordCount;

	/**
	 * character count in input - includes ws
	 */
	private int charCount;

	/**
	 * line count in input
	 */
	private int lineCount;

	/**
	 * Constructs a wc filter.
	 */
	public WordCountFilter() {
		super();
		wordCount = 0;
		charCount = 0;
		lineCount = 0;
	}

	/**
	 * Overrides {@link ConcurrentFilter#process()} by computing the word count,
	 * line count, and character count then adding the string with line count + " "
	 * + word count + " " + character count to the output queue
	 * @throws InterruptedException 
	 */
	@Override
	public void process() throws InterruptedException {
		while (!input.isEmpty()) {
			String line = input.poll();
			processLine(line);
		}
		output.put(lineCount + " " + wordCount + " " + charCount);
		output.put(PoisonPill);
	}

	/**
	 * Overrides SequentialFilter.processLine() - updates the line, word, and
	 * character counts from the current input line
	 */
	@Override
	protected String processLine(String line) {
		lineCount++;
		wordCount += line.split(" ").length;
		charCount += line.length();

		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public void run() {
//		// TODO Auto-generated method stub
//		
//	}

}
