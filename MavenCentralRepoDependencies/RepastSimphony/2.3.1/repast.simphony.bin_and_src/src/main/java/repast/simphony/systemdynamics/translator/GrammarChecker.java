package repast.simphony.systemdynamics.translator;

import java.util.List;
import java.util.Stack;

import repast.simphony.systemdynamics.support.ArrayReference;
import repast.simphony.systemdynamics.support.MutableBoolean;
import repast.simphony.systemdynamics.support.MutableInteger;
import repast.simphony.systemdynamics.support.NamedSubscriptManager;

public class GrammarChecker {

	public static final String ARRAY_INITIALIZATION = "ArrayInitialization";
	public static final String ASSIGNMENT = "Assignment";
	public static final String LOOKUP_DEFINITION = "LookupDefinition";
	public static final String SUBSCRIPT_DEFINITION = "SubscriptDefinition";

	public static final String COMMA = ",";
	public static final String SEMICOLON = ";";
	public static final String LEFT_PAREN = "(";
	public static final String RIGHT_PAREN = ")";
	public static final String VARIABLE_REFERENCE = "VariableReference";
	public static final String FUNCTION_REFERENCE = "FunctionReference";
	public static final String LOOKUP_REFERENCE = "LookupReference";
	public static final String BINARY_OPERATOR = "BinaryOperator";
	public static final String UNARY_OPERATOR = "UnaryOperator";

	public static final String ARRAY = "array.";
	public static final String FUNCTION = "sdFunctions.";
	public static final String SCALAR = "memory.";
	public static final String LOOKUP = "lookup.";
	
	Stack<MutableBoolean> functionStack = new Stack<MutableBoolean>();
	Stack<MutableInteger> openParenStack = new Stack<MutableInteger>();
	
	private boolean debugPrint = false;
	
	private Equation equation;

	/*
	 * Tokens are one of
	 * 
	 * variables constants operators parens argument/subscript separator square
	 * brackets function calls lookup definitions subscript definition
	 * 
	 * Statement types are of lookup definition subscript definition assignment
	 */

	List<String> tokens;

	public GrammarChecker(Equation eqn, List<String> tokens) {
		this.tokens = tokens;
		equation = eqn;
		
	}

	// make sure proper number of parens
	// ends with appropriate token type

	public OperationResult checkGrammar() {
		
		OperationResult or = new OperationResult();
		String type = determineEquationType(or);
		if (!or.isOk())
			return or;
		if (type.equals(ARRAY_INITIALIZATION)) {
			return checkArrayInitializationGrammar();
		}
		if (type.equals(ASSIGNMENT)) {
			return checkAssignmentGrammar();
		}
		if (type.equals(LOOKUP_DEFINITION)) {
			return checkLookupDefinitionGrammar();
		}
		if (type.equals(SUBSCRIPT_DEFINITION)) {
			return checkSubscriptDefinitionGrammar();
		}

		or.setErrorMessage("Cannot determine equation type for grammar check");
		return or;
	}
	
//	private OperationResult checkArrayInitializationGrammar() {
//		OperationResult or = new OperationResult();
//		
//		// for now assume OK, see if this works
//		
//		return or;
//	}
	
