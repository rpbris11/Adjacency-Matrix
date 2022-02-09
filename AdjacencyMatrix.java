import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * * Ryan Brisbane - Adjacency Matrix Project - COSC 310
 * * Dec 11 2021 9:40 pm
 * This class creates an adjacency matrix based on a provided number of vertices. Stored at each point
 * is the distance from the node listed in the row (i) to the node listed in the column (j).
 */
public class AdjacencyMatrix {

    int vertices;
    int matrix[][];
    int infinity = Integer.MAX_VALUE;

    /**
     * Constructor. Initializes the graph of size capacity by capacity, and sets all values to zero initially
     *
     * @param capacity - number of vertices in the graph
     */
    public AdjacencyMatrix(int capacity) {
        vertices = capacity;
        matrix = new int[capacity][capacity];

        for (int i = 0; i < capacity; i++) {
            for (int j = 0; j < capacity; j++) {
                matrix[i][j] = 0;
            }
        }
    }

    /**
     * Inserts an edge into the matrix, taking its distance/weight into account
     *
     * @param source      - starting vertex
     * @param destination - destination vertex
     * @param weight      - distance from source to destination
     */
    public void addEdge(String source, String destination, int weight) {
        //this stuff converts between char and int to access the matrix indices appropriately
        char sourceLetterIndex = source.charAt(0);
        char letterCount = 'A';
        int numberCount = 0;
        while (letterCount < sourceLetterIndex) {
            letterCount++;
            numberCount++;
        }
        int sourceIndex = numberCount;

        letterCount = 'A';
        numberCount = 0;
        char destLetterIndex = destination.charAt(0);
        while (letterCount < destLetterIndex) {
            letterCount++;
            numberCount++;
        }
        int destIndex = numberCount;
        if (destination.charAt(0) == 'X') {
            destIndex = vertices - 1;
        }
        //set value of those indices to the distance
        matrix[sourceIndex][destIndex] = weight;
    }

    /**
     * Dijkstra's algorithm: used to find the shortest path to the destination node, X.
     *
     * @param source - starting vertex
     * @param dest   - destination vertex
     */
    public void Dijkstra(int source, int dest) {

        int u, v;

        int[] distance = new int[vertices];
        int[] marked = new int[vertices];
        int[] pathLength = new int[vertices];
        int[] parent = new int[vertices];

        parent[source] = -1;

        for (int i = 0; i < vertices; i++) {
            distance[i] = infinity;
        }

        distance[source] = 0;

        for (int i = 0; i < vertices - 1; i++) {
            u = minDistance(distance, marked);
            if (u == infinity) {
                break;
            } else {
                marked[u] = 1;
                for (v = 0; v < vertices; v++) {
                    if (marked[v] == 0 && matrix[u][v] != 0
                            && distance[u] + matrix[u][v] < distance[v]) {
                        parent[v] = u;
                        pathLength[v] = pathLength[parent[v]] + 1;
                        distance[v] = distance[u] + matrix[u][v];
                    } else if (marked[v] == 0 && matrix[u][v] != 0
                            && distance[u] + matrix[u][v] < distance[v]
                            && pathLength[u] + 1 < pathLength[v]) {
                        parent[v] = u;
                        pathLength[v] = pathLength[u] + 1;
                    }
                }
            }
        }
        //informs user of the distance as well as if any potions will be required
        if (distance[dest] != infinity) {
            printDijkstra(parent, dest);
            System.out.println("\n\nThe total distance is " + distance[dest] + " steps. ");
            if (distance[dest] > 100) {
                double potions = (distance[dest] - 100) / 50.0;
                System.out.println((Math.ceil(potions)) + " potions will be needed.");
            } else {
                System.out.println("No potions required.");
            }
        } else {
            //converting back to letter from number index
            char letterCount = 'A';
            int numberCount = 0;
            while (numberCount < source) {
                letterCount++;
                numberCount++;
            }
            char sourceLetter = letterCount;
            letterCount = 'A';
            numberCount = 0;
            while (numberCount < dest) {
                letterCount++;
                numberCount++;
            }
            char destLetter = letterCount;
            if (dest == vertices - 1) {
                destLetter = 'X';
            }
            //in the event there isn't a path to the destination node
            System.out.println("There is no path from " + sourceLetter + " to " + destLetter);
            System.out.println("No potions required.");
        }
    }

