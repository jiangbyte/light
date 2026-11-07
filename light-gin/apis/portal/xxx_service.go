package portal

import "github.com/jiangbyte/light-gin/apis/portal/dto"

// XxxService 契约占位；由 triggers/portal/http 实现。
type XxxService interface {
	Query(req *dto.XxxRequest) (*dto.XxxResponse, error)
}
