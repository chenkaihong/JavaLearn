package com.bear.demo.protobuff.tryself5.handle;

import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLite.Builder;

public interface Handle<R extends MessageLite, T extends Builder>{
	void exec(R request, T rspBuilder);
}
