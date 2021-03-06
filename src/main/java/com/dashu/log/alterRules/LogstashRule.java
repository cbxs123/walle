package com.dashu.log.alterRules;

import com.dashu.log.alter.WalleNotify;
import com.dashu.log.monitor.logstash.LogstashDetect;
import com.dashu.log.util.RunCmdLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @Description
 * @Author: xuyouchang
 * @Date 2018/11/26 下午4:59
 **/
public class LogstashRule {
    private static final Logger logger = LoggerFactory.getLogger(LogstashRule.class);

    /**
     * logstash是否存活
     * @param cmd
     * @param hostname
     */
    public void isAliveRule(String cmd,String hostname){
        LogstashDetect logstashDetect = new LogstashDetect(hostname);
        boolean isAlive = logstashDetect.isAlive();
        if (!isAlive){
            logger.warn("the logstash of "+hostname+" is not alive !");
            WalleNotify notify = new WalleNotify();
            notify.sendMessage("logstash down","the logstash of "+hostname+" is not alive !");
            RunCmdLine runCmdLine = new RunCmdLine(this);
            List ret = runCmdLine.callShell(cmd);
            if (ret == null){
                logger.warn("the logstash of "+hostname+" is restart fail,please check it !");
                notify.sendMessage("logstash start fail","the logstash of "+hostname+" is restart fail,please check it !");
            }else{
                logger.info("the logstash of "+hostname+" is up !");
                notify.sendMessage("logstash start up","the logstash of "+hostname+" is up !");
            }
        }else{
            logger.info("the logstash of "+hostname+" is health !");
        }
    }
}
