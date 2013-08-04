package infixpp.ast.parser;

import infixpp.ast.parser.exception.LexerException;

public class Lexer
{
	private char[] cs;
	private int p;
	private int column;

	public Lexer(String text)
	{
		cs = text.toCharArray();
	}

	public Token lex() throws LexerException
	{
		skipws();

		if (end())
		{
			return new Token(Kind.END, "$", p + 1);
		}

		column = p + 1;

		switch (peek())
		{
		case '(':
			succ();
			return token(Kind.L_PAREN, "(");
		case ')':
			succ();
			return token(Kind.R_PAREN, ")");
		case '.':
			succ();
			return token(Kind.DOT, ".");
		case '"':
			return lexLiteral();
		case '\'':
			return lexOperatorSymbol();
		case ':':
			succ();
			if (peek() == '=')
			{
				succ();
				return token(Kind.DEF, ":=");
			}
			throw new LexerException("missing '='", column);
		case '<':
			succ();
			return token(Kind.WEAKER, "<");
		}

		if (Character.isDigit(peek()))
		{
			return lexInteger();
		}

		if (isAlpha(peek()))
		{
			StringBuilder buf = new StringBuilder();
			while (isAlpha(peek()))
			{
				buf.append(peek());
				succ();
			}
			String s = buf.toString();
			if (s.equals("Define"))
			{
				return token(Kind.KW_DEF, "Define");
			}
			else if (s.equals("Order"))
			{
				return token(Kind.KW_ORDER, "Order");
			}
			else if (s.equals("Translate"))
			{
				return token(Kind.KW_TRANSLATE, "Translate");
			}
			else if (s.equals("End"))
			{
				return token(Kind.KW_END, "End");
			}
			else if (s.equals("left"))
			{
				return token(Kind.KW_LEFT, "left");
			}
			else if (s.equals("right"))
			{
				return token(Kind.KW_RIGHT, "right");
			}
			throw new LexerException("unknown keyword " + s, column);
		}

		StringBuilder buf = new StringBuilder();
		while (!end() && !Character.isWhitespace(peek()))
		{
			buf.append(peek());
			succ();
		}
		return token(Kind.OPERATOR, buf.toString());
	}

	private Token lexInteger() throws LexerException
	{
		int value = 0;
		while (Character.isDigit(peek()))
		{
			value = 10 * value + (peek() - '0');
			succ();
		}
		return new Token(Kind.INTEGER, value, column);
	}

	private Token lexLiteral() throws LexerException
	{
		StringBuilder buf = new StringBuilder();
		if (peek() == '"')
		{
			succ();
			while (!end() && peek() != '"')
			{
				buf.append(peek());
				succ();
			}
			if (peek() == '"')
			{
				succ();
				return token(Kind.LITERAL, buf.toString());
			}
		}
		throw new LexerException("unexpected end in literal", -1);
	}

	private Token lexOperatorSymbol() throws LexerException
	{
		StringBuilder buf = new StringBuilder();
		if (peek() == '\'')
		{
			succ();
			while (!end() && Character.isWhitespace(peek())) succ();
			while (!end() && peek() != '\'' && !Character.isWhitespace(peek()))
			{
				buf.append(peek());
				succ();
			}
			while (!end() && Character.isWhitespace(peek())) succ();
			if (peek() == '\'')
			{
				succ();
				return token(Kind.OPERATOR_SYMBOL, buf.toString());
			}
		}
		throw new LexerException("wrong operator symbol", -1);
	}

	private Token token(Kind kind, String text)
	{
		return new Token(kind, text, column);
	}

	private void skipws()
	{
		while (!end() && Character.isWhitespace(peek())) succ();
	}

	private char peek()
	{
		return !end() ? cs[p] : 0;
	}

	private void succ()
	{
		if (!end()) ++p;
	}

	private boolean end()
	{
		return p >= cs.length;
	}

	private static boolean isAlpha(char c)
	{
		return 'A' <= c && c <= 'Z' || 'a' <= c && c <= 'z';
	}
}
