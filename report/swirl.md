## Swirl

The swirl filter applies a distortion effect to an image by rotating its pixels around a central point. This central point serves as the center of rotation for the swirling effect. Each pixel's new position is calculated based on its distance from the central point and a rotation angle determined by a formula.

```java
@Override
public Color apply(int i, int j, Image image) {
    var currentCoordinate = new ImageCoordinate(i, j);
    var centerCoordinate = getCenterCoordinate(image.height(), image.width());
    var swirlCoordinate = getSwirlCoordinateOf(currentCoordinate, centerCoordinate);
    int validX = Math.max(0, Math.min(image.height() - 1, swirlCoordinate.x()));
    int validY = Math.max(0, Math.min(image.width() - 1, swirlCoordinate.y()));
    return image.obtainPixel(validX, validY);
}
```

### Comparison between different image sizes

In this section, the different image processing approaches will be compared with different image sizes.

#### Small

|Benchmark                |Mode|Samples|Score     |Score Error (99.9%)|Unit |
|-------------------------|----|-------|----------|-------------------|-----|
|multithreaded            |avgt|5      |2.555219  |0.038904           |ms/op|
|forkjoin_5000            |avgt|5      |2.633100  |0.040564           |ms/op|
|forkjoin_10000           |avgt|5      |2.717382  |0.025265           |ms/op|
|executorsPerSlice        |avgt|5      |2.762362  |0.079430           |ms/op|
|forkjoin_50000           |avgt|5      |3.053130  |0.013449           |ms/op|
|executorsPerLine         |avgt|5      |3.108516  |0.186301           |ms/op|
|forkjoin_100000          |avgt|5      |4.403993  |0.110388           |ms/op|
|completableFuturePerLine |avgt|5      |9.146927  |0.346656           |ms/op|
|completableFuturePerSlice|avgt|5      |13.260514 |1.949869           |ms/op|
|sequential               |avgt|5      |13.882100 |1.127824           |ms/op|
|completableFuturePerPixel|avgt|5      |63.684018 |0.630354           |ms/op|
|executorsPerPixel        |avgt|5      |391.856028|8.578614           |ms/op|

By looking at the table above for the swirl filter on small images, we can observe the following:

* Multithreaded was the fastest.
* ForkJoin with 5000 pixels threshold was the second fastest.
* ForkJoin with 10000 pixels threshold was the third fastest.
* Sequential was faster than the parallel approaches that send individual pixels to a thread pool.
* The approaches that send individual pixels to a thread pool are bad.
 
#### Big

|Benchmark                |Mode|Samples|Score     |Score Error (99.9%)|Unit |
|-------------------------|----|-------|----------|-------------------|-----|
|executorsPerSlice        |avgt|5      |39.297480 |0.691504           |ms/op|
|forkjoin_10000           |avgt|5      |39.852508 |0.190538           |ms/op|
|forkjoin_5000            |avgt|5      |40.034710 |0.660938           |ms/op|
|forkjoin_100000          |avgt|5      |40.722694 |0.680544           |ms/op|
|forkjoin_50000           |avgt|5      |41.664132 |1.047958           |ms/op|
|multithreaded            |avgt|5      |42.821622 |0.976607           |ms/op|
|executorsPerLine         |avgt|5      |45.263361 |3.054084           |ms/op|
|completableFuturePerLine |avgt|5      |47.362709 |0.341413           |ms/op|
|completableFuturePerSlice|avgt|5      |47.804662 |0.278514           |ms/op|
|sequential               |avgt|5      |329.504866|25.637186          |ms/op|
|completableFuturePerPixel|avgt|5      |1421.079129|31.125093          |ms/op|
|executorsPerPixel        |avgt|5      |8914.503520|128.136955         |ms/op|

By looking at the table above for the swirl filter on big images, we can observe the following:

* Executors per slice was the fastest.
* ForkJoin with 10000 pixels threshold was the second fastest.
* ForkJoin with 5000 pixels threshold was the third fastest.
* Sequential was faster than the parallel approaches that send individual pixels to a thread pool.
* The approaches that send individual pixels to a thread pool are bad.

#### Huge

|Benchmark                |Mode|Samples|Score     |Score Error (99.9%)|Unit |
|-------------------------|----|-------|----------|-------------------|-----|
|forkjoin_10000           |avgt|5      |181.632227|4.922547           |ms/op|
|forkjoin_50000           |avgt|5      |182.363529|6.093872           |ms/op|
|completableFuturePerLine |avgt|5      |183.769407|13.866621          |ms/op|
|forkjoin_100000          |avgt|5      |183.786189|4.055247           |ms/op|
|forkjoin_5000            |avgt|5      |183.846794|1.643841           |ms/op|
|multithreaded            |avgt|5      |186.386704|3.521306           |ms/op|
|executorsPerLine         |avgt|5      |193.013866|6.277138           |ms/op|
|executorsPerSlice        |avgt|5      |259.549501|23.696193          |ms/op|
|completableFuturePerSlice|avgt|5      |261.315559|4.067731           |ms/op|
|sequential               |avgt|5      |1530.844949|13.285406          |ms/op|
|completableFuturePerPixel|avgt|5      |7561.759520|301.488221         |ms/op|
|executorsPerPixel        |avgt|5      |39017.342740|507.487970         |ms/op|

By looking at the table above for the swirl filter on huge images, we can observe the following:

* ForkJoin with 10000 pixels threshold was the fastest.
* ForkJoin with 50000 pixels threshold was the second fastest.
* Completable future per line was the third fastest.
* Sequential was faster than the parallel approaches that send individual pixels to a thread pool.
* The approaches that send individual pixels to a thread pool are bad.

#### Analysis

