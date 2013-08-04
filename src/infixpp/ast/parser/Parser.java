package infixpp.ast.parser;

import infixpp.ast.ASTNode;
import infixpp.ast.parser.exception.LexerException;

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
		else if (token.kind == Kind.KW_TRANSLATE)
		{
			parseTranslate();
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
						boolean right = false;
						if (token.kind == Kind.KW_LEFT || token.kind == Kind.KW_RIGHT)
						{
							right = token.kind == Kind.KW_RIGHT;
							next();
						}
						if (token.kind == Kind.DOT)
						{
							next();
							defineOperator(op, nodeName, right);
							return;
						}
						throw new RuntimeException("missing '.'");
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
			else
			{
				throw new RuntimeException("missing '.'");
			}
		}
		throw new RuntimeException("wrong order");
	}

	private int parseOrderExpression()
	{
		int prec = 0;
		Set<String> ops = parseOrderPrimary();
		if (token.kind == Kind.WEAKER)
		{
			next();
			prec = parseOrderExpression() + 1;
		}
		for (String opstr : ops)
		{
			Operator op = operators.get(opstr);
			if (op == null)
			{
				System.out.println("undefined operator " + op);
				continue;
			}
			op.setPrecedence(prec);
		}
		return prec;
	}

	private Set<String> parseOrderPrimary()
	{
		Set<String> ops = new HashSet<String>();
		if (token.kind == Kind.OPERATOR_SYMBOL)
		{
			ops.add(token.text);
			next();
		}
		else if (token.kind == Kind.L_PAREN)
		{
			next();
			parseOrderParallel(ops);
			if (token.kind == Kind.R_PAREN)
			{
				next();
			}
			else
			{
				throw new RuntimeException("missing ')'");
			}
		}
		else
		{
			throw new RuntimeException("unexpected " + token);
		}
		return ops;
	}

	private void parseOrderParallel(Set<String> ops)
	{
		if (token.kind == Kind.OPERATOR_SYMBOL)
		{
			ops.add(token.text);
			next();
			while (token.kind == Kind.COMMA)
			{
				next();
				if (token.kind == Kind.OPERATOR_SYMBOL)
				{
					ops.add(token.text);
					next();
				}
				else
				{
					throw new RuntimeException("expected operator symbol");
				}
			}
		}
		else
		{
			throw new RuntimeException("expected operator symbol");
		}
	}

	private void parseTranslate()
	{
		if (token.kind == Kind.KW_TRANSLATE)
		{
			next();

			lst.clear();
			translate = true;
			parseBinary();
			translate = false;

			if (token.kind == Kind.KW_END)
			{
				next();
				System.out.println(lst.peek());
			}
			else
			{
				throw new RuntimeException("missing keyword 'End'");
			}
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

	private void defineOperator(String op, String nodeName, boolean right)
	{
		operators.put(op, new Operator(op, nodeName, right));
	}

	private void next()
	{
		try
		{
			token = lexer.lex(translate);
		}
		catch (LexerException e)
		{
			throw new RuntimeException(e);
		}
	}
}
