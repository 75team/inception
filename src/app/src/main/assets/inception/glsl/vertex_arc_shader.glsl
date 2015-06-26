uniform mat4 u_Matrix;

uniform float radius;
uniform float x;
uniform float y;
attribute vec3 a_Color;
attribute vec2 a_Position;

varying vec4 v_Color;

void main()
{
    v_Color = vec4(a_Color, 1);
    float tx = cos(a_Position.x)*radius+x;
    float ty = sin(degrees(a_Position.x))*radius+y;

    gl_Position = u_Matrix*vec4(tx, ty, 0, 1);//u_Matrix * vec4(a_Position,0,1);
}