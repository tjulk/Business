package com.xiangmin.business.models;

import java.io.Serializable;

public class Todo implements Serializable{
	private static final long serialVersionUID = 1L;
	public int id;
	public String todoId;
	public String todoTime;
	public String clientName;
	public String address;
	public String phoneNumber;
	public String state = "";

	@Override
	public String toString() {
		return "Todo [address=" + address + ", clientName=" + clientName
				+ ", id=" + id + ", phoneNumber=" + phoneNumber + ", todoId="
				+ todoId + ", todoTime=" + todoTime + "]";
	}
}
