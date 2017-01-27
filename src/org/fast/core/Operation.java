package org.fast.core;

import java.util.LinkedList;
import java.util.List;

import org.fast.tdst.DataChange;
import org.fast.tdst.LNode;

public class Operation {
	
	public static List<LNode> convertToLNs(List<TestOperation> toList) {
		List<LNode> lns = new LinkedList<LNode>();
		for (TestOperation to: toList) {
			LNode ln = to.convert2LNode();
			lns.add(ln);
		}
		return lns;
	}

	public static List<DataChange> convertToDCs(List<WriteOperation> woList) {
		List<DataChange> dcList = new LinkedList<DataChange>();
		for (WriteOperation wo: woList) {
			DataChange dc = new DataChange(wo.getXId(), (Integer) wo.getOldObj(), 
					(Integer) wo.getNewObj());
			dcList.add(dc);
		}
		return dcList;
	}
}
