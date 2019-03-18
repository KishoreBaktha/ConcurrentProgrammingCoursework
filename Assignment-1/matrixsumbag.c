/* matrix summation using pthreads

   features: uses a barrier; the Worker[0] computes
             the total sum from partial sums computed by Workers
             and prints the total sum to the standard output

   usage under Linux:
     gcc matrixSum.c -lpthread
     a.out size numWorkers

*/
#ifndef _REENTRANT 
#define _REENTRANT 
#endif 
#include <pthread.h>
#include <stdlib.h>
#include <stdio.h>
#include <stdbool.h>
#include <time.h>
#include <sys/time.h>
int nextrow=0;
#define MAXSIZE 10000  /* maximum matrix size */
#define MAXWORKERS 10   /* maximum number of workers */
pthread_mutex_t countmutex=PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t resultmutex=PTHREAD_MUTEX_INITIALIZER;
int numWorkers;           /* number of workers */ 
int numArrived = 0;       /* number who have arrived */
long sum=0;
/* timer */
double read_timer() {
    static bool initialized = false;
    static struct timeval start;
    struct timeval end;
    if( !initialized )
    {
        gettimeofday( &start, NULL );
        initialized = true;
    }
    gettimeofday( &end, NULL );
    return (end.tv_sec - start.tv_sec) + 1.0e-6 * (end.tv_usec - start.tv_usec);
}

double start_time, end_time; /* start and end times */
int size;  /* assume size is multiple of numWorkers */
int sums[MAXWORKERS]; /* partial sums */
int matrix[MAXSIZE][MAXSIZE]; /* matrix */
// int maxelement[MAXWORKERS];
// int minelement[MAXWORKERS];
int maxelement=-1,minelement=100;
int maxrowposition,maxcolumnposition,minrowposition,mincolumnposition;
void *Worker(void *);

/* read command line, initialize, and create threads */
int main(int argc, char *argv[]) {
  int i, j;
  long l; /* use long in case of a 64-bit system */
  pthread_attr_t attr;
  pthread_t workerid[MAXWORKERS];

  /* set global thread attributes */
  pthread_attr_init(&attr);
  pthread_attr_setscope(&attr, PTHREAD_SCOPE_SYSTEM);

  /* initialize mutex and condition variable */

  /* read command line args if any */
  size = (argc > 1)? atoi(argv[1]) : MAXSIZE;
  numWorkers = (argc > 2)? atoi(argv[2]) : MAXWORKERS;
  if (size > MAXSIZE) size = MAXSIZE;
  if (numWorkers > MAXWORKERS) numWorkers = MAXWORKERS;

  /* initialize the matrix */
  for (i = 0; i < size; i++) {
	  for (j = 0; j < size; j++) {
          matrix[i][j] =rand()%99;
	  }
  }

 #ifdef DEBUG
  for (i = 0; i < size; i++) {
	  printf("[ ");
	  for (j = 0; j < size; j++) {
	    printf(" %d", matrix[i][j]);
	  }
	  printf(" ]\n");
  }
#endif
  /* do the parallel work: create the workers */
  start_time = read_timer();
  for (l = 0; l < numWorkers; l++)
    pthread_create(&workerid[l], &attr, Worker,NULL);
    for (l = 0; l < numWorkers; l++)
    pthread_join(workerid[l],NULL);
    end_time = read_timer();
    /* print results */
    printf("The total is %ld\n", sum);
    printf("The maxelement is %d in row %d and column %d\n", maxelement,maxrowposition,maxcolumnposition);

    printf("The minelement is %d in row %d and column %d\n", minelement,minrowposition,mincolumnposition);
    printf("The execution time is %g sec\n", end_time - start_time);
}


/* Each worker sums the values in one strip of the matrix.
   After a barrier, worker(0) computes and prints the total */
void *Worker(void *arg) 
{
  long myid = (long) arg;
  int i, j;int row;int total=0;
  int mymax=-1,mymin=99;
   int mymaxrowpos,mymaxcolpos,myminrowpos,mymincolpos;
      while(true)
      {
        pthread_mutex_lock(&countmutex);
    row=nextrow;nextrow++;
      pthread_mutex_unlock(&countmutex); 
        if(row>=size)
      {
        pthread_mutex_lock(&resultmutex);
        if(mymax>maxelement)
      {
         maxelement=mymax;
         maxrowposition=mymaxrowpos; 
        maxcolumnposition=mymaxcolpos;
      }
      if(mymin<minelement)
      {
       minelement=mymin;
        minrowposition=myminrowpos;
         mincolumnposition=mymincolpos;
      }
       sum+=total;
       pthread_mutex_unlock(&resultmutex); 
        pthread_exit(0);
     }
     else
     {
        for (j = 0; j < size; j++)
    {      
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
    total += matrix[row][j];
    }
     }
}
}
