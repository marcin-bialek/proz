jar:
	mkdir -p bin
	mkdir -p out
	find ./src/ -name "*.java" > sources.txt
	javac --release 8 -classpath ./lib/sqlite-jdbc-3.30.1.jar @sources.txt -d ./out/ 
	cp ./lib/sqlite-jdbc-3.30.1.jar ./out/
	cd out; tar xf *.jar; rm *.jar
	cp ./src/resources/* ./out/resources/
	jar cvfm ./bin/Chat.jar ./src/META-INF/MANIFEST.MF -C ./out/ .  
	rm sources.txt

