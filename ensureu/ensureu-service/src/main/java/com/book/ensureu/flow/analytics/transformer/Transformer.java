package com.book.ensureu.flow.analytics.transformer;

import java.util.List;

public interface Transformer<Model, Dto> {

    Model toModel(Dto from);

    List<Model> toModel(List<Dto> from);

    Dto ToDto(Model from);

    List<Dto>  toDto(List<Model> from);
}
