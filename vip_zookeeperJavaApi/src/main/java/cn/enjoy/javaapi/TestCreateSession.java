package cn.enjoy.javaapi;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

/**
 * Created by VULCAN on 2018/11/5.
 */
public class TestCreateSession {
    //ZooKeeper服务地址
    private static final String SERVER = "59.110.139.17:2181";

    //会话超时时间
    private final int SESSION_TIMEOUT = 30000;

    @Test
    /**
     * 获得session的方式，这种方式可能会在ZooKeeper还没有获得连接的时候就已经对ZK进行访问了
     */
    public  void testSession1() throws Exception {
        ZooKeeper zooKeeper = new ZooKeeper(SERVER,SESSION_TIMEOUT,null);
        System.out.println(zooKeeper);
        System.out.println(zooKeeper.getState());
//        State:CONNECTING sessionid:0x0 local:null remoteserver:null lastZxid:0 xid:1 sent:0 recv:0 queuedpkts:0 pendingresp:0 queuedevents:0
//        CONNECTING - 不可用的
    }



    //发令枪
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    @Test
    /**
     * 对获得Session的方式进行优化，在ZooKeeper初始化完成以前先等待，等待完成后再进行后续操作
     */
    public  void testSession2() throws  Exception {
        ZooKeeper zooKeeper = new ZooKeeper(SERVER, SESSION_TIMEOUT, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                if(watchedEvent.getState() == Event.KeeperState.SyncConnected) {
                    //确认已经连接完毕后再进行操作
                    countDownLatch.countDown();
                    System.out.println("已经获得了连接");
                }
            }
        });

        //连接完成之前先等待
        countDownLatch.await();
//        已经获得了连接
//          fxc-CONNECTED
        System.out.println(zooKeeper.getState());
    }
}
