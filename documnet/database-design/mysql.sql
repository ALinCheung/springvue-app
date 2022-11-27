drop table if exists sys_email;

drop table if exists sys_file;

/*==============================================================*/
/* Table: sys_email                                             */
/*==============================================================*/
create table sys_email
(
    id                   int not null comment 'ID',
    message_id           varchar(128) comment '邮件ID',
    sender               varchar(128) comment '发件人',
    receivers            varchar(128) comment '收件人, 用逗号隔开',
    msg_type             int comment '邮件类型1为发送2为接收',
    title                text comment '主题',
    content              text comment '正文',
    attachments          text comment '附件编码, 用逗号隔开',
    create_user          varchar(64) comment '创建人',
    create_time          datetime comment '创建时间',
    primary key (id)
);

-- alter table sys_email comment '邮件管理';

/*==============================================================*/
/* Table: sys_file                                              */
/*==============================================================*/
create table sys_file
(
    id                   int not null comment 'ID',
    file_name            varchar(128) comment '文件名',
    file_suffix          varchar(16) comment '文件后缀',
    file_path            varchar(1024) comment '文件路径',
    is_temp              int default 1 comment '是否临时文件1为是0为否',
    create_user          varchar(64) comment '创建人',
    create_time          datetime comment '创建时间',
    update_user          varchar(64) comment '修改人',
    update_time          datetime comment '修改时间',
    primary key (id)
);

-- alter table sys_file comment '文件管理';
