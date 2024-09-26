compile:
	@mvn clean compile

run: compile
	@mvn exec:java -Dexec.mainClass=example.JaxbExampleFruit1

json: compile
	@mvn exec:java -Dexec.mainClass=example.ConvertJson