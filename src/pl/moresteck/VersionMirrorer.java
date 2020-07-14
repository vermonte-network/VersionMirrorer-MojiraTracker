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
	public static String errorlogfile = "error-log.txt";
	public static String mojiralogfile = "mojira-log.txt";
	public static String manifestlogfile = "version-log.txt";
	public static boolean yes = true;
	public static File operating = new File("operating_stats.txt");


	public static void main(String[] args) {
		log("VersionMirrorer v1.0.8 by Moresteck & DireMaster (2020-07-02) fired up!");
		CheckMojiraThread.lastMojiraJEcontent = getProperty(operating, "lastMojiraJEcontent");
		String[] read = read(new File("mirrorer/meta/", "latest-version_manifest.txt"));
		if (read.length > 0) {
			CheckNewestThread.lastManifest = read[0];
		}
		while (yes) {
			Thread check = new CheckNewestThread();
			Thread checkMojira = new CheckMojiraThread();
			check.start();
			checkMojira.start();

			try {
				Thread.sleep(10000L);
				while (check.isAlive() || checkMojira.isAlive()); 
			} 
			catch (Throwable t) {
				log("what the FUCK?!?!??!");
				t.printStackTrace();
			} 
		} 
	}

	public static void log(String message) {
		System.out.println(String.valueOf((new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss] ")).format(Long.valueOf(System.currentTimeMillis()))) + message);
		logToFile(defaultLogFile, message);
	}

	public static void logmojira(String message) {
		System.out.println(String.valueOf((new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss] ")).format(Long.valueOf(System.currentTimeMillis()))) + message);
		logToFile(mojiralogfile, message);
	}

	public static void logversions(String message) {
		System.out.println(String.valueOf((new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss] ")).format(Long.valueOf(System.currentTimeMillis()))) + message);
		logToFile(manifestlogfile, message);
	}

	public static void logerrors(String message) {
		System.out.println(String.valueOf((new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss] ")).format(Long.valueOf(System.currentTimeMillis()))) + message);
		logToFile(errorlogfile, message);
	}

	public static void logToFile(String file, String message) {
		try {
			BufferedWriter logWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8"));
			logWriter.append(String.valueOf((new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss] ")).format(Long.valueOf(System.currentTimeMillis()))) + message);
			logWriter.newLine();
			logWriter.close();
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		} 
	}
	public static boolean download(String link, File toFile) {
		try {
			toFile.createNewFile();

			URL url = new URL(link);
			BufferedInputStream inputst = new BufferedInputStream(url.openStream());
			FileOutputStream outputst = new FileOutputStream(toFile);
			byte[] buffer = new byte[1024];
			int count = 0;
			while ((count = inputst.read(buffer, 0, 1024)) != -1) {
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

			if (lines[i] != null)
			{

				if (lines[i].startsWith(String.valueOf(property) + ":")) {
					value = lines[i].substring(property.length() + 1, lines[i].length());
					break;
				}  } 
		} 
		return value;
	}


	public static void setProperty(File file, String property, String value) {
		String[] lines = read(file);
		String[] newlines = new String[lines.length + 1];


		boolean found = false;
		for (int i = 0; i < lines.length; i++) {
			if (lines[i] != null)
				if (lines[i].startsWith(String.valueOf(property) + ":")) {

					newlines[i] = String.valueOf(property) + ":" + value;
					found = true;
				}
				else {

					newlines[i] = lines[i];
				}  
		} 
		if (!found) {

			write(file, new String[] { String.valueOf(property) + ":" + value }, true);

			return;
		} 

		write(file, newlines, false);
	}

	public static String[] read(File file) {
		if (!file.exists()) return new String[0]; 
		InputStreamReader reader = null;

		try {
			reader = new InputStreamReader(new FileInputStream(file), "UTF-8");
			StringBuilder inputB = new StringBuilder();
			char[] buffer = new char[1024];
			while (true) {
				int readcount = reader.read(buffer);
				if (readcount < 0)
					break;  inputB.append(buffer, 0, readcount);
			} 
			return inputB.toString().split("\n");
		} catch (Throwable t) {
			log("A critical error occurred while reading from file: " + file);
			t.printStackTrace();
		} finally {

			try { reader.close(); } catch (Exception exception) {}
		} 
		return null;
	}


	public static void write(File file, String[] lines, boolean append) {
		try {
			file.createNewFile();
		} catch (Throwable t) {
			System.out.println(file.toPath().toString());
			t.printStackTrace();
		} 
		OutputStreamWriter writer = null;

		try {
			writer = new OutputStreamWriter(new FileOutputStream(file, append), "UTF-8");
			for (int i = 0; i < lines.length; i++) {

				if (lines[i] != null) {
					writer.write(String.valueOf(lines[i]) + "\n");
				}
			} 
		} catch (Throwable t) {
			log("A critical error occurred while attempting to write to file: " + file);
			t.printStackTrace();
		} finally {

			try { writer.close(); } catch (Exception exception) {}
		} 
	}
}