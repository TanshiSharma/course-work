import sys
import json
from math import ceil

from MapReduce import MapReduce
map_reduce_obj = MapReduce()


def mapper(record):
    number_of_baskets=len(record)
    candidate_list=open(sys.argv[2])
    for candidate in candidate_list:
        count=0
        candidate=json.loads(candidate.strip())
        for candidate_chunk in record:
            if not set(candidate)-set(candidate_chunk):
               count+=1
        map_reduce_obj.emit_intermediate(tuple(candidate),(count,number_of_baskets))


def reduce(key,list_of_value):
    total_count=0
    total_baskets=0
    for item in list_of_value:
        total_count+=item[0]
        total_baskets+=item[1]
    threshold=ceil(total_baskets*0.3)
    if total_count>=threshold:
        map_reduce_obj.emit([list(key),total_count])


if __name__=='__main__':
    input_data = open(sys.argv[1])
    map_reduce_obj.execute(input_data, mapper, reduce)