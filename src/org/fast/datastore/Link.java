package org.fast.datastore;

import org.fast.core.Function;
import org.fast.core.ReadOperation;
import org.fast.core.TestOperation;
import org.fast.core.WriteOperation;
import org.fast.core.WriteOperation.WOType;
import org.fast.tdst.LNode.OperatorType;

public class Link {
	
	public static enum Attribute {
		BANDWIDTH,
		SRC_NODE,
		DST_NODE
	}

	private int id;
	
	private Integer bw;
	
	private Node srcNode;
	
	private Node dstNode;
	
	private DataStore ds;
	
	private void setToDS() {
		String bwXId = DSUtil.constructXId(id, getAttributeId(Attribute.BANDWIDTH));
		ds.setX(bwXId, bw);
		String srcNodeXId = DSUtil.constructXId(id, getAttributeId(Attribute.SRC_NODE));
		ds.setX(srcNodeXId, srcNode);
		String dstNodeXId = DSUtil.constructXId(id, getAttributeId(Attribute.DST_NODE));
		ds.setX(dstNodeXId, dstNode);
	}
	
	public Link(int id, Node srcNode, Node dstNode, int bw, DataStore ds) {
		this.id = id;
		this.srcNode = srcNode;
		this.dstNode = dstNode;
		this.bw = bw;
		this.ds = ds;
		setToDS();
	}
	
	public int getId() {
		return this.id;
	}
	
	public Integer readBW(Function f) {
		ReadOperation ro = new ReadOperation(this.id, 
				this.getAttributeId(Link.Attribute.BANDWIDTH), this.bw);
		f.addOperation(ro);
		return (Integer) ds.getValue(DSUtil.constructXId(id, getAttributeId(Attribute.BANDWIDTH)));
	}
	
	public Node readSrcNode(Function f) {
		ReadOperation ro = new ReadOperation(this.id, 
				this.getAttributeId(Link.Attribute.SRC_NODE), this.srcNode);
		f.addOperation(ro);
		return (Node) ds.getValue(DSUtil.constructXId(id, getAttributeId(Attribute.SRC_NODE)));
	}
	
	public Node readDstNode(Function f) {
		ReadOperation ro = new ReadOperation(this.id, 
				this.getAttributeId(Link.Attribute.DST_NODE), this.dstNode);
		f.addOperation(ro);
		return (Node) ds.getValue(DSUtil.constructXId(id, getAttributeId(Attribute.DST_NODE)));
	}

	public boolean ifHasEnoughBW(Function f, int bw) {
		int currentBW = ((Integer) ds.getValue(
						DSUtil.constructXId(id, 
								getAttributeId(Attribute.BANDWIDTH)))).intValue();
		//System.out.println("link: currentBW " + currentBW);
		if (currentBW >= bw) {
			TestOperation to = new TestOperation(this.id, 
					this.getAttributeId(Link.Attribute.BANDWIDTH), 
					OperatorType.GEQ, bw, currentBW, f.getFunctionInfo());
			f.addOperation(to);
			return true;
		} else {
			TestOperation to = new TestOperation(this.id, 
					this.getAttributeId(Link.Attribute.BANDWIDTH), 
					OperatorType.L, bw, currentBW, f.getFunctionInfo());
			f.addOperation(to);
			return false;
		}
	}
	
	public void reduceBw(Function f, int bw) {
		WriteOperation wo = new WriteOperation(DSUtil.constructXId(id, getAttributeId(Attribute.BANDWIDTH)), 
				f.getFunctionInfo().getFunctionId(), WOType.INC, -bw, 
				ds.getValue(DSUtil.constructXId(id, getAttributeId(Attribute.BANDWIDTH))));
		f.addOperation(wo);
		ds.applyWO(DSUtil.constructXId(id, getAttributeId(Attribute.BANDWIDTH)), wo);
	}
	
	public int getAttributeId(Attribute att) {
		switch (att) {
		case BANDWIDTH:
			return 1;
		case SRC_NODE:
			return 2;
		case DST_NODE:
			return 3;
		}
		return -1;
	}
	
	public Object getAttributeObject(int attId) {
		switch (attId) {
		case 1:
			return this.bw;
		case 2:
			return this.srcNode;
		case 3:
			return this.dstNode;
		}
		return null;
	}
}
