package com.nt.LMS.outDTO;

import java.util.Objects;

/**
 * A generic standard response wrapper for API responses.
 * <p>
 * This DTO provides a consistent structure for API responses, including a status
 * (e.g., "SUCCESS", "ERROR"), a message, and the response payload.
 *
 * @param <T> the type of the data contained in the response
 */
public class StandardResponseOutDTO<T> {

    /** The status of the response (e.g., "SUCCESS", "ERROR"). */
    private String status;

    /** A human-readable message describing the result of the operation. */
    private String message;

    /** The actual data payload of the response. */
    private T data;

    /**
     * Default no-arg constructor.
     */
    public StandardResponseOutDTO() {
    }

    /**
     * Constructs a new {@code StandardResponseOutDTO} with the given status, message, and data.
     *
     * @param status  the response status
     * @param message the response message
     * @param data    the response payload
     */
    public StandardResponseOutDTO(String status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    /**
     * Creates a success response wrapper with the given data and message.
     *
     * @param data    the response payload
     * @param message the success message
     * @param <T>     the type of the payload
     * @return a {@code StandardResponseOutDTO} with "SUCCESS" status
     */
    public static <T> StandardResponseOutDTO<T> success(T data, String message) {
        return new StandardResponseOutDTO<>("SUCCESS", message, data);
    }

    /**
     * Creates an error response wrapper with the given error message.
     *
     * @param message the error message
     * @param <T>     the type of the payload (null in this case)
     * @return a {@code StandardResponseOutDTO} with "ERROR" status
     */
    public static <T> StandardResponseOutDTO<T> error(String message) {
        return new StandardResponseOutDTO<>("ERROR", message, null);
    }

    /**
     * Returns the response status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the response status.
     *
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Returns the response message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the response message.
     *
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Returns the data payload.
     *
     * @return the data
     */
    public T getData() {
        return data;
    }

    /**
     * Sets the data payload.
     *
     * @param data the data to set
     */
    public void setData(T data) {
        this.data = data;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param o the reference object with which to compare
     * @return {@code true} if this object is the same as the object argument; {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StandardResponseOutDTO<?> that = (StandardResponseOutDTO<?>) o;
        return Objects.equals(status, that.status) &&
                Objects.equals(message, that.message) &&
                Objects.equals(data, that.data);
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(status, message, data);
    }

    /**
     * Returns a string representation of the {@code StandardResponseOutDTO}.
     *
     * @return a string describing the response
     */
    @Override
    public String toString() {
        return "StandardResponseOutDTO{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
