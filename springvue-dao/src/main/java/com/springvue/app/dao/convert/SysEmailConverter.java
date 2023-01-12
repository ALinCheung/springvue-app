package com.springvue.app.dao.convert;

import com.springvue.app.dao.convert.support.ConvertConfig;
import com.springvue.app.dao.convert.support.Converter;
import com.springvue.app.dao.model.po.SysEmailPo;
import com.springvue.app.dao.model.vo.SysEmailVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(config = ConvertConfig.class)
public interface SysEmailConverter extends Converter<SysEmailPo, SysEmailVo> {
    SysEmailConverter INSTANCE = Mappers.getMapper(SysEmailConverter.class);

    @Mapping(target = "receivers", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    @Override
    SysEmailPo toPO(SysEmailVo vo);

    @Mapping(target = "receivers", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    @Override
    SysEmailVo toVO(SysEmailPo sysEmailPo);
}
