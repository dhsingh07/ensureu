package com.book.ensureu.dto;

import java.io.Serializable;
import java.util.List;

public class NotificationRuleConfigDTO implements Serializable {

	private static final long serialVersionUID = 5651043183087654375L;

	private String notificationType;
	private List<String> notificationDay;
	private List<String> notificationWeek;
	private Long startDate;
	private Long EndDate;
	private boolean repeat;
	private boolean active;

	public String getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}

	public List<String> getNotificationDay() {
		return notificationDay;
	}

	public void setNotificationDay(List<String> notificationDay) {
		this.notificationDay = notificationDay;
	}

	public List<String> getNotificationWeek() {
		return notificationWeek;
	}

	public void setNotificationWeek(List<String> notificationWeek) {
		this.notificationWeek = notificationWeek;
	}

	public Long getStartDate() {
		return startDate;
	}

	public void setStartDate(Long startDate) {
		this.startDate = startDate;
	}

	public Long getEndDate() {
		return EndDate;
	}

	public void setEndDate(Long endDate) {
		EndDate = endDate;
	}

	public boolean isRepeat() {
		return repeat;
	}

	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		return "NotificationRuleConfigDTO [notificationType=" + notificationType + ", notificationDay="
				+ notificationDay + ", notificationWeek=" + notificationWeek + ", startDate=" + startDate + ", EndDate="
				+ EndDate + ", repeat=" + repeat + ", active=" + active + "]";
	}

}