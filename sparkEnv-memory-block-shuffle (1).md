# sparkEnv-memory-block-shuffle源码讲解

## 一、memory相关 --- BlockManager

blockManager一共分为memoryStore,diskStore---说明spark在shuffle的时候数据也是要走磁盘的

spark利用的内存分为堆外内存和堆内内存（堆内内存它会预先留下300M，其他的40%用来存运行时候的元数据，剩下的又分为各种50%，分别为execution，storage两部分，堆外内存也是按照各自50%处理）

## 二、shuffle--ShuffleManager

相对于hadoop来说，spark在Shuffle的阶段利用ShuffleManager来进行一些优化，**最终的目的就是要完成怎样才能最优的计算和拉取上游的数据**



