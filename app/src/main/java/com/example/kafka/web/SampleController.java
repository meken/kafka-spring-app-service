/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.kafka.web;

import java.time.Instant;

import com.example.kafka.message.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SampleController {
	private static final Logger logger = LoggerFactory.getLogger(SampleController.class);

	@Autowired
	private Producer producer;

	@GetMapping("/ping")
	public String ping() {
	    logger.info("# Ping @{}", Instant.now());
	    return "OK";
	}

	@PostMapping("/send")
	public String send(@RequestBody String payload) {
		logger.info("# Sending: {}", payload);
		ListenableFuture<SendResult<String, String>> future = producer.send(payload);
		future.addCallback(result -> logger.info("# Successfully sent: {}", result),
				result -> logger.info("# Failed sending: {}", result));
		return "OK";
	}
}
