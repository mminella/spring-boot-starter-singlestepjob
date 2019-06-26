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

import org.junit.Test;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.EmbeddedDataSourceConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michael Minella
 */

public class ItemProcessorAutoConfigurationTests {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(BatchAutoConfiguration.class,
					TransactionAutoConfiguration.class,
					FlatFileItemWriterAutoConfiguration.class,
					FlatFileItemReaderAutoConfiguration.class,
					SingleStepAutoConfiguration.class));

	@Test
	public void testNoJobConfigured() {
		this.contextRunner
				.withUserConfiguration(EmptyConfiguration.class,
						EmbeddedDataSourceConfiguration.class)
				.run((context) -> {
					assertThat(context).doesNotHaveBean("itemReader");
					assertThat(context).doesNotHaveBean("itemProcessor");
					assertThat(context).doesNotHaveBean("itemWriter");
					assertThat(context).doesNotHaveBean("job");
				});
	}

	@Test
	public void testJobWithoutItemProcessor() {
		this.contextRunner
				.withUserConfiguration(EmptyConfiguration.class,
						EmbeddedDataSourceConfiguration.class)
				.withPropertyValues("spring.batch.job.job-name=job1",
						"spring.batch.job.step-name=step1",
						"spring.batch.job.chunk-size=2",
						"spring.batch.job.filewriter.resource=file:/Users/mminella/tmp/summaryFile.csv",
						"spring.batch.job.filewriter.name=fooWriter",
						"spring.batch.job.filewriter.delimiter=;",
						"spring.batch.job.filewriter.names=foo,bar,baz",
						"spring.batch.job.filewriter.append=true",
						"spring.batch.job.filereader.resource=file:/Users/mminella/tmp/summaryFile.csv",
						"spring.batch.job.filereader.name=fooReader",
						"spring.batch.job.filereader.names=foo,bar,baz",
						"spring.batch.job.filereader.delimited=true")
				.run((context) -> {
					assertThat(context).hasBean("itemReader");
					assertThat(context).doesNotHaveBean("itemProcessor");
					assertThat(context).hasBean("itemWriter");
					assertThat(context).hasBean("job");
				});
	}

	@Test
	public void testJobWithItemProcessor() {
		this.contextRunner
				.withUserConfiguration(ItemProcessorConfiguration.class,
						EmbeddedDataSourceConfiguration.class)
				.withPropertyValues("spring.batch.job.job-name=job1",
						"spring.batch.job.step-name=step1",
						"spring.batch.job.chunk-size=2",
						"spring.batch.job.filewriter.resource=file:/Users/mminella/tmp/summaryFile.txt",
						"spring.batch.job.filewriter.name=fooWriter",
						"spring.batch.job.filewriter.delimiter=;",
						"spring.batch.job.filewriter.names=baz,foo",
						"spring.batch.job.filewriter.append=true",
						"spring.batch.job.filereader.resource=file:/Users/mminella/tmp/summaryFile.csv",
						"spring.batch.job.filereader.name=fooReader",
						"spring.batch.job.filereader.names=foo,bar",
						"spring.batch.job.filereader.delimited=true",
						"spring.batch.job.itemprocessor=customItemProcessor")
				.run((context) -> {
					assertThat(context).hasBean("itemReader");
					assertThat(context).hasBean("customItemProcessor");
					assertThat(context).hasBean("itemWriter");
					assertThat(context).hasBean("job");

					Job job = context.getBean(Job.class);
					JobLauncher launcher = context.getBean(JobLauncher.class);

					launcher.run(job, new JobParameters());

					CountingItemProcessor processor = context.getBean(CountingItemProcessor.class);
					assertThat(processor.count).isEqualTo(99);
				});
	}

	@Test
	public void testJobWithFunction() {
		this.contextRunner
				.withUserConfiguration(FunctionConfiguration.class,
						EmbeddedDataSourceConfiguration.class)
				.withPropertyValues("spring.batch.job.job-name=job1",
						"spring.batch.job.step-name=step1",
						"spring.batch.job.chunk-size=2",
						"spring.batch.job.filewriter.resource=file:/Users/mminella/tmp/summaryFile.txt",
						"spring.batch.job.filewriter.name=fooWriter",
						"spring.batch.job.filewriter.delimiter=;",
						"spring.batch.job.filewriter.names=baz,foo",
						"spring.batch.job.filewriter.append=true",
						"spring.batch.job.filereader.resource=file:/Users/mminella/tmp/summaryFile.csv",
						"spring.batch.job.filereader.name=fooReader",
						"spring.batch.job.filereader.names=foo,bar",
						"spring.batch.job.filereader.delimited=true",
						"spring.batch.job.itemprocessor=functionItemProcessor")
				.run((context) -> {
					assertThat(context).hasBean("itemReader");
					assertThat(context).hasBean("functionItemProcessor");
					assertThat(context).hasBean("itemWriter");
					assertThat(context).hasBean("job");

					Job job = context.getBean(Job.class);
					JobLauncher launcher = context.getBean(JobLauncher.class);

					launcher.run(job, new JobParameters());

					CountingFunction processor = (CountingFunction) context.getBean(Function.class);
					assertThat(processor.count).isEqualTo(99);
				});
	}

	@Configuration
	@EnableBatchProcessing
	public static class EmptyConfiguration{}

	@Configuration
	@EnableBatchProcessing
	public static class ItemProcessorConfiguration {

		@Bean
		public ItemProcessor<Map<Object, Object>,Map<Object, Object>> customItemProcessor() {
			return new CountingItemProcessor();
		}
	}

	@Configuration
	@EnableBatchProcessing
	public static class FunctionConfiguration {

		@Bean
		public Function<Map<Object, Object>,Map<Object, Object>> functionItemProcessor() {
			return new CountingFunction();
		}
	}

	public static class CountingItemProcessor implements ItemProcessor<Map<Object, Object>, Map<Object, Object>> {

		public int count = 0;

		@Override
		public Map<Object, Object> process(Map<Object, Object> item) {

			count++;
			return item;
		}
	}

	public static class CountingFunction implements Function<Map<Object, Object>, Map<Object, Object>> {

		public int count = 0;

		@Override
		public Map<Object, Object> apply(Map<Object, Object> item) {

			count++;
			return item;
		}
	}

}
