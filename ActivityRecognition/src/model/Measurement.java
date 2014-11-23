package model;

public class Measurement {
	
	public Measurement(long timestamp, double x, double y, double z) {
		this.timestamp = timestamp;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public long timestamp;
	public double r, x, y, z;
	public double q1,qy2,q3,q4;
	public double p;
	public double a;
	
}
