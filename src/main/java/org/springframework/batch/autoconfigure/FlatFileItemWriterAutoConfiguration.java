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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Michael Minella
 */
@Configuration
@EnableConfigurationProperties(FlatFileItemWriterProperties.class)
@AutoConfigureAfter(BatchAutoConfiguration.class)
public class FlatFileItemWriterAutoConfiguration {

	private FlatFileItemWriterProperties properties;

	public FlatFileItemWriterAutoConfiguration(FlatFileItemWriterProperties properties) {
		this.properties = properties;
	}

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty(prefix = "spring.batch.job.filewriter", name = "name")
	public FlatFileItemWriter<Map<Object, Object>> itemWriter() {

		return new FlatFileItemWriterBuilder<Map<Object, Object>>()
				.name(properties.getName())
				.resource(properties.getResource())
				.delimited()
				.delimiter(properties.getDelimiter())
				.fieldExtractor(new MapFieldExtractor(properties.getNames()))
				.append(properties.isAppend())
				.build();
	}

	public static class MapFieldExtractor implements FieldExtractor<Map<Object, Object>> {

		private String[] names;

		public MapFieldExtractor(String[] names) {
			this.names = names;
		}

		@Override
		public Object[] extract(Map<Object, Object> item) {

			List<Object> fields = new ArrayList<>(item.size());

			for (String name : names) {
				fields.add(item.get(name));
			}

			return fields.toArray();
		}
	}
}
