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
import com.bear.demo.nettyDemo.CmdBox.Message;
import com.bear.demo.nettyDemo.CmdBox.PlayerMoveRequest;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Sends a list of continent/city pairs to a {@link WorldClockServer} to
 * get the local times of the specified cities.
 */
public final class GameClient {

    public static void main(String[] args) throws Exception {
    	
    	final String HOST = "127.0.0.1";
    	final int PORT = 8463;

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
             .channel(NioSocketChannel.class)
             .handler(new GameClientConfig());

            Channel ch = b.connect(HOST, PORT).sync().channel();
            
            Message message = Message.newBuilder().setMessageId(10001)
            									  .setServerId(12)
            									  .setPlayerId(12000000).build();
            PlayerMoveRequest request = PlayerMoveRequest.newBuilder().setMoveStep(5).setMoveDirection(30).build();
            
            Cmd cmd = Cmd.newBuilder().setMessage(message).setChildMessage(request.toByteString()).build();

            ChannelFuture future = ch.writeAndFlush(cmd);
            
            future.sync();

            ch.closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}
