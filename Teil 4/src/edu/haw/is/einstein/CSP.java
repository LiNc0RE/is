package edu.haw.is.einstein;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;


import edu.haw.is.einstein.graph.DirectionalEdge;
import edu.haw.is.einstein.graph.Graph;
import edu.haw.is.einstein.graph.Node;

public class CSP<D extends Comparable<D>> {
	
	private Graph<D> graph;
	private final BinaryConstraintFactory<D> constraintFactory;
	// FIXME: ugly hack, scoping and stuff...
	private Set<Node<D>> solution;
	
	public CSP(final BinaryConstraintFactory<D> constraintFactory) {
		super();
		this.constraintFactory = constraintFactory;
		this.graph = new Graph<>();
	}
	
	private boolean ac3La(final Graph<D> currentGraph, final int currentNodeIndex) {
		final Set<DirectionalEdge<D>> edges = currentGraph.getEdges();
		final Queue<DirectionalEdge<D>> queue = edges.stream().filter(edge -> {
			final int indexOfFrom = currentGraph.getUnassignedNodes().indexOf(edge.getFromNode());
			final int indexOfTo = currentGraph.getUnassignedNodes().indexOf(edge.getToNode());
			return (indexOfFrom > currentNodeIndex) && (indexOfTo == currentNodeIndex);
		}).collect(Collectors.toCollection(LinkedList::new));
//		DirectionalEdge<D>[] copyOfRange = (DirectionalEdge<D>[]) Arrays.copyOfRange(edges.toArray(), currentNodeIndex, edges.size() - 1);
//		System.out.println(queue);
//		System.out.println(currentGraph.getUnassignedNodes());
		while (!queue.isEmpty()) {
			final DirectionalEdge<D> currentArc = queue.poll();
//			System.out.println("before");
//			System.out.println(currentArc);
			if (this.removeInconsistentValues(currentArc)) {
//				System.out.println("after");
				System.out.println(currentArc);
//				System.out.println(currentArc.getFromNode().getEdgesGoingOutFromNode());
//				System.out.println(currentArc.getFromNode().getEdgesPointingToNode());
//				System.out.println(this.graph.getUnassignedNodes());
				// check in backtracking search?
				if (currentArc.getFromNode().getDomain().size() == 0) {
//					System.out.println("domain size of " + currentArc.getFromNode().getName() + " hit zero.");
					return false;
				} else {
					this.addArcsPointingToX(queue, currentArc, currentNodeIndex);					
				}
			}
		}
		return true;
	}
	
	private boolean ac3(final Graph<D> currentGraph) {
		final Queue<DirectionalEdge<D>> queue = new LinkedList<>(currentGraph.getEdges());
		while (!queue.isEmpty()) {
			final DirectionalEdge<D> currentArc = queue.poll();
//			System.out.println("before");
//			System.out.println(currentArc);
			if (this.removeInconsistentValues(currentArc)) {
//				System.out.println("after");
//				System.out.println(currentArc);
//				System.out.println(currentArc.getFromNode().getEdgesGoingOutFromNode());
//				System.out.println(currentArc.getFromNode().getEdgesPointingToNode());
				// check in backtracking search?
				if (currentArc.getFromNode().getDomain().size() == 0) {
					System.out.println("domain size of " + currentArc.getFromNode().getName() + " hit zero.");
					return false;
				} else {
					final Set<DirectionalEdge<D>> edgesPointingToChangedNode = currentArc.getFromNode().getEdgesPointingToNode();
					final Set<DirectionalEdge<D>> edgesPointingToChangedNodeWithoutY = edgesPointingToChangedNode.stream()
							.filter(edge -> (!(edge.getFromNode().equals(currentArc.getToNode()) || edge.getToNode().equals(edge.getFromNode()))))
							.collect(Collectors.toCollection(HashSet::new));
					queue.addAll(edgesPointingToChangedNodeWithoutY);					
				}
			}
		}
		return true;
	}

	private void addArcsPointingToX(final Queue<DirectionalEdge<D>> queue, final DirectionalEdge<D> currentArc, final int currentNodeIndex) {
		final Set<DirectionalEdge<D>> edgesPointingToChangedNode = currentArc.getFromNode().getEdgesPointingToNode();
		final Set<DirectionalEdge<D>> edgesPointingToChangedNodeWithoutY = edgesPointingToChangedNode.stream()
				.filter(edge -> (!(edge.getFromNode().equals(currentArc.getToNode()) || edge.getToNode().equals(edge.getFromNode())) || this.graph.getUnassignedNodes().indexOf(edge.getFromNode()) < currentNodeIndex))
				.collect(Collectors.toCollection(HashSet::new));
		queue.addAll(edgesPointingToChangedNodeWithoutY);
	}

	private boolean removeInconsistentValues(final DirectionalEdge<D> currentArc) {
		boolean removedSomething = false;
		final List<D> domainOfX = new ArrayList<>(currentArc.getFromNode().getDomain());
		for (final D possibleX : domainOfX) {
			if (!this.checkIfThereExistsYThatFullfilsConstraint(currentArc, possibleX)) {
				currentArc.getFromNode().getDomain().remove(possibleX);
				removedSomething = true;
			}
		}
		return removedSomething;
	}

	private boolean checkIfThereExistsYThatFullfilsConstraint(final DirectionalEdge<D> currentArc, final D possibleX) {
		for (final D possibleY : currentArc.getToNode().getDomain()) {
			if (this.constraintFactory.checkConstraint(currentArc.getConstraintName(), possibleX, possibleY)) {
				return true;
			}
		}
		return false;
	}
	
