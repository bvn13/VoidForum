package ru.bvn13.voidforum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import redis.embedded.Redis;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import static java.util.stream.Collectors.joining;

/**
 * @author bvn13 <mail4bvn@gmail.com>
 */

@SpringBootApplication
@EnableCaching
public class Application {

	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	@Autowired
	@Qualifier("RedisServer")
	private Redis redisServer;


	@PostConstruct
	public void start() {
		logger.info("starting redis...");
		if (!redisServer.isActive()) {
            redisServer.start();
        }
		if (redisServer.isActive()) {
            logger.info("redis listen ports: {}", redisServer.ports().stream().map(Object::toString).collect(joining(",")));
        }
	}

	@PreDestroy
	public void stop() {

		logger.info("shutting down redis...");
		redisServer.stop();
		logger.info("bye!");
	}


	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
