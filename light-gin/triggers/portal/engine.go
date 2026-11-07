package portal

import (
	"net/http"

	"github.com/gin-gonic/gin"
	"github.com/jiangbyte/light-gin/types"
)

// NewEngine Portal HTTP 引擎（入站适配器）。
func NewEngine() *gin.Engine {
	gin.SetMode(gin.ReleaseMode)
	r := gin.New()
	r.Use(gin.Recovery())
	r.NoRoute(func(c *gin.Context) {
		c.AbortWithStatusJSON(http.StatusNotFound, types.Fail(types.CodeNotFound, "资源不存在"))
	})
	r.NoMethod(func(c *gin.Context) {
		c.AbortWithStatusJSON(http.StatusMethodNotAllowed, types.Fail(types.CodeMethodNotAllowed, "请求方法不支持"))
	})
	return r
}
