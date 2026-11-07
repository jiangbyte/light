//! 数据访问占位。

pub mod po;


use self::po::XxxPO;

pub trait IXxxDao {
    fn select_by_id(&self, id: &str) -> Option<XxxPO>;
}
