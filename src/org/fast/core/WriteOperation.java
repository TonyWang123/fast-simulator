package org.fast.core;

import org.fast.tdst.DataChange;

public class WriteOperation extends Operation{

	private int fId;
	
	private String xId;
	
	public String getXId() {
		return xId;
	}
	
	public boolean isWrittenBy(Function f) {
		if (f.getFunctionInfo().getFunctionId() == fId) return true;
		else return false;
	}
	
	public int getFId() {
		return this.fId;
	}
	
	public static enum WOType {
		INC, //for Integer, add, sub
		UPDATE,
	}
	
	private WOType woType;
	
	private Object value;
	
	private Object oldValue;
	
	public WriteOperation(String xId, int fId, WOType woType, Object value, Object oldValue) {
		this.xId = xId;
		this.fId = fId;
		this.woType = woType;
		this.value = value;
		this.oldValue = oldValue;
	}
	
	public Object getNewObj() {
		return applyThisWO(oldValue);
	}
	
	public Object getOldObj() {
		return oldValue;
	}
	
	public DataChange getRevDC() {
		return new DataChange(this.xId, (Integer) this.getNewObj(), (Integer) this.oldValue);
	}
	
	public Object applyThisWO(Object obj) {
		if (this.woType.equals(WOType.INC)) {
			//System.out.println("wo: applying inc wo " + (Integer) value);
			Integer intO = (Integer) obj;
			return intO + (Integer) value;
		} else {
			return value;
		}
	}
}
