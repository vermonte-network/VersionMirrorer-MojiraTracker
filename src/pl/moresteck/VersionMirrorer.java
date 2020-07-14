package pl.moresteck;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.text.SimpleDateFormat;

public class VersionMirrorer {
	public static String defaultLogFile = "log.txt";
	public static boolean yes = true;
	public static File operating = new File("operating_stats.txt");

	public static void main(String[] args) {
		log("VersionMirrorer v1.0.7 by Moresteck & DireMaster (2020-06-25) fired up!");
		CheckMojiraThread.lastMojiraJEcontent = getProperty(operating, "lastMojiraJEcontent");
		while (yes) {
			Thread check = new CheckNewestThread();
			Thread checkMojira = new CheckMojiraThread();
			check.start();
			checkMojira.start();

			try {
				Thread.sleep(10000);
				while (check.isAlive() || checkMojira.isAlive());
				check = null;
				checkMojira = null;
				//yes = false;
			} catch (Throwable t) {
				log("what the FUCK?!?!??!");
				t.printStackTrace();
			}
		}
	}

	public static void log(String message) {
		System.out.println(new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss] ").format(Long.valueOf(System.currentTimeMillis())) + message);
		logToFile(defaultLogFile, message);
	}

	public static void logToFile(String file, String message) {	
		try {
			BufferedWriter logWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8"));
			logWriter.append(new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss] ").format(Long.valueOf(System.currentTimeMillis())) + message);
			logWriter.newLine();
			logWriter.close();
		} catch (IOException e){
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	public static boolean download(String link, File toFile) {
		try {
			toFile.createNewFile();
			// Start download
			URL url = new URL(link);
			BufferedInputStream inputst = new BufferedInputStream(url.openStream());
			FileOutputStream outputst = new FileOutputStream(toFile);
			byte[] buffer = new byte[1024];
			int count = 0;
			while((count = inputst.read(buffer, 0, 1024)) != -1) {
				outputst.write(buffer, 0, count);
			}
			outputst.close();
			inputst.close();
			return true;
		} catch (Throwable ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public static String getProperty(File file, String property) {
		String[] lines = read(file);
		String value = "";
		for (int i = 0; i < lines.length; i++) {
			// If the array is empty, ignore it
			if (lines[i] == null) continue;

			// Check if the property matches
			if (lines[i].startsWith(property + ":")) {
				value = lines[i].substring(property.length() + 1, lines[i].length());
				break;
			}
		}
		return value;
	}

	public static void setProperty(File file, String property, String value) {
		// Read the lines
		String[] lines = read(file);
		String[] newlines = new String[lines.length + 1];

		// Try to find the property wanted to be set
		boolean found = false;
		for (int i = 0; i < lines.length; i++) {
			if (lines[i] == null) continue;
			if (lines[i].startsWith(property + ":")) {
				// The wanted property has been found, so we're going to replace its value
				newlines[i] = property + ":" + value;
				found = true;
				continue;
			}
			// The property didn't match, just take this line further
			newlines[i] = lines[i];
		}

		if (!found) {
			// There was no wanted property in the file, so we're going to append it to the file 
			write(file, new String[] {property + ":" + value}, true);
			return;
		}

		// Write to file, without appending
		write(file, newlines, false);
	}

	public static String[] read(File file) {
		if (!file.exists()) return new String[] {};
		InputStreamReader reader = null;
		try {
			// Read in UTF-8
			reader = new InputStreamReader(new FileInputStream(file), "UTF-8");
			StringBuilder inputB = new StringBuilder();
			char[] buffer = new char[1024];
			while (true) {
				int readcount = reader.read(buffer);
				if (readcount < 0) break;
				inputB.append(buffer, 0, readcount);
			}
			return inputB.toString().split("\n");
		} catch (Throwable t) {
			log("A critical error occurred while reading from file: " + file);
			t.printStackTrace();
		} finally {
			// Close the file
			try {reader.close();} catch (Exception ex) {}
		}
		return null;
	}

	public static void write(File file, String[] lines, boolean append) {
		try {
			// Create new file, if it doesn't already exist
			file.createNewFile();
		} catch (Throwable t) {
			System.out.println(file.toPath().toString());
			t.printStackTrace();
		}
		OutputStreamWriter writer = null;
		try {
			// Write in UTF-8
			writer = new OutputStreamWriter(new FileOutputStream(file, append), "UTF-8");
			for (int i = 0; i < lines.length; i++) {
				// Skip empty lines
				if (lines[i] != null) {
					writer.write(lines[i] + "\n");
				}
			}
		} catch (Throwable t) {
			log("A critical error occurred while attempting to write to file: " + file);
			t.printStackTrace();
		} finally {
			// Close the file
			try {writer.close();} catch (Exception ex) {}
		}
	}
}
