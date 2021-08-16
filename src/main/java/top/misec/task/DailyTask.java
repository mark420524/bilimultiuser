package top.misec.task;

import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j2;
import top.misec.apiquery.ApiList;
import top.misec.config.Config;
import top.misec.config.ConfigUser;
import top.misec.utils.HttpUtil;
import top.misec.utils.SleepTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static top.misec.task.TaskInfoHolder.STATUS_CODE_STR;
import static top.misec.task.TaskInfoHolder.calculateUpgradeDays;

/**
 * @author @JunzhouLiu @Kurenai
 * @create 2020/10/11 20:44
 */
@Log4j2
public class DailyTask {

    private final List<Task> dailyTasks;

    public DailyTask() {
        dailyTasks = new ArrayList<>();
        dailyTasks.add(new VideoWatch());
        dailyTasks.add(new MangaSign());
        dailyTasks.add(new CoinAdd());
        dailyTasks.add(new Silver2coin());
        dailyTasks.add(new LiveCheckin());
        dailyTasks.add(new GiveGift());
        dailyTasks.add(new ChargeMe());
        dailyTasks.add(new GetVipPrivilege());
        Collections.shuffle(dailyTasks);
        dailyTasks.add(0, new UserCheck());
        dailyTasks.add(1, new CoinLogs());
    }

    /**
     * @return jsonObject 返回status对象，包含{"login":true,"watch":true,"coins":50,
     * "share":true,"email":true,"tel":true,"safe_question":true,"identify_card":false}
     * @author @srcrs
     */
    public static JsonObject getDailyTaskStatus(String cookie) {
        JsonObject jsonObject = HttpUtil.doGet(ApiList.reward, cookie);
        int responseCode = jsonObject.get(STATUS_CODE_STR).getAsInt();
        if (responseCode == 0) {
            log.info("请求本日任务完成状态成功");
            return jsonObject.get("data").getAsJsonObject();
        } else {
            log.debug(jsonObject.get("message").getAsString());
            return HttpUtil.doGet(ApiList.reward,cookie).get("data").getAsJsonObject();
            //偶发性请求失败，再请求一次。
        }
    }

    public void doDailyTask() {
        try {
            printTime();
            log.debug("任务启动中");
            Config config = Config.getInstance();
            for (ConfigUser user: config.getUsers()) {
                log.info("开始用户:{}任务",user.getUserId());
                for (Task task : dailyTasks) {
                    log.info("------{}开始------", task.getName());
                    task.run(user);
                    log.info("------{}结束------\n", task.getName());
                    new SleepTime().sleepDefault();
                }
                calculateUpgradeDays(user.getUserCookie());
                log.info("用户:{}任务执行结束",user.getUserId());
            }

            log.info("本日任务已全部执行完毕");

        } catch (Exception e) {
            log.debug(e);
        } finally {
            ServerPush.doServerPush();
        }
    }

    private void printTime() {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(d);
        log.info(time);
    }
}

