package com.crp.qa.qaCore.util.counter;
import java.util.Calendar;
import java.util.Date;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class QaCountSaver implements Runnable{
	
	private final Logger LOGGER = LoggerFactory.getLogger(QaCountSaver.class);
	
	private static QaCountSaver countSaver = null;
	
	private QaCountSaver() {}
	
	public static QaCountSaver getInstance() {
		if(countSaver==null) {
			synchronized (QaCountSaver.class) {
				if(countSaver==null) {
					countSaver = new QaCountSaver();
				}
			}
		}
		return countSaver;
	}
	
	@Override
	public void run() {
		//计数器
		QaCounter counter = QaCounter.getInstance();
		while(true) {
			//取到今天
			Calendar calendar = Calendar.getInstance();
			int hour = calendar.get(Calendar.HOUR_OF_DAY);
			
			//如果是0点，保存一次昨天的，然后再删除昨天的临时记录
			if(hour == 0) {
				calendar.add(Calendar.DATE,-1);
				Date yesterday = calendar.getTime();
				Integer yesterday_num = counter.getCount(yesterday);
				if(yesterday_num!=null) {
					counter.saveOrUpdate(yesterday,yesterday_num);
				}
				//删除昨天的记录
				counter.delCount(yesterday);
			}
			
			//查找今天的计数
			Integer today_num = counter.getCount();
			System.out.println(new Date() + "---------num:" + today_num);
			//保存到数据库
			if(today_num!=null) {
				counter.saveOrUpdate(new Date(),today_num);
			}
			
			try {
				//6分钟更新一次
				Thread.sleep(360000);
			} catch (InterruptedException e) {
				LOGGER.error("访问计数线程被意外唤醒！",e);
			}
		}
	}
}
