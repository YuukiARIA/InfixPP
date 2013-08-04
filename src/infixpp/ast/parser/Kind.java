package infixpp.ast.parser;

public enum Kind
{
	END,

	DOT,
	DEF,
	KW_END,
	L_PAREN,
	R_PAREN,

	KW_DEF,
	KW_TRANSLATE,
	KW_LEFT,
	KW_RIGHT,

	OPERATOR_SYMBOL,
	OPERATOR,
	INTEGER,
	LITERAL,
}
