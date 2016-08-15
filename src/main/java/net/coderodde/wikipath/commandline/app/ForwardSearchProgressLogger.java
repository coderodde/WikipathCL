package net.coderodde.wikipath.commandline.app;

/**
 * This class implements a forward search progress logger.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.61 (Aug 15, 2016)
 */
public class ForwardSearchProgressLogger extends AbstractSearchProgressLogger {
    
    @Override
    public synchronized void onExpansion(final String node) {
        super.onExpansion(node);
        System.out.println(
                "[Forward search directions expands:  \"" + node + "\"]");
    }
}
