# MultiMedia


YUV 
planar和packed。
对于planar的YUV格式，先连续存储所有像素点的Y，紧接着存储所有像素点的U，随后是所有像素点的V。
对于packed的YUV格式，每个像素点的Y,U,V是连续交*存储的。



Y 亮度 


YUV420SP  YUV420 ARGB RGB 

https://zh.wikipedia.org/zh/YUV




在自己的线程调用美颜需要初始化一个 glcontext,而在 SurfaceView 的 render 线程 不需要。