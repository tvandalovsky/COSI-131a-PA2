package cs131.pa2.filter.concurrent;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import cs131.pa2.filter.Message;

/**
 * The main implementation of the REPL loop (read-eval-print loop). It reads
 * commands from the user, parses them, executes them and displays the result.
 * 
 * @author cs131a
 *
 */
public class ConcurrentREPL {
	/**
	 * the path of the current working directory
	 */
	static String currentWorkingDirectory;

	/**
	 * pipe string
	 */
	static final String PIPE = "|";

	/**
	 * redirect string
	 */
	static final String REDIRECT = ">";
	/*
	 * ReplJobs string
	 */
	static final String ReplJobs = "repl_jobs";
	
	/*
	 * Kill string
	 */
	static final String KILL = "kill";
	
	/* 
	 * boolean checker for if command will run in the background or foreground 
	 */
	static boolean bgChecker = false;
	
	
	/* 
	 * list for background threads
	 */
	public static List<Thread> bgThreads = new LinkedList<Thread>();
	
	
	/**
	 * The main method that will execute the REPL loop 
	 * creates concurrent threads that execute the commands and filters within them
	 * @param args not used
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) {
		System.out.print(Message.WELCOME);
		// set cwd here so that it can be reset by tests that run main() function
		currentWorkingDirectory = System.getProperty("user.dir");
		Scanner consoleReader = new Scanner(System.in);
		//List<Thread> bgThreads = new LinkedList<Thread>();
		
	
		// whether or not shell is running
		boolean running = true;

		do {
			System.out.print(Message.NEWCOMMAND);
			String cmd = consoleReader.nextLine();
			if (cmd.isBlank()) { //checks for blank
				continue;
			}
			
			if(cmd.equals("exit")) {  //checks for exit
				break;
			}
			
			if(cmd.trim().endsWith("&")) { //bg checker
				cmd = background(cmd);
			}
			
			
			if(cmd.equals(ReplJobs)) {  //checks if replJobs want to be printed
				replJobs(bgThreads);
				continue;
			}
			
			if(cmd.startsWith(KILL)) {  //kill checker which doesn't work
				killer(cmd, bgThreads);
				continue;
			} 
			try {
				// parse command into sub commands, then into Filters, add final PrintFilter if
				// necessary, and link them together - this can throw IAE so surround in
				// try-catch so appropriate Message is printed (will be the message of the IAE)
				List<ConcurrentFilter> filters = ConcurrentCommandBuilder.createFiltersFromCommand(cmd); //creates new filters each loop
				List<Thread> threadsList = new LinkedList<Thread>(); //creates new threadsList each loop
				Long IdChecker = Thread.currentThread().getId();
				Thread main = null;
				//if(filters.size() == 1 && filters.get(0) instanceof ExitFilter) { 
				if(filters == null) { 
					running = false;
				} else {
					//starts all of the threads 
					for(ConcurrentFilter filter: filters) { 
						Thread thread = new Thread(filter, cmd); 
						//filter.createThread(thread); //class instance of thread in ConcurrentFilter
						filter.createThread(thread);
						thread.start(); //starts threads
						threadsList.add(thread); //populates threadsList with threads
					}
					
					//check for ending thread to join all of them later in the code 
					int sizeTL = threadsList.size()-1; //indexed size of threadsList
					for(Thread thr : threadsList) { 
						//thr.start(); // starts thread
						if(thr == threadsList.get(sizeTL)) { 
							main = thr; //end is the last filter which all others will join into
						}
					}
					
					if(bgChecker == false)  { //if bgChecker is false it joins all of the threads to the "main" ending thread
						try {

							if (IdChecker == 1){
								//System.out.print(Thread.currentThread().toString());
								main.join(); // join the main thread
								for(Thread thr: threadsList) { 
									thr.join(); //join all of the threads
								}
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else { //else the bgChecker is true and it is the background \
						bgThreads.add(main);	     //process and needs to be added to the background List
					}
					
				}
				
			} catch (IllegalArgumentException e) { 
				System.out.print(e.getMessage());
			}
			//System.out.println(bgChecker);
			bgChecker = false;  //resets checker after every iteration
			//System.out.println(bgChecker);
			} while (running);
		
		
		System.out.print(Message.GOODBYE);
		consoleReader.close();

}
	/* 
	 * relpJobs printer method which prints the background jobs --> the commands that end in &
	 * @param list of bg threads 
	 */
	private static void replJobs(List<Thread> threads)  {
		int ind = 1; 
		for(Thread thr : threads) { 
			if(thr != null) { 
				if(thr.isAlive()) {
					String str = "\t" + ind + ". " + thr.getName() + "&";
					System.out.println(str);
					ind++;
				}
			}
		}
	}
	
	/*
	 * @param the command entered into the repl 
	 * takes away the & so the command can be passed into the filter creator 
	 * changes bgChecker to true since the command is a background command
	 */
	private static String background(String cmd) { 
		bgChecker = true;
		String[] newCmd = cmd.trim().split("&");
		cmd = newCmd[0]; 
		//System.out.println(cmd);
		return cmd;
	}
	
	/*
	 * @params list of bg commands, command string 
	 * matches the index that should be killed with the thread in the list and interrupts it 
	 * doesnt work
	 */
	private static void killer(String cmd, List<Thread> threads) { 
		String[] newCmd = cmd.trim().split(" ");
		int toKillCounter = Integer.parseInt(newCmd[1]);
		int counter = 1;
		Thread toKill = null;
		for(Thread thr : threads) {  
			if(counter != toKillCounter) { 
				//System.out.print(counter);
				counter++;
			} else { 
				thr.interrupt();
			}
		}
		threads.set(toKillCounter, null);
		threads.remove(toKill);
	}
	

}