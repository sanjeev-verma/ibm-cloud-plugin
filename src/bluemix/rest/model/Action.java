package bluemix.rest.model;

public class Action {
/**
 * Req Body

{"namespace":"_","name":"hello1234","exec":{"kind":"nodejs:default","code":"function main() {\n    return { message: \"Hello world\" };\n}"}}


 */
	
	private String namespace;
	private String name;
	private Exec exec;
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Exec getExec() {
		return exec;
	}
	public void setExec(Exec exec) {
		this.exec = exec;
	}
	
}
