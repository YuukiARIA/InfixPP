package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public final class TextFileIO
{
	private TextFileIO() { }

	public static String read(File file) throws FileNotFoundException, IOException
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		StringBuilder buf = new StringBuilder();
		String line;
		boolean first = true;
		while ((line = reader.readLine()) != null)
		{
			if (!first)
			{
				buf.append('\n');
			}
			buf.append(line);
			first = false;
		}
		reader.close();
		return buf.toString();
	}

	public static void write(File file, String text) throws FileNotFoundException, IOException
	{
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
		writer.write(text);
		writer.close();
	}
}
