package com.thorstenmarx.webtools.core.modules.analytics.db;

/*-
 * #%L
 * webtools-analytics
 * %%
 * Copyright (C) 2016 - 2018 Thorsten Marx
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.alibaba.fastjson.JSONObject;
import com.thorstenmarx.webtools.api.analytics.Fields;
import com.thorstenmarx.webtools.api.analytics.query.Aggregator;
import com.thorstenmarx.webtools.api.analytics.query.Query;
import org.awaitility.Awaitility;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author thmarx
 */
public class PerformanceTest {

	DefaultAnalyticsDb instance;
	MockedExecutor executor = new MockedExecutor();

	private static final int COUNT = 10000;
	
	private static final int INVOCATIONS = 5;
	private AtomicLong writing = new AtomicLong(0);
	private AtomicLong reading = new AtomicLong(0);

	@BeforeClass
	public void setup() {
		Configuration config = new Configuration("target/performance-" + System.currentTimeMillis());

		instance = new DefaultAnalyticsDb(config, executor);

		instance.open();
	}

	@AfterClass
	public void tearDown() throws InterruptedException, Exception {
		instance.close();
		executor.shutdown();
		System.out.println("\n\n");
		System.out.println("Number of tacked events each iteration: " + COUNT);
		System.out.println("writing : " + (writing.get() / INVOCATIONS) + "ms");
		System.out.println("reading : " + (reading.get() / INVOCATIONS) + "ms");
	}

	@Test(invocationCount = INVOCATIONS)
	public void test_performance () {
		long size = instance.index().size();
		long before = System.currentTimeMillis();
		track(COUNT);
		long after = System.currentTimeMillis();
		System.out.println("tracking : " + (after - before) + "ms");
		writing.addAndGet((after - before));

		before = System.currentTimeMillis();
		Awaitility.await().atMost(2, TimeUnit.MINUTES).until(() -> {
			return instance.index().size() == COUNT + size;
		});
		after = System.currentTimeMillis();
		System.out.println("waiting for ready: " + (after - before) + "ms");
		reading.addAndGet((after - before));
	}
	
	private int track(final int count) {
		for (long i = 0; i < count; i++) {
			CompletableFuture.runAsync(() -> {
				instance.track(TestHelper.event(event(), new JSONObject()));
			});
			
		}
		return count;
	}

	private int query_size() throws InterruptedException, ExecutionException {
		long startTime = System.currentTimeMillis() - (1000 * 60 * 60);
		long endTime = System.currentTimeMillis() + (1000 * 60 * 60);
		Query query = Query.builder().start(startTime).end(endTime).build();
		Future<Integer> future = instance.query(query, new Aggregator<Integer>() {
			@Override
			public Integer call() throws Exception {
				return documents.size();
			}
		});

		return future.get();
	}

	private JSONObject event() {

		JSONObject event = new JSONObject();

		event.put(Fields._UUID.value(), UUID.randomUUID().toString());
		event.put(Fields._TimeStamp.value(), System.currentTimeMillis());
		event.put("ua", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:51.0) Gecko/20100101 Firefox/51.0");
		event.put(Fields.UserId.value(), UUID.randomUUID().toString());
		event.put(Fields.VisitId.value(), UUID.randomUUID().toString());
		event.put(Fields.RequestId.value(), UUID.randomUUID().toString());

		return event;
	}
}
