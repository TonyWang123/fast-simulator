package org.fast.pce;

import java.util.LinkedList;
import java.util.List;

import org.fast.core.Function;
import org.fast.datastore.Link;
import org.fast.datastore.Node;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;

public class PCRequest extends Function{
	
	int bw;
	
	int srcId;
	
	int dstId;
	
	public PCRequest(int id, int bw, int prio, int srcId, int dstId) {
		super(id, prio);
		this.bw = bw;
		this.srcId = srcId;
		this.dstId = dstId;
	}
	
	Graph<Node, Link> networkGraph = null;
	DijkstraShortestPath<Node, Link> shortestPath = null;
	
	List<Link> lastTimeLinks = new LinkedList<Link>();
	
	private List<Link> computePath(List<Link> availableLinks) {
		if (networkGraph == null) {
			networkGraph = new SparseMultigraph<>();
		} else {
			for (Link l: lastTimeLinks) {
				networkGraph.removeEdge(l);
			}
		}
		lastTimeLinks.clear();
		
		if (shortestPath == null) {
			shortestPath = new DijkstraShortestPath<>(networkGraph);
		} else {
			shortestPath.reset();
		}
		
		Node tarSNode = null;
		Node tarDNode = null;
		for (Link link: availableLinks) {
			Node srcNode = link.readSrcNode(this);
			Node dstNode = link.readDstNode(this);
			if (srcNode.getId() == srcId) {
				tarSNode = srcNode;
			}
			if (dstNode.getId() == dstId) {
				tarDNode = dstNode;
			}
			networkGraph.addVertex(srcNode);
			networkGraph.addVertex(link.readDstNode(this));
			networkGraph.addEdge(link, srcNode, dstNode, EdgeType.DIRECTED);
			lastTimeLinks.add(link);
		}
		if (tarSNode == null || tarDNode == null) {
			return null;
		}
		List<Link> path = null;
		try {
			path = shortestPath.getPath(tarSNode, tarDNode);
			return path;
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public void run() {
		this.getFunctionInfo().clearOPs();
		
		//System.out.println("pcr: running " + getFunctionInfo().getFunctionId());
		List<Link> links = getTopology();
		
		List<Link> availableLinks = new LinkedList<Link>();
		
		for (Link link: links) {
			if (link.ifHasEnoughBW(this, this.bw)) {
				availableLinks.add(link);
			}
		}
		
		List<Link> usedLinks = computePath(availableLinks);
		
		if (usedLinks == null || usedLinks.isEmpty()) {
			//System.out.println("pcr: fail for " + getFunctionInfo().getFunctionId());
		} else {
			//System.out.println("pcr: success for " + getFunctionInfo().getFunctionId());
			for (Link link: usedLinks) {
				link.reduceBw(this, this.bw);
			}
		}
		//System.out.println("pcr: finish running " + getFunctionInfo().getFunctionId());
	}
}
