# pandadb-browser

# package
```
cd server
mvn clean package

cd ui
npm install
npm install pm2 -g
```
# start
```
nohup java -jar server/target/pandadb-browser-server-0.0.1.jar &
pm2 start ui/bin/www --name=pandadb-browser-ui
```


