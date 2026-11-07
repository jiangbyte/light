package types

type Response struct {
	Code    string `json:"code"`
	Message string `json:"message"`
	Data    any    `json:"data"`
}

const (
	CodeSuccess          = "0"
	CodeUnError          = "1"
	CodeIllegalParameter = "400"
	CodeNotFound         = "404"
	CodeMethodNotAllowed = "405"
)

func Success(data any) Response {
	return Response{Code: CodeSuccess, Message: "成功", Data: data}
}

func Fail(code, message string) Response {
	return Response{Code: code, Message: message, Data: nil}
}

type BizError struct {
	Code       string
	Message    string
	HTTPStatus int
}

func (e *BizError) Error() string { return e.Message }
