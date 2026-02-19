package com.book.ensureu.model;

public class Options {
	// Legacy field for option text
	private String value;

	// Option text (new field from frontend)
	private String text;

	// Option label (A, B, C, D)
	private String prompt;

	private String image;

	// Whether this option is selected by user
	private boolean selected;

	// Whether this is the correct option
	private boolean correct;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getPrompt() {
		return prompt;
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isCorrect() {
		return correct;
	}

	public void setCorrect(boolean correct) {
		this.correct = correct;
	}

	@Override
	public String toString() {
		return "Options [value=" + value + ", text=" + text + ", prompt=" + prompt
				+ ", image=" + image + ", selected=" + selected + ", correct=" + correct + "]";
	}

}
