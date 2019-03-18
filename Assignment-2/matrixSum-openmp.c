/* matrix summation using OpenMP

   usage with gcc (version 4.2 or higher required):
     gcc -O -fopenmp -o matrixSum-openmp matrixSum-openmp.c 
     ./matrixSum-openmp size numWorkers

*/

#include <omp.h>

double start_time, end_time;

#include <stdio.h>
#define MAXSIZE 10000 /* maximum matrix size */
#define MAXWORKERS 8  /* maximum number of workers */

int numWorkers;
int size; 
int matrix[MAXSIZE][MAXSIZE];
void *Worker(void *);
int max=0,maxrowposition,maxcolumnposition;
int min=100,minrowposition,mincolumnposition;
int mymax=-1,mymin=99;
   int mymaxrowpos,mymaxcolpos,myminrowpos,mymincolpos;
/* read command line, initialize, and create threads */
int main(int argc, char *argv[]) 
{
  int i, j;
  long total=0;

  /* read command line args if any */
  size = (argc > 1)? atoi(argv[1]) : MAXSIZE;
  numWorkers = (argc > 2)? atoi(argv[2]) : MAXWORKERS;
  if (size > MAXSIZE) size = MAXSIZE;
  if (numWorkers > MAXWORKERS) numWorkers = MAXWORKERS;

  omp_set_num_threads(numWorkers);

  /* initialize the matrix */
  for (i = 0; i < size; i++) {
    //  printf("[ ");
	  for (j = 0; j < size; j++) {
      matrix[i][j] = rand()%99;
        //printf(" %d", matrix[i][j]);
	  }
	  //	 printf(" ]\n");
  }

  start_time = omp_get_wtime();
#pragma omp parallel for  reduction(+:total) private (j) shared(max,min)
  for (i = 0; i < size; i++)
  {
    for (j = 0; j < size; j++)
    {
      total += matrix[i][j]; 
      if(matrix[i][j]>max)
      {
        #pragma omp critical
        {
          if(matrix[i][j]>max)
          {  
          max=matrix[i][j];
         maxrowposition=i+1; 
        maxcolumnposition=j+1;
        }
      }
      }
      if(matrix[i][j]<min)
      {
        #pragma omp critical
        {
          if(matrix[i][j]<min)
      {
       min=matrix[i][j];
        minrowposition=i+1;
         mincolumnposition=j+1;
      }
      }
      }
  }
  }
// implicit barrier
  end_time = omp_get_wtime();

  printf("the total is %ld\n", total);
  printf("the max is %d\n", max);
  printf(" max element posiiton is row %d and column %d \n",maxrowposition,maxcolumnposition);
 printf("the min is %d\n", min);
  printf(" min element posiiton is row %d and column %d ",minrowposition,mincolumnposition);
  printf("it took %g seconds\n", end_time - start_time);
}

