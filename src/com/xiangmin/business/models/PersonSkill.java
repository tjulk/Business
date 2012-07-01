package com.xiangmin.business.models;

public class PersonSkill {
	
	public int id;
	public String name;
	public String peixunshuliang;//培训数量
	public String jishunengli;//技术能力
	public String fuwunengli;//服务能力
	public String gerenzili;//个人资历
	public String gongdantongji;//工单统计
	@Override
	public String toString() {
		return "PersonSkill [fuwunengli=" + fuwunengli + ", gerenzili="
				+ gerenzili + ", gongdantongji=" + gongdantongji + ", id=" + id
				+ ", jishunengli=" + jishunengli + ", name=" + name
				+ ", peixunshuliang=" + peixunshuliang + "]";
	}
}
