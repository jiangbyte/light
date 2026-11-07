package repository

import "github.com/jiangbyte/light-gin/domain/template/model/entity"

// IXxxRepository 仓储端口占位。
type IXxxRepository interface {
	FindByID(id string) (*entity.XxxEntity, error)
}
