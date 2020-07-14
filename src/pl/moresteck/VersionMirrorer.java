package pl.moresteck;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.text.SimpleDateFormat;

public class VersionMirrorer {
	public static String defaultLogFile = "log.txt";
	public static boolean yes = true;

	public static void main(String[] args) {
		log("VersionMirrorer v1.0.6 by Moresteck & DireMaster (2020-06-23) fired up!");
		while (yes) {
			Thread check = new CheckNewestThread();
			check.start();

			try {
				Thread.sleep(10000);
				while (check.isAlive());
				check = null;
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
}
