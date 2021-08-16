package top.misec.config;

import lombok.Data;

@Data
public class ConfigUser {
    private String userId;
    private String sessData;
    private String biliJct;

    public String getUserCookie() {
        return "bili_jct=" + getBiliJct() + ";SESSDATA=" + getSessData() + ";DedeUserID=" + getUserId() + ";";
    }
}
