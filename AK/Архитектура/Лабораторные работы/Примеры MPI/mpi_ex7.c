
/*
 *  ex7.c  --  �ਬ�� 7       �.⠪��  --  http://www.csa.ru/~il/mpi_tutor
 *
 *  �������� ���-����㭨���஢. ���� ࠧ������ ��㯯� �� �����㯯�:
 *     MPI_Comm_split
 *
 *  �����������:
 *    ������� ��� �ਬ�� ��᪮�쪮 ࠧ � ࠧ�� �᫮� ��⢥� N ( -np N ),
 *    ���ਬ��: N = 6,7,8
 */

#include <mpi.h>
#include <stdio.h>

#define  tag1  1
#define  tag2  2
#define  tag3  1   /* ᮧ��⥫쭠� "�訡��": �����䨪��� ࠢ�� 'tag1' */

#define  ELEMS(x)  (sizeof( x )/sizeof( x[0] ))

int main( int argc, char **argv )
{
    MPI_Comm subComm;
    int rank, size, subCommIndex, subRank, subSize;

    MPI_Init( &argc, &argv );
    MPI_Comm_size( MPI_COMM_WORLD, &size );
    MPI_Comm_rank( MPI_COMM_WORLD, &rank );

      /* ���।��塞 �� ᫥���饬� �ࠢ���:
       * ����� �� ��⢨ - � �����㯯�,
       * ������ �⢥���� - "� ���㤠"
       */
    subCommIndex = rank % 4;
    if( subCommIndex == 3 )
        subCommIndex = MPI_UNDEFINED;

      /* ����⥫쭠� �㬥��� ����� �����㯯:
       * ���⭠� ⮩, �� ������� � MPI_COMM_WORLD
       */
    subRank = size - rank;

      /* ��뢠�� �� ���� �����-�������� MPI_COMM_WORLD,
       * 㪠������� ���� ��㬥�⮬ �㭪樨
       */
    MPI_Comm_split( MPI_COMM_WORLD, subCommIndex, rank, &subComm );

      /* ������ ���� ����, � 祬� ��� ⥯��� �⭮����
       */
    printf("My rank in MPI_COMM_WORLD = %d. ", rank);
    if( subComm == MPI_COMM_NULL )
        printf("I\'m not attached to any sub-communicator\n");
    else {
        MPI_Comm_size( subComm, &subSize );
        MPI_Comm_rank( subComm, &subRank );
        printf("My local rank = %d, local size = %d\n", subRank, subSize );
    }   

    MPI_Comm_free( &subComm );

    MPI_Finalize();
}
