using Domain.Template.Adapter.Repository;
using Domain.Template.Model.Entity;

namespace Infrastructure.Adapter.Repository;

/// <summary>仓储实现占位。</summary>
public class XxxRepository : IXxxRepository
{
    public XxxEntity? FindById(string id) => new() { Id = id };
}
