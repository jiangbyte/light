//! 领域层：按业务边界建模块。
//! 推荐 model/{{entity,valobj,aggregate}}、adapter/{{repository,port}}、service。
//! 新业务复制 template 并改名；Admin/Portal 共用。

pub mod template;
