package com.book.ensureu.flow.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserGrowthDto {

    List<String> paperIds;  //TODO can remove this as UI can iterate map keys
    Map<String,UserGrowthPointDto> userGrowthPointDtoMap;


}
