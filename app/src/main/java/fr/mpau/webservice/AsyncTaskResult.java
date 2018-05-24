package fr.mpau.webservice;

/**
 * Classe de résultat d'une requête au WebService
 * <p>
 * Author: Jonathan B.
 * Created: 12/02/2018
 */

public class AsyncTaskResult<T> {

    /**
     * Attributs
     */
    private T result;
    private Exception error;

    /**
     * Constructeurs
     */
    public AsyncTaskResult(T result) {
        super();
        this.result = result;
    }

    public AsyncTaskResult(Exception error) {
        super();
        this.error = error;
    }

    /**
     * Getters
     */
    public T getResult() {
        return result;
    }

    public Exception getError() {
        return error;
    }

}