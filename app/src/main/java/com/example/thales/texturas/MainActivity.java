package com.example.thales.texturas;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;



class Renderizador implements GLSurfaceView.Renderer
{
    float PosX=300,PosY=500;
    float PosLargura,PosAltura;
    int dir=1,dir2=1;
    int angulo=0;
    int lado=200;
    Activity vrActivity;
    int id;
    Quadrado quadrado = null;


    public Renderizador (Activity vrActivity){

        this.vrActivity = vrActivity;

    }


    @Override
    //será chamado quando o aplicativo for criado, 1 vez só
    public void onSurfaceCreated(GL10 vrOpengl, EGLConfig eglConfig) {

        quadrado = new Quadrado();


    }

    @Override
    //Vai ser chamada quando a superficie mudar
    public void onSurfaceChanged(GL10 vrOpenGL, int largura, int altura) {
        PosLargura=largura;
        PosAltura=altura;

        float[] vetQuadrado={0, 0, 0, lado, lado, lado, lado, 0};

        float[] vetCoordenadas= {0,1, 0,0 , 1,1, 1,0};

        id = carregaTextura(R.mipmap.smile, vrOpenGL);


        FloatBuffer vetTextures = CriaBuffer(vetCoordenadas);


        //Configura a area de visualização utilizada na tela do aparelho
        vrOpenGL.glViewport(0,0,largura,altura);


        //Vai ser chamado sempre que as caracteristicas da superficie mudarem

        //configura a matriz de projeção que define o volume de renderização
        //Criando matriz de projeção
        vrOpenGL.glMatrixMode(GL10.GL_PROJECTION);
        //zerando a matriz (matrix de identidade)
        vrOpenGL.glLoadIdentity();  // carrega a matriz identidade para tirar o lixo da memoria
        //setar volume de renderização
        vrOpenGL.glOrthof(0,largura, 0,altura,1,-1);

        //Setando uma matriz de modelView
        //configura a matriz de cameras e modelo
        vrOpenGL.glMatrixMode(GL10.GL_MODELVIEW);
        vrOpenGL.glLoadIdentity();
        //configura a cor que sera utilizada para limpar o fundo da tela
        vrOpenGL.glClearColor(1.0f,1.0f,1.0f,1.0f);

        //Gera Um VETOR DE VERTICES DO TIPO FLOATEBUFFER
        FloatBuffer buffer= CriaBuffer(vetCoordenadas);

        //habilitar o open gl a receber o array
        //SOLICITAR QUE O OPENGL LIBERE O RCURSO DE ARRAY DE VERTICES
        vrOpenGL.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        //habilita o opengl a
        vrOpenGL.glEnable(GL10.GL_TEXTURE_2D);
        vrOpenGL.glEnableClientState((GL10.GL_TEXTURE_COORD_ARRAY));
        vrOpenGL.glTexCoordPointer(2, GL10.GL_FLOAT, 0, vetTextures);

        //REGISTRAR O VETOR DE VERTICES CRIADO (FLOATBUFFER) NA OPENGL
        vrOpenGL.glVertexPointer(2,GL10.GL_FLOAT,0,buffer);

        //habilita a transparencia da imagem
        vrOpenGL.glEnable(GL10.GL_BLEND);
        vrOpenGL.glEnable(GL10.GL_ALPHA_TEST);
        vrOpenGL.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);


    }

    @Override
    //Esse metodo é o que vai ser chamado a todo momento
    //ele que vai ser chamado.
    public void onDrawFrame(GL10 vrOpengl) {



        vrOpengl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        vrOpengl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0 , 4);

        //vrOpengl.glClearColor((float)Math.random(),(float)Math.random(),(float)Math.random(),1);
        //CONFIGURAR A COR ATUAL DO DESENHO
        //vrOpengl.glColor4f((float) Math.random(), (float) Math.random(), (float) Math.random(), 1.0f);

        vrOpengl.glLoadIdentity();

        vrOpengl.glBindTexture(GL10.GL_TEXTURE_2D,id);

        quadrado.setCor(1,1,1);
        quadrado.setEscala(2.0f);
        quadrado.setXY(150, 400);
        quadrado.desenha(vrOpengl);


        //vrOpengl.glRotatef(angulo,0,0,1);
        //Faz a translação
        //vrOpengl.glTranslatef(PosX, PosY, 0);
        // Manda o OpenGL desenhar o vetor de vertices registrado.
        // vrOpengl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);


        angulo++;

    }

    public int carregaTextura(int codImagem, GL10 vrOpenGL){

        //Cria o vetor responsavel pelo armazenamento do cod da textura
        int[] codTextura = new int[1];

        //Carregar imagem na memoria RAM

        Bitmap vrImage = BitmapFactory.decodeResource(vrActivity.getResources(), codImagem);

        //solicitar a geração do código para a men VRAM
        vrOpenGL.glGenTextures(1, codTextura, 0);

        //aponta a maquina opengl para a memoria a ser trabalhada
        vrOpenGL.glBindTexture(GL10.GL_TEXTURE_2D, codTextura[0]);

        //copiar imagem da ram para a vram
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, vrImage, 0);

        //comfigura os filtros da imagem
        vrOpenGL.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);

        vrOpenGL.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);

        //retira o apontador OpenGl da área de memoria
        vrOpenGL.glBindTexture(GL10.GL_TEXTURE_2D, 0);

        //libera a memoria da imagem na RAM
        vrImage.recycle();

        return codTextura[0];



    }




    public static FloatBuffer CriaBuffer (float[] array)
    {



        //alloc Bytes in memory
        ByteBuffer vrByteBuffer= ByteBuffer.allocateDirect(array.length*4);
        vrByteBuffer.order(ByteOrder.nativeOrder());

        //Create a FloatBuffer
        FloatBuffer vrFloatBuffer= vrByteBuffer.asFloatBuffer();
        vrFloatBuffer.clear();

        //insert a java array into float buffer
        vrFloatBuffer.put(array);
        //reset floatBuffer attribs
        vrFloatBuffer.flip();

        return vrFloatBuffer;
    }
}

public class MainActivity extends AppCompatActivity {
    //Cria uma variavel de referencia para a OpenGL
    GLSurfaceView superficieDesenho=null;
    Renderizador render=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Valida a variavel de referencia com uma instancia da superficie
        superficieDesenho=new GLSurfaceView(this);
        render= new Renderizador(this);
        //Ligando classe
        superficieDesenho.setRenderer(render);
        //Configura a tela do aparelho para mostrar a sup. de desenho
        setContentView(superficieDesenho);
        //IMPRIME UMA MENSAGEM NO LOG COM A TAG FPS E O TEXTO DO 2 PARAMETRO
        Log.i("FPS","Alguma coisa");

    }
}