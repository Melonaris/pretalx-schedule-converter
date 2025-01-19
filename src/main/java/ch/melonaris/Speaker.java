package ch.melonaris;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Speaker {
    private String code;
    private String name;

    public Speaker(@JsonProperty("code") String code,
                   @JsonProperty("name") String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
