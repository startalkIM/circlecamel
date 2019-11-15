-- 匿名表
create table camel_anonymous
(
    id   serial primary key  not null,
    anonymous       varchar(50)  not null,
    anonymous_photo varchar(200) not null
);

comment on table camel_anonymous is '匿名表';

comment on column camel_anonymous.id is '自增id';

comment on column camel_anonymous.anonymous is '匿名名字';

comment on column camel_anonymous.anonymous_photo is '匿名头像';

-- 评论表
create table camel_comment
(
    id  serial  primary key    not null,
    comment_uuid             varchar(50)                                                     not null,
    post_uuid                varchar(50)                                                     not null,
    parent_comment_uuid      varchar(50),
    content_text             varchar(5000),
    from_user                varchar(50)                                                     not null,
    to_user                  varchar(50),
    from_host                varchar(50)              default 'ejabhost1'::character varying not null,
    to_host                  varchar(50)              default 'ejabhost1'::character varying not null,
    creat_time               timestamp with time zone default now()                          not null,
    update_time              timestamp with time zone default now()                          not null,
    like_num                 integer                  default 0                              not null,
    review_flag              smallint                 default 2                              not null,
    anonymous                varchar(50),
    anonymous_flag           smallint                 default 0                              not null,
    anonymous_photo          varchar(200),
    delete_flag              smallint                 default 0                              not null,
    to_anonymous_flag        smallint                 default 0                              not null,
    to_anonymous             varchar(50),
    to_anonymous_photo       varchar(200),
    post_owner               varchar(50)              default ''::character varying          not null,
    post_owner_host          varchar(50)              default 'ejabhost1'::character varying not null,
    superparent_comment_uuid varchar(50),
    comment_status           smallint                 default 0                              not null,
    at_list                  varchar(1000)
);

comment on table camel_comment is '评论表';

comment on column camel_comment.id is '自增id';

comment on column camel_comment.comment_uuid is '评论的uuid 32bits';

comment on column camel_comment.post_uuid is '评论所在帖子的uuid';

comment on column camel_comment.parent_comment_uuid is '父评论的uuid';

comment on column camel_comment.content_text is '评论内容';

comment on column camel_comment.from_user is '评论的发起者';

comment on column camel_comment.to_user is '被评论用户';

comment on column camel_comment.from_host is '评论发起者所在域';

comment on column camel_comment.to_host is '被评论用户所在域';

comment on column camel_comment.creat_time is '评论创建时间';

comment on column camel_comment.update_time is '评论更新时间';

comment on column camel_comment.like_num is '评论点赞数';

comment on column camel_comment.review_flag is '评论的审核状态，0未审核；1审核中；2审核通过';

comment on column camel_comment.anonymous is '评论发起者的匿名名字';

comment on column camel_comment.anonymous_flag is '评论是否匿名发布标志，0实名发布；1匿名发布';

comment on column camel_comment.anonymous_photo is '匿名头像';

comment on column camel_comment.delete_flag is '0未删除，1已删除';

comment on column camel_comment.to_anonymous_flag is '被评论人是否匿名';

comment on column camel_comment.to_anonymous is '被评论用户的匿名名字';

comment on column camel_comment.to_anonymous_photo is '被评论用户的匿名头像';

comment on column camel_comment.post_owner is '评论所在帖子的发帖人';

comment on column camel_comment.post_owner_host is '评论所在帖子的发帖人域';

comment on column camel_comment.superparent_comment_uuid is '超级父类的评论uuid';

comment on column camel_comment.comment_status is '主评论状态位,采用位运算.2.0用到了前两位;00评论正常显示,01评论已被删除但需继续显示,10评论已被删除不需继续显示了';

comment on column camel_comment.at_list is 'at用户lists';


create unique index camel_comment_id_uindex on camel_comment (id);

create unique index camel_comment_comment_uuid_uindex on camel_comment (comment_uuid);

create index "camel_comment_post_uuid _uindex" on camel_comment (post_uuid);

create index camel_comment_parent_uindex on camel_comment (parent_comment_uuid);

create index "camel_comment_from_user _uindex"   on camel_comment (from_user);

create index camel_comment_superparent_comment_uuid_index   on camel_comment (superparent_comment_uuid);


-- 时间线表
create table camel_event_timeline
(
    uuid        varchar(50)                               not null,
    event_type  smallint                                  not null,
    create_time timestamp   default now()                 not null,
    from_user   varchar(50)                               not null,
    to_user     varchar(50),
    id          serial                                    not null,
    post_owner  varchar(50) default ''::character varying not null
);

