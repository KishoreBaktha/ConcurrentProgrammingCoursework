
#ifndef _REENTRANT 
#define _REENTRANT 
#endif 
#include<iostream> 
#include<string> 
#include<omp.h>
#include <stdlib.h>
#include <fstream>
#include <stdio.h>
#include <set>
using namespace std;
 /* maximum matrix size */
#define MAXWORKERS 8  /* maximum number of workers */
int numWorkers;           /* number of workers */ 

double start_time, end_time; 
int sums[MAXWORKERS]; /* partial sums */
string words[25143]; /* matrix */
set<string> myset;
ofstream fp2;

/* read command line, initialize, and create threads */
int main(int argc, char *argv[]) 
{
  char ch, file_name[25];
   FILE *fp; int total=0;
   fp2.open("results.rtf");
 for(int i=0;i<25143;i++)
 {
   words[i]="";
 }
   printf("Enter name of a file you wish to see\n");
   gets(file_name);
 
   fp = fopen(file_name, "r"); // read mode
 
   if (fp == NULL)
   {
      perror("Error while opening the file.\n");
      exit(EXIT_FAILURE);
   }
  numWorkers = (argc > 1)? atoi(argv[1]) : MAXWORKERS;
 if (numWorkers > MAXWORKERS) numWorkers = MAXWORKERS;
   int index=0;int count=0;string g="";
   while((ch = fgetc(fp)) != EOF)
   {
   if(ch=='\n')
   {
       myset.insert(g);
       words[index]=g;index++;
       g="";
   }
   else
    g+=ch;
   }
omp_set_num_threads(numWorkers);
  start_time = omp_get_wtime();
#pragma omp parallel for shared(sums)
  for (int i = 0; i <25143; i++)
  {
        string str="";
    for(int k=words[i].length()-1;k>=0;k--)
    {
      str+=words[i].at(k);
    }
    if(myset.count(str)==1)
    {
        fp2 <<words[i] << endl;
        sums[omp_get_thread_num()] ++;
    }
  } 
  end_time = omp_get_wtime();
  for (int i = 0; i < numWorkers; i++)
    {
       printf(" Thread %d - %d \n",i+1,sums[i]);
       total+=sums[i];
    }
    printf("The total is %d\n", total);
    printf("The execution time is %g sec\n", end_time - start_time);
}