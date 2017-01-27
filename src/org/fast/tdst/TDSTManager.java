package org.fast.tdst;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TDSTManager {

	Map<String, TDST> xId2TDST = new HashMap<String, TDST>();
	
	public Collection<TDST> getAllTDST() {
		return xId2TDST.values();
	}
	
	public TDST get(String xId) {
		if (xId2TDST.containsKey(xId)) {
			return xId2TDST.get(xId);
		} else {
			TDST tdst = new TDST(xId);
			xId2TDST.put(xId, tdst);
			return tdst;
		}
	}
}
