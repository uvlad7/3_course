
/*
 *  ex3.c  --  �ਬ�� 3       �.⠪��  --  http://www.csa.ru/~il/mpi_tutor
 *
 *  �㭪樨 ��饣� �����祭�� ��� ࠡ��� � ⨯���:
 *     MPI_Type_commit, MPI_Type_free, MPI_Type_extent
 *  �������� ���짮��⥫�᪨� ⨯�� ������:
 *     MPI_Type_vector, MPI_Type_hvector
 *
 *  1) ������ ��।�� ��⪨:
 *     ��।����� ⮫쪮 � �祩��, � ������ ��� ���न���� �����
 *          x.x.x
 *          .....       x - �뤥����� � ��।�����
 *          x.x.x
 *          .....       . - �����������
 *          x.x.x
 *
 *  2) ��।�� � �࠭ᯮ��஢�����: �⮫��� �⠭������ ��ப���
 *
 *  �����: ��। ⥬, ��� ����᪠�� ��� �ਬ��,
 *  ��४���� �ନ���/���� � ०�� � ���ᨬ���� ������⢮� ��ப.
 */

#include <mpi.h>
#include <stdio.h>

 /* ������⢮ ��ப � �⮫�殢 � ��室��� �����
  */
#define  SIZE  7

 /* ⮫쪮 ��� ��ࢮ�� �ਬ��:
  * ������⢮ ��ப � �⮫�殢 � �⮣���� �����
  */
#define  halfSize  (SIZE+1)/2

