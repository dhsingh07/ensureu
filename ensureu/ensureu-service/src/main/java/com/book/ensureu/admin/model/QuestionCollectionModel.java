package com.book.ensureu.admin.model;

import org.springframework.data.mongodb.core.mapping.Document;

import com.book.ensureu.model.BaseModel;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
@Document(collection = "questionCollection")
public class QuestionCollectionModel extends BaseModel {
}
