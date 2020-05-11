/*
 * Copyright (C) 2019 Thorsten Marx
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.thorstenmarx.webtools.core.modules.analytics.storage.module.cluster;

/*-
 * #%L
 * webtools-manager
 * %%
 * Copyright (C) 2016 - 2019 Thorsten Marx
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
import com.google.gson.Gson;
import com.thorstenmarx.webtools.api.CoreModuleContext;
import com.thorstenmarx.webtools.api.analytics.AnalyticsDB;
import com.thorstenmarx.webtools.api.analytics.Filter;
import com.thorstenmarx.webtools.api.analytics.query.Aggregator;
import com.thorstenmarx.webtools.api.analytics.query.LimitProvider;
import com.thorstenmarx.webtools.api.analytics.query.Query;
import com.thorstenmarx.webtools.api.analytics.query.ShardedQuery;
import com.thorstenmarx.webtools.api.cluster.ClusterMessageAdapter;
import com.thorstenmarx.webtools.api.cluster.ClusterService;
import com.thorstenmarx.webtools.core.modules.analytics.db.DefaultAnalyticsDb;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author marx
 */
public class ClusterAnalyticsDb implements AnalyticsDB, ClusterMessageAdapter<ClusterAnalyticsDb.PayloadTrack>{

	private static final Logger LOGGER = LoggerFactory.getLogger(ClusterAnalyticsDb.class);

	private static final String EVENT_TRACK = "event_track";

	private final DefaultAnalyticsDb db;
	Gson gson = new Gson();
	final ClusterService clusterSerivce;

	public ClusterAnalyticsDb(final DefaultAnalyticsDb db, final CoreModuleContext context, final ClusterService clusterSerivce) {
		this.db = db;
		this.clusterSerivce = clusterSerivce;

	}

	@Override
	public <T> CompletableFuture<T> query(Query query, Aggregator<T> aggregator) {
		return db.query(query, aggregator);
	}

	@Override
	public <R, S, Q extends LimitProvider> R queryAsync(ShardedQuery<R, S, Q> query) {
		return db.queryAsync(query);
	}

	@Override
	public void track(final Map<String, Map<String, Object>> event) {
		PayloadTrack payload = new PayloadTrack();
		payload.event = event;

		clusterSerivce.append(EVENT_TRACK, payload);
		
		db.track(event);
	}

	@Override
	public void addFilter(Filter filter) {
		db.addFilter(filter);
	}

	@Override
	public List<Filter> getFilters() {
		return db.getFilters();
	}

	@Override
	public boolean hasFilters() {
		return db.hasFilters();
	}

	@Override
	public Class<PayloadTrack> getValueClass() {
		return PayloadTrack.class;
	}

	@Override
	public String getType() {
		return EVENT_TRACK;
	}

	@Override
	public void reset() {
	}

	@Override
	public void apply(PayloadTrack value) {
		db.track(value.event);
	}

	public static class PayloadTrack implements Serializable {

		public Map<String, Map<String, Object>> event;
	}

}
