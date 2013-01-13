package org.net9.simplex.ppmc.util;

public class Random extends java.util.Random {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static long RANDOM_SEED = 0;
	public static boolean ESCAPE_RANDOM_SEED = true;
	private long _mySeed;
	public Random() {
		this(RANDOM_SEED, true);
	}
	public Random(long seed) {
		this(RANDOM_SEED,ESCAPE_RANDOM_SEED);
	}
	public Random(long seed, boolean escape) {
		super(seed);
		if (escape && RANDOM_SEED == seed) {
			_mySeed = System.nanoTime()*System.currentTimeMillis();
			super.setSeed(_mySeed);
		} else {
			_mySeed = seed;
		}
	}
	public long getOriginalSeed() {
		return _mySeed;
	}
}
