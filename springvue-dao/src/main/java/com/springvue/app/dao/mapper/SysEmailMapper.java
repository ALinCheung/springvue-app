package com.springvue.app.dao.mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.springvue.app.dao.model.po.SysEmailPo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
* 
* @description 针对表【SYS_EMAIL】的数据库操作Mapper
* @createDate 2022-11-27 12:07:41
* @Entity com.springvue.app.dao.model.po.SysEmailPo
*/
public interface SysEmailMapper extends BaseMapper<SysEmailPo> {

    List<SysEmailPo> selectAllByTitle(@Param("title") String title);

}




