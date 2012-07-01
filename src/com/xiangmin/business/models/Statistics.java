package com.xiangmin.business.models;

public class Statistics {
	public int id;
	public String data;//月/日期
	public String warrantyPeriodCount;//保外工单总数
	public String warrantyExpiredCount;//保内工单总数
	public String unfinishedCount;//未完成工单总数
	public String finishedCount;//完成工单总数
	public String warrantyPeriodClearingMoney ;//保内结算总金额
	public String warrantyExpiredClearingMoney ;//保外结算总金额
	@Override
	public String toString() {
		return "Statistics [data=" + data + ", finishedCount=" + finishedCount
				+ ", id=" + id + ", unfinishedCount=" + unfinishedCount
				+ ", warrantyExpiredClearingMoney="
				+ warrantyExpiredClearingMoney + ", warrantyExpiredCount="
				+ warrantyExpiredCount + ", warrantyPeriodClearingMoney="
				+ warrantyPeriodClearingMoney + ", warrantyPeriodCount="
				+ warrantyPeriodCount + "]";
	}
}