	private OperationResult checkArrayInitializationGrammar() {
		OperationResult or = new OperationResult();
		MutableInteger pos = new MutableInteger(0);
		ArrayReference arLHS = new ArrayReference(equation.getLhs());
		int numDimensions = arLHS.getSubscripts().size();
		boolean needSemiColon = numDimensions > 1;
		String token;

		// to have gotten this, we know that this is True.

		// format x[a] = 1,2,3,...
		// format x[a,b] = 1,2,3;4,5,6;...

		token = tokens.get(pos.value()); // LHS
		pos.add(1); 
		token = tokens.get(pos.value()); // = 
		pos.add(1);
		token = tokens.get(pos.value()); // first value

		boolean done = false;
		needSemiColon = true;
		
		// simplified grammar checking for array initialization
		// activate other code once we insure that subscript information is available 

		// Simple case for vectors: alternate between values and commas and end with a value
		if (!needSemiColon) {
			while(!done) {
				if (pos.value() % 2 == 0) {
					if (!Parser.isNumber(token)) {
						or.setErrorMessage("Expected numeric value. Found "+token+" in position "+pos.value());
						return or;
					}
				} else {
					if (!token.equals(COMMA)) {
						or.setErrorMessage("Expected comma. Found "+token+" in position "+pos.value());
						return or;
					}
				}

				pos.add(1);

				if (pos.value() >= tokens.size()) {
					// finished all tokens -- are we in right state?
					if (pos.value() % 2 == 0) { // i.e. would we be looking for an another number
						or.setErrorMessage("Array Initialization cannot end with a comma");
					}
					return or;
				}
				token = tokens.get(pos.value());
			}
			// here we have a multidimensional array
			// NOTE: Vensim limits this type of initialization to two dimensions
		} else {
			int[] numValues = new int[]{0, 0};
			List<String> subscripts = arLHS.getSubscripts();
			int sub = 0;
			NamedSubscriptManager nsm = InformationManagers.getInstance().getNamedSubscriptManager();
			for (String subr : subscripts) {
				int numV = nsm.getNumIndexFor(subr);
				if (sub < 2) {
					numValues[sub] = numV;
				} else {
					if (numV != 1) {
						
						// special case -- a single value supplied for all array slots
						if (tokens.size() == 3 && Parser.isNumber(token)) {
							return or;
						} else {
						or.setErrorMessage("Only the first two dimensions can vary in this array initialization");
							return or;
						}
					}
				}
				sub++;
			}
			
			while (pos.value() < tokens.size()) {
				// special case for internal minus sign which is stored as a separate token
				if (token.equals("_")) {
					pos.add(1);
					if (pos.value() < tokens.size()) {
						token = tokens.get(pos.value());
					} else {
						or.setErrorMessage("Unexpected end of data values");
						return or;
					}
				}
				if (Parser.isNumber(token)) {
					// everything ok
				} else {
				
					or.setErrorMessage("Expected numeric value. Found "+token+" in position "+pos.value());
					return or;
				}
				pos.add(1);
				if (pos.value() < tokens.size()) {
					token = tokens.get(pos.value());
				} else {
					// done! break
					break;
				}
				if (token.equals(COMMA) || token.equals(SEMICOLON) ) {
					// everything ok
				} else {
					or.setErrorMessage("Expected Separator. Found "+token+" in position "+pos.value());
					return or;
				}
				pos.add(1);
				if (pos.value() < tokens.size()) {
					token = tokens.get(pos.value());
				} else {
					// done! break
					break;
				}
			}
		}		

		return or;
	}

