package in.oasys.gatepass.dto;

import lombok.Data;

@Data

public class ResponseDTO {

 private String errorMessage;
 private Integer statusCode;
 private Object responseContent;
 
 
}
