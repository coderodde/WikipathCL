package net.coderodde.wikipath.commandline.app;

import java.io.File;
import java.util.List;
import net.coderodde.graph.pathfinding.uniform.delayed.AbstractDelayedGraphPathFinder;
import net.coderodde.graph.pathfinding.uniform.delayed.support.ThreadPoolBidirectionalPathFinder;
import static net.coderodde.wikipath.commandline.app.Miscellanea.bar;
import static net.coderodde.wikipath.commandline.app.Miscellanea.nth;
import static net.coderodde.wikipath.commandline.app.Miscellanea.parseInt;
import net.coderodde.wikipedia.graph.expansion.AbstractWikipediaGraphNodeExpander;
import net.coderodde.wikipedia.graph.expansion.BackwardWikipediaGraphNodeExpander;
import net.coderodde.wikipedia.graph.expansion.ForwardWikipediaGraphNodeExpander;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * This class implements the command line application for searching shortest 
 * paths in Wikipedia article graph.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Aug 7, 2016)
 */

public class App {
    
    /**
     * The default number of threads.
     */
    private static final int DEFAULT_THREAD_COUNT = 8;

    /**
     * The minimum number of threads.
     */
    private static final int MINIMUM_THREAD_COUNT = 2;
    
    /**
     * The minimum number of times a master thread may go to sleep while the 
     * frontier queue is empty, before giving up and terminating the entire 
     * search progress.
     */
    private static final int MINIMUM_THREAD_SLEEP_TRIALS = 1;
    
    /**
     * The minimum number of milliseconds a master thread may sleep whenever it
     * discovers the frontier queue to be empty.
     */
    private static final int MINIMUM_MASTER_THREAD_SLEEP_DURATION = 1;
    
    /**
     * The minimum number of milliseconds a slave thread may sleep whenever it
     * discovers the frontier queue to be empty.
     */
    private static final int MINIMUM_SLAVE_THREAD_SLEEP_DURATION = 1;
    
    public static void main(String[] args) {
        final Options options = createOptionsObject();
        
        if (args.length < 2) {
            new HelpFormatter().printHelp(
                    "java -jar " + getThisJARFileName() + 
                    " [options] SOURCE TARGET", 
                    options);
            System.exit(0);
        }
        
        // The copy of the argument array excluding two last arguments that
        // specify the source and target nodes:
        final String[] argsWithoutTerminals = new String[args.length - 2];
        System.arraycopy(args,
                         0, 
                         argsWithoutTerminals, 
                         0, 
                         argsWithoutTerminals.length);

        final String sourceURLString = args[args.length - 2];
        final String targetURLString = args[args.length - 1];
        
        final String source = sourceURLString.
                              substring(sourceURLString.lastIndexOf('/') + 1);
                
        final String target = targetURLString.
                              substring(targetURLString.lastIndexOf('/') + 1);
        
        CommandLine commandLine = null;
        
        try {
            commandLine = new DefaultParser().parse(options, 
                                                    argsWithoutTerminals);
        } catch (final ParseException ex) {
            System.err.println("[INPUT ERROR]: " + ex.getMessage());
            System.exit(1);
        }
        
        boolean performLogging        = false;
        int threadCount               = DEFAULT_THREAD_COUNT;
        int maximumThreadSleepTrials  = 10;
        int masterThreadSleepDuration = 100;
        int slaveThreadSleepDuration  = 10;
        
        if (commandLine.hasOption("l")) {
            performLogging = true;
        }
        
        if (commandLine.hasOption("t")) {
            threadCount = parseInt(commandLine.getOptionValue("t"));
        }
        
        if (commandLine.hasOption("trials")) {
            maximumThreadSleepTrials = 
                    parseInt(commandLine.getOptionValue("trials"));
        }
        
        if (commandLine.hasOption("m")) {
            masterThreadSleepDuration = 
                    parseInt(commandLine.getOptionValue("m"));
        }
        
        if (commandLine.hasOption("s")) {
            slaveThreadSleepDuration = 
                    parseInt(commandLine.getOptionValue("s"));
        }
        
        bar();
        outputConfiguration("[REQUESTED CONFIGURATION]", 
                            performLogging, 
                            threadCount, 
                            maximumThreadSleepTrials, 
                            masterThreadSleepDuration, 
                            slaveThreadSleepDuration);
        
        threadCount = Math.max(threadCount, MINIMUM_THREAD_COUNT);
        
        maximumThreadSleepTrials = 
                Math.max(maximumThreadSleepTrials, MINIMUM_THREAD_SLEEP_TRIALS);
        
        masterThreadSleepDuration = 
                Math.max(masterThreadSleepDuration,
                         MINIMUM_MASTER_THREAD_SLEEP_DURATION);
        
        slaveThreadSleepDuration = 
                Math.max(slaveThreadSleepDuration,
                         MINIMUM_SLAVE_THREAD_SLEEP_DURATION);
        
        bar();
        outputConfiguration("[EFFECTIVE CONFIGURATION]", 
                            performLogging, 
                            threadCount, 
                            maximumThreadSleepTrials, 
                            masterThreadSleepDuration, 
                            slaveThreadSleepDuration);
        bar();
        System.out.println("[EFFECTIVE CONFIGURATION] The source node \"" +
                           source + "\".");
        System.out.println("[EFFECTIVE CONFIGURATION] The target node \"" +
                           target + "\".");
        bar();
        
        final AbstractWikipediaGraphNodeExpander forwardSearchNodeExpander =
                new ForwardWikipediaGraphNodeExpander(sourceURLString);

        final AbstractWikipediaGraphNodeExpander backwardSearchNodeExpander 
                = new BackwardWikipediaGraphNodeExpander(targetURLString);

        if (!forwardSearchNodeExpander.getBasicUrl()
                .equals(backwardSearchNodeExpander.getBasicUrl())) {
            System.err.println(
                    "[INPUT ERROR] It seems like the two terminal URLs " +
                    "point to articles written in different languages.");
            System.exit(2);
        }
        
        final AbstractDelayedGraphPathFinder<String> finder =
                new ThreadPoolBidirectionalPathFinder<>(
                        threadCount,
                        masterThreadSleepDuration,
                        slaveThreadSleepDuration,
                        maximumThreadSleepTrials);
        
        List<String> path = null;
        
        try {
            path = finder.search(source, 
                                 target, 
                                 forwardSearchNodeExpander, 
                                 backwardSearchNodeExpander,
                                 performLogging ?
                                        new ForwardSearchProgressLogger() :
                                        null,
                                 performLogging ?
                                        new BackwardSearchProgressLogger() :
                                        null,
                                 null);
        } catch (final Exception ex) {
            System.err.println("[SEARCH ERROR] " + ex.getMessage());
            System.exit(3);
        }
        
        if (path.isEmpty()) {
            System.out.println(
                    "[RESULT] The target node is not reachable " +
                    "from the source node.");
        } else {
            System.out.println("[RESULT] The shortest path is:");
            path.forEach(System.out::println);
        }
        
        System.out.printf("[RESULT] The search took %d milliseconds, " +
                          "expanding %d node%s.\n", 
                          finder.getDuration(),
                          finder.getNumberOfExpandedNodes(),
                          nth(finder.getNumberOfExpandedNodes()));
    }
    
