package top.misec;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.misec.org.slf4j.impl.StaticLoggerBinder;
import top.misec.config.Config;
import top.misec.login.ServerVerify;
import top.misec.login.Verify;
import top.misec.task.DailyTask;
import top.misec.task.ServerPush;
import top.misec.utils.VersionInfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;


/**
 * @author Junzhou Liu
 * @create 2020/10/11 2:29
 */

public class BiliMain {
    private static final Logger log;

    static {
        // 如果此标记为true，则为腾讯云函数，使用JUL作为日志输出。
        boolean scfFlag = Boolean.getBoolean("scfFlag");
        StaticLoggerBinder.LOG_IMPL = scfFlag ? StaticLoggerBinder.LogImpl.JUL : StaticLoggerBinder.LogImpl.LOG4J2;
        log = LoggerFactory.getLogger(BiliMain.class);
        InputStream inputStream = BiliMain.class.getResourceAsStream("/logging.properties");
        try {
            LogManager.getLogManager().readConfiguration(inputStream);
        } catch (IOException e) {
            java.util.logging.Logger.getAnonymousLogger().severe("Could not load default logging.properties file");
            java.util.logging.Logger.getAnonymousLogger().severe(e.getMessage());
        }
    }

    public static void main(String[] args) {




        VersionInfo.printVersionInfo();
        //每日任务65经验
        Config.getInstance().configInit();
        if (Config.getInstance().getPushMsg()) {
            ServerVerify.verifyInit(Config.getInstance().getFtKey(),Config.getInstance().getChatId());
        }


        if (!Boolean.TRUE.equals(Config.getInstance().getSkipDailyTask())) {
            DailyTask dailyTask = new DailyTask();
            dailyTask.doDailyTask();
        } else {
            log.info("已开启了跳过本日任务，本日任务跳过（不会发起任何网络请求），如果需要取消跳过，请将skipDailyTask值改为false");
            ServerPush.doServerPush();
        }


    }

    /**
     * 用于腾讯云函数触发
     */
    public   String mainHandler(KeyValueClass ignored) {

        StaticLoggerBinder.LOG_IMPL = StaticLoggerBinder.LogImpl.JUL;
        String json = System.getProperty("config");
        if (null == json) {
            System.out.println("取config配置为空！！！");
            return "config error";
        }

        Config config = Config.getInstance() ;
        try {
            config.configInit(json);
        } catch (JsonSyntaxException e) {
            System.out.println("JSON配置反序列化失败，请检查");
            e.printStackTrace();
            return "config json error";
        }

        VersionInfo.printVersionInfo();
        //每日任务65经验
        if (Config.getInstance().getPushMsg()) {
            ServerVerify.verifyInit(Config.getInstance().getFtKey(),Config.getInstance().getChatId());
        }
        if (!Boolean.TRUE.equals(Config.getInstance().getSkipDailyTask())) {
            DailyTask dailyTask = new DailyTask();
            dailyTask.doDailyTask();
        } else {
            log.info("已开启了跳过当天任务，当天任务跳过（不会发起任何网络请求），如果需要取消跳过，请将skipDailyTask值改为false");
            ServerPush.doServerPush();
        }
        return "SUCCESS";
    }

}
