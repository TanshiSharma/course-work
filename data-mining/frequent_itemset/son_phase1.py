from MapReduce import MapReduce
from apriori import *

map_reduce_obj = MapReduce()


def mapper(record):
    return_apriori=apriori(record,False)
    for item in return_apriori:
        if item:
            for subitem in item:
                if subitem:
                    map_reduce_obj.emit_intermediate(tuple(subitem),1)


def reducer(key,list_of_values):
    map_reduce_obj.emit(list(key))


if __name__ == '__main__':
    input_data = open(sys.argv[1])
    map_reduce_obj.execute(input_data,mapper,reducer)