    private static void 
        outputConfiguration(final String title,
                            final boolean performLogging,
                            final int threadCount,
                            final int maximumThreadSleepTrials,
                            final int masterThreadSleepDuration,
                            final int slaveThreadSleepDuration) {
        
        System.out.println(
                title + " Logging: " + performLogging);
        System.out.println(
                title + " Thread count: " + threadCount);
        System.out.println(
                title + " Maximum sleep trials: " + maximumThreadSleepTrials);
        System.out.println(
                title + " Master sleeps (ms): " + masterThreadSleepDuration);
        System.out.println(
                title + " Slave sleeps (ms): " + slaveThreadSleepDuration);
    }
    
    private static Options createOptionsObject() {
        final Options options = new Options();
        
        final Option loggingOption = 
                new Option("l", 
                           "log", 
                           false, 
                           "sets the progress logging on.");
        
        final Option threadCountOption = 
                Option.builder("t")
                      .longOpt("threads")
                      .hasArg()
                      .argName("N")
                      .desc("sets the number of threads to use in the entire " + 
                            "search process.")
                      .build();
        
        final Option trialCountOption = 
                Option.builder()
                      .longOpt("trials")
                      .hasArg()
                      .argName("N")
                      .desc("sets the maximum number of trials to pop the " +
                            "queue before terminating the search.")
                      .build();
        
        final Option masterSleepOption =
                Option.builder("m")
                      .longOpt("master-sleep")
                      .hasArg()
                      .argName("ms")
                      .desc("sets the hibernate duration for master threads " +
                            "in milliseconds.")
                      .build();
        
        final Option slaveSleepOption =
                Option.builder("s")
                      .longOpt("slave-sleep")
                      .hasArg()
                      .argName("ms")
                      .desc("sets the hibernation duration for slave threads " +
                            "in milliseconds.")
                      .build();
        
        options.addOption(loggingOption);
        options.addOption(threadCountOption);
        options.addOption(trialCountOption);
        options.addOption(masterSleepOption);
        options.addOption(slaveSleepOption);
        
        return options;
    }
    
    /**
     * Returns the name of the JAR file that is running this program.
     * 
     * @return the file name of a JAR file.
     */
    private static String getThisJARFileName() {
        return new File(App.class.getProtectionDomain()
                           .getCodeSource()
                           .getLocation()
                           .getPath())
                           .getName();
    }
}

