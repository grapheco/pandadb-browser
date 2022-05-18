package cn.pandadb.browser.VO;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PandadbConnectionInfo {
    private String pandadbUrl;
    private String host;
    private int port;
    private String cypher;
    private String username;
    private String password;

    public PandadbConnectionInfo(ExecuteCypherVo executeCypherVo) {
        this.pandadbUrl = executeCypherVo.getPandadbUrl();
        this.username = executeCypherVo.getUsername();
        this.password = executeCypherVo.getPassword();
        this.cypher = executeCypherVo.getCypher();
        this.port = Integer.parseInt(pandadbUrl.split(":")[2]);
        this.host = pandadbUrl.split(":")[1].substring(2);
    }

    public String getPandadbUrl() {
        return pandadbUrl;
    }

    public void setPandadbUrl(String pandadbUrl) {
        this.pandadbUrl = pandadbUrl;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getCypher() {
        return cypher;
    }

    public void setCypher(String cypher) {
        this.cypher = cypher;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
