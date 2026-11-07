package repository

import "github.com/jiangbyte/light-gin/domain/template/model/entity"

// XxxRepository 仓储实现占位。
type XxxRepository struct{}

func (XxxRepository) FindByID(id string) (*entity.XxxEntity, error) {
	return &entity.XxxEntity{ID: id}, nil
}
