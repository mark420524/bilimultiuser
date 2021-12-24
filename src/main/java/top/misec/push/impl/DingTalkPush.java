package top.misec.push.impl;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import org.apache.commons.codec.binary.Base64;
import top.misec.push.AbstractPush;
import top.misec.push.model.PushMetaInfo;
import top.misec.utils.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;

/**
 * 钉钉机器人
 *
 * @author itning
 * @since 2021/3/22 19:15
 */
public class DingTalkPush extends AbstractPush {
    private String  url = "https://oapi.dingtalk.com/robot/send?access_token=";

    /*

    * */
    private String generatePushUrl(String token, String secret) throws Exception {
        Long timestamp = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();
        String accessToken = token.replace(url, "");
        sb.append(url);
        sb.append(accessToken);
        if (StringUtils.isNotBlank(secret)) {
            sb.append("&timestamp=").append(timestamp);
            sb.append("&sign=").append(generateSign(timestamp, secret));
        }
        return sb.toString();
    }

    private String generateSign(Long timestamp , String secret ) throws  Exception {
        String stringToSign = timestamp + "\n" + secret;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
        byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
        String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)),"UTF-8");
        return sign;
    }

    @Override
    protected String generatePushUrl(PushMetaInfo metaInfo) {
        try {
            return generatePushUrl(metaInfo.getToken(), metaInfo.getChatId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected boolean checkPushStatus(JsonObject jsonObject) {
        if (jsonObject == null) {
            return false;
        }
        JsonElement errcode = jsonObject.get("errcode");
        JsonElement errmsg = jsonObject.get("errmsg");
        if (null == errcode || null == errmsg) {
            return false;
        }
        return errcode.getAsInt() == 0 && "ok".equals(errmsg.getAsString());
    }

    @Override
    protected String generatePushBody(PushMetaInfo metaInfo, String content) {
        return new Gson().toJson(new MessageModel(content));
    }

    @Getter
    static class MessageModel {
        private final String msgtype = "text";
        private final String title = "BILIBILI-HELPER任务简报";
        private final Text text;

        public MessageModel(String content) {
            this.text = new Text(content);
        }
    }

    @Getter
    static class Text {
        private final String content;

        public Text(String content) {
            this.content = content.replaceAll("\r\n\r", "");
        }
    }
}
