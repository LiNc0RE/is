package edu.haw.is.einstein;

@FunctionalInterface
public interface BinaryConstraint<D> {
	public boolean apply(D x, D y);
}
