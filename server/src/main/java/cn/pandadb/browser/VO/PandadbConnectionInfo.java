package cn.pandadb.browser.VO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PandadbConnectionInfo {
    private String pandadbUrl;
    private String username;
    private String password;

    public PandadbConnectionInfo(ExecuteCypherVo executeCypherVo) {
        this.pandadbUrl = executeCypherVo.getPandadbUrl();
        this.username = executeCypherVo.getUsername();
        this.password = executeCypherVo.getPassword();
    }
}
