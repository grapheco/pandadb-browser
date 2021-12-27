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
cd panda-browser-server
nohup java -jar panda-browser-server-0.0.1.jar &

cd panda-browser-ui
pm2 start ./bin/www --name=panda-browser-ui
```


