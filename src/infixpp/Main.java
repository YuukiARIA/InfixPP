package infixpp;

import infixpp.ast.parser.Parser;
import infixpp.ast.parser.exception.ParserException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main
{
	public static void main(String[] args)
	{
		String source = readAll("sample.txt");
		Parser parser = new Parser(source);
		try
		{
			parser.parse();
		}
		catch (ParserException e)
		{
			System.err.println("Syntax error: " + e.getMessage() + " " + e.getLocation());
		}
	}

	private static String readAll(String fileName)
	{
		StringBuilder buf = new StringBuilder();
		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
			String line;
			while ((line = reader.readLine()) != null)
			{
				buf.append(line);
				buf.append('\n');
			}
			reader.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return buf.toString();
	}
}
