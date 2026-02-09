package com.book.ensureu.model;

import java.io.Serializable;

public class Paper<S extends Sections<SubSections<Question<Problem>>>> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1420551090235519366L;
	private Pattern<S> pattern;

	public Pattern<S> getPattern() {
		return pattern;
	}
	public void setPattern(Pattern<S> pattern) {
		this.pattern = pattern;
	}
	@Override
	public String toString() {
		return "Paper [ pattern=" + pattern + "]";
	}

	
	
	
}