	private OperationResult checkAssignmentGrammar() {
		OperationResult or = new OperationResult();

		// modes:
		//
		// looking for binary operator
		// looking for not binary operator
		// unary -
		// variable
		// function reference
		// lookup reference

		// just need to verify classification of tokens
		// usage checked elsewhere
		
//		System.out.println("######## check assignment grammar ########");

		MutableBoolean lookingForBinaryOperator = new MutableBoolean(false);
		MutableBoolean lookingForArgumentSeparator = new MutableBoolean(false);
		
		
		
		functionStack.push(new MutableBoolean(false));
		openParenStack.push(new MutableInteger(0));
		
		MutableInteger openParens = openParenStack.peek();
		MutableBoolean processingFunctionInvocation = functionStack.peek();
		
		// push, pop, empty peek
		
		// nested functions will need their own processingFunction and openParen instances
		
		
		
		MutableInteger pos = new MutableInteger();
		MutableBoolean lhs = new MutableBoolean(true);
		
		String previousToken = "";

		int calls = 0;

		while (pos.value() < tokens.size()) {
			
			
			
			int posIn = pos.value();
			String token = tokens.get(pos.value());
//			if (pos.value() == 0 && (token.contains("Max_probabilities_of_exceeding_2_Deg_C") || 
//					token.contains("Max probabilities of exceeding 2 Deg C")))
//				debugPrint = true;

			grammerCheck(token, lhs, lookingForBinaryOperator,
					lookingForArgumentSeparator, /* functionStack, */
					previousToken, pos, /* openParenStack,  */ or);

			int posOut = pos.value();
			calls++;
			if (posIn == posOut) {
				or.setErrorMessage("Aborting loop stuck at pos = " + posIn);
				return or;
			}
		}
		if (openParens.value() != 0) {
			or.setErrorMessage("Mismatched parens");
		}
		if (!lookingForBinaryOperator.value()) {
			or.setErrorMessage("Illegal final token in equation");
		}
		return or;
	}

//	private void grammerCheck(String token,
//			MutableBoolean lhs,
//			MutableBoolean lookingForBinaryOperator,
//			MutableBoolean lookingForArgumentSeparator,
//			MutableBoolean processingFunctionInvocation, String previousToken,
//			MutableInteger pos, MutableInteger openParens, OperationResult or) {
	
