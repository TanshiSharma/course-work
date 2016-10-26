import sys
from MapReduce import MapReduce

map_reduce_obj=MapReduce()


def mapper(record):
    length=len(record)
    summation_x, summation_square_x = 0,0
    for value in record:
        summation_x+=value
        summation_square_x+=(value**2)
    map_reduce_obj.emit_intermediate('1', [summation_x, summation_square_x, length])


def reducer(key,list_of_values):
    summation_n,summation_x,summation_square_x=0,0,0
    for value in list_of_values:
        summation_x+=value[0]
        summation_square_x+=value[1]
        summation_n+=value[2]
    variance=float(summation_square_x)/summation_n - (float(summation_x)/summation_n)**2
    map_reduce_obj.emit(variance)


if __name__=='__main__':
    input_data=open(sys.argv[1])
    map_reduce_obj.execute(input_data,mapper,reducer)