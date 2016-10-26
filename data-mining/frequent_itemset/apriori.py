import sys
import json
from math import ceil

import itertools


def apriori(baskets,PrRst):
    length=len(baskets)
    threshold=ceil(length*0.3)
    unique_elements=[]
    output_list = []
    for i in baskets:
        for j in i:
            if j not in unique_elements:
                unique_elements.append([j])
    count_k=1
    while True:
        candidate_list=get_candidate(unique_elements,count_k)
        candidate_list.sort()
        if PrRst:
            print 'C%s: %s' %(count_k, candidate_list)
        if not candidate_list:
            break
        frequent_itemlist=get_frequentitem(threshold,candidate_list,baskets)
        if PrRst:
            print 'L%s: %s' %(count_k, frequent_itemlist)
        output_list.append(frequent_itemlist)
        if not frequent_itemlist:
            break
        unique_elements=frequent_itemlist
        count_k += 1

    return output_list

def get_frequentitem(threshold,candidate_list,baskets):
    output_list=[]
    for candidate in candidate_list:
        candidate_set=set(candidate)
        count=0
        for basket in baskets:
            if not candidate_set-set(basket):
                count+=1
        if count>=threshold:
            output_list.append(candidate)
    return output_list


def get_candidate(unique_elements,count_k):
    output_list = []
    unique_item=[]
    dict_all_elements = {}
    for element in unique_elements:
        element.sort()
        dict_all_elements[tuple(element)] = 1
        for inner_element in element:
            if not inner_element in unique_item:
                unique_item.append(inner_element)
    frequentk_itemset=list(itertools.combinations(unique_item, count_k))

    if count_k > 1:
        for item in frequentk_itemset:
            item=set(item)
            is_valid = True
            possible_combination=list(itertools.combinations(item, count_k-1))
            for combination_item in possible_combination:
                combination_item = list(combination_item)
                combination_item.sort()
                if tuple(combination_item) not in dict_all_elements:
                    is_valid = False
                    break
            if is_valid:
                output_list.append(list(item))
    else:
        for item in frequentk_itemset:
            item=list(item)
            output_list.append(item)
    return output_list

if __name__=='__main__':
    input_data = open(sys.argv[1])
    line = input_data.readline().strip()
    apriori(json.loads(line), True)