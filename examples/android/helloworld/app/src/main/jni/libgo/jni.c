/* SPDX-License-Identifier: Apache-2.0
 *
 * Copyright © 2017-2021 Jason A. Donenfeld <Jason@zx2c4.com>. All Rights Reserved.
 */

#include <jni.h>
#include <stdlib.h>
#include <string.h>

struct go_string { const char *str; long n; };
struct go_slice { void *data; long len; long cap; };
struct data { void *ptr; int len; };
extern const char* start();

JNIEXPORT jstring JNICALL Java_io_grpc_helloworldexample_HelloworldActivity_start(JNIEnv *env, jclass c)
{
	jstring ret;
	const char *err = start();
	if (!err)
		return NULL;
	ret = (*env)->NewStringUTF(env, err);
	free((void*)err);
	return ret;
}
