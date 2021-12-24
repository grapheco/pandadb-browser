package cn.cnic.pandadb.pandabrowser.VO;

import lombok.Data;

@Data
public class ExecuteCypherVo {
    private String pandadbUrl;
    private String username;
    private String password;
    private String cypher;
}
