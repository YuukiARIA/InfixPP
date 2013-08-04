package infixpp;

import infixpp.ast.parser.Operator;
import infixpp.ast.parser.Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Map;

public class Main
{
	public static void main(String[] args)
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		try
		{
			Map<String, Operator> ops = Collections.emptyMap();
			String line;
			while (true)
			{
				System.out.print("> ");
				line = reader.readLine();
				if (line == null) break;

				line = line.trim();
				Parser parser = new Parser(line);
				parser.addOperatorDefinitions(ops);
				parser.parse();
				ops = parser.getDefinedOperators();
			}
		}
		catch (IOException e)
		{
		}
	}
}
/*
Define '<=>' := "eqv" left.
Define '=>' := "imp" right.
Define '\/' := "or" left.
Define '/\' := "and" left.
Order '<=>' < '=>' < '\/' < '/\'.

Define '*' := "mul" left.
Define '/' := "div" left.
Define '+' := "add" left.
Define '-' := "sub" left.
Define '^' := "pow" right.
Order ('+', '-') < ('*', '/') < '^'.
Translate "a" + "b" * "c" + "d" End

Order ('+', '-') < ('*', '/') < '^'.

*/
