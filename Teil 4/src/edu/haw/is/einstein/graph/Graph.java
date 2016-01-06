package edu.haw.is.einstein.graph;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

public class Graph<D> {

	private final Queue<Node<D>> unassignedNodes;
	private final Set<Node<D>> assignedNodes;
	private final Set<DirectionalEdge<D>> edges;

	public Graph() {
		// most constrained variable ordering
		this.unassignedNodes = new PriorityQueue<>();
		this.assignedNodes = new HashSet<>();
		this.edges = new HashSet<>();
	}
	
	private Graph(final Queue<Node<D>> unassignedNodes, final Set<Node<D>> assignedNodes, final Set<DirectionalEdge<D>> edges) {
		this.unassignedNodes = unassignedNodes;
		this.assignedNodes = assignedNodes;
		this.edges = edges;
	}
	
	public Graph<D> deepCopy() {
		// edges never change, only nodes need deep copy
		final Queue<Node<D>> unassignedNodesCopy = this.unassignedNodes.stream().map(node -> new Node<D>(node)).collect(Collectors.toCollection(PriorityQueue::new));
		final Set<Node<D>> assignedNodesCopy = this.assignedNodes.stream().map(node -> new Node<D>(node)).collect(Collectors.toCollection(HashSet::new));
		return new Graph<D>(unassignedNodesCopy, assignedNodesCopy, this.edges);
	}
	
	public void addNode(final String name, final List<D> domain) {
		this.unassignedNodes.add(new Node<>(name, domain));
	}
	
	public boolean addEdgeBetween(final String from, final String to, final String constraintName) {
		final Optional<Node<D>> node1Optional = this.getUnassignedNode(from);
		final Optional<Node<D>> node2Optional = this.getUnassignedNode(to);
		if (node1Optional.isPresent() && node2Optional.isPresent()) {
			final Node<D> node1 = node1Optional.get();
			final Node<D> node2 = node2Optional.get();
			final DirectionalEdge<D> edge = new DirectionalEdge<D>(node1, node2, constraintName);
			node1.addEdgeGoingOutFromNode(edge);
			node2.addEdgePointingToNode(edge);
			return this.edges.add(edge);
		}
		return false;
	}
	
	public boolean addBidirectionalEdgeBetween(final String node1, final String node2, final String constraintName) {
		if (this.addEdgeBetween(node1, node2, constraintName)) {
			return this.addEdgeBetween(node2, node1, constraintName);			
		}
		return false;
	}
	
	public Optional<Node<D>> getUnassignedNode(final String name) {
		return this.unassignedNodes.stream().filter(node -> node.getName().equals(name)).findFirst();
	}
	
	public Queue<Node<D>> getUnassignedNodes() {
		return this.unassignedNodes;
	}

	public Set<Node<D>> getAssignedNodes() {
		return this.assignedNodes;
	}
	
	public Set<DirectionalEdge<D>> getEdges() {
		return this.edges;
	}

	public void assign(final Node<D> currentNode, final D possibleSolution) {
		System.err.println("assign");
		this.unassignedNodes.remove(currentNode);
		currentNode.setSolution(possibleSolution);
		this.assignedNodes.add(currentNode);
	}

}
