package com.springvue.app.dao.convert;

import com.springvue.app.dao.convert.support.ConvertConfig;
import com.springvue.app.dao.convert.support.Converter;
import com.springvue.app.dao.model.po.SysEmailPo;
import com.springvue.app.dao.model.vo.SysEmailVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(config = ConvertConfig.class)
public interface SysEmailConverter extends Converter<SysEmailPo, SysEmailVo> {
    SysEmailConverter INSTANCE = Mappers.getMapper(SysEmailConverter.class);
}
