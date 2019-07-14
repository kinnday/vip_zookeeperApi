package cn.enjoy.product.zk;

import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * Created by VULCAN on 2018/7/28.
 */
public class ServiceRegisterZkClient {

    private  static final String BASE_SERVICES = "/services";
    private static final String  SERVICE_NAME="/products";

    public static  void register(String address,int port) {
        try {
            ZkClient zkc = new ZkClient("59.110.139.17:2181", 3000);
//            ZooKeeper zooKeeper = new ZooKeeper("localhost:2181",5000,(watchedEvent)->{});
            boolean exists = zkc.exists(BASE_SERVICES + SERVICE_NAME);
            if(!exists ) {
                zkc.createPersistent(BASE_SERVICES + SERVICE_NAME);
//                zkc.create(BASE_SERVICES + SERVICE_NAME,"".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            String server_path = address+":"+port;
            zkc.createEphemeralSequential(BASE_SERVICES + SERVICE_NAME+"/child",server_path);
//            zkc.writeData();
//            zkc.create(BASE_SERVICES + SERVICE_NAME+"/child",server_path.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
