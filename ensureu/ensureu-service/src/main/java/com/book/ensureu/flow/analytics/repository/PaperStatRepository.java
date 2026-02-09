package com.book.ensureu.flow.analytics.repository;

import com.book.ensureu.exception.unchecked.EntityNotFound;
import com.book.ensureu.flow.analytics.model.PaperStat;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Slf4j
@Component
public class PaperStatRepository  {

    private MongoTemplate mongoTemplate;

    /**
     * Throws EntityNotFound Exception when no data found
     *
     * */
    public PaperStat fetchPaperStat(String paperId){
        Query query = new Query(Criteria.where("paperId").is(paperId));
        List<PaperStat> paperStatList = mongoTemplate.find(query,PaperStat.class);
        if(Objects.isNull(paperStatList) ||  paperStatList.isEmpty()){
            log.debug("[fetchPaperStat] paper: [{}] not found ",paperId);
            return null;
        }

        return paperStatList.get(0);
    }

    public void addUserMarksToPaperStat(String id, Double marks){

    }

    public void savePaperStat(PaperStat paperStat){
        mongoTemplate.save(paperStat);
    }


    public List<PaperStat> fetchPaperStatByIdIn(List<String> paperIds) {
        Query query = new Query(Criteria.where("paperId").in(paperIds));
        List<PaperStat> paperStatList = mongoTemplate.find(query, PaperStat.class);
        if (Objects.isNull(paperStatList) || paperStatList.isEmpty()) {
            log.error("[fetchPaperStat] papers: [{}] not found ", paperIds);
            throw new EntityNotFound("Paper Invalid");
        }

        return paperStatList;
    }

}
