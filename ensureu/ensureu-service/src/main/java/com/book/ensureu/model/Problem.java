package com.book.ensureu.model;

import java.util.List;

public class Problem {

	private int co;
	private int so;

	private List<Solution> solutions;

	private String value;

	private List<Options> options;
	
	private String image;

	public int getCo() {
		return co;
	}

	public void setCo(int co) {
		this.co = co;
	}

	public List<Solution> getSolutions() {
		return solutions;
	}

	public void setSolutions(List<Solution> solutions) {
		this.solutions = solutions;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<Options> getOptions() {
		return options;
	}

	public void setOptions(List<Options> options) {
		this.options = options;
	}

	
	public int getSo() {
		return so;
	}

	public void setSo(int so) {
		this.so = so;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	@Override
	public String toString() {
		return "Problem [so="+so+ ",co=" + co + ", solutions=" + solutions + ", value=" + value + ", options="
				+ options + "]";
	}

}
