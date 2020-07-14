package pl.moresteck;

public class CheckMojiraThread extends Thread {
	public static String lastMojiraJEcontent = "";
	public static long scanTimes = 0L;

	public void run() {
		String mojiraindex = CheckNewestThread.scanJsun("https://bugs.mojang.com/rest/api/2/project/MC/versions");
		String lastSelf = mojiraindex;
		int index = 0;
		while ((index = lastSelf.indexOf("\"self\":")) > -1) {
			lastSelf = lastSelf.substring(index + 7 + 1, lastSelf.length());
		}
		String self = lastSelf.substring(0, lastSelf.indexOf("\","));
		String id = lastSelf.substring(lastSelf.indexOf("\"id\":") + 5 + 1, lastSelf.length());
		id = id.substring(0, id.indexOf("\","));
		String name = lastSelf.substring(lastSelf.indexOf("\"name\":") + 7 + 1 , lastSelf.length());
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
				VersionMirrorer.logmojira("A change in Mojira has been spotted: " + id);
				if (released.equals("true")) {
					VersionMirrorer.log("Oh Boy, a new version has been released");
					VersionMirrorer.logmojira("Oh Boy, a new version has been released");
				}
				if (archived.equals("true")) {
					VersionMirrorer.log("Another version for archiving");
					VersionMirrorer.logmojira("Another version for archiving");
				}
			}
			else {
				VersionMirrorer.log("A NEW entry in Mojira has been spotted: " + id);
				VersionMirrorer.logmojira("A NEW entry in Mojira has been spotted: " + id);
			}
			// Logs output to main log
			VersionMirrorer.log("Self: " + self);
			VersionMirrorer.log("ID: " + id);
			VersionMirrorer.log("Name: " + name);
			VersionMirrorer.log("Archived: " + archived);
			VersionMirrorer.log("Released: " + released);
			VersionMirrorer.log("Release date: " + releasedate);
			VersionMirrorer.log("User release date: " + userreleasedate);

			//logs output to mojira log
			VersionMirrorer.logmojira("Self: " + self);
			VersionMirrorer.logmojira("ID: " + id);
			VersionMirrorer.logmojira("Name: " + name);
			VersionMirrorer.logmojira("Archived: " + archived);
			VersionMirrorer.logmojira("Released: " + released);
			VersionMirrorer.logmojira("Release date: " + releasedate);
			VersionMirrorer.logmojira("User release date: " + userreleasedate);
		} else {
			VersionMirrorer.log("*silent mojira*");
		} 
		lastMojiraJEcontent = lastSelf;
		scanTimes++;
		if (scanTimes % 10L == 0L)
			VersionMirrorer.setProperty(VersionMirrorer.operating, "lastMojiraJEcontent", lastMojiraJEcontent); 
	}
}