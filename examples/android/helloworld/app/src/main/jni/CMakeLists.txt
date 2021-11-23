# SPDX-License-Identifier: Apache-2.0
#
# Copyright © 2017-2021 WireGuard LLC. All Rights Reserved.

cmake_minimum_required(VERSION 3.10)
project(libgo)
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY "${CMAKE_LIBRARY_OUTPUT_DIRECTORY}")

# Work around https://github.com/android-ndk/ndk/issues/602
set(CMAKE_EXE_LINKER_FLAGS "${CMAKE_EXE_LINKER_FLAGS} -fuse-ld=gold")
set(LIB_FILE "${CMAKE_LIBRARY_OUTPUT_DIRECTORY}/libgo.so")

add_custom_command(OUTPUT ${LIB_FILE}
    DEPENDS libgo/api-android.go libgo/jni.c libgo/Makefile
    WORKING_DIRECTORY "${CMAKE_CURRENT_SOURCE_DIR}/libgo"
    COMMENT "Building go jni library"
    VERBATIM
    COMMAND make
    ANDROID_ARCH_NAME=${ANDROID_ARCH_NAME}
    ANDROID_C_COMPILER=${ANDROID_C_COMPILER}
    ANDROID_TOOLCHAIN_ROOT=${ANDROID_TOOLCHAIN_ROOT}
    ANDROID_LLVM_TRIPLE=${ANDROID_LLVM_TRIPLE}
    ANDROID_SYSROOT=${ANDROID_SYSROOT}
    ANDROID_PACKAGE_NAME=${ANDROID_PACKAGE_NAME}
    GRADLE_USER_HOME=${GRADLE_USER_HOME}
    CFLAGS=${CMAKE_C_FLAGS}\ -Wno-unused-command-line-argument
    LDFLAGS=${CMAKE_SHARED_LINKER_FLAGS}\ -fuse-ld=gold
    DESTDIR=${CMAKE_LIBRARY_OUTPUT_DIRECTORY}
    BUILDDIR=${CMAKE_LIBRARY_OUTPUT_DIRECTORY}/../generated-src
)

add_custom_target(make_libgo
    DEPENDS ${LIB_FILE})

# Add dummy library that forces libgo build.
#file(WRITE dummy.c "/* Dummy file used to force libgo build. */int main() { return 0; }\n")
#add_library(libdummy dummy.c)
##add_library(libdummy SHARED)
#add_dependencies(libdummy make_libgo)

# create an library target out of the library compilation result
add_library(libgo_imported SHARED IMPORTED GLOBAL)
#add_dependencies(libgo_imported make_libgo)

# specify where the library is and where to find the headers
set_target_properties(libgo_imported
    PROPERTIES
    IMPORTED_LOCATION ${LIB_FILE})

# Add dummy library that forces libgo build.
file(WRITE dummy.c "/* Dummy file used to force libgo build. */int main() { return 0; }\n")
add_executable(libgo dummy.c)
target_link_libraries(libgo libgo_imported)
