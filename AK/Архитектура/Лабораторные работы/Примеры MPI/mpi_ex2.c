/*
 *  ex2.c  --  �ਬ�� 2       �.⠪��  --  http://www.csa.ru/~il/mpi_tutor
 *
 *  �ਥ� ᮮ�饭�� �������⭮� �����:
 *      MPI_Probe
 *
 *  �ਥ� ᮮ�饭�� �� ࠧ��� ��ࠢ�⥫�� � ࠧ�묨 �����䨪��ࠬ�
 *  ( � ᮤ�ন�� ࠧ��� ⨯�� ) � �ந����쭮� ���浪�:
 *      MPI_ANY_SOURCE, MPI_ANY_TAG     ( ������� )
 */

#include <mpi.h>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

  /* �����䨪���� ᮮ�饭�� */
#define tagFloatData  1
#define tagLongData   2

  /* ����� ��।������� ᮮ�饭�� ����� ����
   * ��砩��� �� 1 �� maxMessageElems
   */
#define maxMessageElems 100

int main( int argc, char **argv )
{
    int    size, rank, count, i, n, ok;
    float *floatPtr;
    int   *longPtr;
    char  *typeName;
    MPI_Status status;

      /* ���樠������ � ᮮ�饭�� �� �訡��
       * 楫���� ��७�ᥭ� �� ��ࢮ�� �ਬ��
       */
    MPI_Init( &argc, &argv );
    MPI_Comm_size( MPI_COMM_WORLD, &size );
    MPI_Comm_rank( MPI_COMM_WORLD, &rank );

      /* ���짮��⥫� ������ �������� ஢�� ��� �����, ���� �訡�� */
    if( size != 3 ) {
        if( rank==0 )
            printf("Error: 3 processes required instead of %d\n", size );
        MPI_Barrier( MPI_COMM_WORLD );
        MPI_Abort( MPI_COMM_WORLD, MPI_ERR_OTHER );
        return -1;
    }

      /* ������ ����� ���樠������� ������� ��砩��� �ᥫ */
    srand( ( rank + 1 ) * (unsigned )time(0) );

    switch( rank ) {

        case 0:
              /* ᮧ���� ᮮ�饭�� ��砩��� ����� */
            count = 1 + rand() % maxMessageElems;
            floatPtr = malloc( count * sizeof(float) );
            for( i=0; i<count; i++ )
                floatPtr[i] = (float)i;

              /* ���뫠�� ᮮ�饭�� � ������ 2 */
            MPI_Send( floatPtr, count, MPI_FLOAT,
                2, tagFloatData, MPI_COMM_WORLD );
            printf("%d. Send %d float items to process 2\n",
                rank, count );
            break;

        case 1:
              /* ᮧ���� ᮮ�饭�� ��砩��� ����� */
            count = 1 + rand() % maxMessageElems;
            longPtr = malloc( count * sizeof(long) );
            for( i=0; i<count; i++ )
                longPtr[i] = i;

              /* ���뫠�� ᮮ�饭�� � ������ 2 */
            MPI_Send( longPtr, count, MPI_LONG,
                2, tagLongData, MPI_COMM_WORLD );
            printf("%d. Send %d long items to process 2\n",
                rank, count );
            break;

        case 2:
            /* ����� 2 �ਭ����� ᮮ�饭�� �������⭮� �����,
             * �ᯮ���� MPI_Probe
             */
            for( n=0; n<2; n++ )  /* �ᥣ� ��������� ��� ᮮ�饭�� */
            {
                MPI_Probe(
                    MPI_ANY_SOURCE,  /* ������: ���� �� �� ����� */
                    MPI_ANY_TAG,     /* ������: ���� � ��� �����䨪��஬ */
                    MPI_COMM_WORLD,
                    &status );
                /* MPI_Probe ��୥� �ࠢ�����, ����� ᮮ�饭�� �㤥�
                 * 㦥 �� �ਥ���� ��஭� � �㦥���� ����
                 */

                /* �஢��塞 �����䨪��� � ࠧ��� ��襤襣� ᮮ�饭��
                 */
                if( status.MPI_TAG == tagFloatData )
                {
                    MPI_Get_count( &status, MPI_FLOAT, &count );

                    /* �ਭ�⮥ �㤥� ࠧ��饭� � �������᪮� �����:
                     * �����뢠�� � ��� ���� ᮮ⢥�����饩 �����
                     */
                    floatPtr = malloc( count * sizeof(float) );

                    MPI_Recv( floatPtr, count, MPI_FLOAT,
                        MPI_ANY_SOURCE, MPI_ANY_TAG, MPI_COMM_WORLD,
                        &status );
                    /* MPI_Recv ���� ᪮����� 㦥 �ਭ��� �����
                     * �� ��⥬���� ���� � ���짮��⥫�᪨�
                     */

                    /* �஢��塞 �ਭ�⮥ */
                    for( ok=1, i=0; i<count; i++ )
                        if( floatPtr[i] != (float)i )
                            ok = 0;
                    typeName = "float";
                }
                else if( status.MPI_TAG == tagLongData )
                {
                    /* ����⢨�, ��������� ��襮��ᠭ��
                     */
                    MPI_Get_count( &status, MPI_LONG, &count );
                    longPtr = malloc( count * sizeof(long) );

                    MPI_Recv( longPtr, count, MPI_LONG,
                        MPI_ANY_SOURCE, MPI_ANY_TAG, MPI_COMM_WORLD,
                        &status );

                    for( ok=1, i=0; i<count; i++ )
                        if( longPtr[i] != i )
                            ok = 0;
                    typeName = "long";
                }

                 /* ������뢠�� � �����襭�� �ਥ�� */
                printf( "%d. %d %s items are received from %d : %s\n", rank,
                    count, typeName, status.MPI_SOURCE, ok ? "OK" : "FAILED" );

            } /* for(n) */
            break;

    } /* switch(rank) */

     /* �����襭�� ࠡ��� */
    MPI_Finalize();
    return 0;
}
