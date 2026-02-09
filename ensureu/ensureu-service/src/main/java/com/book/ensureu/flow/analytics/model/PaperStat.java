package com.book.ensureu.flow.analytics.model;

import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.flow.analytics.util.KeyConversionUtil;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

/**
 * This class is used to make percentile graph for a paper.
 * TopperPaperStatList contains topper ids and similarly userPaperStatList
 * percentileDataObjectList contains info about particular percentile object
 *
 * @author Manish
 * @version 1.0
 * @since 1 oct 2019
 */
@Data
@Document()
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PaperStat {

    @Id
    private String paperId;

    private String paperDescription;

    private String paperHierarchy; //TODO we can either store whole data or use paper hierarchy to find it category or subcategory

    private PaperSubCategory paperSubCategory;

    private List<String> userPaperStatList;

    private List<String> topperPaperStatList;

    private ArrayList<PercentileDataObject> percentileDataObjectList;

    @Builder.Default
    private TreeMap<String, List<String>> marksVsUsersTreeMap = new TreeMap<>(
            (a, b) -> {
                Double da = KeyConversionUtil.getDoubleFromKey(a);
                Double db = KeyConversionUtil.getDoubleFromKey(b);
                return db.compareTo(da);
            }
    );


}
