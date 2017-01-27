package org.fast.tdst;

import java.util.HashSet;
import java.util.Set;

public class INode extends TDSTNode{
		
	private Set<Integer> lNodeIds = new HashSet<Integer>();
	
	private int minPosD = Integer.MAX_VALUE;
	
	private int maxNegD = Integer.MIN_VALUE;
	
	private INode fatherNode;
	
	private TDSTNode[] childNodes = new TDSTNode[2];
	
	public boolean includingLNode(int lNodeId) {
		if (lNodeIds.contains(lNodeId)) return true;
		else return false;
	}
	
	private void updateMinMaxValue() {
		int tempMinPosD = Integer.MAX_VALUE;
		int tempMaxNegD = Integer.MIN_VALUE;
		if (childNodes[0] != null) {
			if (childNodes[0] instanceof LNode) {
				LNode childLNode = (LNode) childNodes[0];
				if (childLNode.getD() > 0 && childLNode.getD() < tempMinPosD) {
					tempMinPosD = childLNode.getD();
				} else if (childLNode.getD() < 0 && childLNode.getD() > tempMaxNegD) {
					tempMaxNegD = childLNode.getD();
				}
			} else {
				INode childINode = (INode) childNodes[0];
				if (childINode.getMinPosD() < tempMinPosD) {
					tempMinPosD = childINode.getMinPosD();
				} else if (childINode.getMaxNegD() > tempMaxNegD) {
					tempMaxNegD = childINode.getMaxNegD();
				}
			}
		}
		if (childNodes[1] != null) {
			if (childNodes[1] instanceof LNode) {
				LNode childLNode = (LNode) childNodes[1];
				if (childLNode.getD() > 0 && childLNode.getD() < tempMinPosD) {
					tempMinPosD = childLNode.getD();
				} else if (childLNode.getD() < 0 && childLNode.getD() > tempMaxNegD) {
					tempMaxNegD = childLNode.getD();
				}
			} else {
				INode childINode = (INode) childNodes[1];
				if (childINode.getMinPosD() < tempMinPosD) {
					tempMinPosD = childINode.getMinPosD();
				} else if (childINode.getMaxNegD() > tempMaxNegD) {
					tempMaxNegD = childINode.getMaxNegD();
				}
			}
		}
		this.minPosD = tempMinPosD;
		this.maxNegD = tempMaxNegD;
	}
	
	private void updateContentOnlyConsideringL(LNode lNode) {
		if (lNode.getD() > 0) {
			this.minPosD = lNode.getD();
			this.maxNegD = Integer.MIN_VALUE;
		} else {
			this.minPosD = Integer.MAX_VALUE;
			this.maxNegD = lNode.getD();
		}
	}
	
	private void updateContentOnlyConsideringI(INode iNode) {
		this.minPosD = iNode.minPosD;
		this.maxNegD = iNode.maxNegD;
	}
	
	public void updateMinMaxContent(TDSTNode node) {
		if (node instanceof LNode) {
			LNode lNode = (LNode) node;
			updateContentOnlyConsideringL(lNode);
		} else {
			INode iNode = (INode) node;
			updateContentOnlyConsideringI(iNode);
		}
		if (this.fatherNode != null) {
			this.fatherNode.updateMinMaxContent(this);
		}
	}
	
	// return updated inode
	public INode deleteLNode(LNode lNode) {
		if (this.lNodeIds.contains(lNode.getID())) {
			// need to delete from here
			if (childNodes[0] != null) {
				if (childNodes[0] instanceof LNode) {
					LNode childLNode = (LNode) childNodes[0];
					if (childLNode.getID() == lNode.getID()) {
						// delete this lnode
						childNodes[0] = null;
						updateMinMaxValue();
					}
				} else {
					INode childINode = (INode) childNodes[0];
					if (childINode.includingLNode(lNode.getID())) {
						INode newChildINode = childINode.deleteLNode(lNode);
						childNodes[0] = newChildINode;
						updateMinMaxValue();
					}
				}
			}
			
			if (childNodes[1] != null) {
				if (childNodes[1] instanceof LNode) {
					LNode childLNode = (LNode) childNodes[1];
					if (childLNode.getID() == lNode.getID()) {
						// delete this lnode
						childNodes[1] = null;
						updateMinMaxValue();
					}
				} else {
					INode childINode = (INode) childNodes[1];
					if (childINode.includingLNode(lNode.getID())) {
						INode newChildINode = childINode.deleteLNode(lNode);
						childNodes[1] = newChildINode;
						updateMinMaxValue();
					}
				}
			}
			
			lNodeIds.remove(lNode.getID());
			if (this.childNodes[0] == null && this.childNodes[1] == null) return null;
			else return this;
		} else return this;
	}
	
