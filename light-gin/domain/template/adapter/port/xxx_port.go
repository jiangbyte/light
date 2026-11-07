package port

// IXxxPort 出站端口占位。
type IXxxPort interface {
	FetchExternal(id string) (string, error)
}