	private void grammerCheck(String token,
			MutableBoolean lhs,
			MutableBoolean lookingForBinaryOperator,
			MutableBoolean lookingForArgumentSeparator,
			/* Stack<MutableBoolean> processingFunctionInvocationStack, */ String previousToken,
			MutableInteger pos, /* Stack<MutableInteger> openParensStack,*/  OperationResult or) {
		
		or.clear();
		MutableBoolean processingFunctionInvocation = functionStack.peek();
		MutableInteger openParens = openParenStack.peek();

		if (token.startsWith(ARRAY)) {
//			System.out.println("Array " + token);
			if (lookingForBinaryOperator.value()) {
				or.setErrorMessage("[1] Looking for operator found " + token
						+ " in pos " + pos.value());
				return;
			}
			if (lookingForArgumentSeparator.value()) {
				or.setErrorMessage("[2] Looking for argument separator found "
						+ token + " in pos " + pos.value());
				return;
			}
			previousToken = token;
			pos.add(1);
			lookingForBinaryOperator.setValue(true);
			return;
		} else if (token.startsWith(LOOKUP)) {
//			System.out.println("Lookup " + token);
			if (lookingForBinaryOperator.value()) {
				or.setErrorMessage("[3] Looking for operator found " + token
						+ " in pos " + pos.value());
				return;
			}
			if (lookingForArgumentSeparator.value()) {
				or.setErrorMessage("[4] Looking for argument separator found "
						+ token + " in pos " + pos.value());
				return;
			}
			// consumeLookup(lookingForBinaryOperator,
			// lookingForArgumentSeparator, processingFunctionInvocation,
			// previousToken, pos, openParens, or);
			// if (!or.isOk())
			// return;
			pos.add(1);
			lookingForBinaryOperator.setValue(true);
			return;

		} else if (token.startsWith(FUNCTION)) {
//			System.out.println("Function " + token);
			if (lookingForBinaryOperator.value()) {
				or.setErrorMessage("[5] Looking for operator found " + token
						+ " in pos " + pos.value());
				return;
			}
			if (lookingForArgumentSeparator.value()) {
				or.setErrorMessage("[6] Looking for argument separator found "
						+ token + " in pos " + pos.value());
				return;
			}
//			functionStack.push(new MutableBoolean(true));
//			openParenStack.push(new MutableInteger(0));
//			consumeFunction(lookingForBinaryOperator,
//					lookingForArgumentSeparator, processingFunctionInvocation,
//					previousToken, pos, openParens, or);
			
			consumeFunction(lookingForBinaryOperator,
					lookingForArgumentSeparator, /*functionStack, */
					previousToken, pos, /* openParenStack, */ or);
			
			
			if (!or.isOk())
				return;
			pos.add(1);
			lookingForBinaryOperator.setValue(true);
			return;

		} else if (token.startsWith(SCALAR)) {
//			System.out.println("Scalar " + token);
			if (lookingForBinaryOperator.value()) {
				or.setErrorMessage("[7] Looking for operator found " + token
						+ " in pos " + pos.value());
				return;
			}
			if (lookingForArgumentSeparator.value()) {
				or.setErrorMessage("[8] Looking for argument separator found "
						+ token + " in pos " + pos.value());
				return;
			}
			previousToken = token;
			pos.add(1);
			lookingForBinaryOperator.setValue(true);
			return;

		} else if (token.equals(LEFT_PAREN)) {
//			System.out.println("LeftParen " + token);
			if (lookingForBinaryOperator.value()) {
				or.setErrorMessage("[9] Looking for operator found " + token
						+ " in pos " + pos.value());
				return;
			}
			if (lookingForArgumentSeparator.value()) {
				or.setErrorMessage("[10] Looking for argument separator found "
						+ token + " in pos " + pos.value());
				return;
			}
			
			
			openParens.add(1);
			
			if (debugPrint) {
				System.out.println("GC: LP "+openParens.value());
			}
			
			pos.add(1);
			return;

		} else if (token.equals(RIGHT_PAREN)) { // this is a grouping paren
//			System.out.println("RightParen " + token);
			if (lookingForBinaryOperator.value()) {

				// If this paren matches an open paren, we are OK

				if (openParens.value() <= 0) {
					or.setErrorMessage("[11] Looking for operator found " + token
							+ " in pos " + pos.value());
					return;
				}
			}
			if (lookingForArgumentSeparator.value()) {
				or.setErrorMessage("[12] Looking for argument separator found "
						+ token + " in pos " + pos.value());
				return;
			}
			openParens.add(-1);
			
			if (debugPrint) {
				System.out.println("GC: RP "+openParens.value());
			}
			pos.add(1);
			return;
		} else if (token.equals(COMMA)) {
//			System.out.println("Comma " + token);
			if (lookingForBinaryOperator.value() && !processingFunctionInvocation.value()) {
				or.setErrorMessage("[13] Looking for operator found " + token
						+ " in pos " + pos.value());
				return;
			}
		} else if (Parser.isOperator(token)) {
//			System.out.println("Operator " + token);
			if (!lookingForBinaryOperator.value()) {

				if (Parser.isBinaryOperator(token)) {
					or.setErrorMessage("[14] not expecting binary operator but found "
							+ token + " in pos " + pos.value());
					return;
				} else if (Parser.isUnaryOperator(token)) {
//					System.out.println("Unary Operator OK" + token);
					previousToken = token;
					pos.add(1);
					lookingForBinaryOperator.setValue(false);
					return;
				}

			} else {
				previousToken = token;
				pos.add(1);
				lookingForBinaryOperator.setValue(false);
				return;
			}

		} else if (Parser.isNumber(token)) {
//			System.out.println("Number " + token);
			if (lookingForBinaryOperator.value()) {
				or.setErrorMessage("[15] Looking for operator found " + token
						+ " in pos " + pos.value());
				return;
			}
			if (lookingForArgumentSeparator.value()) {
				or.setErrorMessage("[16] Looking for argument separator found "
						+ token + " in pos " + pos.value());
				return;
			}
			previousToken = token;
			pos.add(1);
			lookingForBinaryOperator.setValue(true);
			return;
		} else if (Parser.isQuotedString(token)) {
//			System.out.println("Quoted String " + token);
			previousToken = token;
			pos.add(1);
			lookingForBinaryOperator.setValue(true);
		} else if (Parser.isLocalVariable(token)) {
//			System.out.println("Local variable " + token);
			previousToken = token;
			pos.add(1);
			lookingForBinaryOperator.setValue(true);
		} else {
//			System.out.println("Unknown token type" + token);
			or.setErrorMessage("Unknown token type " + token);
		}
	}

//	private void consumeFunction(MutableBoolean lookingForBinaryOperator,
//			MutableBoolean lookingForArgumentSeparator,
//			MutableBoolean processingFunctionInvocation, String previousToken,
//			MutableInteger pos, MutableInteger openParens, OperationResult or) {
	
