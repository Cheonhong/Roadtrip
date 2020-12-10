import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;

public class Graph {
	int vertices;
	LinkedList<Edge>[] adjacencylist;
	ArrayList<String> city_key;
	Hashtable<String, Integer> attractions;

	static class Edge {
		int source, destination, d, time;

		public Edge(int source, int destination, int d, int time) {
			this.source = source;
			this.destination = destination;
			this.d = d;
			this.time = time;
		}
	}
	static class HeapNode {
		int vertex;
		int distance;
	}

	Graph(int vertices) {
		this.vertices = vertices;
		adjacencylist = new LinkedList[vertices]; // warning: type safety
		city_key = new ArrayList<String>();
		for (int i = 0; i < vertices; i++) {
			adjacencylist[i] = new LinkedList<>();
		}
	}

	public void addEdge(String city1, String city2, int d, int time) {
		// check if the newly added cities are within the city_key
		// if not add it to city key
		if (!city_key.contains(city1)) {
			city_key.add(city1);
		}
		if (!city_key.contains(city2)) {
			city_key.add(city2);
		}
		addEdge(city_key.indexOf(city1), city_key.indexOf(city2), d, time);
	}

	public void addEdge(int source, int destination, int d, int time) {
		Edge edge = new Edge(source, destination, d, time); // source and destination are int
		adjacencylist[source].addFirst(edge);

		edge = new Edge(destination, source, d, time);
		adjacencylist[destination].addFirst(edge);
	}

	public void create_attraction_hash() throws IOException {
		attractions = new Hashtable<>();
		
		String roadFile = "attractions.csv", line;
		BufferedReader br = new BufferedReader(new FileReader(roadFile));
        while ((line = br.readLine()) != null) {
            String[] info = line.split(",");
            attractions.put(info[0], city_key.indexOf(info[1]));
        }
        br.close();
	}
	public int dijkstra(int source, ArrayList<String> a) {
			int INFINITY = Integer.MAX_VALUE;
            boolean[] SPT = new boolean[vertices];

	        //create heapNode for all the vertices
            HeapNode [] heapNodes = new HeapNode[vertices];
            for (int i = 0; i <vertices ; i++) {
				SPT[i] = false;
                heapNodes[i] = new HeapNode();
                heapNodes[i].vertex = i;
                heapNodes[i].distance = INFINITY;
            }

            //decrease the distance for the first index
            heapNodes[source].distance = 0;

            //add all the vertices to the MinHeap
            MinHeap minHeap = new MinHeap(vertices);
            for (int i = 0; i <vertices ; i++) {
                minHeap.insert(heapNodes[i]);
			}
            //while minHeap is not empty
            while(!minHeap.isEmpty()){
                //extract the min
                HeapNode extractedNode = minHeap.extractMin();

                //extracted vertex
                int extractedVertex = extractedNode.vertex;
				SPT[extractedVertex] = true;
				for(int i = 0; i < a.size(); i++) {
					// if an attraction becomes true. This attraction is the closest to source
					if(SPT[attractions.get(a.get(i))] == true) {
						// remove the attraction from the arraylist
						// return the int for the closest attraction to change starting point
						int temp = attractions.get(a.get(i));
						a.remove(i);
						return temp;
					}
				}
                //iterate through all the adjacent vertices
				LinkedList<Edge> list = adjacencylist[extractedVertex];
				
                for (int i = 0; i <list.size() ; i++) {
                    Edge edge = list.get(i);
                    int destination = edge.destination;
					//only if  destination vertex is not present in SPT
                    if(SPT[destination]==false ) {
                        ///check if distance needs an update or not
						int newKey =  heapNodes[extractedVertex].distance + edge.d;
						int currentKey = heapNodes[destination].distance;
                        if(currentKey>newKey){
                            decreaseKey(minHeap, newKey, destination);
							heapNodes[destination].distance = newKey;
                        }
                    }
                }
	        
		}
		//printDijkstra(heapNodes, source);
		return -1;
	}
	public LinkedList<String> findRoute(int start, int end, ArrayList<String> a) {
		LinkedList<String> route = new LinkedList<String>();
		route.add(city_key.get(start)); // starting city is always start
		int point1 = start;
		int point2;
		while(!a.isEmpty()) { // iterate through the attractions until all the attractions added to route
			point2 = dijkstra(point1, a); // starting point changes to the closest attraction
			route.add(city_key.get(point2)); 
			point1 = point2;
		}
		route.add(city_key.get(end)); // ending city is always at the end
		return route;
	}
	public void printDijkstra(HeapNode[] resultSet, int sourceVertex){
		for (int i = 0; i <vertices ; i++) {
			System.out.println("Source Vertex: " + city_key.get(sourceVertex) + " to vertex " + city_key.get(i) +
					" distance: " + resultSet[i].distance);
		}
	}
	public void decreaseKey(MinHeap minHeap, int newKey, int vertex) {
		//get the index which distance's needs a decrease;
		int index = minHeap.indexes[vertex];

        //get the node and update its value
        HeapNode node = minHeap.mH[index];
        node.distance = newKey;
        minHeap.bubbleUp(index);
	}
	public void printGraph() { // testing if the graph stores everything correctly
		for (int i = 0; i < vertices; i++) {
			LinkedList<Edge> list = adjacencylist[i];
			for(int j = 0; j <  list.size(); j++) {
				System.out.println("Vertex- " + city_key.get(i) + " is connected to " +  city_key.get(list.get(j).destination) + 
				" with time " + list.get(j).time + " with distance " + list.get(j).d);
			}
		}
	}
	static class MinHeap{
        int capacity;
        int currentSize;
        HeapNode[] mH;
        int [] indexes; //will be used to decrease the distance


