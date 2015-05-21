attribute vec2 a_Position;  //顶点位置
attribute vec2 a_TexCoor;    //顶点纹理坐标
varying vec2 vTextureCoord;  //用于传递给片元着色器的变量
void main()
{
   gl_Position = vec4(a_Position,0,1); //根据总变换矩阵计算此次绘制此顶点位置
   vTextureCoord = a_TexCoor;//将接收的纹理坐标传递给片元着色器
}