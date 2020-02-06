package cn.edu.nju.loghelper;

import cn.edu.nju.loghelper.entity.LoggerMessage;
import cn.edu.nju.loghelper.util.LoggerQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

import javax.annotation.PostConstruct;
import java.text.DateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
@EnableWebSocketMessageBroker
@EnableScheduling
public class LoghelperApplication {
	private static final Logger LOGGER = LoggerFactory.getLogger(LoghelperApplication.class);

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	public static void main(String[] args) {
		SpringApplication.run(LoghelperApplication.class, args);
	}

	private int info = 0;
	@Scheduled(fixedRate = 1000)
	public void pushMessage() {

		LoggerQueue.getInstance().push(LoggerMessage.builder()
				.timestamp(DateFormat.getDateTimeInstance().format(new Date()))
				.className(this.getClass().getName())
				.body("log info: " + info++)
				.level("info")
				.threadName(Thread.currentThread().getName())
				.build());
	}

	@PostConstruct
	public void pushLog() {
		ExecutorService service = Executors.newFixedThreadPool(2);
		Runnable pull = () -> {
			while (true) {
				LoggerMessage message = LoggerQueue.getInstance().poll();
				if (message != null) {
					if (simpMessagingTemplate != null) {
						simpMessagingTemplate.convertAndSend("/topic/pullLogger",message);
					}
				}
			}
		};
		service.submit(pull);
	}
}
