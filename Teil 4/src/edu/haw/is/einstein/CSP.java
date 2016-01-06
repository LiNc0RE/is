package edu.haw.is.einstein;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import edu.haw.is.einstein.graph.DirectionalEdge;
import edu.haw.is.einstein.graph.Graph;
import edu.haw.is.einstein.graph.Node;

public class CSP<D> {
	
	private final Graph<D> graph;
	private final BinaryConstraintFactory<D> constraintFactory;
	// FIXME: ugly hack, scoping and stuff...
	private boolean removedSomething;
	private boolean validYExists;
	private Set<Node<D>> solution;
	
	public CSP(final BinaryConstraintFactory<D> constraintFactory) {
		super();
		this.constraintFactory = constraintFactory;
		this.graph = new Graph<>();
	}
	
	private boolean ac3(final Graph<D> currentGraph) {
		final Queue<DirectionalEdge<D>> queue = new LinkedList<>(currentGraph.getEdges());
		while (!queue.isEmpty()) {
			final DirectionalEdge<D> currentArc = queue.poll();
			System.out.println("before");
			System.out.println(currentArc);
			if (this.removeInconsistentValues(currentArc)) {
				System.out.println("after");
				System.out.println(currentArc);
				System.out.println(currentArc.getFromNode().getEdgesGoingOutFromNode());
				System.out.println(currentArc.getFromNode().getEdgesPointingToNode());
				// check in backtracking search?
				if (currentArc.getFromNode().getDomain().size() == 0) {
					System.out.println("domain size of " + currentArc.getFromNode().getName() + " hit zero.");
					return false;
				} else {
					this.addArcsPointingToX(queue, currentArc);					
				}
			}
		}
		return true;
	}

	private void addArcsPointingToX(final Queue<DirectionalEdge<D>> queue, final DirectionalEdge<D> currentArc) {
		final Set<DirectionalEdge<D>> edgesPointingToChangedNode = currentArc.getFromNode().getEdgesPointingToNode();
		final Set<DirectionalEdge<D>> edgesPointingToChangedNodeWithoutY = edgesPointingToChangedNode.stream().filter(edge -> !edge.getFromNode().equals(currentArc.getToNode())).collect(Collectors.toCollection(HashSet::new));
		queue.addAll(edgesPointingToChangedNodeWithoutY);
	}

	private boolean removeInconsistentValues(final DirectionalEdge<D> currentArc) {
		this.removedSomething = false;
		final List<D> domainOfX = new ArrayList<>(currentArc.getFromNode().getDomain());
		domainOfX.forEach(possibleX -> {
			this.validYExists = false;
			this.checkIfThereExistsYThatFullfilsConstraint(currentArc, possibleX);
			if (!this.validYExists) {
				currentArc.getFromNode().getDomain().remove(possibleX);
				this.removedSomething = true;
			}
		});
		return this.removedSomething;
	}

	private void checkIfThereExistsYThatFullfilsConstraint(final DirectionalEdge<D> currentArc, final D possibleX) {
		currentArc.getToNode().getDomain().forEach(possibleY -> {
			if (this.constraintFactory.checkConstraint(currentArc.getConstraintName(), possibleX, possibleY)) {
				this.validYExists = true;
			}
		});
	}
	
	public Set<Node<D>> solve() {
		System.out.println(this.graph.getEdges());
		this.enforceUnaryConstraint();
		System.out.println(this.graph.getEdges());
		if (this.ac3(this.graph)) {
			System.err.println("after first ac3");
			return this.backtrackingSearch(this.graph);
		} else {
			System.out.println("initial ac3 failed");
			return null;
		}
		
	}
	
	private void enforceUnaryConstraint() {
		final List<DirectionalEdge<D>> unaryConstraints = this.graph.getEdges().stream().filter(edge -> {
			return edge.getFromNode().equals(edge.getToNode());
		}).collect(Collectors.toCollection(ArrayList::new));
		System.err.println(unaryConstraints.size());
		unaryConstraints.forEach(edge -> {
			edge
				.getFromNode()
				.getDomain()
				.removeIf(possibleSolution -> 
					!this.constraintFactory.checkConstraint(edge.getConstraintName(), possibleSolution, possibleSolution));
			if (edge.getFromNode().getDomain().size() == 1) {
				// wtf?
				System.err.println(this.graph.getEdges());
				System.err.println("found solution through unary constraint");
				this.graph.assign(edge.getFromNode(), edge.getFromNode().getDomain().get(0));
			}
		});
	}

	private Set<Node<D>> backtrackingSearch(final Graph<D> graph) {
		this.recursiveBacktrackingSearch(graph);
		return this.solution;
	}

	private void recursiveBacktrackingSearch(final Graph<D> graph) {
		if (graph.getUnassignedNodes().size() <= 0) {
			// SUCCESS
			this.solution = graph.getAssignedNodes();
		}
		final Node<D> currentNode = graph.getUnassignedNodes().poll();
		if (!(currentNode.getDomain().size() <= 0)) {
			for (final D possibleSolution : currentNode.getDomain()) {
				final Graph<D> currentGraph = graph.deepCopy();
				currentGraph.assign(currentNode, possibleSolution);
				if (this.ac3(currentGraph)) {
					this.recursiveBacktrackingSearch(currentGraph);
				}
			}
		}
	}
	
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
