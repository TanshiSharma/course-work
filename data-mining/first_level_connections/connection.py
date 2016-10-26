from MapReduce import MapReduce
import itertools
import sys

map_reduce_obj = MapReduce()


def mapper(record):
    map_reduce_obj.emit_intermediate(record[0],record[1])
    map_reduce_obj.emit_intermediate(record[1],record[0])


def reducer(key,list_of_values):
    value_group = list(itertools.combinations(list_of_values, 2))
    for value in value_group:
        value=list(value)
        value.sort()
        value.append(key)
        map_reduce_obj.emit(value)

if __name__ == '__main__':
    input_data = open(sys.argv[1])
    map_reduce_obj.execute(input_data,mapper,reducer)