        public MinHeap(int capacity) {
            this.capacity = capacity;
            mH = new HeapNode[capacity + 1];
            indexes = new int[capacity];
            mH[0] = new HeapNode();
            mH[0].distance = Integer.MIN_VALUE;
            mH[0].vertex=-1;
            currentSize = 0;
        }

        public void display() {
            for (int i = 0; i <=currentSize; i++) {
                System.out.println(" " + mH[i].vertex + "   distance   " + mH[i].distance);
            }
            System.out.println("________________________");
        }

        public void insert(HeapNode x) {
            currentSize++;
            int idx = currentSize;
            mH[idx] = x;
            indexes[x.vertex] = idx;
            bubbleUp(idx);
        }

        public void bubbleUp(int pos) {
            int parentIdx = pos/2;
            int currentIdx = pos;
            while (currentIdx > 0 && mH[parentIdx].distance > mH[currentIdx].distance) {
                HeapNode currentNode = mH[currentIdx];
                HeapNode parentNode = mH[parentIdx];

                //swap the positions
                indexes[currentNode.vertex] = parentIdx;
                indexes[parentNode.vertex] = currentIdx;
                swap(currentIdx,parentIdx);
                currentIdx = parentIdx;
                parentIdx = parentIdx/2;
            }
        }

        public HeapNode extractMin() {
            HeapNode min = mH[1];
            HeapNode lastNode = mH[currentSize];
//            update the indexes[] and move the last node to the top
            indexes[lastNode.vertex] = 1;
            mH[1] = lastNode;
            mH[currentSize] = null;
            sinkDown(1);
            currentSize--;
            return min;
        }

        public void sinkDown(int k) {
            int smallest = k;
            int leftChildIdx = 2 * k;
            int rightChildIdx = 2 * k+1;
            if (leftChildIdx < heapSize() && mH[smallest].distance > mH[leftChildIdx].distance) {
                smallest = leftChildIdx;
            }
            if (rightChildIdx < heapSize() && mH[smallest].distance > mH[rightChildIdx].distance) {
                smallest = rightChildIdx;
            }
            if (smallest != k) {

                HeapNode smallestNode = mH[smallest];
                HeapNode kNode = mH[k];

                //swap the positions
                indexes[smallestNode.vertex] = k;
                indexes[kNode.vertex] = smallest;
                swap(k, smallest);
                sinkDown(smallest);
            }
        }

        public void swap(int a, int b) {
            HeapNode temp = mH[a];
            mH[a] = mH[b];
            mH[b] = temp;
        }

        public boolean isEmpty() {
            return currentSize == 0;
        }

        public int heapSize(){
            return currentSize;
        }
	}
}
