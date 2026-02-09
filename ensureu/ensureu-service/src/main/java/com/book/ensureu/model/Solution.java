package com.book.ensureu.model;

public class Solution {
	
	private Integer id;

    private String addedon;

    private String updatedon;

    private String value;

    private String type;
    
    private String image;

    public Integer getId ()
    {
        return id;
    }

    public void setId (Integer id)
    {
        this.id = id;
    }

    public String getAddedon ()
    {
        return addedon;
    }

    public void setAddedon (String addedon)
    {
        this.addedon = addedon;
    }

    public String getUpdatedon ()
    {
        return updatedon;
    }

    public void setUpdatedon (String updatedon)
    {
        this.updatedon = updatedon;
    }

    public String getValue ()
    {
        return value;
    }

    public void setValue (String value)
    {
        this.value = value;
    }

    public String getType ()
    {
        return type;
    }

    public void setType (String type)
    {
        this.type = type;
    }

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	@Override
	public String toString() {
		return "Solution [id=" + id + ", addedon=" + addedon + ", updatedon=" + updatedon + ", value=" + value
				+ ", type=" + type + "]";
	}

    
}
