package main.java;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientEdgeType;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;

public class OrientDbJavaExample {

	public static void main(String[] args) {
		String WorkEdgeLabel = "Work";
		OrientGraphFactory ogf = new OrientGraphFactory("plocal:/home/duytri/orientdb/databases/java_sample", "admin",
				"admin");
		OrientGraph graph = ogf.getTx();

		try {

			// if the database does not contain the classes we need (it was just
			// created),
			// then adds them
			if (graph.getVertexType("Person") == null) {

				// we now extend the Vertex class for Person and Company
				OrientVertexType person = graph.createVertexType("Person");
				person.createProperty("firstName", OType.STRING);
				person.createProperty("lastName", OType.STRING);
				graph.commit();

				OrientVertexType company = graph.createVertexType("Company");
				company.createProperty("name", OType.STRING);
				company.createProperty("revenue", OType.LONG);
				graph.commit();

				OrientVertexType project = graph.createVertexType("Project");
				project.createProperty("name", OType.STRING);
				graph.commit();

				// we now extend the Edge class for a "Work" relationship
				// between Person and Company
				OrientEdgeType work = graph.createEdgeType(WorkEdgeLabel);
				work.createProperty("startDate", OType.DATE);
				work.createProperty("endDate", OType.DATE);
				work.createProperty("projects", OType.LINKSET);
				graph.commit();
			} else {

				// cleans up the DB since it was already created in a preceding
				// run
				graph.command(new OCommandSQL("DELETE VERTEX V")).execute();
				graph.command(new OCommandSQL("DELETE EDGE E")).execute();
				graph.commit();
			}

			// adds some people
			// (we have to force a vararg call in addVertex() method to avoid
			// ambiguous
			// reference compile error, which is pretty ugly)
			Vertex johnDoe = graph.addVertex("class:Person");
			johnDoe.setProperty("firstName", "John");
			johnDoe.setProperty("lastName", "Doe");
			graph.commit();

			// we can also set properties directly in the constructor call
			Vertex johnSmith = graph.addVertex("class:Person", "firstName", "John", "lastName", "Smith");
			Vertex janeDoe = graph.addVertex("class:Person", "firstName", "Jane", "lastName", "Doe");
			graph.commit();

			// creates a Company
			Vertex acme = graph.addVertex("class:Company", "name", "ACME", "revenue", "10000000");
			graph.commit();

			// creates a couple of projects
			Vertex acmeGlue = graph.addVertex("class:Project", "name", "ACME Glue");
			Vertex acmeRocket = graph.addVertex("class:Project", "name", "ACME Rocket");
			graph.commit();

			// creates edge JohnDoe worked for ACME
			Edge johnDoeAcme = graph.addEdge(null, johnDoe, acme, WorkEdgeLabel);
			johnDoeAcme.setProperty("startDate", "2010-01-01");
			johnDoeAcme.setProperty("endDate", "2013-04-21");
			// johnDoeAcme.setProperty("projects", new Parameter(acmeGlue,
			// acmeRocket));
			Set<Vertex> projs = new HashSet<Vertex>();
			projs.add(acmeGlue);
			projs.add(acmeRocket);
			johnDoeAcme.setProperty("projects", projs);
			graph.commit();

			// another way to create an edge, starting from the source vertex
			Edge johnSmithAcme = johnSmith.addEdge(WorkEdgeLabel, acme);
			johnSmithAcme.setProperty("startDate", "2009-01-01");

			// prints all the people who works/worked for ACME
			Iterable<OrientVertex> res = graph
					.command(new OCommandSQL("SELECT expand(in(" + WorkEdgeLabel + ")) FROM Company WHERE name='ACME'"))
					.execute();

			System.out.println("ACME people:");
			for (OrientVertex person : res) {
				// gets the "Work" edge
				Iterator<Edge> workEdgeIterator = person.getEdges(Direction.OUT, WorkEdgeLabel).iterator();
				Edge edge = workEdgeIterator.next();

				// retrieves worker's info
				String status = (edge.getProperty("endDate") != null) ? "retired" : "active";

				Iterable<Vertex> iterVertex = edge.getProperty("projects");

				String projects = "";
				if (edge.getProperty("projects") != null) {
					Iterator<Vertex> setVertex = iterVertex.iterator();
					while (setVertex.hasNext()) {
						Vertex vertex = (Vertex) setVertex.next();
						projects += vertex.getProperty("name") + ", ";
					}
					projects = projects.substring(0, projects.length() - 2);
				} else {
					projects = "Any project";
				}

				// and prints them
				System.out.println("Name: " + person.getProperty("lastName") + " " + person.getProperty("firstName")
						+ ", " + status + ", Worked on: " + projects + ".");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			graph.shutdown();
			ogf.close();
		}
	}

}
