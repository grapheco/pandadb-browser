# pandadb-browser

# package
```sh
cd server
mvn clean package

cd ui
npm install 
```
# start
```sh
nohup java -jar server/target/pandadb-browser-server-0.0.1.jar &

npm run build && npm run serve
```
# deploy
```sh
nohup java -jar server/target/pandadb-browser-server-0.0.1.jar &

npm install pm2 -g
pm2 start ui/server//bin/www --name=pandadb-browser-ui
```