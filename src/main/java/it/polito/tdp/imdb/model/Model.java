package it.polito.tdp.imdb.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.GraphIterator;

import it.polito.tdp.imdb.db.ImdbDAO;

public class Model {

	private ImdbDAO dao;
	private SimpleWeightedGraph<Actor,DefaultWeightedEdge> grafo;
	private List<Actor> vertici;
	private Map<Integer,Actor> idMap;
	
	public Model() {
		dao= new ImdbDAO();
		this.vertici= new ArrayList<>();
		this.idMap= new HashMap<>();
	}
	
	public List<String> getGeneri(){
		return this.dao.getGenere();
	}
	
	public void creaGrafo(String genere) {
		this.grafo= new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		this.vertici= this.dao.getAttoriByGenere(genere,this.idMap);
		//aggiungo vertici
		Graphs.addAllVertices(this.grafo, this.vertici);
		
		//aggiungo archi
		List<Arco> archi= new ArrayList<>(this.dao.getArchi(genere, this.idMap));
		for(Arco a : archi) {
			Graphs.addEdgeWithVertices(this.grafo, a.getA1(), a.getA2(), a.getPeso());
		}
		
	}
	
	public int nVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int nArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public List<Actor> getVertici(){
		return this.vertici;
	}
	
	public List<Actor> getPercorso(Actor selezionato){
		GraphIterator<Actor,DefaultWeightedEdge> dfi= new DepthFirstIterator<Actor,DefaultWeightedEdge>(this.grafo,selezionato);
		List<Actor> result= new LinkedList<>();
		while(dfi.hasNext()) {
			result.add(dfi.next());
		}
		return result;
	}
	
	public List<Actor> attoriSimili(Actor attore){
		if(!this.grafo.vertexSet().contains(attore)) {
			throw new RuntimeException("Attore non presente nel grafo");
		}
		List<Actor> attoriRaggiungibili= this.getPercorso(attore);
		Collections.sort(attoriRaggiungibili);
		return attoriRaggiungibili;
	}
}
