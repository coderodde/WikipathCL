package net.coderodde.wikipath.commandline.app;

import net.coderodde.graph.pathfinding.uniform.delayed.ProgressLogger;

/**
 * This abstract class implements the facilities shared by both the search 
 * directions.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 
 */
public abstract class AbstractSearchProgressLogger 
extends ProgressLogger<String> {

    /**
     * Caches the number of expanded nodes in a particular search direction
     * during the previous search.
     */
    protected int numberOfExpandedNodes;
    
    /**
     * Caches the number of generated neighbors in a particular search direction
     * during the previous search.
     */
    protected int numberOfGeneratedNeighbours;
    
    /**
     * Caches the number of times a particular search direction has improved a
     * distance estimate.
     */
    protected int numberOfImprovedNeighbours;
    
    @Override
    public void onBeginSearch(final String source, final String target) {
        this.numberOfExpandedNodes       = 0;
        this.numberOfGeneratedNeighbours = 0;
        this.numberOfImprovedNeighbours  = 0;
    }
    
    @Override
    public synchronized void onExpansion(final String node) {
        ++numberOfExpandedNodes;
    }
    
    @Override
    public synchronized void onNeighborGeneration(final String node) {
        ++numberOfGeneratedNeighbours;
    }
    
    public synchronized void onNeighborImprovement(final String node) {
        ++numberOfImprovedNeighbours;
    }
    
    /**
     * Returns the number of expanded nodes found in the previous search.
     * 
     * @return the number of expanded nodes.
     */
    public int getNumberOfExpandedNodes() {
        return numberOfExpandedNodes;
    }
    
    /**
     * Returns the number of generated nodes found in the previous search.
     * 
     * @return the number of generated nodes.
     */
    public int getNumberOfGeneratedNeighbours() {
        return numberOfGeneratedNeighbours;
    }
    
    /**
     * Returns the number of improved nodes. A node is improved, if its shortest
     * path distance estimate was lowered during the search.
     * 
     * @return the number of improved neighbors.
     */
    public int getNumberOfImprovedNeighbours() {
        return numberOfImprovedNeighbours;
    }
}
