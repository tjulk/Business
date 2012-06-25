package com.xiangmin.business.models;

import java.io.Serializable;

public class Announce implements Serializable{

	private static final long serialVersionUID = 1L;
	public int id;
	public String announceId;
	public String announceType;
	public String announceTitle;
	public String announceDetail;
	@Override
	public String toString() {
		return "Announce [announceId=" + announceId + ", announceTitle="
				+ announceTitle + ", announceType=" + announceType + ", id="
				+ id + "]";
	}
}
