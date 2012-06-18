package com.xiangmin.business.voice;

import android.os.Environment;

import java.io.File;
import java.io.FilenameFilter;

public class Dirs {

	// This filter excludes files whose name starts with a dot, e.g. ".nomedia" (which
	// is created by Diktofon), ".DS_Store" which is created by Mac OS X, etc.
	// In other words: no recording can have a name that starts with a dot.
	public static final FilenameFilter FILENAME_FILTER = new FilenameFilter() {
		public boolean accept(File dir, String name) {
			return ! name.startsWith(".");
		}
	};


	private static final String RECORDINGS = "/recordings/";

	// This directory is used for the files that have been recorded
	// using the RecorderActivity but that are not part of the Diktofon
	// collection.

	private static File sBaseDir;
	private static File sRecordingsDir;
	private static File sNomediaFile;


	public static void setBaseDir(String packageName) {
		String baseDirAsString = Environment.getExternalStorageDirectory().getAbsolutePath() + "/详民家电服务移动平台/";
		sBaseDir = new File(baseDirAsString);
		sRecordingsDir = new File(baseDirAsString + RECORDINGS);
		sNomediaFile = new File(baseDirAsString + RECORDINGS + ".nomedia");
	}

	public static File getBaseDir() {
		return sBaseDir;
	}

	public static File getRecordingsDir() {
		return sRecordingsDir;
	}

	public static File getNomediaFile() {
		return sNomediaFile;
	}

	public static File getRecorderDir() {
		return new File(sBaseDir.getAbsolutePath());
	}
}