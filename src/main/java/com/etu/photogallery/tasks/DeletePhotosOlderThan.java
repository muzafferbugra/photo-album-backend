package com.etu.photogallery.tasks;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.etu.photogallery.repository.PhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DeletePhotosOlderThan {

	@Autowired
	PhotoRepository photoRepository;

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	@Scheduled(cron = "0 0 12 * * ?")
	public void fixedRateScheduledTaskMethod() {
		System.out.println("Fixed Rate Scheduled Task Worked! " + dateFormat.format(new Date()));
	}
}