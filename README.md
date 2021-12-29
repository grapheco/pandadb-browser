# pandadb-browser

# package
```sh
cd server
mvn clean package

cd ui
npm install
npm run build
```
# test
```sh
cd ui
npm run serve
java -jar ../server/target/pandadb-browser-server-0.0.1.jar
```
Open url: http://localhost:9633
# deploy
```sh
nohup java -jar pandadb-browser-server-0.0.1.jar &

npm install pm2 -g
pm2 start ui/server/bin/www --name=pandadb-browser-ui
```