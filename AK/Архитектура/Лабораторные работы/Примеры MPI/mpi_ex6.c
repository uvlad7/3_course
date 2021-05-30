
/*
 *  ex6.c  --  �ਬ�� 6       �.⠪��  --  http://www.csa.ru/~il/mpi_tutor
 *
 *  �������� ����㭨����-�㯫����, �������� ࠧ������� ��⮪�� ᮮ�饭��:
 *     MPI_Comm_dup, MPI_Comm_free
 *
 *  ��������� ���室 � ��ࠡ�⪥ �訡�� �१
 *     MPI_Abort, MPI_Barrier
 */

#include <mpi.h>
#include <stdio.h>

#define  tag1  1
#define  tag2  2
#define  tag3  1   /* ᮧ��⥫쭠� "�訡��": �����䨪��� ࠢ�� 'tag1' */

#define  ELEMS(x)  (sizeof( x )/sizeof( x[0] ))

int main( int argc, char **argv )
{
    MPI_Comm myComm;
    int rank, size;

    MPI_Init( &argc, &argv );
    MPI_Comm_size( MPI_COMM_WORLD, &size );
    MPI_Comm_rank( MPI_COMM_WORLD, &rank );

      /* ��ࠡ�⪠ �訡�� "����୮� ������⢮ ����饭��� �����" */
    if( size != 2 && rank==0 ) {
        printf( "ERROR: 2 processes required instead of %d, abort.\n", size );
        MPI_Abort( MPI_COMM_WORLD, MPI_ERR_OTHER );
    }
      /* ����� �㦥�, �⮡� � ��砥 �訡��, ���� ���� 0 ࠯�����
       * � ��� � ��뢠�� MPI_Abort, ��⠫�� ��⢨ �� ᬮ��� ����㯨��
       * � �믮������ ᮤ�ঠ⥫쭮� ��� �ணࠬ��.
       */
    MPI_Barrier( MPI_COMM_WORLD );

      /**  ���� ���� �����祭�, ��稭����� ᮤ�ঠ⥫쭠� ����... **/

      /* ������� �� ���� ����㭨���� - ����� MPI_COMM_WORLD */
    MPI_Comm_dup( MPI_COMM_WORLD, &myComm );

    if( rank == 0 )   /*  ���� 0 ��।���  */
    {
        static char buf1[] = "Contents of first message";
        static char buf2[] = "Contents of second message";
        static char buf3[] = "Contents of third message";

          /* ����� ��������: ��� ��� ᮮ�饭�� ��࠭ ���� � �� ��
           * �����䨪���, ����⥫� ������ �裡 ࠧ��
           */  
        MPI_Send( buf1, ELEMS(buf1), MPI_CHAR, 1, tag1, myComm );
        MPI_Send( buf2, ELEMS(buf2), MPI_CHAR, 1, tag2, MPI_COMM_WORLD );
        MPI_Send( buf3, ELEMS(buf3), MPI_CHAR, 1, tag3, MPI_COMM_WORLD );
    }
    else     /*  ���� 1 �ਭ�����  */
    {
        char buf1[100], buf2[100], buf3[100];
        MPI_Status st;

          /* �맮� �� ���墠�� ��ࢮ� ᮮ�饭�� ��-�� ⮣�, �� tag1=tag3 */
        MPI_Recv( buf3, ELEMS(buf3), MPI_CHAR, 0, tag3, MPI_COMM_WORLD, &st );

          /* �맮� �� ���墠�� ��ࢮ� ᮮ�饭�� �����஬ */
        MPI_Recv( buf2, ELEMS(buf2), MPI_CHAR, 0, MPI_ANY_TAG,
            MPI_COMM_WORLD, &st );

          /* ��ࢮ� ᮮ�饭�� �㤥� �ᯥ譮 �ਭ�� ⠬, ��� ���� */
        MPI_Recv( buf1, ELEMS(buf1), MPI_CHAR, 0, tag1, myComm, &st );

          /* ���⠥� १����� �ਥ�� */
        printf("Received in buf1 = \'%s\'\n", buf1 );
        printf("Received in buf2 = \'%s\'\n", buf2 );
        printf("Received in buf3 = \'%s\'\n", buf3 );

    } /* rank 1 */

      /* �������塞 MPI, �� ����� ����㭨���஬ �� ����㥬��.
       * ��᫥ �⮣� myComm �㤥� ��襭 � MPI_COMM_NULL (� ���� � 0),
       * � ᮮ⢥�����騥 ��� ����� ���� ����祭� �� 㤠�����.
       */
    MPI_Comm_free( &myComm );

    MPI_Finalize();
}
