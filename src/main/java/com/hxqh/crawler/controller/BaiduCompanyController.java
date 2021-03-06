package com.hxqh.crawler.controller;

import com.hxqh.crawler.repository.BaiduInfoRepository;
import com.hxqh.crawler.repository.CrawlerURLRepository;
import com.hxqh.crawler.repository.VBaiduCrawlerRepository;
import com.hxqh.crawler.service.CrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Ocean lin on 2018/3/7.
 *
 * @author Ocean lin
 */

@Controller
@RequestMapping("/baidu")
public class BaiduCompanyController {

    @Autowired
    private BaiduInfoRepository baiduInfoRepository;
    @Autowired
    private CrawlerService crawlerService;
    @Autowired
    private CrawlerURLRepository crawlerURLRepository;
    @Autowired
    private VBaiduCrawlerRepository vBaiduCrawlerRepository;


    @RequestMapping("/company")
    public String company() {


        return "crawler/notice";
    }


}
