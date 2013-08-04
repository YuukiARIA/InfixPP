package infixpp.ast.parser;

import infixpp.ast.ASTNode;
import infixpp.ast.parser.exception.LexerException;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Parser
{
	private Lexer lexer;
	private Token token;

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

	public void parse()
	{
		next();
		if (token.kind == Kind.KW_DEF)
		{
			parseDefinition();
		}
		else if (token.kind == Kind.KW_ORDER)
		{
			parseOrder();
		}
		else
		{
			parseBinary();
		}
	}

	private void parseDefinition()
	{
		if (token.kind == Kind.KW_DEF)
		{
			next();
			if (token.kind == Kind.OPERATOR_SYMBOL)
			{
				String op = token.text;
				next();
				if (token.kind == Kind.DEF)
				{
					next();
					if (token.kind == Kind.LITERAL)
					{
						String nodeName = token.text;
						next();
						if (token.kind == Kind.INTEGER)
						{
							int prec = token.intValue;
							next();
							if (token.kind == Kind.KW_LEFT || token.kind == Kind.KW_RIGHT)
							{
								boolean right = token.kind == Kind.KW_RIGHT;
								next();
								defineOperator(op, nodeName, prec, right);
								return;
							}
							throw new RuntimeException("specify associativity 'left' or 'right'.");
						}
						throw new RuntimeException("specify precedence.");
					}
					throw new RuntimeException("literal.");
				}
				throw new RuntimeException("expected ':='.");
			}
			throw new RuntimeException("missing operator symbol.");
		}
		throw new RuntimeException("wrong definition statement.");
	}

	private void parseOrder()
	{
		if (token.kind == Kind.KW_ORDER)
		{
			next();
			parseOrderExpression();
			if (token.kind == Kind.DOT)
			{
				next();
				return;
			}
		}
		throw new RuntimeException("wrong order");
	}

	private void parseOrderExpression()
	{
		parseOrderPrimary();
		while (token.kind == Kind.WEAKER)
		{
			next();
			parseOrderPrimary();
		}
	}

	private void parseOrderPrimary()
	{
		switch (token.kind)
		{
		case OPERATOR_SYMBOL:
			next();
			break;
		case L_PAREN:
			next();
			parseOrderExpression();
			if (token.kind == Kind.R_PAREN)
			{
				next();
				break;
			}
			else
			{
				throw new RuntimeException("missing ')'");
			}
		default:
			throw new RuntimeException("unexpected " + token);
		}
	}

	private void parseBinary()
	{
		LinkedList<Operator> ost = new LinkedList<Operator>();

		parsePrimary();
		while (token.kind == Kind.OPERATOR)
		{
			String op = token.text;
			next();
			processOperator(ost, op);

			parsePrimary();
		}
		reduceAll(ost);
		System.out.println(lst.peek());
	}

	private void parsePrimary()
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
			if (token.kind == Kind.R_PAREN)
			{
				next();
				break;
			}
			else
			{
				throw new RuntimeException("missing ')'");
			}
		default:
			throw new RuntimeException("unexpected " + token);
		}
	}

	private void processOperator(LinkedList<Operator> ost, String op)
	{
		Operator opdef1 = operators.get(op);

		if (opdef1 == null)
		{
			throw new RuntimeException("undefined operator '" + op + "'");
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
		lst.push(new ASTNode.Binary(opdef, x, y));
	}

	private void defineOperator(String op, String nodeName, int prec, boolean right)
	{
		operators.put(op, new Operator(op, nodeName, prec, right));
	}

	private void next()
	{
		try
		{
			token = lexer.lex();
		}
		catch (LexerException e)
		{
			throw new RuntimeException(e);
		}
	}
}