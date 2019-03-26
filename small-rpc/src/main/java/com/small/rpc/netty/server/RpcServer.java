package com.small.rpc.netty.server;

import com.small.rpc.annotation.RpcService;
import com.small.rpc.netty.RpcRequest;
import com.small.rpc.netty.RpcResponse;
import com.small.rpc.netty.coder.RpcDecoder;
import com.small.rpc.netty.coder.RpcEncoder;
import com.small.rpc.service.ServiceRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RpcServer implements ApplicationContextAware, InitializingBean {

    private String serverAddress;

    private ServiceRegistry serviceRegistry;

    private Map<String, Object> handlerMap = new HashMap<>();

    public RpcServer(String serverAddress){
        this.serverAddress = serverAddress;
    }

    public RpcServer(String serverAddress, ServiceRegistry serviceRegistry){
        this.serverAddress = serverAddress;
        this.serviceRegistry = serviceRegistry;
    }

    public void afterPropertiesSet() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            socketChannel.pipeline()
                                    .addLast(new RpcDecoder(RpcRequest.class))
                                    .addLast(new RpcEncoder(RpcResponse.class))
                                    .addLast(new RpcHandler(handlerMap));
                        }
                    })
            .option(ChannelOption.SO_BACKLOG,128)
            .childOption(ChannelOption.SO_KEEPALIVE,true);

            String[] hostAndPortArr = serverAddress.split(":");
            ChannelFuture channelFuture = serverBootstrap.bind(hostAndPortArr[0],Integer.valueOf(hostAndPortArr[1])).sync();
            log.info("server started on port{}",Integer.valueOf(hostAndPortArr[1]));

            if(serviceRegistry != null){
//                serviceRegistry.register(serverAddress);

                for (String interfaceName : handlerMap.keySet()) {
                    serviceRegistry.register(interfaceName, serverAddress);
                    log.debug("register service: {} => {}", interfaceName, serverAddress);
                }

            }

            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        if(MapUtils.isNotEmpty(serviceBeanMap)){
            serviceBeanMap.forEach((key, value) -> {
                handlerMap.put(value.getClass().getAnnotation(RpcService.class).value().getName(),value);
            });
        }
    }
}
