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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcParameterUtils;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
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
@EnableConfigurationProperties(JdbcBatchItemWriterProperties.class)
@AutoConfigureAfter(BatchAutoConfiguration.class)
public class JdbcBatchItemWriterAutoConfiguration {

	private final JdbcBatchItemWriterProperties properties;

	public JdbcBatchItemWriterAutoConfiguration(JdbcBatchItemWriterProperties properties) {
		this.properties = properties;
	}

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty(prefix = "spring.batch.job.jdbcwriter", name = "sql")
	public JdbcBatchItemWriter<Map<Object, Object>> writer(DataSource dataSource) {
		JdbcBatchItemWriterBuilder<Map<Object, Object>> mapJdbcBatchItemWriterBuilder = new JdbcBatchItemWriterBuilder<Map<Object, Object>>()
				.sql(this.properties.getSql())
				.dataSource(dataSource)
				.assertUpdates(this.properties.isAssertUpdates());

		List<String> namedParameters = new ArrayList<>();
		JdbcParameterUtils.countParameterPlaceholders(this.properties.getSql(), namedParameters);

		if(namedParameters.isEmpty()) {
			mapJdbcBatchItemWriterBuilder.itemPreparedStatementSetter(new MapPreparedStatementSetter(this.properties.getNames()));
		}

		return mapJdbcBatchItemWriterBuilder.build();
	}

	public static class MapPreparedStatementSetter implements ItemPreparedStatementSetter<Map<Object, Object>> {

		private final String[] names;

		public MapPreparedStatementSetter(String[] names) {
			this.names = names;
		}

		@Override
		public void setValues(Map<Object, Object> item, PreparedStatement ps) throws SQLException {
			for(int i = 0; i < this.names.length; i++) {
				ps.setObject(i++, item.get(this.names[i]));
			}
		}
	}
}
