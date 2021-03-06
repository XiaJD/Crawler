package com.hxqh.crawler.controller.thread;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.model.VDouBanCrawlerFilm;
import com.hxqh.crawler.repository.CrawlerDoubanSocreRepository;
import com.hxqh.crawler.util.DouBanUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by Ocean lin on 2018/3/9.
 *
 * @author Ocean lin
 */
public class PersistDouBan implements Runnable {



    private List<VDouBanCrawlerFilm> l;
    private CrawlerDoubanSocreRepository crawlerDoubanSocreRepository;
    private String category;

    public PersistDouBan(List<VDouBanCrawlerFilm> l, CrawlerDoubanSocreRepository crawlerDoubanSocreRepository, String category) {
        this.l = l;
        this.crawlerDoubanSocreRepository = crawlerDoubanSocreRepository;
        this.category = category;
    }


    @Override
    public void run() {

        String cate = category == "film" ? "电影" : "";

        String urlString1 = null;

        for (int i = 0; i < l.size(); i++) {
            VDouBanCrawlerFilm crawlerURL = l.get(i);
            try {
                urlString1 = URLEncoder.encode(crawlerURL.getTitle(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String url = Constants.DOUBAN_SEARCH_URL + urlString1;
            // <电影名称/图书名称/上映电影，Url>
            String filmName = null;
            try {
                filmName = URLDecoder.decode(url.split("=")[1], "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            DouBanUtils.persistDouBan(cate, url, filmName, crawlerDoubanSocreRepository, category);
        }
    }


}
