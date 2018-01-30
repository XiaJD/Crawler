package com.hxqh.crawler.controller.thread;

import com.hxqh.crawler.common.Constants;
import com.hxqh.crawler.domain.VideosFilm;
import com.hxqh.crawler.model.CrawlerURL;
import com.hxqh.crawler.repository.CrawlerProblemRepository;
import com.hxqh.crawler.service.SystemService;
import com.hxqh.crawler.util.CrawlerUtils;
import com.hxqh.crawler.util.DateUtils;
import com.hxqh.crawler.util.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

/**
 * Created by Ocean lin on 2018/1/19.
 */
public class PersistFilm implements Runnable {


    private static final Integer STRINGBUILDER_SIZE = 300;


    private List<CrawlerURL> l;
    private CrawlerProblemRepository crawlerProblemRepository;
    private SystemService systemService;


    public PersistFilm(List<CrawlerURL> l, CrawlerProblemRepository crawlerProblemRepository, SystemService systemService) {
        this.l = l;
        this.crawlerProblemRepository = crawlerProblemRepository;
        this.systemService = systemService;
    }

    @Override
    public void run() {
        try {
            parseAndPersist(l, crawlerProblemRepository, systemService);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void parseAndPersist(List<CrawlerURL> hrefList, CrawlerProblemRepository crawlerProblemRepository, SystemService systemService) throws InterruptedException {
        StringBuilder stringBuilder = new StringBuilder(STRINGBUILDER_SIZE);


        for (int i = 0; i < hrefList.size(); i++) {
            CrawlerURL crawlerURL = hrefList.get(i);
            String html = CrawlerUtils.fetchHTMLContent(crawlerURL.getUrl(), Constants.DEFAULT_SEELP_SECOND);
            Document doc = Jsoup.parse(html);
            String source = crawlerURL.getPlatform();

            String filmName = new String();
            String star = new String();
            String director = new String();

            try {
                Element filmNameElement = doc.getElementById("widget-videotitle");
                if (filmNameElement != null) {
                    filmName = filmNameElement.text();
                }

                Elements starElement = doc.getElementsByClass("progInfo_txt").
                        select("p").get(2).select("span").get(1).select("a");
                if (starElement != null) {
                    star = starElement.text();
                }


                Elements directorElement = doc.getElementsByClass("progInfo_txt").
                        select("p").get(1).select("span").get(1).select("a");
                if (directorElement != null) {
                    director = directorElement.text();
                }

                String category = crawlerURL.getCategory();

                Elements labelElement = doc.getElementById("datainfo-taglist").select("a");
                String label = labelElement.text();
                String score = doc.select("span[class=score-new]").get(0).attr("snsscore");


                String commentNum = doc.getElementsByClass("score-user-num").text();
                if (commentNum.endsWith("万人评分")) {
                    commentNum = commentNum.substring(0, commentNum.length() - 4);
                    Double v = Double.valueOf(commentNum) * Constants.TEN_THOUSAND;
                    commentNum = String.valueOf(v.longValue());
                }
                if (commentNum.endsWith("人评分")) {
                    commentNum = commentNum.substring(0, commentNum.length() - 4);
                    commentNum = String.valueOf(Long.valueOf(commentNum));
                }

                String up = doc.getElementById("widget-voteupcount").text();
                if (up.endsWith("万")) {
                    up = up.substring(0, up.length() - 1);
                    Double v = Double.valueOf(up) * Constants.TEN_THOUSAND;
                    up = String.valueOf(v.longValue());
                }

                String addTime = DateUtils.getTodayDate();
                String playNum = doc.getElementById("chartTrigger").select("span").text();

                if (playNum.endsWith("万")) {
                    playNum = playNum.substring(0, playNum.length() - 1);
                    Double v = Double.valueOf(playNum) * Constants.TEN_THOUSAND;
                    playNum = String.valueOf(v.longValue());
                }
                if (playNum.endsWith("亿")) {
                    playNum = playNum.substring(0, playNum.length() - 1);
                    Double v = Double.valueOf(playNum) * Constants.BILLION;
                    playNum = String.valueOf(v.longValue());
                }


                /**
                 * 3.解析并持久化至本地文件系统
                 */
                stringBuilder.append(source.trim()).append("^").
                        append(filmName.trim()).append("^").
                        append(star.trim()).append("^").
                        append(director.trim()).append("^").
                        append(category.trim()).append("^").
                        append(label.trim()).append("^").
                        append(score.trim()).append("^").
                        append(commentNum.trim()).append("^").
                        append(up.trim()).append("^").
                        append(addTime.trim()).append("^").
                        append(playNum.trim()).append("\n");
                String fileName = Constants.SAVE_PATH + Constants.FILE_SPLIT +
                        DateUtils.getTodayDate() + "-" + crawlerURL.getPlatform();
                FileUtils.writeStrToFile(stringBuilder.toString(), fileName);
                stringBuilder.setLength(0);
                System.out.println(filmName.trim() + " Persist Success!");

                /**
                 * 持久化至ES
                 */
                if (!score.trim().equals("评分人数不足")) {
                    VideosFilm videosFilm = new VideosFilm(source.trim(), filmName.trim(), star.trim(), director.trim(),
                            category.trim(), label.trim(), Float.valueOf(score.trim()), Integer.valueOf(commentNum.trim()),
                            Integer.valueOf(up.trim()), addTime.trim(), Integer.valueOf(playNum.trim()));
                    systemService.addVideos(videosFilm);
                } else {
                    continue;
                }


            } catch (Exception e) {
                e.printStackTrace();
                // 持久化无法爬取URL
                CrawlerUtils.persistProblemURL(crawlerProblemRepository, crawlerURL);
            }
        }
    }

}