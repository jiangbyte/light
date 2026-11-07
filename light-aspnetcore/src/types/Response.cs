namespace types;

public sealed class Response<T>
{
    public string Code { get; init; } = "0";
    public string Message { get; init; } = "成功";
    public T? Data { get; init; }

    public static Response<T> Success(T? data = default) => new()
    {
        Code = ResponseCode.Success.Code,
        Message = ResponseCode.Success.Info,
        Data = data
    };

    public static Response<T> Fail(string code, string message) => new()
    {
        Code = code,
        Message = message,
        Data = default
    };

    public static Response<T> Fail(ResponseCode code) => Fail(code.Code, code.Info);
}

public sealed record ResponseCode(string Code, string Info)
{
    public static readonly ResponseCode Success = new("0", "成功");
    public static readonly ResponseCode UnError = new("1", "未知失败");
    public static readonly ResponseCode IllegalParameter = new("400", "非法参数");
    public static readonly ResponseCode NotFound = new("404", "资源不存在");
    public static readonly ResponseCode MethodNotAllowed = new("405", "请求方法不支持");
}

public sealed class BizException : Exception
{
    public string Code { get; }
    public int HttpStatus { get; }

    public BizException(string code, string message, int httpStatus = 400) : base(message)
    {
        Code = code;
        HttpStatus = httpStatus > 0 ? httpStatus : 400;
    }
}
