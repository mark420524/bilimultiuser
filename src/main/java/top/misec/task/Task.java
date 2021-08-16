package top.misec.task;

import top.misec.config.ConfigUser;

/**
 * @author @Kurenai
 * @since 2020-11-22 5:22
 */
public interface Task {

    /**
     * 任务实现
     */
    void run(ConfigUser user);

    /**
     * 任务名
     *
     * @return taskName
     */
    String getName();

}