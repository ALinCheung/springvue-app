package com.springvue.app.dao.service;

import com.springvue.app.dao.model.po.SysEmailPo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.springvue.app.dao.model.vo.SysEmailVo;

/**
* 
* @description 针对表【SYS_EMAIL】的数据库操作Service
* @createDate 2022-11-27 12:07:41
*/
public interface SysEmailService extends IService<SysEmailPo> {

    void save(SysEmailVo vo);
}
