package main

import (
	"fmt"
	"log"

	"github.com/gin-gonic/gin"
	triggeradmin "github.com/jiangbyte/light-gin/triggers/admin"
	triggerhttp "github.com/jiangbyte/light-gin/triggers/admin/http"
	"github.com/jiangbyte/light-gin/types"
)

func main() {
	r := triggeradmin.NewEngine()
	triggerhttp.RegisterXxx(r)
	r.GET("/health", func(c *gin.Context) {
		c.JSON(200, types.Success(map[string]string{"app": "admin"}))
	})
	fmt.Println("应用 'apps/admin' 启动，端口 8081")
	if err := r.Run(":8081"); err != nil {
		log.Fatal(err)
	}
}
