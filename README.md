# 轻量缓存服务

## 使用

已上传中央仓库，直接拉取

```xml
<dependency>
    <groupId>top.meethigher</groupId>
    <artifactId>cache-store</artifactId>
    <version>1.2</version>
</dependency>
```

或者下载标签为1.2的进行打包即可


```sh
mvn clean install
```

## 功能

* 2022-12-25：1.2版本。新增supply、demand、contains、size方法,采用函数式接口,缓存可监控

* 2022-11-06：1.1版本。解决并发问题，lru支持读取与插入两种触发顺序更新的机制

* 2022-10-27：1.0版本。过期失效、LRU策略