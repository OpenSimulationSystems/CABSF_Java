package repast.simphony.engine.watcher.query;

/* Generated By:JJTree: Do not edit this line. ASTName.java */

public class ASTName extends SimpleNode {

	private String name;

  public ASTName(int id) {
    super(id);
  }

  public ASTName(QueryParser p, int id) {
    super(p, id);
  }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}