	public List<Node<D>> solve() {
		System.out.println(this.graph.getEdges());
		this.enforceUnaryConstraint();
		System.out.println(this.graph.getUnassignedNodes());
//		System.out.println(this.graph.getEdges());
		this.ac3(this.graph);
		if (this.solveRecursively(this.graph, 0)) {
			return this.graph.getUnassignedNodes();
		} else {
			return null;
		}
	}
	
	private boolean solveRecursively(final int currentNodeIndex) {
		final Node<D> currentNode = this.graph.getUnassignedNodes().get(currentNodeIndex);
		if (currentNode.getDomain().size() <= 0){
			return false;
		}
		final Graph<D> copyState = this.graph.deepCopy();
		final D possibleSolution = currentNode.getDomain().get(0);
		currentNode.getDomain().remove(0);
		final List<D> domainBackup = new ArrayList<>(currentNode.getDomain());
		this.graph.assign(currentNode, possibleSolution);
		System.out.println("Starte AC3La mit Node: "+currentNode);
		
		if (this.ac3La(this.graph, currentNodeIndex)) {
			if (currentNodeIndex == this.graph.getUnassignedNodes().size() - 1) {
				System.err.println(this.graph.getUnassignedNodes());
				return true;
			}
			System.out.println("rec");
			if (this.solveRecursively(currentNodeIndex + 1)) {
				return true;
			}
		}
		System.out.println("ac3La Schlug fehl mit Node: "+currentNode);
		this.graph = copyState;
		this.graph.getUnassignedNodes().get(currentNodeIndex).setDomain(domainBackup);
		return this.solveRecursively(currentNodeIndex);
	}
	
	private boolean solveRecursively(final Graph<D> graph, final int currentNodeIndex) {
		final Node<D> currentNode = graph.getUnassignedNodes().get(currentNodeIndex);
		if (currentNode.getDomain().size() <= 0){
			return false;
		}
		for (final D possibleSolution : currentNode.getDomain()) {
			final Graph<D> newState = graph.deepCopy();
			final Node<D> currentNodeNewState = newState.getUnassignedNodes().get(currentNodeIndex);
			newState.assign(currentNodeNewState, possibleSolution);
			if (this.ac3La(newState, currentNodeIndex)) {
				if (currentNodeIndex == graph.getUnassignedNodes().size() - 1) {
					System.err.println(newState.getUnassignedNodes());
					this.graph = newState;
					return true;
				}
				System.out.println("rec");
				if (this.solveRecursively(newState, currentNodeIndex + 1)) {
					return true;
				}
			}
		}
		return false;
	}

	private void enforceUnaryConstraint() {
		final List<DirectionalEdge<D>> unaryConstraints = this.graph.getEdges().stream().filter(edge -> {
			return edge.getFromNode().equals(edge.getToNode());
		}).collect(Collectors.toCollection(ArrayList::new));
		System.err.println(unaryConstraints.size());
		for (final DirectionalEdge<D> edge : unaryConstraints) {
			final List<D> domain = edge.getFromNode().getDomain();
			final List<D> newDomain = new ArrayList<>(domain.size());
			for (final D possibleSolution : domain) {
				if (this.constraintFactory.checkConstraint(edge.getConstraintName(), possibleSolution, possibleSolution)) {
					newDomain.add(possibleSolution);
				}
			}
			edge.getFromNode().setDomain(newDomain);
			
			if (newDomain.size() == 1) {
				// wtf?
				System.err.println(this.graph.getEdges());
				System.err.println("found solution through unary constraint");
				this.graph.assign(edge.getFromNode(), newDomain.get(0));
			}
		}
	}

//	private Set<Node<D>> backtrackingSearch(final Graph<D> graph) {
//		this.recursiveBacktrackingSearch(graph);
//		return this.solution;
//	}
//
//	private void recursiveBacktrackingSearch(final Graph<D> graph) {
//		if (graph.getUnassignedNodes().size() <= 0) {
//			// SUCCESS
//			this.solution = graph.getAssignedNodes();
//			return;
//		}
//		final Node<D> currentNode = graph.getUnassignedNodes().poll();
//		if (!(currentNode.getDomain().size() <= 0)) {
//			for (final D possibleSolution : currentNode.getDomain()) {
//				final Graph<D> currentGraph = graph.deepCopy();
//				currentGraph.assign(currentNode, possibleSolution);
//				if (this.ac3(currentGraph)) {
//					this.recursiveBacktrackingSearch(currentGraph);
//				}
//			}
//		}
//	}
	
//	private Set<Node<D>> backtrackingSearch(final Graph<D> graph) {
//		return this.recursiveBacktrackingSearch(graph);
//	}
//
//	private Set<Node<D>> recursiveBacktrackingSearch(final Graph<D> graph) {
//		if (graph.getUnassignedNodes().size() <= 0) {
//			// SUCCESS
//			return graph.getAssignedNodes();
//		}
//		final Node<D> currentNode = graph.getUnassignedNodes().poll();
//		if (currentNode.getDomain().size() <= 0) {
//			// FAIL
//			System.out.println("domain size hit zero for: " + currentNode.getName());
//			return null;
//		}
//		for (final D possibleSolution : currentNode.getDomain()) {
//			final Graph<D> currentGraph = graph.deepCopy();
//			currentGraph.assign(currentNode, possibleSolution);
//			if (this.ac3(currentGraph)) {
//				this.recursiveBacktrackingSearch(currentGraph);
//			}
//		}
//		return null;
//	}

	public void addVariable(final String name, final List<D> domain) {
		this.graph.addNode(name, domain);
	}

	public void addBidirectionalConstraint(final String node1, final String node2, final String constraintName) {
		this.graph.addBidirectionalEdgeBetween(node1, node2, constraintName);
	}

	public void addConstraint(final String node1, final String node2, final String constraintName) {
		this.graph.addEdgeBetween(node1, node2, constraintName);
	}

}
