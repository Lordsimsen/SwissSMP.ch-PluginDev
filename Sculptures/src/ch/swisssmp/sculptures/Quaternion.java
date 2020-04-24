package ch.swisssmp.sculptures;

import ch.swisssmp.utils.Mathf;

/**
 * Quaternions are data structures built from unicorn horns.
 * 
 * I nabbed this implementation from The Internet.
 */
public final class Quaternion {
	private double x;
	private double y;
	private double z;
	private double w;
	//private float[] matrixs;

	public Quaternion() {
		this(0,0,0,0);
	}
	
	public Quaternion(final Quaternion q) {
		this(q.x, q.y, q.z, q.w);
	}

	public Quaternion(double x, double y, double z, double w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public void set(final Quaternion q) {
		//matrixs = null;
		this.x = q.x;
		this.y = q.y;
		this.z = q.z;
		this.w = q.w;
	}

	public Quaternion(Vector3 axis, double angle) {
		set(axis, angle);
	}

	public double norm() {
		return Math.sqrt(dot(this));
	}

	public double getW() {
		return w;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	/**
	 * @param axis
	 *            rotation axis, unit vector
	 * @param angle
	 *            the rotation angle
	 * @return this
	 */
	public Quaternion set(Vector3 axis, double angle) {
		//matrixs = null;
		double s = (double) Math.sin(angle / 2);
		w = (double) Math.cos(angle / 2);
		x = axis.getX() * s;
		y = axis.getY() * s;
		z = axis.getZ() * s;
		return this;
	}

	public Quaternion mulThis(Quaternion q) {
		//matrixs = null;
		double nw = w * q.w - x * q.x - y * q.y - z * q.z;
		double nx = w * q.x + x * q.w + y * q.z - z * q.y;
		double ny = w * q.y + y * q.w + z * q.x - x * q.z;
		z = w * q.z + z * q.w + x * q.y - y * q.x;
		w = nw;
		x = nx;
		y = ny;
		return this;
	}

	public Quaternion scaleThis(double scale) {
		if (scale != 1) {
			//matrixs = null;
			w *= scale;
			x *= scale;
			y *= scale;
			z *= scale;
		}
		return this;
	}

	public Quaternion divThis(double scale) {
		if (scale != 1) {
			//matrixs = null;
			w /= scale;
			x /= scale;
			y /= scale;
			z /= scale;
		}
		return this;
	}

	public double dot(Quaternion q) {
		return x * q.x + y * q.y + z * q.z + w * q.w;
	}

	public boolean equals(Quaternion q) {
		return x == q.x && y == q.y && z == q.z && w == q.w;
	}

	public Quaternion interpolateThis(Quaternion q, double t) {
		if (!equals(q)) {
			double d = dot(q);
			double qx, qy, qz, qw;

			if (d < 0f) {
				qx = -q.x;
				qy = -q.y;
				qz = -q.z;
				qw = -q.w;
				d = -d;
			} else {
				qx = q.x;
				qy = q.y;
				qz = q.z;
				qw = q.w;
			}

			double f0, f1;

			if ((1 - d) > 0.1f) {
				double angle = (double) Math.acos(d);
				double s = (double) Math.sin(angle);
				double tAngle = t * angle;
				f0 = (double) Math.sin(angle - tAngle) / s;
				f1 = (double) Math.sin(tAngle) / s;
			} else {
				f0 = 1 - t;
				f1 = t;
			}

			x = f0 * x + f1 * qx;
			y = f0 * y + f1 * qy;
			z = f0 * z + f1 * qz;
			w = f0 * w + f1 * qw;
		}

		return this;
	}

	public Quaternion normalizeThis() {
		return divThis(norm());
	}

	public Quaternion interpolate(Quaternion q, double t) {
		return new Quaternion(this).interpolateThis(q, t);
	}

	/**
	 * Converts this Quaternion into a matrix, returning it as a float array.
	 */
	public float[] toMatrix() {
		float[] matrixs = new float[16];
		toMatrix(matrixs);
		return matrixs;
	}

	/**
	 * Converts this Quaternion into a matrix, placing the values into the given array.
	 * @param matrixs 16-length float array.
	 */
	public final void toMatrix(float[] matrixs) {
		matrixs[3] = 0.0f;
		matrixs[7] = 0.0f;
		matrixs[11] = 0.0f;
		matrixs[12] = 0.0f;
		matrixs[13] = 0.0f;
		matrixs[14] = 0.0f;
		matrixs[15] = 1.0f;

		matrixs[0] = (float) (1.0f - (2.0f * ((y * y) + (z * z))));
		matrixs[1] = (float) (2.0f * ((x * y) - (z * w)));
		matrixs[2] = (float) (2.0f * ((x * z) + (y * w)));
		
		matrixs[4] = (float) (2.0f * ((x * y) + (z * w)));
		matrixs[5] = (float) (1.0f - (2.0f * ((x * x) + (z * z))));
		matrixs[6] = (float) (2.0f * ((y * z) - (x * w)));
		
		matrixs[8] = (float) (2.0f * ((x * z) - (y * w)));
		matrixs[9] = (float) (2.0f * ((y * z) + (x * w)));
		matrixs[10] = (float) (1.0f - (2.0f * ((x * x) + (y * y))));
	}
	
	public final Vector3 multiply(Vector3 v) {
		  double  k0 = w*w - 0.5;
		  double  k1;
		  double  rx,ry,rz;
		  
		  double vx = v.getX();
		  double vy = v.getY();
		  double vz = v.getZ();

		  // k1 = Q.V
		  k1    = vx*x;
		  k1   += vy*y;
		  k1   += vz*z;

		  // (qq-1/2)V+(Q.V)Q
		  rx  = vx*k0 + x*k1;
		  ry  = vy*k0 + y*k1;
		  rz  = vz*k0 + z*k1;

		  // (Q.V)Q+(qq-1/2)V+q(QxV)
		  rx += w*(y*vz-z*vy);
		  ry += w*(z*vx-x*vz);
		  rz += w*(x*vy-y*vx);

		  //  2((Q.V)Q+(qq-1/2)V+q(QxV))
		  rx += rx;
		  ry += ry;
		  rz += rz;

		  return new Vector3(rx,ry,rz);
	}
	
	public final Vector3 toEulerAngles() {
		double eulerX; // pitch
		double eulerY; // yaw
		double eulerZ; // roll

	    // roll (z-axis rotation)
	    double sinr_cosp = 2 * (w * x + y * z);
	    double cosr_cosp = 1 - 2 * (x * x + y * y);
	    eulerZ = Math.atan2(sinr_cosp, cosr_cosp);

	    // pitch (x-axis rotation)
	    double sinp = 2 * (w * y - z * x);
	    if (Math.abs(sinp) >= 1)
	    	eulerX = Math.copySign(Math.PI / 2, sinp); // use 90 degrees if out of range
	    else
	    	eulerX = Math.asin(sinp);

	    // yaw (y-axis rotation)
	    double siny_cosp = 2 * (w * z + x * y);
	    double cosy_cosp = 1 - 2 * (y * y + z * z);
	    eulerY = Math.atan2(siny_cosp, cosy_cosp);

	    return new Vector3(eulerX,eulerY,eulerZ);
	}
	
	public final Quaternion clone() {
		return new Quaternion(this);
	}

	/**
	 * Returns a rotation that rotates z degrees around the z axis, x degrees around the x axis, and y degrees around the y axis (in that order).
	 */
	public static Quaternion euler(double x, double y, double z) {
		return new Quaternion().set(Vector3.forward, z).set(Vector3.right, x).set(Vector3.up, y);
	}

	/**
	 * Returns a rotation that rotates z degrees around the z axis, x degrees around the x axis, and y degrees around the y axis (in that order).
	 */
	public static Quaternion euler(Vector3 v) {
		return euler(v.getX(),v.getY(),v.getZ());
	}
	
	/**
	 * Interpolates between a and b by t and normalizes the result afterwards. The parameter t is clamped to the range [0, 1].
	 */
	public static Quaternion lerp(Quaternion a, Quaternion b, float t) {
		return a.interpolate(b, Mathf.clamp01(t));
	}
}