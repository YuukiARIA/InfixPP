package infixpp.ast.parser;

public enum Kind
{
	END,

	DOT,
	DEF,
	L_PAREN,
	R_PAREN,
	WEAKER,

	KW_DEF,
	KW_ORDER,
	KW_TRANSLATE,
	KW_END,
	KW_LEFT,
	KW_RIGHT,

	OPERATOR_SYMBOL,
	OPERATOR,
	INTEGER,
	LITERAL,
}
