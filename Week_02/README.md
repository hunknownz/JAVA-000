学习笔记

## 作业

### 必做作业一

* 使用`GCLogAnalysis.java`自己演练一遍串行/并行/CMS/G1的案例。

使用如下命令：
```
java -XX:+UseSerialGC -Xms128M -Xmx128M -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:gc.serial.log00 GCLogAnalysis

java -XX:+UseParallelGC -Xms128M -Xmx128M -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:gc.serial.log00 GCLogAnalysis

java -XX:+UseConcMarkSweepGC -Xms128M -Xmx128M -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:gc.serial.log00 GCLogAnalysis

java -XX:+UseG1GC -Xms128M -Xmx128M -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:gc.serial.log00 GCLogAnalysis
```

其中内存分别会设置成`128M`、`256M`、`512M`、`1G`、`2G`、`4G`、`8G`。

整理出如下列表：

业务性能（对象数量）：
| GC/MEM             | 128M |   256M  | 512M    | 1G      | 2G      | 4G      | 8G      |
| ------------------ | ---- | ------- | ------- | ------- | ------- | ------- | ------- |
| UseSerialGC        | OOM  |   4701  | 9600    | 11000   | 10504   | 8476    | 6827    |
| UseParallelGC      | OOM  |   OOM   | 9000    | 13000   | 13468   | 11821   | 7290    |
| UseConcMarkSweepGC | OOM  |   4282  | 10738   | 12571   | 11460   | 11000   | 9300    |
| UseG1GC            | OOM  |   OOM   | 10568   | 14379   | 14188   | 13249   | 13804   |

从表中数据大致观察出，当内存增大到一定程度后，业务性能不会随着内存增大而升高，而是有一定程度下降。从日志分析，内存过小会导致GC次数增多，内存过大会导致单次GC时间增大，对此有一些疑问，想不通为什么导致这种结果，是不是跟测试程序不断在增加对象有关。GC的时间应该跟对象数量有关，不应该跟JVM分配内存有关，这个地方希望老师能解惑一下。

* 使用压测工具（wrk 或 sb），演练 gateway-server-0.0.1-SNAPSHOT.jar 示例。

使用不同GC策略和内存来启动gateway-server，并用JMC进行记录：
```
java -XX:+UseSerialGC -Xms256M -Xmx256M  -jar gateway-server-0.0.1-SNAPSHOT.jar
wrk -t2 -c60 -d60s --latency http://localhost:8088/api/hello

Running 1m test @ http://localhost:8088/api/hello
  2 threads and 60 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    16.41ms   43.28ms 769.25ms   93.01%
    Req/Sec     5.04k     2.44k   15.00k    64.98%
  Latency Distribution
     50%    3.72ms
     75%    9.33ms
     90%   39.28ms
     99%  204.30ms
  591456 requests in 1.00m, 70.61MB read
Requests/sec:   9849.80
Transfer/sec:      1.18MB
```

```
java -XX:+UseSerialGC -Xms512M -Xmx512M  -jar gateway-server-0.0.1-SNAPSHOT.jar
wrk -t2 -c60 -d60s --latency http://localhost:8088/api/hello

Running 1m test @ http://localhost:8088/api/hello
  2 threads and 60 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    12.33ms   32.52ms 686.43ms   92.97%
    Req/Sec     6.71k     3.30k   19.47k    65.00%
  Latency Distribution
     50%    2.79ms
     75%    5.78ms
     90%   26.77ms
     99%  169.77ms
  788090 requests in 1.00m, 94.09MB read
Requests/sec:  13127.73
Transfer/sec:      1.57MB
```

```
java -XX:+UseSerialGC -Xms1G -Xmx1G  -jar gateway-server-0.0.1-SNAPSHOT.jar
wrk -t2 -c60 -d60s --latency http://localhost:8088/api/hello

Running 1m test @ http://localhost:8088/api/hello
  2 threads and 60 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    11.76ms   33.20ms 643.44ms   93.85%
    Req/Sec     7.29k     3.90k   20.81k    61.14%
  Latency Distribution
     50%    2.51ms
     75%    5.28ms
     90%   23.76ms
     99%  182.77ms
  861628 requests in 1.00m, 102.87MB read
Requests/sec:  14354.76
Transfer/sec:      1.71MB
```

```
java -XX:+UseParallelGC -Xms256M -Xmx256M  -jar gateway-server-0.0.1-SNAPSHOT.jar
wrk -t2 -c60 -d60s --latency http://localhost:8088/api/hello

Running 1m test @ http://localhost:8088/api/hello
  2 threads and 60 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    10.88ms   31.45ms 607.33ms   93.55%
    Req/Sec     8.53k     4.27k   21.22k    63.09%
  Latency Distribution
     50%    2.14ms
     75%    4.35ms
     90%   23.17ms
     99%  168.30ms
  1007885 requests in 1.00m, 120.33MB read
Requests/sec:  16774.25
Transfer/sec:      2.00MB
```

```
java -XX:+UseParallelGC -Xms512M -Xmx512M  -jar gateway-server-0.0.1-SNAPSHOT.jar
wrk -t2 -c60 -d60s --latency http://localhost:8088/api/hello

Running 1m test @ http://localhost:8088/api/hello
  2 threads and 60 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    12.14ms   41.26ms 674.96ms   94.55%
    Req/Sec    10.16k     5.31k   20.48k    55.28%
  Latency Distribution
     50%    1.83ms
     75%    3.43ms
     90%   19.03ms
     99%  229.30ms
  1191991 requests in 1.00m, 142.31MB read
Requests/sec:  19842.42
Transfer/sec:      2.37MB
```

