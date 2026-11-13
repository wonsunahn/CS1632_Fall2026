package edu.pitt.cs;

import java.util.Objects;

/**
 * An immutable arithmetic expression tree. An Expr is either a number node
 * holding a single digit (0-9), or an operator node holding one of '+', '-',
 * '*' and two child Exprs. Because an expression is a tree rather than a
 * string, every Expr you can construct is well-formed by construction: there is
 * no way to build one with an unbalanced parenthesis or a dangling operator.
 * 
 * Use the factory methods num and binOp to build trees, e.g., the tree for
 * "(1+2)*3" is binOp('*', binOp('+', num(1), num(2)), num(3)).
 */
public final class Expr {
	private final int digit; // valid only if op == 0
	private final char op; // 0 for a number node, otherwise '+', '-', or '*'
	private final Expr left;
	private final Expr right;

	private Expr(int digit, char op, Expr left, Expr right) {
		this.digit = digit;
		this.op = op;
		this.left = left;
		this.right = right;
	}

	/**
	 * Creates a number node.
	 * 
	 * @param digit The value of the number, between 0 and 9 inclusive
	 * @return A number node
	 */
	public static Expr num(int digit) {
		if (digit < 0 || digit > 9) {
			throw new IllegalArgumentException("Expr.num: digit must be 0-9 but was " + digit);
		}
		return new Expr(digit, (char) 0, null, null);
	}

	/**
	 * Creates an operator node.
	 * 
	 * @param op    The operator, one of '+', '-', or '*'
	 * @param left  The left operand
	 * @param right The right operand
	 * @return An operator node
	 */
	public static Expr binOp(char op, Expr left, Expr right) {
		if (op != '+' && op != '-' && op != '*') {
			throw new IllegalArgumentException("Expr.binOp: op must be '+', '-', or '*' but was '" + op + "'");
		}
		if (left == null || right == null) {
			throw new IllegalArgumentException("Expr.binOp: operands must not be null");
		}
		return new Expr(0, op, left, right);
	}

	/** @return Whether this is a number node (as opposed to an operator node) */
	public boolean isNum() {
		return op == 0;
	}

	/** @return The digit of a number node */
	public int getDigit() {
		if (!isNum()) {
			throw new IllegalStateException("getDigit called on an operator node");
		}
		return digit;
	}

	/** @return The operator ('+', '-', or '*') of an operator node */
	public char getOp() {
		if (isNum()) {
			throw new IllegalStateException("getOp called on a number node");
		}
		return op;
	}

	/** @return The left operand of an operator node */
	public Expr getLeft() {
		if (isNum()) {
			throw new IllegalStateException("getLeft called on a number node");
		}
		return left;
	}

	/** @return The right operand of an operator node */
	public Expr getRight() {
		if (isNum()) {
			throw new IllegalStateException("getRight called on a number node");
		}
		return right;
	}

	/**
	 * Computes the value of this expression directly from the tree. This serves as
	 * the test oracle: since the tree structure already encodes the order of
	 * operations, no parsing is needed to compute the correct value.
	 * 
	 * @return The value of this expression
	 */
	public long value() {
		if (isNum()) {
			return digit;
		}
		long l = left.value();
		long r = right.value();
		switch (op) {
		case '+':
			return l + r;
		case '-':
			return l - r;
		default:
			return l * r;
		}
	}

	/**
	 * Converts this tree into a string that Calculator.evaluate should be able to
	 * parse back into exactly the same tree. Parentheses are added only where
	 * needed to preserve the tree structure given the precedence and
	 * left-associativity rules of Calculator.evaluate. For example,
	 * binOp('-', binOp('-', num(8), num(4)), num(2)) is rendered as "8-4-2", while
	 * binOp('-', num(8), binOp('-', num(4), num(2))) is rendered as "8-(4-2)".
	 * 
	 * @return The expression as a string
	 */
	public String render() {
		if (isNum()) {
			return Integer.toString(digit);
		}
		String l = left.render();
		String r = right.render();
		// A left operand needs parentheses only if it binds more loosely than op.
		if (precedence(left) < precedence(this)) {
			l = "(" + l + ")";
		}
		// Since all operators are left-associative, a right operand also needs
		// parentheses if it binds equally tightly (e.g. "8-(4-2)", "2*(3*4)").
		if (precedence(right) <= precedence(this)) {
			r = "(" + r + ")";
		}
		return l + op + r;
	}

	/**
	 * @return The number of nodes in this tree, a measure of how "big" it is
	 */
	public int size() {
		return isNum() ? 1 : 1 + left.size() + right.size();
	}

	/**
	 * @return The depth of this tree, where a number node has depth 0
	 */
	public int depth() {
		return isNum() ? 0 : 1 + Math.max(left.depth(), right.depth());
	}

	private static int precedence(Expr e) {
		if (e.isNum()) {
			return 3;
		}
		return e.op == '*' ? 2 : 1;
	}

	/**
	 * Two Exprs are equal if they are structurally identical trees. QuickCheck
	 * relies on equals to discard shrink candidates identical to the original.
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Expr)) {
			return false;
		}
		Expr other = (Expr) o;
		return digit == other.digit && op == other.op && Objects.equals(left, other.left)
				&& Objects.equals(right, other.right);
	}

	@Override
	public int hashCode() {
		return Objects.hash(digit, op, left, right);
	}

	/**
	 * QuickCheck uses toString to print counterexamples, so we print the rendered
	 * expression.
	 */
	@Override
	public String toString() {
		return render();
	}
}