* **Top 3 most consistent:**
    * **forkjoin_10000:** This approach consistently shows competitive performance across all image sizes (small, big, and huge). It maintains relatively stable execution times and demonstrates efficiency in workload distribution and parallel processing.
    * **forkjoin_5000:** Similar to forkjoin_10000, this approach also exhibits stable performance across different image sizes, indicating its robustness and effectiveness in handling the swirl filter.
    * **forkjoin_100000:** * While forkjoin_100000 may not be the fastest approach for all image sizes, it demonstrates consistency in its performance across different image sizes.
 
* **Best approach:** In this case, **forkjoin_10000** demonstrated the best overall performance across all image sizes.

* **Sequential vs. Parallel:** In all cases, the sequential approach consistently shows higher execution times compared to parallel processing approaches, highlighting the benefits of parallelization for image processing tasks.

### Comparison between different garbage collectors

The following table presents benchmarking results for different garbage collectors (SerialGC, ParallelGC, G1GC, and ZGC) across various approaches for image processing measured in milliseconds per operation/iteration (ms/op).

|Benchmark                |Mode|Samples|SerialGC Score|SerialGC Score Error (99.9%)|ParallelGC Score|ParallelGC Score Error (99.9%)|G1GC Score     |G1GC Score Error (99.9%)                |ZGC Score  |ZGC Score Error (99.9%)|Unit |
|-------------------------|----|-------|--------------|----------------------------|----------------|------------------------------|---------------|----------------------------------------|-----------|-----------------------|-----|
|completableFuturePerLine |avgt|5      |49.729027     |0.585654                    |48.458727       |5.120198                      |45.676361      |0.934989                                |47.363028  |1.230655               |ms/op|
|completableFuturePerPixel|avgt|5      |1761.87497    |217.418671                  |1999.413159     |794.945275                    |1467.96054     |69.990772                               |1640.613932|233.864754             |ms/op|
|completableFuturePerSlice|avgt|5      |49.344501     |1.743271                    |47.679946       |0.045321                      |47.205198      |0.29702                                 |50.001047  |1.177301               |ms/op|
|executorsPerLine         |avgt|5      |58.847752     |10.962439                   |47.922528       |0.887145                      |46.083452      |3.59309                                 |47.732603  |4.108634               |ms/op|
|executorsPerPixel        |avgt|5      |8937.02586    |299.583577                  |8987.96247      |227.84813                     |8861.74583     |110.640062                              |8820.80128 |202.051534             |ms/op|
|executorsPerSlice        |avgt|5      |39.949723     |1.721301                    |39.053135       |0.786265                      |39.593358      |0.639768                                |46.459303  |2.771782               |ms/op|
|forkjoin_10000           |avgt|5      |41.192575     |0.262926                    |36.553203       |0.453867                      |39.900684      |0.442067                                |39.672916  |0.917929               |ms/op|
|forkjoin_100000          |avgt|5      |42.478531     |3.128054                    |36.828155       |0.749449                      |40.807978      |0.290263                                |40.637214  |1.277561               |ms/op|
|forkjoin_5000            |avgt|5      |42.466763     |3.305696                    |35.82708        |0.234499                      |39.714183      |0.27947                                 |39.400354  |0.41761                |ms/op|
|forkjoin_50000           |avgt|5      |43.119594     |3.068442                    |36.658958       |0.307056                      |40.790262      |0.189843                                |40.6103    |0.641799               |ms/op|
|multithreaded            |avgt|5      |40.142681     |1.449503                    |37.811743       |0.762121                      |41.971926      |0.397764                                |42.38104   |0.761993               |ms/op|
|sequential               |avgt|5      |287.570738    |13.202281                   |280.54565       |3.55226                       |324.326504     |3.156532                                |314.149786 |5.124692               |ms/op|

Looking at the score columns for each garbage collector, we can see that the lower the score, the better the performance. So, we need to find the lowest score for each benchmark across all garbage collectors.

completableFuturePerLine:
* SerialGC: 49.729027
* ParallelGC: 48.458727
* G1GC: 45.676361
* ZGC: 47.363028

The lowest score is achieved by G1GC with a score of 45.676361.
 
completableFuturePerPixel:
* SerialGC: 1761.87497
* ParallelGC: 1999.413159
* G1GC: 1467.96054
* ZGC: 1640.613932

The lowest score is achieved by G1GC with a score of 1467.96054.
 
completableFuturePerSlice:
* SerialGC: 49.344501
* ParallelGC: 47.679946
* G1GC: 47.205198
* ZGC: 50.001047

The lowest score is achieved by G1GC with a score of 47.205198.

executorsPerLine:

* SerialGC: 58.847752
* ParallelGC: 47.922528
* G1GC: 46.083452
* ZGC: 47.732603

The lowest score is achieved by G1GC with a score of 46.083452.

executorsPerPixel:
* SerialGC: 8937.02586
* ParallelGC: 8987.96247
* G1GC: 8861.74583
* ZGC: 8820.80128
 
The lowest score is achieved by G1GC with a score of 8861.74583.

executorsPerSlice:
* SerialGC: 39.949723
* ParallelGC: 39.053135
* G1GC: 39.593358
* ZGC: 46.459303
 
The lowest score is achieved by ParallelGC with a score of 39.053135.

forkjoin_10000, forkjoin_100000, forkjoin_5000, forkjoin_50000, multithreaded, and sequential benchmarks:

The scores for these benchmarks are significantly lower for ParallelGC compared to others. However, it's worth noting that G1GC and ZGC also show competitive performance in some cases.

Based on the analysis, **G1GC** generally performs the best across the benchmarks provided. However, it's also important to consider the specific requirements and characteristics of the application when selecting a garbage collector.