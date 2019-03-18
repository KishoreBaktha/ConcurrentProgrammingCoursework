
#ifndef _REENTRANT 
#define _REENTRANT 
#endif 
#include<iostream> 
#include<string> 
#include <pthread.h>
#include <stdlib.h>
#include <fstream>
#include <stdio.h>
#include <stdbool.h>
#include <time.h>
#include <sys/time.h>
#include <set>
using namespace std;
 /* maximum matrix size */
#define MAXWORKERS 10   /* maximum number of workers */
pthread_mutex_t mutex1=PTHREAD_MUTEX_INITIALIZER;
int numWorkers;           /* number of workers */ 
int numArrived = 0;       /* number who have arrived */
int numArrived2[MAXWORKERS]; 
/* a reusable counter barrier */
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
int stripSize;  /* assume size is multiple of numWorkers */
int sums[MAXWORKERS]; /* partial sums */
int sums2[MAXWORKERS];
string words[8381][3]; /* matrix */
set<string> myset;
ofstream fp2;
//FILE *fp2=fopen("results","w");
void *Worker(void *);
void *Search(void *);
//string reverse2(string word);

/* read command line, initialize, and create threads */
int main(int argc, char *argv[]) 
{
  char ch, file_name[25];
   FILE *fp; int total=0;
   fp2.open("results.rtf");
 //string words[8381][3];
 for(int i=0;i<8381;i++)
 {
   for(int j=0;j<3;j++)
   words[i][j]="";
 }
   printf("Enter name of a file you wish to see\n");
   gets(file_name);
 
   fp = fopen(file_name, "r"); // read mode
 
   if (fp == NULL)
   {
      perror("Error while opening the file.\n");
      exit(EXIT_FAILURE);
   }
 pthread_attr_t attr;
  pthread_t workerid[MAXWORKERS];

  /* set global thread attributes */
   pthread_attr_init(&attr);
  pthread_attr_setscope(&attr, PTHREAD_SCOPE_SYSTEM);
  numWorkers = (argc > 1)? atoi(argv[1]) : MAXWORKERS;
 if (numWorkers > MAXWORKERS) numWorkers = MAXWORKERS;
 stripSize = 8381/numWorkers;
 
   //printf("The contents of %s file are:\n", file_name);
   int index=0;int count=0;string g="";
   while((ch = fgetc(fp)) != EOF)
   {
   if(ch=='\n')
   {
       myset.insert(g);
       g="";
      if(count==2)
      {
        count=0;index++;
      }
      else
      count++;
   }
   else
   {
    g+=ch;
    words[index][count]+=ch;
   }
  // printf("size is %d",myset.size());
   }  

  /* do the parallel work: create the workers */
  start_time = read_timer();
  for (long l = 0; l < numWorkers; l++)
    pthread_create(&workerid[l], &attr, Worker, (void *) l);
    for (int l = 0; l < numWorkers; l++)
    pthread_join(workerid[l],NULL);
      end_time = read_timer();
  for (int i = 0; i < numWorkers; i++)
    {
       printf(" Thread %d - %d \n",i+1,sums[i]);
       total+=sums[i];
    }
    /* get end time */
    end_time = read_timer();
    /* print results */
    printf("The total is %d\n", total);
    printf("The execution time is %g sec\n", end_time - start_time);
}

/* Each worker sums the values in one strip of the matrix.
   After a barrier, worker(0) computes and prints the total */
void *Worker(void *arg)
 {
  long myid = (long) arg;
  int total, i, j, first, last,m,n;

#ifdef DEBUG
  printf("worker %d (pthread id %d) has started\n", myid, pthread_self());
#endif

  /* determine first and last rows of my strip */
  first = myid*stripSize;
  last = (myid == numWorkers - 1) ? (8381 - 1) : (first + stripSize - 1);
  /* sum values in my strip */
  total = 0;
  for (i = first; i <= last; i++)
  {
     for (j = 0; j < 3; j++)
     {
        string str="";
    for(int k=words[i][j].length()-1;k>=0;k--)
    {
      str+=words[i][j].at(k);
    }
    if(myset.count(str)==1)
    {
        fp2 <<words[i][j] << endl;
    total+=1;
    }
     }
  } 
  sums[myid] = total;
   pthread_exit(0);
  }