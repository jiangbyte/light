package service

import (
	"github.com/jiangbyte/light-gin/domain/template/adapter/repository"
	"github.com/jiangbyte/light-gin/domain/template/model/entity"
)

// XxxService 领域服务占位。
type XxxService struct {
	Repo repository.IXxxRepository
}

// Load 读取实体。
func (s *XxxService) Load(id string) (*entity.XxxEntity, error) {
	// 1. 通过仓储加载
	// 2. 按需做规则校验
	return s.Repo.FindByID(id)
}
