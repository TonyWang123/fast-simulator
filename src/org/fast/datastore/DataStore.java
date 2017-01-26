package org.fast.datastore;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.fast.core.Function;
import org.fast.core.WriteOperation;

public class DataStore {
	
	Map<String, Object> xId2InitialObj = new HashMap<String, Object>();
	
	Map<String, List<WriteOperation>> xId2Log = new HashMap<String, List<WriteOperation>>();
	
	public void setX(String xId, Object obj) {
		this.xId2InitialObj.put(xId, obj);
	}
	
	public void applyWO(String xId, WriteOperation wo) {
		if (xId2Log.containsKey(xId)) {
			List<WriteOperation> logs = xId2Log.get(xId);
			logs.add(wo);
			//System.out.println("ds: puting log into xId2Log by " + wo.getFId());
			xId2Log.put(xId, logs);
		} else {
			List<WriteOperation> logs = new LinkedList<WriteOperation>();
			logs.add(wo);
			//System.out.println("ds: logs.size: " + logs.size());
			xId2Log.put(xId, logs);
		}
	}
	
	public void rollbackBefore(Function f) {
		for (Map.Entry<String, List<WriteOperation>> entry: xId2Log.entrySet()) {
			List<WriteOperation> logs = entry.getValue();
			int woIndex = 0;
			for (int i = 0; i < entry.getValue().size(); i++) {
				if (logs.get(i).isWrittenBy(f)) {
					woIndex = i;
					break;
				}
			}
			while (logs.size() > woIndex) {
				//System.out.println("ds: removing log from xId2Log of " + logs.get(woIndex).getFId());
				logs.remove(woIndex);
			}
		}
	}
	
	private Object computeValue(String xId, List<WriteOperation> logs) {
		Object obj = xId2InitialObj.get(xId);
		if (logs != null && !logs.isEmpty()) {
			WriteOperation wo = logs.get(logs.size() - 1);
			Object oldObj = wo.getOldObj();
			obj = wo.applyThisWO(oldObj);
		}
		return obj;
	}
	
	public Object getValue(String xId) {
		
		//System.out.println("ds: start getValue");
		Object obj = computeValue(xId, this.xId2Log.get(xId));
		//System.out.println("ds: finish getValue");
		return obj;
	}
}
