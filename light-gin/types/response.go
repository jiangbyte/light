package types

type Response struct {
	Code    string `json:"code"`
	Message string `json:"message"`
	Data    any    `json:"data"`
}

const (
	CodeSuccess          = "0000"
	CodeUnError          = "0001"
	CodeIllegalParameter = "0400"
	CodeNotFound         = "0404"
	CodeMethodNotAllowed = "0405"
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
