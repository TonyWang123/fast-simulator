package org.fast.core;

import java.util.List;

import org.fast.datastore.DataStore;
import org.fast.datastore.Link;

public class Function {
	
	private FastSystem fs;

	private FunctionInfo fInfo;
	
	public void setPriority(int prio) {
		fInfo.setPriority(prio);
	}
	
	public Function(int fId, int prio) {
		initializeFInfo(fId, prio);
		fs = FastSystem.getInstance();
	}
	
	public void submitFunction() {
		fs.submitFunction(this);
	}
	
	private void initializeFInfo(int fId, int prio) {
		fInfo = new FunctionInfo(fId);
		fInfo.setPriority(prio);
	}
	
	public void addOperation(Operation op) {
		fInfo.addOperation(op);
	}
	
	public FunctionInfo getFunctionInfo() {
		if (fInfo == null) {
			System.out.println("fIndo null error");
		}
		return this.fInfo;
	}
	
	public List<Link> getTopology() {
		return fs.getTopology(this);
	}
	
	public void run() {
		
	}
	
	// just apply write operations
	public void virtualRun(DataStore ds) {
		//System.out.println("function: virtual running " + this.fInfo.getFunctionId());
		for (Operation op: fInfo.getOPs()) {
			if (op instanceof WriteOperation) {
				WriteOperation wo = (WriteOperation) op;
				ds.applyWO(wo.getXId(), wo);
			}
		}
	}
}
