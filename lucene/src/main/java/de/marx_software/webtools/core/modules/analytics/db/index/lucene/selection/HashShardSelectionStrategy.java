package de.marx_software.webtools.core.modules.analytics.db.index.lucene.selection;

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

import com.google.common.collect.Iterators;
import de.marx_software.webtools.core.modules.analytics.db.index.lucene.Shard;
import de.marx_software.webtools.core.modules.analytics.db.index.lucene.ShardSelectionStrategy;
import de.marx_software.webtools.core.modules.analytics.db.index.lucene.selection.hash.ConsistentHashRouter;
import de.marx_software.webtools.core.modules.analytics.db.index.lucene.selection.hash.Node;
import java.util.ArrayList;

import java.util.Iterator;
import java.util.List;

/**
 * @author marx
 * @param <T>
 */
public class HashShardSelectionStrategy<T extends Shard> implements ShardSelectionStrategy<T> {

	private final int shardCount;
	private Iterator<T> it;
	private final ConsistentHashRouter<ShardNode<T>> consistentHashRouter;

	public HashShardSelectionStrategy(final List<T> shards) {
		it = Iterators.cycle(shards);
		this.shardCount = shards.size();
		
		List<ShardNode<T>> shardNodes = new ArrayList<>();
		shards.stream().map((s) -> new ShardNode(s)).forEach(shardNodes::add);

        //hash them to hash ring
        consistentHashRouter = new ConsistentHashRouter<>(shardNodes, 1);
	}

	@Override
	public synchronized T next() {
		throw new UnsupportedOperationException("not supported by hash function");
	}

	@Override
	public T route(final String key) {
		return consistentHashRouter.routeNode(key).shard;
	}
	
	private static class ShardNode<T extends Shard> implements Node {

		protected final T shard;

		public ShardNode(final T shard) {
			this.shard = shard;
		}
		
		
		@Override
		public String getKey() {
			return shard.getName();
		}
		
	}
}
