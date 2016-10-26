import json
import sys

characteristic_matrix=[]
user_name_list=[]
hash_list = []
min_hash_matrix = []


def read_input(file_path):
    #function reads the input json file and converts the data into characteritic matrix of user and movies

    global characteristic_matrix, user_name_list
    for index in xrange(100):
        characteristic_matrix.append([])
    input_data=open(file_path)
    for line in input_data:
        line=json.loads(line)
        user_name_list.append(line[0])
        for index_line in xrange(100):
            if index_line in line[1]:
                characteristic_matrix[index_line].append(1)
            else:
                characteristic_matrix[index_line].append(0)



def prepare_min_hash():
#function for preparing min hash matrix as the minhash matrix initially contains infinity value
#function  uses the global min_hash_matrix variable and for every value of h(i) for a particular user it initialises it to maxint
    global min_hash_matrix, user_name_list
    length = len(user_name_list)
    for index in xrange(20):
        min_hash_matrix.append([sys.maxint]*length)


def prepare_hash_list():
#function for preparing the hashlist after computing values for all values of x and i
# x: is the row number from 0 to 99
#i: is the ith hash function
    global hash_list
    for i in xrange(1,21):
        hash_list.append([])
        for x in xrange(100):
            h=(3*x+i)%100
            hash_list[i-1].append(h)


def return_index_of_one(row):
#function for returning the indices where the value is 1
#it takes the row of the matrix as an input
    return_list=[]
    for index, element in enumerate(row):
        if element==1:
            return_list.append(index)

    return return_list


def get_hash_index(current_index):
    global hash_list
    hash_index = []
    for in_list in hash_list:
        hash_index.append(in_list[current_index])
    return hash_index


def calculate_min_hash():
#function for implementing min hash
# analyzes the row of the matrix and gets the indices of 1 for comaprison
#finds the minimum row value for the occurence of 1

    global characteristic_matrix, min_hash_matrix, hash_list
    for index, row in enumerate(characteristic_matrix):
        one_value_index_list = return_index_of_one(row)
        hash_indices_list = get_hash_index(index)
        for index3 in xrange(len(hash_indices_list)):
            for index2 in one_value_index_list:
                min_hash_matrix[index3][index2] = min(hash_indices_list[index3], min_hash_matrix[index3][index2])


def create_band():
#function for dividing the min hash matrix into band and taking out the candidate pairs
# every band has the four hasing functions; for every user in  each band the vector of each user is compared with other to give similar candidate
    global min_hash_matrix
    band_0, band_1, band_2, band_3, band_4=[], [], [], [], []
    for user in xrange(len(user_name_list)):
        band_0.append([])
        band_1.append([])
        band_2.append([])
        band_3.append([])
        band_4.append([])
        for index in xrange(4):
            band_0[user].append(min_hash_matrix[index][user])
        for index in xrange(4,8):
            band_1[user].append(min_hash_matrix[index][user])
        for index in xrange(8,12):
            band_2[user].append(min_hash_matrix[index][user])
        for index in xrange(12,16):
            band_3[user].append(min_hash_matrix[index][user])
        for index in xrange(16,20):
            band_4[user].append(min_hash_matrix[index][user])
    candidatepairs=set()
    for index in xrange(len(user_name_list)):
        for inner_index in xrange(index+1,len(user_name_list)):
            if (band_0[index]==band_0[inner_index] or band_1[index]==band_1[inner_index] or band_2[index]==band_2[inner_index] or band_3[index]==band_3[inner_index] or band_4[index]==band_4[inner_index]):
                candidatepairs.add((str(user_name_list[index]),str(user_name_list[inner_index])))

    for element in candidatepairs:
        element = list(element)
        element.sort()
        print json.dumps(element)


if __name__ == '__main__':
    read_input(sys.argv[1])
    prepare_hash_list()
    prepare_min_hash()
    calculate_min_hash()
    create_band()