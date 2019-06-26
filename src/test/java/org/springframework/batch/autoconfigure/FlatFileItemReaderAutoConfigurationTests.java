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

import org.junit.Test;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.EmbeddedDataSourceConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michael Minella
 */
public class FlatFileItemReaderAutoConfigurationTests {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(BatchAutoConfiguration.class,
					TransactionAutoConfiguration.class,
					FlatFileItemReaderAutoConfiguration.class));

	@Test
	public void testEmptyContext() {
		this.contextRunner
				.withUserConfiguration(TestConfiguration.class,
						EmbeddedDataSourceConfiguration.class)
				.run((context) -> {
					assertThat(context).doesNotHaveBean("reader");
				});
	}

	@Test
	public void testDefaultContext() {
		this.contextRunner
				.withUserConfiguration(TestConfiguration.class,
						EmbeddedDataSourceConfiguration.class)
				.withPropertyValues("spring.batch.job.filereader.resource=/data/summaryFile.csv",
						"spring.batch.job.filereader.name=fooReader",
						"spring.batch.job.filereader.names=foo,bar",
						"spring.batch.job.filereader.delimited=true")
				.run((context) -> {
					assertThat(context).hasBean("itemReader");
					FlatFileItemReader flatFileItemReader = context.getBean(FlatFileItemReader.class);
					assertThat(ReflectionTestUtils.getField(flatFileItemReader, "resource")).isNotNull();
					assertThat(flatFileItemReader.getExecutionContextKey("key")).isEqualTo("fooReader.key");
				});
	}

	@EnableBatchProcessing
	protected static class TestConfiguration {
	}

}
