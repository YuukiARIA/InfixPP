package infixpp.ast.parser;

import infixpp.ast.parser.exception.LexerException;

import java.util.HashMap;
import java.util.Map;

public class Lexer
{
	private static Map<String, Kind> keywords = new HashMap<String, Kind>();

	static
	{
		keywords.put("Define", Kind.KW_DEF);
		keywords.put("Order", Kind.KW_ORDER);
		keywords.put("Translate", Kind.KW_TRANSLATE);
		keywords.put("End", Kind.KW_END);
		keywords.put("left", Kind.KW_LEFT);
		keywords.put("right", Kind.KW_RIGHT);
	}

	private char[] cs;
	private int p;
	private int column;

	public Lexer(String text)
	{
		cs = text.toCharArray();
	}

	public Token lex(boolean inTranslate) throws LexerException
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
			if (!inTranslate)
			{
				succ();
				return token(Kind.DOT, ".");
			}
			break;
		case ',':
			if (!inTranslate)
			{
				succ();
				return token(Kind.COMMA, ",");
			}
			break;
		case '"':
			return lexLiteral();
		case '\'':
			return lexOperatorSymbol();
		case ':':
			if (!inTranslate)
			{
				succ();
				if (peek() == '=')
				{
					succ();
					return token(Kind.DEF, ":=");
				}
				throw new LexerException("missing '='", column);
			}
			break;
		case '<':
			if (!inTranslate)
			{
				succ();
				return token(Kind.WEAKER, "<");
			}
			break;
		}

		if (Character.isDigit(peek()))
		{
			return lexInteger();
		}
		else if (isAlpha(peek()))
		{
			StringBuilder buf = new StringBuilder();
			while (isAlpha(peek()))
			{
				buf.append(peek());
				succ();
			}
			String s = buf.toString();
			Kind kind = keywords.get(s);
			if (kind != null)
			{
				return token(kind, s);
			}
			throw new LexerException("unknown keyword '" + s + "'.", column);
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
			while (!end() && peek() != '"' && peek() != '\n')
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
		throw new LexerException("invalid literal.", column);
	}

	private Token lexOperatorSymbol() throws LexerException
	{
		StringBuilder buf = new StringBuilder();
		if (peek() == '\'')
		{
			succ();
			skipws();
			while (!end() && peek() != '\'')
			{
				if (Character.isWhitespace(peek()))
				{
					throw new LexerException("operator must not contain whitespaces.", column);
				}
				buf.append(peek());
				succ();
			}
			skipws();
			if (peek() == '\'')
			{
				succ();
				return token(Kind.OPERATOR_SYMBOL, buf.toString());
			}
		}
		throw new LexerException("invalid operator symbol.", column);
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
