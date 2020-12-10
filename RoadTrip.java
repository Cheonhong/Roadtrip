import java.util.*;
import java.io.*;

public class RoadTrip {
    public static List<String> route(String start, String end, ArrayList<String> attractions) throws IOException{
        int vertices = 0; // number of vertices

        ArrayList<String> cities = new ArrayList<String>();
        String roadFile = "roads.csv", line;

        BufferedReader br = new BufferedReader(new FileReader(roadFile));
        while ((line = br.readLine()) != null) {
            String[] info = line.split(",");
            if(!cities.contains(info[0]))
                cities.add(info[0]);
            if(!cities.contains(info[1]))
                cities.add(info[1]);
        }
        br.close();

        vertices = cities.size();

        Graph graph = new Graph(vertices);

        String File = "roads.csv", line1;

        BufferedReader br1 = new BufferedReader(new FileReader(File));
        while ((line1 = br1.readLine()) != null) {
            String[] info = line1.split(",");
            graph.addEdge(info[0], info[1], Integer.parseInt(info[2]), Integer.parseInt(info[3]));
        }
        br.close();

        graph.create_attraction_hash();

        //graph.printGraph();

        LinkedList<String> route = graph.findRoute(graph.city_key.indexOf(start), graph.city_key.indexOf(end), attractions);;// get the final route linked list
        return route;
    }
    public static void main(String[] args) throws IOException{
        Scanner input = new Scanner(System.in);

        System.out.println("Enter starting city: ");
        String start = input.nextLine();
        System.out.println("Enter ending city: ");
        String end = input.nextLine();

        ArrayList<String> attractions = new ArrayList<String>();
        boolean repeat = true;
        while (repeat) {
            System.out.println("Enter attraction(s) type n when finished: ");
            String attraction = input.nextLine();
            if(!attraction.equals("n")) {
                attractions.add(attraction);
            } else {
                repeat = false;
            }
        }

        List<String> route = route(start, end, attractions);

        System.out.println(route);

        input.close();
        
    }
}
