package com.alinesno.infra.smart.assistant.screen.agent.listener;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alinesno.infra.smart.assistant.api.CodeContent;
import com.alinesno.infra.smart.assistant.api.WorkflowExecutionDto;
import com.alinesno.infra.smart.assistant.screen.agent.bean.WorkerResponseJson;
import com.alinesno.infra.smart.assistant.screen.agent.event.TaskApproveEvent;
import com.alinesno.infra.smart.assistant.screen.agent.event.TaskAssignedEvent;
import com.alinesno.infra.smart.assistant.screen.agent.event.TaskCompletionEvent;
import com.alinesno.infra.smart.assistant.screen.dto.RoleTaskDto;
import com.alinesno.infra.smart.assistant.screen.service.IRoleExecuteService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.im.dto.ChatMessageDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.service.ISSEService;
import com.alinesno.infra.smart.utils.AgentUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 监听工作线程任务的组件
 * 本类主要用于监听和处理工作线程的任务，通过日志记录和异步处理等方式，确保任务的高效和可靠执行
 */
@Slf4j
@Component
public class WorkerTaskListener {

    private final ApplicationEventPublisher publisher;

    @Autowired
    private ISSEService sseService ;

    @Autowired
    private IRoleExecuteService roleExecuteService;

    @Autowired
    private IIndustryRoleService roleService;

    public WorkerTaskListener(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @SneakyThrows
    @EventListener
    public void handleTaskAssigned(TaskAssignedEvent event) {

        RoleTaskDto task = event.getTask() ;
        log.debug("Worker received task: " + task);

        sendSSE(task , "我已经收到内容:" + task.getTaskDesc());

        WorkflowExecutionDto dto = runAgent(task);

        if(dto.getCodeContent() != null && !dto.getCodeContent().isEmpty()){
            CodeContent codeContent = dto.getCodeContent().get(0) ;
            WorkerResponseJson reactResponse = JSON.parseObject(codeContent.getContent(), WorkerResponseJson.class);

            String answer = reactResponse.getAnswer(); // 答案，如果已经获取到答案，则表示任务已经结束

            String thought = reactResponse.getThought();  // 推理思考
            String question = reactResponse.getQuestion(); // 问题咨询

            boolean isCompleted = StringUtils.isNotEmpty(answer) ;

            sendSSE(task , thought + question);

            // --->>>>>  ReAct参数 ---->>>>>
            task.setAnswer(answer);
            task.setThought(thought);
            task.setQuestion(question);

            if(isCompleted){
                publisher.publishEvent(new TaskApproveEvent(this , task));
            }else{
                publisher.publishEvent(new TaskCompletionEvent(this, task , false));
            }

        }

    }

    private WorkflowExecutionDto runAgent(RoleTaskDto task) {

        MessageTaskInfo taskInfo = new MessageTaskInfo() ;

        taskInfo.setRoleId(task.getWorkerRoleId());
        taskInfo.setChannelId(task.getScreenId());
        taskInfo.setScreenId(task.getScreenId());

        taskInfo.setText(task.getThought() + " " + task.getQuestion()); // 输入任务要求
        Map<String, String> params = new HashMap<>();

        // 查询是否包含前一个任务内容
        if(StringUtils.isNotEmpty(task.getPreRoleId())){
            String preContent = roleExecuteService.getPreContent(task.getPreRoleId() , task.getScreenId()) ;

            params.put("label1", preContent);
            taskInfo.setParams(params);
        }

        params.put("question", task.getQuestion());
        params.put("thought", task.getThought());
        taskInfo.setParams(params);

        return roleService.runRoleAgent(taskInfo);
    }

    private void sendSSE(RoleTaskDto task, String msg) throws IOException {
        ChatMessageDto message = AgentUtils.getChatMessageDto(task.getWorkerRole(), IdUtil.getSnowflakeNextId());
        message.setChatText(msg) ;
        message.setRoleType("person");
        message.setLoading(false);
        sseService.send(String.valueOf(task.getScreenId()) , message);
    }
}


//        message = AgentUtils.getChatMessageDto(task.getWorkerRole(), IdUtil.getSnowflakeNextId());
//        message.setLoading(false);
//        message.setRoleType("person");
//        message.setChatText(genContent.getGenContent());
//        sseService.send(String.valueOf(task.getScreenId()) , message);
//
//        // 给出反馈给上线
//        task.setExecuteResult(genContent.getGenContent());
//
//        boolean isCompleted = true ;
//
//        publisher.publishEvent(new TaskCompletionEvent(this, task , isCompleted));