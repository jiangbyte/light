using Domain.Template.Model.Entity;

namespace Domain.Template.Adapter.Repository;

/// <summary>仓储端口占位。</summary>
public interface IXxxRepository
{
    XxxEntity? FindById(string id);
}
