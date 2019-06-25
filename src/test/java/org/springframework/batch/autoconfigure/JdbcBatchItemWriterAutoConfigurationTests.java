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
import org.springframework.batch.item.database.JdbcBatchItemWriter;
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
public class JdbcBatchItemWriterAutoConfigurationTests {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(BatchAutoConfiguration.class,
					TransactionAutoConfiguration.class,
					JdbcBatchItemWriterAutoConfiguration.class));

	@Test
	public void emptyContext() {
		this.contextRunner
				.withUserConfiguration(TestConfiguration.class,
						EmbeddedDataSourceConfiguration.class)
				.run((context) -> assertThat(context).doesNotHaveBean("reader"));
	}

	@Test
	public void testDefaultContextWithNamedParameters() {
		this.contextRunner
				.withUserConfiguration(FlatFileItemReaderAutoConfigurationTests.TestConfiguration.class,
						EmbeddedDataSourceConfiguration.class)
				.withPropertyValues("spring.batch.job.jdbcwriter.sql=INSERT INTO FOO VALUES :one, :two, :three",
						"spring.batch.job.jdbcwriter.names=one,two,three")
				.run((context) -> {
					assertThat(context).hasBean("writer");
					JdbcBatchItemWriter writer = context.getBean(JdbcBatchItemWriter.class);
					assertThat(ReflectionTestUtils.getField(writer, "sql")).isEqualTo("INSERT INTO FOO VALUES :one, :two, :three");
					assertThat(ReflectionTestUtils.getField(writer, "usingNamedParameters")).isEqualTo(true);
				});
	}

	@Test
	public void testDefaultContextWithoutNamedParameters() {
		this.contextRunner
				.withUserConfiguration(FlatFileItemReaderAutoConfigurationTests.TestConfiguration.class,
						EmbeddedDataSourceConfiguration.class)
				.withPropertyValues("spring.batch.job.jdbcwriter.sql=INSERT INTO FOO VALUES ?, ?, ?",
						"spring.batch.job.jdbcwriter.names=one,two,three")
				.run((context) -> {
					assertThat(context).hasBean("writer");
					JdbcBatchItemWriter writer = context.getBean(JdbcBatchItemWriter.class);
					assertThat(ReflectionTestUtils.getField(writer, "sql")).isEqualTo("INSERT INTO FOO VALUES ?, ?, ?");
					assertThat(ReflectionTestUtils.getField(writer, "usingNamedParameters")).isEqualTo(false);
					assertThat(ReflectionTestUtils.getField(writer, "itemPreparedStatementSetter")).isInstanceOf(JdbcBatchItemWriterAutoConfiguration.MapPreparedStatementSetter.class);
				});
	}

	@EnableBatchProcessing
	protected static class TestConfiguration {
	}

}
