package edu.haw.is.einstein;

import java.util.HashMap;

public class BinaryConstraintFactory<D> {
	
	private final HashMap<String, BinaryConstraint<D>>	commands;
	
	private BinaryConstraintFactory() {
		this.commands = new HashMap<>();
	}

	public void addCommand(final String name, final BinaryConstraint<D> command) {
		this.commands.put(name, command);
	}
	
	public boolean checkConstraint(final String name, final D x, final D y) {
		if (this.commands.containsKey(name)) {
			return this.commands.get(name).apply(x, y);
		}
		throw new UnsupportedOperationException();
	}

	public void listCommands() {
		System.out.println("Commands enabled :");
		this.commands.keySet().stream().forEach(System.out::println);
	}
	
	public static BinaryConstraintFactory init() throws RuntimeException {
		final BinaryConstraintFactory cf = new BinaryConstraintFactory();
		return cf;
	}
}