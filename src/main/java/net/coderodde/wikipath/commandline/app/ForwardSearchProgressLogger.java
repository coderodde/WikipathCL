package net.coderodde.wikipath.commandline.app;

import net.coderodde.graph.pathfinding.uniform.delayed.ProgressLogger;

/**
 * This class implements a forward search progress logger.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Aug 7, 2016)
 */
public class ForwardSearchProgressLogger extends ProgressLogger<String> {
    
    @Override
    public synchronized void onExpansion(final String node) {
        System.out.println(
                "[Forward search directions expands:  \"" + node + "\"]");
    }
}
