package infixpp.ast.parser;

import infixpp.ast.ASTNode;
import infixpp.ast.Context;
import infixpp.ast.parser.exception.LexerException;
import infixpp.ast.parser.exception.ParserException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Parser
{
	private Lexer lexer;
	private Token token;
	private boolean translate;

	private LinkedList<ASTNode> lst = new LinkedList<ASTNode>();

	public Parser(String text)
	{
		lexer = new Lexer(text);
	}

	public List<ASTNode> parse(Context ctx) throws ParserException
	{
		List<ASTNode> astList = new ArrayList<ASTNode>();
		next();
		while (!is(Kind.END))
		{
			if (is(Kind.KW_DEF))
			{
				parseDefinition(ctx);
			}
			else if (is(Kind.KW_ORDER))
			{
				parseOrder(ctx);
			}
			else if (is(Kind.KW_TRANSLATE))
			{
				ASTNode parsed = parseTranslate(ctx);
				astList.add(parsed);
			}
			else
			{
				throw makeException("statement must be started with Define/Order/Translate.");
			}
		}
		return Collections.unmodifiableList(astList);
	}

	private void parseDefinition(Context ctx) throws ParserException
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
				ctx.addOperator(op, nodeName, right);
				return;
			}
			throw makeException("literal.");
		}
		throw makeException("missing operator symbol.");
	}

	private void parseOrder(Context ctx) throws ParserException
	{
		expect(Kind.KW_ORDER, "Order");
		parseOrderExpression(ctx);
		expect(Kind.DOT, ".");
	}

	private int parseOrderExpression(Context ctx) throws ParserException
	{
		int prec = 0;
		Set<Operator> ops = parseOrderPrimary(ctx);
		if (is(Kind.WEAKER))
		{
			next();
			prec = parseOrderExpression(ctx) + 1;
		}
		for (Operator op : ops)
		{
			op.setPrecedence(prec);
		}
		return prec;
	}

	private Set<Operator> parseOrderPrimary(Context ctx) throws ParserException
	{
		Set<Operator> ops = new HashSet<Operator>();
		if (is(Kind.OPERATOR_SYMBOL))
		{
			ops.add(parseOperatorSymbol(ctx));
		}
		else if (is(Kind.L_PAREN))
		{
			next();
			parseOrderParallel(ctx, ops);
			expect(Kind.R_PAREN, ")");
		}
		else
		{
			throw makeException("unexpected " + token);
		}
		return ops;
	}

	private void parseOrderParallel(Context ctx, Set<Operator> ops) throws ParserException
	{
		ops.add(parseOperatorSymbol(ctx));
		while (is(Kind.COMMA))
		{
			next();
			ops.add(parseOperatorSymbol(ctx));
		}
	}

	private Operator parseOperatorSymbol(Context ctx) throws ParserException
	{
		if (is(Kind.OPERATOR_SYMBOL))
		{
			Operator op = ctx.getOperator(token.text);
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

	private ASTNode parseTranslate(Context ctx) throws ParserException
	{
		expect(Kind.KW_TRANSLATE, "Translate");

		lst.clear();
		translate = true;
		parseBinary(ctx);
		translate = false;

		expect(Kind.KW_END, "End");
		System.out.println(lst.peek());
		return lst.pop();
	}

	private void parseBinary(Context ctx) throws ParserException
	{
		LinkedList<Operator> ost = new LinkedList<Operator>();

		parsePrimary(ctx);
		while (is(Kind.OPERATOR))
		{
			String op = token.text;
			next();
			processOperator(ctx, ost, op);

			parsePrimary(ctx);
		}
		reduceAll(ost);
	}

	private void parsePrimary(Context ctx) throws ParserException
	{
		switch (token.kind)
		{
		case LITERAL:
			lst.push(new ASTNode.Literal(token.text));
			next();
			break;
		case L_PAREN:
			next();
			parseBinary(ctx);
			expect(Kind.R_PAREN, ")");
			break;
		default:
			throw makeException("unexpected " + token);
		}
	}

	private void processOperator(Context ctx, LinkedList<Operator> ost, String op) throws ParserException
	{
		Operator opdef1 = ctx.getOperator(op);

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
