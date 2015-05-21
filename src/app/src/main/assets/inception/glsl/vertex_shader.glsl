uniform mat4 u_Matrix;

attribute vec2 a_Position;
attribute vec4 a_Color;

varying vec4 v_Color;

void main()
{
    v_Color = a_Color;

    gl_Position = u_Matrix*vec4(a_Position, 0,1);//u_Matrix * vec4(a_Position,0,1);
}