/* *  ex0.c  --  �ਬ�� 0       �.⠪��  --  http://www.csa.ru/~il/mpi_tutor
 * ��砫� � �����襭��: MPI_Init, MPI_Finalize
 * ��।������ ������⢠ ����� � �ਫ������� � ᢮��� ���浪����� �����:
 * MPI_Comm_size, MPI_Comm_rank */

#include <mpi.h>
#include <stdio.h>
int main( int argc, char **argv )
{
    int size, rank, i;
      /* ���樠�����㥬 ������⥪� */
    MPI_Init( &argc, &argv );
      /* ������ ������⢮ ����� � ����饭��� �ਫ������ */
    MPI_Comm_size( MPI_COMM_WORLD, &size );
      /* ... � ᢮� ᮡ�⢥��� �����: �� 0 �� (size-1) */
    MPI_Comm_rank( MPI_COMM_WORLD, &rank );
      /* ����� � ����஬ 0 ᮮ�頥� ���짮��⥫� ࠧ��� ��㯯�,
       * � ���ன �ਪ९���� ������� �裡,
       * � ���ன �ਪ९��� ����⥫� (����㭨����) MPI_COMM_WORLD,
       * �.�. �᫮ ����ᮢ � �ਫ������!!
       */
    if( rank==0 )
        printf("Total processes count = %d\n", size );
      /* ������ ����� �뢮��� ���짮��⥫� ᢮� ����� */
    printf("Hello! My rank in MPI_COMM_WORLD = %d\n", rank );
 
     /* ��窠 ᨭ�஭���樨, ��⥬ ����� 0 ���⠥�
       * ��㬥��� ��������� ��ப�. � ��������� ��ப�
       * ����� ���� ��ࠬ����, ������塞� �����稪�� MPIRUN.
       */
    MPI_Barrier( MPI_COMM_WORLD );
    if( rank == 0 )
        for( puts("Command line of process 0:"), i=0; i<argc; i++ )
            printf( "%d: \"%s\"\n", i, argv[i] );
      /* �� ����� �������� �믮������ */
    MPI_Finalize();
    return 0;
}