create unique index camel_event_timeline_id_uindex on camel_event_timeline (id);


-- 点赞表
create table camel_like
(
    id serial PRIMARY KEY NOT NULL,
    like_uuid      varchar(50)                                                     not null,
    like_owner     varchar(50)                                                     not null,
    owner_host     varchar(50)              default 'ejabhost1'::character varying not null,
    post_uuid      varchar(50),
    comment_uuid   varchar(50),
    update_time    timestamp with time zone default now()                          not null,
    creat_time     timestamp with time zone default now()                          not null,
    delete_flag    smallint                 default 0                              not null,
    anonymous      smallint,
    anonymous_flag smallint                 default 0                              not null
);

comment on table camel_like is '点赞表';

comment on column camel_like.like_uuid is '点赞事件的uuid';

comment on column camel_like.like_owner is '点赞的发起者';

comment on column camel_like.owner_host is '点赞发起者所在域';

comment on column camel_like.post_uuid is '点赞帖子的uuid';

comment on column camel_like.comment_uuid is '点赞评论的uuid';

comment on column camel_like.update_time is '点赞更新时间';

comment on column camel_like.creat_time is '点赞创建时间';

comment on column camel_like.delete_flag is '点赞删除标志，0未删除，1已删除';

comment on column camel_like.anonymous is '点赞用户匿名名字';

comment on column camel_like.anonymous_flag is '点赞用户是否匿名，0实名，1匿名';


create unique index camel_like_id_uindex    on camel_like (id);

create unique index camel_like_like_uuid_uindex    on camel_like (like_uuid);

create index camel_like_post_uuid_uindex    on camel_like (post_uuid);

create index camel_like_comment_uuid_uindex    on camel_like (comment_uuid);

create index camel_like_like_owner_uindex   on camel_like (like_owner);

-- 消息表
create table camel_message
(
    id serial PRIMARY KEY NOT NULL,
    uuid         varchar(50)                                                     not null,
    event_type   smallint                                                        not null,
    from_user    varchar(50)                                                     not null,
    from_host    varchar(50)              default 'ejabhost1'::character varying not null,
    to_user      varchar(50),
    to_host      varchar(50),
    create_time  timestamp with time zone default now()                          not null,
    content_text varchar(5000),
    flag         smallint                 default 0                              not null,
    post_uuid    varchar(50)                                                     not null,
    entity_id    varchar(50)
);

comment on column camel_message.id is '自增id';

comment on column camel_message.uuid is '消息的uuid';

comment on column camel_message.event_type is '下发消息的事件类型，0发帖；1评论；';

comment on column camel_message.from_user is '事件的发起者';

comment on column camel_message.from_host is '事件的发起者所在域';

comment on column camel_message.to_user is '事件接受者用户名';

comment on column camel_message.to_host is '事件接受者所在域';

comment on column camel_message.create_time is '消息的创建时间';

comment on column camel_message.content_text is '消息内容';

comment on column camel_message.flag is '消息的已读标志，0未读；1已读';

comment on column camel_message.post_uuid is '事件所在的帖子';

comment on column camel_message.entity_id is '事件的uuid';


create unique index camel_message_id_uindex    on camel_message (id);

create unique index camel_message_uuid_uindex   on camel_message (uuid);

create index camel_message_to_user_uindex   on camel_message (to_user);

create index camel_message_from_user_to_user_index   on camel_message (from_user, to_user);

-- 通知配置表
create table camel_notify_config
(
    id serial PRIMARY KEY NOT NULL,
    notify_key     varchar(50)                                                     not null,
    notify_user    varchar(50)                                                     not null,
    host           varchar(50)              default 'ejabhost1'::character varying not null,
    flag           smallint                 default 1                              not null,
    update_version integer                  default 1                              not null,
    update_time    timestamp with time zone default now()                          not null
);

comment on table camel_notify_config is '通知配置表';

comment on column camel_notify_config.id is '自增id';

comment on column camel_notify_config.notify_key is '通知的key';

comment on column camel_notify_config.notify_user is '用户';

comment on column camel_notify_config.host is '域';

comment on column camel_notify_config.flag is '通知开关，1:开 0:关';

comment on column camel_notify_config.update_version is '修改的版本，每次修改都递增';

comment on column camel_notify_config.update_time is '更新时间';

