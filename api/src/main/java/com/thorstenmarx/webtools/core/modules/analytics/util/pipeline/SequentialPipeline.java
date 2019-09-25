package com.thorstenmarx.webtools.core.modules.analytics.util.pipeline;

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
import java.util.ArrayList;
import java.util.List;

/**
 * @author thmarx
 */
public class SequentialPipeline implements Pipeline {

	private final List<Stage> m_stages = new ArrayList<>();

	@Override
	public void addStage(Stage stage) {
		m_stages.add(stage);

	}

	@Override
	public void execute(PipelineContext context) {
		/* execute the stages */
		m_stages.stream().forEach((stage) -> {
			stage.execute(context);
		});
	}
}
