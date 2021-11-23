/* SPDX-License-Identifier: Apache-2.0
 *
 * Copyright © 2017-2021 Jason A. Donenfeld <Jason@zx2c4.com>. All Rights Reserved.
 * Copyright © 2021 Mikael Magnusson. All Rights Reserved.
 */

package main

// #cgo LDFLAGS: -llog
// #include <android/log.h>
import "C"

import (
	"fmt"
	"net"
	"unsafe"
	"os"

	"golang.org/x/sys/unix"

	"google.golang.org/grpc"
	"mikma.github.io/golang/grpc-java/helloworld/gen"
	"mikma.github.io/golang/grpc-java/helloworld/service"
)

const (
	protocol = "unix"
)

var socketDirectory = "/tmp"

type AndroidLogger struct {
	level C.int
	tag   *C.char
}

func cstring(s string) *C.char {
	b, err := unix.BytePtrFromString(s)
	if err != nil {
		b := [1]C.char{}
		return &b[0]
	}
	return (*C.char)(unsafe.Pointer(b))
}

func (l AndroidLogger) Printf(format string, args ...interface{}) {
	C.__android_log_write(l.level, l.tag, cstring(fmt.Sprintf(format, args...)))
}

//export start
func start() *C.char {
	return C.CString(goStart())
}

func goStart() string {
	tag := cstring("helloworld")
	logger := AndroidLogger{level:C.ANDROID_LOG_DEBUG, tag: tag,}

	sockAddr := socketDirectory + "/helloworld.sock"

	{
		if _, err := os.Stat(sockAddr); err == nil {
			if err := os.RemoveAll(sockAddr); err != nil {
				return fmt.Sprintf("Cleanup failed: %v", err)
			}
		}
	}

	listener, err := net.Listen(protocol, sockAddr)
	if err != nil {
		return fmt.Sprintf("Listen failed: %v", err)
	}

	server := grpc.NewServer()
	greeter := service.NewGreeterService()

	gen.RegisterGreeterServer(server, greeter)

	go func() {
		logger.Printf("Enter Serve")
		server.Serve(listener)
		logger.Printf("Exit Serve")
	}()

	return "Success"
}

func main() {}
