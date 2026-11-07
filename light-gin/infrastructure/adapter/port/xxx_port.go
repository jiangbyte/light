package port

// XxxPort 出站端口实现占位。
type XxxPort struct{}

func (XxxPort) FetchExternal(id string) (string, error) {
	return "external-" + id, nil
}
