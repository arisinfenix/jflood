all:
	javac -classpath src src/JFlood.java

clean:
	rm -f src/*.class
