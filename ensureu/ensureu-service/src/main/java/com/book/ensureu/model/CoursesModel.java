package com.book.ensureu.model;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "courses")
public class CoursesModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3476294676626892216L;

	@Id
	private Long id;
	private String name;
	private String description;
	private String imageIcon;
	private List<ExamsModel> examsModel;

	public Long getId() {
		return id;
	}

	public List<ExamsModel> getExamsModel() {
		return examsModel;
	}

	public void setExamsModel(List<ExamsModel> examsModel) {
		this.examsModel = examsModel;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "CoursesModel [id=" + id + ", name=" + name + ", description="
				+ description + ", examsModel=" + examsModel + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CoursesModel other = (CoursesModel) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public String getImageIcon() {
		return imageIcon;
	}

	public void setImageIcon(String imageIcon) {
		this.imageIcon = imageIcon;
	}
	
	
}
