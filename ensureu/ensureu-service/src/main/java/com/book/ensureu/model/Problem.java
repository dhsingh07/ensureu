package com.book.ensureu.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonSetter;

@SuppressWarnings("unchecked")
public class Problem {

	// Correct option(s) - can be letter like "A", "B", etc. or index numbers
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
		// Normalize any Integer values to String for JSON serialization
		if (co != null) {
			for (int i = 0; i < co.size(); i++) {
				Object val = co.get(i);
				if (val != null && !(val instanceof String)) {
					((List<Object>)(List<?>)co).set(i, val.toString());
				}
			}
		}
		return co;
	}

	public void setCo(List<String> co) {
		this.co = co;
	}

	// Handle mixed types from MongoDB (integers or strings)
	@JsonSetter("co")
	public void setCoFromJson(Object coValue) {
		if (coValue == null) {
			this.co = new ArrayList<>();
		} else if (coValue instanceof List) {
			List<?> list = (List<?>) coValue;
			this.co = list.stream()
					.map(Object::toString)
					.collect(Collectors.toList());
		} else {
			this.co = new ArrayList<>();
			this.co.add(coValue.toString());
		}
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
		// Normalize any Integer values to String for JSON serialization
		if (so != null) {
			for (int i = 0; i < so.size(); i++) {
				Object val = so.get(i);
				if (val != null && !(val instanceof String)) {
					((List<Object>)(List<?>)so).set(i, val.toString());
				}
			}
		}
		return so;
	}

	public void setSo(List<String> so) {
		this.so = so;
	}

	// Handle mixed types from MongoDB (integers or strings)
	@JsonSetter("so")
	public void setSoFromJson(Object soValue) {
		if (soValue == null) {
			this.so = new ArrayList<>();
		} else if (soValue instanceof List) {
			List<?> list = (List<?>) soValue;
			this.so = list.stream()
					.map(Object::toString)
					.collect(Collectors.toList());
		} else {
			this.so = new ArrayList<>();
			this.so.add(soValue.toString());
		}
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
