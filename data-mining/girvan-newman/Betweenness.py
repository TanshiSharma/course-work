import json
import sys

def get_graph_dict(input_path):
    """Creates a graph dictionary and edge list from the given input.

    """

    edge_list = []

    dict_graph = {}
    file = open(input_path, 'r')
    for line in file:
        edge = json.loads(line)
        source, dest = edge[0], edge[1]
        edge_list.append((edge[0], edge[1]))

        node_list = dict_graph.setdefault(source, [])
        node_list.append(dest)

        node_list = dict_graph.setdefault(dest, [])
        node_list.append(source)

    return dict_graph, edge_list


def perform_bfs(graph_dict, source):
    """For a given source and graph, performs breadth first search.

    Returns all reachable nodes, dictionary of node to all its predecessors and dictionary of number of shortest paths for each node.
    """
    all_reachable_nodes = []
    dict_num_shortest_path = {}
    predecessor_dict = {}
    dict_path_cost = {}

    for node in graph_dict:
        predecessor_dict[node] = []
        dict_num_shortest_path[node] = 0

    # initialise for source
    dict_path_cost[source] = 0
    dict_num_shortest_path[source] = 1

    queue = [source]
    while queue:
        current_node = queue.pop(0)
        all_reachable_nodes.append(current_node)
        path_cost = dict_path_cost[current_node]
        num_shortest_path = dict_num_shortest_path[current_node]

        for adjacent_node in graph_dict[current_node]:
            if adjacent_node not in dict_path_cost:
                dict_path_cost[adjacent_node] = path_cost + 1
                queue.append(adjacent_node)

            if dict_path_cost[adjacent_node] == path_cost + 1:
                dict_num_shortest_path[adjacent_node] += num_shortest_path
                predecessor_dict[adjacent_node].append(current_node)

    return all_reachable_nodes, predecessor_dict, dict_num_shortest_path


def calculate_betweenness(betweenness_dict, all_reachable_nodes, predecessor_dict, dict_num_shortest_path, source_node):
    """Calculates betweenness for all the entry present in the dictionary(node and edges).

    """
    dict_flow_from_below = {}
    for node in all_reachable_nodes:
        dict_flow_from_below[node] = 0.0

    while all_reachable_nodes:
        current_node = all_reachable_nodes.pop()
        partition_val = (dict_flow_from_below[current_node] + 1.0)/dict_num_shortest_path[current_node]

        for parent_node in predecessor_dict[current_node]:
            flow_value = dict_num_shortest_path[parent_node] * partition_val
            dict_flow_from_below[parent_node] += flow_value
            if (current_node, parent_node) not in betweenness_dict:
                betweenness_dict[(parent_node, current_node)] += flow_value
            else:
                betweenness_dict[(current_node, parent_node)] += flow_value

        if current_node != source_node:
            betweenness_dict[current_node] += dict_flow_from_below[current_node]


def main(graph_dict, edge_list):
    """For a given graph and edge list displays the betweenness values for all the edges.

    """
    betweenness_dict = {}
    for source in graph_dict:
        betweenness_dict[source] = 0.0
    for edge in edge_list:
        betweenness_dict[edge] = 0.0

    for source in graph_dict:
        all_reachable_nodes, predecessor_dict, dict_num_shortest_path = perform_bfs(graph_dict, source)
        calculate_betweenness(betweenness_dict, all_reachable_nodes, predecessor_dict, dict_num_shortest_path, source)

    for source in graph_dict:
        del betweenness_dict[source]

    for entry, value in betweenness_dict.iteritems():
        entry_list = [entry[0], entry[1]]
        entry_list.sort()
        entry = json.dumps(entry_list)
        value /= 2.0
        print '%s : %s' % (entry, value)



if __name__ == '__main__':
    graph_dict, edge_list = get_graph_dict(sys.argv[1])
    main(graph_dict, edge_list)
