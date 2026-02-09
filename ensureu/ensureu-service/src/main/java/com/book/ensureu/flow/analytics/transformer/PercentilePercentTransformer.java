package com.book.ensureu.flow.analytics.transformer;

import com.book.ensureu.flow.analytics.dto.PercentileDto;
import com.book.ensureu.flow.analytics.model.PercentileDataObject;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
public class PercentilePercentTransformer implements Transformer<PercentileDto, PercentileDataObject>{


    @Override
    public PercentileDto toModel(PercentileDataObject from) {
        return PercentileDto.builder().
                label(from.getLabel()).
                rank(from.getRank()).
                marks(from.getMarks()).
                percentile(from.getPercentile()).
                userIds(from.getUserIds()).
                build();
    }

    @Override
    public List<PercentileDto> toModel(List<PercentileDataObject> from) {
        List<PercentileDto> percentileDtoList = new LinkedList<>();
        from.forEach(fromPercentileDataObject -> {
            percentileDtoList.add(toModel(fromPercentileDataObject));
        });

        return percentileDtoList;
    }

    @Override
    public PercentileDataObject ToDto(PercentileDto from) {
        return null;
    }

    @Override
    public List<PercentileDataObject> toDto(List<PercentileDto> from) {
        return null;
    }
}