-- 帖子表
create table camel_post
(
    id serial PRIMARY KEY NOT NULL,
    post_uuid       varchar(50)                                                     not null,
    owner_user      varchar(50)                                                     not null,
    creat_time      timestamp with time zone default now()                          not null,
    content_text    varchar(8000),
    update_time     timestamp with time zone default now()                          not null,
    at_list         varchar(1000),
    like_num        integer                  default 0                              not null,
    anonymous       varchar(50),
    anonymous_photo varchar(200),
    comment_num     integer                  default 0                              not null,
    anonymous_flag  smallint                 default 0                              not null,
    delete_flag     smallint                 default 0                              not null,
    review_flag     smallint                 default 2                              not null,
    owner_host      varchar(50)              default 'ejabhost1'::character varying not null,
    post_type       varchar(2)               default 'N'::character varying         not null,
    search_content  varchar(5000)
);

comment on table camel_post is '帖子表';

comment on column camel_post.id is '自增id';

comment on column camel_post.post_uuid is '帖子uuid 32bits';

comment on column camel_post.owner_user is '发帖人user_id';

comment on column camel_post.creat_time is '帖子创建时间';

comment on column camel_post.content_text is '帖子内容';

comment on column camel_post.update_time is '帖子更新时间';

comment on column camel_post.at_list is 'at用户lists';

comment on column camel_post.like_num is '帖子点赞数';

comment on column camel_post.anonymous is '帖子发布者匿名名称';

comment on column camel_post.anonymous_photo is '帖子发布者匿名头像';

comment on column camel_post.comment_num is '帖子评论数';

comment on column camel_post.anonymous_flag is '帖子是否匿名标志，0实名,1匿名';

comment on column camel_post.delete_flag is '删除标记位，0未删除，1已删除';

comment on column camel_post.review_flag is '审核标志位，0为审核 1审核中 2审核通过';

comment on column camel_post.owner_host is '帖子发布者所在域';

comment on column camel_post.post_type is '帖子类型表示符，N正常贴，H热门贴，T置顶贴';

comment on column camel_post.search_content is '搜索的content';



create unique index camel_post_id_uindex   on camel_post (id);

create unique index camel_post_post_uuid_uindex   on camel_post (post_uuid);

create index camel_post_owner_uindex   on camel_post (owner_user);


-- 驼圈用户行为数据统计
create table camel_statistic_data
(
    id serial PRIMARY KEY NOT NULL,
    data_time             timestamp default now() not null,
    active_num            integer   default 0     not null,
    valid_active_num      integer   default 0     not null,
    top_comment_postuuid  varchar(5000),
    top_like_postuuid     varchar(5000),
    post_realname_num     integer   default 0     not null,
    post_total_num        integer   default 0     not null,
    post_anonymous_num    integer   default 0     not null,
    comment_num           integer   default 0     not null,
    comment_realname_num  integer   default 0     not null,
    comment_anonymous_num integer   default 0     not null,
    like_num              integer   default 0     not null,
    brows_time_user       varchar(500),
    post_comments         integer   default 0     not null,
    post_like             integer   default 0     not null
);

comment on table camel_statistic_data is '驼圈用户行为数据统计';

comment on column camel_statistic_data.id is '自增id';

comment on column camel_statistic_data.data_time is '时间';

comment on column camel_statistic_data.active_num is '总日活用户量';

comment on column camel_statistic_data.valid_active_num is '有效日活用户量';

comment on column camel_statistic_data.top_comment_postuuid is '最高回复贴的postUUID';

comment on column camel_statistic_data.top_like_postuuid is '最高点赞帖子 postUUID';

comment on column camel_statistic_data.post_realname_num is '实名发帖量';

comment on column camel_statistic_data.post_total_num is '发帖总量';

comment on column camel_statistic_data.post_anonymous_num is '匿名发帖量';

comment on column camel_statistic_data.comment_num is '评论总量(实名,匿名,总量)';

comment on column camel_statistic_data.comment_realname_num is '实名评论数';

comment on column camel_statistic_data.comment_anonymous_num is '匿名评论数';

comment on column camel_statistic_data.like_num is '点赞量';

comment on column camel_statistic_data.brows_time_user is '不同时间段浏览人数统计';

comment on column camel_statistic_data.post_comments is '最高回复贴的回复数量';

comment on column camel_statistic_data.post_like is '最高点赞贴的点赞数量';



create unique index camel_statistic_data_id_uindex on camel_statistic_data (id);

-- 用户留存数据表
create table camel_user_retain
(    id serial PRIMARY KEY NOT NULL,
    create_time timestamp with time zone default now() not null,
    brow_user   varchar(50)[]                          not null,
    retain_rate varchar(300)                           not null
);

comment on column camel_user_retain.id is '主键自增';

comment on column camel_user_retain.create_time is '时间';

comment on column camel_user_retain.brow_user is '浏览用户id';

comment on column camel_user_retain.retain_rate is '留存率';