int main( int argc, char **argv )
{
    int a[ SIZE ][ SIZE ],         /* ��室��� ����� - ��� ����� �ਬ�஢ */
        b[ halfSize ][ halfSize ], /* �⮣���� ����� ��� ��ࢮ�� �ਬ�� */
        c[ SIZE ][ SIZE ];         /* �⮣���� ����� ��� ��ண� �ਬ�� */
    int rank, i, j;
    MPI_Aint typeExtent;

    MPI_Datatype               /* ����⥫� ���짮��⥫�᪨� ⨯�� */
        oneSlice, twoSlice,        /* ��� ��ࢮ�� �ਬ�� */
        oneRow, rowByRow;          /* ��� ��ண� �ਬ�� */

    MPI_Status status;         /* �㦭� ⮫쪮 ��� �ਥ��, �� �ᯮ������ */

/*-----------------*
 *  ���樠������  *
 *-----------------*/

    MPI_Init( &argc, &argv );
    MPI_Comm_rank( MPI_COMM_WORLD, &rank );

     /* ��� �㦥� ⮫쪮 ���� �����: �� �㤥� � ��।��稪��, � �ਥ������.
      * ��⠫�� ������ ���������� ����������.
      */
    if( rank != 0 )
        goto done;

     /* ������㥬 ��室��� ������ � ���⠥� ��
      */
    for( i=0; i<SIZE; i++ )
        for( j=0; j<SIZE; j++ )
            a[i][j] = 10 * (i+1) + (j+1);

    puts("Sended:");
    for( i=0; i<SIZE; i++ ) {
        for( j=0; j<SIZE; j++ )
            printf("%4d", a[i][j] );
        puts("");
    }
    puts("");

/*------------------------------------------------*
 *  ���� �ਬ��: �뤥����� � ����뫪� ��⪨  *
 *------------------------------------------------*/

     /* ������� ⨯ �� ����⮢ ���� 'x.'
      * #1. ������⢮ ����⮢ � ����� ⨯� = �������� �᫠ �⮫�殢
      * #2. ����� ����� ᮤ�ন� ���� 楫�� �᫮
      * #3. ��砫� ����⮢ ������ ��� �� ��㣠 �१ ��� �祩�� ⨯� int
      * => ���� ⨯ ����뢠�� ����� ���� ��ப� ������ 蠡����� 'x.x.x.x.'
      */
    MPI_Type_vector( halfSize, 1, 2, MPI_INT, &oneSlice );

     /* ������� ⨯ �� ����⮢ ���� 'x.x.x.x.........'
      * #1. ������⢮ ����⮢ � ����� ⨯� = �������� �᫠ ��ப
      * #2. ����� ����� ᮤ�ন� ���� �祩�� ⨯� 'oneSlice'
      * #3. ��砫� ����⮢ ������ ��� �� ��㣠 �१ ��� ��ப�
      * => ���� ⨯ ����뢠�� ������ ��⪮� � ����묨 ���न��⠬�
      */
    MPI_Type_hvector(halfSize, 1, SIZE*2*sizeof(int), oneSlice, &twoSlice );

     /* ��������㥬 ⨯ 'twoSlice' ��� �ᯮ�짮�����.
      * ��᪮��� ⨯ 'oneSlice' �ᯮ������ ⮫쪮
      * ��� ��஦����� ��㣨� ⨯��, ��� ॣ����஢��� �� ����.
      */
    MPI_Type_commit( &twoSlice );

     /* �� �⮬ �����⮢�⥫쭠� ࠡ��, �믮��塞��
      * ����� ���� ���, �����襭�.
      */

     /* ���뫠�� ᠬ� ᥡ�.
      * ��ࠢ�塞 ���� ��६����� ⨯� 'twoSlice',
      * �ᯮ�������� �� ����� ������ 'a'.
      * �ਭ����� ��⮪ 楫�� �ᥫ � ࠧ��頥� ��� �� ����� ������ 'b'
      */
    MPI_Sendrecv( a, 1, twoSlice, rank, 0,
                  b, halfSize*halfSize, MPI_INT, rank, 0,
        MPI_COMM_WORLD, &status );

     /* ����� �ਭ�⮩ ���������
      */
    puts("Received sub-matrix:");
    for( i=0; i<halfSize; i++ ) {
        for( j=0; j<halfSize; j++ )
            printf("%4d", b[i][j] );
        puts("");
    }
    puts("");

     /* ���⠥� ࠧ��� ⨯� �� �࠭����, � ࠧ��� ⨯� �� ��।��
      */
    MPI_Type_extent( oneSlice, &typeExtent );
 /* MPI_Type_size  ( oneSlice, &typeSize ); */
    printf("\'oneSlice\' has extent = %3d\n", typeExtent );
    MPI_Type_extent( twoSlice, &typeExtent );
 /* MPI_Type_size  ( twoSlice, &typeSize ); */
    printf("\'twoSlice\' has extent = %3d\n", typeExtent );

     /* ���⪠ ����⥫��. MPI 㤠��� ᮮ⢥����騥 �����
      * �� ����७��� ⠡��� � ���� ����⥫� � MPI_TYPE_NULL
      */
    MPI_Type_free( &oneSlice );
    MPI_Type_free( &twoSlice );

/*------------------------------------------------*
 *  ��ன �ਬ��: ����뫪� � �࠭ᯮ��஢�����  *
 *------------------------------------------------*/

     /* � ����⨢��� ����� ����� �࠭���� �����筮: ��ப�1,��ப�2,...
      * ������� ⨯-蠡��� ��� ����ண� �뤥����� �� ������ ������ �⮫��:
      * #1. ��᫮ ����⮢ = �������� ��ப, �.�. ���� �⮫��
      * #2. � ������ ��ப� ��� ������� ���� �祩��
      * #3. �������� �ᯮ������ � ����� � 蠣��, ࠢ�� ����� ��ப�
      */
    MPI_Type_vector( SIZE, 1, SIZE, MPI_INT, &oneRow );

     /* ����砥� ⨯ ��� ���⮫�殢��� �।�⠢����� ������:
      * #1. ��᫮ ����⮢ = �������� �⮫�殢
      * #2. ����� ����� ᮤ�ন� ���� �⮫���, �.�. �祩�� ⨯� 'oneRow'
      * #3. ��砫� ����⮢ (�.�. �⮫�殢) ������ ��� �� ��㣠
      *     �� ����ﭨ� � ���� �祩�� ⨯� int.
      */
    MPI_Type_hvector( SIZE, 1, sizeof(int), oneRow, &rowByRow );

    MPI_Type_commit( &rowByRow );
     /* ��� ��ॣ����஢��,
      * �����⮢�⥫쭠� ࠡ�� �����襭�.
      */

     /* ��।��� � �����६���� �࠭ᯮ��஢�����.
      * ��।����� ��६����� ⨯� 'rowByRow', ��室�����
      * �� ����� ������ 'a'. ����� �室�� �⮫��� �� �⮫�殬.
      * �ਭ������� ��⮪ 楫�� �ᥫ, ࠧ��頥�� �� ����� ������ 'b'.
      * ����� ࠧ������� �����뢭�, � ���� �����筮!
      */
    MPI_Sendrecv( a, 1, rowByRow, rank, 0,
                  c, SIZE*SIZE, MPI_INT, rank, 0,
        MPI_COMM_WORLD, &status );

     /* ����� �ਭ�⮩ ������
      */
    puts("Received transposed matrix:");
    for( i=0; i<SIZE; i++ ) {
        for( j=0; j<SIZE; j++ )
            printf("%4d", c[i][j] );
        puts("");
    }
    puts("");

     /* ���⠥� ࠧ��� ⨯� �� �࠭���� � ࠧ��� ⨯� �� ��।��
      */
    MPI_Type_extent( oneRow, &typeExtent );
 /* MPI_Type_size  ( oneRow, &typeSize ); */
    printf("\'oneRow\'   has extent = %3d\n", typeExtent );
    MPI_Type_extent( rowByRow, &typeExtent );
 /* MPI_Type_size  ( rowByRow, &typeSize ); */
    printf("\'rowByRow\' has extent = %3d\n", typeExtent );

     /* ���⪠ ����⥫��.
      */
    MPI_Type_free( &oneRow );
    MPI_Type_free( &rowByRow );

/*---------------------*
 *  �����襭�� ࠡ���  *
 *---------------------*/

done:
    MPI_Finalize();
    return 0;
}