	private void consumeFunction(MutableBoolean lookingForBinaryOperator,
			MutableBoolean lookingForArgumentSeparator,
			/* Stack<MutableBoolean> processingFunctionInvocationStack, */ String previousToken,
			MutableInteger pos, /* Stack<MutableInteger> openParensStack, */ OperationResult or) {

		or.clear();
//		processingFunctionInvocation.setValue(true);
		
		functionStack.push(new MutableBoolean(true));
		openParenStack.push(new MutableInteger(0));
		
		MutableInteger openParens = openParenStack.peek();
		MutableBoolean processingFunctionInvocation = functionStack.peek();
		
//		System.out.println("CF Start: "+openParens.value());
		
		MutableBoolean lhs = new MutableBoolean(false);

		// func is func_name ( notB , notB, ..., notB)

		boolean done = false;
//		int localOpenParen = 0;

		pos.add(1);
		String token = tokens.get(pos.value());
		if (!token.equals(LEFT_PAREN)) {
			or.setErrorMessage("[17] expecting open paren for function found "
					+ token + " in pos " + pos.value());
			return;
		}
		openParens.add(1);
		if (debugPrint) {
			System.out.println("CF: LP "+openParens.value());
		}
		
		// point to first argument
		pos.add(1);

		// process the tokens that appear within the left and right parens
		// we account for only opening and closing parens here. Other counts are performed in grammerCheck
		while (!done && pos.value() < tokens.size()) {
			token = tokens.get(pos.value());
	
			if (token.equals(RIGHT_PAREN)) {
				if (!lookingForBinaryOperator.value()) {
					or.setErrorMessage("[19] Found ) while expecting a variable/function call");
					return;
				}
								
				openParens.add(-1);
				if (debugPrint) {
					System.out.println("CF: RP "+openParens.value());
				}
				
//				localOpenParen--;
//				System.out.println("Right Paren "+" glob ( "+openParens.value()+" loc ( "+localOpenParen);
				
				// found closing paren
//				if (openParens.value() == 0) {
				if (/* localOpenParen == 0 || */ openParens.value() == 0) { // 9/25 try this || op == 0
					done = true;
//					System.out.println("ends function");
					processingFunctionInvocation.setValue(false);
					
					if (debugPrint) {
						System.out.println("CF: pop stacks");
					}
					
					openParenStack.pop();
					functionStack.pop();
					
					return;
				}
				pos.add(1);
				continue;
			}
			
			
			grammerCheck(token, lhs, lookingForBinaryOperator,
					lookingForArgumentSeparator, /* processingFunctionInvocationStack, */
					previousToken, pos, /* openParensStack,*/ or);
			if (!or.isOk())
				return;
			if (pos.value() >= tokens.size() && openParens.value() > 0) {
				or.setErrorMessage("[18] Cannot locate end of function invocation. # open = "+openParens.value());
				return;
			}
			// we may be done at this point
			if (pos.value() >= tokens.size())
				return;
			if (tokens.get(pos.value()).equals(COMMA)) {
//				System.out.println("Comma ");
				// need to grab another function argument
				pos.add(1);
				lookingForArgumentSeparator.setValue(false);
				lookingForBinaryOperator.setValue(false);
			}
		}
		System.out.println("EOT, OpenParens = "+openParens.value());
		or.setErrorMessage("[20] reached end of tokens without complete statment?");
		return;

	}

//	private void consumeLookup(MutableBoolean lookingForBinaryOperator,
//			MutableBoolean lookingForArgumentSeparator,
//			MutableBoolean processingFunctionInvocation, String previousToken,
//			MutableInteger pos, MutableInteger openParens, OperationResult or) {
//
//		// for now...
//		consumeFunction(lookingForBinaryOperator, lookingForArgumentSeparator,
//				processingFunctionInvocation, previousToken, pos, openParens,
//				or);
//	}