    /**
     * Locates the minimum distance to a neighboring node. Used within the Dijstrka method
     *
     * @param distance - array of distances to each node
     * @param marked   - array denoting whether a node has been visited or not yet
     * @return - minimum distance to a neighboring node
     */
    public int minDistance(int[] distance, int[] marked) {
        int min = infinity;
        int minIndex = -1;

        for (int v = 0; v < vertices; v++) {
            if (marked[v] == 0 && distance[v] < min) {
                min = distance[v];
                minIndex = v;
            }
        }
        return min == infinity ? infinity : minIndex; //fancy boolean stuff!!
    }

    /**
     * Prints out the shortest path to the destination node using the Dijkstra method
     *
     * @param parent - array of vertices listing which associated vertex is that one's parent. -1 for source
     * @param dest   - destination vertex
     */
    public void printDijkstra(int[] parent, int dest) {
        //more conversion from index to letter
        char letterCount = 'A';
        int numberCount = 0;
        while (numberCount < dest) {
            letterCount++;
            numberCount++;
        }
        //if the node is the source it is printed first
        if (parent[dest] == -1) {
            System.out.print(letterCount);
            return;
        }
        //recursive call
        printDijkstra(parent, parent[dest]);
        if (dest == vertices - 1) {
            System.out.print(" -> X");
            return;
        }
        System.out.print(" -> " + letterCount);
    }

    /**
     * Main method - reads the input file (which you can change) and adds the edges and nodes to
     * newly created adjacency matrix. Also calls the Dijkstra search and returns the shortest path.
     * I also figured out why it wasn't accepting the file name unless it was
     * hard coded - I was putting the files in the SRC file instead of the project folder.
     *
     * @param args
     */
    public static void main(String[] args) {
        //accessing the input file and storing appropriately
        List<List<String>> inFile = new ArrayList<>();
        try {
            File f = new File("map_0010.txt");
            System.out.println("Hello! For file " + f.getName() + ": ");
            Scanner read = new Scanner(f);
            while (read.hasNextLine()) {
                List<String> values = new ArrayList<>();
                Scanner lineReader = new Scanner(read.nextLine());
                while (lineReader.hasNext()) {
                    values.add(lineReader.next());
                }
                inFile.add(values);
            }
        } catch (IOException e) {
            System.err.println(e);
        }
        //removing comments - if index 0 is a #, removes the sublist entirely
        int test = inFile.size();
        for (int i = test - 1; i >= 0; i--) {
            if (inFile.get(i).get(0).equals("#")) {
                inFile.remove(i);
            }
        }
        int size = inFile.size() + 1;
        AdjacencyMatrix matrix = new AdjacencyMatrix(size);

        //adds the first entry into the matrix since the letters
        // don't follow the same order as the rest of  the file
        matrix.addEdge(inFile.get(0).get(0), inFile.get(0).get(1), Integer.parseInt(inFile.get(0).get(2)));
        for (int i = 0; i < size - 1; i++) {
            //loop through the list and add each associated edge to the matrix
            if (inFile.get(i).size() > 1) {
                //had an issue with 0010.txt - would break because there was only one node related to each one
                matrix.addEdge(inFile.get(i).get(0), inFile.get(i).get(1), Integer.parseInt(inFile.get(i).get(2)));
            }
            //iterates over the rest of the lines
            for (int j = 3; j < inFile.get(i).size(); j += 2) {
                matrix.addEdge(inFile.get(i).get(0), inFile.get(i).get(j), Integer.parseInt(inFile.get(i).get(j + 1)));
            }
        }
        //output! finally!
        matrix.Dijkstra(0, size - 1);


    }
}
