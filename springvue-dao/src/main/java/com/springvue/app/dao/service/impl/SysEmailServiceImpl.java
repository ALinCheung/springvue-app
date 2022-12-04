package com.springvue.app.dao.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springvue.app.common.constants.CommonConstant;
import com.springvue.app.dao.convert.SysEmailConverter;
import com.springvue.app.dao.mapper.SysEmailMapper;
import com.springvue.app.dao.model.po.SysEmailPo;
import com.springvue.app.dao.model.po.SysFilePo;
import com.springvue.app.dao.model.vo.SysEmailVo;
import com.springvue.app.dao.service.SysEmailService;
import com.springvue.app.dao.service.SysFileService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @description 针对表【SYS_EMAIL】的数据库操作Service实现
 * @createDate 2022-11-27 12:07:41
 */
@Service
public class SysEmailServiceImpl extends ServiceImpl<SysEmailMapper, SysEmailPo>
        implements SysEmailService {

    @Autowired
    private SysFileServiceImpl sysFileService;

    @Override
    public void save(SysEmailVo vo) {
        // 创建文件
        List<Long> attachmentIds = new ArrayList<>();
        List<String> attachments = vo.getAttachments();
        if (CollectionUtil.isNotEmpty(attachments)) {
            for (String attachment : attachments) {
                if (FileUtil.exist(attachment)) {
                    // 保存文件信息
                    File file = FileUtil.file(attachment);
                    SysFilePo sysFilePo = new SysFilePo();
                    sysFilePo.setFileName(file.getName());
                    sysFilePo.setFilePath(file.getAbsolutePath());
                    sysFilePo.setFileSuffix(FileUtil.getSuffix(file));
                    sysFilePo.setIsTemp(CommonConstant.FALSE);
                    sysFileService.save(sysFilePo);
                    // 获取文件ID列表
                    attachmentIds.add(sysFilePo.getId());
                }
            }
        }
        // 保存邮件
        SysEmailPo sysEmailPo = SysEmailConverter.INSTANCE.toPO(vo);
        sysEmailPo.setAttachments(StringUtils.join(attachmentIds, ","));
        this.save(sysEmailPo);
    }
}




