package test.apache.commons.pool2;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultEvictionPolicy;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;

import java.util.Optional;

/**
 * <p> 类描述: TODO
 *
 * @author liqxhx
 * @version TODO
 * @date 2020/03/13 15:52
 * @since 2020/03/13 15:52
 */
public class ObjectPoolTests {
    public static void main(String[] args) {
        GenericObjectPool<Connection> connectionPool = new GenericObjectPool<>(new ConnectionFactory());
        connectionPool.setTestOnBorrow(true);
        connectionPool.setTestOnReturn(true);
        connectionPool.setTestWhileIdle(true);
        connectionPool.setMaxTotal(5);
        connectionPool.setMaxIdle(3);
        connectionPool.setMinIdle(1);
        connectionPool.setEvictionPolicy(new DefaultEvictionPolicy());
        // 驱除对象的定时线程每次校验对象个数
        connectionPool.setNumTestsPerEvictionRun(1);

        try {
            Thread t = new Thread(){
                @Override
                public void run() {
                    Connection conn = null;
                    try {
                        conn = connectionPool.borrowObject();
                        System.out.println(conn+" thread:" + Thread.currentThread().getName());
                        connectionPool.returnObject(conn);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            t.start();
            t.join();

            Connection conn = connectionPool.borrowObject();
            System.out.println(conn +" thread:" + Thread.currentThread().getName());
            connectionPool.returnObject(conn);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Connection {

    boolean ping() {
        return true;
    }

    void close() {
        System.out.println("close");
    }

    void clear() {
        System.out.println("ready to reuse");
    }
}

class ConnectionFactory extends BasePooledObjectFactory<Connection> {
    /**
     * 创建资源
     * @return
     * @throws Exception
     */
    @Override
    public Connection create() throws Exception {
        System.out.println("create");
        return new Connection();
    }

    /**
     * 包装为可维护的对象
     * @param connection
     * @return
     */
    @Override
    public PooledObject<Connection> wrap(Connection connection) {
        System.out.println("wrap to PooledObject<Connection>");
        return new DefaultPooledObject<Connection>(connection);
    }

    /**
     * 销毁对象 关闭资源 能关的都关了
     * @param p
     * @throws Exception
     */
    @Override
    public void destroyObject(PooledObject<Connection> p) throws Exception {
        System.out.println("destroyObject");
        Optional.ofNullable(p.getObject()).ifPresent(conn -> conn.close());
    }

    /**
     *  验证对象
     * @param p
     * @return
     */
    @Override
    public boolean validateObject(PooledObject<Connection> p) {
        System.out.println("validateObject");
        return p.getObject() == null ? false : p.getObject().ping();
    }

    /**
     * 对归还的对象清理
     * @param p
     * @throws Exception
     */
    @Override
    public void passivateObject(PooledObject<Connection> p) throws Exception {
        System.out.println("passivateObject");
        Optional.ofNullable(p.getObject()).ifPresent(conn -> conn.clear());
    }
}