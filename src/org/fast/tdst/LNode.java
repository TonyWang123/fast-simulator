package org.fast.tdst;

import org.fast.core.FunctionInfo;

public class LNode extends TDSTNode{
	
	// t - x comparing with 0
	public static enum OperatorType {
		G,
		GEQ,
		L,
		LEQ,
		EQ,
		NEQ
	}
	
	private int id;
	
	private int t;
	
	private int x;
	
	private OperatorType ot;
	
	private INode fatherNode;
	
	private FunctionInfo fi;
	
	public LNode(int id, int t, int x, OperatorType ot, FunctionInfo fi) {
		this.id = id;
		this.t = t;
		this.x = x;
		this.ot = ot;
		this.fi = fi;
	}
	
	public FunctionInfo getFunctionInfo() {
		return this.fi;
	}
	
	public int getFunctionId() {
		return id;
	}
	
	public int getID() {
		return this.id;
	}
	
	public int getX() {
		return this.x;
	}
	
	public INode getFatherNode() {
		return fatherNode;
	}

	public void setFatherNode(INode fatherNode) {
		this.fatherNode = fatherNode;
	}

	public int getD() {
		if (ot.equals(OperatorType.GEQ)) return t - x + 1;
		if (ot.equals(OperatorType.LEQ)) return t - x - 1;
		return t - x;
	}
	
	public boolean isInfected(DataChange dc) {
		if (ot.equals(OperatorType.EQ)) {
			if (dc.getDelta() != 0) return true;
			else return false;
		} else if (ot.equals(OperatorType.NEQ)) {
			if (dc.getNewValue() == this.t) return true;
			else return false;
		} else if (ot.equals(OperatorType.G)) {
			if (this.t - (this.x + dc.getDelta()) <= 0) return true;
			else return false;
		} else if (ot.equals(OperatorType.L)) {
			if (this.t - (this.x + dc.getDelta()) >= 0) return true;
			else return false;
		} else if (ot.equals(OperatorType.GEQ)) {
			if (this.t - (this.x + dc.getDelta()) < 0) return true;
			else return false;
		} else if (ot.equals(OperatorType.LEQ)) {
			if (this.t - (this.x + dc.getDelta()) > 0) return true;
			else return false;
		}
		return false;
	}
}
