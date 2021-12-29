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
nohup java -jar server/target/pandadb-browser-server-0.0.1.jar &
cd ui
npm run serve
```
Open url: http://localhost:9633
# deploy
```sh
nohup java -jar pandadb-browser-server-0.0.1.jar &

npm install pm2 -g
pm2 start ui/server/bin/www --name=pandadb-browser-ui
```