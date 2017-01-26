package org.fast.core;

import java.util.LinkedList;
import java.util.List;

import org.fast.tdst.DataChange;

public class Operation {

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
