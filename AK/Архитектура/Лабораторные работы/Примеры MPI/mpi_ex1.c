/*
 *  ex1.c  --  �ਬ�� 1       �.⠪��  --  http://www.csa.ru/~il/mpi_tutor
 *
 *  ���⥩�� �ਥ����।��:
 *     MPI_Send, MPI_Recv
 *  �����襭�� �� �訡��:
 *     MPI_Abort
 */

#include <mpi.h>

#include <stdio.h>

  /* �����䨪���� ᮮ�饭�� */
#define tagFloatData  1
#define tagDoubleData 2

  /* ��� ����� ������ ��� 㤮��⢠, */
  /* �� �������� 㪠�뢠�� ����� ���ᨢ� � ������⢥ �祥� */ 
#define ELEMS(x)  ( sizeof(x) / sizeof(x[0]) )

int main( int argc, char **argv )
{
    int size, rank, count;
    float floatData[10];
    double doubleData[20];
    MPI_Status status;

      /* ���樠�����㥬 ������⥪� */
    MPI_Init( &argc, &argv );

      /* ������ ������⢮ ����� � ����饭��� �ਫ������ */
    MPI_Comm_size( MPI_COMM_WORLD, &size );

      /* ... � ᢮� ᮡ�⢥��� �����: �� 0 �� (size-1) */
    MPI_Comm_rank( MPI_COMM_WORLD, &rank );

      /* ���짮��⥫� ������ �������� ஢�� ��� �����, ���� �訡�� */
    if( size != 2 ) {

          /* ����� � ����஬ 0 ᮮ�頥� ���짮��⥫� �� �訡�� */
        if( rank==0 )
            printf("Error: two processes required instead of %d, abort\n",
                size );

          /* �� �����-�������� ������ �裡 MPI_COMM_WORLD
           * ���� �����, ���� ����� 0 �� �뢥��� ᮮ�饭��.
           */
        MPI_Barrier( MPI_COMM_WORLD );
         
           /* ��� �窨 ᨭ�஭���樨 ����� ���������, �� ���� �� �����
           * �맮��� MPI_Abort ࠭��, 祬 �ᯥ�� ��ࠡ���� printf()
           * � ����� 0, MPI_Abort ���������� �ਭ㤨⥫쭮 �������
           * �� ����� � ᮮ�饭�� �뢥���� �� �㤥�
           */
          /* �� ����� ���਩�� �������� ࠡ��� */
        MPI_Abort(
            MPI_COMM_WORLD,  /* ����⥫� ������ �裡, �� ������ */
                             /*    �����࠭���� ����⢨� �訡�� */
            MPI_ERR_OTHER ); /* �����᫥��� ��� �訡�� */
        return -1;
    }

    if( rank==0 ) {

     /* ����� 0 ��-� ⠪�� ��।��� ����� 1 */
        MPI_Send(
            floatData,        /* 1) ���� ��।�������� ���ᨢ� */
            5,       /* 2) ᪮�쪮: 5 �祥�, �.�. floatData[0]..floatData[4] */
            MPI_FLOAT,        /* 3) ⨯ �祥� */
            1,                /* 4) ����: ����� 1 */
            tagFloatData,     /* 5) �����䨪��� ᮮ�饭�� */
            MPI_COMM_WORLD ); /* 6) ����⥫� ������ �裡, �१ ������ */
                              /*    �ந�室�� ��।�� */

          /* � �� ���� ��।��: ����� ��㣮�� ⨯� */
        MPI_Send( doubleData, 6, MPI_DOUBLE, 1, tagDoubleData, MPI_COMM_WORLD );

    } else {
      /* ����� 1 ��-� ⠪�� �ਭ����� �� ����� 0 */

          /* ���������� ᮮ�饭�� � ����頥� ��襤訥 ����� � ���� */
        MPI_Recv(
            doubleData,     /* 1) ���� ���ᨢ�, �㤠 ᪫��뢠�� �ਭ�⮥ */
            ELEMS( doubleData ), /* 2) 䠪��᪠� ����� �ਥ����� */
                                 /*    ���ᨢ� � �᫥ �祥� */
            MPI_DOUBLE,     /* 3) ᮮ�頥� MPI, �� ��襤襥 ᮮ�饭�� */
                            /*    ��⮨� �� �ᥫ ⨯� 'double' */
            0,              /* 4) �� ����: �� ����� 0 */
            tagDoubleData,  /* 5) ������� ᮮ�饭�� � ⠪�� �����䨪��஬ */
            MPI_COMM_WORLD, /* 6) ����⥫� ������ �裡, �१ ������ */
                            /*    ��������� ��室 ᮮ�饭�� */
            &status );      /* 7) � �㤥� ����ᠭ ����� �����襭�� �ਥ�� */

          /* ����塞 䠪��᪨ �ਭ�⮥ ������⢮ ������ */
        MPI_Get_count(
            &status,        /* ����� �����襭�� */
            MPI_DOUBLE,     /* ᮮ�頥� MPI, �� ��襤襥 ᮮ�饭�� */
                            /*    ��⮨� �� �ᥫ ⨯� 'double' */
            &count );       /* � �㤥� ����ᠭ १���� */

          /* �뢮��� 䠪����� ����� �ਭ�⮣� �� �࠭ */
        printf("Received %d elems\n", count );

          /* �������筮 �ਭ����� ᮮ�饭�� � ����묨 ⨯� float
           * ����� ��������: �����-�ਥ���� ����� �����������
           * �ਭ����� ᮮ�饭�� �� � ⮬ ���浪�, � ���஬ ���
           * ��ࠢ�﫨��, �᫨ �� ᮮ�饭�� ����� ࠧ�� �����䨪����
           */
        MPI_Recv( floatData, ELEMS( floatData ), MPI_FLOAT,
            0, tagFloatData, MPI_COMM_WORLD, &status );
        MPI_Get_count( &status, MPI_FLOAT, &count );
    }

      /* ��� ����� �������� �믮������ */
    MPI_Finalize();
    return 0;
}
