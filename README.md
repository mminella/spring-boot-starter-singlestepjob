## Spring Boot Starter For A Single Step Job

This library serves as a starter for declaratively creating a single step Spring Batch Job.  Using
Spring Boot properties, you can configure a Spring Batch Job, with a single step using the supported 
Spring Batch functionality.

Current fuctionality includes the abilty to configure readers and writers via Spring Boot properties (yaml or properties).
You can also provide your own `ItemProcessor` or `Function` implementation to serve as an `ItemProcessor` to be injected
into the step being configured.

### Getting Started

1. Create a new project from Spring Initializr with no dependencies and import it into your favorite IDE
1. Add this library to your pom.xml or build.gradle
	```
	<dependency>
		<groupId>org.springframework.batch</groupId>
		<artifactId>spring-boot-starter-singlestepjob</artifactId>
		<version>0.0.1-SNAPSHOT</version>
	</dependency>
	
	```
	
	```
	classpath 'org.springframework.batc:spring-boot-starter-singlestepjob:0.0.1-SNAPSHOT'
	```
1. In your application.yml file, add the following to configure a job that takes an input CSV and reverses the order of the data.
	```
	spring:
	  batch:
		job:
		  job-name: singleStepJob
		  step-name: singleStep
		  chunk-size: 10
		  filereader:
			resource : <PATH_TO_INPUT_CSV>
			name: inputReader
			names : <LIST_OF_NAMES_FOR_CSV_COLUMNS>
		  filewriter :
			resource : <PATH_TO_OUTPUT_CSV>
			name : outputWriter
			delimiter : ;
			names : <THE_REVERSE_OF_THE_NAMES_ABOVE>
	
	```
	Where the following values are set:
	* _PATH_TO_INPUT_CSV_ - The path to your input file
	* _LIST_OF_NAMES_FOR_CSV_COLUMNS_ - The list of names, one for each column in the CSV.
	* _PATH_TO_OUTPUT_CSV_ - The path to your output file
	* _THE_REVERSE_OF_THE_NAMES_ABOVE_ - The output will be generated via the order of the names you specify here. 
1. Build your project via either `./mvnw clean package` or `./gradlew clean build`
1. Execute your job via `java -jar my-jar.jar`

All regular Spring Batch features apply so things like using an external database for the job repository can be 
accomplished via adding the appropriate JDBC driver, the Spring Boot JDBC driver, and the properties for the database
connection.

