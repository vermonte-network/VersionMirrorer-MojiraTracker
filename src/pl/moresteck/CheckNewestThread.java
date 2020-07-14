package pl.moresteck;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class CheckNewestThread extends Thread {

	@Override
	public void run() {
		try {
			String manifest = scanJsun("http://launchermeta.mojang.com/mc/game/version_manifest.json");
			String manifestIDSpending = manifest;
			File mirrorer = new File("mirrorer/");
			mirrorer.mkdirs();
			File folder = new File(mirrorer, "meta/");
			folder.mkdirs();
			File ver_manifest = new File(folder, new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss-").format(Long.valueOf(System.currentTimeMillis())) + "version_manifest.txt");
			VersionMirrorer.download("http://launchermeta.mojang.com/mc/game/version_manifest.json", ver_manifest);
			while (manifestIDSpending.indexOf("\"id\":") > -1) {
				manifestIDSpending = manifestIDSpending.substring(manifestIDSpending.indexOf("\"id\": \"") + 7);
				String version = manifestIDSpending.substring(0, manifestIDSpending.indexOf("\""));

				String fromjsonlink = manifestIDSpending.substring(manifestIDSpending.indexOf(", \"url\": \"") + 10);
				String jsonlink = fromjsonlink.substring(0, fromjsonlink.indexOf("\","));
				String jsun = scanJsun(jsonlink);
				
				String jsunfromclient = jsun.substring(jsun.indexOf("\"client\": {") + 11);
				String fromclientlink = jsunfromclient.substring(jsunfromclient.indexOf("\"url\": \"") + 8);
				String clientlink = fromclientlink.substring(0, fromclientlink.indexOf("\""));

				String jsonhashpath = jsonlink.substring(44, 84);
				File jsonfolder = new File(mirrorer, version + "/");
				//VersionMirrorer.log(jsonfolder.toPath().toString());
				jsonfolder.mkdirs();

				File clientToFile = new File(jsonfolder, clientlink.substring(39, 79) + "-client.jar");
				File jsunToFile = new File(jsonfolder, jsonhashpath + ".json");

				if (!clientToFile.exists()) {
					VersionMirrorer.log("Downloading client for version " + version + " - " + clientToFile.toPath().toString());
					VersionMirrorer.download(clientlink, clientToFile);
				}

				if (!jsunToFile.exists()) {
					VersionMirrorer.log("Downloading json for version " + version + " - " + jsunToFile.toPath().toString());
					VersionMirrorer.download(jsonlink, jsunToFile);
				}

				String serverlink = null;
				if (jsun.indexOf("\"server\": {") > -1) {
					String jsunfromserver = jsun.substring(jsun.indexOf("\"server\": {") + 11);
					String fromserverlink = jsunfromserver.substring(jsunfromserver.indexOf("\"url\": \"") + 8);
					serverlink = fromserverlink.substring(0, fromserverlink.indexOf("\""));
				}

				String serverwindowslink = null;
				if (jsun.indexOf("\"windows_server\": {") > -1) {
					String jsunfromserver = jsun.substring(jsun.indexOf("\"windows_server\": {") + 19);
					String fromserverlink = jsunfromserver.substring(jsunfromserver.indexOf("\"url\": \"") + 8);
					serverwindowslink = fromserverlink.substring(0, fromserverlink.indexOf("\""));
				}

				String clientmaplink = null;
				if (jsun.indexOf("\"client_mappings\": {") > -1) {
					String jsunfromclientmap = jsun.substring(jsun.indexOf("\"client_mappings\": {") + 20);
					String fromclientmaplink = jsunfromclientmap.substring(jsunfromclientmap.indexOf("\"url\": \"") + 8);
					clientmaplink = fromclientmaplink.substring(0, fromclientmaplink.indexOf("\""));
				}

				String servermaplink = null;
				if (jsun.indexOf("\"server_mappings\": {") > -1) {
					String jsunfromservermap = jsun.substring(jsun.indexOf("\"server_mappings\": {") + 20);
					String fromservermaplink = jsunfromservermap.substring(jsunfromservermap.indexOf("\"url\": \"") + 8);
					servermaplink = fromservermaplink.substring(0, fromservermaplink.indexOf("\""));
				}

				if (clientmaplink != null) {
					File clientmapToFile = new File(jsonfolder, clientmaplink.substring(39, 79) + "-client.txt");
					if (!clientmapToFile.exists()) {
						VersionMirrorer.log("Downloading client mappings for version " + version + " - " + clientmapToFile.toPath().toString());
						VersionMirrorer.download(clientmaplink, clientmapToFile);
					}
				}
				
				if (serverlink != null) {
					File serverToFile = new File(jsonfolder, serverlink.substring(39, 79) + "-server.jar");
					if (!serverToFile.exists()) {
						VersionMirrorer.log("Downloading server for version " + version + " - " + serverToFile.toPath().toString());
						VersionMirrorer.download(serverlink, serverToFile);
					}
				}

				if (serverwindowslink != null) {
					File serverWindowsToFile = new File(jsonfolder, serverwindowslink.substring(39, 79) + "-windows_server.exe");
					if (!serverWindowsToFile.exists()) {
						VersionMirrorer.log("Downloading windows server for version " + version + " - " + serverWindowsToFile.toPath().toString());
						VersionMirrorer.download(serverwindowslink, serverWindowsToFile);
					}
				}
				
				if (servermaplink != null) {
					File servermapToFile = new File(jsonfolder, servermaplink.substring(39, 79) + "-server.txt");
					if (!servermapToFile.exists()) {
						VersionMirrorer.log("Downloading server mappings for version " + version + " - " + servermapToFile.toPath().toString());
						VersionMirrorer.download(servermaplink, servermapToFile);
					}
				}
			}
			VersionMirrorer.log("*cricket noise*");
		} catch (Throwable t) {
			VersionMirrorer.log("DAAAH!!!!! SOMETHING WENT WRONG!");
			t.printStackTrace();
		}
	}

	public static String scanJsun(String link) {
		try {
			URL url = new URL(link);
			StringBuilder bobbudowniczy = new StringBuilder();
			Scanner s = new Scanner(url.openStream(), "UTF-8");
			while (s.hasNextLine()) {
				bobbudowniczy.append(s.nextLine());
			}
			s.close();
			return bobbudowniczy.toString();
		} catch (Throwable t) {
			VersionMirrorer.log("WTF??!?!? MOJANG CHANGED JSON STRUCTURE???!?!");
			t.printStackTrace();
			return "";
		}
	}
}
