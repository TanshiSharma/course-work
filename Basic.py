import json

import collections


def create_replace_name_map(actor_list,director_list,reviews_file_path,title_id):

    actor_dict=collections.OrderedDict()
    director_dict=collections.OrderedDict()
    general_dict=collections.OrderedDict()


    for actor in actor_list:
        if actor in actor_dict:
            continue
        else:
            actor_dict[actor]='MOVIE_CAST'
            actor_name=actor.split(" ")
            for names in actor_name:
                actor_dict[names]='MOVIE_CAST'

    for director in director_list:
        if director in director_dict:
            continue
        else:
            director_dict[director]="MOVIE_DIRECTOR"
            director_name=director.split(" ")
            for names in director_name:
                director_dict[names]="MOVIE_DIRECTOR"



    review=json.load(open(reviews_file_path))
    output_filepath = 'processed_%s' % (title_id)
    output = open(output_filepath, 'w')

    for reviews in review:

        reviews['review']=reviews['review'].replace('\n',' ')
        reviews['review'] = reviews['review'].replace('<br>', '')

        for key,value in director_dict.items():
            reviews['review']=reviews['review'].replace(key,value)

        for key,value in actor_dict.items():
            reviews['review']=reviews['review'].replace(key,value)

        for key,value in general_dict.items():
            reviews['review']=reviews['review'].replace(key,value)

        output.write(json.dumps(reviews))
        output.write("\n")



    output.close()
    review.close()

create_replace_name_map(['Ryan Reynolds','Morena Baccarin','T.J. Miller','Ed Skrein'],['Tim Miller'],'/home/tanshi/Downloads/CSCI544_Final_Project/scrapyIMDB/data/reviews_tt1431045.json','tt1431045')