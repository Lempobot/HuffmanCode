package assign1;

public class NodeTree implements Comparable<NodeTree>{
	NodeTree right = null;
	NodeTree left = null;
	short character;// the real value.
	int freq;// frequency.
	public NodeTree(NodeTree left, NodeTree right, short id, int value) {
		this.character = id;
		this.freq = value;
		this.left = left;
		this.right = right;
	}
	
	@Override
	public int compareTo(NodeTree hpCompare) {
		
		return this.freq - hpCompare.freq;
	}

	@Override
	public String toString() {
		return "HeapComperator [id=" + character + ", value=" + freq + "]";
	}
	
}
