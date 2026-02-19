package com.book.ensureu.model;

import java.util.List;

public class Problem {

	// Correct option(s) - can be letter like "A", "B", etc.
	private List<String> co;
	// Selected option(s)
	private List<String> so;

	private List<Solution> solutions;

	// Question text (legacy field)
	private String value;

	// Question text (new field from frontend)
	private String question;

	// Solution explanation
	private String solution;

	private List<Options> options;

	private String image;

	public List<String> getCo() {
		return co;
	}

	public void setCo(List<String> co) {
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

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getSolution() {
		return solution;
	}

	public void setSolution(String solution) {
		this.solution = solution;
	}

	public List<Options> getOptions() {
		return options;
	}

	public void setOptions(List<Options> options) {
		this.options = options;
	}


	public List<String> getSo() {
		return so;
	}

	public void setSo(List<String> so) {
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
		return "Problem [so=" + so + ", co=" + co + ", solutions=" + solutions + ", value=" + value
				+ ", question=" + question + ", solution=" + solution + ", options=" + options + "]";
	}

}
