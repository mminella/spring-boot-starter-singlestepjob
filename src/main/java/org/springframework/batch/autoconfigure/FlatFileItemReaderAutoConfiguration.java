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

import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.Range;
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
@EnableConfigurationProperties(FlatFileItemReaderProperties.class)
@AutoConfigureAfter(BatchAutoConfiguration.class)
public class FlatFileItemReaderAutoConfiguration {

	private final FlatFileItemReaderProperties properties;

	public FlatFileItemReaderAutoConfiguration(FlatFileItemReaderProperties properties) {
		this.properties = properties;
	}

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty(prefix = "spring.batch.job.filereader", name = "name")
	public FlatFileItemReader<Map<Object, Object>> reader() {
		FlatFileItemReaderBuilder<Map<Object, Object>> mapFlatFileItemReaderBuilder = new FlatFileItemReaderBuilder<Map<Object, Object>>()
				.name(this.properties.getName())
				.resource(this.properties.getResource())
				.saveState(this.properties.isSaveState())
				.maxItemCount(this.properties.getMaxItemCount())
				.currentItemCount(this.properties.getCurrentItemCount())
				.strict(this.properties.isStrict())
				.encoding(this.properties.getEncoding())
				.linesToSkip(this.properties.getLinesToSkip());

		if(this.properties.isDelimited()) {
			mapFlatFileItemReaderBuilder.delimited()
					.quoteCharacter(this.properties.getQuoteCharacter())
					.delimiter(this.properties.getDelimiter())
					.includedFields(this.properties.getIncludedFields().toArray(new Integer[0]))
					.names(this.properties.getNames())
					.beanMapperStrict(this.properties.isParsingStrict())
					.fieldSetMapper(new MapFieldSetMapper());
		}
		else {
			mapFlatFileItemReaderBuilder.fixedLength()
				.columns(this.properties.getRanges().toArray(new Range[0]))
				.names(this.properties.getNames())
				.fieldSetMapper(new MapFieldSetMapper())
				.beanMapperStrict(this.properties.isParsingStrict());
		}

		return mapFlatFileItemReaderBuilder.build();
	}

	public static class MapFieldSetMapper implements FieldSetMapper<Map<Object, Object>> {

		@Override
		public Map<Object, Object> mapFieldSet(FieldSet fieldSet) {
			return fieldSet.getProperties();
		}
	}
}
