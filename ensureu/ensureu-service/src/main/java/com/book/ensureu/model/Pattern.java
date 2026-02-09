package com.book.ensureu.model;

import java.io.Serializable;
import java.util.List;

import com.book.ensureu.constant.PaperType;

public class Pattern<S> implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1937435384016942043L;

	private String id;
	
	private String createdOn;

	private long time;
	
    private String title;
	
	private PaperType paperType;

	private List<S> sections;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public List<S> getSections() {
		return sections;
	}

	public void setSections(List<S> sections) {
		this.sections = sections;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public PaperType getPaperType() {
		return paperType;
	}

	public void setPaperType(PaperType paperType) {
		this.paperType = paperType;
	}

	@Override
	public String toString() {
		return "Pattern [id=" + id + ", createdOn=" + createdOn + ", time=" + time + ", sections=" + sections
				+ ", title=" + title + ", paperType=" + paperType + "]";
	}

	

}