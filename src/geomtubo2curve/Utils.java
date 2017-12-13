package geomtubo2curve;

import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

public class Utils {
	public static String readString(File file) {
		String str = "";
		try {
			FileInputStream in = new FileInputStream(file);
			// size 为字串的长度 ，这里一次性读完
			int size = in.available();
			byte[] buffer = new byte[size];
			in.read(buffer);
			in.close();
			str = new String(buffer, "GB2312");

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return str;
	}

	public static void writeString(File file, String str) {
		FileWriter writer;
		try {
			writer = new FileWriter(file);
			writer.write(str);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String readDataFromConsole(String prompt) {  
        Console console = System.console();  
        if (console == null) {  
            throw new IllegalStateException("Console is not available!");  
        }  
        return console.readLine(prompt);  
    }  
}
