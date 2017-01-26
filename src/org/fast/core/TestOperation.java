package org.fast.core;

import org.fast.datastore.DSUtil;
import org.fast.tdst.LNode;
import org.fast.tdst.LNode.OperatorType;

public class TestOperation extends Operation{

	private int dataId;
	
	private int attributeId;
	
	private OperatorType operator;
	
	private Object testValue;
	
	private Object realValue;
	
	private FunctionInfo fi;
	
	public TestOperation(int dataId, int attributeId, OperatorType operator, Object testValue, 
			Object realValue, FunctionInfo fi) {
		this.dataId = dataId;
		this.attributeId = attributeId;
		this.operator = operator;
		this.testValue = testValue;
		this.realValue = realValue;
		this.fi = fi;
	}
	
	public boolean isThisX(String xId) {
		String currentXId = DSUtil.constructXId(dataId, attributeId);
		if (currentXId.equals(xId)) return true;
		else return false;
	}
	
	public LNode convert2LNode(int functionId) {
		LNode lNode = new LNode(functionId, (Integer) testValue, (Integer) realValue, operator, fi);
		return lNode;
	}
	
}
