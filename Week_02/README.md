# 一、使用GCLogAnalysis.java 自己演练一遍串行/并行/CMS/G1的案例
## 1 串行GC
### 执行命令：
```
java -Xms512m -Xmx512m -XX:+UseSerialGC -Xloggc:serialGC.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintGCApplicationStoppedTime  wilbur.javaCamp.week02.GCLogAnalysis
```
### 日志分析：


> 2020-10-28T23:22:48.669-0800: 0.213: [GC (Allocation Failure) 2020-10-28T23:22:48.669-0800: 0.213: [DefNew: 139540K->17472K(157248K), 0.0302241 secs] 139540K->43453K(506816K), 0.0303308 secs] [Times: user=0.02 sys=0.01, real=0.03 secs] 
> 
> 2020-10-28T23:22:48.699-0800: 0.243: Total time for which application threads were stopped: 0.0304682 seconds, Stopping threads took: 0.0000219 seconds

日志里只有DefNew年轻代垃圾收集器，表明这是一次minor GC。GC耗时30毫秒。

> 2020-10-28T23:22:49.132-0800: 0.676: [GC (Allocation Failure) 2020-10-28T23:22:49.132-0800: 0.676: [DefNew: 157246K->157246K(157248K), 0.0000199 secs]2020-10-28T23:22:49.132-0800: 0.676: [Tenured: 330307K->277841K(349568K), 0.0607601 secs] 487554K->277841K(506816K), [Metaspace: 2703K->2703K(1056768K)], 0.0608935 secs] [Times: user=0.06 sys=0.00, real=0.06 secs] 
> 
> 2020-10-28T23:22:49.193-0800: 0.737: Total time for which application threads were stopped: 0.0609892 seconds, Stopping threads took: 0.0000127 seconds

GC日志中同时出现DefNew（年轻代垃圾收集器）及Tenured（老年代垃圾收集器）表示这是一次fullGC。GC耗时60毫秒，是上边minor GC耗时的两倍。



## 2 并行GC

### 执行命令：
```
java -Xms512m -Xmx512m -XX:+UseParallelGC -Xloggc:parallelGC.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintGCApplicationStoppedTime wilbur.javaCamp.week02.GCLogAnalysis
```

### 日志分析：

> 2020-10-26T23:35:32.408-0800: 0.168: [GC (Allocation Failure) [PSYoungGen: 131584K->21502K(153088K)] 131584K->42883K(502784K), 0.0193469 secs] [Times: user=0.04 sys=0.08, real=0.02 secs] 
> 2020-10-26T23:35:32.428-0800: 0.188: Total time for which application threads were stopped: 0.0195438 seconds, Stopping threads took: 0.0000212 seconds
> 

PSYoungGen年轻代垃圾收集器，此时young GC耗时19毫秒。年轻代堆清理空间大小为：131584K - 21502K = 110082K，总堆清理的空间大小为：131584K - 42883K = 88701K，表明有 110082K - 88701K = 21381K的对象进入老年代堆。

> 2020-10-26T23:35:32.761-0800: 0.521: [GC (Allocation Failure) [PSYoungGen: 113026K->36685K(116736K)] 364732K->324416K(466432K), 0.0319809 secs] [Times: user=0.03 sys=0.06, real=0.03 secs] 
> 
> 2020-10-26T23:35:32.793-0800: 0.553: [Full GC (Ergonomics) [PSYoungGen: 36685K->0K(116736K)] [ParOldGen: 287731K->233917K(349696K)] 324416K->233917K(466432K), [Metaspace: 2703K->2703K(1056768K)], 0.0363466 secs] [Times: user=0.20 sys=0.01, real=0.03 secs] 
> 
> 2020-10-26T23:35:32.829-0800: 0.589: Total time for which application threads were stopped: 0.0685945 seconds, Stopping threads took: 0.0000218 seconds
> 
上述日志中一次minor GC后紧跟着一次Full GC，可以看到应用线程总的停止时间为两次GC之和68毫秒。


## 3 CMS

### 执行命令如下所示：

```
java -Xms512m -Xmx512m -XX:+UseConcMarkSweepGC -Xloggc:cmsGC.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintGCApplicationStoppedTime  wilbur.javaCamp.week02.GCLogAnalysis
```
### 日志信息及分析如下：


> CommandLine flags: 
> -XX:InitialHeapSize=536870912 
> -XX:MaxHeapSize=536870912 
> -XX:MaxNewSize=178958336 
> -XX:MaxTenuringThreshold=6 
> -XX:NewSize=178958336 
> -XX:OldPLABSize=16 
> -XX:OldSize=357912576 
> -XX:+PrintGC -XX:+PrintGCApplicationStoppedTime -XX:+PrintGCDateStamps -XX:+PrintGCDetails -XX:+PrintGCTimeStamps 
> -XX:+UseCompressedClassPointers 
> -XX:+UseCompressedOops 
> -XX:+UseConcMarkSweepGC -XX:+UseParNewGC //*年轻代GC使用ParNewGC*
> 
> 

