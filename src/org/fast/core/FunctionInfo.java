package org.fast.core;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.fast.tdst.LNode;

public class FunctionInfo {
	
	private int priority;
	
	private List<Operation> operations = new LinkedList<Operation>();
	
	private Map<String, LNode> xid2LNode = new HashMap<String, LNode>();
	
	private int functionId;
	
	public FunctionInfo(int functionId) {
		this.functionId = functionId;
	}
	
	public void clearOPs() {
		this.operations.clear();
	}
	
	public boolean containsXId(String xId) {
		if (xid2LNode.containsKey(xId)) return true;
		else return false;
	}
	
	public List<LNode> getAllLNodes() {
		List<LNode> ll = new LinkedList<LNode>();
		for (Operation op: operations) {
			if (op instanceof TestOperation) {
				TestOperation to = (TestOperation) op;
				LNode lNode = to.convert2LNode();
				ll.add(lNode);
				this.xid2LNode.put(lNode.getXId(), lNode);
			}
		}
		return ll;
	}
	
	public LNode getLNode(String xId) {
		if (xid2LNode.containsKey(xId)) {
			return xid2LNode.get(xId);
		}
		for (Operation op: operations) {
			if (op instanceof TestOperation) {
				TestOperation to = (TestOperation) op;
				if (to.isThisX(xId)) {
					LNode lNode = to.convert2LNode();
					this.xid2LNode.put(xId, lNode);
					return lNode;
				}
			}
		}
		return null;
	}
	
	public List<Operation> getOPs() {
		return this.operations;
	}
	
	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getFunctionId() {
		return functionId;
	}
	
	public void addOperation(Operation op) {
		this.operations.add(op);
	}
}
