public class Features {

	/**
	 * Bereken de features van een log-bestand met de default settings
	 */
	public static String features(String path) {
		MotionFingerprint.command("--Features "+path);
		return "Features van " + path + " werden berekend.";
	}
	
	/**
	 * Bereken de features van alle log-bestanden in een map met de default settings
	 */
	public static String featuresall(String path) {
		return "";
	}
	
}
