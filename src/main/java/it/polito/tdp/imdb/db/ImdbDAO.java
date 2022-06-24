package it.polito.tdp.imdb.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.imdb.model.Actor;
import it.polito.tdp.imdb.model.Arco;
import it.polito.tdp.imdb.model.Director;
import it.polito.tdp.imdb.model.Movie;

public class ImdbDAO {
	
	public List<Actor> listAllActors(){
		String sql = "SELECT * FROM actors";
		List<Actor> result = new ArrayList<Actor>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Actor actor = new Actor(res.getInt("id"), res.getString("first_name"), res.getString("last_name"),
						res.getString("gender"));
				
				result.add(actor);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Movie> listAllMovies(){
		String sql = "SELECT * FROM movies";
		List<Movie> result = new ArrayList<Movie>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Movie movie = new Movie(res.getInt("id"), res.getString("name"), 
						res.getInt("year"), res.getDouble("rank"));
				
				result.add(movie);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public List<Director> listAllDirectors(){
		String sql = "SELECT * FROM directors";
		List<Director> result = new ArrayList<Director>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Director director = new Director(res.getInt("id"), res.getString("first_name"), res.getString("last_name"));
				
				result.add(director);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public List<String> getGenere(){
		String sql="SELECT DISTINCT genre "
				+ "FROM movies_genres "
				+ "ORDER BY genre";
		List<String> result= new ArrayList<>();
		Connection conn= DBConnect.getConnection();
		try {
			PreparedStatement st= conn.prepareStatement(sql);
			ResultSet rs= st.executeQuery();
			while(rs.next()) {
				String genere= rs.getString("genre");
				result.add(genere);
			}
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("SQL ERROR");
		}
		return result;
		
	}
	
	public List<Actor> getAttoriByGenere(String genere,Map<Integer,Actor> idMap){
		String sql="SELECT DISTINCT a.id AS id , a.first_name AS nome, a.last_name AS cognome, a.gender AS gen "
				+ "FROM actors a, roles r, movies_genres mg "
				+ "WHERE a.id=r.actor_id "
				+ "AND r.movie_id= mg.movie_id "
				+ "AND  mg.genre=? "
				+ "GROUP BY id "
				+"ORDER BY cognome";
		List<Actor> result= new ArrayList<>();
		Connection conn= DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, genere);
			ResultSet rs= st.executeQuery();
			while(rs.next()) {
				int id= rs.getInt("id");
				String nome= rs.getString("nome");
				String cognome= rs.getString("cognome");
				String gender= rs.getString("gen");
				Actor a = new Actor(id,nome,cognome,gender);
				result.add(a);
				idMap.put(id, a);
			}
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("SQL ERROR");	
		}
		return result;
	}
	
	public List<Arco> getArchi(String genere, Map<Integer,Actor> idMap){
		String sql="SELECT r1.actor_id AS id1, r2.actor_id AS id2, COUNT(*) AS peso "
				+ "FROM roles r1, roles r2, movies_genres mg "
				+ "WHERE r1.actor_id<r2.actor_id "
				+ "AND r1.movie_id=r2.movie_id "
				+ "AND mg.movie_id=r1.movie_id "
				+ "AND mg.genre=? "
				+ "GROUP BY id1, id2";
		List<Arco> result= new ArrayList<>();
		Connection conn=DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, genere);
			ResultSet rs= st.executeQuery();
			while(rs.next()) {
				Actor a1= idMap.get(rs.getInt("id1"));
				Actor a2= idMap.get(rs.getInt("id2"));
				int peso= rs.getInt("peso");
				Arco a = new Arco(a1,a2,peso);
				result.add(a);
			}
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("SQL ERROR");
		}
		return result;
	}
	
}
