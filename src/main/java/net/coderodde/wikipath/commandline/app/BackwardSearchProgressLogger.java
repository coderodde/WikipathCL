package net.coderodde.wikipath.commandline.app;

import net.coderodde.graph.pathfinding.uniform.delayed.ProgressLogger;

/**
 * This class implements a forward search progress logger.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Aug 7, 2016)
 */
public class BackwardSearchProgressLogger extends ProgressLogger<String> {
    
    @Override
    public synchronized void onExpansion(final String node) {
        System.out.println(
                "[Backward search directions expands: \"" + node + "\"]");
    }
}
