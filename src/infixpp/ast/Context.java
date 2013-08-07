package infixpp.ast;

import infixpp.ast.parser.Operator;

import java.util.HashMap;
import java.util.Map;

public class Context
{
	private Map<String, Operator> operators = new HashMap<String, Operator>();

	public void addOperator(String opStr, String nodeStr, boolean rightAssoc)
	{
		Operator op = new Operator(opStr.trim(), nodeStr, rightAssoc);
		operators.put(op.getNotation(), op);
	}

	public Operator getOperator(String s)
	{
		return operators.get(s.trim());
	}
}
