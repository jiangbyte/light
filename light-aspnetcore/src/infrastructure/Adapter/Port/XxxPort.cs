using Domain.Template.Adapter.Port;

namespace Infrastructure.Adapter.Port;

/// <summary>出站端口实现占位。</summary>
public class XxxPort : IXxxPort
{
    public string FetchExternal(string id) => $"external-{id}";
}
