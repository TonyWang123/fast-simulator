package org.fast.tdst;

import java.util.LinkedList;
import java.util.List;

import org.fast.core.FunctionInfo;
import org.fast.core.Operation;
import org.fast.core.WriteOperation;

public class TDST {

	// including data id and attribute id
	String xid;
	
	TDSTNode rootNode;
	
	List<LNode> leafNodes = new LinkedList<LNode>();
	
	public TDST(String xid) {
		this.xid = xid;
	}
	
	//return the first infected lnode
	public LNode findFirstInfectedLNode(DataChange dc) {
		if (rootNode == null) {
			return null;
		}
		if (rootNode instanceof LNode) {
			LNode lNode = (LNode) rootNode;
			if (lNode.isInfected(dc)) return lNode;
			else return null;
		} else {
			INode iNode = (INode) rootNode;
			return iNode.getFirstInfectedLNode(dc);
		}
	}
	
	// input is the earliest infected lnode in the original lnode list
	public static int ComputeNewState(LNode theEarliestLNode, DataChange dc) {
		return theEarliestLNode.getX() + dc.getDelta();
	}
	
	/*private void removeLNodeFromLeafNodes(LNode lNode) {
		for (LNode l: this.leafNodes) {
			if (l.getID() == lNode.getID()) {
				this.leafNodes.remove(l);
			}
		}
	}*/
		
	public void deleteLNode(LNode lNode) {
		if (rootNode == null) {
			return;
		}
		if (rootNode instanceof LNode) {
			LNode rootLNode = (LNode) rootNode;
			if (rootLNode.getID() == lNode.getD()) {
				rootNode = null;
			}
		} else {
			INode rootINode = (INode) rootNode;
			INode newRootINode = rootINode.deleteLNode(lNode);
			this.rootNode = newRootINode;
		}
		//removeLNodeFromLeafNodes(lNode);
		this.leafNodes.remove(lNode);
	}
	
	public void insertLNodeFromStart(LNode lNode) {
		if (rootNode == null) {
			rootNode = lNode;
			this.leafNodes.add(0, lNode);
			return;
		}
		LNode firstLNode = leafNodes.get(0);
		INode iNode = firstLNode.getFatherNode();
		if (iNode != null) {
			INode newINode = iNode.insertNode(lNode);
			if (newINode != null) this.rootNode = newINode;
			this.leafNodes.add(0, lNode);
		}
	}
	
	/*public void insertLNodeBefore(LNode lNode, LNode beforeLNode) {
		INode fatherOfBL = beforeLNode.getFatherNode();
		INode newRootNode = fatherOfBL.replace(beforeLNode, lNode); //should update in this function
		if (newRootNode != null) {
			this.rootNode = newRootNode;
		}
	}*/
	
	public void insertLNode(LNode lNode) {
		if (rootNode == null) {
			rootNode = lNode;
			this.leafNodes.add(lNode);
			return;
		}
		
		LNode lastLNode = leafNodes.get(leafNodes.size() - 1);
		INode iNode = lastLNode.getFatherNode();
		INode newINode = iNode.insertNode(lNode);
		if (newINode != null) this.rootNode = newINode;
		this.leafNodes.add(lNode);
	}
	
	// the input lNode is an existing lnode in this tdst
	private DataChange getRevDCForThisTDST(LNode lNode) {
		FunctionInfo fi = lNode.getFunctionInfo();
		List<Operation> ops = fi.getOPs();
		for (Operation op: ops) {
			if (op instanceof WriteOperation) {
				WriteOperation wo = (WriteOperation) op;
				if (wo.getXId().equals(this.xid)) {
					DataChange dc = wo.getRevDC();
					return dc;
				}
			}
		}
		return null;
	}
	
	// input lNode is an existing lNode, and here we try to get a first lNode assuming if input lNode is not here
	public LNode testForward(LNode lNode) {
		DataChange dc = getRevDCForThisTDST(lNode);
		int startPos = this.leafNodes.indexOf(lNode);
		LNode returnLNode = null;
		for (int i = startPos; i < this.leafNodes.size(); i++) {
			LNode testLNode = this.leafNodes.get(i);
			if (testLNode.getFunctionInfo().getPriority() > lNode.getFunctionInfo().getPriority()) {
				break;
			}
			if (testLNode.isInfected(dc)) {
				returnLNode = testLNode;
				break;
			}
		}
		return returnLNode;
	}
	
	// this lNode is the new inserted one
	// should call this method after the insert
	public void updateMinMaxContent(LNode lNode) {
		if (this.rootNode instanceof LNode) {
			// this rootNode should be the input lNode
			return;
		}
		INode fatherINode = lNode.getFatherNode();
		fatherINode.updateMinMaxContent(lNode);
	}
	
	public void recoverMinMaxContent(LNode lNode) {
		INode fatherINode = lNode.getFatherNode();
		fatherINode.reUpdateMinMaxValueToTheRoot();
	}
}
