/* matrix summation using OpenMP

   usage with gcc (version 4.2 or higher required):
     gcc -O -fopenmp -o matrixSum-openmp matrixSum-openmp.c 
     ./matrixSum-openmp size numWorkers

*/

#include <omp.h>

double start_time, end_time;

#include <stdio.h>
#define MAXSIZE 10000  /* maximum matrix size */
#define MAXWORKERS 8  /* maximum number of workers */

int numWorkers;
int size; 
int matrix[MAXSIZE][MAXSIZE];
void *Worker(void *);
long sum=0;
int max=0,maxrowposition,maxcolumnposition;
int min=100,minrowposition,mincolumnposition;
/* read command line, initialize, and create threads */
int main(int argc, char *argv[]) {
  int i, j;
  long total=0;

  /* read command line args if any */
  size = (argc > 1)? atoi(argv[1]) : MAXSIZE;
  numWorkers = (argc > 2)? atoi(argv[2]) : MAXWORKERS;
  if (size > MAXSIZE) size = MAXSIZE;
  if (numWorkers > MAXWORKERS) numWorkers = MAXWORKERS;
  int stripSize = size/numWorkers;
  omp_set_num_threads(numWorkers);

  /* initialize the matrix */
  for (i = 0; i < size; i++) {
    //  printf("[ ");
	  for (j = 0; j < size; j++) {
      matrix[i][j] = rand()%99;
      //	  printf(" %d", matrix[i][j]);
	  }
	  //	  printf(" ]\n");
  }
  start_time = omp_get_wtime();
#pragma omp parallel shared(stripSize,size,sum) private(total)
{
   int id= omp_get_thread_num();
  long total=0;
   int first = id*stripSize;
   int last = (id == numWorkers - 1) ? (size - 1) : (first + stripSize - 1);
   int mymax=-1,mymin=99;
   int mymaxrowpos,mymaxcolpos,myminrowpos,mymincolpos;
  for (int i = first; i <=last; i++)
  {
    for (int j = 0; j < size; j++)
    {
      total += matrix[i][j];
      if(matrix[i][j]>mymax)
      {
         mymax=matrix[i][j];
         mymaxrowpos=i+1; 
        mymaxcolpos=j+1;
      }
      if(matrix[i][j]<mymin)
      {
       mymin=matrix[i][j];
        myminrowpos=i+1;
         mymincolpos=j+1;
      }
      }    
  }
  #pragma omp critical(sum)
     sum+=total;
     if(mymax>max)
      {
         #pragma omp critical(max)
         {
          max=mymax;
         maxrowposition=mymaxrowpos; 
        maxcolumnposition=mymaxcolpos;
         }
      }
      if(mymin<min)
      {
        #pragma omp critical(min)
         {
       min=mymin;
        minrowposition=myminrowpos;
         mincolumnposition=mymincolpos;
         }
      }
  }
  end_time = omp_get_wtime();
  printf("the total is %ld\n", sum);
  printf("the max is %d\n", max);
  printf(" max element posiiton is row %d and column %d \n",maxrowposition+1,maxcolumnposition+1);
 printf("the min is %d\n", min);
  printf(" min element posiiton is row %d and column %d ",minrowposition+1,mincolumnposition+1);
  printf("it took %g seconds\n", end_time - start_time);
}

