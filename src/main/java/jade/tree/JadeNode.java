package jade.tree;

import java.util.*;

import opentree.GeneralUtils;

public class JadeNode {
    
    public static final double MIN_BRANCHLENGTH = 0.0000000000000000000001;

    /*
	 * common associations
	 */
	private double BL; //branch lengths
	private double distance_to_tip;
	private double distance_from_tip; // distance from the root to a tip (not set automatically)
	//private int number;
	private String name;
	private JadeNode parent;
	private ArrayList<JadeNode> children;
	private ArrayList<NodeObject> assoc; // @note might need to make this a HashMap<String, Object> or TreeMap<String,Object>
	
	/*
	 * constructors
	 */
	public JadeNode() {
		this.BL = 0.0;
		this.distance_to_tip = 0.0;
		this.distance_from_tip = 0.0;
		//this.number = 0;
		this.name = "";
		this.parent = null;
		this.children = new ArrayList<JadeNode> ();
		this.assoc = new ArrayList<NodeObject>();
	}
	
	public JadeNode(JadeNode parent) {
		this.BL = 0.0;
		this.distance_to_tip = 0.0;
		this.distance_from_tip = 0.0;
		//this.number = 0;
		this.name = "";
		this.parent = parent;
		this.children = new ArrayList<JadeNode> ();
		this.assoc = new ArrayList<NodeObject>();
	}
	
	public JadeNode(double BL, String name, JadeNode parent) {
		this.BL = BL;
		this.distance_to_tip = 0.0;
		this.distance_from_tip = 0.0;
		//this.number = number;
		this.name = name;
		this.parent = parent;
		this.children = new ArrayList<JadeNode> ();
		this.assoc = new ArrayList<NodeObject>();
	}


    /* ---------------------------- begin node iterators --------------------------------*/

    public enum NodeOrder {PREORDER, POSTORDER};

    private void addDescendants(JadeNode n, List<JadeNode> children, NodeOrder order) {

        if (order == NodeOrder.PREORDER) {
            for (JadeNode c : n.children){
                addDescendants(c, children, order);
            }
            children.add(n);

        } else if (order == NodeOrder.POSTORDER) {
            children.add(n);

            for (JadeNode c : n.children){
                addDescendants(c, children, order);
            }
        }
    }
    
    public Iterable<JadeNode> getDescendants(NodeOrder order) {
        
        LinkedList<JadeNode> nodes = new LinkedList<JadeNode>();
        addDescendants(this, nodes, order);
        return nodes;
    }
    
    public Iterable<JadeNode> getDescendantLeaves(NodeOrder order) {
        LinkedList<JadeNode> leaves = new LinkedList<JadeNode>();
    	for (JadeNode descendant : getDescendants(order)) {
    		if (descendant.isExternal()) {
    			leaves.add(descendant);
    		}
    	}
    	return leaves;
    }
    
    public Iterable<JadeNode> getDescendantLeaves() {
    	return getDescendantLeaves(NodeOrder.PREORDER);
    }
    
    public Iterable<JadeNode> getDescendants() {
    	return getDescendants(NodeOrder.PREORDER);
    }
    
    /* ---------------------------- end node iterators --------------------------------*/
    
    /*
     * other public methods
     */
    
	public JadeNode [] getChildrenArr() {return (JadeNode[])this.children.toArray();}
	
	public ArrayList<JadeNode> getChildren() {return this.children;}
	
	public boolean isExternal() {return (this.children.size() < 1);}
	
	public boolean isInternal() {return (this.children.size() > 0);}
	
	public boolean isTheRoot() {return (this.parent == null);}
	
	public boolean hasParent() {return (this.parent != null);}
	
	public void setParent(JadeNode p) {this.parent = p;}
	
	//public int getNumber() {return this.number;}
	
	//public void setNumber(int n) {this.number = n;}
	
	public double getBL() {return this.BL;}
	
	public void setBL(double b) {this.BL = b;}
	
	public boolean hasChild(JadeNode test) {return this.children.contains(test);}
	
	public boolean addChild(JadeNode c) {
		if (this.hasChild(c) == false) {
			this.children.add(c);
			c.setParent(this);
			return true;
		} else {
			return false;
		}
	}
	
