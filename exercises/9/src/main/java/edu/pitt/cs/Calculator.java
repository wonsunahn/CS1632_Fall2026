package edu.pitt.cs;

public class Calculator {

	/**
	 * Evaluates an integer arithmetic expression and returns its value. The
	 * expression is made up of single-digit integer literals, the binary operators
	 * '+', '-', and '*', and parentheses. Whitespace is not allowed. The usual
	 * rules of arithmetic apply: '*' binds tighter than '+' and '-', and all three
	 * operators are left-associative, meaning "8-4-2" is evaluated as "(8-4)-2".
	 * Formally, the accepted language is described by this grammar:
	 * 
	 * <pre>
	 * expr   := term   (('+' | '-') term)*
	 * term   := factor ('*' factor)*
	 * factor := digit | '(' expr ')'
	 * digit  := '0' | '1' | ... | '9'
	 * </pre>
	 * 
	 * Some examples: "1+2*3" is 7, "(1+2)*3" is 9, "8-4-2" is 2, "8-(4-2)" is 6.
	 * 
	 * @param s String containing the expression
	 * @return The value of the expression
	 * @throws IllegalArgumentException If s is not a well-formed expression
	 */
	public static long evaluate(String s) {
		// TODO: Fix bug!
		if (s == null) {
			throw new IllegalArgumentException("null expression");
		}
		Parser parser = new Parser(s);
		long value = parser.parseExpr();
		if (parser.pos != s.length()) {
			throw new IllegalArgumentException("Unexpected character at position " + parser.pos + " in '" + s + "'");
		}
		return value;
	}

	/**
	 * A recursive descent parser that evaluates the expression while parsing it.
	 * Each parse method consumes characters starting at pos and returns the value
	 * of the part of the expression it consumed.
	 */
	private static class Parser {
		private final String s;
		private int pos = 0;

		Parser(String s) {
			this.s = s;
		}

		/**
		 * @return The character at the current position, or -1 if at end of string
		 */
		private int peek() {
			return pos < s.length() ? s.charAt(pos) : -1;
		}

		/**
		 * expr := term (('+' | '-') term)*
		 */
		long parseExpr() {
			long left = parseTerm();
			if (peek() == '+') {
				pos++;
				return left + parseExpr();
			}
			if (peek() == '-') {
				pos++;
				return left - parseExpr();
			}
			return left;
		}

		/**
		 * term := factor ('*' factor)*
		 */
		long parseTerm() {
			long left = parseFactor();
			if (peek() == '*') {
				pos++;
				return left * parseTerm();
			}
			return left;
		}

		/**
		 * factor := digit | '(' expr ')'
		 */
		long parseFactor() {
			int c = peek();
			if (c >= '0' && c <= '9') {
				pos++;
				return c - '0';
			}
			if (c == '(') {
				pos++;
				long value = parseExpr();
				if (peek() != ')') {
					throw new IllegalArgumentException("Expected ')' at position " + pos + " in '" + s + "'");
				}
				pos++;
				return value;
			}
			throw new IllegalArgumentException("Expected digit or '(' at position " + pos + " in '" + s + "'");
		}
	}
}
