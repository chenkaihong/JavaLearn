package com.bear.demo.nettyDemo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.log4j.Logger;

import com.bear.scan.Demos;
import com.bear.scan.Description;
import com.bear.tool.ToolClassFind;
import com.bear.tool.ToolPath;
import com.google.protobuf.MessageLite;

@Description(description="netty + protobuf + 消息分派的完整示例", sort="N")
public final class GameServer implements Demos{

	private final static Logger logger = Logger.getLogger(GameServer.class);
    private final static GameServer me = new GameServer();
    
    private GameServer(){}
    public static GameServer me(){
    	return me;
    }

    public static void main(String[] args) throws Exception {
    	final int PORT = 8463;
    	logger.info("装载配置");
    	GameServer.me().loadConfig("com.bear.demo.nettyDemo");
    	logger.info("启动服务端");
    	GameServer.me().start(PORT, LogLevel.INFO);
    }
    
    public void start(int port, LogLevel logLevel) throws Exception{
    	EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .handler(new LoggingHandler(logLevel))
             .childHandler(new GameServerConifg());

            b.bind(port).sync().channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
    
    @SuppressWarnings("unchecked")
	public void loadConfig(String ...scanPath) throws Exception{
    	List<Class<?>> classes = ToolClassFind.of(GameSystem.class, ToolPath.getRootClassPath()).search();
		for(Class<?> clazz : classes){
			MessageSystem messageSystem = clazz.getAnnotation(MessageSystem.class);
			String systemDes = messageSystem.description();
			// clazz 检验
			
			for(Method method : clazz.getMethods()){
				MessageMothed messageMothed = method.getAnnotation(MessageMothed.class);
				if(messageMothed != null){
					long startTime = System.currentTimeMillis();
					int messageID = messageMothed.messageID();
					String methodDes = messageMothed.description();
					
					// 消息装配
					MessageIndex.me().addMessageProcess(messageMothed.messageID(), clazz, method);
					// 消息参数解码装配
					Class<?>[] parms = method.getParameterTypes();
					if(parms.length != 1){
						throw new RuntimeException(String.format("Message parameter must be one! class[%s], method[%s]",
																											clazz.getName(),
																											method.getName()));
					}
					Class<?> parm = parms[0];
					if(!MessageLite.class.isAssignableFrom(parm)){
						throw new RuntimeException(String.format("Message parameter must be MessageLite! class[%s], method[%s]",
																											clazz.getName(),
																											method.getName()));
					}
					Class<? extends MessageLite> usefulParm = (Class<? extends MessageLite>) parm;
					GameProtobufDecoder.me().mapping(messageID, usefulParm);
					long endTime = System.currentTimeMillis();
					
					logger.info(String.format("[useTime:%dms] Loading %s-%s", (endTime-startTime), systemDes, methodDes));
				}
			}
		}
    }
}
