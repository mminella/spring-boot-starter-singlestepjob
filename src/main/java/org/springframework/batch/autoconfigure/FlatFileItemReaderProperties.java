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

import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

/**
 * @author Michael Minella
 */
@ConfigurationProperties(prefix = "spring.batch.job.filereader")
public class FlatFileItemReaderProperties {

	private boolean saveState = true;

	private String name;

	private int maxItemCount = Integer.MAX_VALUE;

	private int currentItemCount = 0;

	private List<String> comments = new ArrayList<>();

	//TODO: RecordSeparatorPolicy

	private Resource resource;

	private boolean strict = true;

	private String encoding = FlatFileItemReader.DEFAULT_CHARSET;

	private int linesToSkip = 0;

	//TODO: LineCallbackHandler

	//TODO: LineMapper;

	//TODO: FieldSetMapper

	//TODO: LineTokenizer

	private boolean delimited = false;

	private String delimiter = DelimitedLineTokenizer.DELIMITER_COMMA;

	private char quoteCharacter = DelimitedLineTokenizer.DEFAULT_QUOTE_CHARACTER;

	private List<Integer> includedFields = new ArrayList<>();

	private boolean fixedLength = false;

	private List<Range> ranges = new ArrayList<>();

	private String[] names;

	private boolean parsingStrict = true;

	public boolean isSaveState() {
		return saveState;
	}

	public void setSaveState(boolean saveState) {
		this.saveState = saveState;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMaxItemCount() {
		return maxItemCount;
	}

	public void setMaxItemCount(int maxItemCount) {
		this.maxItemCount = maxItemCount;
	}

	public int getCurrentItemCount() {
		return currentItemCount;
	}

	public void setCurrentItemCount(int currentItemCount) {
		this.currentItemCount = currentItemCount;
	}

	public List<String> getComments() {
		return comments;
	}

	public void setComments(List<String> comments) {
		this.comments = comments;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public boolean isStrict() {
		return strict;
	}

	public void setStrict(boolean strict) {
		this.strict = strict;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public int getLinesToSkip() {
		return linesToSkip;
	}

	public void setLinesToSkip(int linesToSkip) {
		this.linesToSkip = linesToSkip;
	}

	public boolean isDelimited() {
		return delimited;
	}

	public void setDelimited(boolean delimited) {
		this.delimited = delimited;
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public char getQuoteCharacter() {
		return quoteCharacter;
	}

	public void setQuoteCharacter(char quoteCharacter) {
		this.quoteCharacter = quoteCharacter;
	}

	public List<Integer> getIncludedFields() {
		return includedFields;
	}

	public void setIncludedFields(List<Integer> includedFields) {
		this.includedFields = includedFields;
	}

	public boolean isFixedLength() {
		return fixedLength;
	}

	public void setFixedLength(boolean fixedLength) {
		this.fixedLength = fixedLength;
	}

	public List<Range> getRanges() {
		return ranges;
	}

	public void setRanges(List<Range> ranges) {
		this.ranges = ranges;
	}

	public String[] getNames() {
		return names;
	}

	public void setNames(String[] names) {
		this.names = names;
	}

	public boolean isParsingStrict() {
		return parsingStrict;
	}

	public void setParsingStrict(boolean parsingStrict) {
		this.parsingStrict = parsingStrict;
	}
}
