package org.example.todolistandnotebook.backend.utils;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Map;

/**
 * @packageName: org.example.todolistandnotebook.backend.utils
 * @className: Schedule
 * @description: 定时器，定时备份数据库和删除备份数据库文件
 */
@Slf4j
@Component
@EnableScheduling
public class Schedule {

    @Autowired
    private JdbcUtils jdbcUtils;

    @Value("${server.resource}")
    private String resourcePath;

    /**
     * 定时备份数据库信息
     */
    @Scheduled(cron = "59 14 23 * * ?", zone = "Asia/Shanghai")
    public void backUpDataBase() {
        log.info("======执行定时器:定时备份数据库=======");
        String backUpPath = resourcePath + "\\" + Date.valueOf(LocalDate.now());
        File backUpFile = new File(backUpPath);
        if (!backUpFile.exists()) {
            backUpFile.mkdirs();
        }
        File dataFile = new File(backUpPath+"\\campusportal"+System.currentTimeMillis()+".sql");
        //拼接cmd命令
        StringBuilder sb = new StringBuilder();
        Map<String, String> dbInfo = jdbcUtils.getDBInfo();
        sb.append("mysqldump");
        sb.append(" -u").append(dbInfo.get("userName"));
        sb.append(" -p").append(dbInfo.get("passWord"));
        sb.append(" ").append(dbInfo.get("dbName")).append(" > ");
        sb.append(dataFile);
        log.info("======数据库备份cmd命令为：{}=======", sb);
        try {
            Process exec = Runtime.getRuntime().exec("cmd /c "+ sb);
            if (exec.waitFor() == 0){
                log.info("======数据库备份成功，路径为：{}=======", dataFile);
            }
        } catch (Exception e) {
            log.info("======数据库备份失败，异常为：{}=======", e.getMessage());
        }
    }

    /**
     * 定时删除数据库备份文件，只保留最近一个星期
     */
    @Scheduled(cron = "0 15 23 * * ?", zone = "Asia/Shanghai")
    public void deleteBackUpDataBase() {
        log.info("======执行定时器:定时删除备份数据库文件=======");
        String backUpPath = resourcePath;
        File backUpFile = new File(backUpPath);
        if (backUpFile.exists()) {
            File[] files = backUpFile.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    Date date1 = Date.valueOf(file.getName());
                    Date date2 = Date.valueOf(LocalDate.now());
                    long betweenDay = DateUtil.between(date1, date2, DateUnit.DAY);
                    if (betweenDay > 7) {
                        File[] subFiles = file.listFiles();
                        for (File subFile : subFiles) {
                            subFile.delete();
                        }
                        file.delete();
                    }
                }
            }
        }
    }
}

