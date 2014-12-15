public class MotionFingerprint {
	
	public static void command(String command) {
		try {
			Process p = Runtime.getRuntime().exec("java -jar MotionFingerprint.jar "+command);
			p.waitFor();
		} catch (Exception e) {
			System.out.println("Probleem met commando voor MotionFingerprint");
		}
	}

}
