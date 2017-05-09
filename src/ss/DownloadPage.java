package ss;

import javax.management.JMException;

import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.SimplePageProcessor;
import us.codecraft.webmagic.scheduler.DuplicateRemovedScheduler;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.component.BloomFilterDuplicateRemover;
import us.codecraft.webmagic.scheduler.component.HashSetDuplicateRemover;

public class DownloadPage {

	public static void main(String[] args) {
		GetCSDN();
	}

	public static void GetCSDN() {
		try
		{		
		String initpage_url="http://blog.csdn.net/index.html";
		csdnblogPageProcessor ppor = new csdnblogPageProcessor();
		Spider csdnspider = Spider.create(ppor).setScheduler(new QueueScheduler().setDuplicateRemover(new HashSetDuplicateRemover()))
				.addUrl(initpage_url)
//				.addPipeline(new JsonFilePipeline(""))
				.thread(50);
//		SpiderMonitor.instance().register(csdnspider);
		csdnspider.start();
//		WorkCenter.singleworkcenter.AddSpider(csdnspider);
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
	}

}