	private OperationResult checkLookupDefinitionGrammar() {

		OperationResult or = new OperationResult();

		// token stream:
		// <thisisalookup> <(> <[> <(> <0> <,> <0> <)> <-> <(> <100> <,> <100>
		// <)> <]> <,> <(> <12> <,> <12> <)> <,> <(> <12> <,> <12> <)> <)>
		// 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25
		// 26 27
		
		// or
		// x(0,1,2,3,4, 0.5, 0.4, 0.3, 0.2, 0.1); i.e. all X and all Y

		MutableInteger pos = new MutableInteger();
		String[] pattern = { "(", "#", ",", "#", ")" };

		String token = tokens.get(pos.value());
		if (!Parser.isWord(token) && !ArrayReference.isArrayReference(token)) {

			or.setErrorMessage("Invalid Lookup Name \"" + token
					+ "\" in position " + pos.value());
			return or;
		}

		pos.add(1);
		token = tokens.get(pos.value());
		if (!tokens.get(pos.value()).equals("(")) {
			or.setErrorMessage("Missing opening (");
			return or;
		}

		pos.add(1);
		token = tokens.get(pos.value());
		if (token.equals("[")) {
			pos.add(1);
			if (!patternMatch(pattern, tokens, pos, or))
				return or;
			// pos.add(1);
			token = tokens.get(pos.value());
			if (!token.equals("-")) {

				or.setErrorMessage("Invalid range separator(1) \"" + token
						+ "\" in position " + pos.value());
				return or;
			}
			pos.add(1);
			if (!patternMatch(pattern, tokens, pos, or))
				return or;
			// pos.add(1);
			token = tokens.get(pos.value());
			if (!token.equals("]")) {

				or.setErrorMessage("Invalid range terminator  \"" + token
						+ "\" in position " + pos.value());
				return or;
			}
			pos.add(1);
			token = tokens.get(pos.value());
			if (!token.equals(",")) {

				or.setErrorMessage("Invalid range separator(2) \"" + token
						+ "\" in position " + pos.value());
				;
				return or;
			}
			pos.add(1);

		} else if (!token.equals("(")) {
			// this is the alternate syntax with all X's and all Y's group together
			
			int numDataPoints = 0;
			
			while(pos.value() < tokens.size()) {
				// pos even -> should be number
				// pos odd -> should be comma or closing )
				if (pos.value() % 2 == 0) {
					if (!Parser.isNumber(tokens.get(pos.value()))) {
						or.setErrorMessage("Invalid numeric value "
								+ tokens.get(pos.value()));
						System.out.println("Invalid numeric value "
								+ tokens.get(pos.value()));
						return or;
					} else {
						numDataPoints++;
					}
				} else {
					if (!tokens.get(pos.value()).equals(",") && !tokens.get(pos.value()).equals(")")) {
						or.setErrorMessage("Invalid list separator "
								+ tokens.get(pos.value()));
						System.out.println("Invalid numeric value "
								+ tokens.get(pos.value()));
						return or;
				} 
			}
				String tok = tokens.get(pos.value());
			
			pos.add(1);
			if (tok.equals(LEFT_PAREN)) {
				if (numDataPoints == 0 || numDataPoints % 2 != 0) {
					or.setErrorMessage("incorrect number of data points provided in lookup table definition "+numDataPoints);
					System.out.println("incorrect number of data points provided in lookup table definition "+numDataPoints);
				}
				return or;
			}
			
			}
			return or;
		}
		// all that's left should be comma-separated pairs of numbers

		while (pos.value() < tokens.size() - pattern.length) {

			if (!patternMatch(pattern, tokens, pos, or))
				return or;
			// pos.add(1);
			token = tokens.get(pos.value());
			if (pos.value() != tokens.size() - 1 && !token.equals(",")) {

				or.setErrorMessage("Invalid range separator(3) \"" + token
						+ "\" in position " + pos.value());
				return or;
			}
			// if we are end of lookup table data
			if (pos.value() != tokens.size() - 1)
				pos.add(1);
		}
		// pos.add(1);
		token = tokens.get(pos.value());
		if (!token.equals(")")) {

			or.setErrorMessage("Missing closing paren \"" + token
					+ "\" in position " + pos.value());
			return or;
		}

		return or;
	}

