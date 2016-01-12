package edu.haw.is.einstein.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Node<D> implements Comparable<Node<D>> {

	private final String name;
	private final Set<DirectionalEdge<D>> edgesPointingToNode;
	private final Set<DirectionalEdge<D>> edgesGoingOutFromNode;
	private List<D> domain;
	private D solution;

	public Node(final String name, final List<D> domain) {
		super();
		this.name = name;
		this.domain = domain;
		this.solution = null;
		this.edgesPointingToNode = new HashSet<>();
		this.edgesGoingOutFromNode = new HashSet<>();
	}
	
	public Node(final Node<D> node) {
		this.name = node.name;
		this.domain = node.domain;
		this.solution = node.solution;
		this.edgesPointingToNode = node.edgesPointingToNode;
		this.edgesGoingOutFromNode = node.edgesGoingOutFromNode;
	}

	public String getName() {
		return this.name;
	}

	public Set<DirectionalEdge<D>> getEdgesPointingToNode() {
		return this.edgesPointingToNode;
	}

	public Set<DirectionalEdge<D>> getEdgesGoingOutFromNode() {
		return this.edgesGoingOutFromNode;
	}

	public Set<DirectionalEdge<D>> getEdges() {
		final Set<DirectionalEdge<D>> allEdges = new HashSet<>(this.edgesPointingToNode);
		allEdges.addAll(this.edgesGoingOutFromNode);
		return allEdges;
	}

	public Set<Node<D>> getConnectedNodes() {
		return this.getEdges().stream().map(edge -> {
			if (edge.getFromNode().equals(this)) {
				return edge.getToNode();
			} else {
				return edge.getFromNode();
			}
		}).collect(Collectors.toCollection(HashSet::new));
	}

	public boolean addEdgePointingToNode(final DirectionalEdge<D> edge) {
		return this.edgesPointingToNode.add(edge);
	}

	public boolean addEdgeGoingOutFromNode(final DirectionalEdge<D> edge) {
		return this.edgesGoingOutFromNode.add(edge);
	}

	public List<D> getDomain() {
		return this.domain;
	}
	
	public void setSolution(final D solution) {
		this.solution = solution;
	}
	
	public D getSolution() {
		return this.solution;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
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
		final Node<D> other = (Node<D>) obj;
		if (this.name == null) {
			if (other.name != null)
				return false;
		} else if (!this.name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public int compareTo(final Node<D> o) {
		final int node1Size = this.getDomain().size();
		final int node2Size = o.getDomain().size();
		if (node1Size == node2Size) {
			return 0;
		}
		if (node1Size < node2Size) {
			return -1;
		}
		return 1;
	}

	public void setDomain(final List<D> domain) {
		this.domain = domain;
	}

	@Override
	public String toString() {
		return "Node [name=" + this.name + ", domain=" + this.domain + ", solution=" + this.solution + "]";
	}

}
