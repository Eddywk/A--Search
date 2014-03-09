import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

class Node{
	Character id;
	int x;
	int y;
	float h;
	float g;
	Node parent=null;
	HashMap<Character,Float> to;
	Node[] neighbors;
	public Node(Character name){
		id = name;
		to = new HashMap<Character, Float>();
	}
	
	public float getf(){
		return h+g;
	}	
}

class Edge{
	Character start;
	Character end;
	float weight;
	
	public Edge(Character s, Character e, float w){
		start=s;
		end=e;
		weight=w;
	}
}

class State{
	Character cur_checkpoint;
	List<Character> visited_checkpoints = new ArrayList<Character>();
	float h;
	float g;
	public float getf(){
		return h+g;
	}
	public State(Character cur){
		cur_checkpoint=cur;
	}
}

public class tsp {	
static int x_num=0, y_num=0;
static Character[][] Map; //2-d array for storing char
static char[] NodeList; //Node List
static HashMap<Character, Node> graph = new HashMap<Character, Node>(); //whole graph
static boolean[][] closedset;//set of nodes that have been estimated
static Node[][] Nodes;

public static Node getNode(Character id){//return nodes function
	if(graph.containsKey(id)){
		return graph.get(id);
	}
	else{
		Node n = new Node(id);
		graph.put(id, n);
		return n;
	}
}


public static ArrayList<String> A_star(Character Start, Character Goal){             //A*
	boolean tentative_is_better;
	float tentative_g;
//	PriorityQueue<Node> openset = new PriorityQueue<Node>(200);
	ArrayList<String> log = new ArrayList<String>();
	Queue<Node> openset = new PriorityQueue<Node>(50,new Comparator<Node>() //tiebreaking issue handle
			{
			public int compare( Node a,Node b )
			{
			 if(a.getf()<b.getf()){
				 return -1;
			 }else if(a.getf()==b.getf()){
				 if(a.y>b.y){
					 return 1; 
				 }else if(a.y==b.y){
					 if(a.x<b.x){
						 return 1;
					 }else{
						 return -1;
					 }
				 }else{
					 return -1;
				 }
			 }else{
				 return 1;
			 }
			}
			});
	Node start = Nodes[getNode(Start).x][getNode(Start).y];
	Node goal = Nodes[getNode(Goal).x][getNode(Goal).y];
	//===================initialization=======================
    for(int i=0;i<x_num;i++){//initialize closedset
    	for(int j=0;j<y_num;j++){
    		closedset[i][j]=false;
    	}
    }    
    for(int i=0;i<x_num;i++){
    	for(int j=0;j<y_num;j++){//initialize all nodes' g and h
			Nodes[i][j].g=0;
    		Nodes[i][j].h=Math.abs(goal.x-Nodes[i][j].x)+Math.abs(goal.y-Nodes[i][j].y);
//    		System.out.println("i: "+i+" j: "+j+" h: "+Nodes[i][j].h+"\n");
    	}
    }
    openset.add(start);//add start into queue
    //========================================================
    Node cur,neighbor;    
    //=======================Main Loop========================
    while(!openset.isEmpty()){
    	cur = openset.poll();
    	if(closedset[cur.x][cur.y])
    		continue;
    	closedset[cur.x][cur.y]=true;
//    	System.out.println(cur.x+","+cur.y+","+cur.g+","+cur.h+","+cur.getf()+"\n");   	
    	log.add(cur.x+","+cur.y+","+cur.g+","+cur.h+","+cur.getf());//log data
    	
    	if(closedset[goal.x][goal.y]==true){ 
//    		System.out.println(start.id+" to "+ goal.id+" = "+cur.g);
    		graph.get(start.id).to.put(goal.id, cur.g);
    		graph.get(goal.id).to.put(start.id, cur.g);
    		return log;
    		}
    	
    	Node[] nn = {Nodes[cur.x][cur.y+1],Nodes[cur.x+1][cur.y],Nodes[cur.x-1][cur.y],Nodes[cur.x][cur.y-1]};
    	cur.neighbors=nn;
    	
    	for(int i=0;i<cur.neighbors.length;i++){
    		neighbor=cur.neighbors[i];
			if(neighbor.id.equals('*')||closedset[neighbor.x][neighbor.y])
				   continue;
		    tentative_g=cur.g+1;
		    if(!openset.contains(neighbor)){
		    	//openset.add(neighbor);
		    	//neighbor.parent=cur;
		    	//neighbor.g=neighbor.parent.g+1;
		        tentative_is_better=true;
		        }
		     else if(tentative_g<neighbor.g){
		    	 tentative_is_better=true;
		     }
		     else{
		    	 tentative_is_better=false;
		     }
		    
		    if(tentative_is_better){		    	
		    	Node newNeighbor = neighbor;
		    	newNeighbor.parent = cur;
		    	newNeighbor.g = tentative_g;
		    	openset.add(newNeighbor);		    	
		    }
	    }
    	
    }
    
return null;
    
}

public static void getSPG(String outputfile, String logfile) throws IOException{//task1  
	File output_path, output_log;
	output_path = new File(outputfile);
	output_log = new File(logfile);
 		
    		if(output_path.exists()||output_log.exists()){
    			System.out.println("File has existed!");
    		}else{
    				if(output_path.createNewFile()&&output_log.createNewFile()){
    			    System.out.println(output_path+" has been successfully created");
    			    System.out.println(output_log+" has been successfully created");
    				FileWriter writer_path=new FileWriter(output_path);
    				FileWriter writer_log=new FileWriter(output_log);
    				
    				for(int i=0;i<NodeList.length;i++){
    		    	for(int j=i+1;j<NodeList.length;j++){
    		    		Node Start, Goal;
    		    		float weight;
    		    		Start = graph.get(NodeList[i]);
    		    		Goal = graph.get(NodeList[j]);
                        //write output_log_t1
    		    		writer_log.write("from "+"'"+NodeList[i]+"'"+" to "+"'"+NodeList[j]+"'"+"\n");
    		    		writer_log.write("-----------------------------------------------"+"\n");
    		    		ArrayList<String> log = A_star(NodeList[i],NodeList[j]);
    		    		//write output_path_t1
    		    		weight=Start.to.get(Goal.id);
    		    		writer_path.write(NodeList[i]+","+NodeList[j]+","+weight+"\n");
    		    		//write output_path_t1
    		    		for(int m=0;m<log.size();m++){
    		    			writer_log.write(log.get(m)+"\n");
    		    		}
    		    		writer_log.write("-----------------------------------------------"+"\n");
    		    		//write output_log_t1
    		    	}
    		    }    				
    				writer_path.close();
    				writer_log.close();
    				System.out.println("Completed Files Writing!");
    				}else{
    				  System.out.println("Failure to create file.");
    				}    				
    		}
    		
}

public static float findMST(Node curNode, ArrayList<Character> subgraph){//get MST as h
	float total=0;
	List<Character> vertexList = new ArrayList<Character>();//node set
	List<Character> newVertex = new ArrayList<Character>();//nodes that have been visited
	Queue<Edge> Edges = new PriorityQueue<Edge>(50,new Comparator<Edge>()
			{
			public int compare( Edge a,Edge b )
			{
                if(a.weight<b.weight){
                	return -1;
                }else{
                	return 1;
                }
			}
			});
	for(int i=0;i<subgraph.size();i++){
		vertexList.add(subgraph.get(i));
	}

    newVertex.add(curNode.id);
    vertexList.remove(vertexList.indexOf(curNode.id));
    
    while(!vertexList.isEmpty()){
    	for(int i=0;i<newVertex.size();i++){
    		for(int j=0;j<vertexList.size();j++){
    			Node cur = graph.get(newVertex.get(i));
    			if(cur.id.equals(vertexList.get(j))){
    				break;
    			}
    			Edges.add(new Edge(cur.id,vertexList.get(j),cur.to.get(vertexList.get(j))));
    		}
    	}
    	Edge minEdge = Edges.poll();
    	total+=minEdge.weight;
    	newVertex.add(minEdge.end);
    	vertexList.remove(vertexList.indexOf(minEdge.end));
    	Edges.clear();
    }
	return total;
}

public static void getTSP(String outputfile, String logfile) throws IOException{//task2
	for(int i=0;i<NodeList.length;i++){//building graph
    	for(int j=i+1;j<NodeList.length;j++){
    		A_star(NodeList[i],NodeList[j]);
    	}
	}
	graph.get('A').to.put('A', (float) 0);
	
	Comparator<State> comparator = new Comparator<State>() //tiebreaking issue handle
			{
			public int compare( State a,State b )
			{
			 if(a.getf()<b.getf()){
				 return -1;
			 }else if(a.getf()==b.getf()){
                  if(a.cur_checkpoint.hashCode()<b.cur_checkpoint.hashCode()){
                	  return -1;
                  }else{
                	  return 1;
                  }
			 }else{
				 return 1;
			 }
			}
			};
	Queue<State> openset = new PriorityQueue<State>(100,comparator);
	ArrayList<Character> list = new ArrayList<Character>();
	ArrayList<Character> path = new ArrayList<Character>();
	ArrayList<String> log = new ArrayList<String>();
	for(int i=0;i<NodeList.length;i++){
		list.add(NodeList[i]);
	}
	
	Set set=graph.get('A').to.entrySet();
    Iterator it=set.iterator();
    while(it.hasNext()){
        Map.Entry me=(Map.Entry)it.next();
        Character c =(Character)me.getKey();
        if(c.equals('A'))
        	continue;
        State s = new State(c);
        s.g=graph.get('A').to.get(c);
        s.h=findMST(graph.get(c), list);
        s.visited_checkpoints.add('A');
        s.visited_checkpoints.add(c);
        openset.add(s);
//        vertexList.add((Character)me.getKey());
    }
    log.add("A"+","+"0.0"+","+findMST(graph.get('A'), list)+","+findMST(graph.get('A'), list));
    
    ArrayList<Character> unvisited = new ArrayList<Character>();
    float total=0;
	while(!openset.isEmpty()){
		StringBuilder sb = new StringBuilder();
		State cur = openset.poll();
		for(int i=0;i<cur.visited_checkpoints.size();i++){
//			System.out.println(cur.visited_checkpoints.get(i));
			sb.append(cur.visited_checkpoints.get(i));
		}
//		System.out.println(sb.toString());
//		System.out.println("g: "+cur.g+" h: "+cur.h+" f: "+cur.getf()+" Cur_CP: "+cur.cur_checkpoint);
		log.add(sb.toString()+","+cur.g+","+cur.h+","+cur.getf());
//		System.out.println("#####################");
		
		if(cur.visited_checkpoints.size()==NodeList.length){
//			System.out.println("Finished!!!!!!!!");
			sb.append("A");
			for(int i=0;i<sb.toString().length();i++){
				path.add(sb.toString().charAt(i));
			}
			total=cur.getf();
			log.add(sb.toString()+","+cur.getf()+","+"0.0"+","+cur.getf());
			break;
			}
		
	    Iterator iter=graph.get(cur.cur_checkpoint).to.entrySet().iterator();//get current Node's neighbors

	    Character cur_c=null;
	    while(iter.hasNext()){//for all its neighbors
	    	Map.Entry Me=(Map.Entry)iter.next();
	        cur_c =(Character)Me.getKey();//current neighbor's name
	    	if(!cur.visited_checkpoints.contains(cur_c)){//if it has been visited: PASS
	    		unvisited.add(cur_c);
	    	}
	    }
	    if(unvisited.contains(cur.cur_checkpoint)){
	    	unvisited.remove(unvisited.indexOf(cur.cur_checkpoint));
	    }
	    if(!unvisited.contains('A')){
	    	unvisited.add('A');
	    }
    	for(int i=0;i<unvisited.size();i++){
//    	System.out.println(unvisited.get(i));
    	if(unvisited.get(i).equals('A')||unvisited.get(i).equals(cur.cur_checkpoint))
    		continue;
    	State cur_s = new State(unvisited.get(i));
    	cur_s.g=cur.g+graph.get(cur.cur_checkpoint).to.get(cur_s.cur_checkpoint);
    	cur_s.h=findMST(graph.get(cur_s.cur_checkpoint), unvisited);
    	for(int j=0;j<cur.visited_checkpoints.size();j++){
    		cur_s.visited_checkpoints.add(cur.visited_checkpoints.get(j));
    	}
//    	cur_s.visited_checkpoints=cur.visited_checkpoints;
    	if(!cur_s.visited_checkpoints.contains(cur_s.cur_checkpoint)){
    		cur_s.visited_checkpoints.add(cur_s.cur_checkpoint);
    	}
    	if(!openset.contains(cur_s)){
    	    openset.add(cur_s);
    	}
    	}
	    unvisited.clear();
		
	}
		
	File output_path, output_log;
	output_path = new File(outputfile);
	output_log = new File(logfile);
 		
    		if(output_path.exists()||output_log.exists()){
    			System.out.println("File has existed!");
    		}else{
    				if(output_path.createNewFile()&&output_log.createNewFile()){
    			    System.out.println(output_path+" has been successfully created");
    			    System.out.println(output_log+" has been successfully created");
    				FileWriter writer_path=new FileWriter(output_path);
    				FileWriter writer_log=new FileWriter(output_log);
    				//write path_output file
   				    for(int i=0;i<path.size();i++){
   				    	writer_path.write(path.get(i)+"\n");
   				    }
   				    writer_path.write("Total Tour Cost: "+total);
   				    //write log_output file
   				    for(int j=0;j<log.size();j++){
   				    	writer_log.write(log.get(j)+"\n");
   				    }
    				writer_path.close();
    				writer_log.close();
    				System.out.println("Completed Files Writing!");
    				}else{
    				  System.out.println("Failure to create file.");
    				}    				
    		}
	
	
}


public static void readInput(String filename) throws IOException{//read input file
	x_num=0;
	y_num=0;
	String nodes="";
	List list = new ArrayList();
	String line =null;
	FileReader fin=new FileReader(filename);
	BufferedReader bw = new BufferedReader(fin);
	while((line=bw.readLine() )!=null){
		list.add(line);
	}
	bw.close();
	y_num=list.size();//y:17
	x_num=list.get(0).toString().length();//x:18
	Map = new Character[x_num][y_num];
	closedset = new boolean[x_num][y_num];
	Nodes = new Node[x_num][y_num];
	//read input file into map[][]
	for(int i=0;i<x_num;i++){//18
		for(int j=0;j<y_num;j++){//17
//			Map[j][i]=Character.valueOf(line.charAt(j));
			Map[i][j]=list.get(j).toString().charAt(i);
		}
	}
	fin.close();
	StringBuilder mynodes = new StringBuilder();
	for(int i=0;i<x_num;i++){
		for(int j=0;j<y_num;j++){
			Nodes[i][j] = new Node(Map[i][j]);
			Nodes[i][j].x=i;Nodes[i][j].y=j;
			if(Map[i][j]!=' '&&Map[i][j]!='*'&&Map[i][j]!=null){//create nodes
				mynodes.append(Map[i][j]);
				getNode(Map[i][j]).x=i;
				getNode(Map[i][j]).y=j;
			}
		}
	}
	nodes=mynodes.toString();
	NodeList = nodes.toCharArray();
	java.util.Arrays.sort(NodeList);//sort Node List as alphabetic
}

	 public static void main(String args[]) throws IOException{// main function
		 int task=0;
		 int i=0;
		 String input_file="";
		 String output_file="";
		 String output_log="";
		 
//		 //deal with arguments
		 while(i<args.length){
	         if (args[i].equals("-t")) {
	             i++;
	             if (args[i].equals("1")) {
	                 task = 1;
	             } else if (args[i].equals("2")) {
	                 task = 2;
	             }
	         }	         
	         if (args[i].equals("-i")) {
	             i++;
	             input_file = args[i];
	         }	         
	         if (args[i].equals("-op")) {
	             i++;
	             output_file = args[i];
	         }
	         if (args[i].equals("-ol")) {
	             i++;
	             output_log = args[i];
	         }
	         i++;
		 }

		 if(task==1){
			 readInput(input_file);
			 getSPG(output_file,output_log);
		 }
		 else if(task==2){
			 readInput(input_file);
			 getTSP(output_file,output_log);
		 }
		 
	 }
}
