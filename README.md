# panda-browser

# package
cd panda-browser-server
mvn clean package

cd panda-browser-ui
npm install
npm install pm2 -g
# start
cd panda-browser-server
java -jar panda-browser-server-0.0.1.jar

cd panda-browser-ui
pm2 start ./bin/www --name=panda-browser-ui


