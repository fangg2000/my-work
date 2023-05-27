package com.fangg.bean.chat.entity;

import java.util.Date;

public class PageRequest {

	private Integer number;
	private Date firstTime;
	private boolean blackFlag = false;

	public boolean isBlackFlag() {
		return blackFlag;
	}

	public void setBlackFlag(boolean blackFlag) {
		this.blackFlag = blackFlag;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Date getFirstTime() {
		return firstTime;
	}

	public void setFirstTime(Date firstTime) {
		this.firstTime = firstTime;
	}

}
