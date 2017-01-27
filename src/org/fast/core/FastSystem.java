package org.fast.core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fast.datastore.DataStore;
import org.fast.datastore.Link;
import org.fast.datastore.Node;
import org.fast.tdst.DataChange;
import org.fast.tdst.LNode;
import org.fast.tdst.TDST;
import org.fast.tdst.TDSTManager;

public class FastSystem {
	
	private DataStore ds = new DataStore();
	
	private TDSTManager tm = new TDSTManager();
	
	private List<Link> linkList = new LinkedList<Link>();
	
	private static FastSystem fs = null;
	
	private List<Function> functionList = new LinkedList<Function>();
	
	public FastSystem() {
		initializeTopology();
	}
	
	public static FastSystem getInstance() {
		if (fs == null) {
			fs = new FastSystem();
		}
		return fs;
	}
	
	public static int MinBwL = 100000;
	
	public static int ScaleL = 2;
	
	private void fromLines2Links(List<String> lines) {
		int dataId = 0;
		Map<String, Node> nodeNames = new HashMap<String, Node>();
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			//line := src dst
			String[] srcDst = line.split(" ");
			String srcName = srcDst[0];
			String dstName = srcDst[1];
			if (!nodeNames.containsKey(srcName)) {
				Node srcNode = new Node(dataId++);
				nodeNames.put(srcName, srcNode);
			}
			
			if (!nodeNames.containsKey(dstName)) {
				Node dstNode = new Node(dataId++);
				nodeNames.put(dstName, dstNode);
			}
		}
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			//line := src dst
			String[] srcDst = line.split(" ");
			String srcName = srcDst[0];
			String dstName = srcDst[1];
			Node srcNode = null;
			Node dstNode = null;
			if (!nodeNames.containsKey(srcName)) {
				//error
			} else {
				srcNode = nodeNames.get(srcName);
			}
			if (!nodeNames.containsKey(dstName)) {
				//error
			} else {
				dstNode = nodeNames.get(dstName);
			}
			int bw = (int )(Math.random() * MinBwL * (ScaleL - 1) + MinBwL);
			if (srcNode == null || dstNode == null) System.out.println("node is null");
			Link link1 = new Link(dataId++, srcNode, dstNode, bw, ds);
			linkList.add(link1);
			Link link2 = new Link(dataId++, dstNode, srcNode, bw, ds);
			linkList.add(link2);
		}
	}
	
	public void initializeTopology() {
		String fileName = "/Users/tony/PycharmProjects/topologyzoo/Abvt.gml.format";
		
		List<String> lines = new LinkedList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String line = br.readLine();
			while (line != null)
			{
				lines.add(line);
			    line = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			
		}
		
		fromLines2Links(lines);
		
		// write links to ds (in link java file) and update linkIdList
		/*Node n1 = new Node(1);
		Node n2 = new Node(2);
		//Node n3 = new Node(3);
		Link l1 = new Link(3, n1, n2, 100, ds);
		//Link l2 = new Link(5, n2, n3, 100, ds);
		linkList.add(l1);
		//linkList.add(l2);*/
	}
	
	// should record to opetation?
	public List<Link> getTopology(Function f) {
		return this.linkList;
	}
	
	private Function findTheEarliestNextFunction(Function f) {
		for (int i = 0; i < this.functionList.size(); i++) {
			Function fi = this.functionList.get(i);
			if (fi.getFunctionInfo().getPriority() > f.getFunctionInfo().getPriority()) {
				return fi;
			}
		}
		return null;
	}
	
	private List<TestOperation> getTOList(FunctionInfo fi) {
		List<TestOperation> toList = new LinkedList<TestOperation>();
		for (Operation op: fi.getOPs()) {
			if (op instanceof TestOperation) {
				toList.add((TestOperation) op);
			}
		}
		return toList;
	}
	
	private List<WriteOperation> getWOList(FunctionInfo fi) {
		List<WriteOperation> woList = new LinkedList<WriteOperation>();
		for (Operation op: fi.getOPs()) {
			if (op instanceof WriteOperation) {
				woList.add((WriteOperation) op);
			}
		}
		return woList;
	}
	
	private Function getFirstRerunFunction(Function theEarliestNextFunction, 
			Set<Integer> infectedFunctionIds) {
		for (int i = functionList.indexOf(theEarliestNextFunction); 
				i < functionList.size(); i++) {
			Function f = functionList.get(i);
			if (infectedFunctionIds.contains(f.getFunctionInfo().getFunctionId())) {
				return f;
			}
		}
		return null;
	}
	
	private void rerunAndFastForwardToTheNextFunction(Function rerunF, List<DataChange> oldDCs) {
		Function fNextToRerunF = null;
		if (functionList.indexOf(rerunF) != functionList.size() - 1) {
			// not the last one
			fNextToRerunF = functionList.get(functionList.indexOf(rerunF) + 1);
		}
		rerunNum++;
		rerunF.run();
		//System.out.println("fs: start find first infected function after rerun");
		List<DataChange> DCs = Operation.convertToDCs(getWOList(rerunF.getFunctionInfo()));
		List<DataChange> updatedDCs = DataChange.mergeDCs(oldDCs, DCs);
		Set<Integer> infectedFunctionIds = new HashSet<Integer>();
		for (DataChange dc: updatedDCs) {////////////////////////////////////
			String xId = dc.geteXId();
			TDST tdst = tm.get(xId);
			LNode lNode = rerunF.getFunctionInfo().getLNode(xId);
			tdst.updateMinMaxContent(lNode);
			LNode firstInfectedLNode = tdst.findFirstInfectedLNode(dc);
			if (firstInfectedLNode != null) {
				int firstInfectedFunctionId = firstInfectedLNode.getFunctionId();
				infectedFunctionIds.add(firstInfectedFunctionId);
			}
			tdst.recoverMinMaxContent(lNode);
			/*// TODO should modify this part
			for (int i = 0; i < functionList.indexOf(fNextToRerunF); i++) {
				LNode lNode = functionList.get(i).getFunctionInfo().getLNode(xId);
				if (lNode != null) {
					tdst.deleteLNode(lNode);
				}
			}
			LNode firstInfectedLNode = tdst.findFirstInfectedLNode(dc);
			
			if (firstInfectedLNode != null) {
				int firstInfectedFunctionId = firstInfectedLNode.getFunctionId();
				infectedFunctionIds.add(firstInfectedFunctionId);
			}
			
			//add back deleted lnodes
			for (int i = functionList.indexOf(rerunF); i >= 0; i--) {
				LNode lNode = functionList.get(i).getFunctionInfo().getLNode(xId);
				if (lNode != null) {
					tdst.insertLNodeFromStart(lNode);
				}
			}*/
		}
		//System.out.println("fs: end find first infected function after rerun");
		// need to rerun this function
		Function fromThisFunctionRerun = getFirstRerunFunction(fNextToRerunF, 
				infectedFunctionIds);
		
		if (fromThisFunctionRerun == null) {
			for (int i = functionList.indexOf(fNextToRerunF); 
					i < functionList.size(); i++) {
				Function fVirtualRun = functionList.get(i);
				fVirtualRun.virtualRun(ds);
			}
		} else {
			for (int i = functionList.indexOf(fNextToRerunF); 
					i < functionList.indexOf(fromThisFunctionRerun); i++) {
				Function fVirtualRun = functionList.get(i);
				fVirtualRun.virtualRun(ds);
			}
			
			rerunAndFastForwardToTheNextFunction(fromThisFunctionRerun, updatedDCs);
		}
		
	}
	
	int rerunNum = 0;
	
	public void submitFunction(Function f) {
		long startTime = System.currentTimeMillis();
		Function theEarliestNextFunction = findTheEarliestNextFunction(f);
		if (theEarliestNextFunction == null) {
			//insert at the end
			functionList.add(f);
			f.run();
			System.out.println("finish time: " + (System.currentTimeMillis() - startTime));
			//insert this f lnode
			List<LNode> LNs = f.getFunctionInfo().getAllLNodes();
			//List<LNode> LNs = Operation.convertToLNs(getTOList(f.getFunctionInfo()));
			for (LNode lNode: LNs) {
				String xId = lNode.getXId();
				TDST tdst = tm.get(xId);
				//add this lnode
				//System.out.println("fs: inserted lNode " + lNode.getFunctionId() + " for " + xId);
				tdst.insertLNode(lNode);
			}
		} else {
			//insert just before that function
			ds.rollbackBefore(theEarliestNextFunction);
			f.run();
			//System.out.println("fs: start find first infected function in submission");
			List<DataChange> DCs = Operation.convertToDCs(getWOList(f.getFunctionInfo()));
			Set<Integer> infectedFunctionIds = new HashSet<Integer>();
			for (DataChange dc: DCs) {
				System.out.println("fs: in the loop of dc " + dc.geteXId());
				String xId = dc.geteXId();
				TDST tdst = tm.get(xId);
				if (theEarliestNextFunction.getFunctionInfo().containsXId(xId)) {
					//System.out.println("fs: fi correct");
				} else {
					//System.out.println("fs: fi wrong");
				}
				LNode lNode = theEarliestNextFunction.getFunctionInfo().getLNode(xId);
				//System.out.println("fs: need to update lNode " + lNode.getFunctionId() + " for " + xId);
				tdst.updateMinMaxContent(lNode);
				LNode firstInfectedLNode = tdst.findFirstInfectedLNode(dc);
				if (firstInfectedLNode != null) {
					int firstInfectedFunctionId = firstInfectedLNode.getFunctionId();
					infectedFunctionIds.add(firstInfectedFunctionId);
				}
				tdst.recoverMinMaxContent(lNode);
				/*// TODO should modify this part
				for (int i = 0; i < functionList.indexOf(theEarliestNextFunction); i++) {
					LNode lNode = functionList.get(i).getFunctionInfo().getLNode(xId);
					if (lNode != null) {
						tdst.deleteLNode(lNode);
					}
				}
				LNode firstInfectedLNode = tdst.findFirstInfectedLNode(dc);
				
				if (firstInfectedLNode != null) {
					int firstInfectedFunctionId = firstInfectedLNode.getFunctionId();
					infectedFunctionIds.add(firstInfectedFunctionId);
				}
				
				//add this lnode
				LNode thislNode = f.getFunctionInfo().getLNode(xId);
				tdst.insertLNodeFromStart(thislNode);
				
				//add back deleted lnodes
				for (int i = functionList.indexOf(theEarliestNextFunction) - 1; 
						i >= 0; i--) {
					LNode lNode = functionList.get(i).getFunctionInfo().getLNode(xId);
					if (lNode != null) {
						tdst.insertLNodeFromStart(lNode);
					}
				}*/
			}
			System.out.println("fs: before getFirstRerunFunction");
			//System.out.println("fs: end find first infected function in submission");
			// need to rerun this function
			Function fromThisFunctionRerun = getFirstRerunFunction(theEarliestNextFunction, 
					infectedFunctionIds);
			System.out.println("fs: after getFirstRerunFunction");
			
			if (fromThisFunctionRerun == null) {
				for (int i = functionList.indexOf(theEarliestNextFunction); 
						i < functionList.size(); i++) {
					Function fVirtualRun = functionList.get(i);
					fVirtualRun.virtualRun(ds);
				}
			} else {
				System.out.println("fs fromThisFunctionRerun is not null");
				for (int i = functionList.indexOf(theEarliestNextFunction); 
						i < functionList.indexOf(fromThisFunctionRerun); i++) {
					Function fVirtualRun = functionList.get(i);
					fVirtualRun.virtualRun(ds);
				}
				
				/*for (int i = functionList.indexOf(fromThisFunctionRerun); 
						i < functionList.size(); i++) {
						Function fRealRun = functionList.get(i);
						fRealRun.run();
						rerunNum++;
				}*/
				
				rerunAndFastForwardToTheNextFunction(fromThisFunctionRerun, DCs);
			}
			System.out.println("finish time: " + (System.currentTimeMillis() - startTime));
			//insert this f lnode
			for (TDST tdst: tm.getAllTDST()) {
				// TODO should modify this part
				System.out.println("fs: loop in tdst" + tdst.getXId());
				for (int i = 0; i < functionList.indexOf(theEarliestNextFunction); i++) {
					LNode lNode = functionList.get(i).getFunctionInfo().getLNode(tdst.getXId());
					if (lNode != null) {
						tdst.deleteLNode(lNode);
					}
				}
				
				//add this lnode
				LNode thislNode = f.getFunctionInfo().getLNode(tdst.getXId());
				tdst.insertLNodeFromStart(thislNode);
				
				//add back deleted lnodes
				for (int i = functionList.indexOf(theEarliestNextFunction) - 1; 
						i >= 0; i--) {
					LNode lNode = functionList.get(i).getFunctionInfo().getLNode(tdst.getXId());
					if (lNode != null) {
						tdst.insertLNodeFromStart(lNode);
					}
				}
			}
			functionList.add(functionList.indexOf(theEarliestNextFunction), f);
		}
		System.out.println("fs: rerun num: " + rerunNum);
		rerunNum = 0;
	}
}
