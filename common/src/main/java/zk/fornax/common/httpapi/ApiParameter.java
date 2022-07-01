package zk.fornax.common.httpapi;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiParameter {

    private String name;

    private Type type;

    private Location location;

    private String defaultValue;

    private boolean required;

    private String description;

    private String enumerations;

    private String minNum;

    private String maxNum;

    private int minLength;

    private int maxLength;

    public enum Location {
        PATH, QUERY, HEADER, FORM
    }

    public enum Type {
        STRING, NUMBER
    }

}
