package infixpp.ast.parser;

import infixpp.ast.ASTNode;
import infixpp.ast.parser.exception.LexerException;

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

		defineOperator("/\\", 10, false);
		defineOperator("\\/", 20, false);
	}

	public void parse()
	{
		next();
		parseBinary();
		System.out.println(lst.peek());
	}

	private void parseBinary()
	{
		LinkedList<Operator> ost = new LinkedList<Operator>();

		parsePrimary();
		while (token.kind == Kind.OPERATOR)
		{
			String op = token.text;
			next();
			System.out.println("operator " + op);
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

		if (!ost.isEmpty())
		{
			Operator opdef0 = ost.peekFirst();
			if (opdef1.isWeakerThan(opdef0))
			{
				reduce(ost);
			}
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
		lst.push(new ASTNode.Binary(opdef.getNotation(), x, y));
	}

	private void defineOperator(String op, int prec, boolean right)
	{
		operators.put(op, new Operator(op, prec, right));
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
