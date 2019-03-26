package mergeData.mergeGraphs;

/**
 * The <code>GraphType</code> specifies which graph something can be found in.
 *
 * @author Ken Samuel
 * @version 1.0, Mar 4, 2015
 * @since 1.6
 */
public enum GraphType {

	/** 
	 * A <code>VIDEO_GRAPH</code> <code>GraphType</code> contains information collected from analyzing videos. 
	 */
	VIDEO_GRAPH,

	/** 
	 * A <code>TEXT_GRAPH</code> <code>GraphType</code>  contains information collected from analyzing text.
	 */
	TEXT_GRAPH,

	/** 
	 * A <code>MERGED_GRAPH</code> <code>GraphType</code> is the result of merging a video graph with a text
	 * graph.
	 */
	MERGED_GRAPH,
}
