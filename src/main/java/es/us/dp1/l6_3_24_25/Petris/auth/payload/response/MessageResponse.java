package es.us.dp1.l6_3_24_25.Petris.auth.payload.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageResponse {

	 private String message;

	  public MessageResponse(String message) {
	    this.message = message;
	  }

}