	private void validateLNode(LNode lNode) {
		if (lNode.getD() > 0 && minPosD < lNode.getD()) {
			minPosD = lNode.getD();
		}
		if (lNode.getD() < 0 && maxNegD > lNode.getD()) {
			maxNegD = lNode.getD();
		}
	}
	
	public int getMinPosD() {
		return this.minPosD;
	}
	
	public int getMaxNegD() {
		return this.maxNegD;
	}
	
	private void validateINode(INode iNode) {
		if (iNode.getMinPosD() < this.minPosD) {
			this.minPosD = iNode.getMinPosD();
		}
		
		if (iNode.getMaxNegD() < this.maxNegD) {
			this.maxNegD = iNode.getMaxNegD();
		}
	}
	
	public void setFatherNode(INode iNode) {
		this.fatherNode = iNode;
	}
	
	public void reUpdateMinMaxValueToTheRoot() {
		updateMinMaxValue();
		if (this.fatherNode != null) {
			this.fatherNode.reUpdateMinMaxValueToTheRoot();
		}
	}
	
	/*private void justReplace(TDSTNode oldNode, TDSTNode newNode) {
		if (this.childNodes[0].equals(oldNode)) {
			this.childNodes[0] = newNode;
		}
	}
	
	public INode replace(TDSTNode oldNode, TDSTNode newNode) {
		if (oldNode instanceof LNode && newNode instanceof LNode) {
			LNode odlLNode = (LNode) oldNode;
			LNode newLNode = (LNode) newNode;
			if (this.childNodes[0].equals(oldNode) )
		}
	}*/
	
	public INode insertNode(TDSTNode node) {
		if (node instanceof LNode) {
			LNode lNode = (LNode) node;
			if (childNodes[0] == null) {
				childNodes[0] = lNode;
				lNode.setFatherNode(this);
				validateLNode(lNode);
				return null;
			} else if (childNodes[1] == null) {
				childNodes[1] = lNode;
				lNode.setFatherNode(this);
				validateLNode(lNode);
				return null;
			} else {
				INode iNode = new INode();
				iNode.insertNode(node);
				if (this.fatherNode != null) {
					return this.fatherNode.insertNode(iNode);
				} else {
					INode rootNode = new INode();
					rootNode.insertNode(this);
					rootNode.insertNode(iNode);
					return rootNode;
				}
			}
		} else {
			// INode
			INode iNode = (INode) node;
			if (childNodes[0] == null) {
				childNodes[0] = iNode;
				iNode.setFatherNode(this);
				validateINode(iNode);
				return null;
			} else if (childNodes[1] == null) {
				childNodes[1] = iNode;
				iNode.setFatherNode(this);
				validateINode(iNode);
				return null;
			} else {
				INode newINode = new INode();
				newINode.insertNode(node);
				if (this.fatherNode != null) {
					return this.fatherNode.insertNode(newINode);
				} else {
					INode rootNode = new INode();
					rootNode.insertNode(this);
					rootNode.insertNode(newINode);
					return rootNode;
				}
			}
		}
	}
	
	public boolean hasInfectedLNode(DataChange dc) {
		if (dc.getDelta() > 0) {
			if (this.minPosD - dc.getDelta() <= 0) return true;
		}
		
		if (dc.getDelta() < 0) {
			if (this.maxNegD - dc.getDelta() >= 0) return true;
		}
		
		return false;
	}
	
	public LNode getFirstInfectedLNode(DataChange dc) {
		LNode returnLNode = null;
		if (this.hasInfectedLNode(dc)) {
			if (this.childNodes[0] != null) {
				if (this.childNodes[0] instanceof LNode) {
					LNode lNode = (LNode) this.childNodes[0];
					if (lNode.isInfected(dc)) {
						returnLNode = lNode;
					}
				} else {
					INode iNode = (INode) this.childNodes[0];
					LNode lNode = iNode.getFirstInfectedLNode(dc);
					if (lNode != null) {
						returnLNode = lNode;
					}
				}
			} else if (this.childNodes[1] != null) {
				if (this.childNodes[1] instanceof LNode) {
					LNode lNode = (LNode) this.childNodes[1];
					if (lNode.isInfected(dc)) {
						returnLNode = lNode;
					}
				} else {
					INode iNode = (INode) this.childNodes[1];
					LNode lNode = iNode.getFirstInfectedLNode(dc);
					if (lNode != null) {
						returnLNode = lNode;
					}
				}
			}
		}
		return returnLNode;
	}
}
