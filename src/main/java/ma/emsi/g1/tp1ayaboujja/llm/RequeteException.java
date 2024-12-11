package ma.emsi.g1.tp1ayaboujja.llm;


public class RequeteException extends RuntimeException {
    public RequeteException(String message, String prettyPrinting) {

        super(message + "\n" + prettyPrinting);
    }
}
