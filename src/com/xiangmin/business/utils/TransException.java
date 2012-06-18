package com.xiangmin.business.utils;

import java.lang.Exception;

public class TransException extends Exception {

	private static final long serialVersionUID = 427231423082987130L;

	public TransException() {
		super();
	}

	public TransException(String s) {
		super(s);
	}
}