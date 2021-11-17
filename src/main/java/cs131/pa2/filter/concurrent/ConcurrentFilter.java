/*
 * @author Thomas Vandalovsky
 * OS
 */

package cs131.pa2.filter.concurrent;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import cs131.pa2.filter.Filter;

/**
 * An abstract class that extends the Filter and implements the basic functionality of all filters. Each filter should
 * extend this class and implement functionality that is specific for this filter. 
 * You should not modify this class.
 * @author cs131a
 *
 *
 *For all of the filters that implemented ConcurrentFilter, added output.put(PoisonPill) to the 
 *overridden process
 */
public abstract class ConcurrentFilter extends Filter implements Runnable {
	/**
	 * The input queue for this filter
	 */
	protected LinkedBlockingQueue<String> input;
	/**
	 * The output queue for this filter
	 */
	protected LinkedBlockingQueue<String> output;
	
	/* 
	 * creates PosionPill string
	 */
	public static final String PoisonPill = "EndEndEnd";

	/*
	 * creates mainThread
	 */
	public Thread mainThread;
	
	@Override
	public void setPrevFilter(Filter prevFilter) {
		prevFilter.setNextFilter(this);
	}
	
	@Override
	public void setNextFilter(Filter nextFilter) {
		if (nextFilter instanceof ConcurrentFilter){
			ConcurrentFilter sequentialNext = (ConcurrentFilter) nextFilter;
			this.next = sequentialNext;
			sequentialNext.prev = this;
			if (this.output == null){
				this.output = new LinkedBlockingQueue<String>();
			}
			sequentialNext.input = this.output;
		} else {
			throw new RuntimeException("Should not attempt to link dissimilar filter types.");
		}
	}
	
	
	/**
	 * Processes the input queue and passes the result to the output queue
	 * Once passing into output queue has finished, 
	 * @throws InterruptedException 
	 */
	public void process() throws InterruptedException{  //implement poison pill into process	
		while(isDone() == false) { 
			String line = input.take();
			String processedLine = processLine(line);
			if(processedLine != null) { 
				output.put(processedLine);
			}
		}
		
		//if(output != null) {
			//System.out.println(PoisonPill);
			output.put(PoisonPill);
			//mainThread.interrupt();
		//}
		
	}
	
	@Override
	/*
	 * Is done not checks input for three different checks and returns a boolean regarding the condition
	 * If it is empty it returns true 
	 * if the top element in null it returns true 
	 * if the top element is the PoisonPill it returns true 
	 * if all these pass it returns false 
	 */
	public boolean isDone() {
		
		if(input.isEmpty()) { 
			return true;
		}
		else if(input.peek() == null) { 
			return true;
		}
		else if(input.peek().equals(PoisonPill)) { 
			return true;
		}
		else { 
			return false;
		}
	
	}
	
	/**
	 * Called by the {@link #process()} method for every encountered line in the input queue.
	 * It then performs the processing specific for each filter and returns the result.
	 * Each filter inheriting from this class must implement its own version of processLine() to
	 * take care of the filter-specific processing.
	 * @param line the line got from the input queue
	 * @return the line after the filter-specific processing
	 */
	protected abstract String processLine(String line);
	
	/*
	 * runs the thread through implementing runnable 
	 * when thread runs it executes process()
	 */
	public void run() {
		try {
			process();
		} catch (InterruptedException e) {
			//System.out.println();
			e.printStackTrace();
		}
		
	}
	
	/*
	 * Sets instance of protected thread 
	 */
	 public void createThread(Thread instThread){
	        mainThread = instThread;
	    }
	
	
	
}
