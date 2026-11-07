package main

import (
	"fmt"
	"log"

	"github.com/gin-gonic/gin"
	triggerportal "github.com/jiangbyte/light-gin/triggers/portal"
	triggerhttp "github.com/jiangbyte/light-gin/triggers/portal/http"
	"github.com/jiangbyte/light-gin/types"
)

func main() {
	r := triggerportal.NewEngine()
	triggerhttp.RegisterXxx(r)
	r.GET("/health", func(c *gin.Context) {
		c.JSON(200, types.Success(map[string]string{"app": "portal"}))
	})
	fmt.Println("应用 'apps/portal' 启动，端口 8080")
	if err := r.Run(":8080"); err != nil {
		log.Fatal(err)
	}
}
