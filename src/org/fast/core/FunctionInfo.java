package org.fast.core;

import java.util.LinkedList;
import java.util.List;

import org.fast.tdst.LNode;

public class FunctionInfo {
	
	private int priority;
	
	private List<Operation> operations = new LinkedList<Operation>();
	
	private int functionId;
	
	public FunctionInfo(int functionId) {
		this.functionId = functionId;
	}
	
	public void clearOPs() {
		this.operations.clear();
	}
	
	public LNode getLNode(String xId) {
		for (Operation op: operations) {
			if (op instanceof TestOperation) {
				TestOperation to = (TestOperation) op;
				if (to.isThisX(xId)) {
					return to.convert2LNode(functionId);
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
