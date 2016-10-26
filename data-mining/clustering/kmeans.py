import math
import sys
import json


def get_distance_points(point1, point2):
    """ Given two points represented by point1 and point2, returns the distance between them
    """
    sum_val = 0
    for index in xrange(len(point1)):
        value = pow(point1[index] - point2[index], 2)

        sum_val += value

    return math.sqrt(sum_val)


def get_centre_point(input_list):
    """ Given a list of points, returns the centroid as a tuple

    Centroid is calculated as the mean of the dimensional values.
    """
    length = len(input_list)
    num_elements = len(input_list[0])
    output_list = [0]*num_elements

    for element in input_list:
        for index2 in xrange(num_elements):
            output_list[index2] += element[index2]

    for index in xrange(num_elements):
        output_list[index] /= length

    return tuple(output_list)


def get_centroids(point_list, centroid_list, k):
    """ From a list of points, selects k points that can act as centroids.

    Uses the concept of maximising the minimum distance between a point and existing centroids to find out the other centroids.
    """
    while len(centroid_list) < k:
        point_distance_list =[]
        for point in point_list:
            min_distance = sys.maxint
            if point in centroid_list:
                point_distance_list.append(-1)
            else:
                for centroid in centroid_list:
                    min_distance = min(min_distance,get_distance_points(point, centroid))

                point_distance_list.append(min_distance)

        index = point_distance_list.index(max(point_distance_list))
        centroid_list.append(point_list[index])
        centroid_list.sort()


def create_cluster(point_list, centroid_list):
    """Given a point list and list of centroids, associates each point to one of the clusters represented by a centroid

    """
    dict_cluster = {}
    for centroid in centroid_list:
        dict_cluster[centroid] = []

    for point in point_list:
        if point not in centroid_list:
            current_cluster = None
            min_distance = sys.maxint
            for key in centroid_list:
                distance = get_distance_points(point, key)
                if distance < min_distance:
                    min_distance = distance
                    current_cluster = key

            dict_cluster[current_cluster].append(point)

    return dict_cluster


def kmeans(data_points, k):
    """Performs k means to find out the group of points that can exist within the same cluster.

    Returns a list of clusters and cohesion value.
    """
    clusters = []
    centroid_list = [data_points[0]]

    get_centroids(data_points, centroid_list, k)
    dict_cluster = create_cluster(data_points, centroid_list)
    prev_centroid_list = None

    while prev_centroid_list != centroid_list:
        prev_centroid_list = centroid_list
        centroid_list = []
        for key in prev_centroid_list:
            value = dict_cluster[key]
            if not value:
                continue
            if key in data_points:
                value.append(key)
                new_centroid = get_centre_point(value)
            else:
                new_centroid = get_centre_point(value)
            centroid_list.append(new_centroid)

        if len(centroid_list) < k:
            get_centroids(data_points, centroid_list, k)

        centroid_list.sort()
        dict_cluster = create_cluster(data_points, centroid_list)

    for key, value in dict_cluster.iteritems():
        if key in data_points:
            value.append(key)
        value.sort()
        clusters.append(value)

    diameter = 0
    for cluster in clusters:
        max_distance = 0
        for index in xrange(len(cluster)):
            for index2 in xrange(index+1, len(cluster)):
                max_distance = max(max_distance, get_distance_points(cluster[index], cluster[index2]))
        diameter += max_distance
    cohesion = diameter/k

    return clusters, cohesion


if __name__ == '__main__':
    input_file = open(sys.argv[1])
    data_points =[]
    for line in input_file:
        data_points.append(tuple(json.loads(line.strip())))
    num_clusters = int(sys.argv[2])

    if num_clusters > len(data_points):
        print 'Invalid number of clusters present'
    else:
        clusters, cohesion = kmeans(data_points, int(sys.argv[2]))
        for cluster in clusters:
            print cluster
        print cohesion