/*
 * Copyright 2019 the original author or authors.
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
package org.springframework.batch.autoconfigure;

import java.util.Map;
import java.util.function.Function;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * @author Michael Minella
 */
@Configuration
@EnableConfigurationProperties(SingleStepProperties.class)
@AutoConfigureAfter(BatchAutoConfiguration.class)
public class SingleStepAutoConfiguration {

	private JobBuilderFactory jobBuilderFactory;

	private StepBuilderFactory stepBuilderFactory;

	private SingleStepProperties properties;

	private ApplicationContext context;

	public SingleStepAutoConfiguration(JobBuilderFactory jobBuilderFactory,
			StepBuilderFactory stepBuilderFactory,
			SingleStepProperties properties,
			ApplicationContext context) {
		this.jobBuilderFactory = jobBuilderFactory;
		this.stepBuilderFactory = stepBuilderFactory;
		this.properties = properties;
		this.context = context;
	}

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty(prefix = "spring.batch.job", name = "job-name")
	public Job job(ItemReader<Map<Object, Object>> itemReader, ItemWriter<Map<Object, Object>> itemWriter) {
		SimpleStepBuilder<Map<Object, Object>, Map<Object, Object>> stepBuilder = stepBuilderFactory.get(properties.getStepName())
				.<Map<Object, Object>, Map<Object, Object>>chunk(properties.getChunkSize())
				.reader(itemReader);

		if(!StringUtils.isEmpty(this.properties.getItemProcessor())) {
			Object itemProcessor = this.context.getBean(this.properties.getItemProcessor());

			if(itemProcessor instanceof ItemProcessor) {
				stepBuilder.processor((ItemProcessor) itemProcessor);
			}
			else if(itemProcessor instanceof Function) {
				stepBuilder.processor((Function) itemProcessor);
			}
			else {
				throw new IllegalArgumentException("The bean configured as an ItemProcessor must be either an " +
						"ItemProcessor<Map<Object, Object>,Map<Object, Object>> or a " +
						"Function<Map<Object, Object>,Map<Object, Object>>.  The bean found is a " +
						itemProcessor.getClass().toGenericString());
			}
		}

		Step step = stepBuilder.writer(itemWriter)
				.build();

		return this.jobBuilderFactory.get(properties.getJobName())
				.start(step)
				.build();
	}
}
