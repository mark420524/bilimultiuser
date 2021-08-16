package top.misec.login;


/**
 * @author Junzhou Liu
 * @create 2020/10/11 16:49
 */
public class Verify {


    private   String userId = "";
    private   String sessData = "";
    private   String biliJct = "";

    public Verify() {

    }

    /**
     * Cookies信息 从浏览器获取
     *
     * @param userId   uid
     * @param sessData sessData
     * @param biliJct  biliJct or CSRF
     */
    public  Verify(String userId, String sessData, String biliJct) {
        this.userId = userId;
        this.sessData = sessData;
        this.biliJct = biliJct;
    }

    /**
     * 合并cookies
     */
    public static void mergeUserCookies(){

    }



    public String getUserId() {
        return userId;
    }

    public String getSessData() {
        return sessData;
    }

    public String getBiliJct() {
        return biliJct;
    }

    public String getVerify() {
        return "bili_jct=" + getBiliJct() + ";SESSDATA=" + getSessData() + ";DedeUserID=" + getUserId() + ";";
    }
}
