package edu.haw.is.einstein.graph;


public class DirectionalEdge<D extends Comparable<D>> {

	private final Node<D> from;
	private final Node<D> to;
	private final String constraintName;

	public DirectionalEdge(final Node<D> from, final Node<D> to, final String constraintName) {
		super();
		this.from = from;
		this.to = to;
		this.constraintName = constraintName;
	}

	public Node<D> getFromNode() {
		return this.from;
	}

	public Node<D> getToNode() {
		return this.to;
	}

	public String getConstraintName() {
		return this.constraintName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.from == null) ? 0 : this.from.hashCode());
		result = prime * result + ((this.to == null) ? 0 : this.to.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		final DirectionalEdge<D> other = (DirectionalEdge<D>) obj;
		if (this.from == null) {
			if (other.from != null)
				return false;
		} else if (!this.from.equals(other.from))
			return false;
		if (this.to == null) {
			if (other.to != null)
				return false;
		} else if (!this.to.equals(other.to))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DirectionalEdge [from=" + this.from + ", to=" + this.to + ", constraintName=" + this.constraintName + "]";
	}

//	@Override
//	public int compareTo(final Object o) {
//		if (!(o instanceof DirectionalEdge)) {
//			throw new UnsupportedOperationException();
//		}
//		final DirectionalEdge other = (DirectionalEdge) o;
//		final int node1Size = this.getFromNode().getDomain().size();
//		final int node2Size = other.getFromNode().getDomain().size();
//		if (node1Size == node2Size) {
//			return 0;
//		}
//		// invert order since we want node with smallest domain to be checked first
//		if (node1Size > node2Size) {
//			return -1;
//		}
//		return 1;
//	}

}