参数列表如上图所示，-XX:+UseConcMarkSweepGC 等价于-XX:+UseConcMarkSweepGC -XX:+UseParNewGC，表示年轻代GC使用ParNewGC。

> 2020-10-26T23:39:50.587-0800: 0.179: [GC (Allocation Failure) 2020-10-26T23:39:50.587-0800: 0.179: [ParNew: 139776K->17470K(157248K), 0.0198478 secs] 139776K->41249K(506816K), 0.0199650 secs] [Times: user=0.06 sys=0.08, real=0.02 secs] 
> 
> 2020-10-26T23:39:50.607-0800: 0.199: Total time for which application threads were stopped: 0.0201013 seconds, Stopping threads took: 0.0000134 seconds
> 


第一次GC发生时，GC 表示这是一次Minor GC，ParNew表示使用的垃圾收集器名称。young 区使用量从139776K减少到17470K，总堆使用量从139776K减少到41249K，139776K - 17470K=122306K, 年轻代使用量减少了122306K，139776K - 41249K =98347K, 而总的堆使用量减少了98347K，其差值23959K为从年轻代晋升到老年代的对象占用的空间。此次年轻代GC应用线程停止了20ms。

> ......
> 
> 2020-10-26T23:39:50.819-0800: 0.412: [GC (CMS Initial Mark) [1 CMS-initial-mark: 199241K(349568K)] 217487K(506816K), 0.0002836 secs] [Times: user=0.00 sys=0.00, real=0.01 secs] 
> 
> 2020-10-26T23:39:50.820-0800: 0.412: Total time for which application threads were stopped: 0.0003966 seconds, Stopping threads took: 0.0000589 seconds
> 
> 

初始标记阶段：
标记所有根对象及其直接引用的对象，以及年轻代存活对象所引用的对象。此阶段需要STW暂停，从上述日志可以看到CMS初始标记阶段的执行非常快，应用线程只停止了0.3ms。


> 2020-10-26T23:39:50.820-0800: 0.412: [CMS-concurrent-mark-start]
> 
> 2020-10-26T23:39:50.822-0800: 0.414: [CMS-concurrent-mark: 0.002/0.002 secs] [Times: user=0.01 sys=0.00, real=0.00 secs] 
> 

并发标记阶段：
标记所有的存活对象，从并发标记阶段找到的跟对象开始算起。此阶段GC线程与应用线程并发执行，不需要STW。

> 2020-10-26T23:39:50.822-0800: 0.414: [CMS-concurrent-preclean-start]
> 
> 2020-10-26T23:39:50.822-0800: 0.415: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
> 

并发预清理阶段：
如果并发标记阶段对象的引用关系发生变化，JVM 会通过“Card（卡片）”的方式将发生了改变的区域标记为“脏”区，这就是所谓的卡片标记（Card Marking）。

> 2020-10-26T23:39:50.822-0800: 0.415: [CMS-concurrent-abortable-preclean-start]
> 
......此处略过几次young GC

> 2020-10-26T23:39:51.002-0800: 0.595: [CMS-concurrent-abortable-preclean: 0.003/0.180 secs] [Times: user=0.74 sys=0.08, real=0.18 secs] 
> 

可中断的并发预处理？？？

