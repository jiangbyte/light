package http

import (
	"net/http"

	"github.com/gin-gonic/gin"
	apidto "github.com/jiangbyte/light-gin/apis/portal/dto"
	"github.com/jiangbyte/light-gin/types"
)

// RegisterXxx 注册占位示例路由。
func RegisterXxx(r *gin.Engine) {
	g := r.Group("/portal/xxx")
	g.GET("/query", func(c *gin.Context) {
		// 1. 读取查询参数
		req := &apidto.XxxRequest{ID: c.Query("id")}
		// 2. 调用领域（示例直接组装）
		resp := &apidto.XxxResponse{ID: req.ID, Name: "portal-xxx-placeholder"}
		// 3. 写出统一响应
		c.JSON(http.StatusOK, types.Success(resp))
	})
}
