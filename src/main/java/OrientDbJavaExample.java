package main.java;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

public class OrientDbJavaExample {

	public static void main(String[] args) {
		OrientGraphFactory ogf = new OrientGraphFactory(
	            "plocal:/home/duytri/orientdb/databases/blog", "admin", "admin");
	    OrientGraph og = ogf.getTx();

	    try {
	        System.out.println("Edges count = " + og.countEdges());
	    } finally {
	        og.shutdown();
	    }
	}

}
