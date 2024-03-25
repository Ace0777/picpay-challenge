package br.com.challenge.authorization;


import br.com.challenge.transaction.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AuthorizerService {
    private RestClient restClient;

    public AuthorizerService(RestClient.Builder builder) {
        this.restClient = builder.baseUrl(
                "https://run.mocky.io/v3/5794d450-d2e2-4412-8131-73d0293ac1cc").build();
    }
    public void authorize(Transaction transaction){
     var respose = restClient.get()
                .retrieve()
                .toEntity(Authorization.class);

     if (respose.getStatusCode().isError() || !respose.getBody().isAuthorized()){
         throw  new UnauthorizedTransactionException("Transaction not authorized");
     }
    }
}
