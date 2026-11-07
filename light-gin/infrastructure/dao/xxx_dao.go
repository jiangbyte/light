package dao

import "github.com/jiangbyte/light-gin/infrastructure/dao/po"

// IXxxDao 数据访问占位。
type IXxxDao interface {
	SelectByID(id string) (*po.XxxPO, error)
}
