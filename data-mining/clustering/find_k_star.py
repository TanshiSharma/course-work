import sys
import json
from kmeans import kmeans


def binary_search(data_points, start, end, threshold):
    """Performs binary search based on cohesion value to determine the optimum value of k* and then returns it.

    """
    if start+1 == end:
        start_clusters, start_cohesion = kmeans(data_points, start)
        end_clusters, end_cohesion = kmeans(data_points, end)
        if start_cohesion > end_cohesion:
            return end
        return start

    mid = (start+end)/2
    mid_clusters, mid_cohesion = kmeans(data_points, mid)
    end_clusters, end_cohesion = kmeans(data_points, end)


    if float(abs(mid_cohesion - end_cohesion))/(mid_cohesion * abs(end-mid)) < threshold:
        return binary_search(data_points, start, mid, threshold)
    else:
        return binary_search(data_points, mid, end, threshold)


def find_k_star(data_points, threshold):
    """ Starting with 1 cluster, finds out the optimum value of k*.

    In each iteration, the value of number of clusters is doubled.
    """
    num_clusters = 1
    prev_cohesion = None
    while True:
        clusters, cohesion = kmeans(data_points, num_clusters)
        if prev_cohesion:
            change_rate = float(abs(cohesion - prev_cohesion))/(prev_cohesion * num_clusters/2)
            if change_rate < threshold:
                break
        prev_cohesion = cohesion
        num_clusters *= 2

    if num_clusters > len(data_points):
        print len(data_points)
        return

    # perform binary search to find kstar. As num of clusters have already been doubled in previous for loop
    # so start corresponds to num_clusters/4 and end to num_clusters/2
    kstar = binary_search(data_points, num_clusters/4, num_clusters/2, threshold)

    print kstar


if __name__ == '__main__':
    input_file = open(sys.argv[1])
    data_points =[]
    for line in input_file:
        data_points.append(tuple(json.loads(line.strip())))
    threshold = float(sys.argv[2])

    find_k_star(data_points, threshold)

