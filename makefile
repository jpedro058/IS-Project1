compile:
	@mvn clean compile

app: compile
	@mvn exec:java -Dexec.mainClass=example.App

gen: compile
	@mvn exec:java -Dexec.mainClass=example.Generator

chart: compile
	@mvn exec:java -Dexec.mainClass=example.StatisticsChart