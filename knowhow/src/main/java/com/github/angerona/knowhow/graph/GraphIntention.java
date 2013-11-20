package com.github.angerona.knowhow.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This data structure is used by the planning algorithm to store the process
 * of the plan. It uses the complex intention format described in the diploma
 * thesis "Resource-bounded Planning of Communication under Confidentiality 
 * Constraints for BDI-Agents". 
 * 
 * The TBD instance is a static final instance that uses the private default 
 * ctor for creation. That ensures that there are only one instance of TBD.
 * 
 * A GraphIntention without a parent is called root intention and stored by
 * {@link WorkingPlan}, such that they can store their progress.
 * 
 * It is cloneable to support the copying of {@link WorkingPlan} in {@link Selector}
 * nodes, but the clone method throws exceptions if one tries to clone {@link GraphIntention}
 * that are no root intentions.
 * 
 * @author Tim Janus
 */
public class GraphIntention implements Cloneable {
	/** the "to be done" constant */
	public static final GraphIntention TBD = new GraphIntention();
	
	/** the selector associated with the graph intention, that is the parent of the intention attribute */
	private Selector selector;
	
	/** the processor associated with the graph intention, a processor can be seen as intention */
	private Processor intention;
	
	/** the parent intention of this graph intention, is null for root intentions */
	private GraphIntention parent;
	
	/** the list of sub-intentions */
	private List<GraphIntention> subIntentions = new ArrayList<>();
	
	/** Default Ctor: Generates the TBD GraphIntention, therefore is private */
	private GraphIntention() {}
	
	/** 
	 * Copy Ctor: Generates a copy of the given GraphIntention
	 * @param other	The instances that acts as source for the copy
	 */
	public GraphIntention(GraphIntention other) {
		this.selector = other.selector;
		this.intention = other.intention;
		
		for(GraphIntention gi : other.subIntentions) {
			if(gi == TBD) {
				this.subIntentions.add(TBD);
			} else {
				GraphIntention copy = new GraphIntention(gi);
				this.subIntentions.add(copy);
			}
		}
	}
	
	/**
	 * 
	 * @param processor
	 * @param precessor
	 * @param parent
	 */
	public GraphIntention(Processor processor, Selector precessor, GraphIntention parent) {
		this.intention = processor;
		this.selector = precessor;
		this.parent = parent;
		
		for(int i=0; i<this.intention.getChildren().size(); ++i) {
			subIntentions.add(TBD);
		}
	}
	
	/**
	 * Processes the path to the given {@link GraphIntention} 
	 * @param search	The intention that's graph is processed
	 * @return			A list of integers representing the indexes of the children.
	 * 					2,0,1 means: sub-intention with index 2 and on this graph-intention
	 * 					get the sub-intention with index zero and on this graph-intention
	 * 					get the sub-intention with index 1 to get the found node
	 * @remark		This method is used to find the current intention of a {@link WorkingPlan}
	 * 				that is generated by copying.
	 */
	public List<Integer> getPathToSubIntention(GraphIntention search) {
		List<Integer> reval = new ArrayList<>();
		if(search == this) {
			return reval;
		}
		
		int curIndex = 0;
		GraphIntention cur = this;
		while(cur != search) {
			if(curIndex <= cur.subIntentions.size() )
			{
				if(cur == this)
					break;
				cur = cur.getParent();
				curIndex = reval.remove(reval.size()-1) + 1;
				continue;
			}
			
			GraphIntention candidate = cur.subIntentions.get(curIndex);
			if(candidate == TBD) {
				curIndex +=1;
			} else {
				cur = candidate;
				reval.add(curIndex);
				curIndex = 0;
			}
		}
		
		if(cur == null)
			return null;
		
		reval.add(curIndex);
		return reval;
	}
	
	/** @return the parent of this {@link GraphIntention} or null if this is a root intention */
	public GraphIntention getParent() {
		return parent;
	}
	
	public Selector getSelector() {
		return selector;
	}
	
	/**
	 * Replaces the sub intention on the specified index by the given intention
	 * @param index		The index of the sub-intention that shall be replaced
	 * @param intention	The new sub-intention
	 */
	public void replaceSubIntention(int index, GraphIntention intention) {
		if(index < 0 || index >= subIntentions.size())
			throw new IndexOutOfBoundsException();
		if(intention == null)
			throw new IllegalArgumentException();
		
		subIntentions.set(index, intention);
	}
	
	/**
	 * @return The complexity of the {@link GraphIntention}
	 */
	public int getComplexity() {
		int complexity = 0;
		
		if(isComplete()) {
			for(GraphIntention gi : subIntentions) {
				complexity += gi.getComplexity();	
			}
		} else {
			complexity = intention.getComplexity();
		}
		
		return complexity;
	}
	
	/**
	 * Recursively counts the actions stored in this intention
	 * @return	the number of actions stored in this intention
	 */
	public int countActions() {
		int actions = 0;
		for(GraphIntention gi : subIntentions) {
			if(gi.isAtomic())
				actions += 1;
			else
				actions += gi.countActions();
		}
		return actions;
	}
	
	public List<GraphIntention> getSubIntentions() {
		return Collections.unmodifiableList(subIntentions);
	}
	
	/** @return	true if this GraphIntention is atomic and therefore represents an Action, false otherwise */
	public boolean isAtomic() {
		return intention != null && subIntentions.isEmpty();
	}
	
	/** @return true if this GraphIntention represents a complex intention that has sub-intentions */
	public boolean isComplex() {
		return intention != null && !subIntentions.isEmpty();
	}
	
	/** @return true if this GraphIntention represents a complex intention that children are already processed (complete)*/
	public boolean isComplete() {
		return isComplex() && !subIntentions.contains(TBD);
	}
	
	public Processor getNode() {
		return intention;
	}
	
	@Override
	public String toString() {
		if(intention == null)
			return "TBD";
		return intention.toString() + subIntentions.toString();
	}
	
	@Override
	public GraphIntention clone() {
		if(this == TBD || this.parent != null) {
			throw new IllegalStateException("Only root intentions are cloneable");
		}
		return new GraphIntention(this);
	}
}
