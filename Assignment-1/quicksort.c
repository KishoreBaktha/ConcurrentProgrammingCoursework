#include <stdio.h>
#include<pthread.h>
#include<math.h>
#include <stdlib.h>
#include <time.h>
#include <sys/time.h>
#include <stdbool.h>
#define MAXSIZE 100000
int size;
struct arg_struct {
    int low;
    int high;
}; 
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
void* quicksort_thread (void*);
void quicksort(int,int);
 int list[1000000];
int main(int argc,char *argv[])
{
   double start_time, end_time;
     size= (argc > 1)? atoi(argv[1]) : MAXSIZE;
    int i;
    struct arg_struct args;
    srand(time(NULL));
    for (i = 0; i < size; i++)
    {
        list[i]=rand()%99;
    } 
      start_time = read_timer();
    quicksort(0, size - 1);
    end_time=read_timer();
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
     struct arg_struct args;
    args.low=low;
    args.high=high;
    quicksort_thread(&args);
}
void *quicksort_thread(void *arguments)
{
    struct arg_struct *args = (struct arg_struct *)arguments;
    int high=args->high;
    int low=args->low;
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
        struct arg_struct args2;
     args2.low=j+1;
     args2.high=high;
      pthread_t thread;
       quicksort(low, j - 1);
		pthread_create(&thread, NULL, quicksort_thread, &args2);
		pthread_join(thread, NULL);
      //  quicksort(list, j + 1, high);
       // pthread_exit(0);
    }
}