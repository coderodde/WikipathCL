package net.coderodde.wikipath.commandline.app;

/**
 * This class implements a forward search progress logger.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Aug 7, 2016)
 */
public class BackwardSearchProgressLogger 
extends AbstractSearchProgressLogger {
     
    @Override
    public synchronized void onExpansion(final String node) {
        super.onExpansion(node);
        System.out.println(
                "[Backward search directions expands: \"" + node + "\"]");
    }
}
