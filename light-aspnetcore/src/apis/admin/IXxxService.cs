using Apis.Admin.Dto;

namespace Apis.Admin;

/// <summary>管理端契约占位；由 Triggers 实现。</summary>
public interface IXxxService
{
    XxxResponseDto Query(XxxRequestDto request);
}
