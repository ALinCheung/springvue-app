package com.springvue.app.dao.convert.support;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.MapperConfig;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@MapperConfig(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface Converter<Po, Vo> {

    Po toPO(Vo vo);

    @InheritInverseConfiguration
    Vo toVO(Po po);

    List<Po> toPOList(List<Vo> voList);

    List<Vo> toVOList(List<Po> poList);
}