	public boolean removeChild(JadeNode c) {
		if (this.hasChild(c)) {
			this.children.remove(c);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @return the c-th child or throw IndexOutOfBoundsException.
	 */
	public JadeNode getChild(int c) throws IndexOutOfBoundsException {
		return this.children.get(c);
	}
	
	public void setName(String s) {this.name = s;}
	
	public String getName() {return this.name;}
	
	/**
	 * @param bl should be true to include branch lengths
	 * @return string with newick representation of the subtree rooted at this node
	 */
	public String getNewick(boolean bl) {
		StringBuffer ret = new StringBuffer("");
		for (int i = 0; i < this.getChildCount(); i++) {
			if (i == 0) {
				ret.append("(");
			}
			ret.append(this.getChild(i).getNewick(bl));
			if (bl) {
			    double branchLength = this.getChild(i).getBL();
			    if (branchLength == 0)
			        branchLength = MIN_BRANCHLENGTH;

			    ret.append(":".concat(String.valueOf(branchLength)));
			}
			if (i == this.getChildCount()-1) {
				ret.append(")");
			} else {
				ret.append(",");
			}
		}
		if (this.name != null) {
			ret.append(GeneralUtils.cleanName(this.name));
		}
		return ret.toString();
	}
	
	/**
	 * Get JSON output for argus viewer. DEPRECATED. Use the ArgusRepresentationConverter instead.
	 * 
	 * Addendum: I have commented out this method and replaced calls to it with methods from ArgusRepresentationConverter.class.
	 * If this is to remain unused then it should be removed. - ceh 2013 07 17
	 * 
	 * @param bl should be true to include branch lengths
	 * @return string with JSON representation of the subtree rooted at this node
	 */
	@Deprecated
	public String getJSON(boolean bl) { 
		
		throw new java.lang.UnsupportedOperationException("The getJSON method is deprecated. Use ArgusRepresentationConverter instead.");
		/*
		StringBuffer ret = new StringBuffer("{");
		if (this.name != null) {
			ret.append(" \"name\": \"" + this.getName() + "\"");
		} else {
			ret.append(" \"name\": \"\"");
		}
		if (this.getObject("nodeid") != null) {
			ret.append("\n, \"nodeid\": \"" + this.getObject("nodeid") + "\"");
		}
		for (int i = 0; i < this.getChildCount(); i++) {
			if (i == 0) {
				ret.append("\n, \"children\": [\n");
			}
			ret.append(this.getChild(i).getJSON(bl));
			if (i == this.getChildCount() - 1) {
				ret.append("]\n");
			} else {
				ret.append(",\n");
			}
		}
		if (bl) {
			ret.append(", \"size\": " + this.getBL());
		}
		if ((this.getObject("jsonprint")) != null) {
			ret.append(this.getObject("jsonprint"));
		}
		if (this.getObject("nodedepth") != null) {
			ret.append(", \"maxnodedepth\": " + this.getObject("nodedepth"));
		}
		if (this.isInternal()) {
			ret.append(", \"nleaves\": " + this.getTips().size());
		} else {
			ret.append(", \"nleaves\": 0");
		}
		String [] optionalPropN = {"uniqName", "taxSource", "taxSourceId", "taxRank", "ottolId"};
		for (String opn : optionalPropN) {
			JSONExporter.writeStringPropertyIfNotNull(ret, this.getObject(opn), opn, true);
		}
		Object ptr = this.getObject("pathToRoot");
		if (ptr != null) {
			ret.append(", ");
			JSONExporter.escapePropertyColon(ret, "pathToRoot");
			JSONExporter.writeListOfNodesAsJSONSummary(ret, ptr);
		}
		
		Object hc = this.getObject("hasChildren");
		if (hc != null) {
			ret.append(", ");
			JSONExporter.escapePropertyColon(ret, "hasChildren");
			JSONExporter.writeBooleanAsJSON(ret, (Boolean) hc);
		}
		
		Object dnl = this.getObject("descendantNameList");
		if (dnl != null) {
			ret.append(", \"descendantNameList\": ");
			JSONExporter.writeStringArrayAsJSON(ret, (String []) dnl);
		}
		// report tree IDs supporting each clade as supportedBy list of strings
		Object sup = this.getObject("supporting_sources");
		if (sup != null) {
			ret.append(", \"supportedBy\": ");
			JSONExporter.writeStringArrayAsJSON(ret, (String []) sup);
		}
		// report metadata for the sources mentioned in supporting_sources. the sourceMetaList property
		//	should be set for the root
		Object n2m = this.getObject("sourceMetaList");
		if (n2m != null) {
			ret.append(", ");
			JSONExporter.writeSourceToMetaMapForArgus(ret, n2m);
		}
		ret.append("}");
		return ret.toString(); */
	}

	/**
	 * @return Returns all of the tips in the subtree rooted at `this`
	 */
	public ArrayList<JadeNode> getTips() {
		ArrayList<JadeNode> children = new ArrayList<JadeNode>();
		Stack<JadeNode> nodes = new Stack<JadeNode>();
		nodes.push(this);
		while (nodes.isEmpty() == false) {
			JadeNode jt = nodes.pop();
			for (int i = 0; i < jt.getChildCount(); i++) {
				nodes.push(jt.getChild(i));
			}
			if (jt.isExternal()) {
				children.add(jt);
			}
		}
		return children;
	}
	
	/**
	 * @return Returns all of the tips in the subtree rooted at `this`
	 */
	public int getTipCount() {
		int count = 0;
		Stack<JadeNode> nodes = new Stack<JadeNode>();
		nodes.push(this);
		while (nodes.isEmpty() == false) {
			JadeNode jt = nodes.pop();
			for (int i = 0; i < jt.getChildCount(); i++) {
				nodes.push(jt.getChild(i));
			}
			if (jt.isExternal()) {
				count += 1;
			}
		}
		return count;
	}
	
	/**
	 * @return Returns the maximum number of edges between `this` and a tip
	 *		that is a descendant of `this`
	 */
	public int getNodeMaxDepth() {
		int maxnodedepth = 0;
		ArrayList<JadeNode> tips = this.getTips();
		for (int i = 0; i < tips.size(); i++) {
			JadeNode curnode = tips.get(i);
			int tnodedepth = 0;
			while (curnode != this) {
				tnodedepth += 1;
				curnode = curnode.getParent();
			}
			if (tnodedepth > maxnodedepth) {
				maxnodedepth = tnodedepth;
			}
		}
		return maxnodedepth;
	}
	
	public JadeNode getParent() {return this.parent;}
	
	public int getChildCount() {return this.children.size();}
	
	public double getDistanceFromTip() {return this.distance_from_tip;}
	
	public void setDistanceFromTip(double inh) {this.distance_from_tip = inh;}
	
	public double getDistanceToTip() {return this.distance_to_tip;}
	
	public void setDistanceToTip(double inh) {this.distance_to_tip = inh;}
	
	/**
	 * Adds or a replace a mapping of key->obj for this node
	 * @note this in an example of an O(N) routine that would be constant time
	 * 		or O(log(N)) if we change the assoc datatype.
	 * @param key
	 * @param obj Object to be stored
	 */
	public void assocObject(String key, Object obj) {
		// @todo This is written as if there could be multiple elements in assoc that
		//		have the same key. I don't think this is true. (we should probably
		//		return on finding a match, rather than set a test=true flag).
		boolean test = false;
		for (int i = 0; i < this.assoc.size(); i++) {
			if (this.assoc.get(i).getName().compareTo(key) == 0) {
				test = true;
				this.assoc.get(i).setObject(obj);
			}
		}
		if (test == false) {
			NodeObject no = new NodeObject(key, obj);
			this.assoc.add(no);
		}
	}
	
	/**
	 * @return Object associated with this node and key through a previous call
	 *		to assocObject, or null
	 * @note this in an example of an O(N) routine that would be constant time
	 * 		or O(log(N)) if we change the assoc datatype.
	 * @param key
	 */
	public Object getObject(String key) {
		// @todo This is written as if there could be multiple elements in assoc that
		//		have the same key. I don't think this is true. (we should probably
		//		return on finding a match, rather than continuing to walk through the list).
		Object a = null;
		for (int i = 0; i < this.assoc.size(); i++) {
			if (this.assoc.get(i).getName().compareTo(key) == 0) {
				a = this.assoc.get(i).getObject();
			}
		}
		return a;
	}


}
