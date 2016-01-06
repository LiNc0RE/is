package edu.haw.is.einstein;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
	
	public static void main(final String[] args) {
		final List<Integer> domain = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4));
		final BinaryConstraintFactory<Integer> constraintFactory = BinaryConstraintFactory.init();
		
		// unary
		constraintFactory.addCommand("mittelhaus", (x, y) -> x == 2);
		constraintFactory.addCommand("ersteshaus", (x, y) -> x == 0);
		
		// binary
		constraintFactory.addCommand("gleich", (x, y) -> x == y);
		constraintFactory.addCommand("ungleich", (x, y) -> x != y);
		constraintFactory.addCommand("nachbarn", (x, y) -> Math.abs(x - y) == 1);
		constraintFactory.addCommand("linksvon", (x, y) -> x < y);
		constraintFactory.addCommand("rechtsvon", (x, y) -> x > y);
		
		final CSP<Integer> csp = new CSP<>(constraintFactory);

		csp.addVariable("brite", domain);
		csp.addVariable("schwede", domain);
		csp.addVariable("daene", domain);
		csp.addVariable("norweger", domain);
		csp.addVariable("deutscher", domain);
		
		csp.addVariable("rot", domain);
		csp.addVariable("gruen", domain);
		csp.addVariable("gelb", domain);
		csp.addVariable("weiss", domain);
		csp.addVariable("blau", domain);
		
		csp.addVariable("hund", domain);
		csp.addVariable("vogel", domain);
		csp.addVariable("katze", domain);
		csp.addVariable("pferd", domain);
		csp.addVariable("fisch", domain);
		
		csp.addVariable("tee", domain);
		csp.addVariable("kaffee", domain);
		csp.addVariable("milch", domain);
		csp.addVariable("bier", domain);
		csp.addVariable("wasser", domain);
		
		csp.addVariable("pallmall", domain);
		csp.addVariable("dunhill", domain);
		csp.addVariable("marlboro", domain);
		csp.addVariable("winfield", domain);
		csp.addVariable("rothmanns", domain);
		
		// unary
		csp.addBidirectionalConstraint("milch", "milch", "mittelhaus");        // 7
		csp.addBidirectionalConstraint("norweger", "norweger", "ersteshaus");  // 9
		
		// binary
		csp.addBidirectionalConstraint("brite", "rot", "gleich");              // 1
		csp.addBidirectionalConstraint("schwede", "hund", "gleich");           // 2
		csp.addBidirectionalConstraint("daene", "tee", "gleich");              // 3
		csp.addConstraint("gruen", "weiss", "linksvon");                       // 4.1
		csp.addConstraint("weiss", "gruen", "rechtsvon");                      // 4.2
		csp.addBidirectionalConstraint("gruen", "kaffee", "gleich");           // 5
		csp.addBidirectionalConstraint("pallmall", "vogel", "gleich");         // 6
		csp.addBidirectionalConstraint("gelb", "dunnhill", "gleich");          // 8
		csp.addBidirectionalConstraint("winfield", "bier", "gleich");          // 12
		csp.addBidirectionalConstraint("deutscher", "rothmanns", "gleich");    // 14
		csp.addBidirectionalConstraint("marlboro", "katze", "nachbarn");       // 10
		csp.addBidirectionalConstraint("pfed", "dunhill", "nachbarn");         // 11
		csp.addBidirectionalConstraint("norweger", "blau", "nachbarn");        // 13
		csp.addBidirectionalConstraint("marlboro", "wasser", "nachbarn");      // 15
		
		final List<String> nationen = Arrays.asList("brite", "schwede", "daene", "norweger", "deutscher");
		final List<String> farben = Arrays.asList("rot", "gruen", "gelb", "weiss", "blau");
		final List<String> tiere = Arrays.asList("hund", "vogel", "katze", "pferd", "fisch");
		final List<String> getraenke = Arrays.asList("tee", "kaffee", "milch", "bier", "wasser");
		final List<String> zigaretten = Arrays.asList("pallmall", "dunhill", "marlboro", "winfield", "rothmanns");
		
		addConstraintFor(csp, nationen, "ungleich");
		addConstraintFor(csp, farben, "ungleich");
		addConstraintFor(csp, tiere, "ungleich");
		addConstraintFor(csp, getraenke, "ungleich");
		addConstraintFor(csp, zigaretten, "ungleich");
		
		System.out.println(csp.solve());
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
