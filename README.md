# 轻量缓存服务

## 使用

已上传中央仓库，直接拉取

```xml
<dependency>
    <groupId>top.meethigher</groupId>
    <artifactId>cache-store</artifactId>
    <version>1.1</version>
</dependency>
```

或者下载标签为1.1的进行打包即可


```sh
mvn clean install
```

## 功能

* 2022-11-06：1.解决并发问题 2.lru支持读取与插入两种触发顺序更新的机制

* 2022-10-27：过期失效、LRU策略