> 2020-10-26T23:39:51.002-0800: 0.595: [GC (CMS Final Remark) [YG occupancy: 20457 K (157248 K)]2020-10-26T23:39:51.002-0800: 0.595: [Rescan (parallel) , 0.0004350 secs]2020-10-26T23:39:51.003-0800: 0.596: [weak refs processing, 0.0000219 secs]2020-10-26T23:39:51.003-0800: 0.596: [class unloading, 0.0005208 secs]2020-10-26T23:39:51.003-0800: 0.596: [scrub symbol table, 0.0007548 secs]2020-10-26T23:39:51.004-0800: 0.597: [scrub string table, 0.0001731 secs][1 CMS-remark: 337360K(349568K)] 357817K(506816K), 0.0020258 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
> 2020-10-26T23:39:51.004-0800: 0.597: Total time for which application threads were stopped: 0.0021390 seconds, Stopping threads took: 0.0000582 seconds
> 

Final Remark：本阶段的目标是完成老年代中所有存活对象的标记. 因为之前的预清理阶段是并发执行的，有可能GC 线程跟不上应用程序的修改速度。所以需要一次STW 暂停来处理各种复杂的情况。在此阶段应用线程停止了2ms。

> 2020-10-26T23:39:51.005-0800: 0.597: [CMS-concurrent-sweep-start]
> 2020-10-26T23:39:51.006-0800: 0.598: [CMS-concurrent-sweep: 0.001/0.001 secs] [Times: user=0.01 sys=0.00, real=0.00 secs] 
> 

并发清除阶段：清除未被标记的对象

> 2020-10-26T23:39:51.006-0800: 0.598: [CMS-concurrent-reset-start]
> 2020-10-26T23:39:51.007-0800: 0.600: [CMS-concurrent-reset: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
> 

并发重置阶段：重置CMS 算法相关的内部数据，为下一次GC 循环做准备。


> Heap
> 
>  par new generation   total 157248K, used 5787K [0x00000007a0000000, 0x00000007aaaa0000, 0x00000007aaaa0000)
> 
>   eden space 139776K,   4% used [0x00000007a0000000, 0x00000007a05a6db0, 0x00000007a8880000)
> 
>   from space 17472K,   0% used [0x00000007a8880000, 0x00000007a8880000, 0x00000007a9990000)
> 
>   to   space 17472K,   0% used [0x00000007a9990000, 0x00000007a9990000, 0x00000007aaaa0000)
> 
>  concurrent mark-sweep generation total 349568K, used 323611K [0x00000007aaaa0000, 0x00000007c0000000, 0x00000007c0000000)
> 
>  Metaspace       used 2710K, capacity 4486K, committed 4864K, reserved 1056768K
> 
>   class space    used 295K, capacity 386K, committed 512K, reserved 1048576K
> 
> 




## 4 G1

执行如下命令：

```
java -Xms512m -Xmx512m -XX:+UseG1GC -Xloggc:g1GC.log -XX:+PrintGC -XX:+PrintGCDateStamps -XX:+PrintGCApplicationStoppedTime  wilbur.javaCamp.week02.GCLogAnalysis
```

> 
> CommandLine flags: -XX:InitialHeapSize=536870912 -XX:MaxHeapSize=536870912 -XX:+PrintGC -XX:+PrintGCApplicationStoppedTime -XX:+PrintGCDateStamps -XX:+PrintGCTimeStamps -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseG1GC 
> 
> 

参数列表

> 2020-10-27T22:29:16.817-0800: 0.181: [GC pause (G1 Evacuation Pause) (young) 30M->8911K(512M), 0.0029118 secs]
> 2020-10-27T22:29:16.821-0800: 0.184: Total time for which application threads were stopped: 0.0030997 seconds, Stopping threads took: 0.0000110 seconds
> 

1、年轻代模式转移暂停（Evacuation Pause）G1 GC会通过前面一段时间的运行情况来不断的调整自己的回收策略和行为，以此来比较稳定地控制暂停时间。在应用程序刚启动时，G1还没有采集到什么足够的信息，这时候就处于初始的fullyyoung模式。当年轻代空间用满后，应用线程会被暂停，年轻代内存块中的存活对象被拷贝到存活区。如果还没有存活区，则任意选择一部分空闲的内存块作为存活区。拷贝的过程称为转移(Evacuation)，这和前面介绍的其他年轻代收集器是一样的工作原理。

> 2020-10-27T22:29:17.061-0800: 0.424: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 251M->209M(512M), 0.0048667 secs]
> 2020-10-27T22:29:17.066-0800: 0.429: Total time for which application threads were stopped: 0.0049565 seconds, Stopping threads took: 0.0000099 seconds

阶段1: Initial Mark(初始标记)此阶段标记所有从GC根对象直接可达的对象。
> 2020-10-27T22:29:17.066-0800: 0.429: [GC concurrent-root-region-scan-start]
> 2020-10-27T22:29:17.066-0800: 0.429: [GC concurrent-root-region-scan-end, 0.0000865 secs]
> 
> 

阶段2: Root Region Scan(Root区扫描)此阶段标记所有从"根区域" 可达的存活对象。根区域包括：非空的区域，以及在标记过程中不得不收集的区域。

> 2020-10-27T22:29:17.066-0800: 0.429: [GC concurrent-mark-start]
> 2020-10-27T22:29:17.067-0800: 0.431: [GC concurrent-mark-end, 0.0015146 secs]
> 

阶段3: Concurrent Mark(并发标记)此阶段和CMS的并发标记阶段非常类似：只遍历对象图，并在一个特殊的位图中标记能访问到的对象。

> 2020-10-27T22:29:17.067-0800: 0.431: [GC remark, 0.0014195 secs]
> 2020-10-27T22:29:17.069-0800: 0.433: Total time for which application threads were stopped: 0.0014901 seconds, Stopping threads took: 0.0000098 seconds
> 
> 

阶段4: Remark(再次标记)和CMS类似，这是一次STW停顿，以完成标记过程。G1收集器会短暂地停止应用线程，停止并发更新信息的写入，处理其中的少量信息，并标记所有在并发标记开始时未被标记的存活对象。
> 
> 2020-10-27T22:29:17.069-0800: 0.433: [GC cleanup 217M->217M(512M), 0.0006735 secs]
> 2020-10-27T22:29:17.070-0800: 0.433: Total time for which application threads were stopped: 0.0007407 seconds, Stopping threads took: 0.0000354 seconds
> 
> 

阶段5: Cleanup(清理)最后这个清理阶段为即将到来的转移阶段做准备，统计小堆块中所有存活的对象，并将小堆块进行排序，以提升GC的效率，维护并发标记的内部状态。所有不包含存活对象的小堆块在此阶段都被回收了。有一部分任务是并发的：例如空堆区的回收，还有大部分的存活率计算。此阶段也需要一个短暂的STW暂停。
> 
> Heap
>  garbage-first heap   total 524288K, used 353736K [0x00000007a0000000, 0x00000007a0101000, 0x00000007c0000000)
>   region size 1024K, 2 young (2048K), 1 survivors (1024K)
>  Metaspace       used 2710K, capacity 4486K, committed 4864K, reserved 1056768K
>   class space    used 295K, capacity 386K, committed 512K, reserved 1048576K

G1 GC的堆结构不同于之前提到的垃圾回收器，没有固定的年轻代、老年代的概念,堆被划分为多个Region，从上述日志信息可看出region大小为1M。


# 2 使用压测工具（wrk或sb），演练gateway-server-0.0.1-SNAPSHOT.jar 示例

设置堆大小512MB，对比不同GC的压测结果。

### UseSerialGC
```
//执行命令
java -Xms512m -Xmx512m -XX:+UseSerialGC   -jar gateway-server-0.0.1-SNAPSHOT.jar
 
wrk -t8 -c40 -d60s http://localhost:8088/api/hello --latency


//压测结果
  8 threads and 40 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     2.90ms    4.91ms  75.61ms   92.59%
    Req/Sec     2.79k   411.85     5.22k    73.52%
  Latency Distribution
     50%    1.46ms
     75%    2.44ms
     90%    6.18ms
     99%   27.05ms
  1334677 requests in 1.00m, 159.35MB read
Requests/sec:  22237.21
Transfer/sec:      2.65MB
```
### UseParallelGC
```
java -Xms512m -Xmx512m -XX:+UseParallelGC   -jar gateway-server-0.0.1-SNAPSHOT.jar


  8 threads and 40 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     2.59ms    3.84ms  65.59ms   92.16%
    Req/Sec     2.82k   427.02     4.81k    70.65%
  Latency Distribution
     50%    1.46ms
     75%    2.44ms
     90%    5.37ms
     99%   20.55ms
  1348100 requests in 1.00m, 160.95MB read
Requests/sec:  22453.24
Transfer/sec:      2.68MB


```
### UseConcMarkSweepGC
```
java -Xms512m -Xmx512m -XX:+UseConcMarkSweepGC   -jar gateway-server-0.0.1-SNAPSHOT.jar

  8 threads and 40 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     2.77ms    4.12ms  80.66ms   92.02%
    Req/Sec     2.65k   413.78     4.73k    71.04%
  Latency Distribution
     50%    1.55ms
     75%    2.63ms
     90%    5.86ms
     99%   22.03ms
  1265318 requests in 1.00m, 151.07MB read
Requests/sec:  21067.45
Transfer/sec:      2.52MB
```

### UseG1GC
```
java -Xms512m -Xmx512m -XX:+ UseG1GC   -jar gateway-server-0.0.1-SNAPSHOT.jar

  8 threads and 40 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     2.77ms    3.82ms  75.98ms   91.69%
    Req/Sec     2.53k   407.21     4.81k    71.69%
  Latency Distribution
     50%    1.63ms
     75%    2.68ms
     90%    5.79ms
     99%   19.89ms
  1210992 requests in 1.00m, 144.58MB read
Requests/sec:  20166.30
Transfer/sec:      2.41MB
```
### 总结：
对比了相同堆大小下，不同垃圾收集器的压测表现，压测QPS都在20000左右，99%的响应时间也比较接近。由于压测的demo接口无任何业务逻辑，基本上在压测过程中，各个垃圾回收器都不会触发full GC，都是年轻代的minor GC，也就不能体现出各个垃圾回收器的特点。后续可以用真实项目中的接口进行压测尝试。

