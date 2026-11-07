using Infrastructure.Dao.Po;

namespace Infrastructure.Dao;

/// <summary>数据访问占位。</summary>
public interface IXxxDao
{
    XxxPO? SelectById(string id);
}
