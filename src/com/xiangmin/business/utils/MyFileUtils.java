package com.xiangmin.business.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.xiangmin.business.voice.Dirs;

public class MyFileUtils {

	public static String getSizeAsString(long size) {
		String sizeAsString;
		if (size > FileUtils.ONE_MB) {
			sizeAsString = (long) (size / FileUtils.ONE_MB) + "MB";
		} else if (size > FileUtils.ONE_KB) {
			sizeAsString = (long) (size / FileUtils.ONE_KB) + "kB";
		} else {
			sizeAsString = size + "b";
		}
		if (size > NetSpeechApiUtils.MAX_AUDIO_FILE_LENGTH) {
			sizeAsString += " !!!";
		}
		return sizeAsString;
	}


	public static String getSizeAsStringExact(long size) {
		String sizeAsString;
		if (size > FileUtils.ONE_MB) {
			sizeAsString = String.format("%.1fMB", (float) size / FileUtils.ONE_MB);
		} else if (size > FileUtils.ONE_KB) {
			sizeAsString = String.format("%.1fKB", (float) size / FileUtils.ONE_KB);
		} else {
			sizeAsString = size + "B";
		}
		if (size > NetSpeechApiUtils.MAX_AUDIO_FILE_LENGTH) {
			sizeAsString += " !!!";
		}
		return sizeAsString;
	}


	public static void createNomedia() {
		File f = Dirs.getNomediaFile();
		if (! f.exists()) {
			try {
				FileUtils.touch(f);
			} catch (IOException e) { }
		}
	}


	public static void saveFile(File f, String content) throws IOException {
		File dir = f.getParentFile();
		if (!dir.exists() && !dir.mkdirs()) {
			throw new IOException("Cannot create directory: " + dir);
		}
		FileUtils.writeStringToFile(f, content, "UTF8");
	}

	public static String loadFile(File f) throws IOException {
		return FileUtils.readFileToString(f, "UTF8");
	}


	// TODO: could also use: File.renameTo(File)
	public static File moveFileToRecordingsDir(File file) throws IOException {
		String ext = getExtension(file.getName());
		String newFileName = String.valueOf(System.currentTimeMillis()) + ext;
		String newPath = Dirs.getRecordingsDir().getAbsolutePath() + "/" + newFileName;
		File newFile = new File(newPath);
		if (newFile.exists()) {
			throw new IOException("Not overwriting existing file: " + newFileName);
		}
		FileUtils.moveFile(file, newFile);
		return newFile;
	}


	// TODO: share code with moveFile...
	public static File copyFileToRecordingsDir(File file) throws IOException {
		String ext = getExtension(file.getName());
		String newFileName = String.valueOf(System.currentTimeMillis()) + ext;
		String newPath = Dirs.getRecordingsDir().getAbsolutePath() + "/" + newFileName;
		File newFile = new File(newPath);
		if (newFile.exists()) {
			throw new IOException("Not overwriting existing file: " + newFileName);
		}
		FileUtils.copyFile(file, newFile);
		return newFile;
	}


	// Doesn't seem to work... :(
	/*
	public static String guessMime(String url) {
		String mime = URLConnection.guessContentTypeFromName(url);
		if (mime == null) {
			return "audio/wav";
		}
		return mime;
	}
	 */


	/**
	 * TODO: use something more official, e.g. URLConnection.guessContentTypeFromName
	 * 
	 * Note that "audio/amr" does not work with the transcription server (2011-04-05)
	 */
	public static String guessMime(String path) {
		String ext = getExtension(path);
		if (ext.equals(".ogg")) return "audio/ogg";
		if (ext.equals(".mp2")) return "audio/mpeg";
		if (ext.equals(".mp3")) return "audio/mpeg";
		if (ext.equals(".mp4")) return "audio/mp4";
		if (ext.equals(".amr")) return "audio/AMR";
		if (ext.equals(".3gpp")) return "audio/3gpp";
		return "audio/wav";
	}


	private static String getExtension(String path) {
		int dot = path.lastIndexOf('.');
		if (dot == -1) {
			return "";
		}
		return path.substring(dot);
	}
}