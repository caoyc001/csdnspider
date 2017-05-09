package ss;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.management.JMException;

import redis.clients.util.Pool;
import ss.db.SQLiteHelper;
import ss.object.Article;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.scheduler.MonitorableScheduler;

public class WorkCenter {

	public static WorkCenter singleworkcenter = new WorkCenter();
	private Queue<Page> article_pages = new ArrayBlockingQueue<Page>(5000);
	private Queue<Article> articles = new ArrayBlockingQueue<Article>(5000);

	private Queue<Page> article_pages_thread = new ArrayBlockingQueue<Page>(5000);

	ExecutorService pool = Executors.newFixedThreadPool(5);

	private volatile Boolean PageFlag;
	private volatile Boolean ArticleFlag;
	private Spider downloadspider;
	private Boolean SpiderMonitorFlag;

	public WorkCenter() {
		PageFlag = true;
		ArticleFlag = true;
		SpiderMonitorFlag = true;
		OnStarted();
	}

	public int getLeftPageCount(Spider spider) {
		if (spider.getScheduler() instanceof MonitorableScheduler) {
			return ((MonitorableScheduler) spider.getScheduler()).getLeftRequestsCount(spider);
		}
		// logger.warn("Get leftPageCount fail, try to use a Scheduler implement
		// MonitorableScheduler for monitor count!");
		return -1;
	}

	public void AddSpider(Spider spider) {
		downloadspider = spider;
		// SpiderMonitor spiderMonitor = SpiderMonitor.instance();
		// try {
		// spiderMonitor.register(spider);
		// } catch (JMException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		// class MonitorSpider extends Thread {
		// public void run() {
		// while (SpiderMonitorFlag) {
		// int lefttpage_count = getLeftPageCount(downloadspider);
		// if(lefttpage_count==0)
		// {
		// PageFlag = false;
		// ArticleFlag = false;
		// SpiderMonitorFlag = false;
		// System.out.println("===============");
		// System.out.println("no more article");
		// System.out.println("===============");
		// }
		// try {
		// Thread.sleep(2000);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		// }
		// }
		// pool.execute(new MonitorSpider());

	}

	public void AddArticlePage(Page page) {
		synchronized (article_pages) {
			article_pages.add(page);
		}
	}

	public void OnStarted() {
		class AnalysisPage extends Thread {
			int miss_count = 0;
			public void run() {
				while (PageFlag) {
					synchronized (article_pages) {
						if (article_pages.size() == 0) {
							miss_count++;
							if (miss_count == 20) {
								PageFlag = false;
								ArticleFlag = false;
							}
						}
						while (article_pages.size() != 0) {
							article_pages_thread.add(article_pages.poll());
						}
					}
					for (Page page : article_pages_thread) {
						Article newArticle = GetArticle(page);
						articles.add(newArticle);
					}
					article_pages_thread.clear();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		new AnalysisPage().start();
		// pool.execute(new AnalysisPage());
		class AnalysisArticle extends Thread {
			public void run() {
				while (ArticleFlag) {
					synchronized (articles) {
						while (articles.size() != 0) {
							Article cur_article = articles.poll();
							SQLiteHelper.mySqLiteHelper.Insert(cur_article);
						}
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		new AnalysisArticle().start();
		// pool.execute(new AnalysisArticle());

	}

	public Article GetArticle(Page page) {
		String cur_title = page.getHtml().xpath("//h1/span/a/text()").toString();
		String cur_user = page.getHtml().xpath("//div[@id='blog_userface']/span/a[@class='user_name']/text()")
				.toString();
		List<String> content = page.getHtml().xpath("//div[@id='article_content']").$("p", "text").all();
		StringBuilder sbcontent = new StringBuilder();
		for (String s : content) {
			if (s.trim() != "") {
				sbcontent.append(s.trim());
			}
		}
		List<String> tags = page.getHtml().xpath("//div[@class='article_l']/span[@class='link_categories'").$("a","text").all();
		return new Article(cur_user, cur_title, sbcontent.toString(), page.getUrl().toString(),tags.toString());

	}

}
