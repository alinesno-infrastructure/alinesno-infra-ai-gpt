package com.alinesno.infra.smart.assistant.role.tools;

import cn.hutool.core.date.DateUtil;
import com.alinesno.infra.smart.assistant.annotation.ParamInfo;
import com.alinesno.infra.smart.assistant.annotation.ToolInfo;
import com.alinesno.infra.smart.assistant.plugin.tool.Tool;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 获取当前时间(示例工具类)
 */
@ToolInfo(description = "获取当前时间")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class GetTimeTools extends Tool {

    @ParamInfo(name = "dateStr", description = "日期字符串")
    private String dateStr ;

    @Override
    public String execute() {

        // 格式化为 YYYY-MM-DD HH:mm:ss
        String formattedTime = DateUtil.format(DateUtil.date(), "yyyy-MM-dd HH:mm:ss");

        return "当前时间是：" + formattedTime ;
    }
}
