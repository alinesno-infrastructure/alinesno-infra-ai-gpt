package com.alinesno.infra.smart.assistant.api;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

// 工作流执行记录表实体类
@Data
public class WorkflowExecutionDto implements Serializable {

    private long id ; // 处理返回结果的id
    private long roleId ;  // 角色ID
    private Integer buildNumber; // 构建次数
    private String workflowName; // 节点名称或标识符
    private long startTime; // 工作流执行开始时间
    private long endTime; // 工作流执行结束时间
    private String status; // 工作流执行状态
    public String usageTimeSeconds ;
    private String logInfo; // 节点执行的日志信息
    private String genContent ; // 节点生成的内容
    private List<CodeContent> codeContent ; // 生成的代码工程列表

}