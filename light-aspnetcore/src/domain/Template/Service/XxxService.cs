using Domain.Template.Adapter.Repository;
using Domain.Template.Model.Entity;

namespace Domain.Template.Service;

/// <summary>领域服务占位。</summary>
public class XxxService
{
    private readonly IXxxRepository _repository;

    public XxxService(IXxxRepository repository) => _repository = repository;

    public XxxEntity? Load(string id)
    {
        // 1. 通过仓储加载
        // 2. 可做跨实体校验
        return _repository.FindById(id);
    }
}
