package com.alinesno.infra.smart.assistant.plugin.mapper;

import com.alinesno.infra.common.facade.mapper.repository.IBaseMapper;
import com.alinesno.infra.smart.assistant.entity.ToolEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 应用构建Mapper接口
 * 
 * @version 1.0.0
 * @author luoxiaodong
 */
@Mapper
public interface ToolMapper extends IBaseMapper<ToolEntity> {

}