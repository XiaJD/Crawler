package com.hxqh.crawler.repository;

import com.hxqh.crawler.model.CrawlerVarietyURL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by Ocean lin on 2018/3/12.
 *
 * @author Ocean lin
 */
@Repository
public interface CrawlerVarietyURLRepository extends JpaRepository<CrawlerVarietyURL, Integer> {

    @Modifying
    @Query("delete from CrawlerVarietyURL o ")
    void deleteIqiyiVarietyURL();

}
