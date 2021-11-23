package service

import (
	"context"
	"fmt"
	"mikma.github.io/golang/grpc-java/helloworld/gen"
)

type GreeterServiceImpl struct {
	gen.UnimplementedGreeterServer
}

var _ gen.GreeterServer = (*GreeterServiceImpl)(nil)

func NewGreeterService() gen.GreeterServer {
	return new(GreeterServiceImpl)
}

func (e *GreeterServiceImpl) SayHello(ctx context.Context, req *gen.HelloRequest) (*gen.HelloReply, error) {
	r := &gen.HelloReply{
		Message: fmt.Sprintf("Hello %s", req.GetName()),
	}

	return r, nil
}
