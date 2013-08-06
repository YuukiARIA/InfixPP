package infixpp.ast.parser;

import infixpp.ast.ASTNode;
import infixpp.ast.parser.exception.LexerException;
import infixpp.ast.parser.exception.ParserException;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class Parser
{
	private Lexer lexer;
	private Token token;
	private boolean translate;

	private Map<String, Operator> operators = new HashMap<String, Operator>();
	private LinkedList<ASTNode> lst = new LinkedList<ASTNode>();

	public Parser(String text)
	{
		lexer = new Lexer(text);
	}

	public Map<String, Operator> getDefinedOperators()
	{
		return Collections.unmodifiableMap(operators);
	}

	public void addOperatorDefinitions(Map<String, Operator> definitions)
	{
		operators.putAll(definitions);
	}

	public void parse() throws ParserException
	{
		next();
		while (!is(Kind.END))
		{
			if (is(Kind.KW_DEF))
			{
				parseDefinition();
			}
			else if (is(Kind.KW_ORDER))
			{
				parseOrder();
			}
			else if (is(Kind.KW_TRANSLATE))
			{
				parseTranslate();
			}
			else
			{
				throw makeException("statement must be started with Define/Order/Translate.");
			}
		}
	}

	private void parseDefinition() throws ParserException
	{
		expect(Kind.KW_DEF, "Define");
		if (is(Kind.OPERATOR_SYMBOL))
		{
			String op = token.text;
			next();
			expect(Kind.DEF, ":=");
			if (is(Kind.LITERAL))
			{
				String nodeName = token.text;
				next();
				boolean right = false;
				if (is(Kind.KW_LEFT) || is(Kind.KW_RIGHT))
				{
					right = is(Kind.KW_RIGHT);
					next();
				}
				expect(Kind.DOT, ".");
				defineOperator(op, nodeName, right);
				return;
			}
			throw makeException("literal.");
		}
		throw makeException("missing operator symbol.");
	}

	private void parseOrder() throws ParserException
	{
		expect(Kind.KW_ORDER, "Order");
		parseOrderExpression();
		expect(Kind.DOT, ".");
	}

	private int parseOrderExpression() throws ParserException
	{
		int prec = 0;
		Set<Operator> ops = parseOrderPrimary();
		if (is(Kind.WEAKER))
		{
			next();
			prec = parseOrderExpression() + 1;
		}
		for (Operator op : ops)
		{
			op.setPrecedence(prec);
		}
		return prec;
	}

	private Set<Operator> parseOrderPrimary() throws ParserException
	{
		Set<Operator> ops = new HashSet<Operator>();
		if (is(Kind.OPERATOR_SYMBOL))
		{
			ops.add(parseOperatorSymbol());
		}
		else if (is(Kind.L_PAREN))
		{
			next();
			parseOrderParallel(ops);
			expect(Kind.R_PAREN, ")");
		}
		else
		{
			throw makeException("unexpected " + token);
		}
		return ops;
	}

	private void parseOrderParallel(Set<Operator> ops) throws ParserException
	{
		ops.add(parseOperatorSymbol());
		while (is(Kind.COMMA))
		{
			next();
			ops.add(parseOperatorSymbol());
		}
	}

	private Operator parseOperatorSymbol() throws ParserException
	{
		if (is(Kind.OPERATOR_SYMBOL))
		{
			Operator op = operators.get(token.text);
			if (op == null)
			{
				throw makeException("undefined operator '" + token.text + "'");
			}
			next();
			return op;
		}
		else
		{
			throw makeException("expected operator symbol.");
		}
	}

	private void parseTranslate() throws ParserException
	{
		expect(Kind.KW_TRANSLATE, "Translate");

		lst.clear();
		translate = true;
		parseBinary();
		translate = false;

		expect(Kind.KW_END, "End");
		System.out.println(lst.peek());
	}

	private void parseBinary() throws ParserException
	{
		LinkedList<Operator> ost = new LinkedList<Operator>();

		parsePrimary();
		while (is(Kind.OPERATOR))
		{
			String op = token.text;
			next();
			processOperator(ost, op);

			parsePrimary();
		}
		reduceAll(ost);
	}

	private void parsePrimary() throws ParserException
	{
		switch (token.kind)
		{
		case LITERAL:
			lst.push(new ASTNode.Literal(token.text));
			next();
			break;
		case L_PAREN:
			next();
			parseBinary();
			expect(Kind.R_PAREN, ")");
			break;
		default:
			throw makeException("unexpected " + token);
		}
	}

	private void processOperator(LinkedList<Operator> ost, String op) throws ParserException
	{
		Operator opdef1 = operators.get(op);

		if (opdef1 == null)
		{
			throw makeException("undefined operator '" + op + "'");
		}

		while (!ost.isEmpty() && ost.peek().shouldBeReducedBefore(opdef1))
		{
			reduce(ost);
		}
		ost.push(opdef1);
	}

	private void reduceAll(LinkedList<Operator> ost)
	{
		while (!ost.isEmpty())
		{
			reduce(ost);
		}
	}

	private void reduce(LinkedList<Operator> ost)
	{
		Operator opdef = ost.pop();
		ASTNode y = lst.pop();
		ASTNode x = lst.pop();
		lst.push(x.bind(opdef, y));
	}

	private void defineOperator(String op, String nodeName, boolean right)
	{
		operators.put(op, new Operator(op, nodeName, right));
	}

	private boolean is(Kind kind)
	{
		return token.kind == kind;
	}

	private void expect(Kind kind, String s) throws ParserException
	{
		if (token.kind == kind)
		{
			next();
		}
		else
		{
			throw makeException("expected '" + s + "'");
		}
	}

	private ParserException makeException(String message)
	{
		return new ParserException(message, token.location);
	}

	private void next() throws ParserException
	{
		try
		{
			token = lexer.lex(translate);
		}
		catch (LexerException e)
		{
			throw new ParserException(e);
		}
	}
}
