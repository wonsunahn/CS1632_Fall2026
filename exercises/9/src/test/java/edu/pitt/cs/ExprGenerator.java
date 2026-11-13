package edu.pitt.cs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

public class ExprGenerator extends Generator<Expr> {
	/** The maximum depth of a generated tree, however large status.size() gets. */
	public static final int MAX_DEPTH = 4;

	private static final char[] OPS = { '+', '-', '*' };

	public ExprGenerator() {
		super(Expr.class);
	}

	/**
	 * Overridden generate method. Generates a random expression tree. Like
	 * ABCStringGenerator in sample_code/quickcheck_example/, it uses status.size()
	 * to start with small values and grow them, but for a tree it uses the
	 * logarithm of the size to decide the maximum depth:
	 * 
	 * <pre>
	 * maxDepth = log2(status.size() + 1), rounded down, but at most MAX_DEPTH
	 * </pre>
	 * 
	 * That is, sizes 0, 1-2, 3-6, 7-14, and 15 or more give a maxDepth of 0, 1, 2,
	 * 3, and 4. Java has no log2 method, but log2(n) is Math.log(n) / Math.log(2),
	 * and casting it to int rounds it down. Call status.size() only once, since
	 * every call returns a new random number.
	 * 
	 * Then build a tree whose depth is at most maxDepth, recursively:
	 * 
	 * <pre>
	 * 1. If maxDepth is 0, return a number node.
	 * 2. Otherwise, with probability 1/3 return a number node, and with
	 *    probability 2/3 return an operator node whose operator is chosen
	 *    uniformly from '+', '-', '*', and whose left and right children are
	 *    each generated recursively with a maxDepth of maxDepth - 1.
	 * </pre>
	 * 
	 * The digit of each number node is chosen uniformly from 0 to 9 inclusive.
	 * 
	 * @param random QuickCheck random number generator (similar to
	 *               java.util.Random)
	 * @param status Its size() decides the maximum depth of the tree
	 * @return Generated random expression tree
	 */
	@Override
	public Expr generate(SourceOfRandomness random, GenerationStatus status) {
		// TODO: Fill in. You may find it convenient to write a private recursive helper
		// method that takes the maximum allowed depth as a parameter.
		int maxDepth = (int) Math.min(Math.log(status.size() + 1) / Math.log(2), MAX_DEPTH);
		return Expr.num(0);
	}

	/**
	 * Overridden doShrink method. Returns these smaller trees, in this order (see
	 * How QuickCheck Shrinks in the README for why the order matters):
	 * 
	 * <pre>
	 * If larger is a number node:
	 *   1. A number node for each smaller digit, in increasing order starting
	 *      from 0 (none if the digit of larger is 0).
	 * If larger is an operator node with operator op, left child L, right child R:
	 *   1. L by itself.
	 *   2. R by itself.
	 *   3. For each candidate L' in doShrink(L), the tree binOp(op, L', R).
	 *   4. For each candidate R' in doShrink(R), the tree binOp(op, L, R').
	 * </pre>
	 * 
	 * @param random QuickCheck random number generator (similar to
	 *               java.util.Random)
	 * @param larger The tree to shrink
	 * @return List of smaller trees
	 */
	@Override
	public List<Expr> doShrink(SourceOfRandomness random, Expr larger) {
		// TODO: Fill in.
		return Collections.emptyList();
	}
}
