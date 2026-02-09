package com.book.ensureu.model;

public class Options {
	private String value;

    private String prompt;
    
    private String image;

    public String getValue ()
    {
        return value;
    }

    public void setValue (String value)
    {
        this.value = value;
    }

    public String getPrompt ()
    {
        return prompt;
    }

    public void setPrompt (String prompt)
    {
        this.prompt = prompt;
    }

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	@Override
	public String toString() {
		return "Options [value=" + value + ", prompt=" + prompt + ", image=" + image + "]";
	}

}
