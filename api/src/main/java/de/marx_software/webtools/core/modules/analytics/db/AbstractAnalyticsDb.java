package de.marx_software.webtools.core.modules.analytics.db;

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

import de.marx_software.webtools.api.analytics.Filter;
import de.marx_software.webtools.core.modules.analytics.db.index.Index;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author thmarx
 * @param <I>
 */
public abstract class AbstractAnalyticsDb<I extends Index> implements AnalyticsDbInternal<I> {
	
	private final List<Filter> filters = new ArrayList<>();
	
	public void addFilter (final Filter filter) {
		filters.add(filter);
	}
	public List<Filter> getFilters () {
		return filters;
	}
	public boolean hasFilters () {
		return !filters.isEmpty();
	}
}