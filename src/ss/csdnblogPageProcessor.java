package ss;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

public class csdnblogPageProcessor implements PageProcessor {
	private Site site = Site.me().setRetryTimes(3).setSleepTime(1000).setTimeOut(6000);//设置抓取网站的重试次数,间隔和超时时间

	public void process(Page page) {

		try {

			String initPage="http://blog.csdn.net/index.html";
			String articlePattern = "http://blog.csdn.net/\\w+/article/details/\\d+";
			String expertPattern = "\\bhttp://blog.csdn.net/\\w+(?!/)\\b";
			String listpagePattern = "http://blog.csdn.net/\\w+/article/list/\\d+";
			String exn = "(" + expertPattern.replace(".", "\\.").replace("*", "[^\"'#]*") + ")";
			Selectable url = page.getUrl();
			if (url.regex(initPage).match()) // index page
			{
				// article
				addRequests(page, articlePattern);
				// expert
				addRequests(page, expertPattern);
				// //category
				List<String> category_urls = page.getHtml().css("div.side_nav").links().all();
				page.addTargetRequests(category_urls);
				// //next page
				List<String> page_urls = page.getHtml().css("div.page_nav").links().all();
				page.addTargetRequests(page_urls);
				// String lastpage_url=page_urls.get(page_urls.size()-1);
				// "/?&page=17"
				// String[] analysis=lastpage_url.split("=");
				// Integer
				// page_count=Integer.parseInt(analysis[analysis.length-1]);
				// addRequests(page, nextPagePattern);

			}
			// article
			else if (url.regex(articlePattern).match()) {
				
				WorkCenter.singleworkcenter.AddArticlePage(page);
//				page.putField("url", url.toString());
				// add user
				String url_str = url.toString();
				Pattern p = Pattern.compile("http://blog.csdn.net/\\w+");
				Matcher m = p.matcher(url_str);
				if (m.find()) {
					String user_url = m.group();
					page.addTargetRequest(user_url);
				}

			}

			// user
			else if (url.regex(exn).match() || url.regex(listpagePattern).match()) {
				System.out.println("this is expert");
				// get article list
				List<String> article_urls = page.getHtml().css("span.link_view").links().all();
				page.addTargetRequests(article_urls);
				// get pagelist
				List<String> page_urls = page.getHtml().css("div.pagelist").links().all();
				page.addTargetRequests(page_urls);

			} else {
				//no match
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void addRequests(Page page, String urlPattern) {
		String formatPattern = "(" + urlPattern.replace(".", "\\.").replace("*", "[^\"'#]*") + ")";
		List<String> requests = page.getHtml().links().regex(formatPattern).all();
		page.addTargetRequests(requests);
	}

	@Override
	public Site getSite() {
		return site;
	}
}
