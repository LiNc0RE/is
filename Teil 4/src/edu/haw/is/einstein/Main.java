package edu.haw.is.einstein;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import edu.haw.is.einstein.graph.Node;

public class Main {
	
	// constraints
	private static final String RECHTSVON = "rechtsvon";
	private static final String LINKSVON = "linksvon";
	private static final String NACHBARN = "nachbarn";
	private static final String UNGLEICH = "ungleich";
	private static final String GLEICH = "gleich";
	private static final String ERSTESHAUS = "ersteshaus";
	private static final String MITTELHAUS = "mittelhaus";
	// Variablen
	private static final String ROTHMANNS = "rothmanns";
	private static final String WINFIELD = "winfield";
	private static final String MARLBORO = "marlboro";
	private static final String DUNHILL = "dunhill";
	private static final String PALLMALL = "pallmall";
	private static final String WASSER = "wasser";
	private static final String BIER = "bier";
	private static final String MILCH = "milch";
	private static final String KAFFEE = "kaffee";
	private static final String TEE = "tee";
	private static final String FISCH = "fisch";
	private static final String PFERD = "pferd";
	private static final String KATZE = "katze";
	private static final String VOGEL = "vogel";
	private static final String HUND = "hund";
	private static final String BLAU = "blau";
	private static final String WEISS = "weiss";
	private static final String GELB = "gelb";
	private static final String GRUEN = "gruen";
	private static final String ROT = "rot";
	private static final String DEUTSCHER = "deutscher";
	private static final String NORWEGER = "norweger";
	private static final String DAENE = "daene";
	private static final String SCHWEDE = "schwede";
	private static final String BRITE = "brite";

	public static void main(final String[] args) {
		final BinaryConstraintFactory<Integer> constraintFactory = BinaryConstraintFactory.init();
		
		// unary
		constraintFactory.addCommand(MITTELHAUS, (x, y) -> x == 2);
		constraintFactory.addCommand(ERSTESHAUS, (x, y) -> x == 0);
		
		// binary
		constraintFactory.addCommand(GLEICH, (x, y) -> x == y);
		constraintFactory.addCommand(UNGLEICH, (x, y) -> x != y);
		constraintFactory.addCommand(NACHBARN, (x, y) -> Math.abs(x - y) == 1);
		constraintFactory.addCommand(LINKSVON, (x, y) -> x < y);
		constraintFactory.addCommand(RECHTSVON, (x, y) -> x > y);
		
		final CSP<Integer> csp = new CSP<>(constraintFactory);

		csp.addVariable(BRITE, createDomain());
		csp.addVariable(SCHWEDE, createDomain());
		csp.addVariable(DAENE, createDomain());
		csp.addVariable(NORWEGER, createDomain());
		csp.addVariable(DEUTSCHER, createDomain());
		
		csp.addVariable(ROT, createDomain());
		csp.addVariable(GRUEN, createDomain());
		csp.addVariable(GELB, createDomain());
		csp.addVariable(WEISS, createDomain());
		csp.addVariable(BLAU, createDomain());
		
		csp.addVariable(HUND, createDomain());
		csp.addVariable(VOGEL, createDomain());
		csp.addVariable(KATZE, createDomain());
		csp.addVariable(PFERD, createDomain());
		csp.addVariable(FISCH, createDomain());
		
		csp.addVariable(TEE, createDomain());
		csp.addVariable(KAFFEE, createDomain());
		csp.addVariable(MILCH, createDomain());
		csp.addVariable(BIER, createDomain());
		csp.addVariable(WASSER, createDomain());
		
		csp.addVariable(PALLMALL, createDomain());
		csp.addVariable(DUNHILL, createDomain());
		csp.addVariable(MARLBORO, createDomain());
		csp.addVariable(WINFIELD, createDomain());
		csp.addVariable(ROTHMANNS, createDomain());
		
		// unary
		csp.addBidirectionalConstraint(MILCH, MILCH, MITTELHAUS);        // 7
		csp.addBidirectionalConstraint(NORWEGER, NORWEGER, ERSTESHAUS);  // 9
		
		// binary
		csp.addBidirectionalConstraint(BRITE, ROT, GLEICH);              // 1
		csp.addBidirectionalConstraint(SCHWEDE, HUND, GLEICH);           // 2
		csp.addBidirectionalConstraint(DAENE, TEE, GLEICH);              // 3
		csp.addConstraint(GRUEN, WEISS, LINKSVON);                       // 4.1
		csp.addConstraint(WEISS, GRUEN, RECHTSVON);                      // 4.2
		csp.addBidirectionalConstraint(GRUEN, KAFFEE, GLEICH);           // 5
		csp.addBidirectionalConstraint(PALLMALL, VOGEL, GLEICH);         // 6
		csp.addBidirectionalConstraint(GELB, DUNHILL, GLEICH);          // 8
		csp.addBidirectionalConstraint(WINFIELD, BIER, GLEICH);          // 12
		csp.addBidirectionalConstraint(DEUTSCHER, ROTHMANNS, GLEICH);    // 14
		csp.addBidirectionalConstraint(MARLBORO, KATZE, NACHBARN);       // 10
		csp.addBidirectionalConstraint(PFERD, DUNHILL, NACHBARN);         // 11
		csp.addBidirectionalConstraint(NORWEGER, BLAU, NACHBARN);        // 13
		csp.addBidirectionalConstraint(MARLBORO, WASSER, NACHBARN);      // 15
		
		final List<String> nationen = Arrays.asList(BRITE, SCHWEDE, DAENE, NORWEGER, DEUTSCHER);
		final List<String> farben = Arrays.asList(ROT, GRUEN, GELB, WEISS, BLAU);
		final List<String> tiere = Arrays.asList(HUND, VOGEL, KATZE, PFERD, FISCH);
		final List<String> getraenke = Arrays.asList(TEE, KAFFEE, MILCH, BIER, WASSER);
		final List<String> zigaretten = Arrays.asList(PALLMALL, DUNHILL, MARLBORO, WINFIELD, ROTHMANNS);
		
		addConstraintFor(csp, nationen, UNGLEICH);
		addConstraintFor(csp, farben, UNGLEICH);
		addConstraintFor(csp, tiere, UNGLEICH);
		addConstraintFor(csp, getraenke, UNGLEICH);
		addConstraintFor(csp, zigaretten, UNGLEICH);
		
		final List<Node<Integer>> solution = csp.solve();
		prettyPrint(solution);
	}
	
	public static void prettyPrint(final List<Node<Integer>> solution) {
	final ArrayList<Node<Integer>> sortedSolution = solution.stream().sorted(new Comparator<Node<Integer>>() {

		@Override
		public int compare(final Node<Integer> node1, final Node<Integer> node2) {
			return node1.getSolution().compareTo(node2.getSolution());
		}
	}).collect(Collectors.toCollection(ArrayList::new));
	int currentHouse = -1;
	for (final Node<Integer> node : sortedSolution) {
		if (currentHouse < node.getSolution()) {
			System.out.println("Person, die in Haus " + (currentHouse + 1) + " wohnt:");
			currentHouse++;
		}
		System.out.println("    " + node.getName());
	}
}

	private static List<Integer> createDomain() {
		return new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4));
	}

	private static void addConstraintFor(final CSP<Integer> csp, final List<String> nodes, final String constraintName) {
		for (final String node1 : nodes) {
			for (final String node2 : nodes) {
				if (!node1.equals(node2)) {
					csp.addBidirectionalConstraint(node1, node2, constraintName);
				}
			}
		}
	}

}
