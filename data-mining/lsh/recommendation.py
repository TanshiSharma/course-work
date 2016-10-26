import json
import sys

user_movie_dict={}
candidate_pair_dict={}

def prepare_user_movie(file_path):
    #function to prepare movie dictionary for every user
    # format - {'user_id' : [list of movie ids].. }

    global user_movie_dict
    input_data=open(file_path)
    for line in input_data:
        line=json.loads(line.strip())
        user_movie_dict[line[0]]=line[1]


def get_candidate_set(file_path):
    #function to prepare candidate dictionary for users
    # reads the second input file and prepares a dictionary of the format
    # {'userid' : [similar user ids from input file']..}

    global candidate_pair_dict
    input_data=open(file_path)
    for pair in input_data:
        pair=json.loads(pair.strip())
        if pair[0] in candidate_pair_dict:
            candidate_pair_dict[pair[0]].append(pair[1])
        else:
            candidate_pair_dict[pair[0]]=[pair[1]]
        if pair[1] in candidate_pair_dict:
            candidate_pair_dict[pair[1]].append(pair[0])
        else:
            candidate_pair_dict[pair[1]]=[pair[0]]


def calculate_jacccard_similarity():
    # main function - using the candidate dictionary and user movie dictionary
    # calculated the jaccard score, find the top 5 similar users
    # and produce the movie recommendation

    global candidate_pair_dict,user_movie_dict
    output_list = []
    for key,value in candidate_pair_dict.iteritems():
        current_user = key
        related_user_list = value
        current_movie_list = user_movie_dict[current_user]

        for index in xrange(len(related_user_list)):
            user_movie_list = user_movie_dict[related_user_list[index]]

            intersection_len = len(set(current_movie_list).intersection(set(user_movie_list)))
            union_len = len(set(current_movie_list).union(set(user_movie_list)))
            jaccard_val = float(intersection_len)/union_len

            related_user_list[index] = (related_user_list[index], jaccard_val)

    for user,value in candidate_pair_dict.iteritems():
        value=sorted(value,key=lambda x:x[1], reverse=True)
        recommendation_list = get_Recommendation(give_distinct_names(value))
        output=list(set(recommendation_list)-set(user_movie_dict[user]))
        if output:
            output.sort()
            output_list.append([user, output])

    for entry in output_list:
        entry = json.dumps(entry)
        print entry


def get_Recommendation(value):
    # input - list of usernames
    # for a given list of users, returns the set of movies which were seen by more than 3 users
    # of current list

    global user_movie_dict
    movie_reference_dict={}
    recommendation_list=[]

    for username in value:
        getmovie_list=user_movie_dict[username]
        for movie in getmovie_list:
            if movie not in movie_reference_dict:
                movie_reference_dict[movie]=1
            else:
                movie_reference_dict[movie]=movie_reference_dict[movie]+1

    for key,value in movie_reference_dict.iteritems():
        if value >=3:
            recommendation_list.append(key)
    return  recommendation_list


def give_distinct_names(value_list):
    # input - list of (user, jaccard score)
    # return - names of users having top 5 jaccard score in this list

    name_list = []
    distinct_value_list = []
    for element in value_list:
        name=element[0]
        value=element[1]

        if len(distinct_value_list) < 5:
            distinct_value_list.append(value)
        if len(distinct_value_list)==5 and distinct_value_list[-1]!= value:
            break
        name_list.append(name)
    return name_list

if __name__ == '__main__':
    prepare_user_movie(sys.argv[1])
    get_candidate_set(sys.argv[2])
    calculate_jacccard_similarity()