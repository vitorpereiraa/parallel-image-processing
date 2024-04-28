## Grayscale 

The grayscale filter converts a color image into a grayscale image by averaging the red, green, and blue components of each pixel and replacing them with the computed average.

### Comparison between different image sizes

In this section, the different image processing approaches will be compared with different image sizes. 

#### Small 

|Benchmark                |Mode|Samples|Score     |Score Error (99.9%)|Unit |
|-------------------------|----|-------|----------|-------------------|-----|
|forkjoin_100000          |avgt|5      |0.934713  |0.028747           |ms/op|
|forkjoin_50000           |avgt|5      |1.008926  |0.015426           |ms/op|
|executorsPerSlice        |avgt|5      |1.020407  |0.018698           |ms/op|
|multithreaded            |avgt|5      |1.337218  |0.037856           |ms/op|
|forkjoin_10000           |avgt|5      |1.339378  |0.028556           |ms/op|
|sequential               |avgt|5      |1.357654  |0.041508           |ms/op|
|forkjoin_5000            |avgt|5      |1.381923  |0.178934           |ms/op|
|executorsPerLine         |avgt|5      |1.393742  |0.160871           |ms/op|
|completableFuturePerLine |avgt|5      |2.480876  |0.348970           |ms/op|
|completableFuturePerSlice|avgt|5      |13.857710 |1.502096           |ms/op|
|completableFuturePerPixel|avgt|5      |40.811620 |1.952428           |ms/op|
|executorsPerPixel        |avgt|5      |395.049808|22.414668          |ms/op|

By looking at the table above for the grayscale filter on small images, we can observe the following:

* ForkJoin with 100000 pixels threshold or recursion stop condition, was the fastest. Given that the image has ~360000 pixels (696x522), the algorithm divided the image in four quadrants(~90000 pixels each) and created one fork for each quadrant, the forks hit the recursion stop condition and executed the filter (fork tree with 1 of height before join). 
 
* ForkJoin with 50000 pixels threshold or recursion stop condition was the second fastest. The only difference between this approach and the first, is that this approach divided the image in 4 quadrants twice, or in other words the height of the fork tree was two before join.
 
* Executors per slice was the third fastest. 

* Sequential was faster than a few parallel approaches.
 
* The approaches that send individual pixels to a thread pool are bad. 

#### Big

|Benchmark                |Mode|Samples|Score     |Score Error (99.9%)|Unit |
|-------------------------|----|-------|----------|-------------------|-----|
|executorsPerSlice        |avgt|5      |15.240269 |0.126980           |ms/op|
|multithreaded            |avgt|5      |15.586608 |0.177745           |ms/op|
|completableFuturePerLine |avgt|5      |17.087186 |0.134427           |ms/op|
|completableFuturePerSlice|avgt|5      |17.127829 |0.031357           |ms/op|
|forkjoin_5000            |avgt|5      |17.366676 |0.220619           |ms/op|
|forkjoin_10000           |avgt|5      |17.382759 |1.483773           |ms/op|
|executorsPerLine         |avgt|5      |17.524300 |0.888894           |ms/op|
|forkjoin_100000          |avgt|5      |17.628387 |0.320718           |ms/op|
|forkjoin_50000           |avgt|5      |17.835574 |0.077437           |ms/op|
|sequential               |avgt|5      |32.687705 |0.899930           |ms/op|
|completableFuturePerPixel|avgt|5      |1092.575795|175.570869         |ms/op|
|executorsPerPixel        |avgt|5      |8796.253290|312.901461         |ms/op|

By looking at the table above for the grayscale filter on big images, we can observe the following:

* Executors per slice is the best. This approach divides the image in horizontal slices according to the number of available processors and then sends each slice to the thread pool to be executed.
 
* Multithreaded is the second best. This approach is basically the same as the approach above regarding the task division, the only difference is that the threads are created to do the exact task as opposed to look for tasks in a queue. 
 
* Completable future per line is the third best.
 
* Sequential was faster than the parallel approaches that send individual pixels to a thread pool.

* The approaches that send individual pixels to a thread pool are bad.

#### Huge

|Benchmark                |Mode|Samples|Score     |Score Error (99.9%)|Unit |
|-------------------------|----|-------|----------|-------------------|-----|
|completableFuturePerLine |avgt|5      |236.841961|15.426141          |ms/op|
|executorsPerSlice        |avgt|5      |237.148008|32.849460          |ms/op|
|completableFuturePerSlice|avgt|5      |240.266058|12.976349          |ms/op|
|multithreaded            |avgt|5      |245.229084|12.045064          |ms/op|
|forkjoin_10000           |avgt|5      |246.728081|33.752774          |ms/op|
|forkjoin_100000          |avgt|5      |247.807731|30.465241          |ms/op|
|forkjoin_50000           |avgt|5      |249.654975|13.180316          |ms/op|
|forkjoin_5000            |avgt|5      |250.966523|21.089158          |ms/op|
|executorsPerLine         |avgt|5      |257.785994|16.719379          |ms/op|
|sequential               |avgt|5      |320.958291|14.450783          |ms/op|
|completableFuturePerPixel|avgt|5      |6041.618260|657.716907         |ms/op|
|executorsPerPixel        |avgt|5      |39645.233020|1312.374901        |ms/op|

By looking at the table above for the grayscale filter on huge images, we can observe the following:

* The score errors are higher, possibly indicating that some interference happened during the benchmark execution. Score errors are calculated based on the score variability between iterations. 

* Completable future per line is the fastest. This algorithm distributes image lines between the fork join common pool worker thread queues. Each thread processes a line, if one thread finishes, it performs work stealing by getting tasks on other worker queue.
 
* Executors per slice is the second fastest. It was the fastest in big images.
 
* Completable future per slice is the third fastest.

* Sequential was faster than the parallel approaches that send individual pixels to a thread pool.

* The approaches that send individual pixels to a thread pool are bad.

#### Conclusion

* **Top 3 most consistent:** 

    * **Executors Per Slice:** This approach exhibits relatively low variance in its scores across different image sizes. The difference in execution times between small, big, and large images is comparatively minimal, indicating consistent performance regardless of image size.

    * **Multithreaded:** While multithreaded may not be the fastest approach for all image sizes, it demonstrates consistency in its performance across different image sizes. The variance in execution times is relatively low, indicating stable performance across varying computational workloads.

    * **CompletableFuture Per Line:** This approach presented the best performance for huge images and the third-best performance for big images.  

* **Best approach:** In this case, **executorsPerSlice** demonstrated the best overall performance across all image sizes.
 
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