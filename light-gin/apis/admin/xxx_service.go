package admin

import "github.com/jiangbyte/light-gin/apis/admin/dto"

// XxxService 契约占位；由 triggers/admin/http 实现。
type XxxService interface {
	Query(req *dto.XxxRequest) (*dto.XxxResponse, error)
}
