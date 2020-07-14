package pl.moresteck;

public class CheckMojiraThread extends Thread {
	public static String lastMojiraJEcontent = "";
	public static long scanTimes = 0;

	public void run() {
		// we gotta make this get the latest vershun everytime
		// so, we have to search for occurences of `"self":`
		// in a loop, until there is no more `"self":` left
		// then we compare if theres anything different
		// and if there is, we print it to logs
		String mojiraindex = CheckNewestThread.scanJsun("https://bugs.mojang.com/rest/api/2/project/MC/versions");
		String lastSelf = mojiraindex;
		int index = 0;
		while ((index = lastSelf.indexOf("\"self\":")) > -1) {
			lastSelf = lastSelf.substring(index + 7 + 1, lastSelf.length());
		}
		String self = lastSelf.substring(0, lastSelf.indexOf("\","));
		String id = lastSelf.substring(lastSelf.indexOf("\"id\":") + 5 + 1, lastSelf.length());
		id = id.substring(0, id.indexOf("\","));
		String name = lastSelf.substring(lastSelf.indexOf("\"name\":") + 7 + 1, lastSelf.length());
		name = name.substring(0, name.indexOf("\","));
		String archived = lastSelf.substring(lastSelf.indexOf("\"archived\":") + 11, lastSelf.length());
		archived = archived.substring(0, archived.indexOf(","));
		String released = lastSelf.substring(lastSelf.indexOf("\"released\":") + 11, lastSelf.length());
		released = released.substring(0, released.indexOf(","));
		String releasedate = "n/a";
		String userreleasedate = "n/a";
		if (lastSelf.contains("releaseDate")) {
			releasedate = lastSelf.substring(lastSelf.indexOf("\"releaseDate\":") + 14 + 1, lastSelf.length());
			releasedate = releasedate.substring(0, releasedate.indexOf("\","));
		}
		if (lastSelf.contains("userReleaseDate")) {
			userreleasedate = lastSelf.substring(lastSelf.indexOf("\"userReleaseDate\":") + 18 + 1, lastSelf.length());
			userreleasedate = userreleasedate.substring(0, userreleasedate.indexOf("\","));
		}

		String lastid = "";
		if (!lastMojiraJEcontent.equals("")) {
			lastid = lastMojiraJEcontent.substring(lastMojiraJEcontent.indexOf("\"id\":") + 5 + 1, lastMojiraJEcontent.length());
			lastid = lastid.substring(0, lastid.indexOf("\","));
		}

		if (!lastSelf.equals(lastMojiraJEcontent)) {
			if (lastid.equals(id)) {
				VersionMirrorer.log("A change in Mojira has been spotted: " + id);
			} else {
				VersionMirrorer.log("A NEW entry in Mojira has been spotted: " + id);
			}
			VersionMirrorer.log("Self: " + self);
			VersionMirrorer.log("ID: " + id);
			VersionMirrorer.log("Name: " + name);
			VersionMirrorer.log("Archived: " + archived);
			VersionMirrorer.log("Released: " + released);
			VersionMirrorer.log("Release date: " + releasedate);
			VersionMirrorer.log("User release date: " + userreleasedate);
		} else {
			VersionMirrorer.log("*silent mojira*");
		}
		lastMojiraJEcontent = lastSelf;
		scanTimes++;
		if (scanTimes % 10 == 0) {
			VersionMirrorer.setProperty(VersionMirrorer.operating, "lastMojiraJEcontent", lastMojiraJEcontent);
		}
	}
}
