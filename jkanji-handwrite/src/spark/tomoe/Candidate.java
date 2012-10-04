package spark.tomoe;

public class Candidate implements Comparable<Candidate> {
	public DictionaryItem c;
	public double score;
	
	public Candidate(DictionaryItem c, double score) {
		this.c = c;
		this.score = score;
	}
	
	@Override
	public int compareTo(Candidate p1) {
		return (int) (this.score - p1.score);
	}
}