```
java -XX:+UseParallelGC -Xms1G -Xmx1G  -jar gateway-server-0.0.1-SNAPSHOT.jar
wrk -t2 -c60 -d60s --latency http://localhost:8088/api/hello

Running 1m test @ http://localhost:8088/api/hello
  2 threads and 60 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    11.73ms   34.82ms 636.24ms   93.89%
    Req/Sec     8.16k     4.43k   22.84k    64.65%
  Latency Distribution
     50%    2.34ms
     75%    4.34ms
     90%   22.49ms
     99%  193.91ms
  967101 requests in 1.00m, 115.46MB read
Requests/sec:  16108.33
Transfer/sec:      1.92MB
```

```
java -XX:+UseConcMarkSweepGC -Xms256M -Xmx256M  -jar gateway-server-0.0.1-SNAPSHOT.jar
wrk -t2 -c60 -d60s --latency http://localhost:8088/api/hello

Running 1m test @ http://localhost:8088/api/hello
  2 threads and 60 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    10.68ms   34.33ms 647.36ms   94.37%
    Req/Sec     9.49k     4.64k   23.21k    58.56%
  Latency Distribution
     50%    1.99ms
     75%    3.56ms
     90%   17.88ms
     99%  188.96ms
  1117240 requests in 1.00m, 133.39MB read
Requests/sec:  18599.04
Transfer/sec:      2.22MB
```

```
java -XX:+UseConcMarkSweepGC -Xms512M -Xmx512M  -jar gateway-server-0.0.1-SNAPSHOT.jar
wrk -t2 -c60 -d60s --latency http://localhost:8088/api/hello

Running 1m test @ http://localhost:8088/api/hello
  2 threads and 60 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    11.44ms   32.75ms 497.29ms   93.75%
    Req/Sec     7.75k     4.12k   19.44k    62.52%
  Latency Distribution
     50%    2.42ms
     75%    4.77ms
     90%   22.03ms
     99%  181.68ms
  915734 requests in 1.00m, 109.33MB read
Requests/sec:  15259.85
Transfer/sec:      1.82MB
```

```
java -XX:+UseConcMarkSweepGC -Xms1G -Xmx1G  -jar gateway-server-0.0.1-SNAPSHOT.jar
wrk -t2 -c60 -d60s --latency http://localhost:8088/api/hello

Running 1m test @ http://localhost:8088/api/hello
  2 threads and 60 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    10.83ms   31.71ms 555.16ms   94.04%
    Req/Sec     8.02k     3.89k   19.51k    65.78%
  Latency Distribution
     50%    2.32ms
     75%    4.45ms
     90%   19.70ms
     99%  176.99ms
  947352 requests in 1.00m, 113.10MB read
Requests/sec:  15767.18
Transfer/sec:      1.88MB
```

```
java -XX:+UseG1GC -Xms256M -Xmx256M  -jar gateway-server-0.0.1-SNAPSHOT.jar
wrk -t2 -c60 -d60s --latency http://localhost:8088/api/hello

Running 1m test @ http://localhost:8088/api/hello
  2 threads and 60 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    11.80ms   34.76ms 567.64ms   93.64%
    Req/Sec     8.22k     3.99k   18.45k    62.95%
  Latency Distribution
     50%    2.32ms
     75%    4.27ms
     90%   23.08ms
     99%  191.53ms
  971899 requests in 1.00m, 116.03MB read
Requests/sec:  16187.55
Transfer/sec:      1.93MB
```

```
java -XX:+UseG1GC -Xms512M -Xmx512M  -jar gateway-server-0.0.1-SNAPSHOT.jar
wrk -t2 -c60 -d60s --latency http://localhost:8088/api/hello

Running 1m test @ http://localhost:8088/api/hello
  2 threads and 60 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    10.19ms   29.82ms 614.63ms   94.22%
    Req/Sec     8.03k     3.96k   23.06k    62.20%
  Latency Distribution
     50%    2.33ms
     75%    4.36ms
     90%   19.07ms
     99%  166.67ms
  948382 requests in 1.00m, 113.23MB read
Requests/sec:  15801.29
Transfer/sec:      1.89MB
```

```
java -XX:+UseG1GC -Xms1G -Xmx1G  -jar gateway-server-0.0.1-SNAPSHOT.jar
wrk -t2 -c60 -d60s --latency http://localhost:8088/api/hello

Running 1m test @ http://localhost:8088/api/hello
  2 threads and 60 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    17.20ms   57.49ms 754.87ms   94.56%
    Req/Sec     6.80k     3.51k   15.63k    59.25%
  Latency Distribution
     50%    2.65ms
     75%    5.94ms
     90%   31.54ms
     99%  303.99ms
  792368 requests in 1.00m, 94.60MB read
Requests/sec:  13193.15
Transfer/sec:      1.58MB
```
总结：

* 串行GC：适合单CPU的Client模式，响应速度敏感
* 并行GC：多CPU环境下和CMS配合使用，吞吐量敏感
* CMS GC和G1 GC：响应速度敏感，适合Web服务使用