package com.ecomerce.sell.util.response;

import com.ecomerce.sell.excepotions.DAOResponse;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Response {

    private String code;
    private String message;
    private Map<String, Object> responseData;

    public Response() {}

    public Response(DAOResponse response) {
        this.code = response.getCode();
        this.message = response.getMessage();
    }

    public Response(DAOResponse response, Map<String, Object> responseData) {
        this(response);
        this.responseData = responseData;
    }

    // ✅ Custom logic method, keep this
    public void setData(String key, Object value) {
        if (this.responseData == null) {
            this.responseData = new HashMap<>();
        }
        this.responseData.put(key, value);
    }

    // ✅ Optional helper method
    public void setResponse(DAOResponse response) {
        this.code = response.getCode();
        this.message = response.getMessage();
    }
}
