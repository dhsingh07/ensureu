package com.book.ensureu.common.transformer;

import java.util.List;
import java.util.stream.Collectors;

public interface Transformer<Model,Dto> {

    Model dtoToModel(Dto dto);
    Dto  modelToDto(Model model);

    default List<Model> dtoToModel(List<Dto> dtoList){
        return dtoList.stream().map(this::dtoToModel).collect(Collectors.toList());
    }

    default List<Dto> modelToDto(List<Model> modelList){
        return modelList.stream().map(this::modelToDto).collect(Collectors.toList());
    }
}
