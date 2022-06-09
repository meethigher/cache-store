package top.meethigher.cachestore.model;

import java.io.Serializable;

public class CacheWrapper<V> implements Serializable {

    /**
     * 缓存数据
     */
    private V data;

    /**
     * 过期时间
     */
    private Long expireTs;

    /**
     * 创建时间
     */
    private Long createTs;

    public V getData() {
        return data;
    }

    public void setData(V data) {
        this.data = data;
    }

    public Long getExpireTs() {
        return expireTs;
    }

    public void setExpireTs(Long expireTs) {
        this.expireTs = expireTs;
    }

    public Long getCreateTs() {
        return createTs;
    }

    public void setCreateTs(Long createTs) {
        this.createTs = createTs;
    }
}