	private boolean patternMatch(String[] pattern, List<String> tokens,
			MutableInteger pos, OperationResult or) {

//		System.out.println("patternMatch: pos = " + pos.value());

		for (int i = 0; i < pattern.length; i++) {
			if (pattern[i].equals("#")) {
				if (!Parser.isNumber(tokens.get(pos.value()))) {
					or.setErrorMessage("Invalid numeric value "
							+ tokens.get(pos.value()));
//					System.out.println("Invalid numeric value "
//							+ tokens.get(pos.value()));
					return false;
				}
			} else {
				if (!pattern[i].equals((tokens.get(pos.value())))) {
					or.setErrorMessage("Invalid token "
							+ tokens.get(pos.value()) + " expecting "
							+ pattern[i]);
//					System.out.println("Invalid token "
//							+ tokens.get(pos.value()) + " expecting "
//							+ pattern[i]);
					return false;
				}
			}
			pos.add(1);
		}

		return true;

	}

	private OperationResult checkSubscriptDefinitionGrammar() {
		OperationResult or = new OperationResult();

		// note that some preprocessing of the subscript definition has
		// already taken place and the resulting definition is actually quite
		// simple

		String token = tokens.get(0);
		if (!Parser.isWord(token)) {

			or.setErrorMessage("Invalid LHS " + token);
		}

		token = tokens.get(1);
		if (!token.equals(":")) {

			or.setErrorMessage("Invalid operator " + token);
			return or;
		}

		for (int pos = 2; pos < tokens.size(); pos++) {
			token = tokens.get(pos);
			if (pos % 2 == 1) {
				if (!token.equals(",")) {

					or.setErrorMessage("Invalid separator " + token);
					return or;
				}
			} else {
				if (!Parser.isWord(token)) {

					or.setErrorMessage("Invalid subscript value " + token);
					return or;
				}
			}
		}

		return or;
	}

	private String determineEquationType(OperationResult or) {
		String type = "UNKNOWN";
		or.clear();
		
		boolean initialization = true;
		
		// Need to worry about array initialization first
		if (tokens.get(0).startsWith(ARRAY)) {
			if (tokens.get(1).equals("=")) {
				for (int i = 2; i < tokens.size(); i++) {
					// even must be number, odd must be comma or semicolon
					if (i % 2 == 0) {
						if (!Parser.isNumber(tokens.get(i))) {
//							System.out.println("Looking for ");
							initialization = false;
							break;
						}
					} else {
						if (!(tokens.get(i).equals(COMMA) || tokens.get(i).equals(SEMICOLON)))
							initialization = false;
							break;
					}
				}
				if (initialization)
					return ARRAY_INITIALIZATION;
			}
			
		}

		for (String token : tokens) {
			if (token.equals(":")) {
				return SUBSCRIPT_DEFINITION;
			} else if (token.equals("=")) {
				return ASSIGNMENT;
			} else if (token.equals("(")) {
				return LOOKUP_DEFINITION;
			}
		}

		or.setErrorMessage("Cannot determine equation type for grammar check");
		return type;

	}
}
