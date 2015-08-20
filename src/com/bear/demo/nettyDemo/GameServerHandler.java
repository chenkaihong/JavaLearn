/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.bear.demo.nettyDemo;

import com.bear.demo.nettyDemo.CmdBox.Cmd;
import com.google.protobuf.MessageLite;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class GameServerHandler extends SimpleChannelInboundHandler<GameMessageTemp> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, GameMessageTemp gameMessageTemp) throws Exception {
    	MessageLite childResponse = MessageIndex.me().process(gameMessageTemp);
    	
    	Cmd cmd = gameMessageTemp.cmd;
    	
    	Cmd response = Cmd.newBuilder().setMessage(cmd.getMessage())
    	                			   .setAppendCode(cmd.getAppendCode())
    	                			   .setAppendData(cmd.getAppendData())
    	                			   .setCompress(cmd.getCompress())
    	                			   .setChildMessage(childResponse.toByteString()).build();
    	
        ctx.write(response);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
