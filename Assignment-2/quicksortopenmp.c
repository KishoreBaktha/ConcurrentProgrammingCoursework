#include <stdio.h>
#include<pthread.h>
#include<math.h>
#include <stdlib.h>
#include <time.h>
#include <sys/time.h>
#include<omp.h>
#define MAXSIZE 10000
#define NUMWORKERS 8
int size;int workers;
struct arg_struct {
    int low;
    int high;
};
double start_time,end_time; 
void quicksort(int,int);
 int list[10000000];
int main(int argc,char *argv[])
{
   double start_time, end_time;
     size= (argc > 1)? atoi(argv[1]) : MAXSIZE;
    workers= (argc > 2)? atoi(argv[2]) : NUMWORKERS;
    printf("workerd is %d\n",workers);
    int i;
    struct arg_struct args;
    for (i = 0; i < size; i++)
    {
        list[i]=rand()%99;
    } 
     omp_set_num_threads(workers);
      start_time = omp_get_wtime();
     #pragma omp parallel
    {
      #pragma omp single
    quicksort(0, size - 1);
   }
     end_time = omp_get_wtime();
    printf("The elements in sorted order are-sort\n");
    for (i = 0; i < size; i++)
    {
        printf("%d ", list[i]);
    }
    printf("\n");
   printf("The exeuction time is %g \n",end_time-start_time);
 return 0;
}
void quicksort(int low,int high)
{
    int pivot, i, j, temp;
    if (low < high)
    {
        pivot = low;
        i = low;
        j = high;
        while (i < j) 
        {
            while (list[i] <= list[pivot] && i <= high)
                i++;
            while (list[j] > list[pivot] && j >= low)
                j--;
            if (i < j)
            {
                temp = list[i];
                list[i] = list[j];
                list[j] = temp;
            }
        }
        temp = list[j];
        list[j] = list[pivot];
        list[pivot] = temp;
         #pragma omp task
       quicksort(low, j - 1);
       quicksort( j + 1, high);
       //  #pragma omp taskwait
    }
}