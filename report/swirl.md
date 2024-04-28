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

#### Conclusion

* **Top 3 most consistent:**
    * **forkjoin_10000:** This approach consistently shows competitive performance across all image sizes (small, big, and huge). It maintains relatively stable execution times and demonstrates efficiency in workload distribution and parallel processing.
    * **forkjoin_5000:** Similar to forkjoin_10000, this approach also exhibits stable performance across different image sizes, indicating its robustness and effectiveness in handling the swirl filter.
    * **forkjoin_100000:** * While forkjoin_100000 may not be the fastest approach for all image sizes, it demonstrates consistency in its performance across different image sizes.
 
* **Best approach:** In this case, **forkjoin_10000** demonstrated the best overall performance across all image sizes.

* **Sequential vs. Parallel:** In all cases, the sequential approach consistently shows higher execution times compared to parallel processing approaches, highlighting the benefits of parallelization for image processing tasks.

### Comparison between different garbage collectors

#### SerialGC
[serial table and analysis]
[Compare with big image benchmark]

#### ParallelGC
[parallel table and analysis]
[Compare with big image benchmark]

#### G1GC
[g1 table and analysis]
[Compare with big image benchmark]

#### ZGC
[z table and analysis]
[Compare with big image benchmark]

#### Conclusion
[Elect the best GC]
