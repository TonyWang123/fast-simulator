package org.fast.tdst;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DataChange {
	
	private String xid;

	private int oldValue;
	
	private int newValue;
	
	public DataChange(String xid, int oldValue, int newValue) {
		this.xid = xid;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}
	
	public String geteXId() {
		return this.xid;
	}
	
	public int getOldValue() {
		return oldValue;
	}
	
	public int getNewValue() {
		return newValue;
	}
	
	public int getDelta() {
		return newValue - oldValue;
	}
	
	private static DataChange constructDCByMerging(String xid, List<DataChange> dcs) {
		return new DataChange(xid, dcs.get(0).getOldValue(), dcs.get(dcs.size() - 1).getNewValue());
	}
	
	public static List<DataChange> mergeDCs(List<DataChange> DC1, List<DataChange> DC2) {
		System.out.println("dc: dc1.size " + DC1.size());
		System.out.println("dc: dc2.size " + DC2.size());
		List<DataChange> returnDC = new LinkedList<DataChange>();
		Map<String, List<DataChange>> xid2dc = new HashMap<String, List<DataChange>>();
		for (DataChange dc1: DC1) {
			String xid = dc1.geteXId();
			if (xid2dc.containsKey(xid)) {
				xid2dc.get(xid).add(dc1);
			} else {
				List<DataChange> temp = new LinkedList<DataChange>();
				temp.add(dc1);
				xid2dc.put(xid, temp);
			}
		}
		for (DataChange dc2: DC2) {
			String xid = dc2.geteXId();
			if (xid2dc.containsKey(xid)) {
				xid2dc.get(xid).add(dc2);
			} else {
				List<DataChange> temp = new LinkedList<DataChange>();
				temp.add(dc2);
				xid2dc.put(xid, temp);
			}
		}
		for (Map.Entry<String, List<DataChange>> entry: xid2dc.entrySet()) {
			String xid = entry.getKey();
			List<DataChange> dcs = entry.getValue();
			DataChange dc = constructDCByMerging(xid, dcs);
			returnDC.add(dc);
		}
		System.out.println("dc: returnDC.size " + returnDC.size());
		return returnDC;
	}
}
